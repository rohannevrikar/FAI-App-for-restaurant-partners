package com.example.rohannevrikar.foodcart;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.rohannevrikar.foodcart.MainActivity.CODE;
import static com.example.rohannevrikar.foodcart.MainActivity.orderList;
import static com.example.rohannevrikar.foodcart.OrderAdapter.timings;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    LinearLayoutManager orderManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder,container,false);
        getActivity().setTitle("Current Order");
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        CODE = 1;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderRecyclerView.setLayoutManager(layoutManager);
        OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orderList);


        orderRecyclerView.setAdapter(orderAdapter);
        return orderView;
    }




}
