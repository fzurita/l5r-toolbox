package l5r.toolbox.common;

import l5r.toolbox.editor.EditorData;
import l5r.toolbox.rollmods.RollMods;

public interface CommonInterface {

    public RollMods getRollMods();
    public void onRollerCreated(EditorData data, boolean quick);
}
