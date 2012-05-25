package me.battleship.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class ViewFactory
{
	@SuppressWarnings("unchecked")
	public static <T extends View> T createView(int resource, ViewGroup root, Activity activity)
	{
		ViewGroup parent = (ViewGroup) activity.getLayoutInflater().inflate(resource, root);
		return (T) parent.getChildAt(parent.getChildCount() - 1);
	}
}
