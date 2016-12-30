package edu.upenn.sas.archaeologyapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * This activity shows the user the list of items presently in his bucket
 * Created by eanvith on 30/12/16.
 */

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listView;
    BucketListEntryAdapter listEntryAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();

    }

    /**
     * Initialises all the views and other layout components
     */
    private void initialiseViews() {

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure the action button to handle clicks
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // TODO: Begin data entry process here.
                Snackbar.make(view, "Work in progress, check back later.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }

        });

        // Store references to the list and list entry
        listView = (ListView) findViewById(R.id.news_fragment_news_list_view);
        listEntryAdapter = new BucketListEntryAdapter(this, R.layout.bucket_list_entry);
        listView.setAdapter(listEntryAdapter);

        // Configure the list items to handle clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // TODO: Do we need to take the user to a new screen where he can see details about the clicked item?

            }

        });

        // Get a reference for the swipe layout and set the listener
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_activity_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Populate the list with data from DB
        populateDataFromLocalStore();

    }

    /**
     * This function is called when the user swipes down to refresh the list
     */
    @Override
    public void onRefresh() {

        // Populate list with data from DB
        swipeRefreshLayout.setRefreshing(true);
        populateDataFromLocalStore();
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Function to populate the list with data available locally
     */
    void populateDataFromLocalStore() {

        // TODO: Populate data from DB

    }

}
