package me.battleship.screen;

import me.battleship.Playground;
import android.content.Context;
import android.widget.FrameLayout;

/**
 * A view representing a grid cell 
 *
 * @author Manuel Vögele
 */
public class GridCellView extends FrameLayout
{
	/**
	 * Variable to save the maximum height tried by the layout manager
	 */
	private static int height = 0;
	
	/**
	 * Initializes a new GridCellView
	 * @param context the context
	 */
	public GridCellView(Context context)
	{
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int w = MeasureSpec.getSize(widthMeasureSpec);
		height = (height < w / Playground.SIZE ? w / Playground.SIZE : height);
		super.onMeasure(MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(widthMeasureSpec)), MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(widthMeasureSpec)));
	}
}
