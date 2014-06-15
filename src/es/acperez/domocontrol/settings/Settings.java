package es.acperez.domocontrol.settings;

import android.app.DialogFragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.acperez.domocontrol.R;

public class Settings extends DialogFragment {
	private int mBarSize;

	public static Settings newInstance(int barSize) {
	    Settings f = new Settings();

	    // Supply num input as an argument.
	    Bundle args = new Bundle();
	    args.putInt("barSize", barSize);
	    f.setArguments(args);

	    return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBarSize = getArguments().getInt("barSize");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	        final Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.settings, container, false);
        getDialog().setTitle(getString(R.string.settings_title));                

        return view;
//	    final View v = inflater.inflate(R.layout.login_dialog, container, false);
//	    final EditText emailEditText = (EditText)v.findViewById(R.id.emailEditText);
//	    final EditText passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
//	    final Button loginButton = (Button)v.findViewById(R.id.loginButton);
//	    loginButton.setOnClickListener(new OnClickListener() {
//	            @Override
//	            public void onClick(final View v) {
//	                checkLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
//	            }
//	        });
//	    return v;
	}
	
	
	@Override
	public void onStart()
	{
	  super.onStart();
	  
	  Rect windowSize = new Rect();
	  getDialog().getWindow().getWindowManager().getDefaultDisplay().getRectSize(windowSize);
	  getDialog().getWindow().setLayout(windowSize.width(), windowSize.height() - mBarSize);

	}
}