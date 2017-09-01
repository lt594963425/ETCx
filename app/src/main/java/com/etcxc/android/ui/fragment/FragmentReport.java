package com.etcxc.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.adapter.CardAdapterStack;
import com.etcxc.android.ui.view.cardstack.RxCardStackView;
import com.etcxc.android.utils.Md5Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LiuTao on 2017/8/26 0026.
 */

public class FragmentReport extends BaseFragment implements RxCardStackView.ItemExpendListener {
    private RxCardStackView mStackView;
    private CardAdapterStack mCardStackAdapter;
    private List<String> mDatas ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_card_use, null);
        initData();
        initView(view);
        return view;
    }
    int  x = 1;
    private void initData() {
        mDatas =  new ArrayList<>();
        for (int i=0; i<10;i++){
            x +=i;
            mDatas.add(Md5Utils.encryptpwd(String.valueOf(x)));
        }
    }
    private void initView(View view) {
        mStackView = (RxCardStackView) view.findViewById(R.id.card_stack_view);
        mStackView.setItemExpendListener(this);
        mCardStackAdapter = new CardAdapterStack(getActivity());
        mStackView.setAdapter(mCardStackAdapter);
        mCardStackAdapter.updateData(mDatas);
    }
    @Override
    public void onItemExpend(boolean expend) {

    }
}
