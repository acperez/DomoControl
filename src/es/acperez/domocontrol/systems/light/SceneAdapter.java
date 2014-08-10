package es.acperez.domocontrol.systems.light;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.light.controller.Scene;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SceneAdapter extends BaseAdapter {
	private Context mContext;
	private Scene[] mScenes;
 
	public SceneAdapter(Context context, Scene[] scenes) {
		mContext = context;
		mScenes = scenes;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
			gridView = new View(mContext);
			gridView = inflater.inflate(R.layout.light_scene_item, null);
 
			TextView textView = (TextView) gridView.findViewById(R.id.light_scene_item_text);
			textView.setText(mScenes[position].name);
 
			ImageView imageView = (ImageView) gridView.findViewById(R.id.light_scene_item_image);
			imageView.setImageDrawable(mScenes[position].image);
 
		} else
			gridView = (View) convertView;
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return mScenes.length;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
}