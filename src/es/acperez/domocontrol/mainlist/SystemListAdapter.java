package es.acperez.domocontrol.mainlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import es.acperez.domocontrol.DomoControlActivity;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.base.DomoController;

public class SystemListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MenuItem> mData;
    private HashMap<View, AnimatorSet> mAnimations = new HashMap<View, AnimatorSet>();

	public SystemListAdapter(Context context, ArrayList<MenuItem> data) {
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).id;
	}

	public boolean updateStatus(int id, int status) {
		Iterator<MenuItem> iterator = mData.iterator();
		while (iterator.hasNext()) {
			MenuItem item = iterator.next();
			if (item.id == id) {
				item.status = status;
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		MenuView menuView;

		if (view == null) {
			menuView = new MenuView(mContext, mData.get(position));
		} else {
			menuView = (MenuView) view;
		}

		int status = DomoControlActivity.getSystemStatus(mData.get(position).id);
		menuView.setStatusView(status);

		return menuView;
	}
	
	public class MenuView extends LinearLayout {
		private TextView text;
		private ProgressBar loading;
		private ImageView statusImage;
		private ImageView warning;
    	
        public MenuView(Context context, MenuItem menuItem) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.system_list_item, this, true);
			
            text = (TextView) findViewById(R.id.system_list_item_title);
    		text.setText(menuItem.name);

			loading = (ProgressBar) findViewById(R.id.system_list_item_loading);
			statusImage = (ImageView) findViewById(R.id.system_list_item_status);
			warning = (ImageView) findViewById(R.id.system_list_item_warning);	
        }

        private void setStatusView(int status) {
			
    		switch (status) {
    			case DomoController.STATUS_NONE:
    				text.setPadding(12, 0, 0, 0);
    				break;
    				
    			case DomoController.STATUS_LOADING:
    				loading.setVisibility(View.VISIBLE);
    				statusImage.setVisibility(View.GONE);
    				warning.setVisibility(View.GONE);
    				
    				if (mAnimations.containsKey(warning)) { 
    					mAnimations.get(warning).cancel();
    				}
    				break;
    				
    			case DomoController.STATUS_ONLINE:
    				loading.setVisibility(View.GONE);
    				statusImage.setImageResource(R.drawable.status_online);
    				statusImage.setVisibility(View.VISIBLE);
    				warning.setVisibility(View.GONE);
    				
    				if (mAnimations.containsKey(warning)) { 
    					mAnimations.get(warning).cancel();
    				}
    				break;
    				
    			case DomoController.STATUS_OFFLINE:
    				loading.setVisibility(View.GONE);
    				statusImage.setImageResource(R.drawable.status_offline);
    				statusImage.setVisibility(View.VISIBLE);
    				warning.setVisibility(View.GONE);

    				if (mAnimations.containsKey(warning)) { 
    					mAnimations.get(warning).cancel();
    				}
    				break;
    				
    			case DomoController.STATUS_WARNING:
    				loading.setVisibility(View.GONE);
    				statusImage.setVisibility(View.GONE);
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
    }
}