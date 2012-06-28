package me.battleship.screen;

import android.app.Activity;
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
	static ViewAnimator animator;

	/**
	 * The activity for which the screens are handled
	 */
	static Activity activity;
	
	/**
	 * The current screen
	 */
	private static Screen currentScreen;
	
	/**
	 * Initializes the ViewManager
	 * 
	 * @param activity the activity for which the view should be managed
	 * @param startScreen the screen which is initially displayed
	 */
	public static void initialize(@SuppressWarnings("hiding") Activity activity, Screen startScreen)
	{
		if (activity == null) {
			throw new IllegalStateException("The ScreenManager has already been initialized");
		}
		ScreenManager.activity = activity;
		animator = new ViewAnimator(activity);
		animator.addView(startScreen.getView(activity));
		animator.setAnimateFirstView(true);
		activity.setContentView(animator);
	}
	
	/**
	 * Changes the view to the passed view
	 * 
	 * @param screen the new screen
	 * @param outAnimation the animation to animate the current view out
	 * @param inAnimation the animation to animate the new view in
	 */
	public static void setScreen(final Screen screen, final int outAnimation, final int inAnimation)
	{
		checkInit();
		currentScreen = screen;
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				animator.setOutAnimation(activity, outAnimation);
				animator.setInAnimation(activity, inAnimation);
				animator.addView(screen.getView(activity));
				animator.showNext();
				animator.removeViewAt(0);
			}
		});
	}
	
	/**
	 * Returns the current screen
	 * @return the current screen - <code>null</code> if the screen manager is not initialized
	 */
	public static Screen getCurrentScreen()
	{
		return currentScreen;
	}
	
	/**
	 * Checks whether the screen manager is initialized. Throws an exception if not
	 * @throws IllegalStateException if the screen manager is not initialized
	 */
	public static void checkInit() throws IllegalStateException
	{
		if (activity == null) {
			throw new IllegalStateException("The screen manager is not initialized");
		}
	}
}
