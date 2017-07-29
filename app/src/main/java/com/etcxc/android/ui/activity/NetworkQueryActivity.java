package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.Api;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.NetworkQueryAdapter;
import com.etcxc.android.ui.view.XRecyclerView;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 网点查询
 * Created by caoyu on 2017/7/26
 */
public class NetworkQueryActivity extends BaseActivity {

    private XRecyclerView mXrecycler;
    private JSONArray mData;
    private NetworkQueryAdapter mAdapter;
    //定义缓存文件的名字，方便外部调用
    public static final String docCache = "xczx_netstore_cache.txt";//缓存文件

    private LocationManager locationManager;
    private String locationProvider;//位置提供器
    private Location mLocation;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_query);
        initView();
        getLocation(this);
        if (NetConfig.isAvailable()) {//网络是否可用
            getNetwork(Api.networkUrl);
        } else {
            try {
                parseResultJson(FileUtils.readJson(this, docCache));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void initView() {
        setTitle(getString(R.string.gridchek));
        mXrecycler = (XRecyclerView) findViewById(R.id.xrecycler);
    }


    //初始化数据
    private void initData(JSONArray jsonArray) {
        mData = new JSONArray();
        mData = jsonArray;
        mXrecycler.setDefaultLayoutManager();
        mXrecycler.setDivider(R.color.bg_gray, 10);
        mAdapter = new NetworkQueryAdapter(mData, this, mLocation);
        mXrecycler.setAdapter(mAdapter);
    }

    /**
     * 获取网点地址
     *
     * @param url
     */
    private void getNetwork(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(url, new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                parseResultJson(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
            }
        });
    }

    /**
     * 解析数据
     *
     * @param s
     */
    private void parseResultJson(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject == null) return;
        String code = jsonObject.optString("code");
        if (code.equals("s_ok")) {
            if (jsonObject.optJSONArray("var") != null && jsonObject.optJSONArray("var").length() > 0) {
                initData(jsonObject.optJSONArray("var"));
                //判断外部SD卡是否存在，true是存在
                if (PermissionUtil.hasWritePermission(this)) {
                    FileUtils.writeJson(this, jsonObject.toString(), docCache, false);
                } else {
                    ToastUtils.showToast("没权限");
                }

            } else {
                ToastUtils.showToast("没有网点信息");
            }
        }
    }


    private void getLocation(Context context) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在定位...");
        dialog.setCancelable(false);
        dialog.show();
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
            dialog.dismiss();
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
            dialog.dismiss();
        } else {
            ToastUtils.showToast("没有可用的位置提供器");
            dialog.dismiss();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            dialog.dismiss();
            return;
        }
        mLocation = locationManager.getLastKnownLocation(locationProvider);
        if (mLocation != null) {
            Log.d(TAG, "onLocationChanged: " + mLocation);
            dialog.dismiss();
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 0, 0, mListener);
        }
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.d(TAG, "onLocationChanged: " + address);
            dialog.dismiss();
        }
    };
}
