package Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by USER on 27-11-2017.
 */

public class AppPreferences {

    public static final String SEFPRO_PREFERENCE = "MBAPreference";

    private static final String DRIVE_ID = "DriveId";
    private static final String DefaultKeywords = "DefaultKeywords";

    private static AppPreferences appPreferences = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefEditor;
    public AppPreferences() {
    }

    public static AppPreferences getInstance(Context context) {
        if (appPreferences == null) {
            appPreferences = new AppPreferences();
            appPreferences.setContext(context);
        }
        return appPreferences;
    }
    public void setContext(Context context) {
        if (sharedPreferences != null) {
            return;
        }
        sharedPreferences = context.getSharedPreferences(SEFPRO_PREFERENCE,
                Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
    }
    public String getDriveId() {
        return sharedPreferences.getString(DRIVE_ID, "");
    }

    public void setDriveId(String token) {
        prefEditor.putString(DRIVE_ID, token);
        prefEditor.commit();
    }

    public String getDefaultKeywords() {
        return sharedPreferences.getString(DefaultKeywords, "");
    }
    public void setDefaultKeywords(String keywords) {
        prefEditor.putString(DefaultKeywords, keywords);
        prefEditor.commit();
    }
}
