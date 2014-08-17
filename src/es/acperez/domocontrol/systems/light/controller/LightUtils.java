package es.acperez.domocontrol.systems.light.controller;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightType;
import com.philips.lighting.model.PHLightState;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class LightUtils {

	public static PHLightState createSwitchState(boolean status, PHLight light) {
		PHLightState state = new PHLightState();
        state.setOn(status);
        
        return state;
	}
	
	public static PHLightState createColorState(float[] color, PHLight light) {
		PHLightState state = new PHLightState();
        state.setOn(true);
        
        if (light.getLightType() == PHLightType.COLOR_LIGHT) {
        	state.setHue((int)(color[0] * 195.5f));
        	state.setSaturation((int)color[1] * 254);
        	state.setBrightness((int)color[2] * 254);
        	
        } else {
        	int colorValue = Color.HSVToColor(color);
        	float xy[] = PHUtilities.calculateXY(colorValue, light.getModelNumber());
        	state.setX(xy[0]);
        	state.setY(xy[1]);
        	
        }
        
        return state;
	}

	public static GradientDrawable createThumb(PHLight light) {
		float hue = (light.getLastKnownLightState().getHue() / 65535.0f) * 360.0f;
		float sat = light.getLastKnownLightState().getSaturation() / 255.0f;
		float brightness = light.getLastKnownLightState().getBrightness() / 255.0f;
		
		float[] hsv = {hue, sat, brightness};
		
		GradientDrawable thumb = new GradientDrawable();
		thumb.setColor(Color.HSVToColor(hsv));
		thumb.setStroke(1, Color.BLACK);
		
		return thumb;
	}
}
