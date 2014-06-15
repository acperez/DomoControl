package es.acperez.domocontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SystemListAdapter extends BaseAdapter {

	private String[] mData;
	private LayoutInflater mInflater;
	private int mSelection = 0;

	public SystemListAdapter(Context context, String[] data, int selection) {
		this.mData = data;
		this.mSelection = selection;
		mInflater = LayoutInflater.from(context);
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
			holder.text = (TextView) view
					.findViewById(R.id.system_list_item_title);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.text.setText(mData[position]);

		return view;
	}
}