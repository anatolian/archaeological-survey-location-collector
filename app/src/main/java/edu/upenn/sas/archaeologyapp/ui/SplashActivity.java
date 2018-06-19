package edu.upenn.sas.archaeologyapp.ui;
import android.os.Handler;
import android.os.Bundle;
import edu.upenn.sas.archaeologyapp.R;
import edu.upenn.sas.archaeologyapp.util.Constants;
/**
 * The splash activity
 * @author Created by eanvith on 24/12/16.
 */
public class SplashActivity extends BaseActivity
{
    /**
     * App is launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Make this a fullscreen activity
        super.requestFullScreenActivity();
        // Setting the layout for this activity
        setContentView(R.layout.activity_splash);
        // Wait for specified time and start main activity
        new Handler().postDelayed(new Runnable() {
            /**
             * Run the handler
             */
            @Override
            public void run()
            {
                // The activity to start once the splash activity is complete
                SplashActivity.super.startActivityUsingIntent(MainActivity.class);
            }
        }, Constants.SPLASH_TIME_OUT);
    }

    /**
     * Disable on back pressed - this is the splash screen, the user will automatically be taken to the next screen.
     */
    @Override
    public void onBackPressed()
    {
    }
}