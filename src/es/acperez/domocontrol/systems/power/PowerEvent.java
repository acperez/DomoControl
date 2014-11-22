package es.acperez.domocontrol.systems.power;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;

public class PowerEvent {
	public static final String ALARM_SOCKET = "socket";
    public static final String ALARM_ACTION = "action";
    
	private long mId;
	private Calendar mTime;
	public String mTimeStr;
	public boolean mAction;
	public int mSocket;
	
	private AlarmManager mAlarmManager;
	private PendingIntent mAlarmIntent;

	public PowerEvent(long id, String timeEvent, boolean action, int socket) {
		mId = id;
		mTimeStr = timeEvent;
		mAction = action;
		mSocket = socket;
		
		Date time = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat(PowerFragment.DATE_PATTERN, Locale.getDefault());
		try {
			time = df.parse(timeEvent);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		
		mTime = Calendar.getInstance();
		mTime.setTime(time);
	}
	
	public Calendar getTime() {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, mTime.get(Calendar.HOUR_OF_DAY));
		time.set(Calendar.MINUTE, mTime.get(Calendar.MINUTE));
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		return time;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}
	
	public void setAlarm(Context context, int requestId) {
		cancelAlarm();
		System.out.println("--------------------------------- " + requestId);

		Calendar time = getTime();
		if (time.getTimeInMillis() < System.currentTimeMillis()) {
			time.add(Calendar.DATE, 1);
		}
			
		Intent intent = new Intent(context, PowerAlarm.class);
		intent.putExtra(ALARM_SOCKET, mSocket);
		intent.putExtra(ALARM_ACTION, mAction);
		System.out.println(mSocket);
		System.out.println(mAction);
		mAlarmIntent = PendingIntent.getBroadcast(context, requestId, intent, 0);
			
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmIntent);
	}
	
	public void cancelAlarm() {
		if (mAlarmManager != null) {
		    mAlarmManager.cancel(mAlarmIntent);
		    mAlarmManager = null;
		}
	}
}
