package es.acperez.domocontrol.systems.light.controller;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class LightUtils {

	public static PHLightState createSwitchState(boolean status) {
		PHLightState state = new PHLightState();
        state.setOn(status);
        
        return state;
	}
	
	public static PHLightState createColorState(float[] color) {
		PHLightState state = new PHLightState();
        state.setOn(true);

        state.setHue(hueCorrection((color[0] * (65535.0f / 360.0f))));
        state.setSaturation((int)(color[1] * 254));
    	state.setBrightness((int)(color[2] * 254));
    	
    	int colorValue = Color.HSVToColor(color);
    	float xy[] = PHUtilities.calculateXY(colorValue, "LCT001");
    	state.setX(xy[0]);
    	state.setY(xy[1]);
    	
        return state;
	}
	
	public static GradientDrawable createThumb(PHLight light) {
		int color = getColor(light);
		
		GradientDrawable thumb = new GradientDrawable();
		thumb.setColor(color);
		thumb.setStroke(1, Color.BLACK);
		
		return thumb;
	}
	
	public static int getColor(PHLight light) {
		float hue = hueRaw(light.getLastKnownLightState().getHue()) / (65535.0f / 360.0f);
		float sat = light.getLastKnownLightState().getSaturation() / 254.0f;
		float brightness = light.getLastKnownLightState().getBrightness() / 254.0f;
		
		float[] hsv = {hue, sat, brightness};
		return Color.HSVToColor(hsv);
	}
	
	public static int hueCorrection(float hue) {
		// Red - Yellow   -> y = 15834 / 10922.5 * x
		// Yellow - Green -> y = (9666 * x + 67369980) / 10922.5
		// Green - Blue   -> y = (21420 * x + 89127600) / 21845
		// Blue - Magenta -> y = (9180 * x + 111409500) / 10922.5
		// Magenta - Red  -> y = (9435 * x + 97483312.5) / 10922.5
		
		if (hue < 10922.5f)
			return Math.round(1.449668116f * hue);
		
		if (hue < 21845)
			return Math.round(0.884962234f * hue + 6168.0f);
		
		if (hue < 43690)
			return Math.round(0.980544747f * hue + 4080.0f);
		
		if (hue < 54612.5f)
			return Math.round(0.840466926f * hue + 10200.0f);
		
		return Math.round(0.86381323f * hue + 8925.0f);
	}

	public static int hueRaw(float hue) {
		if (hue < 15834.5f)
			return Math.round(hue / 1.449668116f);
		
		if (hue < 25500)
			return Math.round(1.129991724f * hue - 6969.788950962f);
		
		if (hue < 46920)
			return Math.round(1.01984127f * hue - 4160.952380952f);
		
		if (hue < 56100)
			return Math.round(1.189814815f * hue - 12136.111111111f);
		
		return Math.round(1.157657658f * hue - 10332.094594595f);
	}
	
//	public static int hueCorrection(float hue) {
//	// Red      #FF0000 (255,0,0)    (0º,100%,100%)        0    0
//	// Yellow   #FFFF00 (255,255,0)  (60º,100%,100%)   10922,5  15834
//	// Green    #00FF00 (0,255,0)    (120º,100%,100%)  21845    25500
//	// Blue     #0000FF (0,0,255)    (240º,100%,100%)  43690    46920
//	// Magenta  #FF00FF (255,0,255)  (300º,100%,100%)  54612,5  56100
//	// Red      #FF0000 (255,0,0)    (300º,100%,100%)  65535    65535
//	
//	double a1 = 3.10134003142978 * Math.pow(10,-19) * Math.pow(hue, 5);
//	double a2 = -5.572016014209 * Math.pow(10,-14) * Math.pow(hue, 4);
//	double a3 = 3.62369615294603 * Math.pow(10,-9) * Math.pow(hue, 3);
//	double a4 = -0.0001041196 * Math.pow(hue, 2);
//	double a5 = 2.2227969787 * hue;
//	return (int)(a1 + a2 + a3 + a4 + a5 - (4.75263561196466 * Math.pow(10, -11)));
//}
}