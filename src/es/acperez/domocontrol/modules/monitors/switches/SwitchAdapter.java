package es.acperez.domocontrol.modules.monitors.switches;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.ui.SquareImageView;
import es.acperez.domocontrol.modules.DomoSwitch;

public class SwitchAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<DomoSwitch> mSwitches;
 
	public SwitchAdapter(Context context, ArrayList<DomoSwitch> switches) {
		mContext = context;
		mSwitches = switches;
	}
 
	@Override
	public int getCount() {
		return mSwitches.size();
	}
 
	@Override
	public Object getItem(int position) {
		return mSwitches.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void deleteSwitch(DomoSwitch aSwitch) {
		mSwitches.remove(aSwitch);
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		SwitchView view;
		DomoSwitch switchItem = mSwitches.get(position);
		
		if (convertView == null) {	
			view = new SwitchView(mContext, switchItem);
		} else {
			view = (SwitchView) convertView;
			view.setName(switchItem.name);
			view.setImage(switchItem.status);
		}
		
		return view;
	}
	
	public class SwitchView extends RelativeLayout {
    	private TextView mName;
    	private SquareImageView mImageThumb;
    	
        public SwitchView(Context context, DomoSwitch aSwitch) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.switch_item, this, true);
			
			mName = (TextView)findViewById(R.id.switch_item_name);
			mImageThumb = (SquareImageView)findViewById(R.id.switch_item_img);

			setName(aSwitch.name); 
			setImage(aSwitch.status);
        }

        private void setName(String name) {
            mName.setText(name);
            mName.setHint(name);
            mImageThumb.setContentDescription(name);
        }
        
        private void setImage(boolean status) {
			if (status)
				mImageThumb.setImageResource(R.drawable.on);
			else
				mImageThumb.setImageResource(R.drawable.off);        	
        }
    }
}