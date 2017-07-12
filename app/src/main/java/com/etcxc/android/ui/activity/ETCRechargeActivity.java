package com.etcxc.android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.MyRechaergeRecylerViewAdapter;
import com.etcxc.android.ui.adapter.MyRecylerViewAdapter;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.utils.UIUtils.delete;
import static com.etcxc.android.utils.UIUtils.getInfoList;
import static com.etcxc.android.utils.UIUtils.initAutoCompleteCard;
import static com.etcxc.android.utils.UIUtils.saveCardHistory;
import static com.etcxc.android.utils.UIUtils.setPricePoint;
import static java.lang.Double.parseDouble;

/**
 * 充值
 * Created by 刘涛 on 2017/7/5 0005.
 */

public class ETCRechargeActivity extends BaseActivity implements MyRecylerViewAdapter.OnItemClickListener, View.OnClickListener, MyRechaergeRecylerViewAdapter.OnItemRechargeClickListener {
    private AutoCompleteTextView mRechaergeCardEdt;
    private EditText mRechaergeMoneyEdt;
    private ImageView mAddCardImg;
    private RecyclerView mRechaergeMoneyRecyler, mRechaergePrepaidRecyler;
    private TextView mRechaergeDetailNum, mRechaergeTotalMoney, mRechaergeAddDetailBtn;
    private String[] money = {"50", "100", "500", "1000", "1500", "2000"};
    private TextView mRecharge;
    private ArrayList<OrderRechargeInfo> mInfoList;
    private MyRechaergeRecylerViewAdapter myRechaergeRecylerViewAdapter;
    String infoUrl ="http://192.168.6.126:9999/pay/pay/addcard/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_etc_onlinerecharge);
        initView();
    }

    private void initView() {
        setBarBack(find(R.id.etc_back));
        mRecharge = find(R.id.etc_card_recharge);
        mRechaergeCardEdt = find(R.id.recharge_cardnum_edt);//卡号
        mAddCardImg = find(R.id.add_cardnum_img);//添加卡号
        mRechaergeMoneyEdt = find(R.id.recharge_money_Edt);// 输入的充值金额
        mRechaergeMoneyRecyler = find(R.id.recharge_money_recylerview);//选择金额
        mRechaergeDetailNum = find(R.id.recharge_detail_num); //充值明细(单数)
        mRechaergeTotalMoney = find(R.id.recharge_total_money); //合计
        mRechaergeAddDetailBtn = find(R.id.recharge_add_detail_btn); //添加
        mRechaergePrepaidRecyler = find(R.id.prepaid_recharge_recylerview); //待支付的订单列表
        setPricePoint(mRechaergeMoneyEdt);
        init();
    }

    private void init() {
        initData();
        mRecharge.setOnClickListener(this);
        initAutoCompleteCard(this, "cardhistory", mRechaergeCardEdt);
        mAddCardImg.setOnClickListener(this);
        mRechaergeAddDetailBtn.setOnClickListener(this);
        GridLayoutManager gridLayManager = new GridLayoutManager(this, 3);
        gridLayManager.setOrientation(GridLayoutManager.VERTICAL);
        mRechaergeMoneyRecyler.setLayoutManager(gridLayManager);
        MyRecylerViewAdapter Moneyadapter = new MyRecylerViewAdapter(money);
        mRechaergeMoneyRecyler.setAdapter(Moneyadapter);
        mRechaergeMoneyRecyler.setHasFixedSize(true);
        Moneyadapter.setOnItemClickListener(this);
        mRechaergePrepaidRecyler.setLayoutManager(new LinearLayoutManager(this));
        myRechaergeRecylerViewAdapter = new MyRechaergeRecylerViewAdapter(this, mInfoList);
        mRechaergePrepaidRecyler.setAdapter(myRechaergeRecylerViewAdapter);
        myRechaergeRecylerViewAdapter.setmOnItemRechargeClickListener(this);
    }

    private void initData() {
        mInfoList = new ArrayList<>();
        mInfoList = getInfoList(this);
        OrderRechargeInfo infos = new OrderRechargeInfo();
        if (mInfoList != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            double allMoney = 0;
            for (int i = 0; i < mInfoList.size(); i++) {
                infos = mInfoList.get(i);
                String money = infos.getRechargemoney();
                allMoney = allMoney + parseDouble(money);
            }
            mRechaergeDetailNum.setText(mInfoList.size()+"");
            mRechaergeTotalMoney.setText(df.format(allMoney) + App.get().getString(R.string.yuan));
        }
        Log.e(TAG,"初始化：list:"+mInfoList.size());
    }

    //选择的充值金额
    @Override
    public void onItemClick(View view, int position) {//view = -1
        mRechaergeMoneyEdt.setText(money[position]);
    }

    @Override
    public void onItemRechargeClick(ImageView view, int position) {
        myRechaergeRecylerViewAdapter.removeData(position);
        delete(ETCRechargeActivity.this, position);
        initData();
        ToastUtils.showToast("删除");
    }
    DecimalFormat df = new DecimalFormat("0.00");
    String mMoneyNumber;
    double allMoney;
    String mRechargeCardNumber;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_cardnum_img: //添加历史卡号
                startActivityForResult(new Intent(this, HistoryRechargeCardActivity.class), 1);
                break;
            case R.id.recharge_add_detail_btn: //增加充值定单
                mMoneyNumber = mRechaergeMoneyEdt.getText().toString().trim();
                mRechargeCardNumber = mRechaergeCardEdt.getText().toString().trim();
                if (TextUtils.isEmpty(mRechargeCardNumber)) {
                    ToastUtils.showToast(R.string.card_isempty);
                    return;
                } else if (TextUtils.isEmpty(mMoneyNumber)) {
                    ToastUtils.showToast(R.string.money_isempty);
                    return;
                } else if (parseDouble(mMoneyNumber) > 20000.00) {
                    ToastUtils.showToast(R.string .is_charge_big);
                    return;
                }else if(!(parseDouble(mMoneyNumber)> 0.00)){
                    ToastUtils.showToast(R.string .is_zero);
                }

                mInfoList = new ArrayList<>();
                mInfoList = getInfoList(this);

                 allMoney = parseDouble(mMoneyNumber);
                if (mInfoList != null) {
                    for (int i = 0; i < mInfoList.size(); i++) {
                        OrderRechargeInfo info;
                        info = mInfoList.get(i);
                        String cardNumber = info.getEtccarnumber();
                        String money = info.getRechargemoney();
                        allMoney = allMoney + parseDouble(money);
                        if (mRechargeCardNumber.equals(cardNumber)) {
                            double totalMoney = parseDouble(money) + parseDouble(mMoneyNumber);
                            if (totalMoney > 20000.00) {
                                ToastUtils.showToast(R.string .is_charge_big);
                                return;
                            }
                            delete(this, i);
                            myRechaergeRecylerViewAdapter.removeData(i);
                            mMoneyNumber = String.valueOf(df.format(totalMoney));
                        }
                    }//
                }

                showProgressDialog(getString(R.string.add_card_ing));
                Log.e(TAG,"+++++++++++++++++++++++++金钱："+(int)(parseDouble(mMoneyNumber)*100));//这里是分
                net(infoUrl+"card_num/"+mRechargeCardNumber+"/fee/"+(int)(parseDouble(mMoneyNumber)*100));
                break;
            case R.id.etc_card_recharge:  //充值
                if (mInfoList.size()==0){
                    ToastUtils.showToast(R.string.input_recharge_info);
                    return;
                }
                Log.e(TAG,"添加的信息："+mInfoList.size());
                startActivity(new Intent(this, SelectPayWaysActivity.class));
                break;
        }
    }

    private void net(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseResultJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.add_faid);
                        LogUtil.e(TAG, "changePwd", throwable);
                    }
                });
    }

    private void parseResultJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject == null) return;
            String code = jsonObject.getString("code");
            if (code.equals("s_ok")) {
                closeProgressDialog();
                OrderRechargeInfo info = new OrderRechargeInfo();
                JSONObject jsonVar = jsonObject.getJSONObject("var");
                info.setEtccarnumber(mRechargeCardNumber);
                info.setCarnumber(jsonVar.getJSONArray("veh_plate_code").getString(0));
                info.setRechargename(jsonVar.getJSONArray("user_name").getString(0));
                info.setRechargemoney(mMoneyNumber);
                info.setAlloney(String.valueOf((int)(allMoney*100)));//
                Log.e(TAG,"显示的结果：单个充值："+mMoneyNumber+"总数"+String.valueOf((int)(allMoney*100)));
                if (mInfoList == null) {
                    myRechaergeRecylerViewAdapter.addData(info, 0, mRechaergeDetailNum);
                } else {
                    myRechaergeRecylerViewAdapter.addData(info, mInfoList.size(), mRechaergeDetailNum);
                    mRechaergePrepaidRecyler.smoothScrollToPosition(0);
                }
                //总数
                mRechaergeTotalMoney.setText(df.format(allMoney) + App.get().getString(R.string.yuan));
                saveCardHistory(this, "cardhistory", mRechargeCardNumber);
                ToastUtils.showToast(R.string.add_succ);
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");//返回的信息
                closeProgressDialog();
                ToastUtils.showToast(returnMsg);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBarBack(ImageView mImg) {
        mImg.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String result = data.getStringExtra("number");
                    mRechaergeCardEdt.setText(result);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
