package es.acperez.domocontrol;

import java.util.Map;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class DomoControlApplication extends Application {
	
    private static Context mContext = null;
	
	static {
		System.loadLibrary("DomoControl");
	}
    
	public static native byte[] EncryptData(byte[] data);
    public static native byte[] DecryptData(byte[] data);
    
    @Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}
    
	public static void savePreferences(Bundle settings, String prefName) {
		SharedPreferences sharedPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for (String key : settings.keySet()) {
			editor.putString(key, settings.getString(key));
		}

		editor.commit();
	}
	
	public static Bundle restorePreferences(String prefName) {
		Bundle settings = new Bundle();
		SharedPreferences sharedPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		Map<String, ?> preferences = sharedPref.getAll();
		for (String key : preferences.keySet()) {
			settings.putString(key, (String)preferences.get(key));
		}
		
		return settings;
	}
	
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return buffer.toString();
	}
	
	public static byte[] hexStringToByteArray(String string) {
		byte[] bytes = new byte[string.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			int charPos = i * 2;
			bytes[i] = Integer.decode("0x" + string.substring(charPos, charPos + 2)).byteValue();
		}
		return bytes;
	}
	
	public static AnimatorSet setAnimation(View v) {
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
		fadeOut.setDuration(500);
		
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
		fadeIn.setDuration(1500);
		AnimatorSet animatorSet = new AnimatorSet();

		animatorSet.playSequentially(fadeOut, fadeIn);
		animatorSet.addListener(new AnimatorListener() {
			private boolean mCancel = false;
			
			@Override
			public void onAnimationEnd(Animator animation) {
			    if (!mCancel)
					animation.start();
			}

			@Override
			public void onAnimationStart(Animator animation) {}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCancel = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		return animatorSet;
	}
}
