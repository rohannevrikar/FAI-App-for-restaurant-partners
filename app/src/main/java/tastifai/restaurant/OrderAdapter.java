package tastifai.restaurant;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

import static tastifai.restaurant.MainActivity.CODE;
import static tastifai.restaurant.MainActivity.btnStatus;
import static tastifai.restaurant.MainActivity.deliveryOrders;
import static tastifai.restaurant.MainActivity.itemList;
import static tastifai.restaurant.MainActivity.orderList;
import static tastifai.restaurant.MainActivity.progressOrders;
import static tastifai.restaurant.MainActivity.viewPager;

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
    private int layout;
    public OrderAdapter(Context mContext, ArrayList<Order> orderList, int layout) {
        Log.d(TAG, "OrderAdapter: " + orderList.size());
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.orderList = orderList;
        this.layout = layout;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(layout == R.layout.activity_currentorder){
            view = layoutInflater.inflate(R.layout.order_row, parent, false);
            Log.d(TAG, "onCreateViewHolder: View is order_row");
        }
        else if(layout == R.layout.activity_inprogress){
            view = layoutInflater.inflate(R.layout.progress_row, parent, false);
            Log.d(TAG, "onCreateViewHolder: View is inprogress_row");

        }
        else{
            view = layoutInflater.inflate(R.layout.delivery_row, parent, false);
            Log.d(TAG, "onCreateViewHolder: View is delivery_row");

        }


        Log.d(TAG, "onCreateViewHolder: " + view.getResources().getResourceEntryName(layout) + " " + view.getResources().getResourceEntryName(R.layout.activity_currentorder));

        Log.d(TAG, "onCreateViewHolder: " + MainActivity.orderList.size() + MainActivity.CODE);


        return new ViewHolder(view, mContext, MainActivity.orderList);

    }
//18:28:06.392
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //final boolean isExpanded = position==mExpandedPosition;
        holder.customer.setText(orderList.get(position).getCustomerName());
        Log.d(TAG, "onBindViewHolder: View pager value" + viewPager.getCurrentItem());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        CustomListAdapter adapter = new CustomListAdapter(mContext, itemList);
        holder.itemRecyclerView.setAdapter(adapter);


        timings = new TimePOJO();
        if(layout == R.layout.activity_currentorder){
            timings.setReceivedAt("04:20PM");
        }
        else if(layout == R.layout.activity_inprogress){
            timings.setReceivedAt("04:20PM");
            timings.setAcceptedAt("04:22PM");

        }
        else if(layout == R.layout.activity_delivery)
        {
            timings.setReceivedAt("04:20PM");
            timings.setAcceptedAt("04:22PM");
            timings.setDispatchedAt("04:55PM");
        }

            holder.dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout == R.layout.activity_currentorder){
                    Log.d(TAG, "onClick: Accept clicked ");
                    MainActivity.progressOrders.add(orderList.get(position));
                    MainActivity.progressCount = MainActivity.progressOrders.size();
                    orderList.remove(position);
                    MainActivity.currentCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).updateViewPager();
                    ((MainActivity)mContext).orderCount();

                }
                else if(layout == R.layout.activity_inprogress){
                    MainActivity.deliveryOrders.add(orderList.get(position));
                    MainActivity.deliveryCount = MainActivity.deliveryOrders.size();
                    orderList.remove(position);
                    Log.d(TAG, "onClick: Start delivery clicked");

                    MainActivity.progressCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).updateViewPager();

                      ((MainActivity)mContext).orderCount();


                }
                else if(layout == R.layout.activity_delivery) {
                    Log.d(TAG, "onClick: Delivered clicked");

                    orderList.remove(position);
                    MainActivity.deliveryCount = orderList.size();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    ((MainActivity)mContext).updateViewPager();
                    ((MainActivity)mContext).orderCount();


                }




            }
        });


        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).orderDetailsFragment(orderList.get(position).getCustomerName(), itemList, orderList.get(position).getDeliveryAddress(), orderList.get(position).getContactNumber());

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
        //Log.d(TAG, "getItemCount: " + orderList.size());
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
            customer = (TextView)itemView.findViewById(R.id.txtCustomer);
            itemRecyclerView = (RecyclerView)itemView.findViewById(R.id.orderRecyclerView);
            dynamicButton = itemView.findViewById(R.id.dynamicButton);
            btnDetails = itemView.findViewById(R.id.detailsButton);
        }


    }
}