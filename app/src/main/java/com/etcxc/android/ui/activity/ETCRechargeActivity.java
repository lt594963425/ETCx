package com.etcxc.android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.ui.adapter.MyRechaergeRecylerViewAdapter;
import com.etcxc.android.ui.adapter.MyRecylerViewAdapter;
import com.etcxc.android.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_cardnum_img: //添加历史卡号
                startActivityForResult(new Intent(this, HistoryRechargeCardActivity.class), 1);
                break;
            case R.id.recharge_add_detail_btn: //增加充值定单
                String moneyNumber = mRechaergeMoneyEdt.getText().toString().trim();
                String rechargeCardNumber = mRechaergeCardEdt.getText().toString().trim();
                if (TextUtils.isEmpty(rechargeCardNumber)) {
                    ToastUtils.showToast(R.string.card_isempty);
                    return;
                } else if (TextUtils.isEmpty(moneyNumber)) {
                    ToastUtils.showToast(R.string.money_isempty);
                    return;
                } else if (parseDouble(moneyNumber) > 20000.00) {
                    ToastUtils.showToast("单张卡充值金额不能大于¥20000.00元");
                    return;
                }
                DecimalFormat df = new DecimalFormat("0.00");
                mInfoList = new ArrayList<>();
                mInfoList = getInfoList(this);
                OrderRechargeInfo info = new OrderRechargeInfo();
                double allMoney = parseDouble(moneyNumber);
                if (mInfoList != null) {
                    for (int i = 0; i < mInfoList.size(); i++) {
                        info = mInfoList.get(i);
                        String cardNumber = info.getEtccarnumber();
                        String money = info.getRechargemoney();
                        allMoney = allMoney + parseDouble(money);
                        if (rechargeCardNumber.equals(cardNumber)) {
                            double totalMoney = parseDouble(money) + parseDouble(moneyNumber);
                            if (totalMoney > 20000.00) {
                                ToastUtils.showToast("单张卡充值金额不能大于¥20000.00元");
                                return;
                            }
                            delete(this, i);
                            myRechaergeRecylerViewAdapter.removeData(i);
                            moneyNumber = String.valueOf(df.format(totalMoney));
                        }
                    }
                    mRechaergeTotalMoney.setText(df.format(allMoney) + App.get().getString(R.string.yuan));
                }
                info.setEtccarnumber(rechargeCardNumber);
                info.setCarnumber("湘A123456252");
                info.setRechargename("刘小姐");
                info.setRechargemoney(moneyNumber);
                if (mInfoList == null) {
                    myRechaergeRecylerViewAdapter.addData(info, 0, mRechaergeDetailNum);

                } else {
                    myRechaergeRecylerViewAdapter.addData(info, mInfoList.size(), mRechaergeDetailNum);
                    mRechaergePrepaidRecyler.smoothScrollToPosition(0);
                }
                //存，匹配输入框
                saveCardHistory(this, "cardhistory", rechargeCardNumber);
                break;
            case R.id.etc_card_recharge:  //充值
                startActivity(new Intent(this, SelectPayWaysActivity.class));
                break;
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
