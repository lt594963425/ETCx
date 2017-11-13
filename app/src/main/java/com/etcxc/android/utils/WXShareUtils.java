package com.etcxc.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.etcxc.android.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.base.Constants.WX_APP_ID;


/**
 * 微信分享
 * Created by caoyu on 2017/7/8.
 */

public class WXShareUtils extends Activity {
    private IWXAPI WXapi;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
    }

    SendMessageToWX.Req req;

    private static WXShareUtils instance = null;

    public static synchronized WXShareUtils getInstance() {
        if (instance == null) {
            instance = new WXShareUtils();
        }
        return instance;
    }

    /**
     * 微信文本分享
     *
     * @param mContext
     * @param text        内容
     * @param description 消息描述
     * @param tag         0:好友 1:朋友圈
     */
    public void doTextSend(Context mContext, String text, String description, int tag) {
        //初始化一个微信WXWebpageObject对象，填写url
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题，描述
        WXMediaMessage msg = new WXMediaMessage(textObject);
        //此处填写消息标题
        msg.mediaObject = textObject;
        //此处填写消息描述
        msg.description = buildTransaction(description);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        msg.setThumbImage(bitmap);
        //构造一个Req
        req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        if (tag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (tag == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        WXapi.sendReq(req);
    }

    /**
     * 微信url分享
     *
     * @param mContext
     */
    public void doUrlSend(Context mContext, String url, String title, String description, int tag) {
        //初始化一个微信WXWebpageObject对象，填写url
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        //此处填写消息标题
        msg.title = title;
        //此处填写消息描述
        msg.description = description;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        msg.setThumbImage(bitmap);
        //构造一个Req
        req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (tag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (tag == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        WXapi.sendReq(req);

    }

    /**
     * 微信url分享带图片
     *
     * @param mContext
     */
    public void shareUrlSend(Context mContext, String url, String title, String description, Bitmap bitmap, int tag) {
        //初始化一个微信WXWebpageObject对象，填写url
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        //此处填写消息标题
        msg.title = title;
        //此处填写消息描述
        msg.description = description;
        Bitmap img = bitmap;
        msg.setThumbImage(img);
        //构造一个Req
        req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (tag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (tag == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        WXapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}