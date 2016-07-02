package com.liu.Account.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * Created by deonte on 16-4-14.
 */
public class MailsReceive {
    private MimeMessage message;

    public MailsReceive(MimeMessage message){
        this.message=message;
    }


    /**
     *
     * @param type 要取得的地址类型 To:收件人 CC:抄送 BCC:密送
     * @return map<address key>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private Map<String,String> getMailAddress(String type) throws MessagingException, UnsupportedEncodingException {
        Map<String,String> addressMap=new HashMap<>();
        String addType = type.toUpperCase(Locale.getDefault());
        InternetAddress[] address = null;
        switch (addType) {
            case "TO":
                address = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
                break;
            case "CC":
                address = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
                break;
            case "BCC":
                address = (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
                break;

            default:
                System.out.println("error type!");
                break;
        }
        if (address != null) {
            for (InternetAddress addres : address) {
                String mailAddress = addres.getAddress();
                if (mailAddress != null) {
                    mailAddress = MimeUtility.decodeText(mailAddress);
                } else {
                    mailAddress = "";
                }
                String name = addres.getPersonal();
                if (name != null) {
                    name = MimeUtility.decodeText(name);
                } else {
                    name = "";
                }
                addressMap.put(mailAddress,name);
            }
        }
        return addressMap;

    }

    /**
     * 取得收件人地址
     *
     *
     * @return map<address key>
     */
    public Map<String,String> getToAddress(){
        Map<String,String> map=new HashMap<>();
        try {
            map=getMailAddress("TO");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 取得抄送地址
     *
     * @return map<address key>
     */
    public Map<String,String> getCCAdress(){
        Map<String,String> map=new HashMap<>();
        try {
            map=getMailAddress("CC");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 取得密送地址
     *
     * @return map<address key>
     */
    public Map<String,String> getBCCAdress(){
        Map<String,String> map=new HashMap<>();
        try {
            map=getMailAddress("BCC");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 取得发件人地址
     * @return map<address key>
     * @throws MessagingException
     */
    public Map<String,String> getFrom() throws MessagingException {
        Map<String,String> map=new HashMap<>();
        InternetAddress address[] = (InternetAddress[]) message.getFrom();
        for (InternetAddress ad:address) {
            String addr = ad.getAddress();
            String name = ad.getPersonal();
            if (addr == null) {
                addr = "";
            }
            if (name == null) {
                name = "";
            }
            map.put(addr,name);
        }
        return map;
    }

    /**
     * 取得邮件主题
     * @return
     */
    public String getSubject(){
        String subject = "";
        try {
            subject = MimeUtility.decodeText(message.getSubject());
            if (subject == null) {
                subject = "";
            }
        } catch (Exception e) {
        }
        return subject;
    }

    /**
     * 取得邮件的发送时间
     * @return 发送时间的unixTime
     * @throws MessagingException
     */
    public long getSentUnixTime() throws MessagingException {
        long unixTime=0;
        Date sentdata = message.getSentDate();
        if (sentdata != null) {
            unixTime=sentdata.getTime()/1000;
        } else {
            unixTime=-1;
        }
        return unixTime;
    }
    /**
     * 取得邮件的接收时间
     * @return 接收时间的unixTime
     * @throws MessagingException
     */
    public long getReceiveUnixTime() throws MessagingException {
        long unixTime=0;
        Date sentdata = message.getReceivedDate();
        if (sentdata != null) {
            unixTime=sentdata.getTime()/1000;
        } else {
            unixTime=-1;
        }
        return unixTime;
    }

    /**
     * 取得该邮件是否为新邮件
     * @return
     * @throws MessagingException
     */
    public boolean isNew() throws MessagingException {
        boolean isnew = false;
        Flags flags = ((Message) message).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                break;
            }
        }
        return isnew;
    }

    /**
     * 取得该邮件的messageID
     * @return
     * @throws MessagingException
     */
    public String getMessageID() throws MessagingException {

        return message.getMessageID();
    }

    /**
     * 取得该邮件的messageNUmber
     * @return
     */
    public int getMessageNumber(){
        return message.getMessageNumber();
    }

    /**
     * 取得邮件是否已被回复
     * @return
     * @throws MessagingException
     */
    public boolean getReplySign() throws MessagingException {
        boolean replySign = false;
        String needreply[] = message.getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replySign = true;
        }
        return replySign;
    }
    /**
     * 取得邮件内容
     * @return list内可能有多重附件
     * @throws Exception
     */
    public List<MailContentBean> getMailContent() throws Exception {
        List<MailContentBean> result=new ArrayList<>();
        Object content=message.getContent();
        if (content instanceof MimeMultipart){
            MimeMultipart mult= (MimeMultipart) content;
            List<MailContentBean> temp=new ArrayList<>();
            result=compileMailContent(mult,temp);
        }
        return result;
    }
    /**
     * 对复杂邮件的解析
     * @param multipart
     * @throws MessagingException
     * @throws IOException
     */
    private List<MailContentBean> compileMailContent(Multipart multipart,List<MailContentBean> mailContent) throws Exception {
        MailContentBean content=new MailContentBean();
        int count = multipart.getCount();
        for (int idx=0;idx<count;idx++) {
            BodyPart bodyPart = multipart.getBodyPart(idx);
            if (bodyPart.isMimeType("text/plain")) {
                content.setTextPlain((String)bodyPart.getContent());
            } else if(bodyPart.isMimeType("text/html")) {
                content.setTextHtml((String) bodyPart.getContent());
            } else if(bodyPart.isMimeType("multipart/*")) {
                Multipart mpart = (Multipart)bodyPart.getContent();
                mailContent.add(content);
                compileMailContent(mpart, mailContent);

            } else if (bodyPart.isMimeType("application/octet-stream")) {
                String disposition = bodyPart.getDisposition();
                if (disposition.equalsIgnoreCase(BodyPart.ATTACHMENT)) {
                    String fileName = bodyPart.getFileName();
                    InputStream is = bodyPart.getInputStream();
                    content.setFileName(fileName);
                    content.setIs(is);
                }
            }
        }
        mailContent.add(content);
        return mailContent;
    }
    public String getBriefContent() throws IOException, MessagingException {
        StringBuilder builder=new StringBuilder();

        Object content=message.getContent();
//        if (content instanceof MimeMultipart) {
//            MimeMultipart mult = (MimeMultipart) content;
//            int count = mult.getCount();
//            for (int i=0;i<count;i++) {
//                BodyPart bodyPart = mult.getBodyPart(i);
//                builder.append((String) bodyPart.getContent());
//            }
//        }
        builder.append(content.toString());
        if (builder.length()>100){
            return builder.substring(0,100);
        }else {
            return builder.toString();
        }

    }


    public String getMailContentL() {
        StringBuffer stringBuffer = null;

        Object object=null;
        try {
            object=message.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return object.toString();

    }

    public StringBuffer compileMailContent(Part part) throws Exception {
        StringBuffer mailContent=null;
        String contentType = part.getContentType();
        int nameIndex = contentType.indexOf("name");
        boolean connName = false;
        if (nameIndex != -1) {
            connName = true;
        }
        if (part.isMimeType("text/plain") && !connName) {
            mailContent.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && !connName) {
            mailContent.append((String) part.getContent());
        } else if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart)part.getContent();

            int counts = multipart.getCount();
            for(int i=0;i<counts;i++){
                compileMailContent(multipart.getBodyPart(i));
            }
        }else if(part.isMimeType("message/rfc822")){
            compileMailContent((Part)part.getContent());
        }else{}

        return mailContent;
    }
}
