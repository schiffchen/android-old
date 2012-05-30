package me.battleship.screen;

import me.battleship.R;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class Game implements Screen
{
	private static final int SIZE = 10;
	
	
	@Override
	@SuppressWarnings("null")
	public View getView(Activity activity)
	{
		View root = ViewFactory.createView(R.layout.game, activity);
		LinearLayout playgroundView = (LinearLayout) root.findViewById(R.id.playgroundGrid);
		for (int y = 0;y < SIZE;y++)
		{
			LinearLayout row = new LinearLayout(activity);
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			playgroundView.addView(row);
			for (int x = 0;x < SIZE;x++)
			{
				GridCellView gridCellView = new GridCellView(activity);
				gridCellView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
				gridCellView.setBackgroundResource(R.drawable.border);
				gridCellView.setOnClickListener(new FieldClickListener(x, y));
				row.addView(gridCellView);
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
