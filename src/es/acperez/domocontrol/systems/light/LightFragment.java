package es.acperez.domocontrol.systems.light;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.controller.LightDevice;

public class LightFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOfflineView;
	private View mOnlineView;
	private LightSystem mSystem;
	private LightDevice mDevice;
	private AnimatorSet animation;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.light_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.light_loading_panel);
        mOfflineView = mView.findViewById(R.id.light_offline_panel);
        mOnlineView = mView.findViewById(R.id.light_online_panel);
        
		if (mDevice.mServer != null && mDevice.mServer.length() > 0)
			((EditText) mView.findViewById(R.id.light_address)).setText(mDevice.mServer);

		((ImageButton) mView.findViewById(R.id.light_settings_button)).setOnClickListener(mSettingsOpenListener);
		
		((Button) mView.findViewById(R.id.light_connect_with_address)).setOnClickListener(mSettingsConnectListener);
		((Button) mView.findViewById(R.id.light_find)).setOnClickListener(mSettingsFindListener);
		
        ((Button) mView.findViewById(R.id.light_apply_settings)).setOnClickListener(mSettingsApplyListener);
        ((Button) mView.findViewById(R.id.light_cancel_settings)).setOnClickListener(mSettingsCancelListener);
        
        TextView loadingText = (TextView) mLoadingView.findViewById(R.id.light_loading_text);
        animation = DomoControlApplication.setAnimation(loadingText);
        
		((Button) mView.findViewById(R.id.light_test_button)).setOnClickListener(mTestListener);

        updateStatus();
		
        return mView;
    }
	
	private OnClickListener mTestListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSystem.test_connect();
		}
	};

	@Override
	public void updateStatus() {
		
		if (mOfflineView == null || mLoadingView == null || mOnlineView == null)
			return;
		
		switch(mSystem.getStatus()) {
			case DomoSystem.STATUS_LOADING:
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.GONE);
				if (!animation.isRunning()) {
					animation.start();
				}
				mLoadingView.setVisibility(View.VISIBLE);
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
			case DomoSystem.STATUS_WARNING:
				TextView t = (TextView)mLoadingView.findViewById(R.id.light_loading_text);
				t.setText(getString(R.string.light_press_link));
				
				ImageView image = (ImageView)mLoadingView.findViewById(R.id.light_link_image);
				image.setVisibility(View.VISIBLE);
				
				mLoadingView.setVisibility(View.VISIBLE);
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.GONE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mOfflineView.findViewById(R.id.light_monitor_error_text);
		
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
	
	@Override
	public void setSystem(DomoSystem system) {
		this.mSystem = (LightSystem)system;
		this.mDevice = mSystem.mDevice;
	}

	@Override
	public void updateContent() {
	}
	
	private OnClickListener mSettingsOpenListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TableLayout settings = (TableLayout) mView.findViewById(R.id.light_settings_panel);
			settings.setVisibility(View.VISIBLE);
			
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
		    settings.startAnimation(anim);
		}
	};
	
private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSettingsCancelListener.onClick(v);
			
//			mSystem.settingsUpdate();
//			
//			Bundle settings = mSystem.getSettings();
//			DomoControlApplication.savePreferences(getActivity(), settings, DomoSystem.POWER_SETTINGS_NAME);
		}
	};
	
	private OnClickListener mSettingsCancelListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TableLayout settings = (TableLayout) mView.findViewById(R.id.light_settings_panel);
			settings.setVisibility(View.GONE);
			
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
		    settings.startAnimation(anim);
		}
	};
	
	private OnClickListener mSettingsConnectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSettingsCancelListener.onClick(v);
			mDevice.mServer = ((EditText) mView.findViewById(R.id.light_address)).getText().toString();
			mSystem.settingsUpdate();
		}
	};
	
	private OnClickListener mSettingsFindListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSettingsCancelListener.onClick(v);
			mDevice.mServer = null;
			mSystem.settingsUpdate();
		}
	};
}