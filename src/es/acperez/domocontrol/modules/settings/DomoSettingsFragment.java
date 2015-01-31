package es.acperez.domocontrol.modules.settings;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.base.DomoController;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class DomoSettingsFragment extends Fragment {

	protected DomoSettingsController mController;
	private View mView;
	private View mLoadingView;
	private View mSettingsView;
	private View mWarningView;
	private FrameLayout mWarningContentView;
	private FrameLayout mContentView;
	private AnimatorSet mLoadingAnimation = null;
	private AnimatorSet mWarningAnimation = null;
	
	protected abstract View createView(LayoutInflater inflater, ViewGroup container);
	protected abstract void updateServiceSettings(Bundle settings);
	
	protected DomoSettingsFragment(DomoSettingsController controller) {
		mController = controller;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.domo_settings, container, false);
        mLoadingView = mView.findViewById(R.id.settings_loading_panel);
        mSettingsView = mView.findViewById(R.id.settings_content_panel);
        mWarningView = mView.findViewById(R.id.settings_warning_panel);
        
        TextView loadingText = (TextView) mLoadingView.findViewById(R.id.settings_loading_text);
		mLoadingAnimation = DomoControlApplication.setAnimation(loadingText);
		
		mContentView = (FrameLayout)mSettingsView.findViewById(R.id.settings_content);
		View childView = createView(inflater, mContentView);
		mContentView.addView(childView);
		
		mWarningContentView = (FrameLayout)mWarningView.findViewById(R.id.settings_warning_content);
		childView = createWarningView(inflater, mWarningContentView);
		if (childView != null)
			mWarningContentView.addView(childView);
		
		TextView warningText = (TextView) mWarningView.findViewById(R.id.settings_warning_text);
		warningText.setText(getWarningMessage());
		mWarningAnimation = DomoControlApplication.setAnimation(warningText);
		
        updateStatus();
        
        updateServiceSettings(mController.getServiceSettings());
        
        return mView;
    }
	
	protected View createWarningView(LayoutInflater inflater, ViewGroup container) {
		return null;
	}
	
	protected String getWarningMessage() {
		return "";
	}
	
	protected void updateStatus() {

		if (mLoadingView == null || mSettingsView == null)
			return;

		switch(mController.getStatus()) {
			case DomoController.STATUS_LOADING:
				mWarningAnimation.cancel();
				mSettingsView.setVisibility(View.GONE);
				mWarningView.setVisibility(View.GONE);
				if (!mLoadingAnimation.isRunning()) {
					mLoadingAnimation.start();
				}
				mLoadingView.setVisibility(View.VISIBLE);
				break;
			case DomoController.STATUS_ONLINE:
			case DomoController.STATUS_OFFLINE:
				mLoadingAnimation.cancel();
				mWarningAnimation.cancel();
				mSettingsView.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
				mWarningView.setVisibility(View.GONE);
				updateOfflineMessage(mController.getError());
				break;
			case DomoController.STATUS_WARNING:
				mLoadingAnimation.cancel();
				mSettingsView.setVisibility(View.GONE);
				mLoadingView.setVisibility(View.GONE);
				if (!mWarningAnimation.isRunning()) {
					mWarningAnimation.start();
				}
				mWarningView.setVisibility(View.VISIBLE);
				break;
		}
	}
	
	//case DomoController.STATUS_LOADING:
//	{
//		mSettingsView.setVisibility(View.GONE);
//		if (!animation.isRunning()) {
//			animation.start();
//		}
//		
//		TextView t = (TextView) mLoadingView.findViewById(R.id.philips_hue_loading_text);
//		t.setText(getString(R.string.connecting_message));
//		
//		ImageView image = (ImageView) mLoadingView.findViewById(R.id.philips_hue_link_image);
//		image.setVisibility(View.GONE);
//		
//		mLoadingView.setVisibility(View.VISIBLE);
//		break;
//	}
//case DomoController.STATUS_WARNING:
//	TextView t = (TextView)mLoadingView.findViewById(R.id.philips_hue_loading_text);
//	t.setText(getString(R.string.light_press_link));
//	
//	ImageView image = (ImageView)mLoadingView.findViewById(R.id.philips_hue_link_image);
//	image.setVisibility(View.VISIBLE);
//	
//	mLoadingView.setVisibility(View.VISIBLE);
//	mSettingsView.setVisibility(View.GONE);
//	break;
	
	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mSettingsView.findViewById(R.id.settings_status_text);
		
		text.setTextColor(Color.RED);
		
		switch (error) {
			case DomoController.ERROR_NONE:
				text.setTextColor(Color.GREEN);
				text.setText(getString(R.string.settings_status_connected));
				break;
			case DomoController.ERROR_FIND:
				text.setText(getString(R.string.settings_status_find_error));
				break;
			case DomoController.ERROR_NETWORK:
				text.setText(getString(R.string.settings_status_network_error));
				break;
			case DomoController.ERROR_AUTH:
				text.setText(getString(R.string.settings_status_auth_error));
				break;
			default:
				text.setText(getString(R.string.settings_status_unknown_error));
				break;
		}
	}
}
