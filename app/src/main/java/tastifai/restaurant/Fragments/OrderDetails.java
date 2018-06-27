package tastifai.restaurant.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.CustomDetailsAdapter;
import tastifai.restaurant.Models.Item;

import static tastifai.restaurant.Activities.LoginActivity.serviceMediaPlayer;
import static tastifai.restaurant.Activities.MainActivity.deliveryCharge;
import static tastifai.restaurant.Activities.MainActivity.deliveryCount;
import static tastifai.restaurant.Activities.MainActivity.discount;
import static tastifai.restaurant.Activities.MainActivity.guid;
import static tastifai.restaurant.Activities.MainActivity.helpLine;
import static tastifai.restaurant.Activities.MainActivity.mediaPlayer;
import static tastifai.restaurant.Activities.MainActivity.restaurantEarning;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.totalPrice;
import static tastifai.restaurant.Activities.MainActivity.totalUser;
import static tastifai.restaurant.Services.CheckNewOrdersService.isMediaPlayerRunning;

/**
 * Created by Rohan Nevrikar on 02-02-2018.
 */

public class OrderDetails extends Fragment {
    View view;
    TextView name;
    TextView txtAddress;

    RecyclerView orderDetailsRecyclerView;
    ImageView imageView;
    ImageView phoneImage;
    String contactNumber;
    private RelativeLayout relativeLayout;
    private final int textID = 99;
    private TextView acceptedAt;
    private ImageView imageNav;
    private TextView receivedAt;
    private TextView dispatchedAt;
    private TextView deliveredAt;
    private TextView preferences;
    private double userLat;
    private double userLng;
    private int layout;
    private Button button;
    private boolean navigationAvailable;
    private TextView deliveryChargeTextView;
    private TextView customerPriceTextView;
    private TextView restaurantEarningTextView;


    private static final String TAG = "OrderDetails";
    private TextView promoText;
    private TextView promo;


    private static final int REQUEST_CALL = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_details, container, false);
        orderDetailsRecyclerView = view.findViewById(R.id.orderDetailsRecyclerView);
        relativeLayout = view.findViewById(R.id.relative);
        imageView = view.findViewById(R.id.backImage);
        button = view.findViewById(R.id.button);
        imageNav = view.findViewById(R.id.imageNav);
        phoneImage = view.findViewById(R.id.imagePhone);
        promoText = view.findViewById(R.id.promoText);
        promo = view.findViewById(R.id.promo);

        String string = "\u20B9";
        byte[] utf8 = null;
        try {
            utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (discount != 0) {
            promoText.setVisibility(View.VISIBLE);
            promo.setVisibility(View.VISIBLE);
            promo.setText(string + "" + String.format("%.2f", discount));
        } else {
            promoText.setVisibility(View.GONE);
            promo.setVisibility(View.GONE);
        }
        deliveryChargeTextView = view.findViewById(R.id.deliveryCharge);
        deliveryChargeTextView.setText(string + "" + String.format("%.2f", deliveryCharge));
        customerPriceTextView = view.findViewById(R.id.customerPrice);
        customerPriceTextView.setText(string + "" + String.format("%.2f", totalUser));
        restaurantEarningTextView = view.findViewById(R.id.restaurantEarning);
        restaurantEarningTextView.setText(string + "" + String.format("%.2f", restaurantEarning));

        Log.d(TAG, "onCreateView: ");
        Bundle bundle = getArguments();
        if (bundle != null) {
            String customerName = bundle.getString("name");
            name = view.findViewById(R.id.txtName);
            txtAddress = view.findViewById(R.id.txtAddress);
            navigationAvailable = bundle.getBoolean("navigationAvailable");
            layout = bundle.getInt("layout");
            name.setText(customerName);
            contactNumber = bundle.getString("contact");
            Log.d(TAG, "onCreateView: " + contactNumber + name);
            String address = bundle.getString("address");
            txtAddress.setText(address);
            userLat = bundle.getDouble("userLat");
            userLng = bundle.getDouble("userLng");
            Log.d(TAG, "onCreateView: " + userLat + " " + userLng);
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePhoneCall(contactNumber);
                }
            });
        }
        ArrayList<Item> itemArrayList = (ArrayList<Item>) bundle.getSerializable("itemList");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderDetailsRecyclerView.setLayoutManager(layoutManager);
        CustomDetailsAdapter adapter = new CustomDetailsAdapter(getActivity(), itemArrayList);
        orderDetailsRecyclerView.setAdapter(adapter);
        if (!navigationAvailable)
            imageNav.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).previousFragment();
            }
        });
        imageNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlAddress = "http://maps.google.com/maps?q=" + userLat + "," + userLng + "(" + "Deliver here" + ")&iwloc=A&hl=es";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(helpLine);
            }
        });
//        if(layout == R.layout.activity_currentorder){
//            button.setVisibility(View.VISIBLE);
//            button.setText("Reject Order");
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(mediaPlayer != null){
//                        if(mediaPlayer.isPlaying())
//                            mediaPlayer.pause();
//                    }
//                    Log.d(TAG, "onClick: guid: " + guid);
//
//                    new PostAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostRejectOrders/" + restaurantId + "/" + guid);
//
//                }
//            });
//
//        }else if(layout == R.layout.activity_inprogress){
//            button.setVisibility(View.GONE);
//        }else{
//            button.setVisibility(View.VISIBLE);
//            button.setText("Can not deliver");
//
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    new PostAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostNotDeliveredOrder/" + restaurantId + "/" + guid);
//
//                }
//            });
//
//        }
//


        return view;
    }

    private void makePhoneCall(String contactNumber) {

            String dial = "tel:" + contactNumber;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));

    }



    private class PostAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(getActivity(), "Order Rejected", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).previousFragment();

        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                Log.d(TAG, "doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                //connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
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
                Log.d("OrderAdapter", "Response Code: " + responseCode);
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
}
