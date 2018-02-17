package tastifai.restaurant;

import android.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rohannevrikar.restaurant.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<Order> deliveryOrders= new ArrayList<>();
    public FragmentAdapter adapter;
    public static ArrayList<Order> progressOrders= new ArrayList<>();
    public static ArrayList<Order> orderList = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
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
        view.setNavigationItemSelectedListener(MainActivity.this);
        orderCount();

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
    public void updateViewPager(){
        FragmentAdapter.mFragments.clear();
        FragmentAdapter.mFragmentTitles.clear();
        adapter.addFragment(new CurrentOrder(), "ORDER" + "(" + currentCount + ")");
        adapter.addFragment(new InProgress(), "PROGRESS" + "(" + progressCount + ")");
        adapter.addFragment(new Delivery(), "DELIVERY" + "(" + deliveryCount + ")");
        viewPager.setAdapter(adapter);
    }
    private void initAction() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("MainActivity", "initAction: called");
        viewPager.setCurrentItem(0);
    }
    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Order order1 = new Order();
        order1.setCustomerName("Tastifai");
        order1.setContactNumber("7887543650");
        order1.setDeliveryAddress("MSH1202");
        orderList.add(order1);
        Order order = new Order();
        order.setCustomerName("Rohan");
        order.setContactNumber("8733000127");
        order.setDeliveryAddress("D21R30");
        orderList.add(order);
        Item item = new Item();
        item.setItem("Cheese Parantha");
        item.setQty("1");
        item.setPrice("70");
        itemList.add(item);
        Item item1 = new Item();
        item1.setItem("Chicken Hakka Noodles");
        item1.setQty("2");
        item1.setPrice("90");
        itemList.add(item1);
        Item item2 = new Item();
        item2.setItem("Chicken Sandwich");
        item2.setQty("3");
        item2.setPrice("60");
        itemList.add(item2);
        currentCount = orderList.size();
        Log.d("MainActivity", "initComponent: " + currentCount);
    }
    private void setupViewPager(ViewPager viewPager) {
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
        getSupportActionBar().setTitle("Cafe Tanstaafl");
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isSearch) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.current_order) {
            viewPager.setCurrentItem(0);
            //fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new CurrentOrder()).commit();
            // Handle the camera action
        } else if (id == R.id.in_progress) {

            viewPager.setCurrentItem(1);

            //inProgressFragment(progressOrders);
        } else if (id == R.id.delivery) {

            viewPager.setCurrentItem(2);

            //deliveryFragment(deliveryOrders);
        }else if(id == R.id.logout){


        }else if(id == R.id.order_history){
            //getSupportActionBar().setTitle("Order History");
            //getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, new OrderHistory()).commit();
        }else if(id == R.id.menu){
            //getSupportActionBar().setTitle("Menu");
            //getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, new RestaurantMenu()).commit();
        }else if(id == R.id.settings){
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
        menu.findItem(R.id.current_order).setTitle("Current Order " + "(" + currentCount + ")");
        menu.findItem(R.id.in_progress).setTitle("In Progress " + "(" + progressCount + ")");
        menu.findItem(R.id.delivery).setTitle("Delivery " + "(" + deliveryCount + ")");


    }
   public void previousFragment(){
       findViewById(R.id.detailsFragment).setVisibility(View.GONE);
       findViewById(R.id.main_content).setVisibility(View.VISIBLE);

   }


}

