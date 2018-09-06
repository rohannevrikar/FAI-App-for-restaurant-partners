package tastifai.restaurant.Services;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Interfaces.OrderListener;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;

import static android.content.ContentValues.TAG;

/**
 * Created by Rohan Nevrikar on 08-02-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public OrderListener orderListener;
    public static ArrayList<Order> orderList;
    private LocalBroadcastManager broadcaster;
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

//    public MyFirebaseMessagingService() {
//    }
//
//    public MyFirebaseMessagingService(OrderListener orderListener) {
//        Log.d(TAG, "setListener: ");
//        this.orderListener = orderListener;
//    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        orderList = new ArrayList<>();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        JSONObject object = new JSONObject(remoteMessage.getData());
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        ArrayList<String> guidList = new ArrayList<>();

        try {
//            String title = object.getString("title");
            String body = object.getString("body");
            JSONArray orderArray = new JSONArray(body);
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
                        order.setDateTime(orderObj.getString("DateTime"));
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
            Intent intent = new Intent("MyData");
            Bundle args = new Bundle();
            args.putSerializable("arraylist", orderList);

            intent.putExtra("bundle", args);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);            //orderListener.orderCallBack(orderList);

////            String guid = object.getString("guid");
//  //          String itemName = object.getString("itemName");
//
            Log.d(TAG, "onMessageReceived: " + body);


            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

              //  Utils.notificationBuilder(this, title, body);


            }

            if (remoteMessage.getNotification() != null) {
               // Utils.notificationBuilder(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
