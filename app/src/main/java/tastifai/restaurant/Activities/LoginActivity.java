package tastifai.restaurant.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.rohannevrikar.restaurant.R;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;
import tastifai.restaurant.Async.CommonAsyncTask;
import tastifai.restaurant.Async.HTTPRequest;
import tastifai.restaurant.Interfaces.CurrentOrderResponse;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;

import static java.net.HttpURLConnection.HTTP_CREATED;

public class LoginActivity extends AppCompatActivity implements CurrentOrderResponse {
    private EditText emailText;
    private EditText passwordText;
    private Button login;
    private SharedPreferences sharedPreferences;
    private String TAG = "LoginActivity";
    private TextView welcomeText;
    private String URL;
    private CallAPI api;
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    int currentVersion;
    public ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_login);
        Fabric.with(this, new Crashlytics());

        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Dear Restaurant Partners,\n Welcome to FAI Partners app");
        emailText = (EditText) findViewById(R.id.email);
        sharedPreferences = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        passwordText = (EditText) findViewById(R.id.password);
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionCode;
        checkAppVersion();
    }

    private void checkLogin() {
        if (sharedPreferences.getBoolean("success", false)) {
            int id = sharedPreferences.getInt("id", -1);
            String name = sharedPreferences.getString("name", "FAI");
            String helpLine = sharedPreferences.getString("helpLine", "");
            int deliveryCharges = sharedPreferences.getInt("deliveryCharges", 0);
            boolean tokenUploaded = sharedPreferences.getBoolean("tokenUploaded", false);
            Log.d(TAG, "onCreate: " + deliveryCharges);

            if(!tokenUploaded){

                String url = Constants.URL + "PostRestaurantTokenNumber/";
                HTTPRequest request = HTTPRequest.post(url);
                request.setDelegate(new HTTPRequest.HTTPRequestResult() {
                    @Override
                    public void HTTPResponseCode(int httpCode) {
                        Log.d("TokenUpdate", "COde:" + httpCode);
                        if(httpCode == HTTP_CREATED){
                            sharedPreferences.edit().putBoolean("tokenUploaded", true).apply();
                            Toast.makeText(LoginActivity.this.getApplicationContext(), "Firebase Token Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void HTTPResponse(String output) {

                    }
                });
                JSONObject params = new JSONObject();
                try {
                    params.put("RestaurantID", id);
                    params.put("TokenNumber", FirebaseInstanceId.getInstance().getToken());
                    params.put("RestaurantTokenCreatedDateTime", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("jsonString", params.toString());
                request.setParams(params);
                request.execute();
            }

            Log.d(TAG, "onCreate: " + id);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("helpLine", helpLine);

            intent.putExtra("deliveryCharge", deliveryCharges);

            startActivity(intent);
            finish();


        }
        login = (Button) findViewById(R.id.btnLogin);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");
        login.setTypeface(font);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isConnectedToInternet(LoginActivity.this)){
                    if (!emailText.getText().toString().equals("") && !passwordText.getText().toString().equals("")) {

                        URL = Constants.URL + "GetRestaurantDetails/" + emailText.getText().toString() + "/" + passwordText.getText().toString();
                        api = new CallAPI();
                        api.execute(URL);
                    } else
                        Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();

                }else{
                    if (!isFinishing()) {
                        Utils.setUpAlert(LoginActivity.this, new getAPIResponse() {
                            @Override
                            public void OnRetry(DialogInterface dialogInterface) {
                                checkLogin();
                            }
                        });
                    }
                }

            }
        });

    }

    private void checkAppVersion() {

        if (!Utils.isConnectedToInternet(LoginActivity.this)) {
            if (!isFinishing()) {
                Utils.setUpAlert(LoginActivity.this, new getAPIResponse() {
                    @Override
                    public void OnRetry(DialogInterface dialogInterface) {
                        checkAppVersion();
                    }
                });

            }
        } else {
            CommonAsyncTask asyncTask = new CommonAsyncTask(TAG);
            asyncTask.delegate = (CurrentOrderResponse) LoginActivity.this;
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + "CheckPartnerAppVersion/" + currentVersion);
            //Log.d(TAG, "run: calling api");
            //new CheckAppVersionAPI().execute(Constants.URL + "CheckAppVersion/" + currentVersion);
        }
    }

    public void startAlarm() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(String s) {
        try {


            if (!s.equalsIgnoreCase("")) {
                s = s.replace("\"", "");
            }
            switch (s) {
                case "0":
                    checkLogin();
                    break;
                case "1":
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("A new version of the app is available, would you like to download it?" +
                            "")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                            ("market://details?id=tastifai.restaurant")));
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    checkLogin();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    break;
                case "2":
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                    builder2.setMessage("This version is no longer available, please update your app to continue")
                            .setCancelable(false)
                            .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                            ("market://details?id=tastifai.restaurant")));
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);

                                }
                            });
                    AlertDialog alert2 = builder2.create();
                    alert2.show();
                    break;
                default:
                    FirebaseCrash.log("onPostExecute: default case ");
                    break;
            }

        } catch (NullPointerException e) {

            if (!Utils.isConnectedToInternet(LoginActivity.this)) {
                if (!isFinishing()) {
                    Utils.setUpAlert(LoginActivity.this, new getAPIResponse() {
                        @Override
                        public void OnRetry(DialogInterface dialogInterface) {
                            checkLogin();
                        }
                    });
                }
            } else {
                Log.e("Error222", "True");
                e.printStackTrace();
                Crashlytics.logException(e);

                Intent intent = new Intent(LoginActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception e) {
            Log.e("Error333", "True");
            e.printStackTrace();
            Crashlytics.logException(e);

            Intent intent = new Intent(LoginActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    private void updateToken(int id) {
        String url = Constants.URL + "PostRestaurantTokenNumber/";
        HTTPRequest request = HTTPRequest.post(url);
        request.setDelegate(new HTTPRequest.HTTPRequestResult() {
            @Override
            public void HTTPResponseCode(int httpCode) {
                Log.d("TokenUpdate", "COde:" + httpCode);
                if(httpCode == HTTP_CREATED){
                    sharedPreferences.edit().putBoolean("tokenUploaded", true).apply();
                    Toast.makeText(LoginActivity.this.getApplicationContext(), "Firebase Token Updated", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void HTTPResponse(String output) {

            }
        });
        JSONObject params = new JSONObject();
        try {
            params.put("RestaurantID", id);
            params.put("TokenNumber", FirebaseInstanceId.getInstance().getToken());
            params.put("RestaurantTokenCreatedDateTime", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.setParams(params);
        request.execute();
    }


    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(getActivity());
            if(!isFinishing()){
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                JSONObject obj = new JSONObject(s);
                int id = obj.getInt("RestaurantID");
                String name = obj.getString("RestaurantName");
                String email = obj.getString("EmailAddress");
                String password = obj.getString("Password");
                Log.d(TAG, "onPostExecute: " + email + " " + password);

                //Log.d(TAG, "onPostExecute: deliverycharges" + deliveryCharge);
                if (emailText.getText().toString().equals(email) && passwordText.getText().toString().equals(password)) {
                    String helpLine = obj.getString("HelpLineNumber");
                    int deliveryCharge = obj.getInt("DeliveryCharges");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("success", true);
                    editor.putInt("id", id);
                    editor.putString("name", name);
                    editor.putString("helpLine", helpLine);
                    editor.putInt("deliveryCharges", deliveryCharge);
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("deliveryCharge", deliveryCharge);

                    updateToken(id);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onPostExecute: " + email + " " + password);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                if (!isFinishing()) {
                    Utils.setUpAlert(LoginActivity.this, new getAPIResponse() {
                        @Override
                        public void OnRetry(DialogInterface dialogInterface) {
                            checkLogin();
                        }
                    });
                }
                //Toast.makeText(LoginActivity.this, "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();
//              api = new CallAPI();
//              api.execute(URL);
            }


        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");

                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d(TAG, "onPostExecute: " + myArray[0]);
                    return builder.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}



