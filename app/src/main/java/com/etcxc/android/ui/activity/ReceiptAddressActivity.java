package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.FIND_POSTADDRESS;
import static com.etcxc.android.net.FUNC.RECEIPT_POSTADDRESS;
import static com.etcxc.android.net.NetConfig.JSON;

/**
 * 我的收货地址
 * Created by xwpeng on 2017/7/24.
 */

public class ReceiptAddressActivity extends BaseActivity implements View.OnClickListener {

    private TextView mRegionResultTextView, mStreetResultTextView;
    public final static int REQUEST_REGION = 1;
    public final static int REQUEST_STREET = 2;

    private EditText mReceiverEdit, mPhoneNumberEdit, mDetailAddressEdit;

    private String mReceiver, mPhoneNumber, mRegion, mStreet, mDetailaddress,mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_address);
        setTitle(getString(R.string.my_receipt_address));
        initView();
        //findAddress();
    }

    /**
     * 查询用户收货地址
     */
    private void findAddress() {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tel",MeManager.getUid());
                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + FIND_POSTADDRESS)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.optString("code");
                        if ("s_ok".equals(code)) {
                            setForm(jsonObject);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "net", throwable);
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_faileds);
                    }
                });
    }

    /**
     * 数据解析赋值
     * @param jsonObject
     * @throws JSONException
     */
    private void setForm(JSONObject jsonObject) throws JSONException {
        JSONArray ja = jsonObject.optJSONArray("var");
        if (ja == null && ja.length() < 1) return;
        jsonObject = ja.getJSONObject(0);
        mReceiver = jsonObject.optString("receiver");//收件人
        mId = jsonObject.optString("id");//收件人
        String area_province = jsonObject.getString("area_province");//省
        String area_city = jsonObject.getString("area_city");//市
        String area_county = jsonObject.getString("area_county");//区，县
        mStreet = jsonObject.getString("area_street");//街道
        mDetailaddress = jsonObject.getString("address");//详细地址
        mPhoneNumber = jsonObject.getString("mail_tel");//收件人联系电话
        mReceiverEdit.setText(mReceiver);
        mPhoneNumberEdit.setText(mPhoneNumber);
        mDetailAddressEdit.setText(mDetailaddress);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(area_province).append(" ").append(area_city).append(" ").append(area_county);
        mRegion = stringBuilder.toString();
        mRegionResultTextView.setText(mRegion);
        mStreetResultTextView.setText(mStreet);
    }

    private void initView() {
        mRegionResultTextView = find(R.id.receipt_address_region_result);
        mStreetResultTextView = find(R.id.receipt_address_street_result);
        mReceiverEdit = find(R.id.receiver_edittext);
        mPhoneNumberEdit = find(R.id.receipt_address_phone_edittext);
        mDetailAddressEdit = find(R.id.detailedly_address);
        UIUtils.addIcon(mRegionResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        UIUtils.addIcon(mStreetResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        find(R.id.receipt_address_street_layout).setOnClickListener(this);
        find(R.id.receipt_address_region_layout).setOnClickListener(this);
        find(R.id.commit_button).setOnClickListener(this);
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
                        && checkChange(receiver,phoneNumber,region,street,detailaddress)
                        ) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("receiver", receiver);
                        params.put("mail_tel", phoneNumber);
                        params.put("province", regions[0]);
                        params.put("city", regions[1]);
                        params.put("county", regions[2]);
                        params.put("street", street);
                        params.put("address", detailaddress);
                        params.put("tel", MeManager.getUid());
                        commitNet(params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.receipt_address_region_layout:
                startActivityForResult(new Intent(this, SelectRegionActivity.class), REQUEST_REGION);
                break;

            case R.id.receipt_address_street_layout:
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

    /**
     * 修改我的地址
     */
    private void commitNet(JSONObject jsonObject) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + RECEIPT_POSTADDRESS)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.optString("code");
                        if ("s_ok".equals(code)) {
                            ToastUtils.showToast(R.string.finish);
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "net", throwable);
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_faileds);
                    }
                });
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

    private boolean checkChange(String receiver,String phoneNumber,String region,String street,String address) {
        if (receiver.equals(mReceiver)
                &&phoneNumber.equals(mPhoneNumber)
                &&region.equals(mRegion)
                &&street.equals(mStreet)
                &&address.equals(mDetailaddress)){
            ToastUtils.showToast(R.string.postaddress_repeat);
            return false;
        }
        return true;
    }
}
