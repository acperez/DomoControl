package es.acperez.domocontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ControlFragment extends Fragment {
    public static final String POSICION = "position";
    int position = -1;
    private View mContentView;
    private boolean mListFragment = false;
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

        // Set member variable for whether this fragment is the only one in the activity
        Fragment listFragment = getFragmentManager().findFragmentById(R.id.deviceListFragment);
        mListFragment = listFragment == null ? true : false;

        // Current position and UI visibility should survive screen rotations.
        if (savedInstanceState != null) {
            if (mListFragment) {
                mCurPosition = savedInstanceState.getInt("listPosition");
            }
        }
    }
    
    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("listPosition", mCurPosition);
    }

	public void updateControlPanel(int position2) {
		// TODO Auto-generated method stub
		
	}

}