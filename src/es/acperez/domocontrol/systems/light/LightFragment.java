package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.colorpicker.ColorPicker;
import es.acperez.domocontrol.common.customviews.colorpicker.ColorPicker.OnColorChangeListener;
import es.acperez.domocontrol.database.SceneAdapter;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.controller.LightRequest;
import es.acperez.domocontrol.systems.light.controller.LightUtils;
import es.acperez.domocontrol.systems.light.controller.Scene;
import es.acperez.domocontrol.systems.light.controller.SceneRequest;
import es.acperez.domocontrol.systems.light.customviews.LightList;
import es.acperez.domocontrol.systems.light.customviews.LightList.OnLightSelectedListener;

public class LightFragment extends SystemFragment {

	private View mView;
	private View mLoadingView;
	private View mOnlineView;
	private LightSystem mSystem;
	private AnimatorSet animation;
	private ColorPicker mColorPanel;
	private View mScenesContent;
	private View mSettingsContent;
	private View mLightsContent;
	private View mSelectedTab;
	private ArrayList<String> mLightIdList;
	private SceneAdapter mSceneAdapter;
	private GridView mSceneGrid;
	private LightList mLightList;
	private ListView mLightNamesList;
	private LightNamesAdapter mLightNamesAdapter;
	private RadioGroup mTab;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        mView = inflater.inflate(R.layout.light_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.light_loading_panel);
        mOnlineView = mView.findViewById(R.id.light_online_panel);
        mScenesContent = mView.findViewById(R.id.light_tab_scenes_content);
        mLightsContent = mView.findViewById(R.id.light_tab_lights_content);
		mSettingsContent = mView.findViewById(R.id.light_tab_settings_content);
        
		TextView loadingText = (TextView) mLoadingView.findViewById(R.id.light_loading_text);
        animation = DomoControlApplication.setAnimation(loadingText);
        
		// Tab selector
        mTab = (RadioGroup)mView.findViewById(R.id.light_tab);
        mTab.setOnCheckedChangeListener(mTabListener);
        mSelectedTab = mSettingsContent;
        if (mSystem.getStatus() == DomoSystem.STATUS_ONLINE) {
            mSelectedTab = mScenesContent;
            ((RadioButton)mView.findViewById(R.id.light_tab_scenes)).setChecked(true);
            mSettingsContent.setVisibility(View.GONE);
            mScenesContent.setVisibility(View.VISIBLE);
			updateOfflineMessage(DomoSystem.ERROR_NONE);
			((View)mView.findViewById(R.id.light_settings_names_panel)).setVisibility(View.VISIBLE);
        }
        
        // Scenes Tab
        mSceneAdapter = new SceneAdapter(getActivity(), mSystem.getScenes());
        mSceneGrid = (GridView) mView.findViewById(R.id.light_tab_scenes_grid);
        mSceneGrid.setAdapter(mSceneAdapter);
        mSceneGrid.setOnItemClickListener(mSceneSetListener);
        mSceneGrid.setOnItemLongClickListener(mSceneEditListener);
        
        ((Button) mView.findViewById(R.id.light_scenes_switch_on)).setOnClickListener(mSwitchAllListener);
        ((Button) mView.findViewById(R.id.light_scenes_switch_off)).setOnClickListener(mSwitchAllListener);
		
	    ((Button) mView.findViewById(R.id.light_save_scene)).setOnClickListener(mSaveSceneListener);
             
        // Settings Tab
		if (((LightSystem)mSystem).mServer != null && ((LightSystem)mSystem).mServer.length() > 0)
			((EditText) mView.findViewById(R.id.light_address)).setText(((LightSystem)mSystem).mServer);
		
		((Button) mView.findViewById(R.id.light_connect_with_address)).setOnClickListener(mSettingsConnectListener);
		((Button) mView.findViewById(R.id.light_find)).setOnClickListener(mSettingsFindListener);
		
        ((Button) mView.findViewById(R.id.light_apply_settings)).setOnClickListener(mSettingsApplyListener);
        mLightNamesList = (ListView) mView.findViewById(R.id.light_settings_names);
        mLightNamesAdapter = new LightNamesAdapter(getActivity(), null);
        mLightNamesList.setAdapter(mLightNamesAdapter);
        
        // Lights Tab
        mLightList = (LightList) mView.findViewById(R.id.light_list_lights);
        mLightList.setListener(mLightSelected);
        updateContent(LightSystem.UPDATE_BRIDGE, null);
		mLightIdList = new ArrayList<String>();
		
		mColorPanel = (ColorPicker)mView.findViewById(R.id.light_color_panel);
		mColorPanel.setOnColorChangeListener(mColorPickerListener);
		
        ((Button) mView.findViewById(R.id.light_switch_on)).setOnClickListener(mSwitchOnListener);
        ((Button) mView.findViewById(R.id.light_switch_off)).setOnClickListener(mSwitchOffListener);
		
        return mView;
    }

	@Override
	public void updateStatus() {
		
		if (mLoadingView == null || mOnlineView == null)
			return;
		
		switch(mSystem.getStatus()) {
			case DomoSystem.STATUS_LOADING:
				{
					mOnlineView.setVisibility(View.GONE);
					if (!animation.isRunning()) {
						animation.start();
					}
					
					TextView t = (TextView)mLoadingView.findViewById(R.id.light_loading_text);
					t.setText(getString(R.string.connecting_message));
					
					ImageView image = (ImageView)mLoadingView.findViewById(R.id.light_link_image);
					image.setVisibility(View.GONE);
					
					mLoadingView.setVisibility(View.VISIBLE);
					break;
				}
			case DomoSystem.STATUS_OFFLINE:
				animation.cancel();
				mLoadingView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.VISIBLE);
				updateOfflineMessage(mSystem.getError());
				
				((View)mView.findViewById(R.id.light_settings_names_panel)).setVisibility(View.GONE);
				
				((RadioButton)mView.findViewById(R.id.light_tab_setup)).setChecked(true);
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
				
				mTab.check(R.id.light_tab_scenes);
				
				mOnlineView.setVisibility(View.VISIBLE);
				((View)mView.findViewById(R.id.light_settings_names_panel)).setVisibility(View.VISIBLE);
				break;
			case DomoSystem.STATUS_WARNING:
				TextView t = (TextView)mLoadingView.findViewById(R.id.light_loading_text);
				t.setText(getString(R.string.light_press_link));
				
				ImageView image = (ImageView)mLoadingView.findViewById(R.id.light_link_image);
				image.setVisibility(View.VISIBLE);
				
				mLoadingView.setVisibility(View.VISIBLE);
				mOnlineView.setVisibility(View.GONE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		if(!isAdded())
			return;
		
		TextView text = (TextView) mOnlineView.findViewById(R.id.light_settings_status);
		
		text.setTextColor(Color.RED);
		
		switch (error) {
			case DomoSystem.ERROR_NONE:
				text.setTextColor(getActivity().getResources().getColor(R.color.gray));
				text.setText(getString(R.string.light_settings_status_ok));
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
	
	@Override
	public void setSystem(DomoSystem system) {
		this.mSystem = (LightSystem)system;
	}

	@Override
	public void updateContent(int what, Object obj) {
		
		if (mLightList == null)
			return;
		
		if (what == LightSystem.UPDATE_LIGHTS) {
			mLightList.update(mSystem.getLights(), (ArrayList<String>)obj);
			return;
		}
		
		List<PHLight> lights = mSystem.getAllLights();
		if (lights == null)
			return;
		
		if (what == LightSystem.REMOTE_UPDATE_LIGHTS) {
			mLightList.updateAll(lights);
			return;
		}
		
		if (what == LightSystem.UPDATE_BRIDGE) {	
			mLightList.init(lights);
			mLightNamesAdapter.setData(lights);
		}
	}
	
    private OnCheckedChangeListener mTabListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.light_tab_scenes:
					if (mSelectedTab == mScenesContent)
						return;
					
					transition(mScenesContent, mSelectedTab, false);
					mSelectedTab = mScenesContent;
					break;
				case R.id.light_tab_lights:
					if (mSelectedTab == mLightsContent)
						return;
					
					if (mSelectedTab == mScenesContent)
						transition(mLightsContent, mSelectedTab, true);
					else
						transition(mLightsContent, mSelectedTab, false);
					mSelectedTab = mLightsContent;
					break;
				case R.id.light_tab_setup:
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
			((LightSystem)mSystem).mServer = ((EditText) mView.findViewById(R.id.light_address)).getText().toString();
			mSystem.settingsUpdate();
		}
	};
	
	private OnClickListener mSettingsFindListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((LightSystem)mSystem).mServer = null;
			mSystem.settingsUpdate();
		}
	};
	
	private OnLightSelectedListener mLightSelected = new OnLightSelectedListener() {

		@Override
		public void onLightSelected(boolean edit, String lightId) {
			if (edit && !mLightIdList.contains(lightId)) {
				mLightIdList.add(lightId);
				return;
			}
			
			if (!edit && mLightIdList.contains(lightId)) {
				mLightIdList.remove(lightId);
			}
		}
		
	};
	
	private OnColorChangeListener mColorPickerListener = new OnColorChangeListener() {
		
		@Override
		public void onColorChange(float[] color) {
			if (mLightIdList.size() == 0)
				return;
			
			((LightSystem)mSystem).updateLights(new LightRequest(mLightIdList, color));
		}
	};
	
	private OnClickListener mSwitchOnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((LightSystem)mSystem).updateLights(new LightRequest(mLightIdList, true));
		}
	};
	
	private OnClickListener mSwitchOffListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((LightSystem)mSystem).updateLights(new LightRequest(mLightIdList, false));
		}
	};
	
	private OnClickListener mSwitchAllListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean state = false;
			if (v.getId() == R.id.light_scenes_switch_on) {
				state = true;
			}
			
			int size = mLightList.getCount();
			ArrayList<String> lights = new ArrayList<String>(size);
			for (int i = 0; i < size; i++) {
				lights.add(mLightList.getLightId(i));
			}
			
			((LightSystem)mSystem).updateLights(new LightRequest(lights, state));
		}
	};
	
	private OnClickListener mSaveSceneListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showSaveSceneDialog();
		}
	};
	
	private void showSaveSceneDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.light_scene_save_title);
	
		LayoutInflater li = LayoutInflater.from(getActivity());
		View view = li.inflate(R.layout.dialog_scene_save, null);

		dialog.setView(view);

		final EditText input = (EditText) view.findViewById(R.id.scene_save_name);

		dialog.setPositiveButton(getText(R.string.settings_ok), new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        saveScene(input.getText().toString());
		    }
		});
		dialog.setNegativeButton(getText(R.string.settings_cancel), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
	
		dialog.show();
	}
	
	private void saveScene(String name) {
		List<PHLight> lights = ((LightSystem)mSystem).getAllLights();
		int[] colors = new int[lights.size()];
		for (int i = 0; i < lights.size(); i++) {
			colors[i] = LightUtils.getColor(lights.get(i));
		}
		
		Scene scene = new Scene(name, colors);
		mSystem.saveScene(scene);
		mSceneAdapter.addScene(scene);
	}
	
	private OnItemClickListener mSceneSetListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			Scene scene = (Scene)mSceneGrid.getItemAtPosition(position);
			((LightSystem)mSystem).updateLights(new SceneRequest(scene));
		}
	};
	
	private OnItemLongClickListener mSceneEditListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final Scene scene = (Scene)parent.getItemAtPosition(position);
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.light_scene_delete_title);
		
			LayoutInflater li = LayoutInflater.from(getActivity());
			View root = li.inflate(R.layout.dialog_scene_delete, null);

			dialog.setView(root);

			dialog.setPositiveButton(getText(R.string.delete), new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        mSystem.deleteScene(scene);
			        mSceneAdapter.deleteScene(scene);
			    }
			});
			dialog.setNegativeButton(getText(R.string.settings_cancel), new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});
		
			dialog.show();
			
			return true;
		}
	};
}