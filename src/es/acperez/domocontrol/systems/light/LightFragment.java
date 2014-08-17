package es.acperez.domocontrol.systems.light;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.colorpicker.ColorPicker;
import es.acperez.domocontrol.common.customviews.colorpicker.ColorPicker.OnColorChangeListener;
//import es.acperez.domocontrol.common.customviews.colorpicker.ColorPicker.ColorPickerInitListener;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.controller.LightDevice;
import es.acperez.domocontrol.systems.light.controller.Scene;

public class LightFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOfflineView;
	private View mOnlineView;
	private LightSystem mSystem;
	private LightDevice mDevice;
	private AnimatorSet animation;
	private LightAdapter lightAdapter;
	private ColorPicker mColorPanel;
	private View mScenesContent;
	private View mSettingsContent;
	private View mLightsContent;
	private View mSelectedTab;
	private boolean[] mLightsToEdit;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        mView = inflater.inflate(R.layout.light_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.light_loading_panel);
        mOfflineView = mView.findViewById(R.id.light_offline_panel);
        mOnlineView = mView.findViewById(R.id.light_online_panel);
        mScenesContent = mView.findViewById(R.id.light_tab_scenes_content);
        mLightsContent = mView.findViewById(R.id.light_tab_lights_content);
		mSettingsContent = mView.findViewById(R.id.light_tab_settings_content);
        
		TextView loadingText = (TextView) mLoadingView.findViewById(R.id.light_loading_text);
        animation = DomoControlApplication.setAnimation(loadingText);
        
		// Tab selector
        RadioGroup tab = (RadioGroup)mView.findViewById(R.id.light_tab);
        tab.setOnCheckedChangeListener(mTabListener);
        mSelectedTab = mScenesContent;
        
        // Scenes Tab
		Scene[] scenes = DomoControlApplication.getScenes();
		((GridView)mScenesContent).setAdapter(new SceneAdapter(getActivity(), scenes));
		
	     ((Button) mView.findViewById(R.id.light_save_scene)).setOnClickListener(mSaveSceneListener);
             
        // Settings Tab
		if (mDevice.mServer != null && mDevice.mServer.length() > 0)
			((EditText) mView.findViewById(R.id.light_address)).setText(mDevice.mServer);
		
		((Button) mView.findViewById(R.id.light_connect_with_address)).setOnClickListener(mSettingsConnectListener);
		((Button) mView.findViewById(R.id.light_find)).setOnClickListener(mSettingsFindListener);
		
        ((Button) mView.findViewById(R.id.light_apply_settings)).setOnClickListener(mSettingsApplyListener);
        
        // Lights Tab
        ListView lightList = (ListView) mView.findViewById(R.id.light_list_lights);
        lightAdapter = new LightAdapter(getActivity());
		
		updateContent(LightDevice.UPDATE_BRIDGE);
		lightList.setAdapter(lightAdapter);
		mLightsToEdit = new boolean[lightAdapter.getCount()];
		lightList.setOnItemClickListener(mLightSelected);
		
		mColorPanel = (ColorPicker)mView.findViewById(R.id.light_color_panel);
		mColorPanel.setOnColorChangeListener(mColorPickerListener);
		
        ((Button) mView.findViewById(R.id.light_switch_on)).setOnClickListener(mSwitchOnListener);
        ((Button) mView.findViewById(R.id.light_switch_off)).setOnClickListener(mSwitchOffListener);
		
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
	public void updateContent(int what) {
		if (lightAdapter == null || mDevice == null)
			return;
		
		if (what == LightDevice.UPDATE_BRIDGE) {
//			ViewGroup v = (ViewGroup)mView.findViewById(R.id.light_settings_names);
				
			lightAdapter.setData(mDevice.getLights());
//				addSetupLight(v, light.getName());
		} else if (what == LightDevice.UPDATE_LIGHTS) {
			lightAdapter.setData(mDevice.getLights());
			lightAdapter.notifyDataSetChanged();
		}
	}
	
//	private void addSetupLight(ViewGroup parent, String name) {
//		EditText editText = new EditText(getActivity());
//		LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		editText.setLayoutParams(params);
//		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//		editText.setHint(name);
//		editText.setSingleLine(true);
//		editText.setFilters( new InputFilter[] { new InputFilter.LengthFilter(30) } );
//		
//		parent.addView(editText);
//	}	
	
    private OnCheckedChangeListener mTabListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.light_tab_scenes:
					transition(mScenesContent, mSelectedTab, false);
					mSelectedTab = mScenesContent;
					break;
				case R.id.light_tab_lights:
					if (mSelectedTab == mScenesContent)
						transition(mLightsContent, mSelectedTab, true);
					else
						transition(mLightsContent, mSelectedTab, false);
					mSelectedTab = mLightsContent;
					break;
				case R.id.light_tab_setup:
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
	
	private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			mSettingsCancelListener.onClick(v);
//			
//			mSystem.settingsUpdate();
//			
//			Bundle settings = mSystem.getSettings();
//			DomoControlApplication.savePreferences(getActivity(), settings, DomoSystem.POWER_SETTINGS_NAME);
		}
	};
	
	private OnClickListener mSettingsConnectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDevice.mServer = ((EditText) mView.findViewById(R.id.light_address)).getText().toString();
			mSystem.settingsUpdate();
		}
	};
	
	private OnClickListener mSettingsFindListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mDevice.mServer = null;
			mSystem.settingsUpdate();
		}
	};
	
	private OnItemClickListener mLightSelected = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click on item " + position);
			
			CheckBox edit = (CheckBox)view.findViewById(R.id.light_list_edit);
			boolean isChecked = !edit.isChecked();
			
			edit.setChecked(isChecked);
			mLightsToEdit[position] = isChecked;
		}
	};
	
	private OnClickListener mSaveSceneListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			((LightSystem)mSystem).setLightsConfig(mLightsToEdit);
		}
	};
	
	private OnColorChangeListener mColorPickerListener = new OnColorChangeListener() {
		
		@Override
		public void onColorChange(float[] color) {
			((LightSystem)mSystem).setLightsColor(mLightsToEdit, color);
		}
	};
	
	private OnClickListener mSwitchOnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((LightSystem)mSystem).switchLights(mLightsToEdit, true);
		}
	};
	
	private OnClickListener mSwitchOffListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((LightSystem)mSystem).switchLights(mLightsToEdit, false);
		}
	};
}