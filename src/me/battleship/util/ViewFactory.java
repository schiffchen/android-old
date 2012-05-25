package me.battleship.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * A factory for creating views
 *
 * @author Manuel Vögele
 */
public class ViewFactory
{
	/**
	 * Creates a new view from the specified resource and adds it to element parent
	 * @param resource the resource
	 * @param parent the parent element
	 * @param activity the activity for which the view is created
	 * @return the new view
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T createView(int resource, ViewGroup parent, Activity activity)
	{
		parent = (ViewGroup) activity.getLayoutInflater().inflate(resource, parent);
		return (T) parent.getChildAt(parent.getChildCount() - 1);
	}
}
