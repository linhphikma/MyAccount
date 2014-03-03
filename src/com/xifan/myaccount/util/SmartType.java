
package com.xifan.myaccount.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.data.TypeInfo;

import java.util.ArrayList;
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
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    private Cursor getFrequencies(int type) {
        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
        Cursor c = null;
        try {
            c = db.doQuery("select * from record_type where operate_type=? order by freq desc",
                    new String[] {
                        String.valueOf(type)
                    });
            return c;
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    public String getTypeName(int id) {
        Cursor c = getFrequencies();
        String name = null;
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex("id")) == id) {
                name = c.getString(c.getColumnIndex("type_name"));
                c.close();
                Log.e("xifan", "type name is " + name);
                break;
            }
        }
        return name;
    }

    public List<Account> getAccountList() {
        List<Account> accountList = new ArrayList<Account>();
        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
        Cursor c = db.doQuery("select * from account", null);

        while (c.moveToNext()) {
            Account account = new Account();
            account.setAccountName(c.getString(c.getColumnIndex("accountName")));
            account.setAccountType(c.getInt(c.getColumnIndex("accountType")));
            account.setTotal(c.getInt(c.getColumnIndex("total")));
            account.setExpend(c.getInt(c.getColumnIndex("expend")));
            account.setId(c.getInt(c.getColumnIndex("id")));
            account.setRevenue(c.getFloat(c.getColumnIndex("revenue")));
            accountList.add(account);
        }
        db.closeAll(c);
        return accountList;
    }

    public List<String> getAccountTypeList() {
        List<String> accountTypeList = new ArrayList<String>();
        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
        Cursor c = db.doQuery("select * from account_type", null);
        while (c.moveToNext()) {
            accountTypeList.add(c.getString(c.getColumnIndex("typename")));
        }
        c.close();
        return accountTypeList;
    }

    /**
     * 排序逻辑：用mark来标记，首先初始值根据freq排列，如果last_date小于三天则
     * mark++，然后根据event_stamp，有其中一个则mark++，以上没有则mark--。
     * 
     * @return
     */
    public List<TypeInfo> getMatch(int opType) {
        // get types by frequency order
        Cursor c = getFrequencies(opType);
        mList = new ArrayList<TypeInfo>();

        int mark = 0;
        while (c.moveToNext()) {
            TypeInfo type = new TypeInfo();
            type.typeId = c.getInt(c.getColumnIndex("id"));
            type.typeName = c.getString(c.getColumnIndex("type_name"));
            type.typePinyin = c.getString(c.getColumnIndex("type_pinyin"));
            type.lastDate = c.getString(c.getColumnIndex("last_date"));
            type.frequency = c.getInt(c.getColumnIndex("freq"));
            type.stamp = c.getString(c.getColumnIndex("event_stamp"));

            // Start check event_stamp
            int hour = -1;
            int day = -1;
            int month = -1;
            if (!type.stamp.equals("")) {
                String[] stamp = type.stamp.split(",");
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
            if (Util.getDaysFromNow(type.lastDate) > 3) {
                mark++;
            } else {
                mark--;
            }

            // save weight to item
            type.weight = mark;
            mList.add(type);
        }

        c.close();
        return sort(mList);
        // return mList;
    }

    public List<TypeInfo> sort(List<TypeInfo> list) {
        TypeInfo[] array = new TypeInfo[list.size()];
        list.toArray(array);
        if (array.length > 0) { // 查看数组是否为空
            array = Util.quickSort(array);
            list.clear();
            Log.e("xifan", "test: array.length = " + array.length);
            for (int i = array.length - 1; i >= 0; i--) {
                Log.e("xifan", array[i].typeName);
                list.add(array[i]);
            }
        } else {
            Log.e("xifan", "array.length = 0");
        }
        return list;
    }

    public String[] getPinyinList(List<TypeInfo> list) {
        String[] newArray = new String[list.size()];
        for (int i = list.size() - 1; i >= 0; i--) {
            newArray[i] = list.get(i).typePinyin;
        }
        return newArray;
    }
}
