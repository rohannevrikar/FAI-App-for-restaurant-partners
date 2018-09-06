package tastifai.restaurant.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Interfaces.getAPIResponse;

/**
 * Created by Android on 21/04/2018.
 */

public class Utils {

    public static boolean isConnectedToInternet(Context context) {
        if (context != null) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
            return false;

        }
        return false;

    }


    public static void setUpAlert(final Context context, final getAPIResponse getAPIResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your phone is not connected to the internet. Please try again")
                .setCancelable(false).
                setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getAPIResponse.OnRetry(dialogInterface);
                    }
                });
        AlertDialog alert = builder.create();
        if (!alert.isShowing() && !((Activity)context).isFinishing()){
            alert.show();
        }
    }
}
