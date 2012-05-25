package me.battleship;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ViewAnimator;

/**
 * A class which manages the currently displayed view
 *
 * @author Manuel Vögele
 */
public class ViewManager
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "ViewManager";
	
	/**
	 * The animator for making the transitions
	 */
	private ViewAnimator animator;
	
	/**
	 * Initializes the ViewManager using startView as initial view
	 * @param activity the activity in which the view will be displayed
	 * @param startView the view which will be initially showed
	 */
	private ViewManager(Activity activity, View startView)
	{
		animator = new ViewAnimator(activity);
		animator.addView(startView);
		animator.setAnimateFirstView(false);
		AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(1000);
		animator.setOutAnimation(animation);
		animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(1000);
		animator.setInAnimation(animation);
		activity.setContentView(animator);
	}
	
	/**
	 * Changes the view to the passed view
	 * 
	 * @param view the new view
	 */
	public void setView(View view)
	{
		animator.addView(view);
		animator.showNext();
		animator.removeViewAt(0);
	}
	
	/**
	 * The only one instance of the ViewManager
	 */
	private static ViewManager instance;
	
	/**
	 * Initializes the ViewManager
	 * 
	 * @param activity the activity for which the view should be managed
	 * @param startView the view which is initialy displayed
	 */
	public static void initialize(Activity activity, View startView)
	{
		if (instance != null)
		{
			Log.w(LOG_TAG, new IllegalStateException("ViewManager has already been initialized"));
		}
		instance = new ViewManager(activity, startView);
	}
	
	/**
	 * Returns the only instance of ViewManager
	 * @return the only instance of ViewManager
	 */
	public static ViewManager getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException("ViewManager has not been initialized,");
		}
		return instance;
	}
}
