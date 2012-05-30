package me.battleship.screen;

import me.battleship.R;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class Game implements Screen
{
	private static final int SIZE = 10;
	
	
	@Override
	@SuppressWarnings("null")
	public View getView(Activity activity)
	{
		View root = ViewFactory.createView(R.layout.game, activity);
		RelativeLayout playgroundView = (RelativeLayout) root.findViewById(R.id.playgroundGrid);
		View leftView = null;
		View lastView = null;
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				View layout = new Button(activity);
				layout.setBackgroundResource(R.drawable.ic_launcher);
				@SuppressWarnings("static-access")
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (x == 0)
				{
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					leftView = layout;
				}
				else
				{
					params.addRule(RelativeLayout.RIGHT_OF, lastView.getId());
				}
				if (x == SIZE - 1)
				{
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				}
				if (y == 0)
				{
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}
				else
				{
					params.addRule(RelativeLayout.BELOW, leftView.getId());
				}
				if (y == SIZE - 1)
				{
					params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				}
				layout.setLayoutParams(params);
				lastView = layout;
				playgroundView.addView(layout);
			}
		}
		return root;
	}
	
	private class FieldClickListener implements OnClickListener
	{
		private final int x;
		private final int y;

		public FieldClickListener(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void onClick(View v)
		{
			System.out.println("x:" + x + " y:" + y);
		}
	}
}
