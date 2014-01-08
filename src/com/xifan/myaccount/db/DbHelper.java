
package com.xifan.myaccount.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "account.db"; // 数据库名称
    public static final int version = 1; // 数据库版本

    public DbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbHelper(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.e("xifan", "create table");
            db.execSQL("create table if not exists account_type (id integer primary key autoincrement,typename varchar(20) not null);");
            db.execSQL("create table if not exists account (id integer primary key autoincrement,balance money,revenue money,expend money,accountType integer,foreign key(accountType) references account_type(id));");
            db.execSQL("create table if not exists detail (id integer primary key autoincrement,accountId integer,recordType varchar(100),moneyAmount money,picUri varchar(200), recordDate datetime,location varchar(100),note varchar(320),isReimbursabled integer,foreign key(accountId) references account(id));");
            db.execSQL("create table if not exists record_type (id integer primary key autoincrement,type varchar(20),last_date datetime,operate_type integer,freq integer,event_stamp varchar(20));");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("xifan", "upgrade table");
    }

    public void initDataBase(SQLiteDatabase db) {
        try {
            Log.e("xifan", "init table");
            db.execSQL("insert into account_type values(null,'现金');");
            db.execSQL("insert into account_type values(null,'储蓄卡');");
            db.execSQL("insert into account_type values(null,'信用卡');");
            db.execSQL("insert into account_type values(null,'支付宝');");
            db.execSQL("insert into account values(null,0,0,0,1);");
            db.execSQL("insert into record_type values(null,'早餐',datetime('now','localtime'),1,0,'h7');");
            db.execSQL("insert into record_type values(null,'午餐',datetime('now','localtime'),1,0,'h12');");
            db.execSQL("insert into record_type values(null,'晚餐',datetime('now','localtime'),1,0,'h18');");
            db.execSQL("insert into record_type values(null,'甜点',datetime('now','localtime'),1,0,'h14');");
            db.execSQL("insert into record_type values(null,'零食',datetime('now','localtime'),1,0,'h20');");
            db.execSQL("insert into record_type values(null,'烟酒饮料',datetime('now','localtime'),1,0,'h12,h18');");
            db.execSQL("insert into record_type values(null,'日用品',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'考试',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'培训',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'书籍',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'买菜',datetime('now','localtime'),1,0,'h6');");
            db.execSQL("insert into record_type values(null,'购物',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'人情',datetime('now','localtime'),1,0,'h12');");
            db.execSQL("insert into record_type values(null,'坐车',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'转账',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'借钱',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'代付',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'保险',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'投资',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'证券',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'彩票',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'缴费',datetime('now','localtime'),1,0,'d1,d20');");
            db.execSQL("insert into record_type values(null,'充值',datetime('now','localtime'),1,0,'d1,d30');");
            db.execSQL("insert into record_type values(null,'转账',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'医疗',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'娱乐',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'棋牌',datetime('now','localtime'),1,0,'h22');");
            db.execSQL("insert into record_type values(null,'聚会',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'旅游',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'燃油费',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'过路费',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'车辆保修',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'快递',datetime('now','localtime'),1,0,'');");
            db.execSQL("insert into record_type values(null,'工资',datetime('now','localtime'),2,0,'d10');");
            db.execSQL("insert into record_type values(null,'股票盈利',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'奖金',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'应收款',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'报销款',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'中彩',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'棋牌',datetime('now','localtime'),2,0,'');");
            db.execSQL("insert into record_type values(null,'捡钱',datetime('now','localtime'),2,0,'');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
