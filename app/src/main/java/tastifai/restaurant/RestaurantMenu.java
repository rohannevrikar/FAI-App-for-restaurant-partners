package tastifai.restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rohannevrikar.restaurant.R;

/**
 * Created by Rohan Nevrikar on 27-01-2018.
 */

public class RestaurantMenu extends Fragment {
    View myView;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_menu,container,false);
        recyclerView = myView.findViewById(R.id.listMenu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        MenuAdapter adapter = new MenuAdapter(getActivity(), MainActivity.itemList);
        recyclerView.setAdapter(adapter);
        return myView;
    }
}