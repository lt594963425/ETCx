package com.etcxc.android.ui.activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.Api;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.utils.UIUtils.isLook;

/**
 * Created by 刘涛 on 2017/7/4 0004.
 *
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mOldPwdEdt, mNewPwdEdt;
    private ImageView mOldPwdDte, mNewPwdSee, mOldPwdSee, mNewPwdDte;
    private Button mSavePwdBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    private void initView() {
        setTitle(R.string.changepassword);
        mOldPwdEdt = find(R.id.old_password_edt);
        mOldPwdDte = find(R.id.old_password_delete);
        mNewPwdEdt = find(R.id.new_password_edt);
        mNewPwdDte = find(R.id.new_password_delete);
        mNewPwdSee = find(R.id.password_see_iv);
        mSavePwdBtn = find(R.id.password_save_button);
        mOldPwdSee = find(R.id.oldpassword_see_iv);
        mOldPwdDte.setOnClickListener(this);
        mNewPwdDte.setOnClickListener(this);
        mNewPwdSee.setOnClickListener(this);
        mSavePwdBtn.setOnClickListener(this);
        mOldPwdSee.setOnClickListener(this);
        mOldPwdEdt.addTextChangedListener(new myTextWatcher(mOldPwdEdt, mOldPwdDte));
        mNewPwdEdt.addTextChangedListener(new myTextWatcher(mNewPwdEdt, mNewPwdDte));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.old_password_delete:
                mOldPwdEdt.setText("");
                break;
            case R.id.new_password_delete:
                mNewPwdEdt.setText("");
                break;
            case R.id.oldpassword_see_iv:
                isLook(mOldPwdEdt, mOldPwdSee, R.drawable.vd_close_eyes_black, R.drawable.vd_open_eyes_black);
                break;
            case R.id.password_see_iv:
                isLook(mNewPwdEdt, mNewPwdSee, R.drawable.vd_close_eyes_black, R.drawable.vd_open_eyes_black);
                break;

            case R.id.password_save_button:  //保存
                String oldPassWord = mOldPwdEdt.getText().toString().trim();
                String newPassWord = mNewPwdEdt.getText().toString().trim();
                String data;

                if (!LocalThrough(oldPassWord, newPassWord)) {
                    data = "tel/" + MeManager.getSid() +
                            "/pwd/" + Md5Utils.encryptpwd(oldPassWord) +
                            "/new_pwd/" + Md5Utils.encryptpwd(newPassWord);
                    modifyPwd(Api.modifyPwdServerUrl + data);
                }
                break;
        }
    }

    /**
     * 修改密码网络请求
     *
     * @param url 修改密码url
     */
    private void modifyPwd(String url) {
        showProgressDialog(getString(R.string.modify_pwd));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "subscribe: "+url);
                String result = OkClient.get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseResultJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.modify_pwd_fail);
                        LogUtil.e(TAG, "changePwd", throwable);
                    }
                });
    }

    private void parseResultJson(@NonNull String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject != null) {
            String code = jsonObject.getString("code");
            if (code.equals("s_ok")) {
                //请求成功
                closeProgressDialog();
                ToastUtils.showToast(getString(R.string.modify_pwd_succ));
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");//返回的信息
                closeProgressDialog();
                ToastUtils.showToast(returnMsg);

                return;
            }
        }
    }

    private boolean LocalThrough(String oldPassWord, String newPassWord) {
        if (TextUtils.isEmpty(oldPassWord)) {
            ToastUtils.showToast(R.string.password_isempty);
            return true;
        } else if (oldPassWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return true;
        } else if (TextUtils.isEmpty(newPassWord)) {
            ToastUtils.showToast(R.string.password_isempty);
            return true;
        } else if (newPassWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return true;
        } else if (oldPassWord.equals(newPassWord)) {
            ToastUtils.showToast(R.string.password_issame);
            return true;
        }
        return false;
    }
}
