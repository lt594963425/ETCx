package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.NICKNAME_CHANGE;
import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.net.NetConfig.JSON;

/**
 * Created by LiuTao on 2017/8/19 0019.
 */

public class ChangeNickNameActivity extends BaseActivity implements View.OnClickListener {
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
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", MeManager.getUid())
                        .put("token", MeManager.getToken())
                        .put("nick_name", newNickName);
                e.onNext(OkHttpUtils
                        .postString()
                        .url(HOST + NICKNAME_CHANGE)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);

                        String code = jsonObject.getString("code");
                        if (code.equals("s_ok")) {
                            MeManager.clearName();
                            MeManager.setName(newNickName);
                            openActivity(PersonalInfoActivity.class);
                            ToastUtils.showToast(R.string.save_success);
                            finish();
                        }
                        if (code.equals("error")) {
                            String msg = jsonObject.getString("message");
                            switch (msg){
                                case "nickName exceed 16byte":
                                    ToastUtils.showToast(R.string.nick_name_is_long);
                                    break;
                                case NetConfig.ERROR_TOKEN:
                                    MeManager.setIsLgon(false);
                                    openActivity(LoginActivity.class);
                                    finish();
                                    break;
                                default:
                                    ToastUtils.showToast(msg);
                                    break;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
    }
}

