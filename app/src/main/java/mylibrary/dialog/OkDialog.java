package mylibrary.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class OkDialog {
    private final Context mContext;
    private OkDialogListener callback;
    private String title;
    private String message;
    private String positive;

    public OkDialog setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public OkDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public OkDialog setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    private String negative;

    public OkDialog(Context c) {
        mContext = c;
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onClickOk (dialog, which);
                } else {
                    dialog.dismiss();
                }
            }

        });



        AlertDialog alert = builder.create();
        alert.show();

    }

    public OkDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public void setCallback(OkDialogListener callback) {
        this.callback = callback;
    }

    public interface OkDialogListener {
        public void onClickOk(DialogInterface dialog, int which);

    }
}
