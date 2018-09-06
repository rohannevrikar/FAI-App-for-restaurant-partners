package tastifai.restaurant.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.io.File;

import tastifai.restaurant.Models.RestaurantBioPOJO;

/**
 * Created by Rohan Nevrikar on 09-02-2018.
 */

public class    RestaurantBio extends Fragment {
    View view;
    TextView fileName;
    EditText restaurantName;
    EditText restaurantBio;
    EditText restaurantAddress;
    Button photo;
    Button submit;
    File file;
    static final int CAM_REQUEST = 1;
    public static final RestaurantBioPOJO restaurantBioPOJO = new RestaurantBioPOJO();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bio,container,false);
        restaurantAddress = (EditText)view.findViewById(R.id.addressText);
        restaurantBio = view.findViewById(R.id.bioText);
        restaurantName = view.findViewById(R.id.nameText);
        fileName = view.findViewById(R.id.txtFile);
        submit = view.findViewById(R.id.btnSubmit);
        photo = view.findViewById(R.id.btnPhoto);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = getFile();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(cameraIntent, CAM_REQUEST);
                fileName.setText(file.getName());
                restaurantBioPOJO.setPhotoFile(file.getPath());

                Log.d("Restaurant Bio", "onClick: " + file.getPath());
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantBioPOJO.setRestaurantAddress(restaurantAddress.getText().toString());
                restaurantBioPOJO.setRestaurantBio(restaurantBio.getText().toString());
                restaurantBioPOJO.setRestaurantName(restaurantName.getText().toString());
                Toast.makeText(getActivity(), "Bio updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public File getFile(){
        File folder = new File("sdcard/tastifai_photos");
        if(!folder.exists()){
            folder.mkdir();
        }
        File imageFile = new File(folder, "tastifai_image.jpg");
        return imageFile;
    }


}
