package es.acperez.domocontrol;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import es.acperez.domocontrol.SystemListFragment.OnItemSelectedListener;
import es.acperez.domocontrol.common.DomoSystem;
import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;
import es.acperez.domocontrol.power.controller.PowerManager;
import es.acperez.domocontrol.settings.Settings;

public class DomoControlActivity extends Activity implements OnItemSelectedListener, DomoSystemStatusListener {
    
    private boolean mDualFragments = false;
    private boolean mSystemsHidden = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domo_control);
		
        if(savedInstanceState != null) {
            mSystemsHidden = savedInstanceState.getBoolean("devicesHidden");
        }

        ContentFragment frag = (ContentFragment) getFragmentManager()
                .findFragmentById(R.id.controlFragment);
        if (frag != null) mDualFragments = true;
        
        if (mSystemsHidden) {
            getFragmentManager().beginTransaction()
                    .hide(getFragmentManager().findFragmentById(R.id.system_list_fragment)).commit();
        }
        
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
		
        InitializeSystemsStatus();
	}

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

	private void InitializeSystemsStatus() {
        DomoControlApplication.addSystemListener(DomoSystem.TYPE_POWER, this);
        DomoControlApplication.sendSystemRequest(DomoSystem.TYPE_POWER, PowerManager.GET_STATUS);
	}

	@Override
	public void onSystemStatusChange(int systemType, int status) {
		// Update status in list item
        SystemListFragment fragment = (SystemListFragment)getFragmentManager().findFragmentById(R.id.system_list_fragment);
        fragment.updateStatus(systemType, status);
	}
}