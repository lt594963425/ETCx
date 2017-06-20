package com.etcxc.android.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


/**
 *Created by 刘涛 on 2017/6/13 0013.
 *
 */
public class FocusTextview extends android.support.v7.widget.AppCompatTextView {


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

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if(focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	@Override
	public void onWindowFocusChanged(boolean focused) {
		if(focused)
			super.onWindowFocusChanged(focused);
	}

}
