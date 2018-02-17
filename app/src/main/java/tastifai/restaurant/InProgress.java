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
import android.widget.Button;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 18-11-2017.
 */

public class InProgress extends Fragment {
    View myView;
    View view;
    private Button btnStartDelivery;
    private ArrayList<Order> initOrderList;
    private OrderAdapter adapter;
    ArrayList<Order> orderList= new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_inprogress,container,false);
        initOrderList = new ArrayList<>();
        RecyclerView recyclerView;
        //Bundle bundle = getArguments();
//        if(bundle != null){
//            orderList = (ArrayList<Order>) bundle.getSerializable("progressOrders");
//            Log.d("InProgress", "onCreateView: Not null");
//        }
        Log.d("InProgress", "onCreateView: code reached here, bundle is null");
        recyclerView = myView.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        if(orderList == null){
//            adapter = new OrderAdapter(getActivity(), initOrderList);
//
//        }
//        else{
//            Log.d("In Progress", "onCreateView: " + orderList.toString());
//            adapter = new OrderAdapter(getActivity(), orderList);
//
//        }
           adapter = new OrderAdapter(getActivity(), MainActivity.progressOrders, R.layout.activity_inprogress);
        for(Order p : MainActivity.progressOrders )
        Log.d("InProgress", "onCreateView: " + p.getCustomerName());
        recyclerView.setAdapter(adapter);
        return myView;
    }

}
