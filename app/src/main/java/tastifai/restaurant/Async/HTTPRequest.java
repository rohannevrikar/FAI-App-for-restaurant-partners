package tastifai.restaurant.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HTTPRequest extends AsyncTask<Object, Integer, String> {

    private String url;
    private HTTPRequestResult delegate = null;
    private String requestType;
    private JSONObject params;

    private final String TAG = "HTTPRequest";
    public static final String GET = "GET";
    public static final String POST = "POST";

    public HTTPRequest(String url, String requestType) {
        this.url = url;
        this.requestType = requestType;
    }

    public static HTTPRequest post(String url){
        return new HTTPRequest(url, POST);
    }

    public static HTTPRequest get(String url){
        return new HTTPRequest(url, GET);
    }

    public void setDelegate(HTTPRequestResult delegate) {
        this.delegate = delegate;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            URL url = new URL(this.url);
            Log.d(TAG, url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setRequestMethod(requestType);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if(requestType.equals(POST) && params.length() > 0) {
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params.toString());
                writer.flush();
                writer.close();
                os.close();
            }

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            int responseCode = conn.getResponseCode();
            publishProgress(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String[] myArray = builder.toString().split(",");
                return builder.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int responseCode = values[0];
        delegate.HTTPResponseCode(responseCode);
    }

    @Override
    protected void onPostExecute(String s) {
        if (delegate != null)
            delegate.HTTPResponse(s);
    }

    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public interface HTTPRequestResult {
        void HTTPResponseCode(int httpCode);
        void HTTPResponse(String output);
    }
}
