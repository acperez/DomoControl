package es.acperez.domocontrol.systems.light.controller;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.philips.lighting.model.PHLightState;

public class Scene {

	public int id;
	public String name = null;
	public Drawable image = null;
	public String[] lights = null;
	public ArrayList<PHLightState> states = null;
	public int[] colors;

	private Scene(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Scene(String name, int[] colors) {
		this(-1, name, colors);
	}
	
	public Scene(int id, String name, int[] colors) {
		this(id, name);
		this.colors = colors;

		states = new ArrayList<PHLightState>();
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == 0)
				states.add(LightUtils.createSwitchState(false));
			else {
				float[] hsv = new float[3];
				Color.colorToHSV(colors[i], hsv);
				states.add(LightUtils.createColorState(hsv));
			}
		}
		
		if (colors.length < 2) {
			int color = colors[0];
			colors = new int[]{color, color};
		}
		
		image = createThumb(colors);
	}

	private Drawable createThumb(int[] colors) {
		GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
		drawable.setStroke(2, Color.BLACK);
		drawable.setShape(GradientDrawable.RECTANGLE);
		return drawable;
	}
}
