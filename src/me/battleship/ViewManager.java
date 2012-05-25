package me.battleship;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ViewAnimator;


public class ViewManager
{
	public static final String LOG_TAG = "ViewManager";
	
	private Activity activity;
	
	private ViewAnimator animator;
	
	private ViewManager(Activity activity, View startView)
	{
		this.activity = activity;
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
	
	public void setView(View view)
	{
		animator.addView(view);
		animator.showNext();
		animator.removeViewAt(0);
	}
	
	private static ViewManager instance;
	
	public static void initialize(Activity activity, View startView)
	{
		if (instance != null)
		{
			Log.w(LOG_TAG, new IllegalStateException("ViewManager has already been initialized"));
		}
		instance = new ViewManager(activity, startView);
	}
	
	public static ViewManager getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException("ViewManager has not been initialized,");
		}
		return instance;
	}
}
