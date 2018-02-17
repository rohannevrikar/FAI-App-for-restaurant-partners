package tastifai.restaurant;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class CurrentOrder extends Fragment {
    View orderView;
    View details;
    RecyclerView orderRecyclerView;
    LinearLayoutManager orderManager;
    ArrayList<Order> orderList = MainActivity.orderList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderView = inflater.inflate(R.layout.activity_currentorder,container,false);
        getActivity().setTitle("Current Order");
        orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderRecyclerView.setLayoutManager(layoutManager);
        Log.d("CurrentOrder", "onCreateView: " + MainActivity.orderList.size());
        OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orderList, R.layout.activity_currentorder);


        orderRecyclerView.setAdapter(orderAdapter);
        return orderView;
    }


    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }
}
