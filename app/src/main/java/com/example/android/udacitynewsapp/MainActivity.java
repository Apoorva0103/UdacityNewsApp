package com.example.android.udacitynewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>> {

    private String BASE_URL = "http://content.guardianapis.com/search?";

    private static final String API_KEY = "31f49b6a-a80a-4df0-ab57-efd34ec8ad47";

    private String selectedSection;
    private static final int NEWS_LOADER_ID = 1;

    private DrawerLayout drawerLayout;
    private TextView emptyTV;
    private NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    private LoaderManager loaderManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTV = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.news_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ArrayList<NewsData> newsList = new ArrayList<>();
        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(this, newsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(newsRecyclerViewAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        selectedSection = getString(R.string.param_section_world);
        createLoader();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createLoader();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.section_world: {
                        selectedSection = getString(R.string.param_section_world);
                        createLoader();
                        break;
                    }
                    case R.id.section_science: {
                        selectedSection = getString(R.string.param_section_science);
                        createLoader();
                        break;
                    }
                    case R.id.section_environment: {
                        selectedSection = getString(R.string.param_section_environment);
                        createLoader();
                        break;
                    }
                    case R.id.section_technology: {
                        selectedSection = getString(R.string.param_section_technology);
                        createLoader();
                        break;
                    }
                    case R.id.section_sport: {
                        selectedSection = getString(R.string.param_section_sport);
                        createLoader();
                        break;
                    }
                    case R.id.section_settings: {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    }
                    default:
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<NewsData>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String pageSize = sharedPreferences.getString(
                getString(R.string.items_per_page_key),
                getString(R.string.default_items_per_page)
        );

        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", selectedSection);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.items_per_page_key), pageSize);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new AsyncNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsData>> loader, List<NewsData> newsList) {
        swipeRefreshLayout.setRefreshing(false);

        View loadingIndicator = findViewById(R.id.loading_bar);
        loadingIndicator.setVisibility(View.GONE);

        TextView emptyTV = findViewById(R.id.empty_view);
        emptyTV.setText(R.string.empty_list);

        if (!newsList.isEmpty()) {
            toolbar.setTitle(newsList.get(0).getNewsSectionName());
            emptyTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            newsRecyclerViewAdapter.addAll(newsList);
        } else {
            emptyTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            toolbar.setTitle(getString(R.string.app_name));
        }


    }

    @Override
    public void onLoaderReset(Loader<List<NewsData>> loader) {
    }


    private void createLoader() {
        if (isNetworkAvailable()) {
            loaderManager = getLoaderManager();
            Loader<String> loader = loaderManager.getLoader(NEWS_LOADER_ID);

            if (loader == null) {
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            }

        } else {
            View loadingIndicator = findViewById(R.id.loading_bar);
            loadingIndicator.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            emptyTV.setText(R.string.no_internet_connection);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
