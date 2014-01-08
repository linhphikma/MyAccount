
package com.xifan.myaccount;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xifan.myaccount.adapter.AccountAdapter;
import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.data.AccountDetail;
import com.xifan.myaccount.db.DbHelper;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SwipeBackActivity {

    private List<AccountDetail> mDetailList;
    private AccountAdapter mAdapter;
    private Context mContext;

    private ListView mListView;
    private TextView msgText;

    private boolean isFirstRun = true;

    private static final String TASK_TYPE_LOAD_LIST = "loadlist";

    public static final int RESULT_NEED_REFRESH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetailList = new ArrayList<AccountDetail>();
        mContext = this;

        mListView = (ListView) findViewById(R.id.account_detail_list);
        msgText = (TextView) findViewById(R.id.message);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO go to item detail
                Intent intent = new Intent(mContext, RecordDetail.class);
                intent.putExtra("type", RecordDetail.REQUEST_SHOW_RECORD);
                startActivity(intent);
            }
        });

        LoadTask task = new LoadTask();
        task.execute(TASK_TYPE_LOAD_LIST);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Intent intent = new Intent(mContext, RecordDetail.class);
                intent.putExtra("type", RecordDetail.REQUEST_ADD_RECORD);
                startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstRun) {
            isFirstRun = false;
        } else {
            LoadTask task = new LoadTask();
            task.execute(TASK_TYPE_LOAD_LIST);
        }
    }

    private class LoadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params[0] == TASK_TYPE_LOAD_LIST) {
                DbHelper helper = new DbHelper(mContext, DbHelper.DB_NAME, null,
                        DbHelper.version);
                SQLiteDatabase db = helper.getReadableDatabase();

                Cursor c = null;
                try {
                    c = db.rawQuery("select * from detail where accountId=" + Account.defaultId,
                            null);
                } catch (Exception e) {
                    Log.e("xifan", e.getMessage());
                    Log.e("xifan", "details not exist,checking if account exist");
                }

                if (c != null && c.getCount() > 0) {
                    mDetailList.clear();
                    while (c.moveToNext()) {
                        AccountDetail detail = new AccountDetail();
                        detail.setAccountId(c.getInt(c.getColumnIndex("accountId")));
                        detail.setDate(c.getString(c.getColumnIndex("recordDate")));
                        detail.setLocation(c.getString(c.getColumnIndex("location")));
                        detail.setMoney(c.getFloat(c.getColumnIndex("moneyAmount")));
                        detail.setNote(c.getString(c.getColumnIndex("note")));
                        detail.setPicUri(c.getString(c.getColumnIndex("picUri")));
                        detail.setRecordType(c.getInt(c.getColumnIndex("recordType")));
                        detail.setReimbursabled(c.getInt(c.getColumnIndex("isReimbursabled")));
                        mDetailList.add(detail);
                    }
                    Log.e("xifan", "db read");
                } else {
                    Log.e("xifan", "data is null");
                    // check if account exist
                    Cursor c1 = db.query("account", null, null, null, null, null, null);
                    if (!c1.moveToNext()) {
                        // doesn't exists
                        Log.e("xifan", "db is null,initializing");
                        helper.initDataBase(db);
                    }
                }
                // bind
                mAdapter = new AccountAdapter(mContext, mDetailList);
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
        }
    }

}
