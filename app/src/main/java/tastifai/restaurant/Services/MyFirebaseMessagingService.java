package tastifai.restaurant.Services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import tastifai.restaurant.Fragments.CurrentOrder;
import tastifai.restaurant.Fragments.Delivery;
import tastifai.restaurant.Fragments.InProgress;

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
            Thread.sleep(1000);
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
                case "4":
                    orderUpdateIntent = new Intent(CurrentOrder.CANCELLED_CURRENT_ORDER_INTENT);
                    break;
                case "5":
                    orderUpdateIntent = new Intent(Delivery.NOT_DELIVERED_ORDER_INTENT);
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
