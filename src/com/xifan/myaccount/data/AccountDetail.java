
package com.xifan.myaccount.data;


public class AccountDetail {
    private String mType;
    private String mDate;
    private float mMoney;
    private String mNote;
    private String mPicUri;

    public AccountDetail() {
    }

    public AccountDetail(String date, float money, String note, String pic, String type) {
        setmDate(date);
        setmMoney(money);
        setmNote(note);
        setmPicUri(pic);
        setmType(type);
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public float getmMoney() {
        return mMoney;
    }

    public void setmMoney(float mMoney) {
        this.mMoney = mMoney;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

    public String getmPicUri() {
        return mPicUri;
    }

    public void setmPicUri(String mPicUri) {
        this.mPicUri = mPicUri;
    }

}
