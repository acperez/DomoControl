package es.acperez.domocontrol.systems.power;

import android.animation.AnimatorSet;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.SquareImageView;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;

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
	private View mSelectedTab;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.power_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.power_loading_panel);
        mOnlineView = mView.findViewById(R.id.power_online_panel);
        mPowerContent = mView.findViewById(R.id.power_tab_monitor_content);
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
        
		if (mData.mServer != null && mData.mServer.length() > 0)
			((EditText) mView.findViewById(R.id.power_address)).setText(mData.mServer);
		
		if (mData.mPort != 0)
			((EditText) mView.findViewById(R.id.power_address_port)).setText(String.valueOf(mData.mPort));
		
		if (mData.mPassword.length() > 0)
			((EditText) mView.findViewById(R.id.power_password)).setText(mData.mPassword);

		if (mData.mSocketNanes[0] != null && mData.mSocketNanes[0].length() > 0) {
			((TextView) mView.findViewById(R.id.power_monitor_socket1_text)).setText(mData.mSocketNanes[0]);
			((EditText) mView.findViewById(R.id.power_settings_socket1)).setHint(mData.mSocketNanes[0]);
		}
		if (mData.mSocketNanes[1] != null && mData.mSocketNanes[1].length() > 0) {
			((TextView) mView.findViewById(R.id.power_monitor_socket2_text)).setText(mData.mSocketNanes[1]);
			((EditText) mView.findViewById(R.id.power_settings_socket2)).setHint(mData.mSocketNanes[1]);
		}
		if (mData.mSocketNanes[2] != null && mData.mSocketNanes[2].length() > 0) {
			((TextView) mView.findViewById(R.id.power_monitor_socket3_text)).setText(mData.mSocketNanes[2]);
			((EditText) mView.findViewById(R.id.power_settings_socket3)).setHint(mData.mSocketNanes[2]);
		}
		if (mData.mSocketNanes[3] != null && mData.mSocketNanes[3].length() > 0) {
			((TextView) mView.findViewById(R.id.power_monitor_socket4_text)).setText(mData.mSocketNanes[3]);
			((EditText) mView.findViewById(R.id.power_settings_socket4)).setHint(mData.mSocketNanes[3]);
		}
		
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
			mData.mServer = ((EditText) mView.findViewById(R.id.power_address)).getText().toString();
			mData.mPort = Integer.valueOf(((EditText) mView.findViewById(R.id.power_address_port)).getText().toString());
			mData.mPassword = ((EditText) mView.findViewById(R.id.power_password)).getText().toString();
			
			mSystem.settingsUpdate();
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
			
			Bundle settings = mSystem.getSettings();
			DomoControlApplication.savePreferences(settings, DomoSystem.POWER_SETTINGS_NAME);
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
	public void setSystem(DomoSystem system) {
		this.mSystem = (PowerSystem)system;
		this.mData = mSystem.mData;
	}

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
}