package es.acperez.domocontrol.systems.power.customviews;

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
import es.acperez.domocontrol.systems.power.PowerData;
import es.acperez.domocontrol.systems.power.PowerEvent;

public class EventList extends LinearLayout {
	ArrayList<EventView> mViews;
	private PowerData mData;
	private OnPowerEventListener mListener;
	
	public interface OnPowerEventListener {
		void onPowerEventRemoved(PowerEvent event);
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
	
	public void init(List<PowerEvent> events) {
		removeAllViews();
		
		mViews.clear();
		
		if (events != null) {
			for (int i = 0; i < events.size(); i++) {
				createView(events.get(i), i);
			}
		}
	}
	
	public void setEventListener(OnPowerEventListener listener) {
		mListener = listener;
	}
	
	public void setData(PowerData data) {
		mData = data;
	}
	
	public void addEvent(PowerEvent event, int index) {
		createView(event, index);
	}
	
	private void createView(PowerEvent event, int index) {
		EventView view = new EventView(getContext(), event);
		mViews.add(index, view);
		addView(view, index);
	}
	
	private void removeEvent(View view) {
		mViews.remove(view);
		removeView(view);
	}
	
	public void updateData() {
		for (int i = 0; i < mViews.size(); i++) {
			mViews.get(i).updateView();
		}
	}
	
	public class EventView extends LinearLayout {
		private PowerEvent mEvent;
		private EventView mView;
		private TextView mTime;
    	private ImageView mAction;
    	private TextView mSocket;
    	private ImageButton mRemoveBtn;
    	
        public EventView(Context context, PowerEvent event) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.power_event_list_item, this, true);
            mView = this;
            mEvent = event;
			
			mTime = (TextView)findViewById(R.id.power_event_list_time);
			mSocket = (TextView)findViewById(R.id.power_event_list_socket);
			mAction = (ImageView)findViewById(R.id.power_event_list_action);
			mRemoveBtn = (ImageButton)findViewById(R.id.power_event_list_delete);
			
			mTime.setText(String.valueOf(event.mTimeStr));
			mSocket.setText(mData.mSocketNanes[event.mSocket]);
			if (event.mAction)
				mAction.setImageResource(R.drawable.status_online);
			else
				mAction.setImageResource(R.drawable.status_offline);
			
			mRemoveBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeEvent(mView);
					if (mListener != null)
						mListener.onPowerEventRemoved(mEvent);
				}
			});
        }
        
        void updateView() {
        	mSocket.setText(mData.mSocketNanes[mEvent.mSocket]);
        }
    }
}