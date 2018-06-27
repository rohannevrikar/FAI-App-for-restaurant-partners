package tastifai.restaurant.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.ErrorActivity;
import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Adapters.OrderAdapter;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static tastifai.restaurant.Activities.MainActivity.adapter;
import static tastifai.restaurant.Activities.MainActivity.count_d;
import static tastifai.restaurant.Activities.MainActivity.deliveryCharge;
import static tastifai.restaurant.Activities.MainActivity.deliveryCount;
import static tastifai.restaurant.Activities.MainActivity.discount;
import static tastifai.restaurant.Activities.MainActivity.restaurantEarning;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.tabLayout;
import static tastifai.restaurant.Activities.MainActivity.totalUser;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_delivery, container, false);
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        int restaurantId = ((MainActivity) getContext()).getId();
        deliveryList = new ArrayList<>();
        recyclerView = myView.findViewById(R.id.orderRecyclerView);

        message = myView.findViewById(R.id.message);

        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        if (deliveryList.size() == 0) {
            message.setVisibility(View.VISIBLE);
        } else
            message.setVisibility(View.GONE);
        return myView;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (getActivity() != null) {

                if (!getActivity().isFinishing()) {
                    if (Utils.isConnectedToInternet(getActivity())) {
                        CommonAsyncTask asyncTask = new CommonAsyncTask();
                        asyncTask.delegate = (CurrentOrderResponse) Delivery.this;
                        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "GetRestaurantDeliveryOrders/" + restaurantId + "/4");
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

    private void getAPIData() {
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (customHandler != null) {
            customHandler.removeCallbacks(updateTimerThread);

        }
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
        if (count_d == 0) {

            if ((getActivity() != null)) {
                adapter.changeFragmentTitle(2, "DELIVERY(" + deliveryList.size() + ")");
                tabLayout.setupWithViewPager(viewPager);
                mainActivity.orderCount();

            }
            count_d = 1;

        }
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
                        discount = orderObj.getDouble("DiscountPrices");
                        order.setTotalUser(orderObj.getDouble("TotalUser"));
                        totalUser = orderObj.getDouble("TotalUser");
                        restaurantEarning = orderObj.getDouble("RestaurantEarningsTotal");
                        deliveryCharge = orderObj.getDouble("DeliveryCharges");
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
            Log.d(TAG, "processFinish: list size: " + deliveryList.size());
            if (deliveryList.size() == 0) {
                message.setVisibility(View.VISIBLE);
            } else
                message.setVisibility(View.GONE);
            Log.d("Delivery", "onPostExecute: " + deliveryList.size());
            deliveryCount = deliveryList.size();
            recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            deliveryAdapter = new OrderAdapter(getActivity(), deliveryList, R.layout.activity_delivery);

            recyclerView.setAdapter(deliveryAdapter);
            deliveryCount = deliveryList.size();

        } catch (Exception e) {
            if (getActivity() != null) {
                if (!mainActivity.isOnline()) {
                    Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

//                        api = new CallAPI();
//                        api.execute(URL);
                }
            }
//            e.printStackTrace();
//            Intent intent = new Intent(getActivity(), ErrorActivity.class);
//            startActivity(intent);
        }
    }


}
