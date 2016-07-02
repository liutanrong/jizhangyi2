package com.liu.Account.mail;

import java.util.Properties;


//一、POP3收邮件：
//POP3: 110
//POP3 SSL: 995
//二、IMAP收邮件：
//IMAP: 143
//IMAP SSL: 993
//三、SMTP发邮件：
//SMTP: 25
//SMTP SSL: 465
//SMTP TLS: 587
public class MailsProperties {
	private String pop3Host=null;
	private String pop3Port=null;
	private String smtpHost=null;
	private String smtpPort=null;
	private String imapHost=null;
	private String imapPort=null;
	private boolean isSSl=false;

	public MailsProperties(String pop3Host, String pop3Port, String smtpHost, String smtpPort, String imapHost, String imapPort, boolean isSSl) {
		this.pop3Host = pop3Host;
		this.pop3Port = pop3Port;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.imapHost = imapHost;
		this.imapPort = imapPort;
		this.isSSl = isSSl;
	}

	public MailsProperties(String pop3Host, String smtpHost, String imapHost, boolean isSSL){
		this.pop3Host=pop3Host;
		this.smtpHost=smtpHost;
		this.imapHost=imapHost;
		this.isSSl=isSSL;
	}
	public Properties getPop3Properties(){
		Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "pop3");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.host", pop3Host);
        if (pop3Port!=null){
			props.put("mail.pop3.port", pop3Port);
		}else{
			if (isSSl){
				props.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.pop3.socketFactory.fallback","false");
				props.setProperty("mail.pop3s.host",pop3Host);
				props.setProperty("mail.pop3s.port","995");
				props.setProperty("mail.pop3.auth.plain.disable","true");
				props.setProperty("mail.pop3.auth.login.disable","true");
				props.setProperty("mail.smtp.auth","true");
				props.setProperty("mail.smtp.debug","true");
				props.setProperty("mail.smtp.host",smtpHost);
				props.setProperty("mail.smtp.port","465");
				props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback","false");
				props.setProperty("mail.smtp.socketFactory.port","465");
				props.setProperty("mail.smtp.starttls.enable","true");
				props.setProperty("mail.store.protocol","pop3");
				props.setProperty("mail.pop3.port", "995");
			}else {
				props.put("mail.pop3.port", "110");
			}
		}
		return props;
	}
	public Properties getIMAPProperties(){
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "imap");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.host", imapHost);
		if (imapPort!=null){
			props.put("mail.imap.port",imapPort);
		}else{
			if (isSSl){
				props.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.imap.socketFactory.fallback","false");
				props.setProperty("mail.imaps.host",imapHost);
				props.setProperty("mail.imaps.port","993");
				props.setProperty("mail.imap.auth.plain.disable","true");
				props.setProperty("mail.imap.auth.login.disable","true");
				props.setProperty("mail.smtp.auth","true");
				props.setProperty("mail.smtp.debug","true");
				props.setProperty("mail.smtp.host",smtpHost);
				props.setProperty("mail.smtp.port","465");
				props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback","false");
				props.setProperty("mail.smtp.socketFactory.port","465");
				props.setProperty("mail.smtp.starttls.enable","true");
				props.setProperty("mail.store.protocol","imap");
				props.setProperty("mail.imap.port", "993");
			}else {
				props.put("mail.imap.port", "143");
			}
		}
		return props;
	}
	public Properties getSmtpProperties(){
		Properties props=new Properties();
		props.setProperty("mail.transport.protocol", "smtp");  
        props.setProperty("mail.smtp.auth", "true");  
        props.setProperty("mail.host", smtpHost);
        if(smtpPort!=null){
        	props.put("mail.smtp.port", smtpPort);
        }else{
        	if(isSSl){
				props.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.pop3.socketFactory.fallback","false");
				props.setProperty("mail.pop3s.host",pop3Host);
				props.setProperty("mail.pop3s.port","993");
				props.setProperty("mail.pop3.auth.plain.disable","true");
				props.setProperty("mail.pop3.auth.login.disable","true");
				props.setProperty("mail.smtp.auth","true");
				props.setProperty("mail.smtp.debug","true");
				props.setProperty("mail.smtp.host",smtpHost);
				props.setProperty("mail.smtp.port","465");
				props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback","false");
				props.setProperty("mail.smtp.socketFactory.port","465");
				props.setProperty("mail.smtp.starttls.enable","true");
				props.setProperty("mail.store.protocol","smtp");
				props.setProperty("mail.pop3.port", "995");
        	}else{
        		props.put("mail.smtp.port", "25");
        	}
        }
        return props;
	}
}