package tastifai.restaurant.Fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rohannevrikar.restaurant.R;

import tastifai.restaurant.Models.RestaurantBioPOJO;

/**
 * Created by Rohan Nevrikar on 09-02-2018.
 */

public class ViewBio extends Fragment {
    View view;
    TextView restaurantName;
    TextView restaurantAddress;
    TextView restaurantBio;
    ImageView restaurantImage;
    RestaurantBioPOJO restaurantBioPOJO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_bio,container,false);
        restaurantName = view.findViewById(R.id.name);
        restaurantBio = view.findViewById(R.id.bio);
        restaurantAddress = view.findViewById(R.id.address);
        restaurantImage = view.findViewById(R.id.image);
        Log.d("ViewBio", "onCreateView: " + RestaurantBio.restaurantBioPOJO.getRestaurantName());
        restaurantName.setText(RestaurantBio.restaurantBioPOJO.getRestaurantName());
        restaurantBio.setText(RestaurantBio.restaurantBioPOJO.getRestaurantBio());
        restaurantAddress.setText(RestaurantBio.restaurantBioPOJO.getRestaurantAddress());
        restaurantImage.setImageDrawable(Drawable.createFromPath(RestaurantBio.restaurantBioPOJO.getPhotoFile()));

        return view;
    }
}
