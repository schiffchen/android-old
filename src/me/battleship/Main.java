package me.battleship;

import android.app.Activity;
import android.os.Bundle;

/**
 * The main activity
 *
 * @author manuel
 */
public class Main extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}