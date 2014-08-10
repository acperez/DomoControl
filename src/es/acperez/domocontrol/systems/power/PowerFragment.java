package es.acperez.domocontrol.systems.power;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.SquareImageView;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;

public class PowerFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOfflineView;
	private View mOnlineView;
	private PowerData mData;
	private PowerSystem mSystem;
	private AnimatorSet animation = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.power_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.power_loading_panel);
        mOfflineView = mView.findViewById(R.id.power_offline_panel);
        mOnlineView = mView.findViewById(R.id.power_online_panel);
        
		if (mData.mServer != null && mData.mServer.length() > 0)
			((EditText) mView.findViewById(R.id.power_address)).setText(mData.mServer);
		
		if (mData.mPort != 0)
			((EditText) mView.findViewById(R.id.power_address_port)).setText(String.valueOf(mData.mPort));
		
		if (mData.mPassword.length() > 0)
			((EditText) mView.findViewById(R.id.power_password)).setText(mData.mPassword);

		if (mData.mSocketNanes[0] != null && mData.mSocketNanes[0].length() > 0)
			((TextView) mView.findViewById(R.id.power_monitor_socket1_text)).setText(mData.mSocketNanes[0]);
		if (mData.mSocketNanes[1] != null && mData.mSocketNanes[1].length() > 0)
			((TextView) mView.findViewById(R.id.power_monitor_socket2_text)).setText(mData.mSocketNanes[1]);
		if (mData.mSocketNanes[2] != null && mData.mSocketNanes[2].length() > 0)
			((TextView) mView.findViewById(R.id.power_monitor_socket3_text)).setText(mData.mSocketNanes[2]);
		if (mData.mSocketNanes[3] != null && mData.mSocketNanes[3].length() > 0)
			((TextView) mView.findViewById(R.id.power_monitor_socket4_text)).setText(mData.mSocketNanes[3]);
		
		((ImageButton) mView.findViewById(R.id.power_settings_button)).setOnClickListener(mSettingsOpenListener);
		
        ((Button) mView.findViewById(R.id.power_apply_settings)).setOnClickListener(mSettingsApplyListener);
        ((Button) mView.findViewById(R.id.power_cancel_settings)).setOnClickListener(mSettingsCancelListener);
        
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
		
		if (mOfflineView == null || mLoadingView == null || mOnlineView == null)
			return;
		
		switch(mSystem.getStatus()) {
			case DomoSystem.STATUS_LOADING:
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.GONE);
				mLoadingView.setVisibility(View.VISIBLE);
				if (!animation.isRunning()) {
					animation.start();
				}
				break;
			case DomoSystem.STATUS_OFFLINE:
				animation.cancel();
				mOnlineView.setVisibility(View.GONE);
				mLoadingView.setVisibility(View.GONE);
				updateOfflineMessage(mSystem.getError());
				mOfflineView.setVisibility(View.VISIBLE);
				break;
			case DomoSystem.STATUS_ONLINE:
				animation.cancel();
				mLoadingView.setVisibility(View.GONE);
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.VISIBLE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mOfflineView.findViewById(R.id.power_monitor_error_text);
		
		switch (error) {
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
	
	private OnClickListener mSettingsOpenListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TableLayout settings = (TableLayout) mView.findViewById(R.id.power_settings_panel);
			settings.setVisibility(View.VISIBLE);
			
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
		    settings.startAnimation(anim);
		}
	};

	private OnClickListener mSettingsCancelListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TableLayout settings = (TableLayout) mView.findViewById(R.id.power_settings_panel);
			settings.setVisibility(View.GONE);
			
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
		    settings.startAnimation(anim);
		}
	};
	
	private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSettingsCancelListener.onClick(v);
			
			mData.mServer = ((EditText) mView.findViewById(R.id.power_address)).getText().toString();
			mData.mPort = Integer.valueOf(((EditText) mView.findViewById(R.id.power_address_port)).getText().toString());
			mData.mPassword = ((EditText) mView.findViewById(R.id.power_password)).getText().toString();
			
			String name = ((EditText) mView.findViewById(R.id.power_settings_socket1)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[0] = name;
				((TextView) mView.findViewById(R.id.power_monitor_socket1_text)).setText(name);
			}
				
			name = ((EditText) mView.findViewById(R.id.power_settings_socket2)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[1] = name;
				((TextView) mView.findViewById(R.id.power_monitor_socket2_text)).setText(name);
			}
				
			name = ((EditText) mView.findViewById(R.id.power_settings_socket3)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[2] = name;
				((TextView) mView.findViewById(R.id.power_monitor_socket3_text)).setText(name);
			}
			
			name = ((EditText) mView.findViewById(R.id.power_settings_socket4)).getText().toString();
			if (name.length() > 0) {
				mData.mSocketNanes[3] = name;
				((TextView) mView.findViewById(R.id.power_monitor_socket4_text)).setText(name);
			}
			
			mSystem.settingsUpdate();
			
			Bundle settings = mSystem.getSettings();
			DomoControlApplication.savePreferences(settings, DomoSystem.POWER_SETTINGS_NAME);
		}
	};

	@Override
	public void setSystem(DomoSystem system) {
		this.mSystem = (PowerSystem)system;
		this.mData = mSystem.mData;
	}

	@Override
	public void updateContent() {
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