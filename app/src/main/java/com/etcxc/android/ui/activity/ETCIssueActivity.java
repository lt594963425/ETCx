package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.CAN_ISSUE;
import static com.etcxc.android.utils.UIUtils.openAnimator;

/**
 * etc发行Activity
 * Created by xwpeng on 2017/6/17.
 */

public class ETCIssueActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ETCIssueActivity.class.getSimpleName();
    private RadioButton mPersonalRadiobutton;
    private EditText mCarCardEdit;
    private Spinner mCardColorSpinner;

    private String mCarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc_issue);
        initView();
    }

    private void initView() {
        setTitle(R.string.ETC_online_issue);
        mCarCardEdit = find(R.id.car_card_number_edittext);
        mCarCardEdit.setText("湘A12345");
        mCardColorSpinner = find(R.id.car_card_color_spinner);
        List<String> ls = new ArrayList<>();
        ls.add("黄底黑字");
        ls.add("蓝底白字");
        ls.add("黑底白字");
        ls.add("白底黑字");
        ls.add("绿底白字");
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ls);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCardColorSpinner.setAdapter(arr_adapter);
        mCardColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCarColor = ls.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mPersonalRadiobutton = find(R.id.personal_user_radiobutton);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                if (!MeManager.getIsLogin()){
                    openActivity(LoginActivity.class);
                    return;
                }
                String carCard = mCarCardEdit.getText().toString();
                if (okCarCard(carCard)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("licensePlate",carCard)
                                .put("plateColor",mCarColor)
                                .put("userType",mPersonalRadiobutton.isChecked()? 1 : 2)
                                .put("uid", MeManager.getUid())
                                .put("token",MeManager.getToken());
                        net(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else ToastUtils.showToast(getString(R.string.please_input_correct));
                break;
        }
    }

    private void net(JSONObject jsonObject) {
        //OkHttpClient client = new OkHttpClient();
        Log.e(TAG, String.valueOf(jsonObject));
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(NetConfig.consistUrl(CAN_ISSUE), jsonObject);
                Log.e(TAG,result);
                e.onNext(result);
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        Log.e(TAG, String.valueOf(jsonObject));
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code)) {
                            Intent intent = new Intent(ETCIssueActivity.this, UploadLicenseActivity.class);
                            PublicSPUtil.getInstance().putString("carCard", mCarCardEdit.getText().toString());
                            PublicSPUtil.getInstance().putString("carCardColor", mCarColor);
                            intent.putExtra("isOrg", !mPersonalRadiobutton.isChecked());
                            startActivity(intent);
                            openAnimator(ETCIssueActivity.this);
                        } else {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.request_failed);
                        }
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

    private boolean okCarCard(String carCard) {
        if (TextUtils.isEmpty(carCard)) return false;
        String rex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
        Pattern p = Pattern.compile(rex);
        return p.matcher(carCard).matches();
    }
}
