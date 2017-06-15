package com.etcxc.android.ui.view;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 替换{@link Toolbar}，提供更多功能，暂时只是为了单击标题栏才自定义的
 * Created by xwpeng on 2017/6/12.
 */

public class XToolbar extends Toolbar {
    private static final String TAG = "XToolbar";
    private ActionBar mActionBar;
    private TextView mCTitleView;

    public XToolbar(Context context) {
        super(context, null);
    }

    public XToolbar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public XToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setElevation(0f);
        else ViewCompat.setElevation(this, 0f);
        post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutParams() instanceof LayoutParams) {
                    ((LayoutParams) getLayoutParams()).gravity = Gravity.CENTER;
                }
            }
        });
    }

    public void setActionBar(ActionBar ab) {
        this.mActionBar = ab;
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setDisplayShowCustomEnabled(true);
        }
    }

    /**
     * 需要先调用{@link #setTitle(CharSequence)}或{@link #setTitle(int)}后才有用
     * 反射TitleView
     */
    private void reflectViews() {
        long t1 = System.currentTimeMillis();
        try {
            Field f = getClass().getSuperclass().getDeclaredField("mTitleTextView");
            if (f != null) {
                f.setAccessible(true);
                Object o = f.get(this);
                if (o instanceof TextView) {
                    mCTitleView = (TextView) o;
                }
            }
        } catch (NoSuchFieldException e) {
            LogUtil.e(TAG, "reflectViews", e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "reflectViews", e);
        } catch (Exception e) {
            LogUtil.e(TAG, "reflectViews", e);
        }
        LogUtil.d(TAG, "reflectViews:time spent=" + (System.currentTimeMillis() - t1));
    }

    /**
     * 替换{@link Toolbar#addView(View)}方法
     *
     * @param view
     */
    public void setView(View view) {
        if (mActionBar != null) {
            mActionBar.setCustomView(view, new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 激活Toolbar点击事件
     */
    public void setClickEventActivated(boolean activated) {
        reflectViews();
        setToolbarListener(activated);
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends android.os.Handler {

        private static final int WHAT_CLICK = 1;
        private static final int WHAT_DOUBLE_CLICK = 2;

        private WeakReference<XToolbar> mBar;

        public MyHandler(XToolbar mBar) {
            this.mBar = new WeakReference<>(mBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            XToolbar bar = mBar.get();
            if (bar != null) {
                if (msg.what == MyHandler.WHAT_CLICK) {
                    bar.onClick();
                } else if (msg.what == MyHandler.WHAT_DOUBLE_CLICK) {
                    bar.onDoubleClick();
                }
            }
        }
    }

    private void onClick() {
        mFirstClickTime = 0;
        if (mOnToolbarTitleClickListener != null) {
            mOnToolbarTitleClickListener.onToolbarTitleClick();
        }
    }

    private void onDoubleClick() {
        mFirstClickTime = 0;
        if (mOnToolbarTitleDoubleClickListener != null) {
            mOnToolbarTitleDoubleClickListener.onToolbarTitleDoubleClick();
        }
    }

    private long mFirstClickTime;
    private final long CLICK_INTERVAL = 400L;

    /**
     * @param activated 激活或关闭点击事件
     */
    private void setToolbarListener(boolean activated) {
        if (mCTitleView != null) {
            mCTitleView.setOnClickListener(activated ? new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.removeMessages(MyHandler.WHAT_CLICK);
                    mHandler.removeMessages(MyHandler.WHAT_DOUBLE_CLICK);
                    long t = System.currentTimeMillis();
                    if (mFirstClickTime == 0 || t - mFirstClickTime > CLICK_INTERVAL) {
                        mFirstClickTime = t;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(MyHandler.WHAT_CLICK, CLICK_INTERVAL);
                        }
                    } else {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MyHandler.WHAT_DOUBLE_CLICK);
                        }
                    }
                }
            } : null);
        }
    }

    private OnToolbarTitleClickListener mOnToolbarTitleClickListener;

    public void setOnToolbarTitleClickListener(OnToolbarTitleClickListener listener) {
        this.mOnToolbarTitleClickListener = listener;
    }

    public interface OnToolbarTitleClickListener {
        void onToolbarTitleClick();
    }

    private OnToolbarTitleDoubleClickListener mOnToolbarTitleDoubleClickListener;

    public void setOnToolbarTitleDoubleClickListener(OnToolbarTitleDoubleClickListener listener) {
        this.mOnToolbarTitleDoubleClickListener = listener;
    }

    public interface OnToolbarTitleDoubleClickListener {
        void onToolbarTitleDoubleClick();
    }
}

