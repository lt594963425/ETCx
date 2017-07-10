package com.etcxc.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.etcxc.android.base.App;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import static com.etcxc.android.base.App.WX_APP_ID;

/**
 * Created by ${caoyu} on 2017/7/8.
 */

public class WXShareUtils {

    /**
     * 分享文字
     *
     * @param shareContent 分享内容
     * @param type         true:朋友 false:朋友圈
     */
    public static void shareText(Context context,String shareContent, boolean type) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, WX_APP_ID);
        iwxapi.registerApp(WX_APP_ID);
        if (!iwxapi.isWXAppInstalled()){
            ToastUtils.showToast("您尚未安装微信客户端");
            return;
        }

        WXTextObject textObj = new WXTextObject();
        textObj.text = shareContent;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Title";
        msg.description = shareContent;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "text"; // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = type ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        iwxapi.sendReq(req);
    }

    /**
     * 构建一个唯一标志
     *
     * @param type 分享的类型分字符串
     * @return 返回唯一字符串
     */
    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
