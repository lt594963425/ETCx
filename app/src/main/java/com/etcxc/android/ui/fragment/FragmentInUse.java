package com.etcxc.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.adapter.CardAdapterStack;
import com.etcxc.android.ui.view.cardstack.RxCardStackView;

import java.util.ArrayList;
import java.util.List;


/**
 * 正在使用的
 * Created by LiuTao on 2017/8/26 0026.
 */

public class FragmentInUse extends BaseFragment implements RxCardStackView.ItemExpendListener {
    private RxCardStackView mStackView;
    private CardAdapterStack mCardStackAdapter;
    private List<String> mDatas;

    public FragmentInUse(ArrayList<String> datas) {
        mDatas= new ArrayList<>();
        mDatas.addAll(datas);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_card_use, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStackView = (RxCardStackView) view.findViewById(R.id.card_stack_view);
        mStackView.setItemExpendListener(this);
        mCardStackAdapter = new CardAdapterStack(getActivity());
        mStackView.setAdapter(mCardStackAdapter);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mCardStackAdapter.updateData(mDatas);
                    }
                }
                , 800
        );

    }

    @Override
    public void onItemExpend(boolean expend) { //卡片是否展开和折叠
        // do something
    }
}
