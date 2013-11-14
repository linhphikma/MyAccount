
package com.xifan.myaccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xifan.myaccount.R;
import com.xifan.myaccount.data.AccountDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AccountAdapter extends BaseAdapter {

    private List<AccountDetail> list;
    private LayoutInflater mInflater;

    public AccountAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public AccountAdapter(Context context, List<AccountDetail> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
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
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.account_detail_list_view, null);
            holder.typeText = (TextView) view.findViewById(R.id.account_type);
            holder.dateText = (TextView) view.findViewById(R.id.account_date);
            holder.moneyText = (TextView) view.findViewById(R.id.account_money);
            holder.imgIcon = (ImageView) view.findViewById(R.id.account_extra_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.typeText.setText(list.get(position).getmType());
        holder.dateText.setText(list.get(position).getmDate());
        holder.moneyText.setText(String.valueOf(list.get(position).getmMoney()));
        if (list.get(position).getmPicUri() == null) {
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.ic_img);
        }
        System.out.println(list.get(position).getFullDate());
        System.out.println(list.get(position).getOnlyDate());

        return view;
    }

    public final class ViewHolder {
        TextView typeText;
        TextView dateText;
        TextView moneyText;
        ImageView imgIcon;
    }

}
