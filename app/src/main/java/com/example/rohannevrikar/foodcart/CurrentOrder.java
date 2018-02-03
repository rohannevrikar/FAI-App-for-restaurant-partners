package com.example.rohannevrikar.foodcart;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment {
    View orderView;
    View itemView;
    RecyclerView orderRecyclerView;
    LinearLayoutManager orderManager;
    ArrayList<Order> orderList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderList = new ArrayList<>();

        Order order1 = new Order();
        order1.setCustomerName("Tastifai");
        order1.setContactNumber("7887543650");
        order1.setDeliveryAddress("MSH1202");
        orderList.add(order1);
        Order order = new Order();
        order.setCustomerName("Rohan");
        order.setContactNumber("8733000127");
        order.setDeliveryAddress("D21R30");
        orderList.add(order);
        orderView = inflater.inflate(R.layout.activity_currentorder,container,false);
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderRecyclerView.setLayoutManager(layoutManager);
        OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orderList);
        orderRecyclerView.setAdapter(orderAdapter);
        return orderView;
    }




}
