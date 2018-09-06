package tastifai.restaurant.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.rohannevrikar.restaurant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import tastifai.restaurant.Activities.LoginActivity;
import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Interfaces.OrderListener;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Utilities.Utils;

import static tastifai.restaurant.Activities.LoginActivity.serviceMediaPlayer;

/**
 * Created by Rohan Nevrikar on 15-04-2018.
 */

public class CheckNewOrdersService extends Service implements CurrentOrderResponse {
    Context context;
    MainActivity mainActivity = new MainActivity();
    public static Vibrator v;
    private SharedPreferences sharedPreferences;
    int restaurantId;
    private static final String TAG = "CheckNewOrdersService";
    boolean isPausedInCall = false;
    android.os.Handler customHandler;
    ArrayList<Order> orderList = new ArrayList<>();
    private boolean mRunning;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    int maxVolume = 50;
    AudioManager mAudioManager;
    public static CheckNewOrdersService instance;
    int originalVolume;
    public static OrderListener orderListener;
    public static boolean isServiceRunning;
    NotificationCompat.Builder builder;
    private static final String NOTIFICATION_CHANNEL_ID = "notification_channel_id";
    private static final String NOTIFICATION_Service_CHANNEL_ID = "service_channel";
    Notification notification;
    NotificationManager notificationManager;
    final public void setListener(OrderListener orderListener) {
        Log.d(TAG, "setListener: ");
        this.orderListener = orderListener;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            if (Utils.isConnectedToInternet(context)) {
                CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
                asyncTask.delegate = (CurrentOrderResponse) context;
                builder.setContentTitle(serviceMediaPlayer.isPlaying() + " API Last called: " + Calendar.getInstance().getTime().toString());
                notificationManager.notify(121, builder.build());

                Log.d(TAG, "onReceive: http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7");
                //asyncTask.execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7");
                Log.d(TAG, "run: executing ");
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7");

                //write here whaterver you want to repeat
            }
            customHandler.postDelayed(this, 10000);

//            } else {
//                Utils.setUpAlert(context, new getAPIResponse() {
//                    @Override
//                    public void OnRetry(DialogInterface dialogInterface) {
//                        Toast.makeText(context, "Trying to connect to internet", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }


        }
    };

//    public CheckNewOrdersService() {
//        Log.d(TAG, "CheckNewOrdersService: init instance");
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Crashlytics.log("Destroying service");
        Log.d(TAG, "onDestroy: Destroying service");

    }

    @Override
    public void processFinish(String s) {
        ArrayList<String> guidList = new ArrayList<>();
        try {
            orderList.clear();
            JSONArray orderArray = new JSONArray(s);
            for (int i = 0; i < orderArray.length(); i++) {
                JSONObject orderObj = orderArray.getJSONObject(i);
                guidList.add(orderObj.getString("GUID"));

                Log.d("CheckNewOrderService", "onPostExecute: " + orderObj.getString("GUID"));

            }
            double totalPrice = 0;
            Set<String> guidUnique = new HashSet<>(guidList);
            for (String guid : guidUnique) {
                Log.d("CheckNewOrderService", "onPostExecute: " + guid);
                ArrayList<Item> itemList = new ArrayList<>();
                Order order = new Order();
                totalPrice = 0;


                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject orderObj = orderArray.getJSONObject(i);
                    if (orderObj.getString("GUID").equals(guid)) {
                        Item item = new Item();
                        item.setItem(orderObj.getString("ItemName"));
                        totalPrice = totalPrice + (Double.parseDouble(orderObj.getString("ItemPrice")) * Double.parseDouble(orderObj.getString("Quantity")));
                        item.setPrice(orderObj.getDouble("ItemPrice"));
                        item.setQty(orderObj.getString("Quantity"));
                        itemList.add(item);
                        order.setCustomerName(orderObj.getString("UserFirstName"));
                        order.setContactNumber(orderObj.getString("UserContactNumber"));
                        order.setGuid(orderObj.getString("GUID"));
                        order.setDiscount(orderObj.getDouble("DiscountPrices"));
                        //discount = orderObj.getDouble("DiscountPrices");
                        order.setTotalUser(orderObj.getDouble("TotalUser"));
                        //totalUser = orderObj.getDouble("TotalUser");
                        order.setRestaurantEarnings(orderObj.getDouble("RestaurantEarningsTotal"));
                        //deliveryCharge = orderObj.getDouble("DeliveryCharges");
                        order.setDeliveryCharge(orderObj.getDouble("DeliveryCharges"));
                        order.setinstruction(orderObj.getString("DeliveryInstructions"));
                        order.setDateTime(mainActivity.convertDateTime(orderObj.getString("DateTime")));
                        if (!orderObj.getString("AddressLat").equals("") || !orderObj.getString("AddressLong").equals("")) {
                            order.setUserLat(Double.parseDouble(orderObj.getString("AddressLat")));
                            order.setUserLng(Double.parseDouble(orderObj.getString("AddressLong")));
                            order.setNavigationAvailable(true);
                        } else
                            order.setNavigationAvailable(false);
                        if (orderObj.getString("AddressBuilding").equals("") || orderObj.getString("AddressStreet").equals("")) {
                            order.setDeliveryAddress(orderObj.getString("DeliverAt"));
                        } else {
                            order.setDeliveryAddress(orderObj.getString("AddressBuilding") + " " + orderObj.getString("AddressStreet"));
                        }

                        order.setItemList(itemList);
                    }
                }
                order.setTotalPrice(totalPrice);

                orderList.add(order);
                Log.d("CheckNewOrderService", "processFinish: " + orderList.size());
            }
            if (serviceMediaPlayer == null) {
                Log.d(TAG, "processFinish: servicemediaplayer is null");
                serviceMediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                serviceMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
                serviceMediaPlayer.setLooping(true);
                serviceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            }
            if (orderListener != null) {
                Log.d(TAG, "processFinish: init listener");
                orderListener.orderCallBack(orderList);
            } else
                Log.d(TAG, "processFinish: orderListener null");
            if (orderList.size() > 0) {
                try {

                    if (!serviceMediaPlayer.isPlaying()) {
                        if (!isPausedInCall) {
                            serviceMediaPlayer.start();
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                        }
                        Log.d(TAG, "processFinish: inside mediaplayer if");

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (serviceMediaPlayer.isPlaying()) {
                    serviceMediaPlayer.pause();

                }
//                mainActivity.stopService(new Intent(mainActivity, MediaPlayerService.class));
                // v.cancel();
            }


        } catch (JSONException e) {
            isServiceRunning = false;
            e.printStackTrace();
        } catch (NullPointerException e) {

            if (serviceMediaPlayer != null && serviceMediaPlayer.isPlaying()) {
                serviceMediaPlayer.pause();
            }
            isServiceRunning = false;
//            v.cancel();
            //mainActivity.stopService(new Intent(mainActivity, MediaPlayerService.class));

        } catch (Exception e) {
            if (serviceMediaPlayer != null && serviceMediaPlayer.isPlaying()) {
                serviceMediaPlayer.pause();
            }
            isServiceRunning = false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        instance = this;
        mRunning = false;
        context = this;
        isServiceRunning = true;
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        restaurantId = sharedPreferences.getInt("id", -1);
        Log.d(TAG, "onCreate: " + restaurantId);
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 10000);
        if (serviceMediaPlayer == null) {

            serviceMediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            serviceMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            serviceMediaPlayer.setLooping(true);
            serviceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        Log.d(TAG, "onStartCommand");


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.applogo)
                .setContentIntent(pendingIntent)
                .setContentTitle("FAI Partner")
                .setContentText("Click here to launch FAI partner app");
        notification = builder.build();
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
        }
        startForeground(121, notification);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (serviceMediaPlayer != null) {
                                Log.d(TAG, "onCallStateChanged: pausing media");
                                pauseMedia();
                                isPausedInCall = true;

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (serviceMediaPlayer != null) {
                            if (isPausedInCall) {
                                if(orderList.size() > 0) {
                                    Log.d(TAG, "onCallStateChanged: playing media");
                                    isPausedInCall = false;
                                    playMedia();
                                }
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }


    private void playMedia() {
        Log.d(TAG, "playMedia: " + serviceMediaPlayer.isPlaying());
        if (!serviceMediaPlayer.isPlaying()) {
            serviceMediaPlayer.start();
        }
        //customHandler.postDelayed(updateTimerThread, 0);

    }

    private void pauseMedia() {
        Log.d(TAG, "pauseMedia: " + serviceMediaPlayer.isPlaying());
        if (serviceMediaPlayer.isPlaying()) {
            serviceMediaPlayer.pause();
            // customHandler.removeCallbacks(updateTimerThread);
        }
    }
}
