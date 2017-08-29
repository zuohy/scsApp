package cn.longmaster.ihuvc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.ui.BaseActivity;

/**
 * 通用加载进度框
 */
public class CustomProgressDialog extends Dialog {
    public static final int FADED_ROUND_SPINNER = 0;
    public static final long DISMISS_TIME_DEFAULT = 200;
    private static CustomProgressDialog instance;

    private View view;
    private TextView tvMessage;
    private ImageView ivSuccess;
    private ImageView ivFailure;
    private ImageView ivProgressSpinner;
    private AnimationDrawable adProgressSpinner;
    private Context context;

    private OnDialogDismiss onDialogDismiss;
    private long dismissTime = DISMISS_TIME_DEFAULT;
    private boolean consumeKeyBack;

    public OnDialogDismiss getOnDialogDismiss() {
        return onDialogDismiss;
    }

    public void setOnDialogDismiss(OnDialogDismiss onDialogDismiss) {
        this.onDialogDismiss = onDialogDismiss;
    }

    public static CustomProgressDialog getInstance(Context context) {
        if (instance == null) {
            instance = new CustomProgressDialog(context, context.getString(R.string.custom_progress_dialog_loading));
        }
        return instance;
    }

    public CustomProgressDialog(Context context, String message) {
        super(context, R.style.ProgressDialogTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        this.context = context;
        view = getLayoutInflater().inflate(R.layout.layout_custom_progress_dialog, null);
        tvMessage = (TextView) view.findViewById(R.id.textview_message);
        ivSuccess = (ImageView) view.findViewById(R.id.imageview_success);
        ivFailure = (ImageView) view.findViewById(R.id.imageview_failure);
        ivProgressSpinner = (ImageView) view.findViewById(R.id.imageview_progress_spinner);

        setSpinnerType(FADED_ROUND_SPINNER);
        tvMessage.setText(message);
        this.setContentView(view);
    }

    @SuppressWarnings("ResourceType")
    public void setSpinnerType(int spinnerType) {
        switch (spinnerType) {
            default:
                ivProgressSpinner.setImageResource(R.drawable.ic_round_spinner_fade);
        }

        adProgressSpinner = (AnimationDrawable) ivProgressSpinner.getDrawable();
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    @Override
    public void show() {
        if (!((BaseActivity) context).isFinishing()) {
            super.show();
        } else {
            instance = null;
        }
    }

    public void dismissWithSuccess() {
        tvMessage.setText(context.getString(R.string.custom_progress_dialog_load_success));
        showSuccessImage();

        if (onDialogDismiss != null) {
            this.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogDismiss.onDismiss();
                }
            });
        }
        dismissHUD();
    }

    public void dismissWithSuccess(String message) {
        showSuccessImage();
        if (message != null) {
            tvMessage.setText(message);
        } else {
            tvMessage.setText("");
        }

        if (onDialogDismiss != null) {
            this.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogDismiss.onDismiss();
                }
            });
        }
        dismissHUD();
    }

    public void dismissWithFailure() {
        showFailureImage();
        tvMessage.setText(context.getString(R.string.custom_progress_dialog_load_fail));
        if (onDialogDismiss != null) {
            this.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogDismiss.onDismiss();
                }
            });
        }
        dismissHUD();
    }

    public void dismissWithFailure(String message) {
        showFailureImage();
        if (message != null) {
            tvMessage.setText(message);
        } else {
            tvMessage.setText("");
        }
        if (onDialogDismiss != null) {
            this.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogDismiss.onDismiss();
                }
            });
        }
        dismissHUD();
    }

    protected void showSuccessImage() {
        ivProgressSpinner.setVisibility(View.GONE);
        ivSuccess.setVisibility(View.VISIBLE);
    }

    protected void showFailureImage() {
        ivProgressSpinner.setVisibility(View.GONE);
        ivFailure.setVisibility(View.VISIBLE);
    }

    protected void reset() {
        dismissTime = DISMISS_TIME_DEFAULT;
        ivProgressSpinner.setVisibility(View.VISIBLE);
        ivFailure.setVisibility(View.GONE);
        ivSuccess.setVisibility(View.GONE);
        tvMessage.setText(context.getString(R.string.custom_progress_dialog_loading));
    }

    protected void dismissHUD() {
        AsyncTask<String, Integer, Long> task = new AsyncTask<String, Integer, Long>() {

            @Override
            protected Long doInBackground(String... params) {
                if (dismissTime > 0)
                    SystemClock.sleep(dismissTime);
                return null;
            }

            @Override
            protected void onPostExecute(Long result) {
                super.onPostExecute(result);
                if (context != null) {
                    if (!((BaseActivity) context).isActivityFinished() && !((BaseActivity) context).isActivityDestroyed()) {
                        dismiss();
                        reset();
                    }
                }
            }
        };
        task.execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ivProgressSpinner.post(new Runnable() {

            @Override
            public void run() {
                adProgressSpinner.start();

            }
        });
    }

    public interface OnDialogDismiss {
        public void onDismiss();
    }

    public long getDismissTime() {
        return dismissTime;
    }

    public void setDismissTimeDefault() {
        dismissTime = DISMISS_TIME_DEFAULT;
    }

    public void setDismissTime(long dismissTime) {
        this.dismissTime = dismissTime;
    }

    public void setIsConsumeKeyBack(boolean isConsume) {
        consumeKeyBack = isConsume;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && consumeKeyBack) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
