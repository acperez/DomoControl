package es.acperez.domocontrol.mainList;

import java.util.HashMap;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class SystemListFragment extends ListFragment {

	private OnItemSelectedListener mListener;
	private boolean mDualFragments = false;
	private int mCurPosition = 0;
    private static HashMap<View, AnimatorSet> mAnimations = new HashMap<View, AnimatorSet>();


	public interface OnItemSelectedListener {
		public void onItemSelected(int position);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.system_list_fragment, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Check that the container activity has implemented the callback
		// interface
		try {
			mListener = (OnItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnItemSelectedListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ContentFragment frag = (ContentFragment) getFragmentManager()
				.findFragmentById(R.id.controlFragment);
		if (frag != null)
			mDualFragments = true;

		if (savedInstanceState != null) {
			mCurPosition = savedInstanceState.getInt(DomoControlApplication.SYSTEM_SELECTION);
		}

		setListAdapter(new SystemListAdapter(getActivity(), DomoControlApplication.getSystemsName(), mCurPosition, this));

		ListView lv = getListView();
		lv.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling
													// performance

		if (mDualFragments) {
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			selectPosition(mCurPosition);
		}
	}

	@Override
	public void onListItemClick(ListView lv, View view, int position, long id) {
		view.setSelected(true);
		mListener.onItemSelected(position);

		View oldView = lv.getChildAt(mCurPosition);
		oldView.findViewById(R.id.system_list_item).setBackgroundResource(
				R.drawable.list_shadow);

		view.findViewById(R.id.system_list_item).setBackgroundResource(
				R.drawable.list_selected);
		mCurPosition = position;
	}

	public void selectPosition(int position) {
		if (mDualFragments) {
			ListView lv = getListView();
			lv.setItemChecked(position, true);
		}

		mListener.onItemSelected(position);
	}
	
	public void updateStatus(int position, int status) {
		View item = getListView().getChildAt(position);
		updateStatusView(status, item);
	}

	private void updateStatusView(int status, View item) {
		if (item == null)
			return;
		
		View loading = item.findViewById(R.id.system_list_item_loading);
		ImageView image = (ImageView) item.findViewById(R.id.system_list_item_status);
		ImageView warning = (ImageView) item.findViewById(R.id.system_list_item_warning);
		
		
		switch (status) {
			case DomoSystem.STATUS_LOADING:
				loading.setVisibility(View.VISIBLE);
				image.setVisibility(View.GONE);
				warning.setVisibility(View.GONE);
				
				if (mAnimations.containsKey(warning)) { 
					mAnimations.get(warning).cancel();
				}
				break;
				
			case DomoSystem.STATUS_ONLINE:
				loading.setVisibility(View.GONE);
				image.setImageResource(R.drawable.status_online);
				image.setVisibility(View.VISIBLE);
				warning.setVisibility(View.GONE);
				
				if (mAnimations.containsKey(warning)) { 
					mAnimations.get(warning).cancel();
				}
				break;
				
			case DomoSystem.STATUS_OFFLINE:
				loading.setVisibility(View.GONE);
				image.setImageResource(R.drawable.status_offline);
				image.setVisibility(View.VISIBLE);
				warning.setVisibility(View.GONE);

				if (mAnimations.containsKey(warning)) { 
					mAnimations.get(warning).cancel();
				}
				break;
				
			case DomoSystem.STATUS_WARNING:
				loading.setVisibility(View.GONE);
				image.setVisibility(View.GONE);
				warning.setVisibility(View.VISIBLE);
				
				AnimatorSet animation = mAnimations.get(warning);
				if (animation == null) {
					animation = DomoControlApplication.setAnimation(warning);
					mAnimations.put(warning, animation);
				}
				
				if (!animation.isRunning()) {
					mAnimations.get(warning).start();
				}
				break;
		}		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(DomoControlApplication.SYSTEM_SELECTION, mCurPosition);
	}
}