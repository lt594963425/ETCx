package com.etcxc.android.ui.view.cardstack;

/**
 * Created by LiuTao
 */
public interface RxScrollDelegate {

    void scrollViewTo(int x, int y);
    void setViewScrollY(int y);
    void setViewScrollX(int x);
    int getViewScrollY();
    int getViewScrollX();

}
