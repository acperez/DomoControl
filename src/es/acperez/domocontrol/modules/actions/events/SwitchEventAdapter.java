package es.acperez.domocontrol.modules.actions.events;

import java.util.ArrayList;

import es.acperez.domocontrol.modules.DomoSwitch;
import android.content.Context;
import android.widget.ArrayAdapter;

public class SwitchEventAdapter extends ArrayAdapter<String> {
	private ArrayList<DomoSwitch> mSwitches;
 
	public SwitchEventAdapter(Context context, int textViewResourceId, ArrayList<DomoSwitch> switches) {
		super(context, textViewResourceId);
		mSwitches = switches;
	}
 
	@Override
	public int getCount() {
		return mSwitches.size();
	}
 
	@Override
	public String getItem(int position) {
		return mSwitches.get(position).name;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public DomoSwitch getSwitch(int position) {
		return mSwitches.get(position);
	}
	
	public void deleteSwitch(DomoSwitch aSwitch) {
		mSwitches.remove(aSwitch);
		notifyDataSetChanged();
	}
}