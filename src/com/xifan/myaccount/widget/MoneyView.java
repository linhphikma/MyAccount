
package com.xifan.myaccount.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xifan.myaccount.R;

import java.text.DecimalFormat;

public class MoneyView extends TextView {

    private Context mContext;

    public MoneyView(Context context) {
        super(context);
        mContext = context;
    }

    public MoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MoneyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if ("".equals(text))
            super.setText("￥0.00", type);
        else {
            DecimalFormat df = new DecimalFormat("￥###,##0.00");
            super.setText(df.format(Double.valueOf(text.toString())), type);
        }
    }

}
