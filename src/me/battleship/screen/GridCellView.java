package me.battleship.screen;

import android.content.Context;
import android.widget.FrameLayout;

public class GridCellView extends FrameLayout
{
	public GridCellView(Context context)
	{
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
