package tastifai.restaurant.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Adapters.OrderHistoryAdapter;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

import static android.content.ContentValues.TAG;
import static tastifai.restaurant.Activities.MainActivity.progressDialog;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;

/**
 * Created by Rohan Nevrikar on 27-01-2018.
 */

public class OrderHistory extends Fragment{
    View myView;
    private Calendar toCalendar;
    private Calendar fromCalendar;
    private EditText from;
    private EditText to;
    private int from_day, from_month, from_year;
    private int to_day, to_month, to_year;
    private DatePickerDialog.OnDateSetListener fromDataSetListener;
    private DatePickerDialog.OnDateSetListener toDataSetListener;
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;
    private ArrayList<Order> orderHistoryList = new ArrayList<>();
    private RecyclerView orderHistoryRecyclerView;
    private OrderHistoryAdapter adapter;
    private Button btnSubmit;
    private CallAPI api;
    private String URL;
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_orderhistory,container,false);
        from = (EditText)myView.findViewById(R.id.from);
        to = (EditText)myView.findViewById(R.id.to);
        orderHistoryRecyclerView = myView.findViewById(R.id.order_history_recyclerview);
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCalendar = Calendar.getInstance();
                from_day= fromCalendar.get(Calendar.DAY_OF_MONTH);
                from_month = fromCalendar.get(Calendar.MONTH);
                from_year = fromCalendar.get(Calendar.YEAR);
                fromDataSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Log.d(TAG, "onDateSet: " + day + "/" + month + "/" + year);
                        month = month + 1;
                        from.setText(year + "-" + month + "-" + day);
                        from.setGravity(Gravity.CENTER);

                    }
                };
                datePickerDialogFrom = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, fromDataSetListener, from_year, from_month, from_day);
                datePickerDialogFrom.show();

            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCalendar = Calendar.getInstance();
                to_day= toCalendar.get(Calendar.DAY_OF_MONTH);
                to_month = toCalendar.get(Calendar.MONTH);
                to_year = toCalendar.get(Calendar.YEAR);
                Log.d(TAG, "onClick: ");
                toDataSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        to.setText(year + "-" + month + "-" + day);
                        to.setGravity(Gravity.CENTER);

                    }
                };
                datePickerDialogTo = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, toDataSetListener, to_year, to_month, to_day);
                datePickerDialogTo.show();


            }
        });
        btnSubmit = myView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderHistoryList.clear();
                getOrders();

            }
        });
        return myView;
    }

    private void getOrders() {
        //URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetSearchOrdersData/" + restaurantId + "/" + from.getText().toString() + "/" + to.getText().toString() + "/5";
        if(Utils.isConnectedToInternet(getActivity())){
            api = new CallAPI();
            api.execute(Constants.URL + "/GetSearchOrdersData" + restaurantId + "/" + from.getText().toString() + "/" + to.getText().toString() + "/5");
        }else{
            Utils.setUpAlert(getActivity(), new getAPIResponse() {
                @Override
                public void OnRetry(DialogInterface dialogInterface) {
                    //Toast.makeText(getActivity(), "Trying to connect to internet", Toast.LENGTH_LONG).show();
                    getOrders();
                }
            });
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
            //progressDialog = new ProgressDialog(getActivity());
            if(!mainActivity.isFinishing()){
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
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

                            item.setPrice(orderObj.getDouble("ItemPrice"));
                            item.setQty(orderObj.getString("Quantity"));
                            itemList.add(item);
                            order.setCustomerName(orderObj.getString("UserFirstName"));
                            order.setGuid(orderObj.getString("GUID"));
                            order.setDateTime( mainActivity.convertDateTime(orderObj.getString("DateTime")));
                            order.setDeliveryAddress(orderObj.getString("DeliverAt"));
                            order.setItemList(itemList);
                        }

                    }
                    order.setTotalPrice(totalPrice);

                    orderHistoryList.add(order);

                }

                orderHistoryRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                adapter = new OrderHistoryAdapter(getActivity(), orderHistoryList);
                orderHistoryRecyclerView.setAdapter(adapter);



            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                if(getActivity()!=null){
                    if(!mainActivity.isOnline()){
                        Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();

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
                    Log.d("OrderHistory", "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d("OrderHistory", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("OrderHistory", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("OrderHistory", "onPostExecute: " + myArray[0]);
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
