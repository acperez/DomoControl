package es.acperez.domocontrol.modules.monitors.light;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.ui.colorpicker.ColorPicker;
import es.acperez.domocontrol.common.ui.colorpicker.ColorPicker.OnColorChangeListener;
import es.acperez.domocontrol.database.SceneAdapter;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.monitors.DomoMonitorFragment;
import es.acperez.domocontrol.modules.monitors.light.list.LightList;
import es.acperez.domocontrol.modules.monitors.light.list.LightList.OnLightSelectedListener;
import es.acperez.domocontrol.services.philips.hue.DomoLight;
import es.acperez.domocontrol.services.philips.hue.LightUtils;

public class LightFragment extends DomoMonitorFragment {

	private View mView;
	private View mUnavailableView;
	private View mMonitorView;
	private LightController mController;
	private ColorPicker mColorPanel;
	private View mScenesContent;
	private View mLightsContent;
	private View mSelectedTab;
	private SceneAdapter mSceneAdapter;
	private GridView mSceneGrid;
	private LightList mLightList;
	private RadioGroup mTab;
	
	public LightFragment(LightController controller) {
		super(controller);
		mController = controller;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.light_hue_monitor, container, false);
        mUnavailableView = mView.findViewById(R.id.light_hue_unavailable);
        mMonitorView = mView.findViewById(R.id.light_hue_monitor);
        
        updateServiceStatus(mController.isConnected());
        
        mScenesContent = mView.findViewById(R.id.light_hue_tab_scenes_content);
        mLightsContent = mView.findViewById(R.id.light_hue_tab_lights_content);
        
		// Tab selector
        mTab = (RadioGroup)mView.findViewById(R.id.light_hue_tab);
        mTab.setOnCheckedChangeListener(mTabListener);
        mSelectedTab = mScenesContent;
        
        // Scenes Tab
        mSceneAdapter = new SceneAdapter(getActivity(), mController.getScenes());
        mSceneGrid = (GridView) mView.findViewById(R.id.light_hue_tab_scenes_grid);
        mSceneGrid.setAdapter(mSceneAdapter);
        mSceneGrid.setOnItemClickListener(mSceneSetListener);
        mSceneGrid.setOnItemLongClickListener(mSceneEditListener);
        
        ((Button) mView.findViewById(R.id.light_hue_scenes_switch_on)).setOnClickListener(mSwitchAllListener);
        ((Button) mView.findViewById(R.id.light_hue_scenes_switch_off)).setOnClickListener(mSwitchAllListener);
		
	    ((Button) mView.findViewById(R.id.light_hue_save_scene)).setOnClickListener(mSaveSceneListener);
	    
        // Lights Tab
	    ArrayList<DomoSwitch> lights = mController.getSwitches();
        mLightList = (LightList) mView.findViewById(R.id.light_hue_list_lights);
        mLightList.setListener(mLightSelected);
        mLightList.init(lights);
		
		mColorPanel = (ColorPicker)mView.findViewById(R.id.light_hue_color_panel);
		mColorPanel.setOnColorChangeListener(mColorPickerListener);
		
        ((Button) mView.findViewById(R.id.light_hue_switch_on)).setOnClickListener(mSwitchOnListener);
        ((Button) mView.findViewById(R.id.light_hue_switch_off)).setOnClickListener(mSwitchOffListener);
		
        return mView;
    }
	
    private OnCheckedChangeListener mTabListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.light_hue_tab_scenes:
					if (mSelectedTab == mScenesContent)
						return;
					
					transition(mScenesContent, mSelectedTab, false);
					mSelectedTab = mScenesContent;
					break;
				case R.id.light_hue_tab_lights:
					if (mSelectedTab == mLightsContent)
						return;
					
					if (mSelectedTab == mScenesContent)
						transition(mLightsContent, mSelectedTab, true);
					else
						transition(mLightsContent, mSelectedTab, false);
					mSelectedTab = mLightsContent;
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
	
	private boolean newSelection = false;
	
	private OnLightSelectedListener mLightSelected = new OnLightSelectedListener() {

		@Override
		public void onLightSelected() {
			newSelection = true;
		}
	};
	
	private OnColorChangeListener mColorPickerListener = new OnColorChangeListener() {
		
		@Override
		public void onColorChange(float[] color) {
			ArrayList<DomoLight> lights = mLightList.getSelectedLights();
			if (lights.size() == 0)
				return;
			
			if (newSelection) {
				newSelection = false;
				
				for (DomoLight light : lights) {
					mController.setSwitchStatus(light, !light.status);
				}
			}
			
			mController.updateLights(lights, color);
		}
	};
	
	private OnClickListener mSwitchOnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<DomoLight> lights = mLightList.getSelectedLights();
			
			newSelection = true;
			
			for (DomoLight light : lights)
				mController.setSwitchStatus(light, true);
		}
	};
	
	private OnClickListener mSwitchOffListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<DomoLight> lights = mLightList.getSelectedLights();
			
			newSelection = true;
			
			for (DomoLight light : lights)
				mController.setSwitchStatus(light, false);
		}
	};
	
	private OnClickListener mSwitchAllListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean state = true;
			if (v.getId() == R.id.light_hue_scenes_switch_off) {
				state = false;
				newSelection = true;
			}
			
			for (int i = 0; i < mLightList.getCount(); i++) {
				mController.setSwitchStatus(mLightList.getItem(i), state);
			}
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
	
		View view = View.inflate(getActivity(), R.layout.dialog_input_text, null);

		((TextView) view.findViewById(R.id.dialog_input_text_message)).setText(getString(R.string.light_scene_save_message));

		dialog.setView(view);

		final EditText input = (EditText) view.findViewById(R.id.dialog_input_text_input);

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
		List<PHLight> lights = mController.getSceneLights();
		int[] colors = new int[lights.size()];
		for (int i = 0; i < lights.size(); i++) {
			colors[i] = LightUtils.getColor(lights.get(i));
		}
		
		Scene scene = new Scene(name, colors);
		mController.saveScene(scene);
		mSceneAdapter.addScene(scene);
	}
	
	private OnItemClickListener mSceneSetListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			Scene scene = (Scene)mSceneGrid.getItemAtPosition(position);
			
			int colour = 0;
			for (int i = 0; i < mLightList.getCount(); i++) {
				mController.setColor(mLightList.getLightId(i), scene.states.get(colour));
				if (colour + 1 < scene.states.size()) {
					colour++;
				}
			}
		}
	};
	
	private OnItemLongClickListener mSceneEditListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final Scene scene = (Scene)parent.getItemAtPosition(position);
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.light_scene_delete_title);

			View root = View.inflate(getActivity(), R.layout.dialog_scene_delete, null);
			dialog.setView(root);

			dialog.setPositiveButton(getText(R.string.delete), new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        mController.deleteScene(scene);
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

	@Override
	public void updateContent() {
		if (mView == null)
			return;
		
		mLightList.notifyDataSetChanged();
	}

	public void updateColors(ArrayList<DomoLight> lights) {
		mLightList.updateColor(lights);
	}

	@Override
	public void updateSwitch(DomoSwitch domoSwitch) {
		mLightList.updateLight(domoSwitch);
	}

	@Override
	public void updateServiceStatus(boolean status) {
		if (!status) {
        	mUnavailableView.setVisibility(View.VISIBLE);
        	mMonitorView.setVisibility(View.GONE);
        } else {
        	mUnavailableView.setVisibility(View.GONE);
        	mMonitorView.setVisibility(View.VISIBLE);
        }
	}
}