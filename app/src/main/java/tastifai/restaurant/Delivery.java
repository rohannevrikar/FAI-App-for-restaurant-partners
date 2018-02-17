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

public class Delivery extends Fragment {
    View myView;
    private ArrayList<Order> initOrderList;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_currentorder,container,false);
        initOrderList = new ArrayList<>();

        RecyclerView recyclerView;
        ArrayList<Order> orderList= null;
        Bundle bundle = getArguments();
        Log.d("Delivery", "onCreateView: ");
        if(bundle != null){
            orderList = (ArrayList<Order>) bundle.getSerializable("deliveryOrders");
        }
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrderAdapter(getActivity(), MainActivity.deliveryOrders, R.layout.activity_delivery);
        for(Order p : MainActivity.deliveryOrders )
            Log.d("Delivery", "onCreateView: " + p.getCustomerName());

        recyclerView.setAdapter(adapter);
        return myView;
    }
}
