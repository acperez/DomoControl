package es.acperez.domocontrol.mainList;

import es.acperez.domocontrol.DomoControlActivity;
import es.acperez.domocontrol.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SystemListAdapter extends BaseAdapter {

	private String[] mData;
	private LayoutInflater mInflater;
	private int mSelection = 0;
	private SystemListFragment list = null;

	public SystemListAdapter(Context context, String[] data, int selection, SystemListFragment list) {
		this.mData = data;
		this.mSelection = selection;
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return mData.length;
	}

	@Override
	public Object getItem(int position) {
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView text;
		ProgressBar loading;
		ImageView status;
		ImageView warning;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;

		if (view == null) {

			view = mInflater.inflate(R.layout.system_list_item, null);

			if (position == mSelection) {
				view.findViewById(R.id.system_list_item).setBackgroundResource(
						R.drawable.list_selected);
			}

			holder = new ViewHolder();
			holder.text = (TextView) view.findViewById(R.id.system_list_item_title);
			holder.loading = (ProgressBar) view.findViewById(R.id.system_list_item_loading);
			holder.status = (ImageView) view.findViewById(R.id.system_list_item_status);
			holder.warning = (ImageView) view.findViewById(R.id.system_list_item_warning);			
			
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		int status = DomoControlActivity.getSystemStatus(position);

		list.updateStatus(position, status);
		
		holder.text.setText(mData[position]);

		return view;
	}
}