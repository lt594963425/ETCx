package com.etcxc.android.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 调用本地地图app
 * Created by caoyu on 2017/7/27.
 */

public class OpenExternalMapAppUtils {
    /**
     * 调起百度客户端 自定义打点
     * @param activity
     * @param content 目的地
     * mode 导航方式
     */
    public static void openBaiduMarkerMap(Context activity, String content) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?region=&origin=&destination="+content+"&mode=driving"));
        activity.startActivity(intent);
    }

    /**
     * 调起百度客户端 路径规划
     * lat,lng (先纬度，后经度)
     * 40.057406655722,116.2964407172
     * lat,lng,lat,lng (先纬度，后经度, 先左下,后右上)
     * @param activity
     */
    public static void openBaiduiDrectionMap(Context activity, String sLongitude, String sLatitude, String sName,
                                             String dLongitude, String dLatitude, String dName) {
        Intent intent = new Intent("android.intent.action.VIEW",
                android.net.Uri.parse("baidumap://map/direction?origin=name:" +
                        sName + "|latlng:" + sLatitude + "," + sLongitude + "&destination=name:" +
                        dName + "|latlng:" + dLatitude + "," + dLongitude + "&" +
                        "mode=transit&sy=0&index=0&target=0"));
        activity.startActivity(intent);
    }

    /**
     * 启动高德App进行导航
     * @param poiname 非必填 POI 名称
     * @param lat 必填 纬度
     * @param lon 必填 经度
     * @param dev 必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
     * @param style 必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
     */
    public static void goToNaviActivity(Context context, String poiname , Double lat , Double lon , String dev , String style){
        StringBuffer stringBuffer  = new StringBuffer("androidamap://navi?sourceApplication=")
                .append("");
        if (!TextUtils.isEmpty(poiname)){
            stringBuffer.append("&poiname=").append(poiname);
        }
        stringBuffer
                .append("&lat=").append(lat)
                .append("&lon=").append(lon)
                .append("&destination=").append(context)
                .append("&dev=").append(dev)
                .append("&style=").append(style);

        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }


    /**
     * 打开百度网页版 导航
     * @param activity
     */
    public static void openBrosserNaviMap(Context activity, Location location,String content) {
        Uri webpage = Uri.parse("http://api.map.baidu.com/marker?location="+
                location.getLatitude() +","+ location.getLongitude()+
                "&title="+content+
                "&content="+content+
                "&output=html");
        Uri mapUri = Uri.parse("http://api.map.baidu.com/direction?origin=latlng:" +
                location.getLatitude() +","+ location.getLongitude()+ "|name:" + content + "&destination=latlng:" +
                "|name:" + content + "&mode=driving&region="+
                "&output=html");
        Log.d("百度地图", "openBrosserNaviMap: "+webpage.toString());

        Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);
        activity.startActivity(webIntent);
    }

    //判断是否安装目标应用
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName)
                .exists();
    }

}
