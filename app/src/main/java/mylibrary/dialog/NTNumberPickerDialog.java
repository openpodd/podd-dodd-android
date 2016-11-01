package mylibrary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

public class NTNumberPickerDialog extends  BaseDialogHelper  {
    private final AlertDialog mAlertDialog;
    protected int mCurrentValue = 0;
    private  NumberPickerListener mNumberPickerCallback = null;
    private Context mContext;
    final NumberPicker aNumberPicker;

    public NTNumberPickerDialog setCallback(DialogHelperListener callback) {
        baseDialogCallback = callback;
        return this;
    }

    public static NTNumberPickerDialog getInstance(Context c) {
        return new NTNumberPickerDialog(c);
    }
    public NTNumberPickerDialog setTitle(String title) {
        mAlertDialog.setTitle(title);
        return this;
    }

    public NTNumberPickerDialog(Context context) {
        mContext = context;

        mNumberPickerCallback = new NumberPickerListener() {
            @Override
            public void onNumberPickerSubmit(int input) {
                if (baseDialogCallback != null) {
                    baseDialogCallback.onDialogSubmitted(String.valueOf(input), getTag());
                }
                else {
                    Log.d("NO CALLBACK", "baseDialog");
                }
            }

            @Override
            public void onNumberPickerCancel() {
                if (baseDialogCallback != null) {
                    baseDialogCallback.onDialogCancel(getTag());
                }
                else {
                    Log.d("NO CALLBACK", "baseDialog");
                }

            }
        } ;

        RelativeLayout linearLayout = new RelativeLayout(mContext);
        aNumberPicker = new NumberPicker(mContext);
        aNumberPicker.setMaxValue(500);
        aNumberPicker.setMinValue(1);
        aNumberPicker.setValue(mCurrentValue);
//        aNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        aNumberPicker.setWrapSelectorWheel(true);
        if (mCurrentValue == 0) {
            aNumberPicker.setValue(1);
        }
        else {
            aNumberPicker.setValue(mCurrentValue);
        }

        EditText input = findInput(aNumberPicker);
        TextWatcher tw = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    Integer value = Integer.parseInt(s.toString());
                    if (value >= aNumberPicker.getMinValue()) {
                        aNumberPicker.setValue(value);
                    }
                }
            }
        };

        input.addTextChangedListener(tw);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("Select the number");
        mAlertDialog.setView(linearLayout);
        mAlertDialog
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                mCurrentValue = aNumberPicker.getValue();
                                if (mNumberPickerCallback != null) {
                                    mNumberPickerCallback.onNumberPickerSubmit(aNumberPicker.getValue());
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                if (mNumberPickerCallback != null) {
                                    mNumberPickerCallback.onNumberPickerCancel();
                                }

                                dialog.cancel();
                            }
                        });
        this.mAlertDialog = mAlertDialog.create();
    }

    public void show() {
        mAlertDialog.show();
    }

    public AlertDialog getDialog() {
        return mAlertDialog;
    }

    public NumberPicker getNumberPicker() {
        return aNumberPicker;
    }

    public NTNumberPickerDialog setMaxValue(int i) {
        aNumberPicker.setMaxValue(i);
        return this;
    }


    public NTNumberPickerDialog setMinValue(int i) {
        aNumberPicker.setMinValue(i);
        return this;
    }

    public NTNumberPickerDialog setValue(int i) {
        aNumberPicker.setValue(i);
        return this;
    }


    public static interface NumberPickerListener {
        public void onNumberPickerSubmit(int input);
        public void onNumberPickerCancel();
    }

    //http://stackoverflow.com/questions/18944997/numberpicker-doesnt-work-with-keyboard
    private EditText findInput(ViewGroup np) {
        int count = np.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = np.getChildAt(i);
            if (child instanceof ViewGroup) {
                findInput((ViewGroup) child);
            } else if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }

}
