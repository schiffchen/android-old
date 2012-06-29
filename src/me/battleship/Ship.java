package me.battleship;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * A ship
 * 
 * @author manuel
 */
public class Ship
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "Ship";
	
	/**
	 * The type of the ship
	 */
	private ShipType type;

	/**
	 * The size of the ship
	 */
	private int size;

	/**
	 * The id of the drawable of the ship
	 */
	private int drawable;

	/**
	 * The id of the name of the ship
	 */
	private int name;

	/**
	 * The x position of the ship
	 */
	private int x;

	/**
	 * The y position of the ship
	 */
	private int y;

	/**
	 * The orientation of the ship
	 */
	private Orientation orientation;
	
	/**
	 * An array containing the field that was destroyed 
	 */
	private boolean fieldDestroyed[];
	
	/**
	 * The view displaying this ship
	 */
	private View view;

	/**
	 * Constructs a new ship
	 * 
	 * @param type
	 *           The type of the ship
	 * @param x
	 *           The x position of the ship
	 * @param y
	 *           The y position of the ship
	 * @param orientation
	 *           the orientation of the ship
	 * @param view
	 *           the view this ship is displayed in
	 */
	public Ship(ShipType type, int x, int y, Orientation orientation, View view)
	{
		if (type == null)
		{
			throw new NullPointerException("type may not be null");
		}
		this.type = type;
		this.x = x;
		this.y = y;
		this.orientation = orientation;
		this.size = getSizeForType(type);
		this.view = view;
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				name = R.string.aircraft_carrier;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.aircraftcarrier_horizontal : R.drawable.aircraftcarrier_vertical);
			break;
			case BATTLESHIP:
				name = R.string.battleship;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.battleship_horizontal : R.drawable.battleship_vertical);
			break;
			case SUBMARINE:
				name = R.string.submarine;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.submarine_horizontal : R.drawable.submarine_vertical);
			break;
			case DESTROYER:
				name = R.string.destoryer;
				drawable = (orientation == Orientation.HORIZONTAL ? R.drawable.destroyer_horizontal : R.drawable.destroyer_vertical);
			break;
		}
		fieldDestroyed = new boolean[size];
		for (int i = 0;i < size;i++)
		{
			fieldDestroyed[i] = false;
		}
	}
	
	/**
	 * Returns the size for the specified ship type
	 * 
	 * @param type
	 *           the type of the ship
	 * @return the size
	 */
	public static int getSizeForType(ShipType type)
	{
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				return 5;
			case BATTLESHIP:
				return 4;
			case SUBMARINE:
				return 3;
			case DESTROYER:
				return 2;
			default:
				Log.wtf(LOG_TAG, "Unrecognized value " + type + " in getSizeForType(ShipType)");
				return -1;
		}
	}
	
	/**
	 * Returns the ship type for the specified size
	 * 
	 * @param size
	 *           the size
	 * @return the ship type
	 * @throws IllegalArgumentException
	 *            if there is no ship type with the specified size
	 */
	public static ShipType getTypeForSize(int size) throws IllegalArgumentException
	{
		switch (size)
		{
			case 5:
				return ShipType.AIRCRAFT_CARRIER;
			case 4:
				return ShipType.BATTLESHIP;
			case 3:
				return ShipType.SUBMARINE;
			case 2:
				return ShipType.DESTROYER;
			default:
				throw new IllegalArgumentException("unrecognized value " + size + " in getTypeForSize(int)");
		}
	}

	/**
	 * Returns the type of the ship
	 * 
	 * @return the type of the ship
	 */
	public ShipType getType()
	{
		return type;
	}

	/**
	 * Returns the size of the ship
	 * 
	 * @return the size of the ship
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Returns the id of the name of the ship
	 * 
	 * @return the id of the name of the ship
	 */
	public int getName()
	{
		return name;
	}

	/**
	 * Returns the id of the drawable of the ship
	 * 
	 * @return the id of the drawable of the ship
	 */
	public int getDrawable()
	{
		return drawable;
	}

	/**
	 * Returns the x pos of the ship
	 * 
	 * @return the x pos of the ship
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y pos of the ship
	 * 
	 * @return the y pos of the ship
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Returns the orientation of the ship
	 * 
	 * @return the orientation of the ship
	 */
	public Orientation getOrientation()
	{
		return orientation;
	}
	
	/**
	 * Returns the view displaying this ship
	 * @return the view displaying this ship
	 */
	public View getView()
	{
		return view;
	}
	
	/**
	 * Destroys the field at the specified position
	 * 
	 * @param xpos
	 *           the x position
	 * @param ypos
	 *           the y position
	 * @throws IllegalArgumentException
	 *            if the field is not a field of the ship
	 */
	public void destroyField(int xpos, int ypos) throws IllegalArgumentException
	{
		for (int i = 0;i < size;i++)
		{
			int iX, iY;
			if (orientation == Orientation.HORIZONTAL)
			{
				iX = x + i;
				iY = y;
			}
			else
			{
				iX = x;
				iY = y + i;
			}
			if (iX == xpos && iY == ypos)
			{
				fieldDestroyed[i] = true;
				return;
			}
		}
		throw new IllegalArgumentException("Position " + xpos + "," + ypos + " is not a position of this ship (" + x + "," + y + "," + orientation + ")");
	}
	
	/**
	 * Returns if all fields of this ship are destroyed
	 * 
	 * @return if all fields of this ship are destroyed
	 */
	public boolean areAllFieldsDestroyed()
	{
		for (boolean destroyed : fieldDestroyed)
		{
			if (destroyed == false)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sets the view of the ship
	 * 
	 * @param view the view of the ship
	 */
	public void setView(View view)
	{
		this.view = view;
	}
	
	/**
	 * Returns the LayoutParams for the specified ship
	 * 
	 * @param ship
	 *           the ship
	 * @return the layout params for the ship
	 */
	public static RelativeLayout.LayoutParams getLayoutParamsForShip(Ship ship)
	{
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int startX = ship.getX();
		int startY = ship.getY();
		int endX;
		int endY;
		int size = ship.getSize() - 1;
		if (ship.getOrientation() == Orientation.HORIZONTAL)
		{
			endX = startX + size;
			endY = startY;
		}
		else
		{
			endX = startX;
			endY = startY + size;
		}
		layoutParams.addRule(RelativeLayout.ALIGN_LEFT, Playground.getViewId(startX, startY));
		layoutParams.addRule(RelativeLayout.ALIGN_TOP, Playground.getViewId(startX, startY));
		layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, Playground.getViewId(endX, endY));
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, Playground.getViewId(endX, endY));
		return layoutParams;
	}
}
