package es.acperez.domocontrol.widget;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	private static final String ACTION_LIGHT_ON = 
            "es.acperez.domocontrol.action.LIGHT_ON";
	private static final String ACTION_LIGHT_OFF = 
            "es.acperez.domocontrol.action.LIGHT_OFF";
	
	private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	      int[] appWidgetIds) {
		
		for (int i = 0; i < appWidgetIds.length; ++i) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			
			views.setOnClickPendingIntent(R.id.button_light_on, getPendingSelfIntent(context, ACTION_LIGHT_ON));
			views.setOnClickPendingIntent(R.id.button_light_off, getPendingSelfIntent(context, ACTION_LIGHT_OFF));
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_LIGHT_ON.equals(intent.getAction())) {
            WidgetHelper.switchLight(true, context);
            return;
        }
        
        if (ACTION_LIGHT_OFF.equals(intent.getAction())) {
            WidgetHelper.switchLight(false, context);
        }
    }
}