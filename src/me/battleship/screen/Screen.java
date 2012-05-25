package me.battleship.screen;

import android.app.Activity;
import android.view.View;

/**
 * A interface representing a screen
 *
 * @author Manuel Vögele
 */
public interface Screen
{
	/**
	 * Returns the view for the current screen
	 * 
	 * @param activity the activity in which the screen will be shown
	 * @return the view for the current screen
	 */
	public View getView(Activity activity);
}
