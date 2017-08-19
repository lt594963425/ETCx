package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.etcxc.android.net.FUNC.NICKNAME_CHANGE;
import static com.etcxc.android.net.NetConfig.HOST;

/**
 * Created by LiuTao on 2017/8/19 0019.
 */

public class ChangeNickNameActivity extends BaseActivity implements View.OnClickListener {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private EditText mNickname;
    private Button mSaveNN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick_name);
        initView();
    }

    private void initView() {
        setTitle(R.string.change_nick_Name);
        mNickname = find(R.id.nick_name_edit);
        mSaveNN = find(R.id.nikname_save_button);
        mSaveNN.setOnClickListener(this);
        if (MeManager.getIsLogin())
            mNickname.setText(MeManager.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nikname_save_button:
                String newNickName = mNickname.getText().toString();
                modifyNickName(newNickName);

                break;
        }
    }

    private void modifyNickName(String newNickName) {
        showProgressDialog(R.string.loading);
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", MeManager.getUid());
            jsonObject.put("token", MeManager.getToken());
            jsonObject.put("nick_name", newNickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(HOST + NICKNAME_CHANGE)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToast(R.string.send_faid);
                closeProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("s_ok")) {
                        MeManager.clearName();
                        MeManager.setName(newNickName);
                        openActivity(PersonalInfoActivity.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(R.string.save_success);
                            }
                        });
                        closeProgressDialog();
                        finish();
                    }
                    if (code.equals("error")) {
                        String resultError = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(resultError);
                            }
                        });
                        closeProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

