package com.etcxc.android.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.etcxc.android.R;

/**
 * 分享弹出框
 * Created by caoyu on 2017/11/8/008.
 */

public class SharePopupWindows extends PopupWindow {

    private View mView;
    private TextView tv_wechat, tv_wechat_timeline, tv_qq, tv_sms;

    public SharePopupWindows(Context context, View.OnClickListener onClickListener) {
        super(context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.dialog_share, null);
        initDialogView(context, mView);
        tv_wechat.setOnClickListener(onClickListener);
        tv_wechat_timeline.setOnClickListener(onClickListener);
        tv_qq.setOnClickListener(onClickListener);
        tv_sms.setOnClickListener(onClickListener);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private void initDialogView(Context context, View v) {
        tv_wechat = (TextView) v.findViewById(R.id.tv_wechat);
        tv_wechat_timeline = (TextView) v.findViewById(R.id.tv_wechat_timeline);
        tv_qq = (TextView) v.findViewById(R.id.tv_qq);
        tv_sms = (TextView) v.findViewById(R.id.tv_sms);

        Drawable drawableWechat = context.getResources().getDrawable(
                R.drawable.vd_wechat);
        tv_wechat.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableWechat, null, null);
        tv_wechat.setCompoundDrawablePadding(5);

        Drawable drawableTimeline = context.getResources().getDrawable(
                R.drawable.vd_circle_friends);
        tv_wechat_timeline.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableTimeline, null, null);
        tv_wechat_timeline.setCompoundDrawablePadding(5);


        Drawable drawableQQ = context.getResources().getDrawable(
                R.drawable.vd_qq);
        tv_qq.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableQQ, null, null);
        tv_qq.setCompoundDrawablePadding(5);

        Drawable drawableSMS = context.getResources().getDrawable(
                R.drawable.vd_message);
        tv_sms.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableSMS, null, null);
        tv_sms.setCompoundDrawablePadding(5);

    }



}
