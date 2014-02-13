
package com.xifan.myaccount.data;

public class TypeInfo{
    private String typeName;
    private String typePinyin;
    private String lastDate;
    private int frequency;
    private String stamp;
    private Integer weight;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypePinyin() {
        return typePinyin;
    }

    public void setTypePinyin(String typePinyin) {
        this.typePinyin = typePinyin;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

}
