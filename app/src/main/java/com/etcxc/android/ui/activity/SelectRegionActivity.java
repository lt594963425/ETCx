package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.AddressBean;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.SelectRegionAdapter;
import com.etcxc.android.ui.view.XRecyclerView;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.AREAPROVINCE;
import static com.etcxc.android.net.FUNC.CITY;
import static com.etcxc.android.net.FUNC.COUNTY;
import static com.etcxc.android.net.FUNC.STREET;

/**
 * 选择地区，街道都用这个，只是数据源于返回的结果有区别
 * Created by xwpeng on 2017/6/21.
 */

public class SelectRegionActivity extends BaseActivity implements SelectRegionAdapter.CallBack {
    private final static String TAG = SelectRegionActivity.class.getSimpleName();
    private XRecyclerView mContentView;
    private SelectRegionAdapter mAdapter;
    private List<AddressBean> mDatas = new ArrayList<>();
    private List<AddressBean> mResult = new ArrayList<>();

    private boolean mIsRegionSelect;//街道选择还是地区选择
    private List<AddressBean> mPrivence = new ArrayList<>();
    private List<AddressBean> mCity = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);
        String countyCode = getIntent().getStringExtra("countyCode");
        mIsRegionSelect = TextUtils.isEmpty(countyCode);
        Log.e(TAG, "mIsRegionSelect:" + mIsRegionSelect + ",countyCode:" + countyCode);
        initView();
        //如果county == null对应省的接口
        if (mIsRegionSelect) {
            net(AREAPROVINCE, null);
        } else
            net(STREET, countyCode);
    }

    private List<AddressBean> parseRegion(String result) throws Exception {
        if (TextUtils.isEmpty(result)) return null;
        Gson gson = new Gson();
        JSONObject j = new JSONObject(result);
        if ("s_ok".equals(j.getString("code"))) {
            JSONArray array = j.getJSONArray("var");
            //List<AddressBean> temp = new ArrayList<AddressBean>();
            List<AddressBean> temp = gson.fromJson(array.toString(), new TypeToken<List<AddressBean>>() {
            }.getType());
            return temp;
        } else {
            String errMsg = j.getString("message");
            throw new Exception(errMsg);
        }
    }

    private void net(String url, String code) {
        Observable.create(new ObservableOnSubscribe<List<AddressBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<AddressBean>> e) throws Exception {
                JSONObject pamares = new JSONObject();
                pamares.put("code", code);
                List<AddressBean> temp;
                String result = OkClient.get(NetConfig.consistUrl(url), code != null?pamares:null);
                Log.e(TAG, "result:" + result);
                temp = parseRegion(result);
                if (temp == null) e.onError(new Exception());
                else e.onNext(temp);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<List<AddressBean>>() {
                    @Override
                    public void accept(@NonNull List<AddressBean> datas) throws Exception {
                        if (datas == null || datas.size() < 1) return;
                        if (mResult.size() == 0) {
                            mPrivence.addAll(datas);
                        } else if (mResult.size() == 1) {
                            mCity.clear();
                            mCity.addAll(datas);
                        }
                        refresUi(datas);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(getString(R.string.request_failed));
                        LogUtil.e(TAG, "net", throwable);
                    }
                });
    }

    private void refresUi(List<AddressBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        setTitle(mIsRegionSelect ? R.string.select_region : R.string.select_street);
        mContentView = find(R.id.content_recyclerview);
        mContentView.setDivider();
        mAdapter = new SelectRegionAdapter(mDatas, this);
        mContentView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(String code, String content) {
        if (!mIsRegionSelect) {
            Intent intent = new Intent();
            intent.putExtra("street", content);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        AddressBean address = new AddressBean();
        address.setName(content);
        address.setCode(code);
        mResult.add(address);

        switch (mResult.size()) {
            case 1:
                net(CITY, mResult.get(0).getCode());
                break;
            case 2:
                net(COUNTY, mResult.get(1).getCode());
                break;
            case 3:
                String s = mResult.get(0).getName() + " " + mResult.get(1).getName() + " " + mResult.get(2).getName();
                Intent intent = new Intent();
                intent.putExtra("region", s);
                intent.putExtra("countyCode", mResult.get(2).getCode());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mIsRegionSelect) {
            int size = mResult.size();
            if (size == 1) {
                mResult.remove(0);
                refresUi(mPrivence);
                return;
            } else if (size == 2) {
                mResult.remove(1);
                refresUi(mCity);
                return;
            }
        }
        super.onBackPressed();
    }
}


