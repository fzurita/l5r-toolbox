package l5r.toolbox.rollmods;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Francisco
 *
 */
public class RollMods implements Parcelable {

	private int rollPenalty = 0;
	private int keepPenalty = 0;
	private int staticPenalty = 0;
	private int rollBonus = 0;
	private int keepBonus = 0;
	private int staticBonus = 0;
	private boolean fullAttack = false;
	private boolean vpBonus = false;
	private boolean keepHighest = true;
	private boolean save = false;
	
	
	/**
	 * @param rollPenalty
	 * @param keepPenalty
	 * @param staticPenalty
	 * @param rollBonus
	 * @param keepBonus
	 * @param staticBonus
	 * @param fullAttack
	 * @param vpBonus
	 * @param keepHighest
	 * @param save
	 */
	public RollMods(int rollPenalty, int keepPenalty, int staticPenalty, int rollBonus, int keepBonus, int staticBonus,
            boolean fullAttack, boolean vpBonus, boolean keepHighest, boolean save) {
        super();
        this.rollPenalty = rollPenalty;
        this.keepPenalty = keepPenalty;
        this.staticPenalty = staticPenalty;
        this.rollBonus = rollBonus;
        this.keepBonus = keepBonus;
        this.staticBonus = staticBonus;
        this.fullAttack = fullAttack;
        this.vpBonus = vpBonus;
        this.keepHighest = keepHighest;
        this.save = save;
    }

	/**
	 * @return the rollPenalty
	 */
	public int getRollPenalty() {
		return rollPenalty;
	}


	/**
	 * @param rollPenalty the rollPenalty to set
	 */
	public void setRollPenalty(int rollPenalty) {
		this.rollPenalty = rollPenalty;
	}


	/**
	 * @return the keepPenalty
	 */
	public int getKeepPenalty() {
		return keepPenalty;
	}


	/**
	 * @param keepPenalty the keepPenalty to set
	 */
	public void setKeepPenalty(int keepPenalty) {
		this.keepPenalty = keepPenalty;
	}


	/**
	 * @return the staticPenalty
	 */
	public int getStaticPenalty() {
		return staticPenalty;
	}


	/**
	 * @param staticPenalty the staticPenalty to set
	 */
	public void setStaticPenalty(int staticPenalty) {
		this.staticPenalty = staticPenalty;
	}

	/**
     * @return the rollBonus
     */
    public int getRollBonus() {
        return rollBonus;
    }

    /**
     * @param rollBonus the rollBonus to set
     */
    public void setRollBonus(int rollBonus) {
        this.rollBonus = rollBonus;
    }

    /**
     * @return the keepBonus
     */
    public int getKeepBonus() {
        return keepBonus;
    }

    /**
     * @param keepBonus the keepBonus to set
     */
    public void setKeepBonus(int keepBonus) {
        this.keepBonus = keepBonus;
    }

    /**
     * @return the staticBonus
     */
    public int getStaticBonus() {
        return staticBonus;
    }

    /**
     * @param staticBonus the staticBonus to set
     */
    public void setStaticBonus(int staticBonus) {
        this.staticBonus = staticBonus;
    }

    /**
	 * @return the fullAttack
	 */
	public boolean isFullAttack() {
		return fullAttack;
	}


	/**
	 * @param fullAttack the fullAttack to set
	 */
	public void setFullAttack(boolean fullAttack) {
		this.fullAttack = fullAttack;
	}


	/**
	 * @return the vpBonus
	 */
	public boolean isVpBonus() {
		return vpBonus;
	}


	/**
	 * @param vpBonus the vpBonus to set
	 */
	public void setVpBonus(boolean vpBonus) {
		this.vpBonus = vpBonus;
	}


    /**
     * @return the keepHighest
     */
    public boolean isKeepHighest() {
        return keepHighest;
    }


    /**
     * @param keepHighest the keepHighest to set
     */
    public void setKeepHighest(boolean keepHighest) {
        this.keepHighest = keepHighest;
    }


    /**
     * @return the save
     */
    public boolean isSave() {
        return save;
    }

    /**
     * @param save the save to set
     */
    public void setSave(boolean save) {
        this.save = save;
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
        dest.writeInt(rollPenalty);
        dest.writeInt(keepPenalty);
        dest.writeInt(staticPenalty);
        dest.writeInt(rollBonus);
        dest.writeInt(keepBonus);
        dest.writeInt(staticBonus);
        dest.writeInt(fullAttack?1:0);
        dest.writeInt(vpBonus?1:0);
        dest.writeInt(keepHighest?1:0);
        dest.writeInt(save?1:0);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<RollMods> CREATOR = new Parcelable.Creator<RollMods>() {
        public RollMods createFromParcel(Parcel in) {
            return new RollMods(in);
        }

        public RollMods[] newArray(int size) {
            return new RollMods[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private RollMods(Parcel in) {
        
        rollPenalty = in.readInt();
        keepPenalty = in.readInt();
        staticPenalty = in.readInt();
        rollBonus = in.readInt();
        keepBonus = in.readInt();
        staticBonus = in.readInt();
        fullAttack = in.readInt() == 1;
        vpBonus = in.readInt() == 1;
        keepHighest = in.readInt() == 1;
        save = in.readInt() == 1;
    }

}
