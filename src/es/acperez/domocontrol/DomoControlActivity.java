package es.acperez.domocontrol;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import es.acperez.domocontrol.power.PowerManager;
import es.acperez.domocontrol.settings.Settings;

public class DomoControlActivity extends Activity implements SystemListFragment.OnItemSelectedListener {
	private DomoControlApplication application;
    
	static ImageView img[];
	static boolean plugs[];
	PowerManager powerManager;
    private boolean mDualFragments = false;
    private boolean mTitlesHidden = false;
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domo_control);
		
		application = (DomoControlApplication)getApplication();
		
        if(savedInstanceState != null) {
            mTitlesHidden = savedInstanceState.getBoolean("devicesHidden");
        }

        ContentFragment frag = (ContentFragment) getFragmentManager()
                .findFragmentById(R.id.controlFragment);
        if (frag != null) mDualFragments = true;
        
        if (mTitlesHidden) {
            getFragmentManager().beginTransaction()
                    .hide(getFragmentManager().findFragmentById(R.id.system_list_fragment)).commit();
        }
		
		
		
		
		img = new ImageView[4];
//		img[0] = (ImageView) findViewById(R.id.img1);
//		img[0].setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				powerManager.setStatus(powerManagerHandler, 0, !plugs[0]);
//			}
//		});
//		
//		img[1] = (ImageView) findViewById(R.id.img2);
//		img[1].setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				powerManager.setStatus(powerManagerHandler, 1, !plugs[1]);
//			}
//		});
//		
//		img[2] = (ImageView) findViewById(R.id.img3);
//		img[2].setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				powerManager.setStatus(powerManagerHandler, 2, !plugs[2]);
//			}
//		});
//		
//		img[3] = (ImageView) findViewById(R.id.img4);
//		img[3].setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				powerManager.setStatus(powerManagerHandler, 3, !plugs[3]);
//			}
//		});
		
		plugs = new boolean[4];
		
		powerManager = new PowerManager();
    	powerManager.getStatus(powerManagerHandler);
	}
		
	static Handler powerManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			switch (m.what) {
				case PowerManager.ERROR_NETWORK:
					break;
				case PowerManager.ERROR_PASSWORD:
					break;
				case PowerManager.ERROR_NONE:
					UpdateUI((boolean[]) m.obj);
					break;
			}
		}
		
		private void UpdateUI(boolean[] status) {
			for (int i = 0; i < status.length; i++) {
				plugs[i] = status[i];
				
				if (status[i] == true)
					img[i].setImageResource(R.drawable.on);
				else
					img[i].setImageResource(R.drawable.off);
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();
	 
	  inflater.inflate(R.menu.action_bar, menu);
	  return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  // Handle presses on the action bar items
	  switch (item.getItemId()) {
	    case R.id.action_settings:
	      createSettingsPopup();
	      return true;
	    default:
	      return super.onOptionsItemSelected(item);
	  }
	}
	
	private void createSettingsPopup() {
	    Rect rect = new Rect();
	    Window win = this.getWindow();
	    win.getDecorView().getWindowVisibleDisplayFrame(rect);
	    int statusBarHeight = rect.top;
	    int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop();
	    int titleBarHeight = contentViewTop - statusBarHeight;

	    FragmentManager fragmentManager = getFragmentManager();
	    Settings newFragment = Settings.newInstance(titleBarHeight);
	    
        newFragment.show(fragmentManager, "fragment_edit_name");
	}

	@Override
	public void onItemSelected(int position) {
        if (!mDualFragments) {
//            // If showing only the TitlesFragment, start the ContentActivity and
//            // pass it the info about the selected item
//            Intent intent = new Intent(this, ContentActivity.class);
//            intent.putExtra("category", category);
//            intent.putExtra("position", position);
//            intent.putExtra("theme", mThemeId);
//            startActivity(intent);
        } else {
            // If showing both fragments, directly update the ContentFragment
            ContentFragment frag = (ContentFragment) getFragmentManager()
                    .findFragmentById(R.id.controlFragment);
            frag.updateControlPanel(position);
        }
	}
	
}