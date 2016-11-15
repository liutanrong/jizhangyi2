package com.liu.Account.model;

/**
 * Created by tanrong on 16/10/17.
 */

public class AllBillFragmentData {
    private String dateTime;
    private String totalMoney;
    private String moneyType; //1是收入 2是支出

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }
}
