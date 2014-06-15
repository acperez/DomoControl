package es.acperez.domocontrol;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	private String[] data;
	private LayoutInflater inflater=null;
	private int selection = 0;

	
	public ListAdapter(Activity activity, String[] data, int selection) {
        this.data = data;
        this.selection = selection;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		if (data != null)
			return data.length;
		
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ViewHolder {
        public TextView text;
    }

	@Override
	public View getView(int position, View view, ViewGroup parentView) {
		View v = view;
		ViewHolder holder;
		
		if (view == null) {
			v = inflater.inflate(R.layout.list_row, null);
			
			if (position == selection) {
				v.findViewById(R.id.list_item).setBackgroundResource(R.drawable.list_selected);
			}
			
			holder = new ViewHolder();
			holder.text = (TextView) v.findViewById(R.id.title);
			v.setTag(holder);
			
			
			
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		if (data.length <= 0) {
			holder.text.setText("no data");
		} else {
			holder.text.setText(data[position]);
		}
		
		return v;
	}

	
}
