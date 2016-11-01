package mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;

public class BaseDialogHelper {
    protected  Context mContext;
    private Dialog mDialog;
    private String mTag;
    protected DialogHelperListener baseDialogCallback = null;


    public BaseDialogHelper() {

    }

    protected void dialogSubmitted(String str) {
        if (baseDialogCallback != null) {
            baseDialogCallback.onDialogSubmitted(str, getTag());
        }
    }


    protected void dialogCancel() {
        if (baseDialogCallback != null) {
            baseDialogCallback.onDialogCancel(getTag());
        }
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }


    public static interface DialogHelperListener {
        void onDialogSubmitted(String str, String tag);
        void onDialogCancel(String tag);
    }
}
