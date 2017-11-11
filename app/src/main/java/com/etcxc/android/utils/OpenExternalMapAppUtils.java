package com.etcxc.android.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.net.URISyntaxException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 调用本地地图app
 * Created by caoyu on 2017/7/27.
 */

public class OpenExternalMapAppUtils {
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 调起百度客户端 自定义打点
     *
     * @param activity
     * @param content  目的地
     *                 mode 导航方式
     */
    public static void openBaiduMarkerMap(Context activity, String content) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?region=&origin=&destination=" + content + "&mode=driving"));
        activity.startActivity(intent);
    }

    /**
     * 启动高德App进行导航
     */
    public static void openNaviActivity(Context context, String mDestination) {
        try {
            Intent intent = Intent.getIntent("androidamap://route?sourceApplication=softname" + "&sname=我的位置&dname=" + mDestination + "&dev=0&m=0&t=1");
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.autonavi.minimap");// pkg=com.autonavi.minimap
            intent.addCategory("android.intent.category.DEFAULT");
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开网页版 导航
     *
     * @param activity
     * @param sLocation 起点经纬度
     * @param sName     起点名字
     * @param dName     目的地名字
     */
    public static void openBrosserNaviMap(Context activity, Location sLocation,
                                          String sName, String dName) {
        Uri mapUri;
        if (sLocation != null) {
            mapUri = Uri.parse("http://api.map.baidu.com/direction?origin=latlng:" +
                    sLocation.getLatitude() + "," + sLocation.getLongitude() + "|name:" + sName + "&destination=latlng:" + "|name:" + dName + "&mode=driving&region=" +
                    "&output=html&src=迅畅在线");
        } else {
            mapUri = Uri.parse("http://api.map.baidu.com/direction?origin=" + sName + "&destination=" + dName + "&mode=driving&region=" +
                    "&output=html&src=迅畅在线");
        }

        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        activity.startActivity(loction);
    }

    //判断是否安装目标应用
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName)
                .exists();
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离：单位为米
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10;
        Log.i("距离", s + "");
        return s;
    }

    /**
     * 单位换算
     *
     * @param s 米
     * @return
     */
    public static String unitConversion(double s) {
        String distance = "";
        if (s > 0 && s < 1000) {//大于0并小于1000米，返回米
            distance = s + "米";
        } else if (s > 1000) {//大于1000米，返回公里
            distance = s / 1000 + "公里";
        }
        return distance;
    }
}
