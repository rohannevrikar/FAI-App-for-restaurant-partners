package tastifai.restaurant;

import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.ProgressBar;
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

import static tastifai.restaurant.FragmentAdapter.mFragmentTitles;
import static tastifai.restaurant.MainActivity.adapter;
import static tastifai.restaurant.MainActivity.count;
import static tastifai.restaurant.MainActivity.currentCount;
import static tastifai.restaurant.MainActivity.progressDialog;
import static tastifai.restaurant.MainActivity.tabLayout;
import static tastifai.restaurant.MainActivity.viewPager;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    LinearLayoutManager orderManager;
    AlarmManager alarmManager;
    android.os.Handler customHandler;
    CallAPI api;
    private String URL;
    ArrayList<Order> orderList = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    public static Vibrator v;
    private TextView message;
    public static  MediaPlayer mediaPlayer;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder,container,false);
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm);
        message = orderView.findViewById(R.id.message);
        mediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
        v = (Vibrator)(getActivity().getSystemService(Context.VIBRATOR_SERVICE));
        int restaurantId = ((MainActivity)getContext()).getId();
        Log.d("CurrentOrder", "onCreateView: " + restaurantId);
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantCurrentOrders/" + restaurantId + "/7";
        customHandler = new android.os.Handler();
        customHandler.postDelayed(updateTimerThread, 0);


        return orderView;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(customHandler != null){
            customHandler.removeCallbacks(updateTimerThread);

        }
    }

    public class CallAPI extends AsyncTask<Object, Integer, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());

            //Toast.makeText(getActivity(), "" + orderList.size(), Toast.LENGTH_SHORT).show();
            if(!mainActivity.isFinishing()){
                if(count == 0){
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                }
            }


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {


            if((getActivity() != null)){
                adapter.changeFragmentTitle(0, "ORDER(" + orderList.size() + ")");
                tabLayout.setupWithViewPager(viewPager);
                mainActivity.orderCount();

            }


            count = 1;

            try {
                progressDialog.dismiss();
                orderList.clear();
                JSONArray orderArray = new JSONArray(s);
                for(int i=0;i<orderArray.length();i++){
                    JSONObject orderObj = orderArray.getJSONObject(i);
                    guidList.add(orderObj.getString("GUID"));

                    Log.d("Current Order", "onPostExecute: " + orderObj.getString("GUID"));

                }
                double totalPrice = 0;
                Set<String> guidUnique = new HashSet<>(guidList);
                for(String guid : guidUnique){
                    Log.d("Current Order unique", "onPostExecute: " + guid);
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

                    orderList.add(order);
                }
                if(orderList.size() == 0){
                    message.setVisibility(View.VISIBLE);
                }
                else
                    message.setVisibility(View.GONE);
                currentCount = orderList.size();
                if(orderList.size()>0){
                    try {
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();

                        }
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
//                        r.play();
                        long[] pattern = {0, 1000, 2000};

                        v.vibrate(pattern, 0);



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    v.cancel();
                    mediaPlayer.stop();
                }


                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                if(getActivity()!=null){
                    orderRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orderList, R.layout.activity_currentorder);
                    orderRecyclerView.setAdapter(orderAdapter);
                }



            } catch (JSONException e) {

                e.printStackTrace();
            } catch (NullPointerException e){
                v.cancel();
                mediaPlayer.stop();
                if(getActivity()!=null){
                    if(!mainActivity.isOnline()){
                        Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

//                        api = new CallAPI();
//                        api.execute(URL);
                    }
                }

            }
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
                    Log.d("Current Order", "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d("Current Order", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("Current Order", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("Current Order", "onPostExecute: " + myArray[0]);
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
