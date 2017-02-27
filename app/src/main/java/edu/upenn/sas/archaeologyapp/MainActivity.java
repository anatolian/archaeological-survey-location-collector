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

    /**
     * Reference to the list view
     */
    ListView listView;

    /**
     * Reference to the list entry adapter
     */
    BucketListEntryAdapter listEntryAdapter;

    /**
     * Reference to the swipe refresh layout
     */
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Configure the new action button to handle clicks
        findViewById(R.id.fab_new).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Open DataEntryActivity to create a new entry
                MainActivity.super.startActivityUsingIntent(DataEntryActivity.class, false);

            }

        });

        // Configure the settings action button to handle clicks
        findViewById(R.id.fab_sync).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.this.startActivityUsingIntent(SyncActivity.class, false);

            }

        });

        // Store references to the list and list entry
        listView = (ListView) findViewById(R.id.main_activity_list_view);
        listEntryAdapter = new BucketListEntryAdapter(this, R.layout.bucket_list_entry);
        listView.setAdapter(listEntryAdapter);

        // Configure the list items to handle clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Open the data entry activity with fields pre-populated
                DataEntryElement dataEntryElement = listEntryAdapter.getItem(position);

                Bundle paramsToPass = new Bundle();
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_ID, dataEntryElement.getID());
                paramsToPass.putDouble(ConstantsAndHelpers.PARAM_KEY_LATITUDE, dataEntryElement.getLatitude());
                paramsToPass.putDouble(ConstantsAndHelpers.PARAM_KEY_LONGITUDE, dataEntryElement.getLongitude());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_IMAGE, dataEntryElement.getImagePath());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_MATERIAL, dataEntryElement.getMaterial());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_COMMENTS, dataEntryElement.getComments());

                startActivityUsingIntent(DataEntryActivity.class, false, paramsToPass);

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
    private void populateDataFromLocalStore() {

        // Get data from DB
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

        // Clear list and populate with data got from DB
        listEntryAdapter.clear();
        listEntryAdapter.addAll(dataBaseHandler.getRows());
        listEntryAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {

        super.onResume();

        populateDataFromLocalStore();

    }

}
