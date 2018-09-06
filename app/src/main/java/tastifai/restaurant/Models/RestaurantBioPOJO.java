package tastifai.restaurant.Models;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 09-02-2018.
 */

public class RestaurantBioPOJO implements Serializable {
    private String restaurantName;
    private String restaurantBio;
    private String restaurantAddress;
    private String imagePath;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantBio() {
        return restaurantBio;
    }

    public void setRestaurantBio(String restaurantBio) {
        this.restaurantBio = restaurantBio;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getPhotoFile() {
        return imagePath;
    }

    public void setPhotoFile(String imagePath) {
        this.imagePath = imagePath;
    }
}
