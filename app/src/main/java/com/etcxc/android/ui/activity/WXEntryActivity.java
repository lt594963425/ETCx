package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.etcxc.android.base.App;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PrefUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.path;
import static com.etcxc.android.base.App.WX_APP_ID;
import static com.etcxc.android.base.App.WX_APP_SECRET;
import static com.etcxc.android.base.App.userTag;

/**
 * Created by 刘涛 on 2017/6/5 0005.
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private BaseResp resp = null;
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 获取用户个人信息
    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
    private final OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        //将你收到的intent和实现IWXAPIEventHandler接口的对象传递给handleIntent方法
        App.WXapi.handleIntent(this.getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // finish();

    }

    /**
     * 点击确认会回调的方法
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     *
     * @param resp
     */

    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                if (sendResp != null) {
                    String code = sendResp.token;
                    getAccess_token(code);
                }
                userTag = true;
                PrefUtils.setBoolean(App.getContext(), "userTag", userTag);
                finish();

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                //finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                //finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                //finish();
                break;
        }
        finish();
    }

    /**
     * 获取openid accessToken值用于后期操作
     *
     * @param code 请求码
     */
    private void getAccess_token(final String code) {
        //用户信息
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + WX_APP_ID
                + "&secret="
                + WX_APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        run(url);
    }

    public void run(String url) {
        final Request request = new Request.Builder()
                //.url("http://publicobject.com/helloworld.txt")
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) ;

                try {
                    String responseStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseStr);
                    String openid = jsonObject.getString("openid").toString().trim();
                    String access_token = jsonObject.getString("access_token").toString().trim();
                    getUserMesg(access_token, openid);

                    //                 String openid = jsonObject.getString("openid").toString().trim();
//                  String access_token = jsonObject.getString("access_token").toString().trim();
//                  LogUtil.i(TAG, "getUserMesg 拿到了用户Wx基本信息.. nickname:" + openid + "asdasdasdadadadad测试2222" + access_token);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 获取微信的个人信息
     *
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) {
        JSONObject jsonObject = null;
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        LogUtil.i(TAG, "getUserMesg:" + path);
        try {
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseStr = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        String nickname = jsonObject.getString("nickname");//名字
                        int sex = Integer.parseInt(jsonObject.get("sex").toString());//性别
                        String headimgurl = jsonObject.getString("headimgurl");  //头像
                        String openid = jsonObject.getString("openid");
                        String unionid = jsonObject.getString("openid");
                        PrefUtils.setString(App.getContext(), "openid", openid);
                        PrefUtils.setString(App.getContext(), "username", nickname);
                        PrefUtils.setInt(App.getContext(), "usersex", sex);
                        PrefUtils.setString(App.getContext(), "headurl", headimgurl);
                        PrefUtils.setString(App.getContext(), "unionid", unionid);
                        LogUtil.i(TAG, "getUserMesg 拿到了用户Wx基本信息：");
                        LogUtil.i(TAG, "名字.. nickname:" + nickname);
                        LogUtil.i(TAG, "性别.. nickname:" + sex);
                        LogUtil.i(TAG, "头像:" + headimgurl);
                        //todo：不使用eventbus
//                        EventBus.getDefault().post(new MessageEvent(nickname));
                        //拿到这些信息后进行相应的业务操作，提交服务器
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        App.WXapi.handleIntent(intent, this);
        finish();
    }


}
