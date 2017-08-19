package com.etcxc.android.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by 刘涛 on 2017/7/4 0004.
 * @param
 */

public class myTextWatcher implements TextWatcher {
    private EditText editView;
    private ImageView imageView;
    public myTextWatcher(EditText editView,ImageView imageView){
         this.editView =editView;
         this.imageView =imageView;


    }
    CharSequence temp = "" ;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (temp.length() > 0 && !editView.getText().toString().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            temp = "";
        }else if(editView.getText().toString().trim().length()>0){
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

    }
}
