package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.ui.view.cardstack.RxAdapterStack;
import com.etcxc.android.ui.view.cardstack.RxCardStackView;


public class CardAdapterStack extends RxAdapterStack<String> {

    public CardAdapterStack(Context context) {
        super(context);
    }

    @Override
    public void bindView(String data, int position, RxCardStackView.ViewHolder holder) {
        ColorItemViewHolder h = (ColorItemViewHolder) holder;
        h.onBind(data, position);

    }

    @Override
    protected RxCardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;

    }

    static class ColorItemViewHolder extends RxCardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle, mCardNumber;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mCardNumber = (TextView) view.findViewById(R.id.card_number);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(String data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position + 1));
            mCardNumber.setText(data);
        }
    }


}
