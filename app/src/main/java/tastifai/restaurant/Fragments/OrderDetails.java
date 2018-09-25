package tastifai.restaurant.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.example.rohannevrikar.restaurant.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Adapters.CustomDetailsAdapter;
import tastifai.restaurant.Models.Item;

import static tastifai.restaurant.Activities.MainActivity.helpLine;

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
    private double discount;
    public  double restaurantEarning;
    public double totalUser;
    public double deliveryCharge;

    private int layout;
    private Button button;
    private boolean navigationAvailable;
    private TextView deliveryChargeTextView;
    private TextView customerPriceTextView;
    private TextView restaurantEarningTextView;


    private static final String TAG = "OrderDetails";
    private TextView promoText;
    private TextView promo;

    private MainActivity mainActivity;

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
            deliveryCharge = bundle.getDouble("deliveryCharge");
            discount = bundle.getDouble("discount");
            totalUser= bundle.getDouble("totalUser");
            restaurantEarning= bundle.getDouble("restaurantEarning");
            deliveryChargeTextView = view.findViewById(R.id.deliveryCharge);
            deliveryChargeTextView.setText(string + "" + String.format("%.2f", deliveryCharge));
            customerPriceTextView = view.findViewById(R.id.customerPrice);
            customerPriceTextView.setText(string + "" + String.format("%.2f", totalUser));
            restaurantEarningTextView = view.findViewById(R.id.restaurantEarning);
            restaurantEarningTextView.setText(string + "" + String.format("%.2f", restaurantEarning));
            if (discount != 0) {
                promoText.setVisibility(View.VISIBLE);
                promo.setVisibility(View.VISIBLE);
                promo.setText(string + "" + String.format("%.2f", discount));
            } else {
                promoText.setVisibility(View.GONE);
                promo.setVisibility(View.GONE);
            }

        }
        ArrayList<Item> itemArrayList = (ArrayList<Item>) bundle.getSerializable("itemList");
        Log.d(TAG, "onCreateView: itemArrayList" + itemArrayList.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        orderDetailsRecyclerView.setLayoutManager(layoutManager);
        CustomDetailsAdapter adapter = new CustomDetailsAdapter(mainActivity, itemArrayList);
        orderDetailsRecyclerView.setAdapter(adapter);
        if (!navigationAvailable)
            imageNav.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mainActivity).previousFragment();
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


        return view;
    }

    private void makePhoneCall(String contactNumber) {

            String dial = "tel:" + contactNumber;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }
}
