package com.liu.Account.model;

import com.liu.Account.Constants.TagConstats;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by deonte on 16-1-24.
 */
public class AddBillData {
    private int year,month,dayOfMonth;


    private BigDecimal money;
    private String remark;

    private int type;
    private String tag;
    private Date happenTime;//显示时间
    private Date createTime;//创建时间




    private boolean isSelectTime=false;

    public AddBillData(boolean init){
        if (init) {
            Calendar calendar = Calendar.getInstance();
            year= calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            tag= TagConstats.tagList[0];
        }
    }

    public boolean getIsSelectTime() {
        return isSelectTime;
    }

    public void setIsSelectTime(boolean selectTime) {
        this.isSelectTime = selectTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}
