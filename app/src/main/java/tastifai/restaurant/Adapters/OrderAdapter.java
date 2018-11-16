package tastifai.restaurant.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Models.TimePOJO;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;

import static tastifai.restaurant.Activities.MainActivity.currentCount;
import static tastifai.restaurant.Activities.MainActivity.deliveryCount;
import static tastifai.restaurant.Activities.MainActivity.guid;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;
import static tastifai.restaurant.Activities.MainActivity.totalPrice;
import static tastifai.restaurant.Activities.MainActivity.viewPager;

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
    private View emptyListView;
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
        if (layout == R.layout.activity_currentorder) {
            view = layoutInflater.inflate(R.layout.order_row, parent, false);

            Log.d(TAG, "onCreateViewHolder: View is order_row");
        } else if (layout == R.layout.activity_inprogress) {
            view = layoutInflater.inflate(R.layout.progress_row, parent, false);
            Log.d(TAG, "onCreateViewHolder: View is inprogress_row");

        } else {
            view = layoutInflater.inflate(R.layout.delivery_row, parent, false);
            Log.d(TAG, "onCreateViewHolder: View is delivery_row");

        }


        Log.d(TAG, "onCreateViewHolder: " + view.getResources().getResourceEntryName(layout) + " " + view.getResources().getResourceEntryName(R.layout.activity_currentorder));


        return new ViewHolder(view, mContext, orderList);

    }

    //18:28:06.392
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int serialNumber = position + 1;

        String string = "\u20B9";
        byte[] utf8 = null;
        try {
            utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //final boolean isExpanded = position==mExpandedPosition;
        holder.customer.setText(orderList.get(position).getCustomerName());
        holder.price.setText(string + "" + String.format("%.2f", orderList.get(position).getTotalUser()));
        holder.itemNumber.setText(serialNumber + ". ");
        holder.dateTime.setText(orderList.get(position).getDateTime());
        Log.d(TAG, "onBindViewHolder: View pager value" + viewPager.getCurrentItem());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        CustomListAdapter adapter = new CustomListAdapter(mContext, orderList.get(position).getItemList());
        holder.itemRecyclerView.setAdapter(adapter);
        holder.instructions.setText(orderList.get(position).getinstruction());


//        timings = new TimePOJO();
//        if(layout == R.layout.activity_currentorder){
//            timings.setReceivedAt("04:20PM");
//        }
//        else if(layout == R.layout.activity_inprogress){
//            timings.setReceivedAt("04:20PM");
//            timings.setAcceptedAt("04:22PM");
//
//        }
//        else if(layout == R.layout.activity_delivery)
//        {
//            timings.setReceivedAt("04:20PM");
//            timings.setAcceptedAt("04:22PM");
//            timings.setDispatchedAt("04:55PM");
//        }

        holder.dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Utils.isConnectedToInternet(mContext)) {
                    if (layout == R.layout.activity_currentorder) {


                        Log.d(TAG, "onClick: Accept clicked ");
                        guid = orderList.get(position).getGuid();

                        String URL = Constants.URL + "PostAcceptOrders/" + restaurantId + "/" + guid;
                        CallAPI api = new CallAPI();
                        api.execute(URL);
                        Toast.makeText(mContext, "Accepting order, please wait a moment...", Toast.LENGTH_LONG).show();



                        orderList.remove(orderList.get(position));
                        currentCount = orderList.size();
                        ((MainActivity) mContext).orderCount();

                        holder.dynamicButton.setClickable(false);

                        notifyDataSetChanged();


                    } else if (layout == R.layout.activity_inprogress) {
                        Log.d(TAG, "onClick: Progress" + orderList.size() + " " + position);
                        guid = orderList.get(position).getGuid();
                        String URL =  Constants.URL + "PostStartDeliveryOrders/" + restaurantId + "/" + guid;

                        CallAPI api = new CallAPI();
                        api.execute(URL);
                        Toast.makeText(mContext, "Starting delivery, please wait a moment...", Toast.LENGTH_LONG).show();

                        orderList.remove(orderList.get(position));
                        MainActivity.progressCount = orderList.size();
                        ((MainActivity) mContext).orderCount();

                        holder.dynamicButton.setClickable(false);

                        notifyDataSetChanged();


                    } else if (layout == R.layout.activity_delivery) {

                        Log.d(TAG, "onClick: Delivered clicked" + orderList.size());
                        guid = orderList.get(position).getGuid();
                        Log.d(TAG, "onClick: guid: " + guid);
                        String URL = Constants.URL + "PostDeliveredOrder/" + restaurantId + "/" + guid + "/";
                        CallAPI api = new CallAPI();
                        api.execute(URL);

                        Toast.makeText(mContext, "Changing delivery status, please wait a moment...", Toast.LENGTH_LONG).show();

                        orderList.remove(orderList.get(position));
                        deliveryCount = orderList.size();
                        ((MainActivity) mContext).orderCount();

                        holder.dynamicButton.setClickable(false);

                        notifyDataSetChanged();

                    }
                } else {
                    Toast.makeText(mContext, "Not connected to internet. Please try again", Toast.LENGTH_LONG).show();

                }


            }
        });

//
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrice = orderList.get(position).getTotalUser();
                guid = orderList.get(position).getGuid();
                Log.d(TAG, "onClick: guid: " + guid);
                ((MainActivity) mContext).orderDetailsFragment(layout, orderList, position);

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + orderList.size());
        return orderList.size();
    }

    public void registerEmptyListListener(View emptyListView){
        this.emptyListView = emptyListView;
        registerAdapterDataObserver(observer);

        checkIfEmpty();
    }

    public void unregisterEmptyListListener(){
        this.emptyListView = null;
        unregisterAdapterDataObserver(observer);
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    private void checkIfEmpty() {
        Log.d("orderList Size", orderList.size() + "");
        if (orderList.size() == 0) {
            emptyListView.setVisibility(View.VISIBLE);
        } else
            emptyListView.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView customer;
        public TextView price;
        public final ArrayList<Order> orderList;
        private View expandView;
        private RecyclerView itemRecyclerView;
        private Button btnDetails;
        private Button dynamicButton;
        public TextView dateTime;
        private TextView itemNumber;
        private TextView instructions;

        public final Context mContext;

        public ViewHolder(View itemView, Context mContext, ArrayList<Order> orderList) {
            super(itemView);
            this.mContext = mContext;
            this.orderList = orderList;
            customer = (TextView) itemView.findViewById(R.id.txtCustomer);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.orderRecyclerView);
            dynamicButton = itemView.findViewById(R.id.dynamicButton);
            btnDetails = itemView.findViewById(R.id.detailsButton);
            price = itemView.findViewById(R.id.totalPrice);
            dateTime = itemView.findViewById(R.id.dateTime);
            itemNumber = itemView.findViewById(R.id.itemNumber);
            instructions = itemView.findViewById(R.id.instructions);

        }


    }

    private class CallAPI extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                Log.d(TAG, "doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                //connection.setDoInput(true);
                connection.connect();
//                InputStream istream = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Log.d("OrderAdapter", "doInBackground: " + line);
//                    builder.append(line);
//
//
//
//                }
                int responseCode = connection.getResponseCode();
                Log.d("OrderAdapter", "Response Code: " + responseCode);
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("OrderAdapter", "doInBackground: " + responseCode + " " + builder.toString());
//                    String[] myArray = builder.toString().split(",");
//                    Log.d("OrderAdapter", "onPostExecute: " + myArray[0]);
//                    return builder.toString();
//                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}