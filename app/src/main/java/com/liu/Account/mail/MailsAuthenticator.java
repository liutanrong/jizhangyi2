package com.liu.Account.mail;

import javax.mail.PasswordAuthentication;

public class MailsAuthenticator extends javax.mail.Authenticator{
	private String userName;
	private String password;
	public MailsAuthenticator(String username, String password){
		this.userName=username;
		this.password=password;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName(){
		return this.userName;
	}
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}
