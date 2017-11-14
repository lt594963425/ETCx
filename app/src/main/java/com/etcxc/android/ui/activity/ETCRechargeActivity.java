package com.etcxc.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.callback.StringCallback;
import com.etcxc.android.ui.adapter.RechargeOrderAdapter;
import com.etcxc.android.ui.adapter.SelectMoneyAdapter;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.mTextWatcher;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.net.NetConfig.JSON;
import static com.etcxc.android.utils.UIUtils.closeAnimator;
import static com.etcxc.android.utils.UIUtils.initAutoCompleteCard;
import static com.etcxc.android.utils.UIUtils.setPricePoint;

/**
 * 充值
 *
 * @author 刘涛
 * @date 2017/7/5 0005
 */
public class ETCRechargeActivity extends BaseActivity implements SelectMoneyAdapter.OnItemClickListener, View.OnClickListener, RechargeOrderAdapter.OnItemDeleteClickListener {
    private static final int SELECT_SUCCESS = 1;
    private static final int RECHARGE_SUCCESS = 2;
    private AutoCompleteTextView mRechaergeCardEdt;
    private EditText mRechaergeMoneyEdt;
    private ImageView mAddCardImg, mCardNumDelete;
    private RecyclerView mRechaergeMoneyRecyler, mRechaergePrepaidRecyler;
    private TextView mRechaergeDetailNum, mRechaergeTotalMoney, mRechaergeAddDetailBtn;
    private String[] money = {"50", "100", "500", "1000", "1500", "2000"};
    private TextView mRecharge;
    private List<OrderRechargeInfo> mDatas = new ArrayList<>();
    private String mRechargeCardNumber;
    private int mMoneyNumber;
    private View viewEtc;
    private RechargeOrderAdapter mOrderFormAdapter;
    private Boolean isShowLeadPager = true;//是否显示过充值提示引导页面
    private Handler mHandler = null;
    private String SP_ADD＿CARD＿HISTORY = "cardhistory";
    private int mAllMoney = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_etc_onlinerecharge);
        initView();
    }

    private void initView() {
        setBarBack(find(R.id.etc_back));
        viewEtc = find(R.id.view_etc);
        mRecharge = find(R.id.etc_card_recharge);
        mRechaergeCardEdt = find(R.id.recharge_cardnum_edt);//卡号
        mAddCardImg = find(R.id.add_cardnum_img);//添加卡号
        mRechaergeMoneyEdt = find(R.id.recharge_money_Edt);// 输入的充值金额
        mRechaergeMoneyRecyler = find(R.id.recharge_money_recylerview);//选择金额
        mRechaergeDetailNum = find(R.id.recharge_detail_num); //充值明细(单数)
        mCardNumDelete = find(R.id.card_num_delete);
        mRechaergeTotalMoney = find(R.id.recharge_total_money); //合计
        mRechaergeAddDetailBtn = find(R.id.recharge_add_detail_btn); //添加
        UIUtils.addIcon(mRechaergeAddDetailBtn, R.drawable.vd_add, UIUtils.LEFT);
        mRechaergePrepaidRecyler = find(R.id.prepaid_recharge_recylerview); //待支付的订单列表
        setPricePoint(mRechaergeMoneyEdt);
        initAutoCompleteCard(SP_ADD＿CARD＿HISTORY, mRechaergeCardEdt);
        initLayout();
    }

    private void initLayout() {
        mHandler = new Handler();
        mRecharge.setOnClickListener(this);
        mAddCardImg.setOnClickListener(this);
        mRechaergeAddDetailBtn.setOnClickListener(this);
        setSeletMoneyRecyler();
        setOrderFormsRecyler();
        if (mHandler != null) {
            mHandler.postDelayed(LOAD_DATA, 400);
        }
    }

    private void setSeletMoneyRecyler() {
        GridLayoutManager gridLayManager = new GridLayoutManager(this, 3);
        gridLayManager.setOrientation(GridLayoutManager.VERTICAL);
        mRechaergeMoneyRecyler.setLayoutManager(gridLayManager);
        SelectMoneyAdapter Moneyadapter = new SelectMoneyAdapter(money);
        mRechaergeMoneyRecyler.setAdapter(Moneyadapter);
        mRechaergeMoneyRecyler.setHasFixedSize(true);
        Moneyadapter.setOnItemClickListener(this);
    }

    private void setOrderFormsRecyler() {
        mRechaergePrepaidRecyler.setLayoutManager(new LinearLayoutManager(this));
        mOrderFormAdapter = new RechargeOrderAdapter();
        mRechaergePrepaidRecyler.setAdapter(mOrderFormAdapter);
        mRechaergePrepaidRecyler.setHasFixedSize(true);
        mOrderFormAdapter.setmOnItemDeleteClickListener(this);
        mRechaergeCardEdt.addTextChangedListener(new mTextWatcher(mRechaergeCardEdt, mCardNumDelete));
        mCardNumDelete.setOnClickListener(this);

    }

    //选择充值金额
    @Override
    public void onItemClick(View view, int position) {//view = -1
        mRechaergeMoneyEdt.setText(money[position]);
    }

    @Override
    public void onItemDeleteClick(ImageView view, int position) {
        LogUtil.e(TAG, "删除：" + mDatas.get(position).toString());
        ToastUtils.showToast("position" + position);
        mAllMoney -= mDatas.get(position).getRechargemoney();
        mDatas.remove(position);
        mOrderFormAdapter.updateData(mDatas);
        mRechaergeTotalMoney.setText(mAllMoney / 100.00 + "元");
        mRechaergeDetailNum.setText(mDatas.size() + "");
        ToastUtils.showToast("删除");
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            isShowLeadPager = PublicSPUtil.getInstance().getBoolean("isShowLeadPager", true);
            if (isShowLeadPager) {
                showLeadHintPager();
            }
        }
    };

    /**
     * 引导
     */
    public void showLeadHintPager() {
        isShowLeadPager = false;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        PublicSPUtil.getInstance().putBoolean("isShowLeadPager", isShowLeadPager);
        View contentView = LayoutInflater.from(App.get()).inflate(R.layout.pup_lead_hint_one, null);
        View contentView2 = LayoutInflater.from(App.get()).inflate(R.layout.pup_lead_hint_two, null);
        UIUtils.addIcon(mRechaergeAddDetailBtn, R.drawable.vd_add, UIUtils.LEFT);
        PopupWindow pw1 = new PopupWindow(contentView, mRecharge.getWidth() * 3, UIUtils.dip2Px(100));
        pw1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        backgroundAlpha(this, 0.5f);
        pw1.setFocusable(true);
        pw1.setOutsideTouchable(true);
        pw1.showAsDropDown(mRecharge, UIUtils.dip2Px(-190), UIUtils.dip2Px(-30));
        PopupWindow pw2 = new PopupWindow(contentView2, dm.widthPixels, UIUtils.dip2Px(100));
        pw2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw2.setFocusable(true);
        pw2.setOutsideTouchable(true);
        pw2.showAsDropDown(viewEtc, 0, UIUtils.dip2Px(-23));
        pw2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(ETCRechargeActivity.this, 1f);
                if (pw1.isShowing()) {
                    pw1.dismiss();
                }
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_cardnum_img:
                openActivityForResult(HistoryRechargeCardActivity.class, SELECT_SUCCESS);
                break;
            case R.id.recharge_add_detail_btn: //增加充值定单
                addOrderFroms();
                break;
            case R.id.etc_card_recharge:
                MobclickAgent.onEvent(this, "RechargeClick");
                if (mDatas.size() == 0) {
                    ToastUtils.showToast(R.string.add_recharge_info);
                    return;
                }
                Intent intent = new Intent(this, ETCPayActivity.class);
                intent.putExtra("orderData", (Serializable) mDatas);
                intent.putExtra("allMoney", mAllMoney);
                startActivity(intent);
                break;
            case R.id.card_num_delete:
                mRechaergeCardEdt.setText("");
                break;
        }
    }

    private void addOrderFroms() {
        String stringMoney = mRechaergeMoneyEdt.getText().toString();
        mRechargeCardNumber = mRechaergeCardEdt.getText().toString();
        if (TextUtils.isEmpty(mRechargeCardNumber)) {
            ToastUtils.showToast(R.string.card_isempty);
            return;
        } else if (TextUtils.isEmpty(stringMoney)) {
            ToastUtils.showToast(R.string.money_isempty);
            return;
        }
        mMoneyNumber = (int) (Double.parseDouble(stringMoney) * 100);
        if (mMoneyNumber <= 0) {
            ToastUtils.showToast(R.string.money_isempty);
            return;
        }
        if (mMoneyNumber > 2000000) {
            ToastUtils.showToast(R.string.is_charge_big);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("etc_card", mRechargeCardNumber);
            jsonObject.put("money", mMoneyNumber);
            checkCardNum(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //检查充值卡是否存在
    private void checkCardNum(JSONObject jsonObject) {
        showProgressDialog(R.string.loading);
        OkHttpUtils
                .postString()
                .url(HOST + FUNC.ADDCARD)
                .content(String.valueOf(jsonObject))
                .tag(this)
                .mediaType(JSON).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showToast(R.string.request_failed);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        closeProgressDialog();
                        ToastUtils.showToast("添加成功");
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if ("s_ok".equals(jsonObject1.getString("code"))) {
                                mAllMoney += mMoneyNumber;
                                for (int i = 0; i < mDatas.size(); i++) {
                                    if (mDatas.get(i).getEtccarnumber().equals(mRechargeCardNumber)) {
                                        int eachMoney = mDatas.get(i).getRechargemoney() + mMoneyNumber;
                                        mDatas.get(i).setRechargemoney(eachMoney);
                                        mOrderFormAdapter.updateData(mDatas);
                                        return;
                                    }
                                }
                                JSONObject jsonArray = jsonObject1.getJSONObject("var");
                                OrderRechargeInfo orderInfo = new OrderRechargeInfo();
                                orderInfo.setRechargemoney(mMoneyNumber);
                                orderInfo.setRechargename(jsonArray.getString("username"));
                                orderInfo.setLicenseplate(jsonArray.getString("license_plate"));
                                orderInfo.setEtccarnumber(mRechargeCardNumber);
                                mDatas.add(0,orderInfo);
                                mOrderFormAdapter.updateData(mDatas);
                                mRechaergeTotalMoney.setText(mAllMoney / 100.00 + App.get().getString(R.string.yuan));
                                mRechaergeDetailNum.setText(mDatas.size() + "");
                                mRechaergePrepaidRecyler.scrollToPosition(0);
                            } else {
                                ToastUtils.showToast("查无此卡");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void setBarBack(ImageView mImg) {
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                closeAnimator(ETCRechargeActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_SUCCESS:
                if (data != null) {
                    String result = data.getStringExtra("number");
                    mRechaergeCardEdt.setText(result);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacks(LOAD_DATA);
        }
        OkHttpUtils.cancelTag(this);
        super.onDestroy();
    }


}
