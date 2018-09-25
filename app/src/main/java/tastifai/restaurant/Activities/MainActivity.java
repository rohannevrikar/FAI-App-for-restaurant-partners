package tastifai.restaurant.Activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.rohannevrikar.restaurant.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.fabric.sdk.android.Fabric;
import tastifai.restaurant.Adapters.FragmentAdapter;
import tastifai.restaurant.Fragments.CurrentOrder;
import tastifai.restaurant.Fragments.Delivery;
import tastifai.restaurant.Fragments.InProgress;
import tastifai.restaurant.Fragments.OrderDetails;
import tastifai.restaurant.Fragments.RestaurantMenu;
import tastifai.restaurant.Fragments.SettingsFragment;
import tastifai.restaurant.Models.Item;
import tastifai.restaurant.Models.Order;
import tastifai.restaurant.Services.OrderService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentAdapter adapter;
    public static final ArrayList<Item> itemList = new ArrayList<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private DrawerLayout drawerLayout;
    private Toolbar detailsToolbar;
    public static ViewPager viewPager;
    public static int CODE;
    public static int btnStatus = 0;
    public static ProgressDialog progressDialog;


    public static int currentCount;
    public static int progressCount;
    public static int deliveryCount;
    private CurrentOrder f_neworder;
    private InProgress f_inprogress;
    private Delivery f_delivery;
    private NavigationView view;
    public static int restaurantId;
    private String restaurantName;
    private static final String TAG = "MainActivity";
    public static TabLayout tabLayout;
    public static String guid;
    public static double deliveryCharge;
    public static double discount;
    public static double totalUser;
    public static String helpLine;
    public static double restaurantEarning;

    private ImageView restaurantLogo;
    public static double totalPrice;
    int countBack = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());

        initComponent();

        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("id", 0);
        restaurantName = intent.getStringExtra("name");
        helpLine = intent.getStringExtra("helpLine");
        deliveryCharge = intent.getIntExtra("deliveryCharge", 0);
        Log.d(TAG, "onCreate: deliverycharges: " + deliveryCharge);
        Log.d("MainActivity", "onCreate: " + restaurantId);
//        Log.d("MainActivity", "refreshtoken: " + FirebaseInstanceId.getInstance().getToken());
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        initAction();
        //setupDrawerLayout();
        prepareActionBar(toolbar);
        if (OrderService.mInstance == null)
            startService(new Intent(this, OrderService.class));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            float sumPositionAndOffset;
            boolean first;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                Log.d("MainActivity", "onPageSelected: " + position);
                btnStatus = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        view = (NavigationView) findViewById(R.id.nav_view);
        onNavigationItemSelected(view.getMenu().findItem(R.id.home).setChecked(true));

        View headerLayout = view.getHeaderView(0);
        restaurantLogo = headerLayout.findViewById(R.id.logo);


        Picasso.get().load("http://foodspec1-test.us-east-1.elasticbeanstalk.com/Images/" + restaurantName + ".jpg").into(restaurantLogo, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                restaurantLogo.setImageResource(R.drawable.applogo);

            }
        });


        view.setNavigationItemSelectedListener(MainActivity.this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }


    private void initAction() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("MainActivity", "initAction: called");
        viewPager.setCurrentItem(0);
    }

    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

    }

    public void setupViewPager(ViewPager viewPager) {
        FragmentAdapter.mFragments.clear();
        FragmentAdapter.mFragmentTitles.clear();
        adapter = new FragmentAdapter(getSupportFragmentManager());

        if (f_neworder == null) {
            f_neworder = new CurrentOrder();
        }
        if (f_inprogress == null) {
            f_inprogress = new InProgress();
        }
        if (f_delivery == null) {
            f_delivery = new Delivery();
        }

        adapter.addFragment(f_neworder, getString(R.string.tab_neworder) + "(" + currentCount + ")");
        adapter.addFragment(f_inprogress, getString(R.string.tab_inprogress) + "(" + progressCount + ")");
        adapter.addFragment(f_delivery, getString(R.string.tab_delivery) + "(" + deliveryCount + ")");

        viewPager.setAdapter(adapter);
    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(restaurantName);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            previousFragment();
        }
        if (findViewById(R.id.main_content).getVisibility() == View.VISIBLE) {
            if (countBack == 0) {
                Toast.makeText(MainActivity.this, "Press back one more time to exit", Toast.LENGTH_SHORT).show();
                ++countBack;
            } else if (countBack == 1) {
                finishAffinity();
            }
        } else {
            findViewById(R.id.main_content).setVisibility(View.VISIBLE);
            findViewById(R.id.detailsFragment).setVisibility(View.GONE);

        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            findViewById(R.id.main_content).setVisibility(View.VISIBLE);
            findViewById(R.id.detailsFragment).setVisibility(View.GONE);

            viewPager.setCurrentItem(0);
//        } else if (id == R.id.order_history) {
//            findViewById(R.id.main_content).setVisibility(View.GONE);
//            findViewById(R.id.detailsFragment).setVisibility(View.VISIBLE);
//            detailsToolbar = findViewById(R.id.toolbar);
//            setSupportActionBar(detailsToolbar);
//            getSupportActionBar().setTitle("Order History");
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, new OrderHistory())
//                    .addToBackStack(null)
//                    .commit();

        } else if (id == R.id.menu) {
            findViewById(R.id.main_content).setVisibility(View.GONE);
            findViewById(R.id.detailsFragment).setVisibility(View.VISIBLE);
            detailsToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(detailsToolbar);
            getSupportActionBar().setTitle("Menu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RestaurantMenu())
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.settings) {
            findViewById(R.id.main_content).setVisibility(View.GONE);
            findViewById(R.id.detailsFragment).setVisibility(View.VISIBLE);
            detailsToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(detailsToolbar);
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void orderDetailsFragment(int layout, ArrayList<Order> orderList, int position) {
        Bundle args = new Bundle();
        args.putInt("layout", layout);
        args.putString("name", orderList.get(position).getCustomerName());
        args.putString("address", orderList.get(position).getDeliveryAddress());
        args.putString("contact", orderList.get(position).getContactNumber());
        args.putDouble("userLat", orderList.get(position).getUserLat());
        args.putDouble("userLng", orderList.get(position).getUserLng());
        args.putBoolean("navigationAvailable", orderList.get(position).isNavigationAvailable());
        args.putDouble("discount", orderList.get(position).getDiscount());
        args.putDouble("deliveryCharge", orderList.get(position).getDeliveryCharge());
        args.putDouble("restaurantEarning", orderList.get(position).getRestaurantEarnings());
        args.putDouble("totalUser", orderList.get(position).getTotalUser());
        args.putSerializable("itemList", orderList.get(position).getItemList());
        OrderDetails orderDetailsFragment = new OrderDetails();
        orderDetailsFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        findViewById(R.id.main_content).setVisibility(View.GONE);
        findViewById(R.id.detailsFragment).setVisibility(View.VISIBLE);
        detailsToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(detailsToolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, orderDetailsFragment)
                .addToBackStack(null)
                .commit();

        ;
    }

    public void orderCount() {
        android.view.Menu menu = view.getMenu();
        menu.findItem(R.id.home).setTitle("Home " + "(" + currentCount + "/" + progressCount + "/" + deliveryCount + ")");
        adapter.changeFragmentTitle(0, "ORDER(" + currentCount + ")");
        adapter.changeFragmentTitle(1, "PROGRESS(" + progressCount + ")");
        adapter.changeFragmentTitle(2, "DELIVERY(" + deliveryCount + ")");
        tabLayout.setupWithViewPager(viewPager);

    }


    public void previousFragment() {
        setupViewPager(viewPager);
        findViewById(R.id.detailsFragment).setVisibility(View.GONE);
        findViewById(R.id.main_content).setVisibility(View.VISIBLE);

    }

    public String convertDateTime(String dateTime) {
        String start_dt = dateTime.replace("T", " ");

        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = (Date) parser.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        return formatter.format(date);
    }

    public int getId() {
        return restaurantId;
    }

}

