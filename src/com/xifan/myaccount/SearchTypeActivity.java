
package com.xifan.myaccount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xifan.myaccount.data.TypeInfo;
import com.xifan.myaccount.util.SmartType;
import com.xifan.myaccount.util.Util;

import java.util.List;

public class SearchTypeActivity extends Activity implements OnClickListener {

    private EditText mSearchBar;
    private TextView mOk;
    private ListView mListView;

    private List<TypeInfo> mTypeList;

    private LayoutInflater mInflater;
    private SearchListAdapter mAdapter;

    private int mTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type);
        getActionBar().hide();
        mInflater = LayoutInflater.from(this);

        init();
    }

    private void init() {
        mSearchBar = (EditText) findViewById(R.id.search_bar_input);
        mOk = (TextView) findViewById(R.id.search_bar_confirm);
        mListView = (ListView) findViewById(R.id.search_result_list);
        final SmartType smartType = new SmartType(this);
        mTypeList = smartType.getMatch();
        mAdapter = new SearchListAdapter();

        mSearchBar.addTextChangedListener(new TextWatcher() {

            private long lastInput;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    lastInput = Util.getSecondsNow();
                    if (Util.getSecondsNow() - lastInput > 1200) {

                        for (int i = 0; i < mTypeList.size(); i++) {
                            TypeInfo type = mTypeList.get(i);
                            if (type.getTypePinyin().equalsIgnoreCase(s.toString())) {
                                type.setWeight(type.getWeight() + 1);
                            }
                        }
                        mTypeList = smartType.sort(mTypeList);
                        mAdapter.notifyDataSetChanged();
                        Log.e("xifan", "searching");
                    } else {
                        Log.e("xifan", "inputing...pause searching");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            if (Util.getSecondsNow() - lastInput > 1200) {
                                // No input now
                                for (int i = 0; i < mTypeList.size(); i++) {
                                    TypeInfo type = mTypeList.get(i);
                                    if (type.getTypePinyin().equalsIgnoreCase(s.toString())) {
                                        type.setWeight(type.getWeight() + 1);
                                    }
                                }
                                mTypeList = smartType.sort(mTypeList);
                                mHandler.sendEmptyMessage(0);
                                Log.e("xfian", "searching in thread");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    
                    
                });
                t.start();
            }
        });

        mOk.setOnClickListener(this);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchBar.setText(mTypeList.get(position).getTypeName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == mOk) {
            Intent intent = getIntent();
            intent.putExtra("typeId", mTypeId);
            setResult(RESULT_OK, intent);
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
            holder.typeName.setText(mTypeList.get(position).getTypeName());
            return view;
        }
    }

    class Holder {
        TextView typeName;
    }

}
