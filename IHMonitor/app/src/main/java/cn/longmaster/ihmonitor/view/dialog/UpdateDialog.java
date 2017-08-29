package cn.longmaster.ihmonitor.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.longmaster.doctorlibrary.util.common.TextViewUtil;
import cn.longmaster.ihmonitor.R;

public class UpdateDialog extends Dialog {

    private static Builder sBuilder;
    private static UpdateDialog sUpdateDialog;

    public UpdateDialog(Context context) {
        super(context);
    }

    public UpdateDialog(Context context, int theme) {
        super(context, theme);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        TextViewUtil.setTextAfterColon(getContext(), (TextView) findViewById(R.id.dialog_update_latest_version_tv), sBuilder.version);
        TextViewUtil.setTextAfterColon(getContext(), (TextView) findViewById(R.id.dialog_update_latest_size_tv), sBuilder.size);
        TextViewUtil.setTextAfterColon(getContext(), (TextView) findViewById(R.id.dialog_update_latest_feature_list_tv), sBuilder.feature);
        findViewById(R.id.dialog_update_latest_left_button_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sBuilder.onClickListenerLeft != null)
                    sBuilder.onClickListenerLeft.onClick(v);
            }
        });
        findViewById(R.id.dialog_update_latest_right_button_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sBuilder.onClickListenerRight != null)
                    sBuilder.onClickListenerRight.onClick(v);
            }
        });
        setCancelable(sBuilder.cancelable);
    }

    private static UpdateDialog showDialog(Context context, Builder builder) {
        sBuilder = builder;
        if (sUpdateDialog == null || !sUpdateDialog.getContext().getClass().getCanonicalName().equals(context.getClass().getCanonicalName())) {
            sUpdateDialog = new UpdateDialog(context, R.style.updateDialogStyle);
        }
        sUpdateDialog.show();
        return sUpdateDialog;
    }

    public static class Builder {
        private Context context;
        private String version;
        private String size;
        private String feature;
        private View.OnClickListener onClickListenerLeft;
        private View.OnClickListener onClickListenerRight;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setFeature(String feature) {
            this.feature = feature;
            return this;
        }

        public Builder setOnClickListenerLeft(View.OnClickListener onClickListenerLeft) {
            this.onClickListenerLeft = onClickListenerLeft;
            return this;
        }

        public Builder setOnClickListenerRight(View.OnClickListener onClickListenerRight) {
            this.onClickListenerRight = onClickListenerRight;
            return this;
        }

        public void setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
        }

        public UpdateDialog show() {
            return showDialog(context, this);
        }
    }
}
