package tastifai.restaurant.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import tastifai.restaurant.Activities.MainActivity;
import tastifai.restaurant.Fragments.RestaurantMenu;
import tastifai.restaurant.Interfaces.getAPIResponse;
import tastifai.restaurant.Models.MenuModel;
import tastifai.restaurant.Utilities.Constants;
import tastifai.restaurant.Utilities.Utils;

import static tastifai.restaurant.Activities.MainActivity.progressDialog;
import static tastifai.restaurant.Activities.MainActivity.restaurantId;

/**
 * Created by Rohan Nevrikar on 08-02-2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MenuModel> itemList;
    private final LayoutInflater layoutInflater;
    private View view;
    private RestaurantMenu restaurantMenu;

    public MenuAdapter(Context context, ArrayList<MenuModel> itemList, RestaurantMenu restaurantMenu) {
        this.context = context;
        this.itemList = itemList;
        layoutInflater = LayoutInflater.from(context);
        this.restaurantMenu = restaurantMenu;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.menu_row, parent, false);
        Log.d("Menu Adapter", "onCreateViewHolder: " + getItemCount());
        return new ViewHolder(view, context, itemList);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.aSwitch.setOnCheckedChangeListener(null);
        Log.d("MenuAdapter", "onBindViewHolder: " + position);
        holder.item.setText(itemList.get(position).getItemName());
        if (itemList.get(position).getIsActive().equals("1")) {
            holder.aSwitch.setChecked(true);

        } else
            holder.aSwitch.setChecked(false);
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isConnectedToInternet(context)) {
                    Toast.makeText(context, "Not connected to internet. Please try again", Toast.LENGTH_LONG).show();

                }

            }
        });
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    String URL = Constants.URL + "PostToggelMenuItem/" + restaurantId + "/" + itemList.get(position).getItemId() + "/1";
                    CallAPI api = new CallAPI();
                    api.execute(URL);
                    holder.itemList.get(position).setIsActive("1");

                } else {
                    String URL = Constants.URL + "/PostToggelMenuItem/" + restaurantId + "/" + itemList.get(position).getItemId() + "/0";
                    CallAPI api = new CallAPI();
                    api.execute(URL);
                    holder.itemList.get(position).setIsActive("0");

                }


            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("This item will be deleted from menu. Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteMenu(position);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    private void deleteMenu(final int position) {
        if (!Utils.isConnectedToInternet(context)) {
            Utils.setUpAlert(context, new getAPIResponse() {
                @Override
                public void OnRetry(DialogInterface dialogInterface) {
                    deleteMenu(position);
                }
            });

        } else {
            String URL = Constants.URL + "/PostDeleteRestaurantMenuItem/" + restaurantId + "/" + itemList.get(position).getItemId();
            new CallAPI().execute(URL);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ArrayList<MenuModel> itemList;
        private Switch aSwitch;
        private ImageView remove;

        private TextView item;

        public ViewHolder(View view, Context mContext, ArrayList<MenuModel> itemList) {
            super(view);
            this.itemList = itemList;
            aSwitch = view.findViewById(R.id.switch1);
            item = view.findViewById(R.id.item);
            remove = view.findViewById(R.id.remove);

        }


    }

    private class CallAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(context);
            if (!((MainActivity) context).isFinishing()) {
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
//            restaurantMenu.getFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, new RestaurantMenu())
//                    .addToBackStack(null)
//                    .commit();

        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                Log.d("MenuAdapter", "doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                //connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                connection.connect();
//                InputStream istream = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Log.d("OrderAdapter", "doInBackground: " + line);
//                    builder.append(line);
//
//
//
//                }
                int responseCode = connection.getResponseCode();
                Log.d("MenuAdapter", "Response Code: " + responseCode);
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("OrderAdapter", "doInBackground: " + responseCode + " " + builder.toString());
//                    String[] myArray = builder.toString().split(",");
//                    Log.d("OrderAdapter", "onPostExecute: " + myArray[0]);
//                    return builder.toString();
//                }
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
