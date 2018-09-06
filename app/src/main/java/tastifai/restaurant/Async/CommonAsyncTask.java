package tastifai.restaurant.Async;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import tastifai.restaurant.Interfaces.CurrentOrderResponse;

/**
 * Created by Rohan Nevrikar on 15-04-2018.
 */

public class CommonAsyncTask extends AsyncTask<Object, Integer, String> {
    StringBuilder builder = new StringBuilder();
    public CurrentOrderResponse delegate = null;
    private static final String TAG = "CommonAsyncTask";
    private String TagString;

public CommonAsyncTask(String TagString){
    this.TagString = TagString;

}
    @Override
    protected void onPreExecute() {
        Log.d(TAG, TagString + "onPreExecute: ");
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s);

    }

    @Override
    protected String doInBackground(Object... objects) {
        Log.d(TAG, TagString + "doInBackground: ");
        try {
            URL url = new URL((String) objects[0]);
            Log.d(TAG, "doInBackground: " + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            Log.d(TAG, TagString + "doInBackground: " + connection.getResponseCode() + " " + connection.getResponseMessage());

            InputStream istream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, TagString + "doInBackground: " + line);
                builder.append(line);



            }
            int responseCode = connection.getResponseCode();
            Log.d(TAG, TagString + "Response Code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, TagString + "doInBackground: " + responseCode + " " + builder.toString());
                String[] myArray = builder.toString().split(",");
                Log.d(TAG, TagString + "onPostExecute: " + myArray[0]);
                return builder.toString();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

}

