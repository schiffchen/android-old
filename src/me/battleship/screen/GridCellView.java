package me.battleship.screen;

import android.content.Context;
import android.widget.FrameLayout;

public class GridCellView extends FrameLayout
{
	private static int height = 0;
	
	public GridCellView(Context context)
	{
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int w = MeasureSpec.getSize(widthMeasureSpec);
		height = (height < w / Game.SIZE ? w / Game.SIZE : height);
		super.onMeasure(MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(widthMeasureSpec)), MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(widthMeasureSpec)));
	}
}
