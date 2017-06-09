package com.etcxc.android.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.UIUtils;
import com.tencent.mm.sdk.openapi.SendAuth;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.R.id.editText1;
import static com.etcxc.android.R.id.editText2;


/**
 * Created by 刘涛 on 2017/6/7 0007.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private boolean userTag;
    private EditText f2_editText1;
    private EditText f2_editText2;
    private TextView f2_textView3;
    private ImageView iv_wx_login;
    private ImageView iv_qq_login;
    public String userName;
    public String passWord;
    private  Bitmap bitmap;
    private FrameLayout flwx_user;
    private FrameLayout fl_f2;
    SendAuth.Req req;
    private static final String WEIXIN_SCOPE = "snsapi_userinfo";// 用于请求用户信息的作用域
    private static final String WEIXIN_STATE = "login_state"; // 自定义

    private static final int ERROR = 1;
    private static final int SUCCESS = 2 ;
    private TextView username;
    private TextView usersex;
    private ImageView head;
    private TextView openid;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    head.setImageBitmap((Bitmap) msg.obj);
                    break;

                case ERROR:

                    Toast.makeText(App.getContext(), "请求超时", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        flwx_user = (FrameLayout) findViewById(R.id.flwx_user);
        fl_f2 = (FrameLayout) findViewById(R.id.fl_f2);

            username =(TextView) findViewById(R.id.tv_username);
             usersex =(TextView) findViewById(R.id.tv_usersex);
                head = (ImageView) findViewById(R.id.iv_userhead);
              openid =(TextView) findViewById(R.id.tv_userid);
        f2_editText1 = (EditText) findViewById(editText1);//用户名
        f2_editText2 = (EditText)findViewById(editText2);//密码
        f2_textView3 = (TextView)findViewById(R.id.textView3);//登录
         iv_wx_login = (ImageView)findViewById(R.id.iv_wx_login);
         iv_qq_login = (ImageView)findViewById(R.id.iv_qq_login);
        initview();

    }

    private void initview() {
        userTag = PrefUtils.getBoolean(App.getContext(), "userTag",false);
        if(userTag){
            //加载头像url
            String headurl = PrefUtils.getString(App.getContext(), "headurl", null);
            flwx_user.setVisibility(View.VISIBLE);
            fl_f2.setVisibility(View.INVISIBLE);
            if(headurl!=null)
            SetWXUserInfo(headurl);
        }else{
            fl_f2.setVisibility(View.VISIBLE);
            flwx_user.setVisibility(View.INVISIBLE);
        }
        f2_textView3.setOnClickListener( this);
        iv_wx_login.setOnClickListener(this);
        iv_qq_login.setOnClickListener(this);
        userName = f2_editText1.getText().toString();
        passWord = f2_editText2.getText().toString();
        username.setText(PrefUtils.getString(App.getContext(), "username", null));
        openid.setText(PrefUtils.getString(App.getContext(), "openid", null));
        // headurl.setText(PrefUtils.getString(App.getContext(),"headurl",null));
        if (PrefUtils.getInt(App.getContext(), "usersex", 1) == 1) {
            usersex.setText("男");
        } else {
            usersex.setText("女");
        }

    }

    private void SetWXUserInfo(final String url) {
        new Thread(){
            @Override
            public void run() {
                OkHttpClient uOkHttpClient = new OkHttpClient();
                Request requst = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                uOkHttpClient.newCall(requst).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is  = response.body().byteStream();//字节流
                        bitmap = BitmapFactory.decodeStream(is);
                        //使用Hanlder发送消息
                        Message msg = Message.obtain();
                        msg.what = SUCCESS;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);

                    }
                });
            }

        }.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView3:
                Toast.makeText(UIUtils.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_wx_login://微信登录
                WXLogin();
                finish();
                break;
            case R.id.iv_qq_login://
                QQLogin();
                break;

        }
    }
    /**
     * 登录微信
     */

    private void WXLogin() {
        if (App.WXapi != null && App.WXapi.isWXAppInstalled()) {
            req = new SendAuth.Req();
            req.scope = WEIXIN_SCOPE;
            req.state = WEIXIN_STATE;
            App.WXapi.sendReq(req);
            Log.i(TAG,"。。。。。。。。。。。。WxLogin()，微信登录。。。。。。。");
            Toast.makeText(this, "请稍后....", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "用户未安装微信", Toast.LENGTH_SHORT).show();

    }

    /**
     * qq登录
     */
    private void QQLogin() {
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
    }


}
