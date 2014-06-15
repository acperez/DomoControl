package es.acperez.domocontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContentFragment extends Fragment {
	int mPosition = -1;
	private View mContentView;
	private boolean mDualFragments = false;
	private int mCurPosition = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.home, null);

		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set member variable for whether this fragment is the only one in the
		// activity
		Fragment listFragment = getFragmentManager().findFragmentById(
				R.id.system_list_fragment);
		mDualFragments = listFragment == null ? true : false;

		// Current position and UI visibility should survive screen rotations.
		if (savedInstanceState != null) {
			if (mDualFragments) {
				mCurPosition = savedInstanceState
						.getInt(DomoControlApplication.SYSTEM_SELECTION);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(DomoControlApplication.SYSTEM_SELECTION, mCurPosition);
	}

	public void updateControlPanel(int position2) {
		// TODO Auto-generated method stub

	}

}