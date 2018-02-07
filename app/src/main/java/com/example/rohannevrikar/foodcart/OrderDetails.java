package com.example.rohannevrikar.foodcart;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;

import static com.example.rohannevrikar.foodcart.MainActivity.CODE;
import static com.example.rohannevrikar.foodcart.OrderAdapter.timings;

/**
 * Created by Rohan Nevrikar on 02-02-2018.
 */

public class OrderDetails extends Fragment {
    View view;
    TextView name;
    RecyclerView orderDetailsRecyclerView;
    ImageView imageView;
    ImageView phoneImage;
    String contactNumber;
    private RelativeLayout relativeLayout;
    private final int textID = 99;
    private TextView acceptedAt;
    private TextView receivedAt;
    private TextView dispatchedAt;
    private TextView deliveredAt;
    private TextView preferences;

    private static final int REQUEST_CALL = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_details,container,false);
        orderDetailsRecyclerView = view.findViewById(R.id.orderDetailsRecyclerView);

        relativeLayout = view.findViewById(R.id.relative);
        imageView = view.findViewById(R.id.backImage);
        Bundle bundle = getArguments();
        if(bundle!=null){
            String customerName = bundle.getString("name");
            name = view.findViewById(R.id.txtName);
            name.setText(customerName);
            contactNumber = bundle.getString("phone");
        }
        ArrayList<Item> itemArrayList = (ArrayList<Item>) bundle.getSerializable("itemList");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderDetailsRecyclerView.setLayoutManager(layoutManager);
        CustomDetailsAdapter adapter = new CustomDetailsAdapter(getActivity(), itemArrayList);
        orderDetailsRecyclerView.setAdapter(adapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).previousFragment();
            }
        });
        receivedAt = view.findViewById(R.id.receivedAt);
        acceptedAt = view.findViewById(R.id.acceptedAt);
        dispatchedAt = view.findViewById(R.id.dispatchedAt);
        switch (CODE){
            case 1 :
                receivedAt.setText("Order Received At : " + timings.getReceivedAt());

                break;
            case 2 :
                receivedAt.setText("Order Received At : " + timings.getReceivedAt());
                acceptedAt.setText("Order Accepted At: " + timings.getAcceptedAt());
                break;
            case 3 : receivedAt.setText("Order Received At : " + timings.getReceivedAt());
                acceptedAt.setText("Order Accepted At: " + timings.getAcceptedAt());
                dispatchedAt.setText("Order Dispatched At: " + timings.getDispatchedAt());
                break;
        }


        phoneImage = view.findViewById(R.id.imagePhone);
        phoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(contactNumber);
            }
        });
        return view;
    }

    private void makePhoneCall(String contactNumber) {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

        }else{
            String dial = "tel:" + contactNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(contactNumber);
            }else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
