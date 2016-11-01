package mylibrary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.widget.EditText;

import org.cm.podd.urban.report.R;


public class SelectPopupHelper {

    private final Context mContext;
    private AlertDialog mAlertDialog;
    private String mTitle;

    private String mDefaultValue;
    private SelectDialogListener mCallback;


    private boolean mAutoShowKeyboard;

    public void setTitle(String title) {
        this.mTitle = mTitle;
    }

    public static SelectPopupHelper getInstance(Context mContext) {
        return new SelectPopupHelper(mContext);
    }

    public SelectPopupHelper(Context context, String defaultValue, String title) {
        mContext = context;
        mTitle = title;
        mDefaultValue = defaultValue;
        mAutoShowKeyboard = true;
        setCallback(null);
    }


    public void setDefaultValue(String mDefaultValue) {
        this.mDefaultValue = mDefaultValue;
    }

    public SelectPopupHelper(Context context) {
        mAutoShowKeyboard = true;
        mContext = context;
        mTitle = "Enter text";
        mDefaultValue = "";
        setCallback(null);
    }

    public void setAutoShowKeyboard(boolean mAutoShowKeyboard) {
        this.mAutoShowKeyboard = mAutoShowKeyboard;
    }


    public void setCallback(SelectDialogListener mCallback) {
        this.mCallback = mCallback;
    }


    public AlertDialog getDialog() {
        Resources res = mContext.getResources();

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


//        builder.setTitle(mContext.getString(R.string.choose_one));
        builder.setTitle("CHOOSE");

        final CharSequence[] choiceList = { res.getString(R.string.male),
                res.getString(R.string.female) };


        builder.setSingleChoiceItems(
                choiceList,
                1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("CLICKED ON ", String.valueOf(i));
                    }
                }
        );



        mAlertDialog = builder.create();
        return mAlertDialog;

    }



    public static interface SelectDialogListener {
        public void onSetInputProperties(EditText input);
        public void onSubmit(String value);
        public void onCancel(String value);
        public void onDialogDismiss();
    }
}
