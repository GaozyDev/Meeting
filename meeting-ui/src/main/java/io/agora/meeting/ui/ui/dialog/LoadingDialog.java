package io.agora.meeting.ui.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.agora.meeting.ui.R;

public class LoadingDialog extends Dialog {

    private TextView mMessageTv;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        mMessageTv = findViewById(R.id.dialog_tv_loading);
    }

    public void setMessage(String msg) {
        mMessageTv.setText(msg);
    }
}