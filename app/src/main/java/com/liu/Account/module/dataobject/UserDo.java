package com.liu.Account.module.dataobject;

import java.util.Date;

/**
 * Created by tanrong on 16/7/4.
 */
public class UserDo {
    private Long id;
    private Long installationId;
    private String phoneNum;
    private String email;
    private Character emailVerified;
    private String password;
    private String nickName;
    private Date lastInsertTime;
    private Date lastModifiedTime;

    private String databasePath;
    private String imageHeadPath;

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getImageHeadPath() {
        return imageHeadPath;
    }

    public void setImageHeadPath(String imageHeadPath) {
        this.imageHeadPath = imageHeadPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Character getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Character emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getLastInsertTime() {
        return lastInsertTime;
    }

    public void setLastInsertTime(Date lastInsertTime) {
        this.lastInsertTime = lastInsertTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
