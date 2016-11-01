package mylibrary.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class YesNoDialog {
    private final Context mContext;
    private YesNoListener callback;
    private String title;
    private String message;
    private String positive;

    public YesNoDialog setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public YesNoDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public YesNoDialog setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    private String negative;

    public YesNoDialog(Context c) {
        mContext = c;
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onClickYes(dialog, which);
                }
                else {
                    dialog.dismiss();
                }
            }

        });

        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (callback != null) {
                    callback.onClickNo(dialog, which);
                }
                else {
                    dialog.dismiss();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public YesNoDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public void setCallback(YesNoListener callback) {
        this.callback = callback;
    }

    public interface YesNoListener {
        public void onClickYes(DialogInterface dialog, int which);
        public void onClickNo(DialogInterface dialog, int which);
    }
}
