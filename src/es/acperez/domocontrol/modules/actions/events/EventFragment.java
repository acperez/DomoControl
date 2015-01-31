package es.acperez.domocontrol.modules.actions.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.DialogFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.ui.CustomTimePicker;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.actions.DomoActionFragment;
import es.acperez.domocontrol.modules.actions.events.EventList.OnEventListener;

public class EventFragment extends DomoActionFragment {
	private View mView;
	private EventController mController;
	private EventList mEventList;
	private TextView mTimeEvent;
	private Switch mActionEvent;
	private Spinner mSocketEvent;
	private SwitchEventAdapter mSocketAdapter;
	
	public EventFragment(EventController controller) {
		super(controller);
		mController =  controller;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.test_events, container, false);
        
		Switch alarmsEnabled = (Switch) mView.findViewById(R.id.test_event_status);
		alarmsEnabled.setChecked(mController.isAlarmEnabled());
		alarmsEnabled.setOnCheckedChangeListener(alarmsEnabledListener);
		
		mTimeEvent = (TextView) mView.findViewById(R.id.test_event_time);
		DateFormat df = new SimpleDateFormat(DomoEvent.DATE_PATTERN, Locale.getDefault());
		String date = df.format(Calendar.getInstance().getTime());
		mTimeEvent.setText(date);
		mTimeEvent.setOnClickListener(mEventTimerListener);
		
		mActionEvent = (Switch) mView.findViewById(R.id.test_event_action);
		
		ArrayList<DomoSwitch> switches = mController.getSwitches();
		mSocketAdapter = new SwitchEventAdapter(getActivity(), R.layout.spinner_row, switches);
		mSocketEvent = (Spinner) mView.findViewById(R.id.test_event_sockets);
		mSocketEvent.setAdapter(mSocketAdapter);
		
		mEventList = (EventList) mView.findViewById(R.id.test_list_events);
		mEventList.setEventListener(mEventListListener);
		ArrayList<DomoEvent> events = mController.getEvents();
		mEventList.init(events);
		((Button) mView.findViewById(R.id.test_add_event_button)).setOnClickListener(mEventAddListener);
		
        return mView;
    }

	private OnClickListener mEventTimerListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		    DialogFragment newFragment = new CustomTimePicker(mTimePickerListener);
		    newFragment.show(getChildFragmentManager(), "timePicker");
		}
	};
	
	private OnTimeSetListener mTimePickerListener =  new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE,minute);
			
			DateFormat df = new SimpleDateFormat(DomoEvent.DATE_PATTERN, Locale.getDefault());
			String date = df.format(cal.getTime());
			mTimeEvent.setText(date);
		}
	};

	private OnClickListener mEventAddListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DomoSwitch domoSwitch = mSocketAdapter.getSwitch(mSocketEvent.getSelectedItemPosition());
			DomoEvent event = new DomoEvent(0, mTimeEvent.getText().toString(),
		            			mActionEvent.isChecked(), domoSwitch);
			
			int index = mController.addEvent(event);
			mEventList.addEvent(event, index);
		}
	};
	
	private OnEventListener mEventListListener = new OnEventListener() {
		
		@Override
		public void onEventRemoved(DomoEvent event) {
			mController.deleteEvent(event);
		}
	};

	private android.widget.CompoundButton.OnCheckedChangeListener alarmsEnabledListener = new android.widget.CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			mController.enableAlarms(isChecked);
		}
	};

	@Override
	public void updateContent() {
		// TODO Auto-generated method stub
		
	}
}