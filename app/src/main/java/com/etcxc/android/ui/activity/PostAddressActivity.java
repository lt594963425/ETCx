package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.POST_INFO;

/**
 * 收货地址
 * Created by xwpeng on 2017/6/20.
 */

public class PostAddressActivity extends BaseActivity implements View.OnClickListener {
    private TextView mRegionResultTextView, mStreetResultTextView;
    public final static int REQUEST_REGION = 1;
    public final static int REQUEST_STREET = 2;
    private EditText mReceiverEdit, mPhoneNumberEdit, mDetailAddressEdit;
    private String countyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_address);
        setTitle(R.string.edit_address);
        initView();
    }

    private void initView() {
        mRegionResultTextView = find(R.id.post_address_region_result);
        mStreetResultTextView = find(R.id.post_address_street_result);
        mReceiverEdit = find(R.id.receiver_edittext);
        mPhoneNumberEdit = find(R.id.post_address_phone_edittext);
        mDetailAddressEdit = find(R.id.detailedly_address);
        UIUtils.addIcon(mRegionResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        UIUtils.addIcon(mStreetResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        find(R.id.post_address_street_layout).setOnClickListener(this);
        find(R.id.post_address_region_layout).setOnClickListener(this);
        find(R.id.commit_button).setOnClickListener(this);
        if (MeManager.getIsLogin()) {
            mReceiverEdit.setText(MeManager.getName());
            mPhoneNumberEdit.setText(MeManager.getPhone());
        }
    }

    private boolean checkReceiver(String receiver) {
        if (TextUtils.isEmpty(receiver)) {
            ToastUtils.showToast(getString(R.string.receiver_notallow_empty));
            return false;
        }
        int length = receiver.length();
        if (length < 2 || length > 15) {
            ToastUtils.showToast(R.string.receiver_length_limit);
            return false;
        }
        return true;
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showToast(getString(R.string.receiver_phone_notallow_empty));
            return false;
        }
        Matcher m = SystemUtil.phonePattern.matcher(phoneNumber);
        if (m.matches()) {
            return true;
        } else ToastUtils.showToast(R.string.please_input_correct_phone_number);
        return false;
    }

    private boolean checkRegion(String region) {
        if (TextUtils.isEmpty(region)) {
            ToastUtils.showToast(getString(R.string.please_select_region));
            return false;
        }
        return true;
    }

    private boolean checkStreet(String street) {
        if (TextUtils.isEmpty(street)) {
            ToastUtils.showToast(getString(R.string.please_select_street));
            return false;
        }
        return true;
    }

    private boolean checkDetailAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showToast(getString(R.string.please_input_detail_address));
            return false;
        }
        int length = address.length();
        if (length < 5 || length > 60) {
            ToastUtils.showToast(R.string.detail_address_length_limit);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                String receiver = mReceiverEdit.getText().toString(),
                        phoneNumber = mPhoneNumberEdit.getText().toString(),
                        region = mRegionResultTextView.getText().toString(),
                        street = mStreetResultTextView.getText().toString(),
                        detailaddress = mDetailAddressEdit.getText().toString();
                String[] regions = region.split(" ");
                if (checkReceiver(receiver)
                        && checkPhoneNumber(phoneNumber)
                        && checkRegion(region)
                        && checkStreet(street)
                        && checkDetailAddress(detailaddress)
                        ) {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("licensePlate", PublicSPUtil.getInstance().getString("carCard", ""))
                                .put("plateColor", PublicSPUtil.getInstance().getString("carCardColor", ""))
                                .put("receiver", receiver)
                                .put("tel", phoneNumber)
                                .put("province", regions[0])
                                .put("city", regions[1])
                                .put("county", regions[2])
                                .put("street", street)
                                .put("detail", detailaddress);
                        Log.e(TAG,jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commitNet(jsonObject);
                }

                break;
            case R.id.post_address_region_layout: //地区选择

                startActivityForResult(new Intent(this, SelectRegionActivity.class), REQUEST_REGION);
                break;
            case R.id.post_address_street_layout:  //街道选择
                String county = mRegionResultTextView.getText().toString();
                if (TextUtils.isEmpty(county)) {
                    ToastUtils.showToast(R.string.please_select_region_before);
                    return;
                }
                Intent intent = new Intent(this, SelectRegionActivity.class);
                intent.putExtra("countyCode", countyCode);
                Log.e(TAG, "countyCode:" + countyCode);
                startActivityForResult(intent, REQUEST_STREET);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_REGION:
                    mRegionResultTextView.setText(data.getStringExtra("region"));
                    countyCode = data.getStringExtra("countyCode");
                    break;
                case REQUEST_STREET:
                    mStreetResultTextView.setText(data.getStringExtra("street"));

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void commitNet(JSONObject jsonObject) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(NetConfig.consistUrl(POST_INFO), jsonObject));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code))
                            openActivity(IssuePayActivity.class);
                        else ToastUtils.showToast(R.string.request_failed);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "net", throwable);
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
    }
}
