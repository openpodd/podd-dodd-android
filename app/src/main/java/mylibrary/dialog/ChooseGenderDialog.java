package mylibrary.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import org.cm.podd.urban.report.R;


public class ChooseGenderDialog extends DialogFragment {
    Context mContext;

    OnGenderChanged mCallback;

    public void setCallback(OnGenderChanged callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.choose_gender_title))
                .setItems(R.array.gender_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        String[] genderList = getResources().getStringArray(R.array.gender_list);
                        if (mCallback == null) {
                            OnGenderChanged listener = null;
                            try {
                                listener = (OnGenderChanged) mContext;
                                ((OnGenderChanged) mContext).onSubmitted(genderList[which]);
                            }
                            catch (ClassCastException exception)
                            {
                                throw exception;
                            }
                        }
                        else {
                            mCallback.onSubmitted(genderList[which]);
                        }
                    }
                });
        return builder.create();
    }

    public static interface OnGenderChanged {
        public void onSubmitted(String str);
    }
}
