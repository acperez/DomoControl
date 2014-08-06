package es.acperez.domocontrol.systems.light;

import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHScene;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.ColorPicker.ColorPickerInitListener;
import es.acperez.domocontrol.systems.light.controller.LightDevice;

public class LightFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOfflineView;
	private View mOnlineView;
	private LightSystem mSystem;
	private LightDevice mDevice;
	private AnimatorSet animation;
	private ArrayAdapter<String> lightAdapter;
	private ArrayAdapter<String> groupAdapter;
	private ArrayAdapter<String> sceneAdapter;
	private LinearLayout mColorPanel;
	private ColorPicker mSaturationSelector;
	private ColorPicker mHueSelector;
	private ColorPicker mValueSelector;
	
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
        
        ListView lightList = (ListView) mView.findViewById(R.id.light_list_lights);
		lightAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		ListView groupList = (ListView) mView.findViewById(R.id.light_list_groups);
		groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		ListView sceneList = (ListView) mView.findViewById(R.id.light_list_scenes);
		sceneAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		updateContent();
		lightList.setAdapter(lightAdapter);
		lightList.setOnItemClickListener(mLightSelected);
		groupList.setAdapter(groupAdapter);
		groupList.setOnItemClickListener(mGroupSelected);
		sceneList.setAdapter(sceneAdapter);
		sceneList.setOnItemClickListener(mSceneSelected);
        
		mColorPanel = (LinearLayout) mView.findViewById(R.id.light_color_panel);
		
		mHueSelector = (ColorPicker) mView.findViewById(R.id.light_color_selector);
		mHueSelector.setInitListener(mHueInitListener);
		mHueSelector.setOnSeekBarChangeListener(mHueListener);
		
		mSaturationSelector = (ColorPicker) mView.findViewById(R.id.light_hue_selector);
		mSaturationSelector.setOnSeekBarChangeListener(mSaturationListener);

		mValueSelector = (ColorPicker) mView.findViewById(R.id.light_value_selector);
		mValueSelector.setOnSeekBarChangeListener(mValueListener);
		
        return mView;
    }

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
		if (lightAdapter == null || mDevice == null)
			return;
		
		if (mDevice.lights != null) {
			for (PHLight light : mDevice.lights) {
				lightAdapter.add(light.getName());
			}
		}
		
		if (mDevice.lights != null) {
			for (PHGroup group : mDevice.groups) {
				groupAdapter.add(group.getName());
			}
		}
		
		if (mDevice.lights != null) {
			for (PHScene scene : mDevice.scenes) {
				sceneAdapter.add(scene.getName());
			}
		}
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
	
	private OnItemClickListener mLightSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click on item " + position);
		}
	};
	
	private OnItemClickListener mGroupSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click on item " + position);
		}
	};
	
	private OnItemClickListener mSceneSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click on item " + position);
		}
	};
	
	private ColorPickerInitListener mHueInitListener = new ColorPickerInitListener() {
		
		@Override
		public void onColorViewInit(int color) {
			mColorPanel.setBackgroundColor(color);
			mSaturationSelector.setColor(color);
			mValueSelector.setColor(color);
		}
	};
	
	private OnSeekBarChangeListener mHueListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			int color = ((ColorPicker) seekBar).getColor();
			mColorPanel.setBackgroundColor(color);
			mSaturationSelector.setColor(color);
			mValueSelector.setColor(color);
			mValueSelector.setColor(color);
		}
	};
	
	private OnSeekBarChangeListener mSaturationListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			int color = ((ColorPicker) seekBar).getColor();
			mColorPanel.setBackgroundColor(color);
			mHueSelector.setColor(color);
			mValueSelector.setColor(color);
		}
	};
	
	private OnSeekBarChangeListener mValueListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			int color = ((ColorPicker) seekBar).getColor();
			mColorPanel.setBackgroundColor(color);
			mHueSelector.setColor(color);
			mSaturationSelector.setColor(color);
		}
	};
}