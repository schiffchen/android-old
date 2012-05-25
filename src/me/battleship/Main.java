package me.battleship;

import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * The main activity
 * 
 * @author Manuel Vögele
 */
public class Main extends Activity 
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "Main";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "main activity started");
		FrameLayout frameLayout = new FrameLayout(this);
		ViewFactory.createView(R.layout.login, frameLayout, this);
		ViewManager.initialize(this, frameLayout);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}