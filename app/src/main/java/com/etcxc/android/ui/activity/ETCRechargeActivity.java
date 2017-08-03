package com.etcxc.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.adapter.RechargeOrderFormAdapter;
import com.etcxc.android.ui.adapter.SelectMoneyAdapter;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.ADDCARD;
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
public class ETCRechargeActivity extends BaseActivity implements SelectMoneyAdapter.OnItemClickListener, View.OnClickListener, RechargeOrderFormAdapter.OnItemRechargeClickListener {
    private static final int SELECT_SUCCESS = 1;
    private static final int RECHARGE_SUCCESS = 2;
    private AutoCompleteTextView mRechaergeCardEdt;
    private EditText mRechaergeMoneyEdt;
    private ImageView mAddCardImg, mCardNumDelete;
    private RecyclerView mRechaergeMoneyRecyler, mRechaergePrepaidRecyler;
    private TextView mRechaergeDetailNum, mRechaergeTotalMoney, mRechaergeAddDetailBtn;
    private String[] money = {"50", "100", "500", "1000", "1500", "2000"};
    private TextView mRecharge;
    private List<OrderRechargeInfo> mInfoList;
    private String mRechargeCardNumber;
    private String mMoneyNumber;
    private View viewEtc;
    private double allMoney;
    private RechargeOrderFormAdapter myRechaergeRecylerViewAdapter;
    private DecimalFormat df;
    private Boolean isShowLeadPager = true;
    private Handler mHandler = null;
    public static final String RESP_CODE_INFO = "com.etcxc.android.ui.activity.ETCRechargeActivity.code";

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
        mRechaergePrepaidRecyler = find(R.id.prepaid_recharge_recylerview); //待支付的订单列表
        setPricePoint(mRechaergeMoneyEdt);
        initAutoCompleteCard(this, "cardhistory", mRechaergeCardEdt);
        init();
    }

    private void init() {
        mHandler = new Handler();
        df = new DecimalFormat("0.00");
        mRecharge.setOnClickListener(this);
        mAddCardImg.setOnClickListener(this);
        mRechaergeAddDetailBtn.setOnClickListener(this);
        initData();
        setSeletMoneyData();
        setOrderFormData();
        if (mHandler != null) {
            mHandler.postDelayed(LOAD_DATA, 400);
        }
    }

    private void setSeletMoneyData() {
        GridLayoutManager gridLayManager = new GridLayoutManager(this, 3);
        gridLayManager.setOrientation(GridLayoutManager.VERTICAL);
        mRechaergeMoneyRecyler.setLayoutManager(gridLayManager);
        SelectMoneyAdapter Moneyadapter = new SelectMoneyAdapter(money);
        mRechaergeMoneyRecyler.setAdapter(Moneyadapter);
        mRechaergeMoneyRecyler.setHasFixedSize(true);
        Moneyadapter.setOnItemClickListener(this);
    }

    private void setOrderFormData() {
        mRechaergePrepaidRecyler.setLayoutManager(new LinearLayoutManager(this));
        myRechaergeRecylerViewAdapter = new RechargeOrderFormAdapter(this, mInfoList);
        mRechaergePrepaidRecyler.setAdapter(myRechaergeRecylerViewAdapter);
        mRechaergePrepaidRecyler.setItemAnimator(new DefaultItemAnimator());
        myRechaergeRecylerViewAdapter.setmOnItemRechargeClickListener(this);
        mRechaergeCardEdt.addTextChangedListener(new myTextWatcher(mRechaergeCardEdt, mCardNumDelete));
        mCardNumDelete.setOnClickListener(this);
    }

    private void initData() {
        mInfoList = new ArrayList<>();
        mInfoList = getInfoList(this);
        OrderRechargeInfo infos;
        if (mInfoList != null) {
            double allMoney = 0;
            for (int i = 0; i < mInfoList.size(); i++) {
                infos = mInfoList.get(i);
                String money = infos.getRechargemoney();
                allMoney = allMoney + parseDouble(money);
            }
            mRechaergeDetailNum.setText(mInfoList.size() + "");
            mRechaergeTotalMoney.setText(df.format(allMoney) + App.get().getString(R.string.yuan));
        }

    }

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
    protected void onResume() {

        super.onResume();
    }

    private Runnable LOAD_DATA = () -> {
        isShowLeadPager = PrefUtils.getBoolean(ETCRechargeActivity.this, "isShowLeadPager", true);
        if (isShowLeadPager) {
            showLeadHintPager();
        }
    };

    public void showLeadHintPager() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        isShowLeadPager = false;
        PrefUtils.setBoolean(this, "isShowLeadPager", isShowLeadPager);
        View contentView = LayoutInflater.from(App.get()).inflate(R.layout.pup_lead_hint_one, null);
        View contentView2 = LayoutInflater.from(App.get()).inflate(R.layout.pup_lead_hint_two, null);
        PopupWindow pw = new PopupWindow(contentView, mRecharge.getWidth() * 3, UIUtils.dip2Px(100));
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//**************设置背景图片***************
        backgroundAlpha(this, 0.5f);
        pw.setFocusable(true);
        pw.setOutsideTouchable(true);
        pw.showAsDropDown(mRecharge, UIUtils.dip2Px(-190), UIUtils.dip2Px(-30));
        PopupWindow pw2 = new PopupWindow(contentView2, width, UIUtils.dip2Px(100));
        pw2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//**************设置背景图片***************
        pw2.setFocusable(true);
        pw2.setOutsideTouchable(true);
        pw2.showAsDropDown(viewEtc, 0, UIUtils.dip2Px(-23));
        pw2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(ETCRechargeActivity.this, 1f);
                if (pw.isShowing()) {
                    pw.dismiss();
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
            case R.id.add_cardnum_img: //添加历史卡号
                startActivityForResult(new Intent(this, HistoryRechargeCardActivity.class), SELECT_SUCCESS);
                break;
            case R.id.recharge_add_detail_btn: //增加充值定单
                addRechargeDetail();
                break;
            case R.id.etc_card_recharge:  //充值
                MobclickAgent.onEvent(this, "RechargeClick");
                if (getInfoList(this) == null || getInfoList(this).size() < 1) {
                    ToastUtils.showToast(R.string.add_recharge_info);
                    return;
                }
                Log.e(TAG, "添加订单信息：" + getInfoList(this).size());
                openActivity(SelectPayWaysActivity.class);
                break;
            case R.id.card_num_delete:
                mRechaergeCardEdt.setText("");
                break;
        }
    }

    private void addRechargeDetail() {
        if (LocalThrough()) return;
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
                        ToastUtils.showToast(R.string.is_charge_big);
                        return;
                    }
                    delete(this, i);
                    myRechaergeRecylerViewAdapter.removeData(i);
                    mMoneyNumber = String.valueOf(df.format(totalMoney));
                }
            }
        }
        showProgressDialog(getString(R.string.add_card_ing));
        Log.e(TAG, "+++++++++++++++++++++++++金钱：" + (int) (parseDouble(mMoneyNumber) * 100));//这里是分
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("card_num",mRechargeCardNumber);
            jsonObject.put("fee", (int) (parseDouble(mMoneyNumber) * 100));
            jsonObject.put("card_num",mRechargeCardNumber);
            net(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean LocalThrough() {
        mMoneyNumber = mRechaergeMoneyEdt.getText().toString().trim();
        mRechargeCardNumber = mRechaergeCardEdt.getText().toString().trim();
        if (TextUtils.isEmpty(mRechargeCardNumber)) {
            ToastUtils.showToast(R.string.card_isempty);
            return true;
        } else if (TextUtils.isEmpty(mMoneyNumber)) {
            ToastUtils.showToast(R.string.money_isempty);
            return true;
        } else if (parseDouble(mMoneyNumber) > 20000.00) {
            ToastUtils.showToast(R.string.is_charge_big);
            return true;
        } else if (!(parseDouble(mMoneyNumber) > 0.00)) {
            Log.e(TAG, "+++++++++++++++++++++" + mMoneyNumber);
            ToastUtils.showToast(R.string.is_zero);
            return true;
        }
        return false;
    }

    private void net(JSONObject jsonObject) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(NetConfig.consistUrl(ADDCARD), jsonObject);
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
                if (jsonVar == null) {
                    return;
                }
                info.setEtccarnumber(mRechargeCardNumber);
                info.setCarnumber(jsonVar.getJSONArray("veh_plate_code").getString(0));
                info.setRechargename(jsonVar.getJSONArray("user_name").getString(0));
                info.setRechargemoney(mMoneyNumber);
                info.setAlloney(String.valueOf((int) (allMoney * 100)));//
                Log.e(TAG, "显示的结果：单个充值：" + mMoneyNumber + "总数" + String.valueOf((int) (allMoney * 100)));
                if (mInfoList == null) {
                    myRechaergeRecylerViewAdapter.addData(info, 0, mRechaergeDetailNum);
                } else {
                    myRechaergeRecylerViewAdapter.addData(info, mInfoList.size(), mRechaergeDetailNum);
                    mRechaergePrepaidRecyler.smoothScrollToPosition(0);
                }
                //总数
                mRechaergeTotalMoney.setText(df.format(allMoney) + App.get().getString(R.string.yuan));
                saveCardHistory(this, "cardhistory", mRechargeCardNumber);

            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");//返回的信息
                closeProgressDialog();
                ToastUtils.showToast(returnMsg);
                return;
            }
        } catch (JSONException e) {
            closeProgressDialog();
            ToastUtils.showToast(R.string.request_failed);
            e.printStackTrace();
        }
    }

    private void setBarBack(ImageView mImg) {
        mImg.setOnClickListener(v -> finish());
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
        super.onDestroy();
    }



}
