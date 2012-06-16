package me.battleship.screen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.battleship.Orientation;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.communication.OpponentConnection;
import me.battleship.communication.OpponentConnection.GameStartListener;
import me.battleship.util.ViewFactory;
import me.battleship.util.ViewUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * This is where the match takes place
 * 
 * @author Manuel Vögele
 */
public class Game implements Screen, GameStartListener
{
	/**
	 * The x and y size of the playground
	 */
	public static final int SIZE = 10;

	/**
	 * A list containing the ships which have to be placed on the playground
	 */
	List<ShipType> shipsToPlace;
	
	/**
	 * The currently visible red views
	 */
	Set<View> redViews;
	
	/**
	 * A array storing which ship is placed on the fieldsS
	 */
	Ship[][] fields = new Ship[SIZE][SIZE];

	/**
	 * The root view
	 */
	private View root;

	/**
	 * The grid to place the ships in
	 */
	private RelativeLayout playgroundView;

	/**
	 * The activity
	 */
	private Activity activity;

	/**
	 * The connection to the opponent
	 */
	private final OpponentConnection connection;

	/**
	 * A dialog displayed while waiting for the enemy
	 */
	private AlertDialog waitingDialog;

	/**
	 * Instantiates a new Game
	 * 
	 * @param connection
	 *           the connection to the opponent
	 */
	public Game(OpponentConnection connection)
	{
		this.connection = connection;
		redViews = new HashSet<View>();
		shipsToPlace = new LinkedList<ShipType>();
		shipsToPlace.add(ShipType.AIRCRAFT_CARRIER);
		shipsToPlace.add(ShipType.BATTLESHIP);
		shipsToPlace.add(ShipType.SUBMARINE);
		shipsToPlace.add(ShipType.SUBMARINE);
		shipsToPlace.add(ShipType.DESTROYER);
	}

	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		root = ViewFactory.createView(R.layout.game, activity);
		playgroundView = (RelativeLayout) root.findViewById(R.id.playgroundGrid);
		FieldTouchListener fieldTouchListener = new FieldTouchListener();
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
				View cell = new GridCellView(activity);
				cell.setLayoutParams(layoutParams);
				int id = getViewId(x, y);
				cell.setId(id);
				cell.setOnTouchListener(fieldTouchListener);
				cell.setBackgroundResource(R.drawable.border);
				playgroundView.addView(cell);
			}
		}
		refreshPreview();
		return root;
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
	static int getViewId(int x, int y)
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
	static int getXFromId(int id)
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
	static int getYFromId(int id)
	{
		return (id - 1) / SIZE;
	}

	/**
	 * Generates the preview for the next ship to place
	 */
	void refreshPreview()
	{
		FrameLayout topArea = (FrameLayout) root.findViewById(R.id.topArea);
		topArea.removeAllViews();
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		if (iterator.hasNext())
		{
			ImageView imageView = new ImageView(activity);
			int drawable = new Ship(iterator.next(), 0, 0, Orientation.HORIZONTAL, null).getDrawable();
			imageView.setImageResource(drawable);
			imageView.setOnClickListener(new SwitchNextShipListener());
			topArea.addView(imageView);
		}
		else
		{
			Button button = new Button(activity);
			button.setText(R.string.ready);
			button.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					ViewUtils.removeView(view);
					sendDiceroll();
				}
			});
			topArea.addView(button);
		}
	}

	/**
	 * Returns the LayoutParams for the specified ship
	 * 
	 * @param ship
	 *           the ship
	 * @return the layout params for the ship
	 */
	private static RelativeLayout.LayoutParams getLayoutParamsForShip(Ship ship)
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
		layoutParams.addRule(RelativeLayout.ALIGN_LEFT, getViewId(startX, startY));
		layoutParams.addRule(RelativeLayout.ALIGN_TOP, getViewId(startX, startY));
		layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, getViewId(endX, endY));
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, getViewId(endX, endY));
		return layoutParams;
	}

	/**
	 * Place a ship at the specified position
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @param orientation
	 *           the orientation of the ship
	 */
	void placeShip(int x, int y, Orientation orientation)
	{
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		ShipType type = iterator.next();
		if (!validateShipPos(x, y, orientation, type))
		{
			HashSet<View> views = new HashSet<View>();
			for (int i = 0;i < Ship.getSizeForType(type);i++)
			{
				boolean horizontal = orientation == Orientation.HORIZONTAL;
				if (horizontal && !isViewOnPlaygroud(x + i, y))
				{
					continue;
				}
				if (!horizontal && !isViewOnPlaygroud(x, y + i))
				{
					continue;
				}
				int viewId = getViewId((horizontal ? x + i : x), (horizontal ? y : y + i));
				views.add(playgroundView.findViewById(viewId));
			}
			markFieldsRed(views);
			return;
		}
		markFieldsRed(null);
		iterator.remove();
		refreshPreview();
		ImageView imageView = new ImageView(activity);
		Ship ship = new Ship(type, x, y, orientation, imageView);
		imageView.setImageResource(ship.getDrawable());
		imageView.setLayoutParams(getLayoutParamsForShip(ship));
		for (int i = 0;i < ship.getSize();i++)
		{
			if (orientation == Orientation.HORIZONTAL) 
			{
				fields[x + i][y] = ship;
			}
			else
			{
				fields[x][y + i] = ship;
			}
		}
		playgroundView.addView(imageView);
	}
	
	/**
	 * Marks the specified fields red. If already some fields are marked red, they will be removed.
	 * @param views the views to mark red. If <code>null</code> no views will be marked red.
	 */
	private void markFieldsRed(Collection<View> views)
	{
		for (View view : redViews)
		{
			ViewUtils.removeView(view);
		}
		redViews.clear();
		if (views == null)
			return;
		for (View view : views)
		{
			int id = view.getId();
			LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_LEFT, id);
			layoutParams.addRule(RelativeLayout.ALIGN_TOP, id);
			layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, id);
			layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
			ColorDrawable color = new ColorDrawable(Color.RED);
			color.setAlpha(50);
			ImageView imageView = new ImageView(activity);
			imageView.setImageDrawable(color);
			imageView.setLayoutParams(layoutParams);
			playgroundView.addView(imageView);
			redViews.add(imageView);
		}
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
			if (orientation == Orientation.HORIZONTAL && fields[x + i][y] != null)
				return false;
			if (orientation == Orientation.VERTICAL && fields[x][y + i] != null)
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
	private static boolean isViewOnPlaygroud(int x, int y)
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
	
	/**
	 * Removes a ship from the playground
	 * @param x the x position of the ship
	 * @param y the y position of the ship
	 */
	void removeShip(int x, int y)
	{
		markFieldsRed(null);
		Ship ship = fields[x][y];
		x = ship.getX();
		y = ship.getY();
		for (int i = 0;i < ship.getSize();i++)
		{
			if (ship.getOrientation() == Orientation.HORIZONTAL)
			{
				fields[x + i][y] = null;
			}
			else
			{
				fields[x][y + i] = null;
			}
		}
		ShipType type = ship.getType();
		int index = shipsToPlace.indexOf(type);
		if (index == -1)
		{
			shipsToPlace.add(type);
		}
		else
		{
			shipsToPlace.add(index, type);
		}
		refreshPreview();
		ViewUtils.removeView(ship.getView());
	}
	
	/**
	 * Sends a diceroll to the enemy
	 */
	void sendDiceroll()
	{
		Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		FrameLayout view = ViewFactory.createView(R.layout.progress_with_text, activity);
		TextView text = (TextView) view.findViewById(R.id.progessText);
		text.setText(R.string.waiting_for_opponent);
		builder.setView(view);
		waitingDialog = builder.show();
		connection.sendDiceroll(this);
	}

	@Override
	public void onGameStart(boolean yourturn)
	{
		waitingDialog.dismiss();
		waitingDialog = null;
		System.out.println("Game started. " + yourturn);
	}

	/**
	 * A listener called when a field is clicked
	 * 
	 * @author Manuel Vögele
	 */
	private class FieldTouchListener implements OnTouchListener
	{		
		/**
		 * The x position at which the gesture started
		 */
		private float startx;
		
		/**
		 * The y position at which the gesture started
		 */
		private float starty;
		
		/**
		 * Instantiates a new FieldTouchListener
		 */
		public FieldTouchListener()
		{
			startx = Float.NaN;
			starty = Float.NaN;
		}
		
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			int x = getXFromId(view.getId());
			int y = getYFromId(view.getId());
			if (fields[x][y] != null)
			{
				removeShip(x, y);
				return false;
			}
			Iterator<ShipType> iterator = shipsToPlace.iterator();
			if (!iterator.hasNext())
			{
				return false;
			}
			float eventx = event.getX();
			float eventy = event.getY();
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				startx = eventx;
				starty = eventy;
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				float xdist = Math.abs(startx - eventx);
				float ydist = Math.abs(starty - eventy);
				int shipsize = Ship.getSizeForType(iterator.next()) - 1;
				Orientation orientation;
				if (xdist > ydist)
				{
					x = (startx <= eventx ? x : x - shipsize);
					orientation = Orientation.HORIZONTAL;
				}
				else
				{
					y = (starty <= eventy ? y : y - shipsize);
					orientation = Orientation.VERTICAL;
				}
				startx = Float.NaN;
				starty = Float.NaN;
				placeShip(x, y, orientation);
			}
			return true;
		}
	}
	
	/**
	 * A listener switching to the next ship to set 
	 *
	 * @author Manuel Vögele
	 */
	private class SwitchNextShipListener implements OnClickListener
	{
		/**
		 * Instantiates a new SwitchNextShipListener
		 */
		public SwitchNextShipListener()
		{
			// Nothing to do
		}
		
		@Override
		public void onClick(View v)
		{
			Iterator<ShipType> iterator = shipsToPlace.iterator();
			ShipType next = iterator.next();
			int count = 0;
			do
			{
				iterator.remove();
				count++;
			} while (iterator.next() == next);
			for (int i = 0;i < count;i++)
			{
				shipsToPlace.add(next);
			}
			refreshPreview();
		}
	}
}
