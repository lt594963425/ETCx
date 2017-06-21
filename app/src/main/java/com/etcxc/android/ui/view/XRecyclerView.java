package com.etcxc.android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;

import com.etcxc.android.R;

/**
 * 对RecyclerView封装
 * Created by xwpeng on 2017/6/21.
 */

public class XRecyclerView extends RecyclerView{
    private static final String TAG = "XRecyclerView";

    private RecyclerView.ItemDecoration mItemDecoration;

    public XRecyclerView(Context context) {
       this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
       this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDefaultLayoutManager();
        setFocusableInTouchMode(true);
        ItemAnimator itemAnimator = getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);

    }

    /**
     * 设置一个默认的{@link android.support.v7.widget.RecyclerView.LayoutManager LayoutManager}
     */
    public void setDefaultLayoutManager() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
    }

    public void removeDivider() {
        if (mItemDecoration != null) removeItemDecoration(mItemDecoration);

    }

    public void setDivider() {
        setDivider(R.color.space_line_color);
    }

    public void setDivider(int color) {
        setDivider(getResources().getColor(color), 1, 0, 0);
    }

    public void setDivider(int leftSpace, int rightSpace) {
        setDivider(leftSpace, rightSpace, R.color.space_line_color);
    }

    public void setDivider(int leftSpace, int rightSpace, int color) {
        setDivider(getResources().getColor(color), 1, leftSpace, rightSpace);
    }

    /*** 设置Divider*/
    public void setDivider(int color, int thickness, final int leftSpace, final int rightSpace) {
        final Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(thickness);
        removeDivider();
        mItemDecoration = new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                for (int i = 0, size = parent.getChildCount() - 1; i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft() + leftSpace, child.getBottom(), child.getRight() - rightSpace, child.getBottom(), paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        };
        addItemDecoration(mItemDecoration);
    }

    /**
     * 设置Divider
     * @param divider 若为null，则会移除分隔线
     */
    public void setDivider(final Divider divider) {
        if (divider == null) {
            removeDivider();
            return;
        }
        final Paint paint = new Paint();
        paint.setColor(getResources().getColor(divider.color));
        paint.setStrokeWidth(divider.thickness);
        removeDivider();
        mItemDecoration = new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                for (int i = 0, size = divider.lastOne ? parent.getChildCount() : parent.getChildCount() - 1; i < size; i++) {
                    View child = parent.getChildAt(i);
                    if (divider.matchParentIfLastOne && i == size - 1) {
                        c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
                    } else {
                        c.drawLine(child.getLeft() + divider.leftSpace, child.getBottom(), child.getRight() - divider.rightSpace, child.getBottom(), paint);
                    }
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        };
        addItemDecoration(mItemDecoration);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        LayoutManager lm = getLayoutManager();
        int firstPosition = -1;
        int lastPosition = -1;
        int startPosition = -1;
        if (lm instanceof LinearLayoutManager) {
            firstPosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
            lastPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager) {
            firstPosition = ((GridLayoutManager) lm).findFirstVisibleItemPosition();
            lastPosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
        }
        if (firstPosition != -1 && lastPosition != -1) {
            if (position < firstPosition) {
                //向上滚
                if (firstPosition - position > 20) {
                    startPosition = position + 5;
                    super.scrollToPosition(startPosition);
                }
            } else if (position > lastPosition) {
                //向下滚
                if (position - lastPosition > 20) {
                    startPosition = position - 5;
                    super.scrollToPosition(startPosition);
                }
            } else {
                //在可视范围，不做特殊处理
            }
        }
        smoothDistanceByPosition(position);
    }

    private void smoothDistanceByPosition(int position) {
        View view = getLayoutManager().findViewByPosition(position);
        if (view != null) {
            super.smoothScrollBy(0, view.getTop());
        } else {
            super.smoothScrollToPosition(position);
        }
    }

    /**
     * 分隔线，纯色
     */
    public static class Divider {
        private int color;
        private int thickness;
        private int leftSpace;
        private int rightSpace;
        private boolean lastOne;//最后一条也显示
        private boolean matchParentIfLastOne;//让最后一线分隔线完整

        public static class Builder {
            private int color;
            private int thickness;
            private int leftSpace;
            private int rightSpace;
            private boolean lastOne;//最后一条也显示
            private boolean matchParentIfLastOne;//让最后一线分隔线完整

            public Builder() {
            }

            /**
             * @param color 颜色，只可以是纯色
             * @return
             */
            public Divider.Builder color(@ColorRes int color) {
                this.color = color;
                return this;
            }

            /**
             * @param thickness 粗细，单位像素
             * @return
             */
            public Divider.Builder thickness(int thickness) {
                this.thickness = thickness;
                return this;
            }

            /**
             * @param leftSpace 左边空出的宽度
             * @return
             */
            public Divider.Builder leftSpace(int leftSpace) {
                this.leftSpace = leftSpace;
                return this;
            }

            /**
             * @param rightSpace 右边空出的宽度
             * @return
             */
            public Divider.Builder rightSpace(int rightSpace) {
                this.rightSpace = rightSpace;
                return this;
            }

            /**
             * @param lastOne 若为true，则最后一条分隔线也显示
             * @return
             */
            public Divider.Builder lastOne(boolean lastOne) {
                this.lastOne = lastOne;
                return this;
            }

            /**
             * @param matchParentIfLastOne 若为true，则最后一条分隔线撑满宽度
             * @return
             */
            public Divider.Builder matchParentIfLastOne(boolean matchParentIfLastOne) {
                if (matchParentIfLastOne) {
                    this.lastOne = true;
                }
                this.matchParentIfLastOne = matchParentIfLastOne;
                return this;
            }

            public Divider build() {
                Divider d = new Divider();
                //默认值
                if (color == 0) {
                    color = R.color.space_line_color;
                }
                d.color = this.color;
                d.thickness = this.thickness;
                d.leftSpace = this.leftSpace;
                d.rightSpace = this.rightSpace;
                d.lastOne = this.lastOne;
                d.matchParentIfLastOne = this.matchParentIfLastOne;
                return d;
            }
        }
    }
}
