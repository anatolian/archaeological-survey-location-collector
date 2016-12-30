package edu.upenn.sas.archaeologyapp;

import android.os.Handler;
import android.os.Bundle;

/**
 * Created by eanvith on 24/12/16.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make this a fullscreen activity
        super.requestFullScreenActivity();

        // Setting the layout for this activity
        setContentView(R.layout.activity_splash);

        // Wait for specified time and start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // The activity to start once the splash activity is complete
                SplashActivity.super.startActivityUsingIntent(MainActivity.class);

            }
        }, ConstantsAndHelpers.SPLASH_TIME_OUT);

    }

    @Override
    public void onBackPressed() {
        /*  Disable on back pressed - this is the splash screen,
         *  the user will automatically be taken to the next screen.
         */
    }

}