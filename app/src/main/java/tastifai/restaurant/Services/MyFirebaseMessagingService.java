package tastifai.restaurant.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.rohannevrikar.restaurant.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import tastifai.restaurant.Activities.LoginActivity;
import tastifai.restaurant.Fragments.CurrentOrder;
import tastifai.restaurant.Fragments.Delivery;
import tastifai.restaurant.Fragments.InProgress;
import tastifai.restaurant.Utilities.BellManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Rohan Nevrikar on 08-02-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject object = new JSONObject(remoteMessage.getData());

        try {
            String orderStatusNumber = object.getString("orderStatusNumber");
            String orderGuid = object.getString("guid");
            Intent orderUpdateIntent = null;

            switch(orderStatusNumber) {
                case "1":
                    orderUpdateIntent = new Intent(CurrentOrder.NEW_CURRENT_ORDER_INTENT);
                    orderUpdateIntent.putExtra("orderGuid", orderGuid);

                    break;
                case "2":
                    orderUpdateIntent = new Intent(InProgress.IN_PROGRESS_ORDER_INTENT);
                    orderUpdateIntent.putExtra("orderGuid", orderGuid);

                    break;
                case "3":
                    orderUpdateIntent = new Intent(Delivery.IN_DELIVERY_ORDER_INTENT);
                    break;
                default:
                    orderUpdateIntent = new Intent(CurrentOrder.NEW_CURRENT_ORDER_INTENT);
                    orderUpdateIntent.putExtra("orderGuid", orderGuid);
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(orderUpdateIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //bellManager.destroy();
    }

}
