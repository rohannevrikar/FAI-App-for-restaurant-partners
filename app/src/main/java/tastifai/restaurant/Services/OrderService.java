package tastifai.restaurant.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.rohannevrikar.restaurant.R;

import java.util.HashSet;

import tastifai.restaurant.Activities.LoginActivity;
import tastifai.restaurant.Utilities.BellManager;

import static tastifai.restaurant.Fragments.CurrentOrder.CANCELLED_CURRENT_ORDER_INTENT;
import static tastifai.restaurant.Fragments.CurrentOrder.NEW_CURRENT_ORDER_INTENT;
import static tastifai.restaurant.Fragments.InProgress.IN_PROGRESS_ORDER_INTENT;

public class OrderService extends Service {
    protected BellManager bellManager;

    public static OrderService mInstance;
    public static HashSet<String> newOrders;

    private static final String NOTIFICATION_Service_CHANNEL_ID = "service_channel";

    public OrderService() {
        mInstance = this;
        newOrders = new HashSet<>();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void updateOrders(HashSet<String> newOrders) {
        OrderService.newOrders = newOrders;
        if(mInstance != null)
            if(newOrders.size() > 0)
                mInstance.bellManager.play();
            else
                mInstance.bellManager.stop();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LocalBroadcastManager.getInstance(this).registerReceiver((newOrderReceiver),
                new IntentFilter(NEW_CURRENT_ORDER_INTENT)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((cancelOrderReceiver),
                new IntentFilter(CANCELLED_CURRENT_ORDER_INTENT)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((currentOrderReceiver),
                new IntentFilter(IN_PROGRESS_ORDER_INTENT)
        );

        bellManager = new BellManager(this);

        startForegroundNotification();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mInstance = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newOrderReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cancelOrderReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentOrderReceiver);
        bellManager.destroy();
    }

    private BroadcastReceiver newOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String orderGuid = intent.getStringExtra("orderGuid");
            newOrders.add(orderGuid);
            bellManager.play();
        }
    };

    private BroadcastReceiver cancelOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String orderGuid = intent.getStringExtra("orderGuid");
            newOrders.remove(orderGuid);
            if(newOrders.isEmpty())
                bellManager.stop();
        }
    };

    private BroadcastReceiver currentOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String orderGuid = intent.getStringExtra("orderGuid");
            newOrders.remove(orderGuid);
            if(newOrders.isEmpty())
                bellManager.stop();
        }
    };

    private void startForegroundNotification () {
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_Service_CHANNEL_ID, "Sync Service", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Service Name");
            notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(this, NOTIFICATION_Service_CHANNEL_ID)
                    .setContentTitle("FAI Partner")
                    .setContentText("Click here to launch FAI partner app")
                    .setSmallIcon(R.drawable.applogo)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_Service_CHANNEL_ID)
                    .setSmallIcon(R.drawable.applogo)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("FAI Partner")
                    .setContentText("Click here to launch FAI partner app");
            notification = builder.build();
        }
        startForeground(121, notification);
    }
}
