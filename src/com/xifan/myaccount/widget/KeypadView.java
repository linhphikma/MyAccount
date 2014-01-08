package com.xifan.myaccount.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class KeypadView extends View {

    public KeypadView(Context context) {
        super(context);
    }
    public KeypadView(Context context,AttributeSet attribute) {
        super(context, attribute);
    }
    public KeypadView(Context context,AttributeSet attribute,int code) {
        super(context,attribute,code);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
