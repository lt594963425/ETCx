package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.FUNC_POSTADDRESS;

/**
 * 收货地址
 * Created by xwpeng on 2017/6/20.
 */

public class PostAddressActivity extends BaseActivity implements View.OnClickListener {
    private TextView mRegionResultTextView, mStreetResultTextView;
    public final static int REQUEST_REGION = 1;
    public final static int REQUEST_STREET = 2;

    private EditText mReceiverEdit, mPhoneNumberEdit, mDetailAddressEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_address);
        setTitle(R.string.edit_address);
        initView();
    }

    private void initView() {
        mRegionResultTextView =find(R.id.post_address_region_result);
        mStreetResultTextView =find(R.id.post_address_street_result);
        mReceiverEdit = find(R.id.receiver_edittext);
        mPhoneNumberEdit = find(R.id.post_address_phone_edittext);
        mDetailAddressEdit = find(R.id.detailedly_address);
        UIUtils.addIcon(mRegionResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        UIUtils.addIcon(mStreetResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        find(R.id.post_address_street_layout).setOnClickListener(this);
        find(R.id.post_address_region_layout).setOnClickListener(this);
        find(R.id.commit_button).setOnClickListener(this);
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
                if(checkReceiver(receiver)
                        && checkPhoneNumber(phoneNumber)
                        && checkRegion(region)
                        && checkStreet(street)
                        && checkDetailAddress(detailaddress)
                        ) {
                    Map<String, String> params = new HashMap<>();
                    params.put("receiver", receiver);
                    params.put("mail_tel", phoneNumber);
                    params.put("area_province", regions[0]);
                    params.put("area_city", regions[1]);
                    params.put("area_county", regions[2]);
                    params.put("area_street", street);
                    params.put("address", detailaddress);
                    params.put("veh_plate_code", PublicSPUtil.getInstance().getString("carCard", ""));
                    params.put("veh_plate_colour", PublicSPUtil.getInstance().getString("carCardColor", ""));
                    commitNet(params);
                }

                break;
            case R.id.post_address_region_layout:
                startActivityForResult(new Intent(this, SelectRegionActivity.class), REQUEST_REGION);
                break;
            case R.id.post_address_street_layout:
                String county = mRegionResultTextView.getText().toString();
                if (TextUtils.isEmpty(county)) {
                    ToastUtils.showToast(R.string.please_select_region_before);
                    return;
                }
                county = county.split(" ")[2];
                Intent intent = new Intent(this, SelectRegionActivity.class);
                intent.putExtra("county", county);
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
                    break;
                case REQUEST_STREET:
                    mStreetResultTextView.setText(data.getStringExtra("street"));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void commitNet(Map<String, String> params) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(NetConfig.consistUrl(FUNC_POSTADDRESS, params), new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code))
                            startActivity(new Intent(PostAddressActivity.this, IssuePayActivity.class));
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
