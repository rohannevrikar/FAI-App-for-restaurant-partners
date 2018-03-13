package tastifai.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static tastifai.restaurant.MainActivity.progressDialog;

/**
 * Created by Rohan Nevrikar on 27-01-2018.
 */

public class RestaurantMenu extends Fragment {
    View myView;
    RecyclerView recyclerView;
    private ArrayList<MenuModel> menuItemsList= new ArrayList<>();
    private String URL;
    private CallAPI api;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_menu,container,false);
        int restaurantId = mainActivity.getId();

        recyclerView = myView.findViewById(R.id.listMenu);
        URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantMenuItems/" + restaurantId;
        api = new CallAPI();
        api.execute(URL);

        return myView;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            if(!mainActivity.isFinishing()){
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

        }
        @Override
        protected void onPostExecute(String s) {
            try {
                progressDialog.dismiss();
                JSONArray menuArray = new JSONArray(s);
                for(int i=0;i<menuArray.length();i++){
                    JSONObject obj = menuArray.getJSONObject(i);
                    MenuModel menuModel = new MenuModel();
                    menuModel.setItemName(obj.getString("ItemName"));
                    menuModel.setItemId(obj.getString("ItemId"));
                    menuModel.setIsActive(obj.getString("IsActive"));
                    menuItemsList.add(menuModel);

//                    String GUID = orderObj.getString("GUID");
//                    orderModel.setDateTime(orderObj.getString("DateTime"));
//                    orderModel.setDeliverAt(orderObj.getString("DeliverAt"));
//                    orderModel.setItemName(orderObj.getString("ItemName"));
//                    orderModel.setItemPrice(orderObj.getString("ItemPrice"));
//                    orderModel.setQuantity(orderObj.getString("Quantity"));
//                    orderList.add(orderModel);

                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                MenuAdapter adapter = new MenuAdapter(getActivity(), menuItemsList);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {

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

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
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