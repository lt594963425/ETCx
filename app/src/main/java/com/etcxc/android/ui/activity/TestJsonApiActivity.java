package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 给后端做json接口测试
 * Created by xwpeng on 2017/7/24.
 */

public class TestJsonApiActivity extends BaseActivity implements View.OnClickListener {
    private EditText mUrlEdit,mJsonEdit;
    private TextView mResultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_json_api);
        initView();
    }

    private void  initView() {
        mUrlEdit = find(R.id.json_api_test_url_edt);
        mUrlEdit.setText("http://192.168.6.58/xczx/pay/text");
        mJsonEdit = find(R.id.json_api_test_json_edt);
        mJsonEdit.setText("{\"tel\":\"13739085585\"}");
        mResultTextView = find(R.id.json_api_test_result_tv);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                testApi();
                break;
        }

    }

    /**
     * json提交测试
     */
    private void testApi() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject(mJsonEdit.getText().toString());
//                jsonObject.put("me", "xwpeng");
                e.onNext(OkHttpUtils
                        .postString()
                        .url(mUrlEdit.getText().toString())
                        .content(String.valueOf(jsonObject))
                        .mediaType(NetConfig.JSON)
                        .build()
                        .execute()
                        .body().string());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
               mResultTextView.setText(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
                mResultTextView.setText("错误：" + throwable.getMessage());
            }
        });
    }


}
