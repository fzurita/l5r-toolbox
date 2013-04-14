package l5r.toolbox.profile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;

public class ProfileManager extends ArrayList<ProfileData> {

	/**
     * 
     */
    private static final long serialVersionUID = 7552132713974291094L;
    
    String filename = null;
	int currentIdCount = 0;
	int currentProfileId = 0;

	public ProfileManager(String filename) {
		this.filename = filename;
	}

	/**
     * @return the currentProfileId
     */
    public int getCurrentProfileId() {
        return currentProfileId;
    }

    /**
     * @param currentProfileId the currentProfileId to set
     */
    public void setCurrentProfileId(int currentProfileId) {
        this.currentProfileId = currentProfileId;
    }

    /**
	 * @param data
	 * @return
	 */
	public int addWithId(ProfileData data)
	{
		int tempId = currentIdCount;
		currentIdCount++;
		data.setId(tempId);
		add(data);
		return tempId;
	}
	
	/**
	 * @param data
	 * @return
	 */
	public void deleteWithId(int id) {
	    ProfileData temp = new ProfileData(id, null);
		this.remove(temp);
	}

	/**
	 * @param id
	 */
	public void modify(ProfileData data) {
		int index = this.indexOf(data);
		this.set(index, data);
	}
	
	public ProfileData getWithId(int id)
	{
	    ProfileData temp = new ProfileData(id, null);
		int index = this.indexOf(temp);
		
		ProfileData finalData = index == -1 ? null : this.get(index);
		return finalData;
	}
	
	public void saveProfiles(Context context) {
		try {
			//Save rollers
			FileOutputStream fos = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadProfiles(Context context) {
		try {
			FileInputStream fis = context.openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(fis);
			ProfileManager rollers = (ProfileManager) is.readObject();
			this.clear();
			this.addAll(rollers);
			this.currentIdCount = rollers.currentIdCount;
			is.close();
		} catch (IOException e) {
			//File not found... do nothing
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//Add the default profile if it doesn't exist
        if(getWithId(0) == null){
            addWithId(new ProfileData(0, "Default"));
            saveProfiles(context);
        }
	}
}
