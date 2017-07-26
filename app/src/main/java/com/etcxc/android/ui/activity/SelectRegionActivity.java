package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.SelectRegionAdapter;
import com.etcxc.android.ui.view.XRecyclerView;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.NetConfig.HOST;

/**
 * 选择地区，街道都用这个，只是数据源于返回的结果有区别
 * Created by xwpeng on 2017/6/21.
 */

public class SelectRegionActivity extends BaseActivity implements SelectRegionAdapter.CallBack {
    private final static String TAG = SelectRegionActivity.class.getSimpleName();
    private XRecyclerView mContentView;
    private SelectRegionAdapter mAdapter;
    private List<String> mDatas = new ArrayList<>();
    private List<String> mResult = new ArrayList<>();
    private boolean mIsRegionSelect;//街道选择还是地区选择
    private List<String> mPrivence = new ArrayList<>();
    private List<String> mCity = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);
        String county = getIntent().getStringExtra("county");
        mIsRegionSelect = TextUtils.isEmpty(county);
        initView();
        net(HOST + (mIsRegionSelect
                ?  "/transaction/transaction/areaprovince/"
                : "/transaction/transaction/areastreet/county/") + county);
    }

    private List<String> parseRegion(String result) throws Exception {
        if (TextUtils.isEmpty(result)) return null;
        JSONObject j = new JSONObject(result);
        if ("s_ok".equals(j.getString("code"))) {
            JSONArray array = j.getJSONArray("var");
            if (array != null && array.length() > 0) {
                List<String> temp = new ArrayList<String>();
                for (int i = 0; i < array.length(); i++) temp.add(array.getString(i));
                return temp;
            }
        } else {
            String errMsg = j.getString("message");
            throw new Exception(errMsg);
        }
        return null;
    }

    private void net(String url) {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> e) throws Exception {
                List<String> temp;
                String result = OkClient.get(url, new JSONObject());
                temp = parseRegion(result);
                if (temp == null) e.onError(new Exception());
                else e.onNext(temp);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> datas) throws Exception {
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

    private void refresUi(List<String> datas) {
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
    public void onItemClick(String content) {
        if (!mIsRegionSelect) {
            Intent intent = new Intent();
            intent.putExtra("street", content);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        mResult.add(content);
        switch (mResult.size()) {
            case 1:
                net(HOST + "/transaction/transaction/areacity/province/" + content);
                break;
            case 2:
                net(HOST + "/transaction/transaction/areacounty/city/" + content);
                break;
            case 3:
                String s = mResult.get(0) + " " + mResult.get(1) + " " + mResult.get(2);
                Intent intent = new Intent();
                intent.putExtra("region", s);
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


