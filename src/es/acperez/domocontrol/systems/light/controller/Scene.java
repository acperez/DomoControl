package es.acperez.domocontrol.systems.light.controller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class Scene {
	public String name = null;
	public Drawable image = null;
	private int[] colours = null;

	public Scene(String name, int[] colours) {
		this.name = name;
		
		if (colours.length < 2) {
			int colour = colours[0];
			colours = new int[]{colour, colour};
		}
		
		this.colours = colours;
		GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colours);
		drawable.setStroke(2, Color.BLACK);
		drawable.setShape(GradientDrawable.RECTANGLE);
		image = drawable;
	}
	
	public static String[] defNames = {"Blue", "Orange", "Red", "Green", "Sea", "Peach", "Fire", "Forest",
										"Blue", "Orange", "Red", "Green", "Sea", "Peach", "Fire", "Forest"};
	public static int[][] defColours = {{0xFF0000FF}, {0xFFFF9600}, {0xFFFF0000}, {0xFF00FF00},
			                     {0xFF0030FF, 0xFF009CFF, 0xFF0FF1FF},
			                     {0xFFFFA800, 0xFFFF9600, 0xFFFFC600},
			                     {0xFFFF0000, 0xFFFF4545, 0xFFFF1E00},
			                     {0xFF1B8F00, 0xFF30FF00, 0xFF86FF6A},
			                     {0xFF0000FF}, {0xFFFF9600}, {0xFFFF0000}, {0xFF00FF00},
			                     {0xFF0030FF, 0xFF009CFF, 0xFF0FF1FF},
			                     {0xFFFFA800, 0xFFFF9600, 0xFFFFC600},
			                     {0xFFFF0000, 0xFFFF4545, 0xFFFF1E00},
			                     {0xFF1B8F00, 0xFF30FF00, 0xFF86FF6A}};
}
