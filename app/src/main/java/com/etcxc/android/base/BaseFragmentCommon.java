package com.etcxc.android.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 对Fragment进行一些常规的抽取
 *
 * BaseFragmentCommon抽取的好处(基类抽取的好处?)
 *      1.从java语言角度 --> 减少代码量,基类里面放置共有的方法,以及共有的属性
 *      2.针对BaseFragmentCommon --> 后期子类就只需要实现我们自己定义的方法就可以(init,initViews,initData,initListener)
 *      3.针对BaseFragmentCommon --> 还可以控制子类哪些方法是选择性实现,哪些方法是必须实现
 */
public abstract class BaseFragmentCommon extends Fragment {

    /**
     * Fragment被创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    /**
     * 返回Fragment所需要的布局
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 宿主Activity被创建的时候
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * @des 进行相关初始化操作
     * @des 在BaseFragmentCommon中, 不知道进行什么样的初始化操作, 交给子类, 子类是选择性实现
     * @called Fragment被创建
     */
    public void init() {

    }

    /**
     * @return
     * @des 初始化对应的视图, 返回给Fragment进行展示
     * @des 在BaseFragmentCommon中, 不知道如何具体初始化对应的视图, 交给子类, 子类是必须实现
     * @des 针对initView方法,必须实现,但是不知道具体实现,所以定义成为抽象方法,交给子类具体实现
     * @called Fragemnt需要一个布局的时候
     */
    public abstract View initView();

    /**
     * @des 初始化Fragment里面的数据加载
     * @des 在BaseFragmentCommon中, 不知道如何具体进行数据加载,交给子类,子类是选择性实现
     * @called 宿主Activity被创建的时候
     */
    public void initData() {

    }

    /**
     * @des 初始化Fragment里面相关的监听
     * @des 在BaseFragmentCommon中, 不知道如何具体添加事件的监听,交给子类,子类选择性的实现
     * @called 宿主Activity被创建的时候
     */
    public void initListener() {

    }
}
