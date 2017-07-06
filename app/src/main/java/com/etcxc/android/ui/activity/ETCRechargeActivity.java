package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * Created by 刘涛 on 2017/7/5 0005.
 */

public class ETCRechargeActivity  extends BaseActivity implements Toolbar.OnMenuItemClickListener {
    private EditText mRechaergeCardEdt,mRechaergeMoneyEdt;
    private ImageView mAddCardImg ;
    private RecyclerView mRechaergeMoneyRecyler,mRechaergePrepaidRecyler;
    private TextView mRechaergeDetailNum,mRechaergeTotalMoney;
    private Button mRechaergeAddDetailBtn;
    private int [] money ={50,100,500,1000,1500,2000};
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_etc_onlinerecharge);
        initView();


    }

    private void initView() {
        mToolbar = find(R.id.etc_toolbar);
        mToolbar.setTitle(R.string.home_etcrecharge);
        setBarBack(mToolbar);
        mToolbar.inflateMenu(R.menu.menu_etc_recharge);
        mToolbar.setPopupTheme(R.style.etc_popup_theme);
        mRechaergeCardEdt = find(R.id.recharge_cardnum_edt);//卡号
        mAddCardImg = find(R.id.add_cardnum_img);//添加卡号
        mRechaergeMoneyEdt = find(R.id.recharge_money_Edt);// 输入的充值金额
        mRechaergeMoneyRecyler = find(R.id.recharge_money_recylerview);//选择金额
        mRechaergeDetailNum = find(R.id.recharge_detail_num); //充值明细
        mRechaergeTotalMoney = find(R.id.recharge_total_money); //合计
        mRechaergeAddDetailBtn = find(R.id.recharge_add_detail_btn); //添加
        mRechaergePrepaidRecyler = find(R.id.prepaid_recharge_recylerview); //待支付的订单列表
        //充值金额
        GridLayoutManager gridLayManager = new GridLayoutManager(this,3);
        gridLayManager.setOrientation(GridLayoutManager.VERTICAL);
        mRechaergeMoneyRecyler.setLayoutManager(gridLayManager);
        mRechaergeMoneyRecyler.setAdapter(new MyRecylerViewAdapter(money));
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_etc_recharge:      //充值
                startActivity(new Intent(this, SelectPayWaysActivity.class));
                break;
        }
        return false;
    }

    class MyRecylerViewAdapter extends RecyclerView.Adapter {
        public int [] datas = null;
        public MyRecylerViewAdapter(int [] datas) {
            this.datas = datas;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etc_rechargemoney_recylerview,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder mh = (MyViewHolder) holder;
            mh.setDate(position);
        }
        @Override
        public int getItemCount() {
            return datas.length;
        }
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public MyViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_select_money_tv);
        }
        public void setDate(final int position) {
            mTextView.setText(""+money[position]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_etc_recharge,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    private void setBarBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
