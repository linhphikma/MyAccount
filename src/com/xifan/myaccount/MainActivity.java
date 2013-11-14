
package com.xifan.myaccount;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xifan.myaccount.adapter.AccountAdapter;
import com.xifan.myaccount.data.AccountDetail;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ActionBar mActionbar;
    private List<AccountDetail> mDetailList;
    private AccountAdapter mAdapter;
    private Context mContext;

    private ListView mListView;
    private TextView msgText;

    private boolean isListEmpty = true;

    public static final String TASK_TYPE_LOAD_LIST = "loadlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionbar();

        mDetailList = new ArrayList();
        mContext = this;

        LoadTask task = new LoadTask();
        task.execute(TASK_TYPE_LOAD_LIST);

    }

    private void setActionbar() {
        mActionbar = getActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class LoadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params[0] == TASK_TYPE_LOAD_LIST) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bindData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mListView = (ListView) findViewById(R.id.account_detail_list);
            msgText = (TextView) findViewById(R.id.message);

            if (mDetailList.size() > 0) {
                msgText.setVisibility(View.GONE);
                isListEmpty = false;
            } else {
                msgText.setText(R.string.msg_no_details);
            }
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(mContext, "you have clicked my item", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void bindData() {
            // fake data for now
            Time time = new Time("Asia/Hong_Kong");
            time.setToNow();
            AccountDetail detail1 = new AccountDetail(time.format("%Y-%m-%d %H:%M:%S"), 23.00f,
                    null, null, "午餐");
            time.setToNow();
            AccountDetail detail2 = new AccountDetail(time.format("%Y-%m-%d %H:%M:%S"), 15.13f,
                    null, null, "零食");
            mDetailList.add(detail1);
            mDetailList.add(detail2);

            mAdapter = new AccountAdapter(mContext, mDetailList);

        }

    }

}
