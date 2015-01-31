package es.acperez.domocontrol.mainlist;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import es.acperez.domocontrol.DomoControlActivity;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.base.DomoController;

public class SystemListFragment extends Fragment {

	final public static String SELECTION = "system.selection";	
	
	private OnItemSelectedListener mListener;
	private SystemListAdapter mMonitorsAdapter;
	private SystemListAdapter mActionsAdapter;
	private SystemListAdapter mSettingsAdapter;
	private View mCurrentView;

	public interface OnItemSelectedListener {
		public void onItemSelected(int position);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Activity activity = getActivity();
		
		mListener = (OnItemSelectedListener) activity;
		
		View mView = inflater.inflate(R.layout.system_list_fragment, container, false);
		ListView monitorsList = (ListView) mView.findViewById(R.id.system_monitor_list);
		ListView actionsList = (ListView) mView.findViewById(R.id.system_action_list);
		ListView settingsList = (ListView) mView.findViewById(R.id.system_settings_list);
		
		ArrayList<MenuItem> monitorsSystems = DomoControlActivity.getSystemsName(DomoController.TYPE_MONITOR);
		ArrayList<MenuItem> actionSystems = DomoControlActivity.getSystemsName(DomoController.TYPE_ACTION);
		ArrayList<MenuItem> settingsSystems = DomoControlActivity.getSystemsName(DomoController.TYPE_SETTINGS);
		
		mMonitorsAdapter = new SystemListAdapter(activity, monitorsSystems);
		monitorsList.setAdapter(mMonitorsAdapter);
		monitorsList.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling performance
		monitorsList.setOnItemClickListener(mListClickListener);

		mActionsAdapter = new SystemListAdapter(activity, actionSystems);
		actionsList.setAdapter(mActionsAdapter);
		actionsList.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling performance
		actionsList.setOnItemClickListener(mListClickListener);
		
		mSettingsAdapter = new SystemListAdapter(activity, settingsSystems);
		settingsList.setAdapter(mSettingsAdapter);
		settingsList.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling performance
		settingsList.setOnItemClickListener(mListClickListener);

		monitorsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		settingsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		monitorsList.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutObserver(monitorsList, mMonitorsAdapter.getItemId(0)));
		
		return mView;
	}
	
	private OnItemClickListener mListClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			if (view == mCurrentView)
				return;
			
			view.setSelected(true);
			selectItem(view, (int) id);
		}
	};
	
	public void updateStatus(int id, int status) {
		if (mSettingsAdapter.updateStatus(id, status))
			mSettingsAdapter.notifyDataSetChanged();
	}
	
	private void selectItem(View view, int itemId) {
		mListener.onItemSelected(itemId);
		mCurrentView.findViewById(R.id.system_list_item).setBackgroundResource(R.drawable.list_shadow);
		view.findViewById(R.id.system_list_item).setBackgroundResource(R.drawable.list_selected);
		mCurrentView = view;
	}
	
	private class GlobalLayoutObserver implements OnGlobalLayoutListener {
		private ListView mList;
		private long mSystemId;
		
		public GlobalLayoutObserver(ListView list, long systemId) {
			mList = list;
			mSystemId = systemId;
		}
		
		@Override
		public void onGlobalLayout() {
			mCurrentView = mList.getChildAt(0);
			selectItem(mCurrentView, (int) mSystemId);
		    ViewTreeObserver obs = mList.getViewTreeObserver();
		    obs.removeOnGlobalLayoutListener(this);
		}
		
	}
}