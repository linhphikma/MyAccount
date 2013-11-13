package com.xifan.myaccount.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xifan.myaccount.data.AccountDetail;

import java.util.List;

public class AccountAdapter extends BaseAdapter {
    
    private List<AccountDetail> list;
    
    public AccountAdapter() {}
    
    public AccountAdapter(List<AccountDetail> list) {
        list = this.list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        
        return null;
    }
    
    public final class ViewHolder {
    }

}
