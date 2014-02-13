
package com.xifan.myaccount.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.pinyin4android.PinyinUtil;
import com.xifan.myaccount.data.TypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SmartType {

    private Context mContext;

    private List<TypeInfo> mList;

    public SmartType(Context context) {
        mContext = context;
    }

    private Cursor getFrequencies() {
        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
        Cursor c = null;
        try {
            c = db.doQuery("select * from record_type order by freq desc", null);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getTypeName() {
        List<String> typeNameList = new ArrayList<String>();
        Cursor c = getFrequencies();

        mList = new ArrayList<TypeInfo>();
        while (c.moveToNext()) {
            typeNameList.add(c.getString(c.getColumnIndex("type_name")));
        }
        return typeNameList;
    }

    public int getTypeIndex(String type) {
        int index = getTypeName().indexOf(type);
        return index > -1 ? index : 0;
    }

    /**
     * 排序逻辑：用mark来标记，首先初始值根据freq排列，如果last_date小于三天则
     * mark++，然后根据event_stamp，有其中一个则mark++，以上没有则mark--。
     * 
     * @return
     */
    public List<TypeInfo> getMatch() {
        // get types by frequency order
        Cursor c = getFrequencies();
        mList = new ArrayList<TypeInfo>();

        int count = -1;
        int mark = 0;
        long lastTime = 0l;
        while (c.moveToNext()) {
            count++;
            TypeInfo type = new TypeInfo();
            type.setTypeName(c.getString(c.getColumnIndex("type_name")));
            type.setTypePinyin(c.getString(c.getColumnIndex("type_pinyin")));
            type.setLastDate(c.getString(c.getColumnIndex("last_date")));
            // TODO add it in addRecord
            type.setFrequency(c.getInt(c.getColumnIndex("freq")));
            type.setStamp(c.getString(c.getColumnIndex("event_stamp")));

            // Start check event_stamp
            int hour = -1;
            int day = -1;
            int month = -1;
            if (!type.getStamp().equals("")) {
                String[] stamp = type.getStamp().split(",");
                for (String str : stamp) {
                    if (str.indexOf("h") > -1) {
                        hour = Integer.valueOf(str.replace("h", "").trim());
                    } else if (str.indexOf("d") > -1) {
                        day = Integer.valueOf(str.replace("d", "").trim());
                    } else if (str.indexOf("m") > -1) {
                        month = Integer.valueOf(str.replace("m", "").trim());
                    }
                }
                if (hour == Util.getHourOfTime())
                    mark++;
                if (day == Util.getDayOfTime())
                    mark++;
                if (month == Util.getMonth())
                    mark++;
            } else {
                mark--;
            }

            // Start check last_date
            if (Util.getDaysFromNow(type.getLastDate()) > 3) {
                mark++;
            } else {
                mark--;
            }

            // save weignt to item
            type.setWeight(mark);
            mList.add(type);
        }

        return sort(mList);
    }

    public List<TypeInfo> sort(List<TypeInfo> list) {
        TypeInfo[] array = new TypeInfo[list.size()];
        list.toArray(array);
        if (array.length > 0) { // 查看数组是否为空
            Util.quickSort(array, 0, array.length - 1);
            list.clear();
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        } else {
            Log.e("xifan", "array.length = 0");
        }
        return list;
    }

    public String[] getMatchPinyin() {
        List<TypeInfo> list = getMatch();
        Iterator<TypeInfo> inter = list.iterator();
        String[] newArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = inter.next().getTypePinyin();
        }
        return newArray;
    }
}
