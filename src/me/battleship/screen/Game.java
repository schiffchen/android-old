package me.battleship.screen;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

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
	private List<ShipType> shipsToPlace;

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
	 * Instantiates a new Game
	 * @param connection the connection to the opponent
	 */
	public Game(OpponentConnection connection)
	{
		this.connection = connection;
		List<ShipType> ships = Arrays.asList(ShipType.AIRCRAFT_CARRIER, ShipType.BATTLESHIP, ShipType.SUBMARINE, ShipType.SUBMARINE, ShipType.DESTROYER);
		shipsToPlace = new ArrayList<ShipType>(ships);
	}

	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		root = ViewFactory.createView(R.layout.game, activity);
		playgroundView = (RelativeLayout) root.findViewById(R.id.playgroundGrid);
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
				cell.setOnClickListener(new FieldClickListener(x, y));
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
	 * @param x the x position
	 * @param y the y position
	 * @return the id of the view at the specified position
	 */
	private static int getViewId(int x, int y)
	{
		return y * SIZE + x + 1;
	}

	/**
	 * Generates the preview for the next ship to place
	 */
	void previewNext()
	{
		FrameLayout topArea = (FrameLayout) root.findViewById(R.id.topArea);
		topArea.removeAllViews();
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		if (iterator.hasNext()) {
			ImageView imageView = new ImageView(activity);
			int drawable = new Ship(iterator.next(), 0, 0, Orientation.HORIZONTAL).getDrawable();
			imageView.setImageResource(drawable);
			topArea.addView(imageView);
		}
		else
		{
			connection.sendDiceroll(this);
			// TODO
		}
	}

	/**
	 * Returns the LayoutParams for the specified ship
	 * 
	 * @param ship the ship
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
	 * @param x the x position
	 * @param y the y position
	 */
	void placeShip(int x, int y) {
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		ShipType type = iterator.next();
		iterator.remove();
		previewNext();
		Ship ship = new Ship(type, x, y, Orientation.VERTICAL);
		ImageView imageView = new ImageView(activity);
		imageView.setImageResource(ship.getDrawable());
		imageView.setLayoutParams(getLayoutParamsForShip(ship));
		playgroundView.addView(imageView);
	}

	@Override
	public void onGameStart(boolean yourturn)
	{
		System.out.println("Game started. " + yourturn);
	}

	/**
	 * A listener called when a field is clicked 
	 *
	 * @author Manuel Vögele
	 */
	private class FieldClickListener implements OnClickListener
	{
		/**
		 * The x position of the field
		 */
		private final int x;
		
		/**
		 * The y position of the field
		 */
		private final int y;

		/**
		 * Initializes a new FieldClickListener
		 * 
		 * @param x the x position of the field
		 * @param y the y position of the field
		 */
		public FieldClickListener(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public void onClick(View v)
		{
			placeShip(x, y);
		}
	}
}
