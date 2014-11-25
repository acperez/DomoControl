package es.acperez.domocontrol.systems.power;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.animation.AnimatorSet;
import android.app.DialogFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.SquareImageView;
import es.acperez.domocontrol.systems.base.DomoEvent;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.service.ServiceData;
import es.acperez.domocontrol.systems.power.customviews.CustomTimePicker;
import es.acperez.domocontrol.systems.power.customviews.EventList;
import es.acperez.domocontrol.systems.power.customviews.EventList.OnPowerEventListener;
import es.acperez.domocontrol.systems.power.service.PowerServiceData;

public class PowerFragment extends SystemFragment {
	private View mView;
	private View mLoadingView;
	private View mOnlineView;
	private PowerData mData;
	private PowerSystem mSystem;
	private AnimatorSet animation = null;
	private RadioGroup mTab;
	private View mPowerContent;
	private View mSettingsContent;
	private View mPowerTimerContent;
	private View mSelectedTab;
	private EventList mEventList;
	private TextView mTimeEvent;
	private Switch mActionEvent;
	private Spinner mSocketEvent;
	private ArrayAdapter<String> mSocketAdapter;
	
	public PowerFragment(PowerSystem system) {
		mSystem =  system;
		mData = system.mData;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.power_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.power_loading_panel);
        mOnlineView = mView.findViewById(R.id.power_online_panel);
        mPowerContent = mView.findViewById(R.id.power_tab_monitor_content);
        mPowerTimerContent = mView.findViewById(R.id.power_tab_timer_content);
		mSettingsContent = mView.findViewById(R.id.power_tab_settings_content);
        
        // Tab selector
        mTab = (RadioGroup)mView.findViewById(R.id.power_tab);
        mTab.setOnCheckedChangeListener(mTabListener);
        mSelectedTab = mSettingsContent;
        if (mSystem.getStatus() == DomoSystem.STATUS_ONLINE) {
            mSelectedTab = mPowerContent;
            ((RadioButton)mView.findViewById(R.id.power_tab_control)).setChecked(true);
            mSettingsContent.setVisibility(View.GONE);
            mPowerContent.setVisibility(View.VISIBLE);
			updateOfflineMessage(DomoSystem.ERROR_NONE);
        }

		((TextView) mView.findViewById(R.id.power_monitor_socket1_text)).setText(mData.mSocketNanes[0]);
		((EditText) mView.findViewById(R.id.power_settings_socket1)).setHint(mData.mSocketNanes[0]);
		((TextView) mView.findViewById(R.id.power_monitor_socket2_text)).setText(mData.mSocketNanes[1]);
		((EditText) mView.findViewById(R.id.power_settings_socket2)).setHint(mData.mSocketNanes[1]);
		((TextView) mView.findViewById(R.id.power_monitor_socket3_text)).setText(mData.mSocketNanes[2]);
		((EditText) mView.findViewById(R.id.power_settings_socket3)).setHint(mData.mSocketNanes[2]);
		((TextView) mView.findViewById(R.id.power_monitor_socket4_text)).setText(mData.mSocketNanes[3]);
		((EditText) mView.findViewById(R.id.power_settings_socket4)).setHint(mData.mSocketNanes[3]);
		
		Switch alarmsEnabled = (Switch)mView.findViewById(R.id.power_event_status);
		alarmsEnabled.setChecked(mData.mAlarmsEnabled);
		alarmsEnabled.setOnCheckedChangeListener(alarmsEnabledListener);
		
		mTimeEvent = (TextView) mView.findViewById(R.id.power_event_time);
		DateFormat df = new SimpleDateFormat(DomoEvent.DATE_PATTERN, Locale.getDefault());
		String date = df.format(Calendar.getInstance().getTime());
		mTimeEvent.setText(date);
		mTimeEvent.setOnClickListener(mEventTimerListener);
		
		mActionEvent = (Switch) mView.findViewById(R.id.power_event_action);
		
		mSocketAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, mData.mSocketNanes);
		mSocketEvent = (Spinner) mView.findViewById(R.id.power_event_sockets);
		mSocketEvent.setAdapter(mSocketAdapter);
		
		mEventList = (EventList) mView.findViewById(R.id.power_list_events);
		mEventList.setData(mData);
		mEventList.setEventListener(mEventListListener);
		ArrayList<PowerEvent> events = mSystem.getEvents();
		mEventList.init(events);
		((Button) mView.findViewById(R.id.power_add_event_button)).setOnClickListener(mEventAddListener);

		((Button) mView.findViewById(R.id.power_connect_settings)).setOnClickListener(mSettingsConnectListener);
        ((Button) mView.findViewById(R.id.power_apply_settings)).setOnClickListener(mSettingsApplyListener);
        ((Button) mView.findViewById(R.id.power_clear_settings)).setOnClickListener(mSettingsClearListener);
        
        TextView loadingText = (TextView) mLoadingView.findViewById(R.id.power_loading_text);
		animation = DomoControlApplication.setAnimation(loadingText);
        
        SquareImageView socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket1_img);
        updatePlugStatus(0, socket);
        socket.setOnClickListener(new OnClickListener() {	
    		@Override
    		public void onClick(View v) {
    			((PowerSystem)mSystem).requestPlugSwitch(0);
    		}
    	});
        
        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket2_img);
        updatePlugStatus(1, socket);
        socket.setOnClickListener(new OnClickListener() {	
    		@Override
    		public void onClick(View v) {
    			((PowerSystem)mSystem).requestPlugSwitch(1);
    		}
    	});
        
        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket3_img);
        updatePlugStatus(2, socket);
        socket.setOnClickListener(new OnClickListener() {	
    		@Override
    		public void onClick(View v) {
    			((PowerSystem)mSystem).requestPlugSwitch(2);
    		}
    	});
        
        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket4_img);
        updatePlugStatus(3, socket);
        socket.setOnClickListener(new OnClickListener() {	
    		@Override
    		public void onClick(View v) {
    			((PowerSystem)mSystem).requestPlugSwitch(3);
    		}
    	});

        updateStatus();
        return mView;
    }
	
	private void updatePlugStatus(int index, SquareImageView view) {
		if (mData.mSockets[index])
			view.setImageResource(R.drawable.on);
		else
			view.setImageResource(R.drawable.off);
	}

	@Override
	public void updateStatus() {
		
		if (mLoadingView == null || mOnlineView == null)
			return;

		switch(mSystem.getStatus()) {
			case DomoSystem.STATUS_LOADING:
				mOnlineView.setVisibility(View.GONE);
				if (!animation.isRunning()) {
					animation.start();
				}
				mLoadingView.setVisibility(View.VISIBLE);
				break;
			case DomoSystem.STATUS_OFFLINE:
				animation.cancel();
				mOnlineView.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
				updateOfflineMessage(mSystem.getError());
								
				((RadioButton)mView.findViewById(R.id.power_tab_setup)).setChecked(true);
				for (int i = 0; i < mTab.getChildCount(); i++) {
					mTab.getChildAt(i).setEnabled(false);
				}
				break;
			case DomoSystem.STATUS_ONLINE:
				animation.cancel();
				mLoadingView.setVisibility(View.GONE);
				updateOfflineMessage(DomoSystem.ERROR_NONE);

				for (int i = 0; i < mTab.getChildCount(); i++) {
					mTab.getChildAt(i).setEnabled(true);
				}
				
				mTab.check(R.id.power_tab_control);
				
				mOnlineView.setVisibility(View.VISIBLE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mOnlineView.findViewById(R.id.power_settings_status);
		
		text.setTextColor(Color.RED);
		
		switch (error) {
			case DomoSystem.ERROR_NONE:
				text.setTextColor(getActivity().getResources().getColor(R.color.gray));
				text.setText(getString(R.string.power_settings_status_ok));
				break;
			case DomoSystem.ERROR_NETWORK:
				text.setText(getString(R.string.power_network_error));
				break;
			case DomoSystem.ERROR_PASSWORD:
				text.setText(getString(R.string.power_loging_error));
				break;
			default:
				text.setText(getString(R.string.network_unknown_error));
				break;
		}
	}
	
    private OnCheckedChangeListener mTabListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.power_tab_control:
					if (mSelectedTab == mPowerContent)
						return;
					
					transition(mPowerContent, mSelectedTab, false);
					mSelectedTab = mPowerContent;
					break;
				case R.id.power_tab_timer:
					if (mSelectedTab == mPowerTimerContent)
						return;
					
					if (mSelectedTab == mPowerContent)
						transition(mPowerTimerContent, mSelectedTab, true);
					else
						transition(mPowerTimerContent, mSelectedTab, false);
					mSelectedTab = mPowerTimerContent;
					break;
				case R.id.power_tab_setup:
					if (mSelectedTab == mSettingsContent)
						return;
					
					transition(mSettingsContent, mSelectedTab, true);
					mSelectedTab = mSettingsContent;
					break;
			}
		}
	};
	
	private void transition(View in, View out, Boolean leftToRight) {
		Animation animIn, animOut;
		
		if (leftToRight) {
			animIn = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
			animOut = AnimationUtils.loadAnimation(getActivity(), R.anim.center_to_right);
		} else {
			animIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
			animOut = AnimationUtils.loadAnimation(getActivity(), R.anim.center_to_left);
		}
		
		final View outView = out;
		
		animOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				outView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		
		in.setVisibility(View.VISIBLE);
		in.startAnimation(animIn);
		out.startAnimation(animOut);
	}
		
	private OnClickListener mSettingsConnectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			String server = ((EditText) mView.findViewById(R.id.power_address)).getText().toString();
			int port = Integer.valueOf(((EditText) mView.findViewById(R.id.power_address_port)).getText().toString());
			String password = ((EditText) mView.findViewById(R.id.power_password)).getText().toString();
			
			mSystem.connect(server, port, password);
		}
	};
	
	private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			String name = ((EditText) mView.findViewById(R.id.power_settings_socket1)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[0] = name;
				((EditText) mView.findViewById(R.id.power_settings_socket1)).setText("");
				((EditText) mView.findViewById(R.id.power_settings_socket1)).setHint(name);
				((TextView) mView.findViewById(R.id.power_monitor_socket1_text)).setText(name);
			}
				
			name = ((EditText) mView.findViewById(R.id.power_settings_socket2)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[1] = name;
				((EditText) mView.findViewById(R.id.power_settings_socket2)).setText("");
				((EditText) mView.findViewById(R.id.power_settings_socket2)).setHint(name);
				((TextView) mView.findViewById(R.id.power_monitor_socket2_text)).setText(name);
			}
				
			name = ((EditText) mView.findViewById(R.id.power_settings_socket3)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[2] = name;
				((EditText) mView.findViewById(R.id.power_settings_socket3)).setText("");
				((EditText) mView.findViewById(R.id.power_settings_socket3)).setHint(name);
				((TextView) mView.findViewById(R.id.power_monitor_socket3_text)).setText(name);
			}
			
			name = ((EditText) mView.findViewById(R.id.power_settings_socket4)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[3] = name;
				((EditText) mView.findViewById(R.id.power_settings_socket4)).setText("");
				((EditText) mView.findViewById(R.id.power_settings_socket4)).setHint(name);
				((TextView) mView.findViewById(R.id.power_monitor_socket4_text)).setText(name);
			}
			
			mEventList.updateData();
			
			mData.exportSettings();
			
			mSocketAdapter.notifyDataSetChanged();
		}
	};
	
	private OnClickListener mSettingsClearListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((EditText) mView.findViewById(R.id.power_settings_socket1)).setText("");
			((EditText) mView.findViewById(R.id.power_settings_socket2)).setText("");
			((EditText) mView.findViewById(R.id.power_settings_socket3)).setText("");
			((EditText) mView.findViewById(R.id.power_settings_socket4)).setText("");	
		}
	};

	@Override
	public void updateContent(int what, Object obj) {
		if (mView == null)
			return;
		
		SquareImageView socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket1_img);
        updatePlugStatus(0, socket);

        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket2_img);
        updatePlugStatus(1, socket);
        
        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket3_img);
        updatePlugStatus(2, socket);
        
        socket = (SquareImageView) mView.findViewById(R.id.power_monitor_socket4_img);
        updatePlugStatus(3, socket);
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
			int socket = mSocketEvent.getSelectedItemPosition();
			PowerEvent event = new PowerEvent(0, mTimeEvent.getText().toString(),
		            			mActionEvent.isChecked(), socket);
			
			int index = mSystem.addEvent(event);
			mEventList.addEvent(event, index);
		}
	};
	
	private OnPowerEventListener mEventListListener = new OnPowerEventListener() {
		
		@Override
		public void onPowerEventRemoved(PowerEvent event) {
			mSystem.deleteEvent(event);
		}
	};

	private android.widget.CompoundButton.OnCheckedChangeListener alarmsEnabledListener = new android.widget.CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			mData.mAlarmsEnabled = isChecked;
			mData.exportSettings();
			
			mSystem.enableAlarms(isChecked);
		}
	};
	

	@Override
	public void updateServiceSettings(ServiceData settings) {
		PowerServiceData conf = (PowerServiceData) settings;
		
		if (conf.mServer != null && conf.mServer.length() > 0)
			((EditText) mView.findViewById(R.id.power_address)).setText(conf.mServer);
		
		if (conf.mPort != 0)
			((EditText) mView.findViewById(R.id.power_address_port)).setText(String.valueOf(conf.mPort));
		
		if (conf.mPassword.length() > 0)
			((EditText) mView.findViewById(R.id.power_password)).setText(conf.mPassword);		
	}
}