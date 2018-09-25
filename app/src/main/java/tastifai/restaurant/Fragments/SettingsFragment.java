package tastifai.restaurant.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import tastifai.restaurant.Activities.LoginActivity;
import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Services.OrderService;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;

import static android.content.Context.MODE_PRIVATE;
import static tastifai.restaurant.Activities.MainActivity.progressDialog;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;


/**
 * Created by Rohan Nevrikar on 13-02-2018.
 */

public class SettingsFragment extends Fragment {
    private View view;
    private Button logOut;
    private Switch aSwitch;
    private MainActivity mainActivity;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings, container, false);
        logOut = view.findViewById(R.id.logout);
        aSwitch = view.findViewById(R.id.onlineSwitch);

        getRestaurantStatus();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    postRestaurantStatusOnline();
                } else {
                    postRestaurantStatusOffline();
                }
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                mainActivity.stopService(new Intent(mainActivity, OrderService.class));
                MainActivity.itemList.clear();
                FragmentAdapter.mFragments.clear();
                FragmentAdapter.mFragmentTitles.clear();
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                startActivity(intent);
                ((MainActivity) mainActivity).finish();
            }
        });
        return view;
    }

    private void getRestaurantStatus() {
        //String URLGet = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantStatus/" + restaurantId;
        if (Utils.isConnectedToInternet(mainActivity)) {
            CallAPIGet apiGet = new CallAPIGet();
            apiGet.execute(Constants.URL + "GetRestaurantStatus/" + restaurantId);
        } else {
            Utils.setUpAlert(mainActivity, new getAPIResponse() {
                @Override
                public void OnRetry(DialogInterface dialogInterface) {
                    getRestaurantStatus();
                }
            });

        }

    }

    private void postRestaurantStatusOnline() {
        //String URLGet = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantStatus/" + restaurantId;
        if (Utils.isConnectedToInternet(mainActivity)) {
            CallAPI api = new CallAPI();
            api.execute(Constants.URL + "PostToggleRestaurant/" + restaurantId + "/1");
            Toast.makeText(mainActivity, "You are online now", Toast.LENGTH_SHORT).show();
        } else {
            Utils.setUpAlert(mainActivity, new getAPIResponse() {
                @Override
                public void OnRetry(DialogInterface dialogInterface) {
                    postRestaurantStatusOnline();
                }
            });

        }

    }

    private void postRestaurantStatusOffline() {
        //String URLGet = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantStatus/" + restaurantId;
        if (Utils.isConnectedToInternet(mainActivity)) {
            CallAPI api = new CallAPI();
            api.execute(Constants.URL + "PostToggleRestaurant/" + restaurantId + "/0");
            Toast.makeText(mainActivity, "You are offline now", Toast.LENGTH_SHORT).show();
        } else {
            if (!mainActivity.isFinishing() && mainActivity != null) {
                Utils.setUpAlert(mainActivity, new getAPIResponse() {
                    @Override
                    public void OnRetry(DialogInterface dialogInterface) {
                        postRestaurantStatusOffline();
                    }
                });

            }

        }

    }


    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mainActivity != null) {
                progressDialog = new ProgressDialog(mainActivity);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mainActivity, "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                Log.d("MenuAdapter", "doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                //connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.connect();
//                InputStream istream = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Log.d("OrderAdapter", "doInBackground: " + line);
//                    builder.append(line);
//
//
//
//                }
                int responseCode = connection.getResponseCode();
                Log.d("MenuAdapter", "Response Code: " + responseCode);
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("OrderAdapter", "doInBackground: " + responseCode + " " + builder.toString());
//                    String[] myArray = builder.toString().split(",");
//                    Log.d("OrderAdapter", "onPostExecute: " + myArray[0]);
//                    return builder.toString();
//                }
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

    private class CallAPIGet extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();


        @Override
        protected void onPostExecute(String s) {
            try {
                Log.d("SettingsFramgment", "onPostExecute: " + s);
                if (s.equals("true")) {
                    aSwitch.setChecked(true);
                } else
                    aSwitch.setChecked(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
                getRestaurantStatus();
            }

        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");

                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("SettingsFragment", "doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d("SettingsFragment", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("SettingsFragment", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("SettingsFragment", "onPostExecute: " + myArray[0]);
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
