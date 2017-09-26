package com.etcxc.android.ui.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.etcxc.android.base.Constants;
import com.etcxc.android.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 地理编码 （地址转换成经纬度）
 * Created by caoyu on 2017/7/31.
 */

public class GeocodeAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;
    private static final String TAG = "GEO_ADDY_SERVICE";

    public GeocodeAddressIntentService() {
        super("GeocodeAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.e(TAG, "onHandleIntent");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        List<Address> addresses = null;

        String name = intent.getStringExtra(Constants.LOCATION_NAME_DATA_EXTRA);
        try {
            addresses = geocoder.getFromLocationName(name, 1);
        } catch (IOException e) {
            errorMessage = "Service not available";
            LogUtil.e(TAG, errorMessage, e);
        }

        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Not Found";
                LogUtil.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, null);
        } else {
            for (Address address : addresses) {
                String outputAddress = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " --- " + address.getAddressLine(i);
                }
                LogUtil.e(TAG, outputAddress);
            }
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            
            LogUtil.i(TAG, "Address Found");
            if (Constants.LOCATION == intent.getIntExtra("flag", 0)) {
                deliverResultToReceiver(Constants.LOCATION,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments), address);
            } else if (Constants.DISTANCE == intent.getIntExtra("flag", 0)) {
                deliverResultToReceiver(Constants.DISTANCE, TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
            }

        }
    }

    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }
}