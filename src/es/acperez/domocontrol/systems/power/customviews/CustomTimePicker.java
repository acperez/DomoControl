package es.acperez.domocontrol.systems.power.customviews;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;

public class CustomTimePicker extends DialogFragment {

	private OnTimeSetListener mTimePickerListener;

	public CustomTimePicker(OnTimeSetListener timePickerListener) {
		mTimePickerListener = timePickerListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), mTimePickerListener, hour, minute, false);
	}
}
