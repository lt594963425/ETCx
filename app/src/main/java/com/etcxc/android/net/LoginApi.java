package com.etcxc.android.net;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.etcxc.android.net.NetConfig.consistUrl;

/**
 * Created by LiuTao on 2017/8/19 0019.
 */

public class LoginApi {

    public static void loginRun(JSONObject jsonObject) {
        LogUtil.e(TAG, jsonObject.toString());
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(consistUrl(FUNC.LOGIN_PWD), jsonObject);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {

                        LogUtil.e(TAG, s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String code = jsonObject.getString("code");
                            if ("s_ok".equals(code)) {

                                successResult(jsonObject);
                            }
                            if (code.equals("error")) {
                                errorResult(jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.login_failed);
                        LogUtil.e(TAG, "++++++++++++++登录失败+2++++++++++++++", throwable);

                    }
                });

    }

    private static void successResult(JSONObject jsonObject) throws JSONException {
        LogUtil.e(TAG, "登录成功" + jsonObject);
        JSONObject varJson = jsonObject.getJSONObject("var");
        String token = varJson.getString("token");
        JSONObject userJson = varJson.getJSONObject("user");

        String uid = userJson.getString("uid");
        String nickName = userJson.getString("nick_name");
        String tel = userJson.getString("tel");
        String pwd = userJson.getString("pwd");

        //String head_image = varJson.getString("head_image");
        PublicSPUtil.getInstance().putString("uid", uid);
        PublicSPUtil.getInstance().putString("token", token);
        PublicSPUtil.getInstance().putString("nickname", nickName);
        LogUtil.e(TAG, nickName + ":" + nickName + ":" + token);
        EventBus.getDefault().post(new MessageEvent(nickName));
        MeManager.setUid(uid);   //todo
        MeManager.setPhone(tel);
        MeManager.setName(nickName);
        MeManager.setToken(token);
        MeManager.setIsLgon(true);
        MeManager.setPWD(pwd);
        App.onProfileSignIn("mLoginPhone");//帐号登录统计

    }

    private static void errorResult(JSONObject jsonObject) throws JSONException {
        LogUtil.e(TAG, "++++++++++++++登录失败++3+++++++++++++");
        String returnMsg = jsonObject.getString("message");//返回的信息
        ToastUtils.showToast(R.string.login_failed);
        MeManager.clearAll();
    }
}
