package com.etcxc.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LiuTao on 2017/8/26 0026.
 */

public class FragmentTotal extends BaseFragment  {

    private List<String> mDatas = new ArrayList<>();
    //private ProgressBar mProgress;
    public FragmentTotal() {
    }

    public FragmentTotal(List<String> datas) {
        mDatas = new ArrayList<>();
        mDatas.addAll(datas);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_card_use, null);
        return view;
    }


}
