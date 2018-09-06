package tastifai.restaurant.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Interfaces.OrderListener;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Services.CheckNewOrdersService;
import tastifai.restaurant.Services.MyFirebaseMessagingService;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.LoginActivity.serviceMediaPlayer;
import static tastifai.restaurant.Activities.MainActivity.count;
import static tastifai.restaurant.Activities.MainActivity.count_c;
import static tastifai.restaurant.Activities.MainActivity.count_d;
import static tastifai.restaurant.Activities.MainActivity.deliveryCharge;
import static tastifai.restaurant.Activities.MainActivity.discount;
import static tastifai.restaurant.Activities.MainActivity.isMediaPlaying;
import static tastifai.restaurant.Activities.MainActivity.mediaPlayer;
import static tastifai.restaurant.Activities.MainActivity.restaurantEarning;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.adapter;
import static tastifai.restaurant.Activities.MainActivity.currentCount;
import static tastifai.restaurant.Activities.MainActivity.tabLayout;
import static tastifai.restaurant.Activities.MainActivity.totalUser;
import static tastifai.restaurant.Activities.MainActivity.viewPager;
import static tastifai.restaurant.Services.CheckNewOrdersService.instance;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    private android.os.Handler customHandler;

    ArrayList<Order> orderList = new ArrayList<>();
    public static Vibrator v;
    private TextView message;
    private MainActivity mainActivity;
    private static final String TAG = "CurrentOrder";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder, container, false);
        message = orderView.findViewById(R.id.message);
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(mainActivity, R.raw.alarm);
            mediaPlayer.setWakeMode(mainActivity, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setLooping(true);
            //isMediaPlaying = true;
        }
        customHandler = new android.os.Handler();
       // customHandler.postDelayed(updateTimerThread, 5000);
//        if (instance != null) {
//            instance.setListener(new OrderListener() {
//                @Override
//                public void orderCallBack(ArrayList<Order> order) {
//                        if ((mainActivity != null)) {
//                            Log.d(TAG, "orderCallBack: " + order.size());
//                            //adapter.changeFragmentTitle(0, "ORDER(" + order.size() + ")");
//                            currentCount = order.size();
//                            tabLayout.setupWithViewPager(viewPager);
//                            mainActivity.orderCount();
//
//                        }
//
//                    Log.d(TAG, "orderCallBack: ");
//                    orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
//                    OrderAdapter adapter = new OrderAdapter(mainActivity, order, R.layout.activity_currentorder);
//
//                    orderRecyclerView.setAdapter(adapter);
//                    if (order.size() == 0) {
//                        Log.d(TAG, "processFinish: inside if");
////                if (serviceMediaPlayer.isPlaying())
////                    serviceMediaPlayer.stop();
//
//                        message.setVisibility(View.VISIBLE);
//
//                    } else {
//                        Log.d(TAG, "processFinish: inside else");
//                        message.setVisibility(View.GONE);
//                    }
//                }
//            });
//        } else {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.detach(this).attach(this).commit();
//
//        }


        //CallAPI callAPI = new CallAPI();
        //callAPI.execute("https://getfaitechnologies1:3ec429152844e115c71bb45946f5ab520cf3a91c@api.exotel.com/v1/Accounts/getfaitechnologies1/calls/connect", "From=09824350333&CallerId=07939276640&Url=http://my.exotel.com/exoml/start/181024");
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        //URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7";
//        customHandler = new android.os.Handler();
//        customHandler.postDelayed(updateTimerThread, 6000);
//        if (orderList.size() == 0) {
//            message.setVisibility(View.VISIBLE);
//        } else
//            message.setVisibility(View.GONE);

        return orderView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if(MyFirebaseMessagingService.orderList != null && MyFirebaseMessagingService.orderList.size() > 0){
                Log.d(TAG, "run: inside run" + MyFirebaseMessagingService.orderList.size());
                orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
                OrderAdapter adapter = new OrderAdapter(mainActivity, MyFirebaseMessagingService.orderList, R.layout.activity_currentorder);
                orderRecyclerView.setAdapter(adapter);
            }else
                Log.d(TAG, "run: firebase is null");
//            if (mainActivity != null) {
//
//                if (!mainActivity.isFinishing()) {
//                    if (Utils.isConnectedToInternet(mainActivity)) {
//                        CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
//                        asyncTask.delegate = (CurrentOrderResponse) InProgress.this;
//                        //    asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantInProgressOrders/" + restaurantId + "/1");
//                        //write here whaterver you want to repeat
//                    }  else {
//                        Toast.makeText(mainActivity, "Not connected to internet runnable in progress", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
                customHandler.postDelayed(this, 5000);

            }



    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(mMessageReceiver);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(mainActivity, "Received", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: ");
            orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
            OrderAdapter adapter = new OrderAdapter(mainActivity, (ArrayList<Order>)intent.getBundleExtra("bundle").getSerializable("arraylist"), R.layout.activity_currentorder);
            orderRecyclerView.setAdapter(adapter);

        }
    };

//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        if (!isMediaPlayerRunning) {
//            mediaPlayer.start();
//            isMediaPlayerRunning = true;
//        }
//
//    }
}

