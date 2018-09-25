package tastifai.restaurant.Fragments;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.MainActivity.deliveryCount;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.tabLayout;
import static tastifai.restaurant.Activities.MainActivity.viewPager;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class Delivery extends Fragment implements CurrentOrderResponse {
    View myView;
    private ArrayList<Order> deliveryList;
    private OrderAdapter deliveryAdapter;
    private RecyclerView recyclerView;
    private android.os.Handler customHandler;
    private FragmentAdapter fragmentAdapter;
    private TextView message;
    private MainActivity mainActivity;

    private static final String TAG = "Delivery";
    public static final String IN_DELIVERY_ORDER_INTENT = "inDeliveryOrder";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_delivery, container, false);
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        //int restaurantId = ((MainActivity) getContext()).getId();
        deliveryList = new ArrayList<>();
        recyclerView = myView.findViewById(R.id.orderRecyclerView);

        message = myView.findViewById(R.id.message);

        if (deliveryList.size() == 0) {
            message.setVisibility(View.VISIBLE);
        } else
            message.setVisibility(View.GONE);
        return myView;
    }

    private void updateInDeliveryOrders() {
        if (mainActivity != null && !mainActivity.isFinishing()) {
            if (Utils.isConnectedToInternet(mainActivity)) {
                CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
                asyncTask.delegate = (CurrentOrderResponse) Delivery.this;
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantDeliveryOrders/" + restaurantId + "/4");
                Log.d(TAG, "run: calling api");

            } else {
                Toast.makeText(mainActivity, "Not connected to internet runnable delivery", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateInDeliveryOrders();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        updateInDeliveryOrders();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver((mMessageReceiver),
                new IntentFilter(IN_DELIVERY_ORDER_INTENT)
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }


    @Override
    public void processFinish(String s) {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        //progressDialog.dismiss();
        deliveryList.clear();
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
                        order.setinstruction(orderObj.getString("DeliveryInstructions"));
                        order.setDiscount(orderObj.getDouble("DiscountPrices"));
                        order.setTotalUser(orderObj.getDouble("TotalUser"));
                        order.setRestaurantEarnings(orderObj.getDouble("RestaurantEarningsTotal"));
                        order.setDeliveryCharge(orderObj.getDouble("DeliveryCharges"));
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

                deliveryList.add(order);
            }

            if ((mainActivity != null)) {
                //adapter.changeFragmentTitle(2, "DELIVERY(" + deliveryList.size() + ")");
                deliveryCount = deliveryList.size();
                tabLayout.setupWithViewPager(viewPager);
                mainActivity.orderCount();

            }

            Log.d(TAG, "processFinish: list size: " + deliveryList.size());
            if (deliveryList.size() == 0) {
                message.setVisibility(View.VISIBLE);
            } else
                message.setVisibility(View.GONE);
            Log.d("Delivery", "onPostExecute: " + deliveryList.size());
            recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
            deliveryAdapter = new OrderAdapter(mainActivity, deliveryList, R.layout.activity_delivery);

            recyclerView.setAdapter(deliveryAdapter);
            // deliveryCount = deliveryList.size();

        } catch (Exception e) {
            if (!mainActivity.isFinishing() && mainActivity != null) {
                if (!Utils.isConnectedToInternet(mainActivity)) {
                    Toast.makeText(mainActivity, "Not connected to internet exception delivery", Toast.LENGTH_SHORT).show();

                    //Toast.makeText(mainActivity, "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

//                        api = new CallAPI();
//                        api.execute(URL);
                }
            }
//            e.printStackTrace();
//            Intent intent = new Intent(mainActivity, ErrorActivity.class);
//            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;

    }
}

