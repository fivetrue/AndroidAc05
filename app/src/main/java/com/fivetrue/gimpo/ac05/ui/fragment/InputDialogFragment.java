package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class InputDialogFragment extends BaseDialogFragment{

    private static final String TAG = "InputDialogFragment";

    private EditText mInput;
    private String mText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mText = getArguments().getString("text");
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_input, null);
        mInput = (EditText) view.findViewById(R.id.et_fragment_input);
        mInput.setText(mText);
        mInput.selectAll();
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mText = s != null ? s.toString() : "";
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onClickOkButton() {
        if(getOnClickDialogFragmentListener() != null){
            getOnClickDialogFragmentListener().onClickOKButton(this, mText);
        }
    }
}
