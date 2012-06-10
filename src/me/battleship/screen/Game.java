package me.battleship.screen;

import me.battleship.R;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class Game implements Screen
{
	public static final int SIZE = 10;
	
	@Override
	public View getView(Activity activity)
	{
		View root = ViewFactory.createView(R.layout.game, activity);
		RelativeLayout playgroundView = (RelativeLayout) root.findViewById(R.id.playgroundGrid);
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				if (y == 0) {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}
				else {
					layoutParams.addRule(RelativeLayout.BELOW, getViewId(x, y - 1));
				}
				if (x == 0) {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
				else {
					layoutParams.addRule(RelativeLayout.RIGHT_OF, getViewId(x - 1, y));
				}
				View cell = new GridCellView(activity);
				cell.setLayoutParams(layoutParams);
				int id = getViewId(x, y);
				cell.setId(id);
				cell.setOnClickListener(new FieldClickListener(x, y));
				cell.setBackgroundResource(R.drawable.border);
				playgroundView.addView(cell);
			}
		}
		return root;
	}
	
	private int getViewId(int x, int y) {
		return y * SIZE + x + 1;
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
