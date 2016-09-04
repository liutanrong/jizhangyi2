package com.liu.Account.model;

import com.orm.dsl.NotNull;

import java.math.BigDecimal;
import java.util.Date;

/**
 *Created by liu on 15-10-11.
 * */
public class HomeListViewData {

    private BigDecimal spendMoney;
    private String remark;
    private Integer moneyType;// 1 支出 2 收入
    private String tag;
    private Integer tagId;
    private Date happenTime;//账单发生时间
    private Date gmtCreate;//创建账单时间



    String allInMoney,allOutMoney,totalMoney;
    public String getAllInMoney() {
        return allInMoney;
    }

    public void setAllInMoney(String allInMoney) {
        this.allInMoney = allInMoney;
    }

    public String getAllOutMoney() {
        return allOutMoney;
    }

    public void setAllOutMoney(String allOutMoney) {
        this.allOutMoney = allOutMoney;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getSpendMoney() {
        return spendMoney;
    }

    public void setSpendMoney(BigDecimal spendMoney) {
        this.spendMoney = spendMoney;
    }

    public Integer getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(Integer moneyType) {
        this.moneyType = moneyType;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public HomeListViewData() {
    }

    public HomeListViewData(String remark, Date gmtCreate, BigDecimal money) {
        this.remark = remark;
        this.gmtCreate = gmtCreate;
        this.spendMoney = money;
    }
}
