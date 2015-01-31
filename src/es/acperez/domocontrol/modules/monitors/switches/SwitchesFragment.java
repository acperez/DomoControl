package es.acperez.domocontrol.modules.monitors.switches;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.monitors.DomoMonitorFragment;

public class SwitchesFragment extends DomoMonitorFragment {
	
	private View mView;
	private SwitchesController mController;
	private GridView mGridView;
	private SwitchAdapter mSwitchAdapter;
	
	public SwitchesFragment(SwitchesController controller) {
		super(controller);
		mController =  controller;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (mView != null)
			return mView;
		
        mView = inflater.inflate(R.layout.switches_monitor, container, false);
        
        ArrayList<DomoSwitch> switches = mController.getSwitches();
        mSwitchAdapter = new SwitchAdapter(getActivity(), switches);
        mGridView = (GridView) mView.findViewById(R.id.test_switch_grid);
        mGridView.setAdapter(mSwitchAdapter);
        mGridView.setOnItemClickListener(mGridClickListener);
        mGridView.setOnItemLongClickListener(mGridLongClickListener);
        
        return mView;
    }
	
	private OnItemClickListener mGridClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			DomoSwitch switchItem = (DomoSwitch) mGridView.getItemAtPosition(position);
			mController.setSwitchStatus(switchItem, !switchItem.status);
		}
	};
	
	private OnItemLongClickListener mGridLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			DomoSwitch switchItem = (DomoSwitch) mGridView.getItemAtPosition(position);
			showSaveSceneDialog(switchItem);
			return true;
		}
	};
	
	private void showSaveSceneDialog(final DomoSwitch switchItem) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.switch_edit_name_title);
	
		View view = View.inflate(getActivity(), R.layout.dialog_input_text, null);

		((TextView) view.findViewById(R.id.dialog_input_text_message)).setText(getString(R.string.switch_edit_name_message));
		final EditText input = (EditText) view.findViewById(R.id.dialog_input_text_input);
		input.setHint(switchItem.name);
		
		dialog.setView(view);

		dialog.setPositiveButton(getText(R.string.settings_ok), new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        editSwitchName(switchItem, input.getText().toString());
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
	
	private void editSwitchName(DomoSwitch switchItem, String name) {
		mController.editSwitchName(switchItem, name);
	}
	
	@Override
	public void updateContent() {
		if (mView == null)
			return;
		
		mSwitchAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void updateSwitch(DomoSwitch domoSwitch) {
		updateContent();
	}

	@Override
	public void updateServiceStatus(boolean status) {	
	}
}