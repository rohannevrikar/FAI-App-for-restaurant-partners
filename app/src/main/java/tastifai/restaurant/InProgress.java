package tastifai.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import static tastifai.restaurant.MainActivity.count_p;
import static tastifai.restaurant.MainActivity.currentCount;
import static tastifai.restaurant.MainActivity.progressCount;
import static tastifai.restaurant.MainActivity.tabLayout;
import static tastifai.restaurant.MainActivity.viewPager;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class InProgress extends Fragment {
    View myView;
    View view;
    private Button btnStartDelivery;
    private ArrayList<Order> progressOrderList = new ArrayList<>();
    private OrderAdapter progressAdapter;
    private RecyclerView recyclerView;
    private CallAPI api;
    private String URL;
    private TextView message;
    private MainActivity mainActivity;
    private android.os.Handler customHandler;
    private ProgressDialog progressDialog;
    private FragmentAdapter fragmentAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_inprogress,container,false);
        //Bundle bundle = getArguments();
//        if(bundle != null){
//            orderList = (ArrayList<Order>) bundle.getSerializable("progressOrders");
//            Log.d("InProgress", "onCreateView: Not null");
//        }
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        message = myView.findViewById(R.id.message);
        int restaurantId = ((MainActivity)getContext()).getId();
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        if(progressOrderList.size() == 0){
            message.setVisibility(View.VISIBLE);
        }
        else
            message.setVisibility(View.GONE);

        URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantInProgressOrders/" + restaurantId + "/1";

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
//            if(progressOrderList.size() == 0){
//                progressDialog = new ProgressDialog(getActivity());
//                progressDialog.setMessage("Loading...");
//                progressDialog.show();
//            }

        }

        @Override
        protected void onPostExecute(String s) {
           // progressDialog.dismiss();
            if(count_p == 0){

                if((getActivity() != null)){
                    adapter.changeFragmentTitle(1, "PROGRESS(" + progressOrderList.size() + ")");
                    tabLayout.setupWithViewPager(viewPager);
                    mainActivity.orderCount();

                }
                count_p = 1;

            }

            try {
                progressOrderList.clear();
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

                    progressOrderList.add(order);

                }
                if(progressOrderList.size() == 0){
                    message.setVisibility(View.VISIBLE);
                }
                else
                    message.setVisibility(View.GONE);
                progressCount = progressOrderList.size();
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                if(getActivity() != null){
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    progressAdapter = new OrderAdapter(getActivity(), progressOrderList, R.layout.activity_inprogress);
                    recyclerView.setAdapter(progressAdapter);
                }

                progressCount = progressOrderList.size();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
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
            return progressOrderList.size();
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
                    Log.d("InProgress", "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d("InProgress", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("InProgress", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("InProgress", "onPostExecute: " + myArray[0]);
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


