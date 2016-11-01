package mylibrary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import org.cm.podd.urban.report.R;

import mylibrary.Utils;


public class InputDialogHelper {

    private final Context mContext;
    private AlertDialog mAlertDialog;
    private String mTitle;

    private String mDefaultValue;


    private InputDialogListener mCallback;


    private boolean mAutoShowKeyboard;

    public void setTitle(String title) {
        this.mTitle = mTitle;
    }

    public InputDialogHelper(Context context, String defaultValue, String title) {
        mContext = context;
        mTitle = title;
        mDefaultValue = defaultValue;
        mAutoShowKeyboard = true;
        setCallback(null);
    }


    public void setDefaultValue(String mDefaultValue) {
        this.mDefaultValue = mDefaultValue;
    }

    public InputDialogHelper(Context context) {
        mAutoShowKeyboard = true;
        mContext = context;
        mTitle = "Enter text";
        mDefaultValue = "";
        setCallback(null);
    }

    public void setAutoShowKeyboard(boolean mAutoShowKeyboard) {
        this.mAutoShowKeyboard = mAutoShowKeyboard;
    }


    public void setCallback(InputDialogListener mCallback) {
        this.mCallback = mCallback;
    }


    public AlertDialog getInputDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(mTitle);

        final EditText input = new EditText(mContext);
        input.setSelectAllOnFocus(true);
        input.setText(mDefaultValue);

        if (mCallback != null) {
            mCallback.onSetInputProperties(input);
        }

        if (mAutoShowKeyboard) {
            Utils.showKeyboard(mContext, input);
        }

        alert.setView(input);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String inputText = String.valueOf(input.getText());
                if (mAutoShowKeyboard) {
                    Utils.hideKeyboard(mContext, input);
                }

                if (mCallback != null) {
                    setDefaultValue(inputText);
                    mCallback.onSubmit(inputText);
                    mCallback.onDialogDismiss();
                }

            }
        });

        alert.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                if (mAutoShowKeyboard) {
                    Utils.hideKeyboard(mContext, input);
                }

               if (mCallback != null) {
                   mCallback.onDialogDismiss();
               }
            }
        });


        mAlertDialog = alert.create();
        return mAlertDialog;

    }

    public AlertDialog getAlertDialog() {
        mAlertDialog =  getInputDialog();
        return mAlertDialog;
    }

    public static interface InputDialogListener {
        public void onSetInputProperties(EditText input);
        public void onSubmit(String value);
        public void onCancel(String value);
        public void onDialogDismiss();
    }
}
