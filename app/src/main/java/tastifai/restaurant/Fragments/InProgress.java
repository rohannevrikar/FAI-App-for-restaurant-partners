package tastifai.restaurant.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.MainActivity.progressCount;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.tabLayout;
import static tastifai.restaurant.Activities.MainActivity.viewPager;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class InProgress extends Fragment implements CurrentOrderResponse {
    View myView;
    View view;
    private Button btnStartDelivery;
    private ArrayList<Order> progressOrderList = new ArrayList<>();
    private OrderAdapter progressAdapter;
    private RecyclerView recyclerView;
    //private String URL;
    private TextView message;
    private MainActivity mainActivity;
    private ProgressDialog progressDialog;

    private static final String TAG = "InProgress";
    public static final String IN_PROGRESS_ORDER_INTENT = "inProgressOrder";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_inprogress, container, false);
        message = myView.findViewById(R.id.message);
        recyclerView = myView.findViewById(R.id.orderRecyclerView);

        if (progressOrderList.size() == 0)
            message.setVisibility(View.VISIBLE);
        else
            message.setVisibility(View.GONE);

        // URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantInProgressOrders/" + restaurantId + "/1";

        return myView;
    }

    void updateInProgressOrders() {
        if (mainActivity != null && !mainActivity.isFinishing()) {
            if (Utils.isConnectedToInternet(mainActivity)) {
                CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
                asyncTask.delegate = (CurrentOrderResponse) InProgress.this;
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantInProgressOrders/" + restaurantId + "/1");

            }  else {
                Toast.makeText(mainActivity, "Not connected to internet runnable in progress", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInProgressOrders();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver((mMessageReceiver),
                new IntentFilter(IN_PROGRESS_ORDER_INTENT)
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {;
            updateInProgressOrders();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity)activity;
    }

    @Override
    public void processFinish(String s) {
        ArrayList<String> guidList = new ArrayList<>();

        try {
            progressOrderList.clear();
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
                        order.setinstruction(orderObj.getString("DeliveryInstructions"));
                        order.setDiscount(orderObj.getDouble("DiscountPrices"));
                        //discount = orderObj.getDouble("DiscountPrices");
                        order.setTotalUser(orderObj.getDouble("TotalUser"));
                        //totalUser = orderObj.getDouble("TotalUser");
                        order.setRestaurantEarnings(orderObj.getDouble("RestaurantEarningsTotal"));
                        //deliveryCharge = orderObj.getDouble("DeliveryCharges");
                        order.setDeliveryCharge(orderObj.getDouble("DeliveryCharges"));
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
                        order.setDateTime(mainActivity.convertDateTime(orderObj.getString("DateTime")));
                        order.setItemList(itemList);
                    }

                }
                order.setTotalPrice(totalPrice);

                progressOrderList.add(order);

            }

                if ((mainActivity != null)) {
                   // adapter.changeFragmentTitle(1, "PROGRESS(" + progressOrderList.size() + ")");
                    progressCount = progressOrderList.size();
                    tabLayout.setupWithViewPager(viewPager);
                    mainActivity.orderCount();

                }


            Log.d(TAG, "processFinish: list size: " + progressOrderList.size());
            if (progressOrderList.size() == 0) {
                message.setVisibility(View.VISIBLE);
            } else
                message.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
            if (mainActivity != null) {
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
                progressAdapter = new OrderAdapter(mainActivity, progressOrderList, R.layout.activity_inprogress);
                recyclerView.setAdapter(progressAdapter);
            }

          //  progressCount = progressOrderList.size();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            if (!mainActivity.isFinishing() && mainActivity != null) {
                if (!Utils.isConnectedToInternet(mainActivity)) {
                    Toast.makeText(mainActivity, "Not connected to internet exception in progress", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }



}


