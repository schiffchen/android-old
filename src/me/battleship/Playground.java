package me.battleship;

import me.battleship.screen.GridCellView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * This is where game takes place
 * 
 * @author manuel
 */
public class Playground
{
	/**
	 * The size of the playground. The playground is assumed to be square so this is used for the x and y size.
	 */
	public static final int SIZE = 10;
	
	/**
	 * Stores all fields of this playground.
	 */
	private PlaygroundField[][] fields;
	
	/**
	 * Constructs a new playground
	 */
	public Playground()
	{
		fields = new PlaygroundField[SIZE][SIZE];
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				fields[x][y] = new PlaygroundField();
			}
		}
	}
	
	/**
	 * Returns a field of the playground
	 * 
	 * @param x the x position of the field
	 * @param y the y position of the field
	 * @return the field at the specified postitino
	 */
	public PlaygroundField getField(int x, int y)
	{
		return fields[x][y];
	}
	
	/**
	 * Draws an empty playground grid
	 * @param root the root layout
	 * @param listener the listener which will be attached to every field
	 */
	private static void makeGrid(RelativeLayout root, OnTouchListener listener)
	{
		Context context = root.getContext();
		root.removeAllViews();
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				if (y == 0)
				{
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}
				else
				{
					layoutParams.addRule(RelativeLayout.BELOW, getViewId(x, y - 1));
				}
				if (x == 0)
				{
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
				else
				{
					layoutParams.addRule(RelativeLayout.RIGHT_OF, getViewId(x - 1, y));
				}
				View cell = new GridCellView(context);
				cell.setLayoutParams(layoutParams);
				int id = getViewId(x, y);
				cell.setId(id);
				cell.setOnTouchListener(listener);
				cell.setBackgroundResource(R.drawable.border);
				root.addView(cell);
			}
		}
	}
	
	/**
	 * Draws the playground
	 * @param root the root element the view will be drawn to
	 * @param listener the listener that will be attached to every field
	 */
	public void drawView(RelativeLayout root, OnTouchListener listener)
	{
		makeGrid(root, listener);
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				PlaygroundField field = fields[x][y];
				Ship ship = field.getShip();
				if (ship != null && ship.getX() == x && ship.getY() == y)
				{
					View view = ship.getView();
					view.setLayoutParams(Ship.getLayoutParamsForShip(ship));
					root.addView(view);
				}
				if (field.isHit())
				{
					ImageView colorfield = makeColorForField(field, root.getContext());
					colorfield.setLayoutParams(getLayoutParamsForPos(x, y));
					root.addView(colorfield);
				}
			}
		}
	}
	
	/**
	 * Returns the id of the view at the specified position
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @return the id of the view at the specified position
	 */
	public static int getViewId(int x, int y)
	{
		return y * SIZE + x + 1;
	}
	
	/**
	 * Returns the x position of the specified view id
	 * 
	 * @param id
	 *           the view id
	 * @return the x position of the specified view id
	 */
	public static int getXFromId(int id)
	{
		return (id - 1) % SIZE;
	}
	
	/**
	 * Returns the y position of the specified view id
	 * 
	 * @param id
	 *           the view id
	 * @return the y position of the specified view id
	 */
	public static int getYFromId(int id)
	{
		return (id - 1) / SIZE;
	}

	/**
	 * Makes a colored view for the specified playground field
	 * 
	 * @param field
	 *           the field
	 * @param context
	 *           the context
	 * @return the colored view
	 */
	private static ImageView makeColorForField(PlaygroundField field, Context context)
	{
		ImageView colorfield = new ImageView(context);
		ColorDrawable colorDrawable = new ColorDrawable(field.isShip() ? Color.RED : Color.WHITE);
		colorDrawable.setAlpha(50);
		colorfield.setImageDrawable(colorDrawable);
		return colorfield;
	}
	
	/**
	 * Returns the layout params for the specified position
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @return the layout params for the specified position
	 */
	public static RelativeLayout.LayoutParams getLayoutParamsForPos(int x, int y)
	{
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int id = getViewId(x, y);
		layoutParams.addRule(RelativeLayout.ALIGN_LEFT, id);
		layoutParams.addRule(RelativeLayout.ALIGN_TOP, id);
		layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, id);
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
		return layoutParams;
	}
	

	
	/**
	 * Validates if a ship with the specified orientation can be placed on field orientation
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @param orientation the orientation of the ship
	 * @param type the type of the ship
	 * @return if the position is valid
	 */
	public boolean validateShipPos(int x, int y, Orientation orientation, ShipType type)
	{
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		int size = Ship.getSizeForType(type);
		if (orientation == Orientation.HORIZONTAL && x + size - 1 >= SIZE)
			return false;
		if (orientation == Orientation.VERTICAL && y + size - 1 >= SIZE)
			return false;
		for (int i = 0;i < size;i++)
		{
			if (orientation == Orientation.HORIZONTAL && fields[x + i][y].getShip() != null)
				return false;
			if (orientation == Orientation.VERTICAL && fields[x][y + i].getShip() != null)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the specified coordinate is on the field or not
	 * 
	 * @param x the x pos
	 * @param y the y pos
	 * @return if the specified position is on the playground
	 */
	public static boolean isPosOnPlaygroud(int x, int y)
	{
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		if (x >= SIZE)
			return false;
		if (y >= SIZE)
			return false;
		return true;
	}
}
