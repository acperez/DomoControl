package es.acperez.domocontrol;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;
 
public class DeviceFragment extends ListFragment {
 
    private OnItemSelectedListener mListener;
    private boolean mDualFragments = false;
    private int mCurPosition = 0;
    private ListAdapter listAdapter;
    
    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_fragment, container, false);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity has implemented the callback interface
        try {
            mListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() 
                    + " must implement OnItemSelectedListener");
        }
    }

    /** This is where we perform setup for the fragment that's either
     * not related to the fragment's layout or must be done after the layout is drawn.
     * Notice that this fragment does not implement onCreateView(), because it extends
     * ListFragment, which includes a ListView as the root view by default, so there's
     * no need to set up the layout.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ControlFragment frag = (ControlFragment) getFragmentManager()
                .findFragmentById(R.id.controlFragment);
        if (frag != null) mDualFragments = true;

        //Current position should survive screen rotations.
        if (savedInstanceState != null) {
            mCurPosition = savedInstanceState.getInt("listPosition");
        }

        populateDevices();
        ListView lv = getListView();
        lv.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling performance

        if (mDualFragments) {
            // Highlight the currently selected item
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        // If showing both fragments, select the appropriate list item by default
        if (mDualFragments) selectPosition(mCurPosition);
    }

    @Override
    public void onDestroyView() {
      super.onDestroyView();
      // Always detach ViewTreeObserver listeners when the view tears down
    }

    public void populateDevices() {
        String devices[] = getResources().getStringArray(R.array.devices_array);
        listAdapter = new ListAdapter(getActivity(), devices, mCurPosition);
        setListAdapter(listAdapter);
//        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.devices_list_item, devices));
    }
    
  
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity via OnItemSelectedListener callback
    	System.out.println("-----------------------------------------");
    	v.setSelected(true);
        mListener.onItemSelected(position);

        View view = l.getChildAt(mCurPosition);
        view.findViewById(R.id.list_item).setBackgroundResource(R.drawable.list_shadow);

		v.findViewById(R.id.list_item).setBackgroundResource(R.drawable.list_selected);
		mCurPosition = position;
    }

    public void selectPosition(int position) {
        if (mDualFragments) {
            ListView lv = getListView();
            lv.setItemChecked(position, true);
        }

        mListener.onItemSelected(position);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("listPosition", mCurPosition);
    }
}