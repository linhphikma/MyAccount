
package com.xifan.myaccount.util;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.pinyin4android.PinyinUtil;
import com.xifan.myaccount.data.TypeInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Util {

    public static void setActionBar(ActionBar bar, Context context, boolean isParent) {
        bar.setDisplayHomeAsUpEnabled(isParent);
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static String getTime() {
        SimpleDateFormat dateFomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.getDefault());
        return dateFomatter.format(Calendar.getInstance().getTime());
    }

    public static int getDayOfTime() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getHourOfTime() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getDaysFromNow(String date) {
        SimpleDateFormat dateFomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.getDefault());
        String dateNow = dateFomatter.format(Calendar.getInstance().getTime());
        int day = Integer.valueOf(date.substring(8, 10));
        int dayNow = Integer.valueOf(dateNow.substring(8, 10));
        int month = Integer.valueOf(date.substring(5, 7));
        int monthNow = Integer.valueOf(dateNow.substring(5, 7));
        if (monthNow == month) {
            return dayNow - day;
        } else {
            return 30;
        }
    }

    public static int getMinuteOfTime() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static long getSecondsNow() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static TypeInfo[] quickSort(TypeInfo[] list) {
        TypeInfo[] tmparray = new TypeInfo[list.length];
        TypeInfo tmptype;
        final int max = list.length;
        boolean hasChanged = false;
        for (int i = 0; i < max; i++) {
            tmparray[i] = list[i];
            for (int j = i + 1; j < max; j++) {
                if (tmparray[i].weight < list[j].weight) {
                    // put the bigest to head
                    tmparray[i] = list[j];
                    hasChanged = true;
                }
                // change the order for later arrange
                tmptype = list[i];
                list[i] = list[j];
                list[j] = tmptype;
            }
        }
        return hasChanged ? tmparray : list;
    }
    
    public static boolean isChinese(String s) {
        return s.substring(0,1).matches("[\u4E00-\u9FA5]");
    }

    public String getPinyin(Context context, CharSequence s) {
        String[] tmp = PinyinUtil.toPinyin(context, s.toString()).split(" ");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < tmp.length; i++) {
            str.append(tmp[i].substring(0, 1));
        }
        return str.toString().trim();

    }

    private Bitmap blurImageAmeliorate(Bitmap bmp)
    {
        long start = System.currentTimeMillis();
        // 高斯矩阵
        int[] gauss = new int[] {
                1, 2, 1, 2, 4, 2, 1, 2, 1
        };

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int delta = 16; // 值越小图片会越亮，越大则越暗

        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++)
        {
            for (int k = 1, len = width - 1; k < len; k++)
            {
                idx = 0;
                for (int m = -1; m <= 1; m++)
                {
                    for (int n = -1; n <= 1; n++)
                    {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * gauss[idx]);
                        newG = newG + (int) (pixG * gauss[idx]);
                        newB = newB + (int) (pixB * gauss[idx]);
                        idx++;
                    }
                }

                newR /= delta;
                newG /= delta;
                newB /= delta;

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);

                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("may", "used time=" + (end - start));
        return bitmap;
    }

}
