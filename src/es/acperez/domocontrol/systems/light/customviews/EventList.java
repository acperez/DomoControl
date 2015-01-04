package es.acperez.domocontrol.systems.light.customviews;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.light.LightEvent;

public class EventList extends LinearLayout {
	ArrayList<EventView> mViews;
	private OnLightEventListener mListener;
	
	public interface OnLightEventListener {
		void onLightEventRemoved(LightEvent event);
	}
	
	public EventList(Context context) {
		super(context);
		init();
	}

	public EventList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EventList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mViews = new ArrayList<EventView>();
	}
	
	public void init(List<LightEvent> events) {
		removeAllViews();
		
		mViews.clear();
		
		if (events != null) {
			for (int i = 0; i < events.size(); i++) {
				createView(events.get(i), i);
			}
		}
	}
	
	public void setEventListener(OnLightEventListener listener) {
		mListener = listener;
	}
	
	public void addEvent(LightEvent event, int index) {
		createView(event, index);
	}
	
	private void createView(LightEvent event, int index) {
		EventView view = new EventView(getContext(), event);
		mViews.add(index, view);
		addView(view, index);
	}
	
	private void removeEvent(View view) {
		mViews.remove(view);
		removeView(view);
	}
	
	public class EventView extends LinearLayout {
		private LightEvent mEvent;
		private EventView mView;
		private TextView mTime;
    	private ImageView mAction;
    	private ImageButton mRemoveBtn;
    	
        public EventView(Context context, LightEvent event) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_event_list_item, this, true);
            mView = this;
            mEvent = event;
			
			mTime = (TextView)findViewById(R.id.light_event_list_time);
			mAction = (ImageView)findViewById(R.id.light_event_list_action);
			mRemoveBtn = (ImageButton)findViewById(R.id.light_event_list_delete);
			
			mTime.setText(String.valueOf(event.mTimeStr));
			if (event.mAction)
				mAction.setImageResource(R.drawable.status_online);
			else
				mAction.setImageResource(R.drawable.status_offline);
			
			mRemoveBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeEvent(mView);
					if (mListener != null)
						mListener.onLightEventRemoved(mEvent);
				}
			});
        }
    }
}