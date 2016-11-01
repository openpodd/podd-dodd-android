package mylibrary.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import mylibrary.widget.DatePickerDialogFragment;

public class DatePopup extends BaseDialogHelper {
    private Context mContext;

    public DatePopup(Context c) {
        mContext = c;

    }

    public static DatePopup getInstance(Context c) {
        return new DatePopup(c);
    }


    public DialogFragment getDialog() {
        return getPicker();
    }


     DialogFragment getPicker() {
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Bundle b = new Bundle();

        b.putInt(DatePickerDialogFragment.YEAR, year);
        b.putInt(DatePickerDialogFragment.MONTH, month);
        b.putInt(DatePickerDialogFragment.DATE, day);

        DialogFragment picker = new DatePickerDialogFragment();
        picker.setArguments(b);


        return picker;

    }

}
