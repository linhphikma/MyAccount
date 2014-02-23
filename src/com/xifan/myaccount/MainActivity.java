
package com.xifan.myaccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xifan.myaccount.adapter.AccountAdapter;
import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.data.AccountDetail;
import com.xifan.myaccount.fragments.AccountManage;
import com.xifan.myaccount.util.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    // TODO Slide or Tab to switch month

    private List<AccountDetail> mDetailList;
    private AccountAdapter mAdapter;
    private Context mContext;

    private LinearLayout mFloatingBar;
    private ListView mListView;
    private TextView msgText;
    private TextView expendText;
    private TextView revenueText;
    private TextView totalText;

    private AnimationSet mHideAnim;

    private LoadTask mTask;

    private float firstY;
    private boolean isListEnd;

    private int mCurrentAccount = Account.currentAccountId;
    private float mExpend = 0f;
    private float mRevenue = 0f;
    private float mTotal = 0f;

    private static final String TASK_TYPE_LOAD_LIST = "loadlist";
    private static final int GESTURE_LENGTH = 20;
    private static final int REQUEST_ADD_FLAG = 1;
    private static final int REQUEST_ACCOUNT_FLAG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetailList = new ArrayList<AccountDetail>();
        mContext = this;

        initView();

        mTask = new LoadTask();
        mTask.execute(TASK_TYPE_LOAD_LIST);
    }

    private void initView() {
        // Util.setActionBar(getActionBar(), this, true); // TODO set UI
        mFloatingBar = (LinearLayout) findViewById(R.id.floating_bar);
        mListView = (ListView) findViewById(R.id.account_detail_list);
        msgText = (TextView) findViewById(R.id.message);
        expendText = (TextView) findViewById(R.id.account_expend_of_month_value);
        revenueText = (TextView) findViewById(R.id.account_revenue_of_month_value);
        totalText = (TextView) findViewById(R.id.account_total_of_month_value);

        mHideAnim = (AnimationSet) AnimationUtils.loadAnimation(mContext, R.anim.hide_t2b);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, RecordDetail.class);
                intent.putExtra("type", RecordDetail.REQUEST_SHOW_RECORD);
                intent.putExtra("detail", mDetailList.get(position));
                startActivity(intent);
            }
        });
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstY = event.getAxisValue(MotionEvent.AXIS_Y);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (firstY - event.getAxisValue(MotionEvent.AXIS_Y) > GESTURE_LENGTH) {
                            if (!isListEnd)
                                mFloatingBar.startAnimation(mHideAnim);
                            mFloatingBar.setVisibility(View.GONE);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (firstY - event.getAxisValue(MotionEvent.AXIS_Y) > GESTURE_LENGTH) {
                            if (!isListEnd)
                                mFloatingBar.startAnimation(mHideAnim);
                            mFloatingBar.setVisibility(View.GONE);
                        }
                        else if (event.getAxisValue(MotionEvent.AXIS_Y) - firstY > GESTURE_LENGTH) {
                            mFloatingBar.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return false;
            }
        });

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                isListEnd = totalItemCount == firstVisibleItem + visibleItemCount;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_add:
                intent = new Intent(mContext, RecordDetail.class);
                intent.putExtra("type", RecordDetail.REQUEST_ADD_RECORD);
                startActivityForResult(intent, REQUEST_ADD_FLAG);
                break;
            case R.id.menu_account_manage:
                intent = new Intent(mContext, SettingsActivity.class);
                intent.putExtra("entry", AccountManage.class.getSimpleName());
                startActivityForResult(intent, REQUEST_ACCOUNT_FLAG);
                break;
            case R.id.action_settings:
                intent = new Intent(mContext, SettingsActivity.class);
                intent.putExtra("entry", "settings");
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_FLAG) {
                mTask.execute(TASK_TYPE_LOAD_LIST);
            }
            else if (requestCode == REQUEST_ACCOUNT_FLAG) {
                mTask.execute(TASK_TYPE_LOAD_LIST);
            }
        }
    }

    private void loadInfo() {
        SharedPreferences prefs = getSharedPreferences("pref", 0);
        mCurrentAccount = prefs.getInt("currentAccount", -1);
        if (mCurrentAccount < 1) {
            // default use Cash account
            mCurrentAccount = 1;
            prefs.edit().putInt("currentAccount", 1).apply();
        }
        Account.currentAccountId = mCurrentAccount; // write into global variant
    }

    private class LoadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            loadInfo();
            if (params[0] == TASK_TYPE_LOAD_LIST) {
                DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null,
                        DbHelper.version);
                Cursor c = null;
                try {
                    c = db.doQuery("select * from detail where accountId=" + mCurrentAccount
                            + " and strftime('%m',recordDate) = strftime('%m','now')", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("xifan", "details not exist,checking if account exist");
                }

                if (c != null && c.getCount() > 0) {
                    mDetailList.clear();
                    int operate = -1;
                    while (c.moveToNext()) {
                        AccountDetail detail = new AccountDetail();
                        detail.setId(c.getInt(c.getColumnIndex("id")));
                        detail.setAccountId(c.getInt(c.getColumnIndex("accountId")));
                        detail.setDate(c.getString(c.getColumnIndex("recordDate")));
                        detail.setLocation(c.getString(c.getColumnIndex("location")));
                        detail.setMoney(c.getFloat(c.getColumnIndex("moneyAmount")));
                        detail.setNote(c.getString(c.getColumnIndex("note")));
                        detail.setPicUri(c.getString(c.getColumnIndex("picUri")));
                        detail.setRecordType(c.getInt(c.getColumnIndex("recordType")));
                        detail.setReimbursabled(c.getInt(c.getColumnIndex("isReimbursabled")));
                        mDetailList.add(detail);

                        // Count data
                        operate = c.getInt(c.getColumnIndex("recordOp"));
                        if (operate == 0) {
                            mExpend += Float.parseFloat(detail.getMoney());
                        } else if (operate == 1) {
                            mRevenue += Float.parseFloat(detail.getMoney());
                        }
                    }
                    mTotal = mRevenue - mExpend;
                    Log.e("xifan", "db read");
                } else {
                    Log.e("xifan", "data is null");
                    // check if account exist
                    c = db.doQuery("select * from account", null);
                    if (!c.moveToNext()) {
                        // doesn't exists
                        Log.e("xifan", "db is null,initializing");
                        db.initDataBase();
                    }
                }
                // bind
                mAdapter = new AccountAdapter(mContext, mDetailList);
                db.closeAll(c);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mDetailList.size() > 0) {
                msgText.setVisibility(View.GONE);
            } else {
                msgText.setText(R.string.msg_no_details);
            }
            mListView.setAdapter(mAdapter);
            expendText.setText(String.valueOf(mExpend));
            revenueText.setText(String.valueOf(mRevenue));
            totalText.setText(String.valueOf(mTotal));

            // clear
            mExpend = 0f;
            mRevenue = 0f;
            mTotal = 0f;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTask.cancel(true);
    }

}
