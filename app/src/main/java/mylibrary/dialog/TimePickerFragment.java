package mylibrary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Dialog mDialog;
    private Activity mContext;
    private TimePickerDialog.OnTimeSetListener mListener;

    int hour;
    int minute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();

        mContext = getActivity();

        Log.d("TimePickerFragment", "onCreateDialog");

        // Create a new instance of TimePickerDialog and return it
        mDialog =  new TimePickerDialog(mContext, this, this.hour, this.minute,
                DateFormat.is24HourFormat(mContext));
        return mDialog;
    }

    public Dialog setTime(int hour, int minute, final Context context) {
        this.hour = hour;
        this.minute = minute;
        mDialog =  new TimePickerDialog(context, this, this.hour, this.minute,
                DateFormat.is24HourFormat(context));
        return mDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if (mListener != null) {
            mListener.onTimeSet(view, hourOfDay, minute);

        }
    }

    public void setCallback(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.mListener = onTimeSetListener;
    }
}
