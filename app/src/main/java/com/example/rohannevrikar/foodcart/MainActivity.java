package com.example.rohannevrikar.foodcart;

import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<Order> progressOrders= new ArrayList<>();
    public static ArrayList<Order> orderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tastifai");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_first) {
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new CurrentOrder()).commit();
            // Handle the camera action
        } else if (id == R.id.nav_second) {
            inProgressFragment(progressOrders);
        } else if (id == R.id.nav_third) {
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new Delivery()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void inProgressFragment(ArrayList<Order> progressOrders) {

        InProgress fragment = new InProgress();
        Bundle bundle = new Bundle();
        bundle.putSerializable("progressOrders",progressOrders);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();

        Log.d("MainActivity", "inProgressFragment: " + progressOrders.toString());


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
    public void previousFragment(){
        getFragmentManager().popBackStackImmediate();
    }
}
