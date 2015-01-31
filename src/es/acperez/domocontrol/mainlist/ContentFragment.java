package es.acperez.domocontrol.mainlist;

import es.acperez.domocontrol.R;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContentFragment extends Fragment {
	int mPosition = -1;
	private View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.content_fragment, container);
		
		return mContentView;
	}

	public void updateControlPanel(Fragment fragment) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.content_fragment_container, fragment).commit();
	}
}