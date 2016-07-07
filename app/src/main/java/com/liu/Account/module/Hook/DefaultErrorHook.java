package com.liu.Account.module.Hook;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.activity.MainActivity;
import com.liu.Account.application.MyApplication;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.mail.Mails;
import com.liu.Account.mail.MailsAuthenticator;
import com.liu.Account.mail.MailsProperties;
import com.liu.Account.network.beans.ErrorHook;

import java.util.List;
import java.util.Map;

/**
 * Created by pak2c on 16/4/11.
 */
public class DefaultErrorHook implements ErrorHook {

    @Override
    public void deal(Context context, VolleyError error,String request) {

        LogUtil.d("error:"+error.toString());

        LogUtil.d("errorRequest:"+request);
        MyApplication application=new MyApplication();
        context=application.getContext();

            JSONObject re=JSON.parseObject(request);
            String method=re.getString("method");
            LogUtil.i("method:"+method);
            if (method.equalsIgnoreCase( MethodConstant.ADD_ACCESSLOG)||method.equalsIgnoreCase(MethodConstant.ADD_EVENTLOG)
                    ||method.equalsIgnoreCase(MethodConstant.UPDDATE_INSTALLATION)
                    ){
                PrefsUtil prefsUtil=new PrefsUtil(context,Constants.ERROR_NETWORK_SP,Context.MODE_PRIVATE);
                int all=prefsUtil.getAll().size();
                prefsUtil.putString(all+"",JSON.toJSONString(request));
            }




        SendEmailsTask task=new SendEmailsTask("记账易网络错误自动报告", JSON.toJSONString(request)+"\n\n\n"+error.toString());
        //task.execute();
    }

    private class SendEmailsTask extends AsyncTask<String,Integer,Boolean> {

        private String subject;
        private String content;

        public SendEmailsTask( String subject, String content) {
            this.subject = subject;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result=false;

            String userName = "jizhangyi_xian@163.com";
            String pwd = "zhangyijiji123";
            String smtpHost = "smtp.163.com";
            String imapHost = "imap.163.com";
            String pop3Host = "pop.163.com";
            String toAddress="liutanrong0425@163.com";
            boolean isSSL = false;
            MailsAuthenticator authenticator=new MailsAuthenticator(userName,pwd);
            MailsProperties properties=new MailsProperties(pop3Host,smtpHost,imapHost,isSSL);

            LogUtil.i("userName:"+userName+" subject:"+subject+" to:"+userName);
            try {

                Mails mails=new Mails(authenticator,properties);
                mails.setDebug(true);
                result=mails.sendHtmlEmail(userName,subject,content);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                LogUtil.i("错误日志发送成功");
            }else {

                LogUtil.i("错误日志发送失败");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
