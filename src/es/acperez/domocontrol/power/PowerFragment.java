package es.acperez.domocontrol.power;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.DomoSystem;

public class PowerFragment extends SystemFragment {
	private View mView;
	private View mLoadingView;
	private View mOfflineView;
	private View mOnlineView;

	public PowerFragment() {
		System.out.println("PowerFragment constructor");
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.power_monitor, container, false);
        mLoadingView = mView.findViewById(R.id.power_loading_panel);
        mOfflineView = mView.findViewById(R.id.power_offline_panel);
        mOnlineView = mView.findViewById(R.id.power_online_panel);
        return mView;
    }

	@Override
	public void updateStatus(int status, int error) {
		switch(status) {
			case DomoSystem.STATUS_LOADING:
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.GONE);
				mLoadingView.setVisibility(View.VISIBLE);
				break;
			case DomoSystem.STATUS_OFFLINE:
				mOnlineView.setVisibility(View.GONE);
				mLoadingView.setVisibility(View.GONE);
				updateOfflineMessage(error);
				mOfflineView.setVisibility(View.VISIBLE);
				break;
			case DomoSystem.STATUS_ONLINE:
				mLoadingView.setVisibility(View.GONE);
				mOfflineView.setVisibility(View.GONE);
				mOnlineView.setVisibility(View.VISIBLE);
				break;
		}
	}

	private void updateOfflineMessage(int error) {
		TextView text = (TextView) mOfflineView.findViewById(R.id.power_monitor_error_text);
		
		switch (error) {
			case DomoSystem.ERROR_NETWORK:
				text.setText(getString(R.string.power_network_error));
				break;
			case DomoSystem.ERROR_PASSWORD:
				text.setText(getString(R.string.power_loging_error));
				break;
			default:
				text.setText(getString(R.string.network_unknown_error));
				break;
		}
	}
}