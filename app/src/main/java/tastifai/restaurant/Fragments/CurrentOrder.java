package tastifai.restaurant.Fragments;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.MainActivity.count;
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

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment implements CurrentOrderResponse {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    LinearLayoutManager orderManager;
    AlarmManager alarmManager;
    android.os.Handler customHandler;
    //private String URL;
    ArrayList<Order> orderList = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    public static Vibrator v;
    private TextView message;
    private MainActivity mainActivity;
    private static final String TAG = "CurrentOrder";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder, container, false);
        message = orderView.findViewById(R.id.message);
        if (!isMediaPlaying) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm);
            mediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setLooping(true);
            isMediaPlaying = true;
        }

        v = (Vibrator) (getActivity().getSystemService(Context.VIBRATOR_SERVICE));
        int restaurantId = ((MainActivity) getContext()).getId();
        Log.d("CurrentOrder", "onCreateView: " + restaurantId);
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        //URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7";
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        if (orderList.size() == 0) {
            message.setVisibility(View.VISIBLE);
        } else
            message.setVisibility(View.GONE);

        return orderView;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (getActivity() != null) {
                if (!getActivity().isFinishing()) {
                    if (Utils.isConnectedToInternet(getActivity())) {
                        CommonAsyncTask asyncTask = new CommonAsyncTask();
                        asyncTask.delegate = (CurrentOrderResponse) CurrentOrder.this;
                        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantCurrentOrders/" + restaurantId + "/7");
                        Log.d(TAG, "run: calling api");

                        //write here whaterver you want to repeat
                        customHandler.postDelayed(this, 6000);
                    } else {
                        Utils.setUpAlert(getActivity(), new getAPIResponse() {
                            @Override
                            public void OnRetry() {
                                Toast.makeText(getActivity(), "Trying to connect to internet", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            }

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (customHandler != null) {
            customHandler.removeCallbacks(updateTimerThread);

        }

    }

    @Override
    public void processFinish(String s) {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();


        if ((getActivity() != null)) {
            adapter.changeFragmentTitle(0, "ORDER(" + orderList.size() + ")");
            tabLayout.setupWithViewPager(viewPager);
            mainActivity.orderCount();

        }

        count = 1;

        //progressDialog.dismiss();
        orderList.clear();
        try {

            JSONArray orderArray = new JSONArray(s);
            for (int i = 0; i < orderArray.length(); i++) {
                JSONObject orderObj = orderArray.getJSONObject(i);
                guidList.add(orderObj.getString("GUID"));
            }
            double totalPrice = 0;
            Set<String> guidUnique = new HashSet<>(guidList);
            for (String guid : guidUnique) {
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
                        discount = orderObj.getDouble("DiscountPrices");
                        order.setTotalUser(orderObj.getDouble("TotalUser"));
                        totalUser = orderObj.getDouble("TotalUser");
                        restaurantEarning = orderObj.getDouble("RestaurantEarningsTotal");
                        deliveryCharge = orderObj.getDouble("DeliveryCharges");
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
            }
            Log.d(TAG, "processFinish: list size: " + orderList.size());
            if (orderList.size() == 0) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();

                message.setVisibility(View.VISIBLE);

            } else {
                Log.d(TAG, "processFinish: currentordermediaplaying" + isMediaPlaying);

                if (!isMediaPlaying) {
                    Log.d(TAG, "processFinish: currentordermediaplaying" + isMediaPlaying);
                    mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm);
                    mediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
//
//                        }
//
//                    });

                }
                mediaPlayer.start();
                isMediaPlaying = true;
                //   getActivity().startService(intent);
                message.setVisibility(View.GONE);

            }
            Log.d(TAG, "onPostExecute: " + orderList.size());
            currentCount = orderList.size();
            orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            OrderAdapter adapter = new OrderAdapter(getActivity(), orderList, R.layout.activity_currentorder);

            orderRecyclerView.setAdapter(adapter);
            currentCount = orderList.size();

        } catch (JSONException e) {
//                api = new CallAPI();
//                api.execute(URL);
//                Toast.makeText(getActivity(), "Exception caught, calling api", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (NullPointerException e) {
            if (getActivity() != null) {
                if (!mainActivity.isOnline()) {
                    Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

//                        api = new CallAPI();
//                        api.execute(URL);
                }
            }

        }
    }


}
