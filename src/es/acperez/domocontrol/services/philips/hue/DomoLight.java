package es.acperez.domocontrol.services.philips.hue;

import es.acperez.domocontrol.modules.DomoSwitch;
import android.graphics.drawable.Drawable;

public class DomoLight extends DomoSwitch {
	public Drawable thumb;
	
	public DomoLight(int aId, boolean aStatus, String aName, int aServiceId, Drawable aThumb) {
		super(aId, aStatus, aName, aServiceId);
		thumb = aThumb;
	}
}
