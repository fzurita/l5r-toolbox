package l5r.toolbox.profile;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileData implements Parcelable, Serializable{
	
	/**
     * 
     */
    private static final long serialVersionUID = -411541672209790389L;

    private int id;
    private String title;

	
	/**
	 * @param id
	 * @param title
	 * @param roll
	 * @param keep
	 * @param explode
	 * @param emphasis
	 * @param bonus
	 */
	public ProfileData(int id, String title) {
		this.title = title;
		this.id = id;
	}
	

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
    /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return title;
    }

    /* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
		dest.writeString(title);
	}

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<ProfileData> CREATOR = new Parcelable.Creator<ProfileData>() {
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private ProfileData(Parcel in) {
        id = in.readInt();
    	title = in.readString();
    }


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProfileData))
			return false;
		ProfileData other = (ProfileData) obj;
		if (id != other.id)
			return false;
		return true;
	}    
}
