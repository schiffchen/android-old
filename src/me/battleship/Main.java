package me.battleship;

import me.battleship.communication.Connection;
import me.battleship.screen.LoginScreen;
import me.battleship.screen.ScreenManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

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
		ScreenManager.initialize(this, new LoginScreen());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (ScreenManager.getCurrentScreen().onKeyDown(keyCode, event))
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (Connection.INSTANCE != null) {
			Connection.INSTANCE.disconnect();
		}
	}
}