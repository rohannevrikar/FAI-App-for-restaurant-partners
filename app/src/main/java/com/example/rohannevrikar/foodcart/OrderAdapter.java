package com.example.rohannevrikar.foodcart;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.example.rohannevrikar.foodcart.MainActivity.CODE;
import static com.example.rohannevrikar.foodcart.MainActivity.currentCount;
import static com.example.rohannevrikar.foodcart.MainActivity.deliveryCount;
import static com.example.rohannevrikar.foodcart.MainActivity.deliveryOrders;
import static com.example.rohannevrikar.foodcart.MainActivity.progressCount;
import static com.example.rohannevrikar.foodcart.MainActivity.progressOrders;

/**
 * Created by Rohan Nevrikar on 01-02-2018.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final Context mContext;
    private View view;
    private final LayoutInflater layoutInflater;
    private static final String TAG = "message";
    private ArrayList<Order> orderList;
    private RecyclerView mRecyclerView;
    public static TimePOJO timings = null;
    public OrderAdapter(Context mContext, ArrayList<Order> orderList) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.orderList = orderList;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
             view = layoutInflater.inflate(R.layout.order_row, parent, false);




        return new ViewHolder(view, mContext, orderList);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //final boolean isExpanded = position==mExpandedPosition;
        holder.customer.setText(orderList.get(position).getCustomerName() + " \n" + orderList.get(position).getDeliveryAddress() + " \n" + orderList.get(position).getContactNumber());
        final ArrayList<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setItem("Cheese Parantha");
        item.setQty("1");
        item.setPrice("70");
        itemList.add(item);
        Item item1 = new Item();
        item1.setItem("Chicken Hakka Noodles");
        item1.setQty("2");
        item1.setPrice("90");
        itemList.add(item1);
        Item item2 = new Item();
        item2.setItem("Chicken Sandwich");
        item2.setQty("3");
        item2.setPrice("60");
        itemList.add(item2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        CustomListAdapter adapter = new CustomListAdapter(mContext, itemList);
        holder.itemRecyclerView.setAdapter(adapter);
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).orderDetailsFragment(orderList.get(position).getCustomerName(), itemList, orderList.get(position).getContactNumber());
            }
        });
        timings = new TimePOJO();
        if(MainActivity.CODE == 1){
            holder.dynamicButton.setText("ACCEPT");
            timings.setReceivedAt("04:20PM");
        }
        else if(CODE == 2){
            holder.dynamicButton.setText("START DELIVERY");
            timings.setReceivedAt("04:20PM");
            timings.setAcceptedAt("04:22PM");

        }
        else
        {
            holder.dynamicButton.setText("DELIVERED");
            timings.setReceivedAt("04:20PM");
            timings.setAcceptedAt("04:22PM");
            timings.setDispatchedAt("04:55PM");
        }

            holder.dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CODE == 1){
                    progressOrders.add(orderList.get(position));
                    progressCount = progressOrders.size();
                    orderList.remove(position);
                    currentCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).orderCount();

                }
                else if(CODE == 2){
                    deliveryOrders.add(orderList.get(position));
                    deliveryCount = deliveryOrders.size();
                    orderList.remove(position);
                    progressCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).orderCount();


                }
                else {
                    orderList.remove(position);
                    deliveryCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).orderCount();

                }




            }
        });





        //setListViewHeightBasedOnItems(holder.orderListView);

//        holder.expandView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
//        holder.expandView.setActivated(isExpanded);
//        if (isExpanded)
//            previousExpandedPosition = position;
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mExpandedPosition = isExpanded ? -1:position;
//                notifyItemChanged(previousExpandedPosition);
//                notifyItemChanged(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView customer;

        public final ArrayList<Order> orderList;
        private View expandView;
        private RecyclerView itemRecyclerView;
        private Button btnDetails;
        private Button dynamicButton;



        public final Context mContext;

        public ViewHolder(View itemView, Context mContext, ArrayList<Order> orderList) {
            super(itemView);
            this.mContext = mContext;
            this.orderList = orderList;
            btnDetails = itemView.findViewById(R.id.details);
            customer = (TextView)itemView.findViewById(R.id.txtCustomer);
            itemRecyclerView = (RecyclerView)itemView.findViewById(R.id.orderRecyclerView);
            dynamicButton = itemView.findViewById(R.id.dynamicButton);
        }


    }
}