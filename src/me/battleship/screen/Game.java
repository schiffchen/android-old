package me.battleship.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.battleship.Orientation;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.communication.OpponentConnection;
import me.battleship.communication.OpponentConnection.GameStartListener;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
		shipsToPlace = new ArrayList<ShipType>(5);
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
		previewNext();
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
	void previewNext()
	{
		FrameLayout topArea = (FrameLayout) root.findViewById(R.id.topArea);
		topArea.removeAllViews();
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		if (iterator.hasNext())
		{
			ImageView imageView = new ImageView(activity);
			int drawable = new Ship(iterator.next(), 0, 0, Orientation.HORIZONTAL).getDrawable();
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
					((ViewGroup)view.getParent()).removeView(view);
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
		iterator.remove();
		previewNext();
		Ship ship = new Ship(type, x, y, orientation);
		ImageView imageView = new ImageView(activity);
		imageView.setImageResource(ship.getDrawable());
		imageView.setLayoutParams(getLayoutParamsForShip(ship));
		playgroundView.addView(imageView);
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
				int shipsize = Ship.getSizeForType(shipsToPlace.iterator().next()) - 1;
				Orientation orientation;
				int x = getXFromId(view.getId());
				int y = getYFromId(view.getId());
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
			previewNext();
		}
	}
}
