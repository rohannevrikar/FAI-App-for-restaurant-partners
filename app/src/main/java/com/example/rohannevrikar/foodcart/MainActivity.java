package com.example.rohannevrikar.foodcart;

import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<Order> deliveryOrders= new ArrayList<>();

    public static ArrayList<Order> progressOrders= new ArrayList<>();
    public static ArrayList<Order> orderList = new ArrayList<>();
    public static int CODE = 1;
    public static int currentCount;
    public static int progressCount;
    public static int deliveryCount;

    private Toolbar toolbar;
    private CurrentOrder currentOrderFragment;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        currentOrderFragment = new CurrentOrder();
        setSupportActionBar(toolbar);
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();
                    navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(MainActivity.this);
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
                    currentCount = orderList.size();
                    orderCount();
                    currentOrderFragment();


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




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.current_order) {
            getSupportActionBar().setTitle("Current Order");

            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, currentOrderFragment).commit();
            // Handle the camera action
        } else if (id == R.id.in_progress) {
            getSupportActionBar().setTitle("In Progress");
            inProgressFragment(progressOrders);
        } else if (id == R.id.delivery) {
            getSupportActionBar().setTitle("Delivery");
            deliveryFragment(deliveryOrders);
        }else if(id == R.id.logout){
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                orderList.clear();
                progressOrders.clear();
                deliveryOrders.clear();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deliveryFragment(ArrayList<Order> deliveryOrders) {
        Delivery fragment = new Delivery();
        Bundle bundle = new Bundle();
        bundle.putSerializable("deliveryOrders", deliveryOrders);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
    }
    private void currentOrderFragment() {
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, currentOrderFragment).commit();
    }

    public void inProgressFragment(ArrayList<Order> progressOrders) {

        InProgress fragment = new InProgress();
        Bundle bundle = new Bundle();
        bundle.putSerializable("progressOrders",progressOrders);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
    }

    public void orderDetailsFragment(String name, ArrayList<Item> itemList, String contactNumber){
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("phone", contactNumber);
        args.putSerializable("itemList", itemList);
        OrderDetails orderDetailsFragment = new OrderDetails();
        orderDetailsFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, orderDetailsFragment)
                .addToBackStack(null)
                .commit();

        ;
    }
    public void orderCount(){
        android.view.Menu menu = navigationView.getMenu();
        menu.findItem(R.id.current_order).setTitle("Current Order " + "(" + currentCount + ")");
        menu.findItem(R.id.in_progress).setTitle("In Progress " + "(" + progressCount + ")");
        menu.findItem(R.id.delivery).setTitle("Delivery " + "(" + deliveryCount + ")");


    }
    public void previousFragment(){
        getFragmentManager().popBackStackImmediate();
    }
}
