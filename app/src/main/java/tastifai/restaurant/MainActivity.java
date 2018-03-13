package tastifai.restaurant;

import android.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
   // public static ArrayList<Order> deliveryOrders= new ArrayList<>();
    public static FragmentAdapter adapter;
//    public static ArrayList<Order> progressOrders= new ArrayList<>();
//    public static ArrayList<Order> orderList = new ArrayList<>();
    public static final ArrayList<Item> itemList = new ArrayList<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private DrawerLayout drawerLayout;
    private Toolbar detailsToolbar;
    public static ViewPager viewPager;
    public static int CODE;
    public static int btnStatus = 0;
    public static int currentCount;
    public static int progressCount;
    public static int deliveryCount;
    private boolean isSearch = false;
    private CurrentOrder f_neworder;
    private InProgress f_inprogress;
    private Delivery f_delivery;
    private CurrentOrder currentOrderFragment;
    private NavigationView view;
    private ArrayList statusList;
    public static int restaurantId;
    private String restaurantName;
    private TextView restaurantNameNavBar;
    private SharedPreferences sharedPreferences;
    public static int count_c = 0;
    public static int count_p = 0;
    public static int count_d = 0;
    public static int count = 0;
    public static ProgressDialog progressDialog;

    public static TabLayout tabLayout;
    public static ArrayList<Order> progressOrders = new ArrayList<>();
    public static ArrayList<Order> deliveryOrders = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("id", 0);
        restaurantName = intent.getStringExtra("name");
        Log.d("MainActivity", "onCreate: " + restaurantId);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        initAction();
        //setupDrawerLayout();

        prepareActionBar(toolbar);


        statusList = new ArrayList();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            float sumPositionAndOffset;
            boolean first;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (first && positionOffset == 0 && positionOffsetPixels == 0){
//                    onPageSelected(0);
//                    first = false;
//                }
//                else{
//                    if(viewPager.getCurrentItem() == 0){
//                        btnStatus = 0;
//                        Log.d("MainActivity", "onPageScrolled: Right swipe" + btnStatus);
//
//                    }
//                    else
//                    {
//                        btnStatus = viewPager.getCurrentItem() - 1;
//                        Log.d("MainActivity", "onPageScrolled: Right swipe" + btnStatus);
//
//                    }
//
//
//                }
//                sumPositionAndOffset = positionOffset + position;
//
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        view = (NavigationView) findViewById(R.id.nav_view);
        onNavigationItemSelected(view.getMenu().findItem(R.id.home).setChecked(true));
//        Menu menu = view.getMenu();
//        for(int i=0;i<menu.size(); i++){
//            MenuItem mi = menu.getItem(i);
//            applyFontToMenuItem(mi);
//        }
        View headerLayout = view.getHeaderView(0);

        //restaurantNameNavBar = headerLayout.findViewById(R.id.restaurant_name);
        //restaurantNameNavBar.setText(restaurantName);
        view.setNavigationItemSelectedListener(MainActivity.this);

//                    orderCount();
//                    currentOrderFragment();


    }
//    private void setupDrawerLayout(){
//
////        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
////
////        view = (NavigationView) findViewById(R.id.nav_view);
////        view.setNavigationItemSelectedListener(MainActivity.this);
////        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
////            @Override
////            public boolean onNavigationItemSelected(MenuItem menuItem) {
////                menuItem.setChecked(true);
////                drawerLayout.closeDrawers();
////                return true;
////            }
////        });
//        orderCount();

    //}
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
  public void updateViewPager(){
        FragmentAdapter.mFragments.clear();
        FragmentAdapter.mFragmentTitles.clear();
        CurrentOrder currentOrder = new CurrentOrder();
        InProgress inProgress = new InProgress();
        Delivery delivery = new Delivery();
        adapter.addFragment(currentOrder, "ORDER" + "(" + currentCount + ")");
        adapter.addFragment(inProgress, "PROGRESS" + "(" + progressCount + ")");
        adapter.addFragment(delivery, "DELIVERY" + "(" + deliveryCount + ")");
        viewPager.setAdapter(adapter);
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
//        Order order1 = new Order();
//        order1.setCustomerName("Tastifai");
//        order1.setContactNumber("7887543650");
//        order1.setDeliveryAddress("MSH1202");
//        orderList.add(order1);
//        Order order = new Order();
//        order.setCustomerName("Rohan");
//        order.setContactNumber("8733000127");
//        order.setDeliveryAddress("D21R30");
//        orderList.add(order);
//        Item item = new Item();
//        item.setItem("Cheese Parantha");
//        item.setQty("1");
//        item.setPrice("70");
//        itemList.add(item);
//        Item item1 = new Item();
//        item1.setItem("Chicken Hakka Noodles");
//        item1.setQty("2");
//        item1.setPrice("90");
//        itemList.add(item1);
//        Item item2 = new Item();
//        item2.setItem("Chicken Sandwich");
//        item2.setQty("3");
//        item2.setPrice("60");
//        itemList.add(item2);
//        currentCount = orderList.size();
//        Log.d("MainActivity", "initComponent: " + currentCount);
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

        adapter.addFragment(f_neworder,  getString(R.string.tab_neworder)  + "(" + currentCount + ")");
        adapter.addFragment(f_inprogress, getString(R.string.tab_inprogress)  + "(" + progressCount + ")");
        adapter.addFragment(f_delivery, getString(R.string.tab_delivery)  + "(" + deliveryCount + ")");

        viewPager.setAdapter(adapter);
    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(restaurantName);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
//        if (!isSearch) {

        //    settingDrawer();
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            previousFragment();
        }

    }

//    private void settingDrawer() {
//        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//        };
//        // Set the drawer toggle as the DrawerListener
//        drawerLayout.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//    }
//    private void setupDrawerLayout() {
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
//        orderCount();
//        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.setChecked(true);
//                drawerLayout.closeDrawers();
//                return true;
//            }
//        });
//    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (!isSearch) {
//            mDrawerToggle.onConfigurationChanged(newConfig);
//        }
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.home) {
            findViewById(R.id.main_content).setVisibility(View.VISIBLE);
            findViewById(R.id.detailsFragment).setVisibility(View.GONE);
//            prepareActionBar(toolbar);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.main_content, new OrderHistory())
//                    .addToBackStack(null)
//                    .commit();

            viewPager.setCurrentItem(0);
            //fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new CurrentOrder()).commit();
            // Handle the camera action
        } else if(id == R.id.order_history){
            findViewById(R.id.main_content).setVisibility(View.GONE);
            findViewById(R.id.detailsFragment).setVisibility(View.VISIBLE);
            detailsToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(detailsToolbar);
            getSupportActionBar().setTitle("Order History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new OrderHistory())
                    .addToBackStack(null)
                    .commit();

        }else if(id == R.id.menu){
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

        }
        else if(id == R.id.settings){
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

    public void deliveryFragment(ArrayList<Order> deliveryOrders) {
        Delivery fragment = new Delivery();
        Bundle bundle = new Bundle();
        bundle.putSerializable("deliveryOrders", deliveryOrders);
        fragment.setArguments(bundle);
        Log.d("MainActivity", "deliveryFragment: bundle init" );

    }
//    private void currentOrderFragment() {
//        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, currentOrderFragment).commit();
//    }

//    public void inProgressFragment(ArrayList<Order> progressOrders) {
//
//        InProgress fragment = new InProgress();
////        Bundle bundle = new Bundle();
////        bundle.putSerializable("progressOrders",progressOrders);
////        fragment.setArguments(bundle);
//        Log.d("MainActivity", "inProgressFragment: bundle init" );
//    }

    public void orderDetailsFragment(String name, ArrayList<Item> itemList, String address, String contactNumber){
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("address", address);
        args.putString("contact", contactNumber);
        args.putSerializable("itemList", itemList);
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

    public void orderCount(){
        android.view.Menu menu = view.getMenu();
        menu.findItem(R.id.home).setTitle("Home " + "(" + currentCount + "/" + progressCount + "/" + deliveryCount + ")");


    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
   public void previousFragment(){
       findViewById(R.id.detailsFragment).setVisibility(View.GONE);
       findViewById(R.id.main_content).setVisibility(View.VISIBLE);

   }
    public String convertDateTime(String dateTime){
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
    public int getId(){
        return restaurantId;
    }
}

