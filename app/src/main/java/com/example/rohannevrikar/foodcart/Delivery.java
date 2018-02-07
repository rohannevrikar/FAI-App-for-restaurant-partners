package com.example.rohannevrikar.foodcart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class Delivery extends Fragment {
    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_currentorder,container,false);
        RecyclerView recyclerView;
        ArrayList<Order> orderList= null;
        Bundle bundle = getArguments();
        MainActivity.CODE = 3;
        if(bundle != null){
            orderList = (ArrayList<Order>) bundle.getSerializable("deliveryOrders");
        }
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        OrderAdapter adapter = new OrderAdapter(getActivity(), orderList);
        recyclerView.setAdapter(adapter);
        return myView;
    }
}
