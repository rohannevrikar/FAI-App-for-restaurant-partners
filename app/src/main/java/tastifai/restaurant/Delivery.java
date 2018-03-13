package tastifai.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
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

import static tastifai.restaurant.MainActivity.adapter;
import static tastifai.restaurant.MainActivity.count;
import static tastifai.restaurant.MainActivity.count_d;
import static tastifai.restaurant.MainActivity.deliveryCount;
import static tastifai.restaurant.MainActivity.tabLayout;
import static tastifai.restaurant.MainActivity.viewPager;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class Delivery extends Fragment {
    View myView;
    private ArrayList<Order> deliveryList;
    private OrderAdapter deliveryAdapter;
    private RecyclerView recyclerView;
    private  CallAPI api;
    private android.os.Handler customHandler;
    private String URL;
    private FragmentAdapter fragmentAdapter;
    private TextView message;
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_delivery,container,false);
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        int restaurantId = ((MainActivity)getContext()).getId();
        deliveryList = new ArrayList<>();
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        message = myView.findViewById(R.id.message);
        URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantDeliveryOrders/" + restaurantId + "/4";

        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        if(deliveryList.size() == 0){
            message.setVisibility(View.VISIBLE);
        }
        else
            message.setVisibility(View.GONE);
        return myView;
    }
    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            if(getActivity()!=null){
                api = new CallAPI();
                api.execute(URL);

                //write here whaterver you want to repeat
                customHandler.postDelayed(this, 5000);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(customHandler != null){
            customHandler.removeCallbacks(updateTimerThread);

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if(deliveryList.size() == 0){
//                progressDialog = new ProgressDialog(getActivity());
//                progressDialog.setMessage("Loading...");
//                progressDialog.show();
//            }
        }
        @Override
        protected void onPostExecute(String s) {
            if(count_d == 0){

                if((getActivity() != null)){
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
                for(int i=0;i<orderArray.length();i++){
                    JSONObject orderObj = orderArray.getJSONObject(i);
                    guidList.add(orderObj.getString("GUID"));
                }
                double totalPrice = 0;
                Set<String> guidUnique = new HashSet<>(guidList);
                for(String guid : guidUnique){
                    ArrayList<Item> itemList = new ArrayList<>();
                    Order order = new Order();

                    totalPrice = 0;

                    for(int i=0;i<orderArray.length(); i++){
                        JSONObject orderObj = orderArray.getJSONObject(i);

                        if(orderObj.getString("GUID").equals(guid)){
                            Item item = new Item();
                            item.setItem(orderObj.getString("ItemName"));
                            totalPrice = totalPrice + (Double.parseDouble(orderObj.getString("ItemPrice")) * Double.parseDouble(orderObj.getString("Quantity")));

                            item.setPrice(orderObj.getString("ItemPrice"));
                            item.setQty(orderObj.getString("Quantity"));
                            itemList.add(item);
                            order.setCustomerName(orderObj.getString("UserFirstName"));
                            order.setContactNumber(orderObj.getString("UserContactNumber"));
                            order.setGuid(orderObj.getString("GUID"));
                            order.setDateTime( mainActivity.convertDateTime(orderObj.getString("DateTime")));
                            order.setDeliveryAddress(orderObj.getString("DeliverAt"));
                            order.setItemList(itemList);
                        }

                    }
                    order.setTotalPrice(String.valueOf(totalPrice));

                    deliveryList.add(order);
                }
                if(deliveryList.size() == 0){
                    message.setVisibility(View.VISIBLE);
                }
                else
                    message.setVisibility(View.GONE);
                Log.d("Delivery", "onPostExecute: " + deliveryList.size());
                deliveryCount = deliveryList.size();
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                deliveryAdapter = new OrderAdapter(getActivity(), deliveryList, R.layout.activity_delivery);

                recyclerView.setAdapter(deliveryAdapter);
                deliveryCount = deliveryList.size();

            } catch (JSONException e) {
//                api = new CallAPI();
//                api.execute(URL);
//                Toast.makeText(getActivity(), "Exception caught, calling api", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }catch (NullPointerException e){
                if(getActivity()!=null){
                    if(!mainActivity.isOnline()){
                        Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

//                        api = new CallAPI();
//                        api.execute(URL);
                    }
                }

            }
        }
        public int getSize(){
            return deliveryList.size();
        }
        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("Delivery", "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d("Delivery", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("Delivery", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("Delivery", "onPostExecute: " + myArray[0]);
                    return builder.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}
