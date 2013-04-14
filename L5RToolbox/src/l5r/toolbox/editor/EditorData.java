package l5r.toolbox.editor;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class EditorData implements Parcelable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7601018770545027305L;
	
	private String title;
	private int roll;
	private int keep;
	private int explode;
	private boolean emphasis;
	private int bonus;
	private boolean explodeOnlyOnce;
	private boolean affectedByWoundPenalty;
	int id;
	
	/**
	 * @param id
	 * @param title
	 * @param roll
	 * @param keep
	 * @param explode
	 * @param emphasis
	 * @param bonus
	 */
	public EditorData(int id, String title, int roll, int keep, int explode,
			boolean emphasis, int bonus, boolean explodeOnlyOnce, boolean affectedByWoundPenalty) {
		this.title = title;
		this.roll = roll;
		this.keep = keep;
		this.explode = explode;
		this.emphasis = emphasis;
		this.bonus = bonus;
		this.id = id;
		this.explodeOnlyOnce = explodeOnlyOnce;
		this.affectedByWoundPenalty = affectedByWoundPenalty;
	}
	

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @return the roll
	 */
	public int getRoll() {
		return roll;
	}


	/**
	 * @return the keep
	 */
	public int getKeep() {
		return keep;
	}


	/**
	 * @return the explode
	 */
	public int getExplode() {
		return explode;
	}


	/**
	 * @return the emphasis
	 */
	public boolean isEmphasis() {
		return emphasis;
	}

	/**
	 * @return
	 */
	public int getBonus() {
		return bonus;
	}

	/**
     * @return the explodeOnlyOnce
     */
    public boolean isExplodeOnlyOnce() {
        return explodeOnlyOnce;
    }

    /**
     * @return the affectedByWoundPenalty
     */
    public boolean isAffectedByWoundPenalty() {
        return affectedByWoundPenalty;
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
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeInt(roll);
		dest.writeInt(keep);
		dest.writeInt(explode);
		dest.writeInt(emphasis?1:0);
		dest.writeInt(bonus);
		dest.writeInt(explodeOnlyOnce?1:0);
		dest.writeInt(affectedByWoundPenalty?1:0);
		dest.writeInt(id);
	}

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<EditorData> CREATOR = new Parcelable.Creator<EditorData>() {
        public EditorData createFromParcel(Parcel in) {
            return new EditorData(in);
        }

        public EditorData[] newArray(int size) {
            return new EditorData[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private EditorData(Parcel in) {
    	title = in.readString();
    	roll = in.readInt();
    	keep = in.readInt();
    	explode = in.readInt();
    	emphasis = in.readInt() == 1;
    	bonus = in.readInt();
    	explodeOnlyOnce = in.readInt() == 1;
    	affectedByWoundPenalty = in.readInt() == 1;
    	id = in.readInt();
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
		if (!(obj instanceof EditorData))
			return false;
		EditorData other = (EditorData) obj;
		if (id != other.id)
			return false;
		return true;
	}
    
    
}
