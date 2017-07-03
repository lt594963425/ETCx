package com.etcxc.android.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 *Created by 刘涛 on 2017/6/13 0013.
 *
 */
public class FocusTextview extends TextView {


	public FocusTextview(Context context) {
		super(context);
	}

	public FocusTextview(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	/**
	 * 模拟获取点击的焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
