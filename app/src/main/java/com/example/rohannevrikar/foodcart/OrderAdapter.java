package com.example.rohannevrikar.foodcart;

import android.content.Context;
import android.content.Intent;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Nevrikar on 01-02-2018.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private static final String TAG = "message";
    private ArrayList<Order> orderList;
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;
    private ListView orderListView;

    public OrderAdapter(Context mContext, ArrayList<Order> orderList) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.orderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.order_row, parent, false);
        return new ViewHolder(itemView, mContext, orderList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final boolean isExpanded = position==mExpandedPosition;
        holder.name.setText(orderList.get(position).getCustomerName());
        holder.address.setText(orderList.get(position).getDeliveryAddress());
        holder.amount.setText(orderList.get(position).getAmount());
        holder.orderId.setText("1011234");
        holder.contactNumber.setText("7887543650");
        holder.dateTime.setText("01/02/2018 \n 7:35PM");

        ArrayList<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setItem("Cheese Parantha");
        item.setPrice("70");
        item.setQty("1");
        itemList.add(item);
        Item item1 = new Item();
        item1.setItem("Chicken Hakka Noodles");
        item1.setPrice("90");
        item1.setQty("2");
        itemList.add(item1);
        Item item2 = new Item();
        item2.setItem("Chicken Sandwich");
        item2.setPrice("60");
        item2.setQty("3");
        itemList.add(item2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        CustomListAdapter adapter = new CustomListAdapter(mContext, itemList);
        holder.itemRecyclerView.setAdapter(adapter);
        //setListViewHeightBasedOnItems(holder.orderListView);

        holder.expandView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.expandView.setActivated(isExpanded);
        if (isExpanded)
            previousExpandedPosition = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            Log.d(TAG, "setListViewHeightBasedOnItems: number of items" + listAdapter.getCount());

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight;
            Log.d(TAG, "setListViewHeightBasedOnItems: " + params.height);
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView name;
        public final TextView address;
        public final TextView amount;
        public final TextView orderId;
        public final TextView dateTime;
        public final TextView contactNumber;

        public final ArrayList<Order> orderList;
        private View expandView;
        private RecyclerView itemRecyclerView;

        public final Context mContext;

        public ViewHolder(View itemView, Context mContext, ArrayList<Order> orderList) {
            super(itemView);
            this.mContext = mContext;
            this.orderList = orderList;
            expandView = itemView.findViewById(R.id.detailsId);
            orderId = itemView.findViewById(R.id.txtId);
            dateTime = itemView.findViewById(R.id.txtDate);
            contactNumber = itemView.findViewById(R.id.txtNumber);

            name = (TextView)itemView.findViewById(R.id.txtName);
            address = (TextView)itemView.findViewById(R.id.txtAddress);
            amount = (TextView)itemView.findViewById(R.id.txtAmount);
            itemRecyclerView = (RecyclerView)itemView.findViewById(R.id.orderRecyclerView);
        }


    }
}