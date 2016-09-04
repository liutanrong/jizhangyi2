package com.liu.Account.database;


import com.orm.SugarRecord;
import com.orm.dsl.NotNull;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by tanrong on 16/9/4.
 * 账单表实体类
 */
public class Bill extends SugarRecord {
    public static final int MONEY_TYPE_IN=2;
    public static final int MONEY_TYPE_OUT=1;

    private Long userId;
    private Long installationId;
    @NotNull
    private BigDecimal spendMoney;
    private String remark;
    @NotNull
    private Integer moneyType;// 1 支出 2 收入
    @NotNull
    private String tag;
    private Integer tagId;
    @NotNull
    private Boolean isDelete=false; //是否被删除
    private Date deleteTime;
    @NotNull
    private Date happenTime;//账单发生时间
    @NotNull
    private Date gmtCreate;//创建账单时间
    private Date gmtModified;//修改时间

    public Bill() {
    }

    public Bill(Integer moneyType, BigDecimal spendMoney, String tag, Boolean isDelete, Date happenTime, Date gmtCreate) {
        this.moneyType = moneyType;
        this.spendMoney = spendMoney;
        this.tag = tag;
        this.isDelete = isDelete;
        this.happenTime = happenTime;
        this.gmtCreate = gmtCreate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
    }

    public BigDecimal getSpendMoney() {
        return spendMoney.setScale(3,BigDecimal.ROUND_HALF_DOWN);
    }

    public void setSpendMoney(BigDecimal spendMoney) {
        this.spendMoney = spendMoney.setScale(3,BigDecimal.ROUND_HALF_DOWN);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(Integer moneyType) {
        this.moneyType = moneyType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean delete) {
        isDelete = delete;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
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

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
