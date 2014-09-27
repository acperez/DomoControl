package es.acperez.domocontrol.systems.light.customviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.R;

public class LightNameList extends LinearLayout {
	ArrayList<LightNameView> mViews;
	
	public LightNameList(Context context) {
		super(context);
		init();
	}

	public LightNameList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LightNameList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mViews = new ArrayList<LightNameView>();
	}
	
	public void init(List<PHLight> lights) {
		removeAllViews();
		
		mViews.clear();
		
		for (int i = 0; i < lights.size(); i++) {
			createView(lights.get(i));
		}
	}
	
	private void createView(PHLight light) {
		LightNameView view = new LightNameView(getContext(), light);
		mViews.add(view);
		addView(view);
	}
	
	public void reset() {
		for (LightNameView view : mViews) {
			view.reset();
		}
	}
	
	public Map<String, String> getNames() {
		Map<String, String> names = new HashMap<String, String>();
		for (LightNameView view : mViews) {
			String name = view.getName();
			if (name.length() > 0)
				names.put(view.mId, name);
		}
		return names;
	}
	
	public class LightNameView extends LinearLayout {
    	private EditText mName;
    	public String mId;
    	
        public LightNameView(Context context, PHLight light) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_name_list_item, this, true);
			
			mName = (EditText)findViewById(R.id.light_name_list_name);
			setName(light.getName());
			
			mId = light.getIdentifier();
        }

        private void setName(String name) {
            mName.setHint(name);
        }
        
        public String getName() {
            return mName.getText().toString();
        }
        
        public void reset() {
        	mName.setText("");
        }
    }
}