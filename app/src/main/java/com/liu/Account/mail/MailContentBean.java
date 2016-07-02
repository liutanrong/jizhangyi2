package com.liu.Account.mail;

import java.io.InputStream;

/**
 * Created by deonte on 16-4-14.
 */
public class MailContentBean {
    private String textPlain;
    private String textHtml;
    private InputStream is;
    private String fileName;
    public MailContentBean() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTextPlain() {
        return textPlain;
    }

    public void setTextPlain(String textPlain) {
        this.textPlain = textPlain;
    }

    public String getTextHtml() {
        return textHtml;
    }

    public void setTextHtml(String textHtml) {
        this.textHtml = textHtml;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }
}
