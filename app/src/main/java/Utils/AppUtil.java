package Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by USER on 25-07-2018.
 */

public class AppUtil {
    public static void ShowMessage(Context mcontext, String Message, int duration){
        Toast.makeText(mcontext,Message,duration).show();
    }
}
