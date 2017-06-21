package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.SelectRegionAdapter;
import com.etcxc.android.ui.view.XRecyclerView;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 选择地区，街道都用这个，只是数据源于返回的结果有区别
 * Created by xwpeng on 2017/6/21.
 */

public class SelectRegionActivity extends BaseActivity implements SelectRegionAdapter.CallBack {
    private final static String TAG = SelectRegionActivity.class.getSimpleName();
    private XRecyclerView mContentView;
    private SelectRegionAdapter mAdapter;
    private List<String> mDatas = new ArrayList<>();
    private String mResult;
    private boolean isStreetSelect;//街道选择还是地区选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);
        initView();
        net();
    }

    private void net() {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> e) throws Exception {
                List<String> temp = new ArrayList<String>();
                temp.add("湖南");
                temp.add("湖北");
                temp.add("山西");
                temp.add("山东");
                e.onNext(temp);
                e.onComplete();
                String result = OkClient.get("http://192.169.6.119/transaction/transaction/areaprovince/", new JSONObject());
                if (!TextUtils.isEmpty(result)) {
                    e.onError(new Exception());
                }
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .doOnNext(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> strings) throws Exception {
                        if (strings != null && strings.size() > 0) {
                            mDatas.clear();
                            mDatas.addAll(strings);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(getString(R.string.request_failed));
                        LogUtil.e(TAG, "net", throwable);
                    }
                })
                .subscribe()
        ;
    }


    private void initView() {
        setTitle(isStreetSelect ? R.string.select_street : R.string.select_region);
        mContentView = find(R.id.content_recyclerview);
        mContentView.setDivider();
        mAdapter = new SelectRegionAdapter(mDatas, this);
        mContentView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(String content) {

    }
}
