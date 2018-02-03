package com.example.rohannevrikar.foodcart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 01-02-2018.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    Context context;
    ArrayList<Item> itemArrayList;
    private final LayoutInflater layoutInflater;


    public CustomListAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.order_row_item, parent, false);

        return new ViewHolder(itemView, context, itemArrayList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setText(itemArrayList.get(position).getItem());
        holder.qty.setText("x "+ itemArrayList.get(position).getQty());
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final TextView item;
        public final TextView qty;
        public final Context mContext;
        public final ArrayList<Item> itemList;


        public ViewHolder(View itemView, Context context, ArrayList<Item> itemList) {
            super(itemView);
            mContext = context;
            this.itemList = itemList;
            item = itemView.findViewById(R.id.txtItem);
            qty = itemView.findViewById(R.id.txtQty);


        }
    }
}
