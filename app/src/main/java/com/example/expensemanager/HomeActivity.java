package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments for different tabs
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private StatsFragment statsFragment;

    private FirebaseAuth mAuth;  // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // Set the layout for the activity

        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Authentication instance

        Toolbar toolbar = findViewById(R.id.my_toolbar);  // Initialize Toolbar
        toolbar.setTitle("Rupiyala Manage");  // Set title for Toolbar
        setSupportActionBar(toolbar);  // Set Toolbar as support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable up button
        getSupportActionBar().setHomeButtonEnabled(true);  // Enable home button

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);  // Initialize DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,  // "Open drawer" accessibility label for screen readers
                R.string.navigation_drawer_close  // "Close drawer" accessibility label for screen readers
        ) {
        };
        toggle.setDrawerIndicatorEnabled(true);  // Enable drawer indicator
        drawerLayout.addDrawerListener(toggle);  // Set drawer listener

        toggle.syncState();  // Synchronize the state of the drawer indicator with the DrawerLayout

        NavigationView navigationView = findViewById(R.id.navView);  // Initialize NavigationView
        navigationView.setNavigationItemSelectedListener(this);  // Set navigation item click listener

        bottomNavigationView = findViewById(R.id.bottomNavbar);  // Initialize BottomNavigationView
        frameLayout = findViewById(R.id.main_frame);  // Initialize FrameLayout

        // Initialize Fragments for different tabs
        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        statsFragment = new StatsFragment();

        setFragment(dashboardFragment);  // Set initial fragment to DashboardFragment

        // Set listener for BottomNavigationView item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        setFragment(dashboardFragment);  // Switch to DashboardFragment
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);  // Switch to IncomeFragment
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);  // Switch to ExpenseFragment
                        return true;
                    case R.id.stats:
                        setFragment(statsFragment);  // Switch to StatsFragment
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // Method to replace current fragment in FrameLayout
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // Close the navigation drawer if open; otherwise, proceed with default back behavior
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    // Method to handle navigation item selection in the drawer
    public void displaySelectedListener(int itemId) {
        Fragment fragment = null;

        switch (itemId) {
            case android.R.id.home:
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);  // Open navigation drawer from start (left)
                return;
            case R.id.dashboard:
                bottomNavigationView.setSelectedItemId(R.id.dashboard);  // Set BottomNavigationView item to Dashboard
                fragment = new DashboardFragment();  // Initialize DashboardFragment
                break;
            case R.id.income:
                bottomNavigationView.setSelectedItemId(R.id.income);  // Set BottomNavigationView item to Income
                fragment = new IncomeFragment();  // Initialize IncomeFragment
                break;
            case R.id.expense:
                bottomNavigationView.setSelectedItemId(R.id.expense);  // Set BottomNavigationView item to Expense
                fragment = new ExpenseFragment();  // Initialize ExpenseFragment
                break;
            case R.id.stats:
                bottomNavigationView.setSelectedItemId(R.id.stats);  // Set BottomNavigationView item to Stats
                bottomNavigationView.findViewById(R.id.stats).performClick();  // Perform click on Stats item
                bottomNavigationView.performClick();  // Perform click on BottomNavigationView
                fragment = new StatsFragment();  // Initialize StatsFragment
                break;
            case R.id.account:
                fragment = new AccountFragment();  // Initialize AccountFragment
                break;
            case R.id.logout:
                mAuth.signOut();  // Sign out user from Firebase
                startActivity(new Intent(getApplicationContext(), MainActivity.class));  // Redirect to MainActivity
                break;
            case R.id.google_map1:
                startActivity(new Intent(getApplicationContext(), MapActivity.class));  // Redirect to MapActivity
                break;
            case R.id.sensor1:
                startActivity(new Intent(getApplicationContext(), SensorActivity.class));  // Redirect to SensorActivity
                break;
        }

        // Replace current fragment with selected fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);  // Close navigation drawer from start (left)
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i("ITEM ID", Integer.toString(item.getItemId()));  // Log selected item ID
        displaySelectedListener(item.getItemId());  // Call method to handle item selection
        return true;
    }
}
