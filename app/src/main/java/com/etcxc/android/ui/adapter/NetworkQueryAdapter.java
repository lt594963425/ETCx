package com.etcxc.android.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.Constants;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.NetworkQueryActivity;
import com.etcxc.android.ui.server.GeocodeAddressIntentService;
import com.etcxc.android.utils.OpenExternalMapAppUtils;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.etcxc.android.utils.UIUtils.getString;

/**
 * 网点查询适配器
 * Created by caoyu on 2017/7/26.
 */

public class NetworkQueryAdapter extends RecyclerView.Adapter<NetworkQueryAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "NetworkQueryAdapter";
    private JSONArray mData;
    private Context mContext;
    private Dialog mDialog;
    private String mDestination;//目的地
    private int position;
    private String phone_nums[];
    private Location mLocation;
    int fetchType = Constants.USE_ADDRESS_LOCATION;

    public NetworkQueryAdapter(JSONArray mData, Context context, Location location) {
        this.mData = mData;
        this.mContext = context;
        this.mLocation = location;
    }

    @Override
    public NetworkQueryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_network, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NetworkQueryAdapter.ViewHolder holder, int position) {
        try {
            if (mData.getJSONObject(position) != null) {
                JSONObject jsonObject = mData.getJSONObject(position);
                holder.mTv_netstores_address.setText(jsonObject.optString("netstores_address"));
                holder.mTv_netstores_name.setText(String.format(jsonObject.optString("netstores_name")));
                holder.mTV_person_charge.setText(
                        String.format(getString(R.string.person_charge), jsonObject.optString("person_charge")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mTv_map.setOnClickListener(this);
        holder.mTv_map.setTag(position);//设置点击position
        holder.mTv_call.setOnClickListener(this);
        holder.mTv_call.setTag(position);//设置点击position
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_map://打开地图dialog
                position = (int) v.getTag();//获取点击position
                showMapDialog();
                break;
            case R.id.tv_call://拨打电话
                position = (int) v.getTag();//获取点击position
                callPhone();
                break;
            case R.id.btn_select_baidu://百度地图
                selectMap(0);
                mDialog.dismiss();
                break;
            case R.id.btn_select_gaode://高德地图
                selectMap(1);
                mDialog.dismiss();
                break;
            case R.id.btn_select_baidu_web://百度地图web
                selectMap(2);
                mDialog.dismiss();
                break;
            case R.id.btn_select_pic_cancel://取消
                mDialog.dismiss();
                break;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv_map, mTv_call, mTv_netstores_address, mTv_netstores_name, mTV_person_charge;

        public ViewHolder(View itemView) {
            super(itemView);
            mTv_map = (TextView) itemView.findViewById(R.id.tv_map);
            mTv_call = (TextView) itemView.findViewById(R.id.tv_call);
            mTv_netstores_address = (TextView) itemView.findViewById(R.id.tv_netstores_address);
            mTv_netstores_name = (TextView) itemView.findViewById(R.id.tv_netstores_name);
            mTV_person_charge = (TextView) itemView.findViewById(R.id.tv_person_charge);
        }
    }


    /**
     * 弹出地图选择dialog
     */
    private void showMapDialog() {
        mDialog = new Dialog(mContext, R.style.BottomDialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_map, null);
        //初始化控件
        initDialogView(inflate);
        //将布局设置给Dialog
        mDialog.setContentView(inflate);
        mDialog.setCancelable(true);
        //获取当前Activity所在的窗体
        Window dialogWindow = mDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        int mWindowWidth;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mWindowWidth = displayMetrics.widthPixels;
        mDialog.setContentView(inflate, new ViewGroup.MarginLayoutParams(mWindowWidth,
                ViewGroup.MarginLayoutParams.MATCH_PARENT));
        mDialog.show();//显示对话框
    }

    private void initDialogView(View inflate) {
        Button mGaode = (Button) inflate.findViewById(R.id.btn_select_gaode);
        Button mBaidu = (Button) inflate.findViewById(R.id.btn_select_baidu);
        Button mBaiduWeb = (Button) inflate.findViewById(R.id.btn_select_baidu_web);
        Button mCancel = (Button) inflate.findViewById(R.id.btn_select_pic_cancel);

        mCancel.setOnClickListener(this);
        mBaidu.setOnClickListener(this);
        mBaiduWeb.setOnClickListener(this);
        mGaode.setOnClickListener(this);
    }

    /**
     * 调用地图
     *
     * @param type 0：百度地图 1：高德地图 2:百度地图web
     */
    private void selectMap(int type) {
        try {
            if (mData.getJSONObject(position) != null) {
                JSONObject jsonObject = mData.getJSONObject(position);
                mDestination = jsonObject.optString("netstores_address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(mDestination)) {
            switch (type) {
                case 0://百度
                    if (OpenExternalMapAppUtils.isInstallByread("com.baidu.BaiduMap")) {
                        OpenExternalMapAppUtils.openBaiduMarkerMap(mContext, mDestination);
                    } else {
                        ToastUtils.showToast("您未安装百度地图");
                    }
                    break;
                case 1://高德
                    if (OpenExternalMapAppUtils.isInstallByread("com.autonavi.minimap")) {
                        AddressResultReceiver mResultReceiver = new AddressResultReceiver(null);
                        fetchType = Constants.USE_ADDRESS_NAME;
                        Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
                        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, mDestination);
                        Log.e(TAG, "Starting Service");
                        mContext.startService(intent);
                    } else {
                        ToastUtils.showToast("您未安装高德地图");
                    }
                    break;
                case 2://百度地图WEB版
                    OpenExternalMapAppUtils.openBrosserNaviMap(mContext, mLocation, mDestination);
                    break;
            }
        } else {
            ToastUtils.showToast("目的地为空");
        }
    }

    //拨号
    private void callPhone() {
        try {
            if (mData.getJSONObject(position) != null) {
                JSONObject jsonObject = mData.getJSONObject(position);
                String phone = jsonObject.optString("phone");
                phone_nums = phone.split("、");
                if (phone_nums.length > 1) {//多个号码时弹出提示框
                    SystemUtil.showCallDialog(mContext, phone_nums);
                } else {//单个号码直接拨打
                    SystemUtil.callPhone(mContext, phone_nums[0]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                OpenExternalMapAppUtils.goToNaviActivity(mContext, "", address.getLatitude(), address.getLongitude(), "1", "0");
            }
        }
    }
}
