package cn.longmaster.ihmonitor.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import cn.longmaster.ihmonitor.R;

public class LogoutDialog extends Dialog implements View.OnClickListener {
    private LogoutDialogClickListener listener;

    public LogoutDialog(@NonNull Context context) {
        this(context, R.style.custom_noActionbar_window_style);
    }

    public LogoutDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    public void setLogoutDialogClickListener(LogoutDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logout);
        findViewById(R.id.dialog_logout_cancle_textview).setOnClickListener(this);
        findViewById(R.id.dialog_logout_confirm_textview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.dialog_logout_cancle_textview:
                dismiss();
                listener.onCancleClicked();
                break;

            case R.id.dialog_logout_confirm_textview:
                listener.onConfirmClicked();
                dismiss();
                break;
        }
    }

    public interface LogoutDialogClickListener {
        void onCancleClicked();

        void onConfirmClicked();
    }
}
