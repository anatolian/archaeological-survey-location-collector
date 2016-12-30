package edu.upenn.sas.archaeologyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by eanvith on 24/12/16.
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * Function to make the activity full screen
     */
    protected void requestFullScreenActivity() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

    }

    /**
     * Function to start a new Activity, and finish the current activity
     * @param activityToOpen The class of the Activity to be opened
     */
    protected void startActivityUsingIntent(Class activityToOpen) {

        startActivityUsingIntent(activityToOpen, true);

    }

    /**
     * Function to start a new Activity
     * @param activityToOpen The class of the Activity to be opened
     * @param finishCurrentActivity This flag decides whether the current activity must be closed
     *                              after starting the new activity
     */
    protected void startActivityUsingIntent(Class activityToOpen, boolean finishCurrentActivity) {

        Intent i = new Intent(this, activityToOpen);
        startActivity(i);

        if (finishCurrentActivity) {

            finish();

        }

    }

}
