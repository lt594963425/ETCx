package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 圈存完成
 */
public class CreditForLoadFinishActivity extends BaseActivity {
    private LinearLayout linearLayout_succ;
    private TextView tv_defeat,tv_finish;
    private ImageView img_finish;
    private boolean isSucc = false;//圈存是否成功


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_for_load_finish);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.set_accomplish));
        linearLayout_succ = (LinearLayout) findViewById(R.id.line_succ);
        tv_defeat = (TextView) findViewById(R.id.tv_defeat);
        img_finish = (ImageView) findViewById(R.id.img_finish);
        tv_finish = (TextView) findViewById(R.id.tv_finish);

        if (isSucc){//显示成功
            tv_finish.setText("圈存成功");
            img_finish.setImageResource(R.drawable.vd_succeed);
            linearLayout_succ.setVisibility(View.VISIBLE);
            tv_defeat.setVisibility(View.INVISIBLE);
        }else {//显示失败
            tv_finish.setText("圈存失败");
            img_finish.setImageResource(R.drawable.vd_defeated);
            tv_defeat.setVisibility(View.VISIBLE);
            linearLayout_succ.setVisibility(View.INVISIBLE);
        }
    }
}
