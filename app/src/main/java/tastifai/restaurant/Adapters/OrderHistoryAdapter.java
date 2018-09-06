package tastifai.restaurant.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Utilities.WrapContentLinearLayoutManager;

/**
 * Created by Rohan Nevrikar on 08-03-2018.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orderHistoryList;
    private View view;
    private final LayoutInflater layoutInflater;
    private CustomListAdapter customListAdapter;
    public OrderHistoryAdapter(Context context, ArrayList<Order> orderHistoryList){
        this.orderHistoryList = orderHistoryList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);

    }
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.order_history_row, parent, false);

        return new ViewHolder(view, context, orderHistoryList);
    }

    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, int position) {
        int serialNumber = position + 1;
        holder.txtCustomer.setText(orderHistoryList.get(position).getCustomerName());
        holder.txtPrice.setText(String.valueOf(orderHistoryList.get(position).getTotalPrice()));
        holder.itemNumber.setText(serialNumber + ". ");
        holder.dateTime.setText(orderHistoryList.get(position).getDateTime());
        holder.itemsRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        customListAdapter = new CustomListAdapter(context, orderHistoryList.get(position).getItemList());
        holder.itemsRecyclerView.setAdapter(customListAdapter);
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtCustomer;
        public RecyclerView itemsRecyclerView;
        public TextView txtPrice;
        public TextView dateTime;
        public TextView itemNumber;
        public ViewHolder(View itemView, Context context, ArrayList<Order> orderHistoryList) {
            super(itemView);
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
            txtPrice = itemView.findViewById(R.id.totalPrice);
            dateTime = itemView.findViewById(R.id.dateTime);
            itemNumber = itemView.findViewById(R.id.itemNumber);
        }
    }
}
