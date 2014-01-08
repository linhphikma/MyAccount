
package com.xifan.myaccount.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import com.xifan.myaccount.db.DbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmartType {

    private Context mContext;

    List<DetailType> mList = new ArrayList<DetailType>();
    

    public SmartType(Context context) {
        mContext = context;
    }

    public List<String> getTypeName() {
        List<String> typeNameList = new ArrayList<String>();

        Cursor c = readDb();
        while (c.moveToNext()) {
            typeNameList.add(c.getString(c.getColumnIndex("type")));
        }
        return typeNameList;
    }

    public int getMatch() {
        Cursor c = readDb();
        int count = -1;
        int mark = -1;
        long lastTime = 0l;
        while (c.moveToNext()) {
            count++;
            DetailType type = new DetailType();
            type.setTypeName(c.getString(c.getColumnIndex("type")));
            type.setLastDate(c.getString(c.getColumnIndex("last_date")));
            type.setFrequency(c.getInt(c.getColumnIndex("freq")));
            type.setStamp(c.getString(c.getColumnIndex("event_stamp")));

            int hour = -1;
            int day = -1;
            if (type.getStamp().contains(",") && type.getStamp() != "") {
                String[] stamp = type.getStamp().split(",");
                for (String str : stamp) {
                    if (str.contains("h")) {
                        hour = Integer.valueOf(str.replace("h", ""));
                    } else if (str.contains("d")) {
                        day = Integer.valueOf(str.replace("d", ""));
                    }
                }
            } else if (type.getStamp() != "") {
                String str = type.getStamp();
                if (str.contains("h")) {
                    hour = Integer.valueOf(str.replace("h", ""));
                } else if (str.contains("d")) {
                    day = Integer.valueOf(str.replace("d", ""));
                }
            } else { // 默认排序
                return 0;
            }
        }
        return 0;
    }

    private void setList(DetailType type) {
        if (mList.size() < 1) {
            mList.add(0, type);
        } else {
            if (type.getFrequency() > mList.get(0).getFrequency()) {
                mList.add(0, type);
            } else {
                mList.add(mList.size(), type);
            }
        }
    }

    private Cursor readDb() {
        SQLiteDatabase db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version)
                .getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("select * from record_type order by freq desc", null);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
