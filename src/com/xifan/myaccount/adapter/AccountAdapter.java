
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
import com.xifan.myaccount.util.SmartType;
import com.xifan.myaccount.util.Util;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private List<AccountDetail> list;
    private LayoutInflater mInflater;
    private Context mContext;

    public AccountAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public AccountAdapter(Context context, List<AccountDetail> list) {
        mContext = context;
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

    public void setCheckItem(int position, boolean isChecked) {
        list.get(position).setState(isChecked ? 1 : 0);
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

        AccountDetail detail = list.get(position);
        holder.typeText.setText(new SmartType(mContext).getTypeName(detail.getRecordType()));
        holder.dateText.setText(detail.getOnlyDate());
        holder.moneyText.setText(detail.getMoney());
        if (detail.getPicUri() == null) {
            holder.imgIcon.setVisibility(View.VISIBLE);
            holder.imgIcon.setImageResource(R.drawable.ic_img);
        }
        // update checked item view
        if (detail.getState() == 1) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue));
            holder.typeText.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.dateText.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.moneyText.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            view.setBackground(mContext.getResources().getDrawable(R.drawable.card_background));
            view.setPadding(Util.getPx(mContext, 10), 0, Util.getPx(mContext, 10), 0);
            holder.typeText.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.dateText.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.moneyText.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        return view;
    }

    public final class ViewHolder {
        TextView typeText;
        TextView dateText;
        TextView moneyText;
        ImageView imgIcon;
    }

}
