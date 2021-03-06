package com.liu.Account.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liu.Account.BmobRespose.BmobUsers;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.R;
import com.liu.Account.commonUtils.AppUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.module.dataobject.UserDo;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.utils.EncryptionUtils;
import com.liu.Account.utils.UserSettingUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by deonte on 16-1-25.
 */
public class LoginActivity extends AutoLayoutActivity{
    private Context context;
    private ImageView titleBack;
    private TextView topText;
    private TextView topRight;

    private EditText mLogin_user_name;
    private EditText mLogin_password;
    private Button mLogin_login;
    private TextView mFindPassword;
    private String userName,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context=LoginActivity.this;
        initTop();
        bindViews();
        mFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //找回密码界面
                startActivity(new Intent(context, RetrievePasswordActivity.class));
                finish();
            }
        });

        mLogin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = mLogin_user_name.getText().toString().trim();
                password = mLogin_password.getText().toString().trim();
                BmobLogin(userName, password);
            }
        });

        AppUtil.requestFocus(mLogin_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BmobUser bmobUser=BmobUser.getCurrentUser(context);
        if (bmobUser!=null&&bmobUser.getEmail()!=null){
            mLogin_user_name.setText(bmobUser.getEmail());
        }
    }

    private void BmobLogin(final String userName, final String password) {
        if (userName==null) {
            ToastUtil.showShort(context, getString(R.string.userNameNull));
            return;
        }else if (password==null){
            ToastUtil.showShort(context, getString(R.string.passwordNull));
            return;
        }else if (!AppUtil.isNetworkOK(context)){
            ToastUtil.showShort(context,getString(R.string.loginNetworkFalse));
            return;
        }else if (!AppUtil.isEmailNO(userName)){
            ToastUtil.showShort(context,getString(R.string.emailIncorrent));
            return;
        }else if (password.length()<6){
            ToastUtil.showShort(context,getString(R.string.passwordTooShort));
            return;
        }
        final ProgressDialog pro = new ProgressDialog(context);
        pro.setTitle("正在登陆");
        pro.setMessage("请稍等...");
        pro.show();

        final BmobUser user=new BmobUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.login(context, new SaveListener() {
            @Override
            public void onSuccess() {
                //登陆成功
                pro.dismiss();

                BmobUsers user1 = BmobUsers.getCurrentUser(context,BmobUsers.class);
                MobclickAgent.onProfileSignIn(user1.getObjectId());
                UserDo userDo=new UserDo();
                userDo.setInstallationId(UserSettingUtil.getInstallationId(context));
                userDo.setEmail(userName);
                userDo.setEmailVerified(user1.getEmailVerified()==true?'Y':'N');
                userDo.setPassword(EncryptionUtils.encrypt(password,userName));
                userDo.setNickName(user1.getNickName());


                //todo 和服务器交互获得user_id
//                HttpUtil.post(MethodConstant.ADD_USER, userDo, new ResponseHook() {
//                    @Override
//                    public void deal(Context context, JsonReceive receive) {
//                        if (receive.getResponse()!=null){
//                            if (receive.getResponse() != null) {
//                                Long userId = Long.valueOf(receive.getResponse().toString());
//                                UserSettingUtil.setUserId(context, userId);
//                                PrefsUtil d = new PrefsUtil(context, Constants.AutoUpdatePrefsName, Context.MODE_PRIVATE);
//                                d.putBoolean("isLoginAgain",true);
//                            }
//                        }
//                    }
//                },new DefaultErrorHook());
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                //登陆失败
                pro.dismiss();
                ToastUtil.showShort(context, getString(R.string.loginFailed) + s);
            }
        });
    }

    private void initTop() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        topText= (TextView) findViewById(R.id.title_text);
        topText.setText(R.string.login);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topRight= (TextView) findViewById(R.id.title_right);
        topRight.setText("注册");
        topRight.setTextColor(getResources().getColor(R.color.white));
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击注册进入注册页面
                startActivity(new Intent(context, RegisterActivity.class));
                finish();
            }
        });
    }
    private void bindViews() {

        mLogin_user_name = (EditText) findViewById(R.id.login_user_name);
        mLogin_password = (EditText) findViewById(R.id.login_password);
        mLogin_login = (Button) findViewById(R.id.login_login);
        mFindPassword= (TextView) findViewById(R.id.findPassword);
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LoginActivity");
        MobclickAgent.onPause(this);
    }
    /**
     * 返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return false;
    }
}
