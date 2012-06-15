package me.battleship.util;

import android.view.View;
import android.view.ViewGroup;

/**
 * A utility for handling views 
 *
 * @author Manuel Vögele
 */
public class ViewUtils
{
	/**
	 * Removes a view
	 * @param view the view to remove
	 */
	public static void removeView(View view)
	{
		((ViewGroup) view.getParent()).removeView(view);
	}
}
