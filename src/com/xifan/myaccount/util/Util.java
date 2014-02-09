
package com.xifan.myaccount.util;

import android.app.ActionBar;
import android.content.Context;

public class Util {

    public static void setActionBar(ActionBar bar, Context context, boolean isParent) {
        bar.setDisplayHomeAsUpEnabled(isParent);
    }

}
