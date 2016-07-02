package com.liu.Account.mail;

/**
 * Created by tanrong on 16/7/1.
 */
public class EmailBean {
    private String uniqueFlag;
    private int MessageNumber;
    private String messageId;
    private String toAddress;
    private String ccAddress;
    private String bccAddress;
    private String fromAddress;
    private String subject;
    private long sentUnixTime;
    private long receiveUnixTime;
    private String briefContent;
    private boolean isRead=false; //0未读 1已读

    public String getUniqueFlag() {
        return uniqueFlag;
    }

    public void setUniqueFlag(String uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    public int getMessageNumber() {
        return MessageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        MessageNumber = messageNumber;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(String bccAddress) {
        this.bccAddress = bccAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getSentUnixTime() {
        return sentUnixTime;
    }

    public void setSentUnixTime(long sentUnixTime) {
        this.sentUnixTime = sentUnixTime;
    }

    public long getReceiveUnixTime() {
        return receiveUnixTime;
    }

    public void setReceiveUnixTime(long receiveUnixTime) {
        this.receiveUnixTime = receiveUnixTime;
    }

    public String getBriefContent() {
        return briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
