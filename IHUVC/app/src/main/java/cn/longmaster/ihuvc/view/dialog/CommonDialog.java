package cn.longmaster.ihuvc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihuvc.R;

/**
 * 通用Dialog
 * 可现实隐藏标题，显示一个或两个按钮
 * Created by Yang² on 2016/7/21.
 */
public class CommonDialog extends Dialog {

    @FindViewById(R.id.layout_common_dialog_title_tv)
    private TextView mTitleTv;
    @FindViewById(R.id.layout_common_dialog_message_tv1)
    private TextView mMessageTvOne;
    @FindViewById(R.id.layout_common_dialog_message_tv2)
    private TextView mMessageTvTwo;
    @FindViewById(R.id.layout_common_dialog_positive_tv)
    private TextView mPositiveTv;
    @FindViewById(R.id.layout_common_dialog_negative_tv)
    private TextView mNegativeTv;
    @FindViewById(R.id.layout_common_dialog_line_view)
    private View mLineView;

    private static List<DialogParams> mDialogParamsList = new ArrayList<>();

    protected CommonDialog(Context context) {
        this(context, R.style.custom_noActionbar_window_style);
    }

    protected CommonDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_dialog);
        ViewInjecter.inject(this);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.messageGravity = Gravity.CENTER;
        win.setAttributes(lp);
        setContent();
    }


    private void setContent() {
        DialogParams dialogParams = mDialogParamsList.get(mDialogParamsList.size() - 1);
        mTitleTv.setVisibility(!TextUtils.isEmpty(dialogParams.title) ? View.VISIBLE : View.GONE);
        mTitleTv.setText(dialogParams.title);
        mMessageTvOne.setVisibility(!TextUtils.isEmpty(dialogParams.messageOne) ? View.VISIBLE : View.GONE);
        mMessageTvTwo.setVisibility(!TextUtils.isEmpty(dialogParams.messageTwo) ? View.VISIBLE : View.GONE);
        mMessageTvOne.setText(dialogParams.messageOne);
        mMessageTvTwo.setText(dialogParams.messageTwo);
        /*mMessageTvTwo.setGravity(START);
        mMessageTvOne.setGravity(dialogParams.getMessageGravity());*/

        mPositiveTv.setVisibility(!TextUtils.isEmpty(dialogParams.positiveButtonText) ? View.VISIBLE : View.GONE);
        mPositiveTv.setText(dialogParams.positiveButtonText);
        mNegativeTv.setVisibility(!TextUtils.isEmpty(dialogParams.negativeButtonText) ? View.VISIBLE : View.GONE);
        mNegativeTv.setText(dialogParams.negativeButtonText);
//        mLineView.setVisibility(!TextUtils.isEmpty(dialogParams.positiveButtonText) && !TextUtils.isEmpty(dialogParams.negativeButtonText) ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(dialogParams.positiveButtonText) && !TextUtils.isEmpty(dialogParams.negativeButtonText)) {
            mLineView.setVisibility(View.VISIBLE);
            mPositiveTv.setBackgroundResource(R.drawable.bg_dialog_btn_bottom_left_white_corners);
            mNegativeTv.setBackgroundResource(R.drawable.bg_dialog_btn_bottom_right_white_corners);
        } else {
            mLineView.setVisibility(View.GONE);
            mPositiveTv.setBackgroundResource(R.drawable.bg_dialog_btn_bottom_white_corners);
            mNegativeTv.setBackgroundResource(R.drawable.bg_dialog_btn_bottom_white_corners);
        }
      /*  mTitleTv.setTextColor(getContext().getResources().getColor(dialogParams.titleTextColor));
        mMessageTvOne.setTextColor(getContext().getResources().getColor(dialogParams.messageTextColor));
        mMessageTvTwo.setTextColor(getContext().getResources().getColor(dialogParams.messageTextColor));*/
        if (dialogParams.btnPosTextColorList != null) {
            mPositiveTv.setTextColor(dialogParams.btnPosTextColorList);
        } else {
            mPositiveTv.setTextColor(getContext().getResources().getColorStateList(R.color.text_color_808080_white));
        }
        if (dialogParams.btnNegTextColorList != null) {
            mNegativeTv.setTextColor(dialogParams.btnNegTextColorList);
        } else {
            mNegativeTv.setTextColor(getContext().getResources().getColorStateList(R.color.text_color_808080_white));
        }
    }

    @OnClick({R.id.layout_common_dialog_positive_tv, R.id.layout_common_dialog_negative_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_common_dialog_positive_tv: {
                if (mDialogParamsList.size() > 0) {
                    DialogParams dialogParams = mDialogParamsList.get(mDialogParamsList.size() - 1);
                    dialogParams.positiveBtnClickListener.onPositiveBtnClicked();
                }
                dismiss();
            }
            break;

            case R.id.layout_common_dialog_negative_tv: {
                if (mDialogParamsList.size() > 0) {
                    DialogParams dialogParams = mDialogParamsList.get(mDialogParamsList.size() - 1);
                    dialogParams.negativeBtnClickListener.onNegativeBtnClicked();
                }
                dismiss();
            }
            break;
        }
    }

    public static class DialogParams {

        private String title;
        private String messageOne;
        private String messageTwo;
        private String positiveButtonText;
        private String negativeButtonText;
        private int titleTextColor = R.color.color_333333;
        private int messageTextColor = R.color.color_333333;
        private int positiveButtonTextColor = R.color.text_color_808080_white;
        private int negativeButtonTextColor = R.color.text_color_157efb_white;
        private ColorStateList btnPosTextColorList = null;
        private ColorStateList btnNegTextColorList = null;
        private OnPositiveBtnClickListener positiveBtnClickListener;
        private OnNegativeBtnClickListener negativeBtnClickListener;
        private boolean canceledOnTouchOutside = true;
        private boolean cancelable = true;
        private int messageGravity = Gravity.CENTER;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessageOne(String message) {
            this.messageOne = message;
        }

        public void setMessageTwo(String message) {
            this.messageTwo = message;
        }

        public void setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
        }


        public void setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
        }

        public void setTitleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
        }

        public void setMessageTextColor(int messageTextColor) {
            this.messageTextColor = messageTextColor;
        }

        public void setPositiveButtonTextColor(int positiveButtonTextColor) {
            this.positiveButtonTextColor = positiveButtonTextColor;
        }

        public void setNegativeButtonTextColor(int negativeButtonTextColor) {
            this.negativeButtonTextColor = negativeButtonTextColor;
        }

        public void setBtnPosTextColorList(ColorStateList btnPosTextColorList) {
            this.btnPosTextColorList = btnPosTextColorList;
        }

        public void setBtnNegTextColorList(ColorStateList btnNegTextColorList) {
            this.btnNegTextColorList = btnNegTextColorList;
        }

        public void setPositiveBtnClickListener(OnPositiveBtnClickListener positiveBtnClickListener) {
            this.positiveBtnClickListener = positiveBtnClickListener;
        }

        public void setNegativeBtnClickListener(OnNegativeBtnClickListener negativeBtnClickListener) {
            this.negativeBtnClickListener = negativeBtnClickListener;
        }

        public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
        }

        public void setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
        }

        public int getMessageGravity() {
            return messageGravity;
        }

        public void setMessageGravity(int messageGravity) {
            this.messageGravity = messageGravity;
        }
    }

    public static class Builder {
        private Context context;
        private DialogParams dialogParams;

        public Builder(Context context) {
            this.context = context;
            dialogParams = new DialogParams();
        }

        public Builder setTitle(int titleId) {
            dialogParams.setTitle(context.getString(titleId));
            return this;
        }

        public Builder setTitle(String title) {
            dialogParams.setTitle(title);
            return this;
        }

        public Builder setMessageOne(int messageId) {
            dialogParams.setMessageOne(context.getString(messageId));
            return this;
        }

        public Builder setMessageTwo(int messageId) {
            dialogParams.setMessageTwo(context.getString(messageId));
            return this;
        }

        public Builder setMessageOne(String message) {
            dialogParams.setMessageOne(message);
            return this;
        }

        public Builder setMessageTwo(String message) {
            dialogParams.setMessageTwo(message);
            return this;
        }

        public Builder setMessageOne(int messageId, int textColorId) {
            dialogParams.setMessageOne(context.getString(messageId));
            dialogParams.setMessageTextColor(textColorId);
            return this;
        }

        public Builder setMessageTwo(int messageId, int textColorId) {
            dialogParams.setMessageTwo(context.getString(messageId));
            dialogParams.setMessageTextColor(textColorId);
            return this;
        }

        public Builder setMessageOne(String message, int textColorId) {
            dialogParams.setMessageOne(message);
            dialogParams.setMessageTextColor(textColorId);
            return this;
        }

        public Builder setMessageTwo(String message, int textColorId) {
            dialogParams.setMessageTwo(message);
            dialogParams.setMessageTextColor(textColorId);
            return this;
        }

        public Builder setPositiveButton(int textId, OnPositiveBtnClickListener listener) {
            dialogParams.setPositiveButtonText(context.getString(textId));
            dialogParams.setPositiveBtnClickListener(listener);
            return this;
        }

        public Builder setNegativeButton(int textId, OnNegativeBtnClickListener listener) {
            dialogParams.setNegativeButtonText(context.getString(textId));
            dialogParams.setNegativeBtnClickListener(listener);
            return this;
        }

        public Builder setPosBtnTextColor(ColorStateList colorStateList) {
            dialogParams.setBtnPosTextColorList(colorStateList);
            return this;
        }

        public Builder setNegBtnTextColor(ColorStateList colorStateList) {
            dialogParams.setBtnNegTextColorList(colorStateList);
            return this;
        }

        public Builder setPosBtnTextColor(int colorId) {
            dialogParams.setPositiveButtonTextColor(colorId);
            return this;
        }

        public Builder setNegBtnTextColor(int colorId) {
            dialogParams.setNegativeButtonTextColor(colorId);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b) {
            dialogParams.setCanceledOnTouchOutside(b);
            return this;
        }

        public Builder setCancelable(boolean b) {
            dialogParams.setCancelable(b);
            return this;
        }

        public Builder setMessageGravity(int gravity) {
            dialogParams.setMessageGravity(gravity);
            return this;
        }

        private CommonDialog create() {
            mDialogParamsList.add(dialogParams);
            CommonDialog dialog = new CommonDialog(context);
            return dialog;
        }

        public CommonDialog show() {
            final CommonDialog dialog = create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(dialogParams.canceledOnTouchOutside);
            dialog.setCancelable(dialogParams.cancelable);
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mDialogParamsList.size() > 0)
                        mDialogParamsList.remove(mDialogParamsList.size() - 1);
                }
            });
            return dialog;
        }
    }

    public interface OnPositiveBtnClickListener {
        void onPositiveBtnClicked();
    }

    public interface OnNegativeBtnClickListener {
        void onNegativeBtnClicked();
    }
}