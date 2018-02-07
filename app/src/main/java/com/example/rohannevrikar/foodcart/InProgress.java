package com.example.rohannevrikar.foodcart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class InProgress extends Fragment {
    View myView;
    View view;
    private Button btnStartDelivery;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_currentorder,container,false);

        RecyclerView recyclerView;
        ArrayList<Order> orderList= null;
        Bundle bundle = getArguments();
        MainActivity.CODE = 2;
        if(bundle != null){
            orderList = (ArrayList<Order>) bundle.getSerializable("progressOrders");
            Log.d("InProgress", "onCreateView: Not null");
        }
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        OrderAdapter adapter = new OrderAdapter(getActivity(), orderList);
        recyclerView.setAdapter(adapter);
        return myView;
    }
}
