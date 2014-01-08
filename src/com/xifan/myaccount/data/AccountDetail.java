
package com.xifan.myaccount.data;

import java.text.DecimalFormat;

public class AccountDetail {
    private int accountId;
    private int recordType;
    private float money;
    private String picUri;
    private String date;
    private String location;
    private String note;
    boolean isReimbursabled;

    public static final int TYPE_EXPEND = 1; // 支出
    public static final int TYPE_REVENUE = 2; // 收入
    public static final int TYPE_TRANSFER = 3;// 转账

    public AccountDetail() {
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeOfDate() {
        return date.substring(11, date.length());
    }

    public String getOnlyDate() {
        return date.substring(0, 11).trim();
    }

    public String getMoney() {
        return new DecimalFormat("0.00").format(money);
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isReimbursabled() {
        return isReimbursabled;
    }

    public void setReimbursabled(int isReimbursabled) {
        this.isReimbursabled = isReimbursabled == 1 ? true : false;
    }

}
