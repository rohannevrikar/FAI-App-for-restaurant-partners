package tastifai.restaurant.Services;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import tastifai.restaurant.Activities.LoginActivity;
import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Async.HTTPRequest;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;

import static android.content.ContentValues.TAG;
import static java.net.HttpURLConnection.HTTP_CREATED;

/**
 * Created by Rohan Nevrikar on 07-02-2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        saveToken(refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    private void saveToken(final String token) {
        String url = Constants.URL + "PostRestaurantTokenNumber/";
        HTTPRequest request = HTTPRequest.post(url);
        request.setDelegate(new HTTPRequest.HTTPRequestResult() {
            @Override
            public void HTTPResponseCode(int httpCode) {
                Log.d("TokenUpdate", "HTTPCode:" + httpCode);
                if(httpCode != HTTP_CREATED){
                    saveToken(token);
                }
            }
            @Override
            public void HTTPResponse(String output) {

            }
        });
        JSONObject params = new JSONObject();
        try {
            params.put("RestaurantID", MainActivity.restaurantId);
            params.put("TokenNumber", token);
            params.put("RestaurantTokenCreatedDateTime", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.setParams(params);
        request.execute();
    }
}
