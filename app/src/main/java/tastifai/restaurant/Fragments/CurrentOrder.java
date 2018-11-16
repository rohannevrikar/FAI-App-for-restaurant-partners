package tastifai.restaurant.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Services.OrderService;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.MainActivity.currentCount;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment implements CurrentOrderResponse {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    private android.os.Handler customHandler;

    ArrayList<Order> orderList = new ArrayList<>();
    public static Vibrator v;
    private TextView message;
    private MainActivity mainActivity;

    private static final String TAG = "CurrentOrder";
    public static final String NEW_CURRENT_ORDER_INTENT = "newCurrentOrder";
    public static final String CANCELLED_CURRENT_ORDER_INTENT = "cancelledCurrentOrder";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder, container, false);
        message = orderView.findViewById(R.id.message);
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);

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
    public void onResume() {
        super.onResume();
        updateCurrentOrders();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filterNewCanceled = new IntentFilter();
        filterNewCanceled.addAction(NEW_CURRENT_ORDER_INTENT);
        filterNewCanceled.addAction(CANCELLED_CURRENT_ORDER_INTENT);

        LocalBroadcastManager.getInstance(mainActivity).registerReceiver((mMessageReceiver),
                filterNewCanceled
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(mainActivity, "CurrentOrder Received", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: ");
            updateCurrentOrders();
        }
    };

    private void updateCurrentOrders() {
        if (Utils.isConnectedToInternet(mainActivity)) {
            CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
            asyncTask.delegate = (CurrentOrderResponse) CurrentOrder.this;

            Log.d(TAG, "onReceive: " + Constants.URL + "GetRestaurantCurrentOrders/" + restaurantId + "/7");
            //asyncTask.execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7");
            Log.d(TAG, "run: executing ");
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantCurrentOrders/" + restaurantId + "/7");

            //write here whaterver you want to repeat
        }
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

                Log.d("CurrentOrder", "onPostExecute: " + orderObj.getString("GUID"));

            }
            double totalPrice = 0;
            Set<String> guidUnique = new HashSet<>(guidList);
            Log.d("guidUnique Size","" + guidUnique.size());
            OrderService.updateOrders((HashSet<String>) guidUnique);
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
                Log.d("CurrentOrder", "processFinish: " + orderList.size());
            }

            currentCount = orderList.size();

//            if (currentCount == 0)
//                message.setVisibility(View.VISIBLE);
//            else
//                message.setVisibility(View.GONE);

            mainActivity.orderCount();
            orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
            OrderAdapter adapter = new OrderAdapter(mainActivity, orderList, R.layout.activity_currentorder);
            adapter.registerEmptyListListener(message);
            orderRecyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

