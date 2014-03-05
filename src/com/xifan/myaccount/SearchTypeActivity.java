
package com.xifan.myaccount;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xifan.myaccount.data.TypeInfo;
import com.xifan.myaccount.util.DbHelper;
import com.xifan.myaccount.util.SmartType;
import com.xifan.myaccount.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SearchTypeActivity extends Activity implements OnClickListener {

    private EditText mSearchBar;
    private TextView mOk;
    private ListView mListView;

    private List<TypeInfo> mTypeList;
    private List<TypeInfo> mTmpList;

    private Context mContext;
    private LayoutInflater mInflater;
    private SearchListAdapter mAdapter;

    private LoaderTask mTask;
    private SmartType smartType;

    private int mTypeId;
    private int mOperateType;
    private boolean isAdd;

    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type);

        mOperateType = getIntent().getIntExtra("opType", 1);
        mContext = this;
        mInflater = LayoutInflater.from(this);

        init();
    }

    private void init() {
        mSearchBar = (EditText) findViewById(R.id.search_bar_input);
        mOk = (TextView) findViewById(R.id.search_bar_confirm);
        mListView = (ListView) findViewById(R.id.search_result_list);
        mTask = new LoaderTask();
        smartType = new SmartType(mContext);
        mTask.execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_bar_confirm) {
            boolean isNewType = true;
            for (TypeInfo i : mTypeList) {
                if (i.typeName.equals(mSearchBar.getText().toString())) {
                    if (!isSearch) {
                        // type is already exist in list
                        mTypeId = i.typeId;
                        isNewType = false;
                    }
                    Intent intent = getIntent();
                    intent.putExtra("typeId", mTypeId);
                    intent.putExtra("typeName", mSearchBar.getText().toString());
                    setResult(RESULT_OK, intent);
                    break;
                }
            }
            if (isNewType && !isSearch) {
                String newType = mSearchBar.getText().toString();
                String typePinyin = Util.getPinyin(mContext, newType);
                DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null,
                        DbHelper.version);
                ContentValues cv = new ContentValues();
                cv.put("type_name", newType);
                cv.put("type_pinyin", typePinyin);
                cv.put("last_date", Util.getTime());
                cv.put("operate_type", mOperateType);
                cv.put("freq", 1);
                cv.put("event_stamp", "");
                db.doInsert("record_type", cv);
                Cursor c = db.doQuery("select id from record_type where type_name=?",
                        new String[] {
                            newType
                        });
                if (c.moveToFirst()) {
                    mTypeId = c.getInt(c.getColumnIndex("id"));
                } else {
                    // issue
                }
                db.close();

                Intent intent = getIntent();
                intent.putExtra("typeId", mTypeId);
                intent.putExtra("typeName", newType);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    private class SearchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTypeList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTypeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view = mInflater.inflate(R.layout.view_search_list, null);
                holder.typeName = (TextView) view.findViewById(R.id.type_name);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.typeName.setText(mTypeList.get(position).typeName);
            return view;
        }
    }

    class Holder {
        TextView typeName;
    }

    private class LoaderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mTypeList = smartType.getMatch(mOperateType);
            mAdapter = new SearchListAdapter();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mOk.setOnClickListener(SearchTypeActivity.this);

            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSearchBar.setText(mTypeList.get(position).typeName);
                    mTypeId = mTypeList.get(position).typeId;
                }
            });

            mSearchBar.addTextChangedListener(new TextWatcher() {

                SearchTask task = new SearchTask();
                private long lastCount;

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Util.isChinese(s.toString())) {
                        // Check is Chinese then don't search the list
                        isSearch = false;
                    } else {
                        isSearch = true;

                        if (s.length() > 0) {
                            isAdd = s.length() > lastCount;
                            if (task.getStatus() != Status.FINISHED) {
                                task.cancel(true);
                                Log.e("xifan", "Thread canceled");
                            }
                            task = new SearchTask();
                            task.execute(s.toString());
                        } else if (TextUtils.isEmpty(s)) {
                            mTypeList = smartType.getMatch(mOperateType);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    lastCount = s.length();
                }

                @Override
                public void afterTextChanged(final Editable s) {

                }
            });
        }
    }

    private class SearchTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (isAdd) {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<TypeInfo> newList = new ArrayList<TypeInfo>();
                String[] matchesPinyin = smartType.getPinyinList(mTypeList);
                for (int i = 0; i < mTypeList.size(); i++) {
                    if (matchesPinyin[i].indexOf(params[0].toString().toLowerCase()) > -1) {
                        mTypeList.get(i).weight += 1;
                        newList.add(mTypeList.get(i));
                    }
                }
                newList = smartType.sort(newList);
                mTmpList = mTypeList;// backup list for speed
                mTypeList = newList;
            } else {
                mTypeList = mTmpList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
        }

    }

}
