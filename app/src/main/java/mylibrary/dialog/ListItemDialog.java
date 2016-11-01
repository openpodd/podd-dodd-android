package mylibrary.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class ListItemDialog extends DialogFragment {
    Context mContext;

    onUserSubmit mCallback;

    private String[] itemList = null;
    AlertDialog.Builder builder;


    public ListItemDialog() {
        super();
    }

    public ListItemDialog setTitle(CharSequence string) {
        builder.setTitle(string);
        return this;
    }

    public void setCallback(onUserSubmit callback) {
        mCallback = callback;
    }

    public ListItemDialog builder(Context context) {
        mContext = context;
        builder =  new AlertDialog.Builder(mContext);
        return this;
    }

    public ListItemDialog setItems(String[] items) {
        itemList = items;
        return this;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder.setItems(itemList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mCallback != null) {
                    mCallback.onSubmitted(which, itemList[which]);
                }
            }
        });
        return builder.create();
    }

    public void show(String tag) {
        FragmentManager manager = ((Activity) mContext).getFragmentManager();
        super.show(manager, tag);
    }

    public static interface onUserSubmit {
        public void onSubmitted(int which, String str);
    }
}
