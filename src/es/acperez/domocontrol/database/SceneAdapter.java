package es.acperez.domocontrol.database;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.monitors.light.Scene;

public class SceneAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Scene> mScenes;
 
	public SceneAdapter(Context context, ArrayList<Scene> scenes) {
		mContext = context;
		mScenes = scenes;
	}
 
	@Override
	public int getCount() {
		return mScenes.size();
	}
 
	@Override
	public Object getItem(int position) {
		return mScenes.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setData(ArrayList<Scene> scenes) {
		mScenes = scenes;
	}
	
	public void addScene(Scene scene) {
		mScenes.add(scene);
		notifyDataSetChanged();
	}
	
	public void deleteScene(Scene scene) {
		mScenes.remove(scene);
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		SceneView view;
		Scene scene = mScenes.get(position);
		
		if (convertView == null) {	
			view = new SceneView(mContext, scene);
		} else {
			view = (SceneView) convertView;
			view.setName(scene.name);
			view.setThumb(scene.image);
		}
		
		return view;
	}
	
	public class SceneView extends LinearLayout {
    	private TextView mName;
    	private ImageView mImageThumb;
    	
        public SceneView(Context context, Scene scene) {
            super(context);

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_scene_item, this, true);
			
			mName = (TextView)findViewById(R.id.light_scene_item_text);
			setName(scene.name);
 
			mImageThumb = (ImageView)findViewById(R.id.light_scene_item_image);
			setThumb(scene.image);
        }

        private void setName(String name) {
            mName.setText(name);
        }

        private void setThumb(Drawable thumb) {
            mImageThumb.setImageDrawable(thumb);
        }
    }
}