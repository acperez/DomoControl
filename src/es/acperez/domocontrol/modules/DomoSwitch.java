package es.acperez.domocontrol.modules;

import android.os.Parcel;
import android.os.Parcelable;

public class DomoSwitch implements Parcelable {
	public int id;
	public boolean status;
	public String name;
	public int serviceId;
	
	public DomoSwitch(int aId, boolean aStatus, String aName, int aServiceId) {
		id = aId;
		status = aStatus;
		name = aName;
		serviceId = aServiceId;
	}
	
	public DomoSwitch(Parcel source) {
		id = source.readInt();
		status = source.readByte() == 1 ? true : false;
		name = source.readString();
		serviceId = source.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeByte((byte) (status == true ? 1 : 0));
		dest.writeString(name);
		dest.writeInt(serviceId);
	}
	
	public static final Parcelable.Creator<DomoSwitch> CREATOR = new Parcelable.Creator<DomoSwitch>() {
		@Override
		public DomoSwitch createFromParcel(Parcel source) {
			return new DomoSwitch(source);
		}
		
		@Override
		public DomoSwitch[] newArray(int size) {
			return new DomoSwitch[size];
		}
	};
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof DomoSwitch))return false;
	    
		DomoSwitch otherSwitch = (DomoSwitch) other;
		if (otherSwitch.id != this.id)
			return false;
		
		return otherSwitch.serviceId == this.serviceId;
	}
}
