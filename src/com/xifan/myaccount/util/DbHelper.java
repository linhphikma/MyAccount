
package com.xifan.myaccount.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xifan.myaccount.data.Account;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "account.db"; // 数据库名称
    public static final int version = 1; // 数据库版本

    public static final String DB_TABLE_DETAIL = "detail";
    public static final String DB_TABLE_ACCOUNT = "account";
    public static final String DB_TABLE_TYPE = "record_type";
    public static final int DB_WRITABLE_FLAG = 1;
    public static final int DB_READABLE_FLAG = 1;
    public static final int SYNC_DELETE_TYPE_ID = -1;

    private SQLiteDatabase mDb;

    /**
     * @param context
     * @param name DB name,like DbHelper.DB_NAME
     * @param factory if you don't have special need, keep it null
     * @param version DB Version,like DbHelper.version
     */
    public DbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * @param context
     * @param name DB name,like DbHelper.DB_NAME
     * @param factory if you don't have special need, keep it null
     * @param version DB Version,like DbHelper.version
     * @param errorHandler
     */
    public DbHelper(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.e("xifan", "creating table");
            db.execSQL("create table if not exists account_type (id integer primary key autoincrement,typename varchar(20) not null);");
            db.execSQL("create table if not exists account (id integer primary key autoincrement,total money,revenue money,expend money,accountType integer,accountName varchar(20),foreign key(accountType) references account_type(id));");
            db.execSQL("create table if not exists detail (id integer primary key autoincrement,accountId integer,recordOp integer,recordType varchar(100),moneyAmount money,picUri varchar(200), recordDate datetime,location varchar(100),note varchar(320),isReimbursabled integer,foreign key(accountId) references account(id));");
            db.execSQL("create table if not exists record_type (id integer primary key autoincrement,type_name varchar(20),type_pinyin varchar(10),last_date datetime,operate_type integer,freq integer,event_stamp varchar(20));");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("xifan", "upgrading table");
    }

    public Cursor doQuery(String sql, String[] args) {
        Log.e("xifan", "Query: " + sql);
        mDb = getWritableDatabase();
        return mDb.rawQuery(sql, args);
    }

    public int doUpdate(String table, ContentValues values, String where, String[] args) {
        Log.e("xifan", "Update: " + table + where + args[0]);
        mDb = getWritableDatabase();
        return mDb.update(table, values, where, args);
    }

    /**
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long doInsert(String table, ContentValues values) {
        Log.e("xifan", "Insert: " + table);
        mDb = getWritableDatabase();
        return mDb.insert(table, null, values);
    }

    public int doDelete(String table, String where, String[] args) {
        Log.e("xifan", "Delete: " + table + "where " + where);
        mDb = getWritableDatabase();
        return mDb.delete(table, where, args);
    }

    // TODO need to fix with transfer
    public void syncAccount(String moneyText, int typeId, int operation) {
        Log.e("xifan", "Syncing account data");
        Account account = new Account();
        Cursor c = doQuery("select * from account where id=?", new String[] {
                String.valueOf(Account.currentAccountId)
        });
        while (c.moveToNext()) {
            account.setTotal(c.getFloat(c.getColumnIndex("total")));
            account.setExpend(c.getFloat(c.getColumnIndex("expend")));
            account.setRevenue(c.getFloat(c.getColumnIndex("revenue")));
        }
        ContentValues cv = new ContentValues();
        float money = Float.valueOf(moneyText);

        // Check if is operating deletion.
        final boolean isDel;
        if (operation < 0) {
            operation = -operation;
            // swap operation
            operation = operation == 2 ? 1 : 2;
            isDel = true;
        } else {
            isDel = false;
        }
        
        if (operation == 1) {
            // 支出
            cv.put("expend", account.getExpend() + money);
            cv.put("total", account.getTotal() - money);
        } else if (operation == 2) {
            // 收入
            cv.put("revenue", account.getRevenue() + money);
            cv.put("total", account.getTotal() + money);
        }
        doUpdate("account", cv, "id=?", new String[] {
                String.valueOf(Account.currentAccountId)
        });

        // Do not count type freqs if is deletion operate.
        if (!isDel) {
            c = doQuery(
                    "select freq from record_type where operate_type=? and id=? order by freq desc",
                    new String[] {
                            String.valueOf(operation), String.valueOf(typeId)
                    });
            ContentValues typeValues = new ContentValues();
            typeValues.put("last_date", Util.getTime());
            if (c.moveToNext()) {
                typeValues.put("freq", c.getInt(c.getColumnIndex("freq")) + 1);
            }
            doUpdate("record_type", typeValues, "id=?", new String[] {
                    String.valueOf(typeId)
            });
        }

        closeAll(c);
    }

    /**
     * Close all opened database and cursors.
     * 
     * @param cursors Cursor collections that need to closed
     */
    public void closeAll(Cursor[] cursors) {
        for (Cursor c : cursors) {
            c.close();
        }
        close();
        cursors = null;
    }

    /**
     * Close all opened database and single cursor.
     * 
     * @param c Cursor that need to closed
     */
    public void closeAll(Cursor c) {
        c.close();
        close();
    }

    public void initDataBase() {
        try {
            Log.e("xifan", "init table");
            mDb.execSQL("insert into account_type values(null,'现金');");
            mDb.execSQL("insert into account_type values(null,'储蓄卡');");
            mDb.execSQL("insert into account_type values(null,'信用卡');");
            mDb.execSQL("insert into account_type values(null,'支付宝');");
            mDb.execSQL("insert into account values(null,0,0,0,1,'');");
            mDb.execSQL("insert into record_type values(null,'早餐','zc',datetime('now','localtime'),1,0,'h7');");
            mDb.execSQL("insert into record_type values(null,'午餐','wc',datetime('now','localtime'),1,0,'h12');");
            mDb.execSQL("insert into record_type values(null,'晚餐','wc',datetime('now','localtime'),1,0,'h18');");
            mDb.execSQL("insert into record_type values(null,'甜点','td',datetime('now','localtime'),1,0,'h14');");
            mDb.execSQL("insert into record_type values(null,'零食','ls',datetime('now','localtime'),1,0,'h20');");
            mDb.execSQL("insert into record_type values(null,'烟酒饮料','yjyl',datetime('now','localtime'),1,0,'h12,h18');");
            mDb.execSQL("insert into record_type values(null,'日用品','ryp',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'考试','ks',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'培训','px',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'书籍','sj',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'买菜','mc',datetime('now','localtime'),1,0,'h6');");
            mDb.execSQL("insert into record_type values(null,'购物','gw',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'人情','rq',datetime('now','localtime'),1,0,'h12');");
            mDb.execSQL("insert into record_type values(null,'坐车','zc',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'转账','zz',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'借钱','jq',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'代付','df',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'保险','bx',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'投资','tz',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'证券','zq',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'彩票','cp',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'缴费','jf',datetime('now','localtime'),1,0,'d1,d20');");
            mDb.execSQL("insert into record_type values(null,'充值','cz',datetime('now','localtime'),1,0,'d1,d30');");
            mDb.execSQL("insert into record_type values(null,'转账','zz',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'医疗','yl',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'娱乐','yl',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'棋牌','qp',datetime('now','localtime'),1,0,'h22');");
            mDb.execSQL("insert into record_type values(null,'聚会','jh',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'旅游','ly',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'燃油费','ryf',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'过路费','glf',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'车辆保修','clbx',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'快递','kd',datetime('now','localtime'),1,0,'');");
            mDb.execSQL("insert into record_type values(null,'工资','gz',datetime('now','localtime'),2,0,'d10');");
            mDb.execSQL("insert into record_type values(null,'股票盈利','gpyl',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'奖金','jj',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'应收款','ysk',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'报销款','bxk',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'中彩','zc',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'棋牌','qp',datetime('now','localtime'),2,0,'');");
            mDb.execSQL("insert into record_type values(null,'捡钱','jq',datetime('now','localtime'),2,0,'');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
