
package com.xifan.myaccount;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xifan.myaccount.adapter.AccountAdapter;
import com.xifan.myaccount.data.AccountDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private ActionBar actionbar;
    private List<AccountDetail> mDetailList;
    private AccountAdapter mAdapter;

    private ListView mListView;
    private TextView msgText;

    public static final String TASK_TYPE_LOAD_LIST = "loadlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionbar();

        mDetailList = new ArrayList();

        mListView = (ListView) findViewById(R.id.account_detail_list);
        msgText = (TextView) findViewById(R.id.message);

        LoadTask task = new LoadTask();
        task.execute(TASK_TYPE_LOAD_LIST);

    }

    private void setActionbar() {
        actionbar = getActionBar();
        actionbar.setLogo(R.drawable.ic_logo);
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
                bindData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mListView.setAdapter(mAdapter);
        }

        private void bindData() {
            // fake data for now
            AccountDetail detail1 = new AccountDetail(
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date()),
                    23.00f, null,
                    null, "type1");
            AccountDetail detail2 = new AccountDetail(
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date()),
                    15.10f, null,
                    null, "type2");
            mDetailList.add(detail1);
            mDetailList.add(detail2);

            mAdapter = new AccountAdapter(mDetailList);
            if (mDetailList.size() > 0) {
                msgText.setVisibility(View.GONE);
            } else {
                msgText.setText(R.string.msg_no_details);
            }

        }

    }

}
