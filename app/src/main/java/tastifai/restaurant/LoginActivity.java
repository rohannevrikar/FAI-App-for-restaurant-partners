package tastifai.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private Button login;
    private SharedPreferences sharedPreferences;
    private String TAG = "TAG";
    private TextView welcomeText;
    private String URL;
    private CallAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_login);
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Dear Restaurant Partners,\n Welcome to FAI Partners app");
        emailText = (EditText) findViewById(R.id.email);
        sharedPreferences = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        passwordText = (EditText) findViewById(R.id.password);
        if(sharedPreferences.getBoolean("success",false)){
            int id = sharedPreferences.getInt("id", -1);
            String name = sharedPreferences.getString("name", "Tastifai");


            Log.d(TAG, "onCreate: " + id);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();


        }
        login = (Button) findViewById(R.id.btnLogin);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");
        login.setTypeface(font);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!emailText.getText().toString().equals("") && !passwordText.getText().toString().equals("")) {

                    URL = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantDetails/" + emailText.getText().toString() + "/" + passwordText.getText().toString();
                    api = new CallAPI();
                    api.execute(URL);
                }else
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();


//                if (("tannstafel@gmail.com").equals(email.getText().toString()) && ("user123").equals(password.getText().toString())) {
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("success", true);
//                    editor.apply();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//
//
//
//                } else {
//                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
//
//                }
            }
        });

    }
    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;

        @Override
        protected void onPostExecute(String s) {
          try {
                JSONObject obj = new JSONObject(s);
              int id = obj.getInt("RestaurantID");
                String name = obj.getString("RestaurantName");
              String email = obj.getString("EmailAddress");
              String password = obj.getString("Password");


                  if(emailText.getText().toString().equals(email) && passwordText.getText().toString().equals(password)){
                      SharedPreferences.Editor editor = sharedPreferences.edit();
                      editor.putBoolean("success", true);
                      editor.putInt("id", id);
                      editor.putString("name", name);
                      editor.apply();
                      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                      intent.putExtra("id", id);
                      intent.putExtra("name", name);
                      startActivity(intent);
                      finish();
                  }else
                      Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();



              Log.d(TAG, "onPostExecute: " + email + " " + password);

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
              Toast.makeText(LoginActivity.this, "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();
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
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
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



