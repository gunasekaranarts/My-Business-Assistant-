package POJO;

import java.io.Serializable;

/**
 * Created by USER on 24-07-2018.
 */

public class Item implements Serializable {

    public int MasterItemId;
    public String MasterItemName;

    public int getMasterItemId() {
        return MasterItemId;
    }

    public void setMasterItemId(int masterItemId) {
        MasterItemId = masterItemId;
    }

    public String getMasterItemName() {
        return MasterItemName;
    }

    public void setMasterItemName(String masterItemName) {
        MasterItemName = masterItemName;
    }

    @Override
    public String toString() {
        return this.getMasterItemName();
    }
}
