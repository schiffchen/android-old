package me.battleship.screen;

import android.app.Activity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ViewAnimator;

/**
 * A class which manages the currently displayed screen
 *
 * @author Manuel Vögele
 */
public class ScreenManager
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
	 * The activity for which the screens are handled
	 */
	private final Activity activity;
	
	/**
	 * Initializes the ViewManager using startView as initial view
	 * @param activity the activity in which the view will be displayed
	 * @param startScreen the screen which will be initially showed
	 */
	private ScreenManager(Activity activity, Screen startScreen)
	{
		this.activity = activity;
		animator = new ViewAnimator(activity);
		animator.addView(startScreen.getView(activity));
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
	 * @param screen the new screen
	 */
	public void setScreen(Screen screen)
	{
		animator.addView(screen.getView(activity));
		animator.showNext();
		animator.removeViewAt(0);
	}
	
	/**
	 * The only one instance of the ViewManager
	 */
	private static ScreenManager instance;
	
	/**
	 * Initializes the ViewManager
	 * 
	 * @param activity the activity for which the view should be managed
	 * @param startScreen the screen which is initially displayed
	 */
	public static void initialize(Activity activity, Screen startScreen)
	{
		if (instance != null)
		{
			Log.w(LOG_TAG, new IllegalStateException("ViewManager has already been initialized"));
		}
		instance = new ScreenManager(activity, startScreen);
	}
	
	/**
	 * Returns the only instance of ViewManager
	 * @return the only instance of ViewManager
	 */
	public static ScreenManager getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException("ViewManager has not been initialized,");
		}
		return instance;
	}
}
