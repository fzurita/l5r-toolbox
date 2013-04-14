package l5r.toolbox.roller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import l5r.toolbox.editor.EditorData;

public class RollerManager extends ArrayList<EditorData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2413927196184739836L;
	String filename = null;
	int currentIdCount = 0;

	public RollerManager(String filename) {
		this.filename = filename;
	}
	
	/**
	 * @param data
	 * @return
	 */
	public int addWithId(EditorData data)
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
		EditorData temp = new EditorData(id, null, 0, 0, 0, false, 0, false, true);
		this.remove(temp);
	}

	/**
	 * @param id
	 */
	public void modify(EditorData data) {
		int index = this.indexOf(data);
		this.set(index, data);
	}
	
	public EditorData getWithId(int id)
	{
		EditorData temp = new EditorData(id, null, 0, 0, 0, false, 0, false, true);
		int index = this.indexOf(temp);
		return this.get(index);
	}
	
	public void saveRollers(Context context) {
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

	public void loadRollers(Context context) {
		try {		    
			FileInputStream fis = context.openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(fis);
			RollerManager rollers = (RollerManager) is.readObject();
			this.clear();
			this.addAll(rollers);
			this.currentIdCount = rollers.currentIdCount;
			is.close();
		} catch (IOException e) {
			//File not found... do nothing
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
