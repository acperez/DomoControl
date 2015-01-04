package es.acperez.domocontrol.systems.wemo;

import android.animation.AnimatorSet;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import es.acperez.domocontrol.systems.light.service.ServiceData;
import es.acperez.domocontrol.systems.power.PowerSystem;

public class WemoFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOnlineView;
	private View mWemoContent;
	private View mSettingsContent;
	private WemoSystem mSystem;
	private WemoData mData;
	
	private RadioGroup mTab;
	private View mSelectedTab;
	private AnimatorSet animation = null;


	public WemoFragment(WemoSystem system) {
		mSystem =  system;
		mData = system.mData;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        mView = inflater.inflate(R.layout.wemo_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.wemo_loading_panel);
        mOnlineView = mView.findViewById(R.id.wemo_online_panel);
        mWemoContent = mView.findViewById(R.id.wemo_tab_monitor_content);
		mSettingsContent = mView.findViewById(R.id.wemo_tab_settings_content);
		
		// Tab selector
        mTab = (RadioGroup)mView.findViewById(R.id.wemo_tab);
        mTab.setOnCheckedChangeListener(mTabListener);
        mSelectedTab = mSettingsContent;
        if (mSystem.getStatus() == DomoSystem.STATUS_ONLINE) {
            mSelectedTab = mWemoContent;
            ((RadioButton)mView.findViewById(R.id.wemo_tab_control)).setChecked(true);
            mSettingsContent.setVisibility(View.GONE);
            mWemoContent.setVisibility(View.VISIBLE);
			updateOfflineMessage(DomoSystem.ERROR_NONE);
        }
		
		((TextView) mView.findViewById(R.id.wemo_monitor_socket_text)).setText(mData.mName);
		((TextView) mView.findViewById(R.id.wemo_device_address)).setHint(mData.mDevice.mBaseURL.getHost());
		((EditText) mView.findViewById(R.id.wemo_device_name)).setHint(mData.mName);
		
        TextView loadingText = (TextView) mLoadingView.findViewById(R.id.wemo_loading_text);
		animation = DomoControlApplication.setAnimation(loadingText);

		SquareImageView socket = (SquareImageView) mView.findViewById(R.id.wemo_monitor_socket_img);
        updatePlugStatus(socket);
        socket.setOnClickListener(new OnClickListener() {	
    		@Override
    		public void onClick(View v) {
    			mSystem.requestPlugSwitch();
    		}
    	});
        
        ((Button) mView.findViewById(R.id.wemo_apply_settings)).setOnClickListener(mSettingsApplyListener);
        ((Button) mView.findViewById(R.id.wemo_clear_settings)).setOnClickListener(mSettingsClearListener);
        
        updateStatus();
        return mView;
    }
	
	private void updatePlugStatus(SquareImageView view) {
		if (mData.mStatus)
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
								
				((RadioButton)mView.findViewById(R.id.wemo_tab_setup)).setChecked(true);
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
				
				mTab.check(R.id.wemo_tab_control);
				
				mOnlineView.setVisibility(View.VISIBLE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mOnlineView.findViewById(R.id.wemo_settings_status);
		
		text.setTextColor(Color.RED);
		
		switch (error) {
			case DomoSystem.ERROR_NONE:
				text.setTextColor(getActivity().getResources().getColor(R.color.gray));
				text.setText(getString(R.string.power_settings_status_ok));
				break;
			case DomoSystem.ERROR_FIND:
				text.setText(getString(R.string.find_error));
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
				case R.id.wemo_tab_control:
					if (mSelectedTab == mWemoContent)
						return;
					
					transition(mWemoContent, mSelectedTab, false);
					mSelectedTab = mWemoContent;
					break;
					
				case R.id.wemo_tab_setup:
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
	
	@Override
	public void updateContent(int what, Object obj) {
		if (mView == null)
			return;
		
		((TextView) mView.findViewById(R.id.wemo_device_address)).setHint(mData.mDevice.mBaseURL.getHost());
		((EditText) mView.findViewById(R.id.wemo_device_name)).setHint(mData.mName);
		SquareImageView socket = (SquareImageView) mView.findViewById(R.id.wemo_monitor_socket_img);
        updatePlugStatus(socket);
	}

	@Override
	public void updateServiceSettings(ServiceData settings) {
		// TODO Auto-generated method stub
	}
	
	private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			String name = ((EditText) mView.findViewById(R.id.wemo_device_name)).getText().toString();
			if (name.length() > 0)
				mData.mName = name;
			
			mData.exportSettings(mData.mDevice);
			
			((EditText) mView.findViewById(R.id.wemo_device_name)).setHint(mData.mName);
			((EditText) mView.findViewById(R.id.wemo_device_name)).setText(mData.mName);
			((TextView) mView.findViewById(R.id.wemo_monitor_socket_text)).setText(name);
		}
	};
	
	private OnClickListener mSettingsClearListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((EditText) mView.findViewById(R.id.wemo_device_name)).setText("");
		}
	};
}
