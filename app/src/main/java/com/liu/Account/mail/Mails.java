package com.liu.Account.mail;
import com.liu.Account.commonUtils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mails {
	private boolean isDebug=true;
	public static interface onMailsError{
		void hasError(String error);
	}
	private onMailsError mOnMailsError;
	public void setOnMailesError(onMailsError error){
		this.mOnMailsError =error;
	}


	private MailsProperties properties;
	private MailsAuthenticator authenticator;
	public Mails(MailsAuthenticator au,MailsProperties properties){
		this.properties=properties;
		this.authenticator=au;

	}
	public void setDebug(boolean isDebug){
		this.isDebug=isDebug;
	}
	/***
	 * 检查邮箱账户是否正确
	 * 原理:通过pop3协议进行连接，连接上则代表账户可用，否则为不可用
	 * @return boolean 是否认证通过
	 * 
	 * 163邮箱测试通过 qq邮箱未通过
	 */
	public  boolean checkEmailAccountWithPOP3(){
		boolean flag=false;
	    //取得Session对象
	    Properties pr=properties.getPop3Properties();

		System.out.println("checkEmailAccountWithPOP3 Properties:"+pr);
      
        Session session=Session.getInstance(pr, authenticator);
	    session.setDebug(isDebug);
	    
	    Store store = null;
	    try {
	    	
			store = session.getStore("pop3");
			store.connect();
			flag=true;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			flag=false;
			e.printStackTrace();
		} catch(MessagingException e){
			flag=false;
			e.printStackTrace();
		}
       
		return flag;
		
	}
	//// TODO: 16-4-15 测试此处
	/***
	 * ！！！未测验通过
	 * 检查邮箱账户是否正确
	 * 原理:通过smtp协议进行连接，连接上则代表账户可用，否则为不可用
	 * @return boolean 是否认证通过
	 *
	 * 163邮箱测试通过
	 */
	private  boolean checkEmailAccountWithSMTP(){
		boolean flag=false;
		//取得Session对象
		Properties pr=properties.getSmtpProperties();

		System.out.println("checkEmailAccountWithSMTP Properties:"+pr);

		Session session=Session.getInstance(pr, authenticator);
		session.setDebug(isDebug);

		Store store = null;
		try {

			store = session.getStore();
			store.connect();
			flag=true;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			flag=false;
			e.printStackTrace();
		} catch(MessagingException e){
			flag=false;

			e.printStackTrace();
		}

		return flag;

	}
	/***
	 * 检查邮箱账户是否正确
	 * 原理:通过IMAP协议进行连接，连接上则代表账户可用，否则为不可用
	 * @return boolean 是否认证通过
	 *
	 * 163邮箱测试通过 qq邮箱未通过
	 */
	public  boolean checkEmailAccountWithIMAP(){
		boolean flag=false;
		//取得Session对象
		Properties pr=properties.getIMAPProperties();

		System.out.println("checkEmailAccountWithIMAP Properties:"+pr);

		Session session=Session.getInstance(pr, authenticator);
		session.setDebug(isDebug);

		Store store = null;
		try {

			store = session.getStore("imap");
			store.connect();
			flag=true;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			flag=false;
			e.printStackTrace();
		} catch(MessagingException e){
			flag=false;
			e.printStackTrace();
		}

		return flag;

	}

	/****
	 * 发送带附件的文本邮件
	 * 附件路径为空时不发送此附件
	 * @param to 收件人
	 * @param subject 主题
	 * @param body 邮件内容
	 * @param fileMap 附件的map集合 key为附件名  value为附件
	 * @return
	 */
	public boolean sendTextEmail(String to
			,String subject,String body,Map<String,String> fileMap){
		boolean flag=false;

		Properties pr=properties.getSmtpProperties();

		System.out.println("sendTextEmail Properties:" + pr);
		Session session=Session.getInstance(pr,authenticator);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(authenticator.getUserName()));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);

			// 创建文本区域
			Multipart multipart = new MimeMultipart();

			// 设置邮件文本
			multipart.addBodyPart(messageBodyPart);

			// 设置附件
			//附件map不为空时
			//遍历map取出附件路径及名称，添加至邮件附件
			//key:文件名称
			//value:文件路径
			if(fileMap!=null&&fileMap.size()!=0){
				for(Map.Entry<String, String> entry:fileMap.entrySet()){
					if(entry.getKey().isEmpty()||entry.getValue().isEmpty()){
						continue;
					}
					try{
						messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(entry.getValue());
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(entry.getKey());
						multipart.addBodyPart(messageBodyPart);
					}catch(MessagingException e){
						System.out.println("传入路径不合法,找不到:"+entry.getValue());
					}
				}
			}


			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			flag=true;

		} catch (MessagingException e) {
			flag=false;
			throw new RuntimeException(e);
		}



		return flag;
	}
	/**
	 * 发送带附件的Html格式邮件
	 * 附件路径为空时不发送此附件
	 * @param to 收件人
	 * @param subject 主题
	 * @param htmlContent 邮件内容(Html格式)
	 * @param fileMap 附件的map集合 key为附件名  value为附件
	 * @return
	 */
	public boolean sendHtmlEmail(String to
			,String subject,String htmlContent,Map<String,String> fileMap){
		boolean flag=false;
		Properties pr=new Properties();
		pr=properties.getSmtpProperties();

		System.out.println("ReceiveMail Properties:"+pr);
		Session session=Session.getInstance(pr,authenticator);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(authenticator.getUserName()));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlContent, "text/html");

			// 创建文本区域
			Multipart multipart = new MimeMultipart();

			// 设置邮件文本
			multipart.addBodyPart(messageBodyPart);

			// 设置附件
			//附件map不为空时
			//遍历map取出附件路径及名称，添加至邮件附件
			//key:文件名称
			//value:文件路径
			if(fileMap!=null&&fileMap.size()!=0){
				for(Map.Entry<String, String> entry:fileMap.entrySet()){
					if(entry.getKey().isEmpty()||entry.getValue().isEmpty()){
						continue;
					}
					try{
						messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(entry.getValue());

						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(entry.getKey());
						multipart.addBodyPart(messageBodyPart);
					}catch(MessagingException e){
						System.out.println("传入路径不合法,找不到:"+entry.getValue());
					}
				}
			}


			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			flag=true;

		} catch (MessagingException e) {
			flag=false;
			throw new RuntimeException(e);
		}
		return flag;

	}

	/****
	 * 发送不带附件的文本邮件
	 * @param to 收件人
	 * @param subject 主题
	 * @param body 邮件内容
	 * @return
	 */
	public boolean sendTextEmail(String to
			,String subject,String body){
		return sendTextEmail( to, subject, body,null);
	}
	/****
	 * 发送不带附件的文本邮件
	 * @param to 收件人
	 * @param subject 主题
	 * @param body 邮件内容
	 * @param filename 附件名称
	 * @param filePath 附件路径
	 * @return
	 */
	public boolean sendTextEmail(String to
			,String subject,String body,String filename ,String filePath){
		Map<String,String> map=new HashMap<>();
		map.put(filename, filePath);
		return sendTextEmail(to, subject, body, map);
	}

	/****
	 * 发送不带附件的Html邮件
	 * @param to 收件人
	 * @param subject 主题
	 * @param body 邮件内容
	 * @return
	 */
	public boolean sendHtmlEmail(String to
			,String subject,String body){
		return sendHtmlEmail(to, subject, body, null);
	}
	/****
	 * 发送不带附件的Html邮件
	 * @param to 收件人
	 * @param subject 主题
	 * @param body 邮件内容
	 * @param filename 附件名称
	 * @param filePath 附件路径
	 * @return
	 */
	public boolean sendHtmlEmail(String to
			,String subject,String body,String filename ,String filePath){
		Map<String,String> map=new HashMap<>();
		map.put(filename, filePath);
		return sendHtmlEmail(to, subject, body, map);
	}

	/**
	 * 接收邮件
	 * 通过POP3协议
	 * @return list
	 */
	public List<EmailBean> receiveMailByPOP3()
	{
		List<EmailBean> list=new ArrayList<>();
		try {

			//create properties field
			Properties properties =this.properties.getPop3Properties();
			System.out.println("ReceiveMail Properties:"+properties);

			Session emailSession = Session.getInstance(properties, authenticator);
			emailSession.setDebug(isDebug);
			//create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3");

			store.connect();

			//create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				MailsReceive mailsReceive=new MailsReceive((MimeMessage)message);
				String subject=mailsReceive.getSubject();
				int messageNumberString=mailsReceive.getMessageNumber();
				String messageIdString=mailsReceive.getMessageID();
				long sentUnixTime =mailsReceive.getSentUnixTime();
				long receiveUnixTime=mailsReceive.getReceiveUnixTime();
				String brinfContent= null;
				try {
					brinfContent = mailsReceive.getBriefContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
//					mailsReceive.getMailContent();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Map<String,String> toAddress=mailsReceive.getToAddress();
				Map<String,String> ccAddress=mailsReceive.getCCAdress();
				Map<String,String> bccAddress=mailsReceive.getBCCAdress();
				Map<String,String> fromm=mailsReceive.getFrom();

				StringBuilder to=new StringBuilder();
				StringBuilder cc=new StringBuilder();
				StringBuilder bcc=new StringBuilder();
				StringBuilder from=new StringBuilder();
				for (Map.Entry<String,String> entry:toAddress.entrySet()){
					to.append(entry.getValue()+"<"+entry.getKey()+"> ");
				}
				for (Map.Entry<String,String> entry:ccAddress.entrySet()){
					cc.append(entry.getValue()+"<"+entry.getKey()+"> ");
				}
				for (Map.Entry<String,String> entry:bccAddress.entrySet()){
					bcc.append(entry.getValue()+"<"+entry.getKey()+"> ");
				}
				for (Map.Entry<String,String> entry:fromm.entrySet()){
					from.append(entry.getValue()+"<"+entry.getKey()+"> ");
				}

				EmailBean emails=new EmailBean();
				emails.setSubject(subject);
				emails.setBccAddress(bcc.toString());
				emails.setCcAddress(cc.toString());
				emails.setFromAddress(from.toString());
				emails.setToAddress(to.toString());
				emails.setMessageNumber(messageNumberString);
				emails.setMessageId(messageIdString);
				emails.setSentUnixTime(sentUnixTime);
				emails.setReceiveUnixTime(receiveUnixTime);
				emails.setBriefContent(brinfContent);
				emails.setUniqueFlag(toAddress+messageIdString+messageNumberString);
				/**
				Map<String,String> map=new HashMap<>();
				map.put("subject", subject);
				map.put("messageNumber", messageNumberString+"");
				map.put("messageId", messageIdString);
				map.put("sentUnixTime", sentUnixTime+"");
				map.put("receiveUnixTime",receiveUnixTime+"");
				map.put("brinfContent",brinfContent);
				map.put("to",to.toString());
				map.put("from",from.toString());**/
				list.add(emails);

			}

			//close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
			e.getMessage();
		}
		return list;
	}
	public EmailBean getMailDetails(int MessageNumber){
		EmailBean emails=new EmailBean();
		try {

			//create properties field
			Properties properties =this.properties.getPop3Properties();
			System.out.println("ReceiveMail Properties:"+properties);

			Session emailSession = Session.getInstance(properties, authenticator);
			emailSession.setDebug(isDebug);
			//create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3");

			store.connect();

			//create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");

			emailFolder.open(Folder.READ_ONLY);
			// retrieve the messages from the folder in an array and print it
			Message message = emailFolder.getMessage(MessageNumber);
			if (message!=null) {

				MailsReceive mailsReceive = new MailsReceive((MimeMessage) message);
				String subject = mailsReceive.getSubject();
				int messageNumberString = mailsReceive.getMessageNumber();
				String messageIdString = mailsReceive.getMessageID();
				long sentUnixTime = mailsReceive.getSentUnixTime();
				long receiveUnixTime = mailsReceive.getReceiveUnixTime();
				String brinfContent = null;
				try {
					brinfContent = mailsReceive.getBriefContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					List<MailContentBean> list=mailsReceive.getMailContent();
					for (MailContentBean aList : list) {
						LogUtil.i("----------------------------");


						LogUtil.i("" + aList.getTextHtml());
						LogUtil.i("" + aList.getTextPlain());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				Map<String, String> toAddress = mailsReceive.getToAddress();
				Map<String, String> ccAddress = mailsReceive.getCCAdress();
				Map<String, String> bccAddress = mailsReceive.getBCCAdress();
				Map<String, String> fromm = mailsReceive.getFrom();

				StringBuilder to = new StringBuilder();
				StringBuilder cc = new StringBuilder();
				StringBuilder bcc = new StringBuilder();
				StringBuilder from = new StringBuilder();
				for (Map.Entry<String, String> entry : toAddress.entrySet()) {
					to.append(entry.getValue() + "<" + entry.getKey() + "> ");
				}
				for (Map.Entry<String, String> entry : ccAddress.entrySet()) {
					cc.append(entry.getValue() + "<" + entry.getKey() + "> ");
				}
				for (Map.Entry<String, String> entry : bccAddress.entrySet()) {
					bcc.append(entry.getValue() + "<" + entry.getKey() + "> ");
				}
				for (Map.Entry<String, String> entry : fromm.entrySet()) {
					from.append(entry.getValue() + "<" + entry.getKey() + "> ");
				}

				emails.setSubject(subject);
				emails.setBccAddress(bcc.toString());
				emails.setCcAddress(cc.toString());
				emails.setFromAddress(from.toString());
				emails.setToAddress(to.toString());
				emails.setMessageNumber(messageNumberString);
				emails.setMessageId(messageIdString);
				emails.setSentUnixTime(sentUnixTime);
				emails.setReceiveUnixTime(receiveUnixTime);
				emails.setBriefContent(brinfContent);
				emails.setUniqueFlag(toAddress + messageIdString + messageNumberString);
			}
			//close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
			e.getMessage();
		}
		return emails;
	}
	public Message getMailDetailByPop3(int messageNumber) throws MessagingException {

		Properties properties =this.properties.getPop3Properties();
		System.out.println("getMailDetailByPop3 Properties:"+properties);

		Session emailSession = Session.getInstance(properties, authenticator);
		emailSession.setDebug(isDebug);
		//create the POP3 store object and connect with the pop server
		Store store = emailSession.getStore("pop3");

		store.connect();

		//create the folder object and open it
		Folder emailFolder = store.getFolder("INBOX");

		emailFolder.open(Folder.READ_ONLY);
		// retrieve the messages from the folder in an array and print it
		Message message = emailFolder.getMessage(messageNumber);
		return message;
	}
	/**
	 * 接收邮件
	 * 通过IMAP协议
	 * @return list
	 */
	public List<MailsReceive> receiveMailByIMAP()
	{
		List<MailsReceive> list=new ArrayList<>();
		try {

			//create properties field
			Properties properties =this.properties.getIMAPProperties();
			System.out.println("receiveMailByIMAP Properties:"+properties);

			Session emailSession = Session.getInstance(properties);
			emailSession.setDebug(isDebug);
			//create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("imap");

			store.connect(authenticator.getUserName(),authenticator.getPassword());

			//create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();

			System.out.println("new message count:" + emailFolder.getNewMessageCount());
			System.out.println("messages.length---" + messages.length);

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				MailsReceive mails=new MailsReceive((MimeMessage)message);
				list.add(mails);

			}

			//close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
			e.getMessage();
		}
		return list;
	}
	private void setError(String s){
		if (this.mOnMailsError !=null){
			mOnMailsError.hasError(s);
		}
	}
}