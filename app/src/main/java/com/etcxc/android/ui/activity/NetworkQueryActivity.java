package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.Networkstore;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.ui.adapter.NetworkQueryAdapter;
import com.etcxc.android.ui.view.XRecyclerView;
import com.etcxc.android.utils.DistanceLowToHighComparator;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.OpenExternalMapAppUtils;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.NETWORK;


/**
 * 网点查询
 * Created by caoyu on 2017/7/26
 */
public class NetworkQueryActivity extends BaseActivity {

    private XRecyclerView mXrecycler;
    private List<Networkstore.VarBean> mData;
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
        initData();
    }

    private void initData() {
        showProgressDialog(getString(R.string.loading));
        getLocation(this);
        if (NetConfig.isAvailable()) {//网络是否可用
            getNetwork();
        } else {
            try {
                parseResultJson(FileUtils.readJson(this, docCache));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        setTitle(getString(R.string.website_check));
        mXrecycler = (XRecyclerView) findViewById(R.id.xrecycler);
    }

    //初始化数据
    private void initData(List<Networkstore.VarBean> jsonArray) {
        mData = new ArrayList<>();
        mData = jsonArray;
        initDistance(mData);
    }

    /**
     * 获取网点地址
     */
    private void getNetwork() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(NetConfig.HOST + NETWORK,new JSONArray()));
//                e.onNext(OkHttpUtils
//                        .get()
//                        .url(NetConfig.HOST + NETWORK)
//                        .build()
//                        .execute().body().string());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                closeProgressDialog();
                parseResultJson(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                closeProgressDialog();
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
        Log.d(TAG, "parseResultJson: "+s);
        Gson gson = new Gson();
        Networkstore networkstore = gson.fromJson(s, Networkstore.class);
        if ("s_ok".equals(networkstore.getCode())) {
            if (networkstore.getVar() != null && networkstore.getVar().size() > 0) {
                initData(networkstore.getVar());
                //判断外部SD卡是否存在，true是存在
                if (PermissionUtil.hasWritePermission(this)) {
                    FileUtils.writeJson(this, s, docCache, false);
                } else {
                    ToastUtils.showToast("没权限");
                }

            } else {
                ToastUtils.showToast("没有网点信息");
            }
        } else {
            closeProgressDialog();
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
            dialog.dismiss();
            closeProgressDialog();
            openGPSDlg();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void openGPSDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请开启网络定位或者GPS定位");
        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //返回开启GPS导航设置界面
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                initData();
                break;
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

    private List<Networkstore.VarBean> getList(){
        for (int i = 0; i < mData.size(); i++) {
            Double d = OpenExternalMapAppUtils.DistanceOfTwoPoints(
                    mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    mData.get(i).getLatitude(),
                    mData.get(i).getLongitude());
            Log.d(TAG, "run: " + d);
            mData.get(i).setDistance(d);
        }

        DistanceLowToHighComparator comparator = new DistanceLowToHighComparator();
        Collections.sort(mData, comparator);
        return mData;
    }

    private void initDistance(List<Networkstore.VarBean> mData) {
        if (mData != null && mData.size() > 0 && mLocation != null) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    e.onNext(getList());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            closeProgressDialog();
                            mXrecycler.setDefaultLayoutManager();
                            mXrecycler.setDivider(R.color.bg_gray, 10);
                            mAdapter = new NetworkQueryAdapter(mData, NetworkQueryActivity.this, mLocation);
                            mXrecycler.setAdapter(mAdapter);
                        }
                    });
        } else {
            closeProgressDialog(   );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener = null;
        }
    }
}
