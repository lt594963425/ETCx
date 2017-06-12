package com.etcxc.android.ui.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.PhoneRegistActivity;
import com.etcxc.android.base.App;
import com.etcxc.android.utils.PrefUtils;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.base.App.userTag;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class Fragment2 extends Fragment implements View.OnClickListener {
    private Button bt_f2_rg;
    private ImageView head;
    private TextView username;
    private RelativeLayout rl_login;
    private static final int ERROR = 1;
    private static final int SUCCESS = 2;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment2_item1, null);
        head = (ImageView) view.findViewById(R.id.f2_uhead);
        username = (TextView) view.findViewById(R.id.f2_uid);
        rl_login = (RelativeLayout) view.findViewById(R.id.rl_login);
        bt_f2_rg =(Button)view.findViewById(R.id.bt_f2_rg);
        initView();
        return view;
    }

    private void initView() {
        rl_login.setOnClickListener(this);
        bt_f2_rg.setOnClickListener(this);
        userTag = PrefUtils.getBoolean(App.getContext(), "userTag",false);
        if(userTag) {
            //加载头像url
            String headurl = PrefUtils.getString(App.getContext(), "headurl", null);
            username.setText(PrefUtils.getString(App.getContext(), "username", null));
            if (headurl != null)
                SetWXUserInfo(headurl);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_login:
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_f2_rg:
                Intent intent2 = new Intent(getActivity(), PhoneRegistActivity.class);
                startActivity(intent2);
                break;
        }
    }


    public void setUserName(String str) {
        username.setText(str);
    }


    public void setHead(String headurl) {
        if(headurl != null)
        SetWXUserInfo(headurl);
    }

    private void SetWXUserInfo(final String url) {
        new Thread() {
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
                        InputStream is = response.body().byteStream();//字节流
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
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
}
