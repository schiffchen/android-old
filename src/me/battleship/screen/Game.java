package me.battleship.screen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import me.battleship.Orientation;
import me.battleship.PlaygroundField;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.communication.OpponentConnection;
import me.battleship.communication.OpponentConnection.OpponentConnectionListener;
import me.battleship.communication.Result;
import me.battleship.util.ViewFactory;
import me.battleship.util.ViewUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.widget.Toast;

/**
 * This is where the match takes place
 * 
 * @author Manuel Vögele
 */
public class Game implements Screen, OpponentConnectionListener
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
	 * An array storing the fields of the player
	 */
	PlaygroundField[][] fields = new PlaygroundField[SIZE][SIZE];
	
	/**
	 * An array storing the fields of the opponent
	 */
	PlaygroundField[][] opponentFields = new PlaygroundField[SIZE][SIZE];

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
	AlertDialog waitingDialog;
	
	/**
	 * Indicates whether the game has already started
	 */
	boolean gameStarted;
	
	/**
	 * Indicates whether its your turn or not
	 */
	private boolean yourturn;
	
	/**
	 * The ships of the player
	 */
	private Set<Ship> ships;

	/**
	 * Instantiates a new Game
	 * 
	 * @param connection
	 *           the connection to the opponent
	 */
	public Game(OpponentConnection connection)
	{
		this.connection = connection;
		connection.setListener(this);
		gameStarted = false;
		ships = new HashSet<Ship>();
		redViews = new HashSet<View>();
		shipsToPlace = new LinkedList<ShipType>();
		shipsToPlace.add(ShipType.AIRCRAFT_CARRIER);
		shipsToPlace.add(ShipType.BATTLESHIP);
		shipsToPlace.add(ShipType.SUBMARINE);
		shipsToPlace.add(ShipType.DESTROYER);
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
				fields[x][y] = new PlaygroundField();
				opponentFields[x][y] = new PlaygroundField();
			}
		}
		makeGrid();
		refreshPreview();
		return root;
	}

	/**
	 * Draws an empty playground grid
	 */
	private void makeGrid()
	{
		playgroundView.removeAllViews();
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
	}
	
	/**
	 * Draws the playground
	 * 
	 * @param viewFields
	 *           the fields which should be drawn
	 */
	void drawView(PlaygroundField viewFields[][])
	{
		makeGrid();
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				PlaygroundField field = viewFields[x][y];
				Ship ship = field.getShip();
				if (ship != null && ship.getX() == x && ship.getY() == y)
				{
					View view = ship.getView();
					view.setLayoutParams(getLayoutParamsForShip(ship));
					playgroundView.addView(view);
				}
				if (field.isHit())
				{
					ImageView colorfield = makeColorForField(field);
					colorfield.setLayoutParams(getLayoutParamsForPos(x, y));
					playgroundView.addView(colorfield);
				}
			}
		}
	}
	
	/**
	 * Makes a colored view for the specified playground field
	 * 
	 * @param field
	 *           the field
	 * @return the colored view
	 */
	private ImageView makeColorForField(PlaygroundField field)
	{
		ImageView colorfield = new ImageView(activity);
		ColorDrawable colorDrawable = new ColorDrawable(field.isShip() ? Color.RED : Color.WHITE);
		colorDrawable.setAlpha(50);
		colorfield.setImageDrawable(colorDrawable);
		return colorfield;
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
	 * Returns the layout params for the specified position
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @return the layout params for the specified position
	 */
	private static RelativeLayout.LayoutParams getLayoutParamsForPos(int x, int y)
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
		ships.add(ship);
		imageView.setImageResource(ship.getDrawable());
		imageView.setLayoutParams(getLayoutParamsForShip(ship));
		for (int i = 0;i < ship.getSize();i++)
		{
			if (orientation == Orientation.HORIZONTAL) 
			{
				fields[x + i][y].setShip(ship);
			}
			else
			{
				fields[x][y + i].setShip(ship);
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
	private boolean validateShipPos(int x, int y, Orientation orientation, ShipType type)
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
		Ship ship = fields[x][y].getShip();
		ships.remove(ship);
		x = ship.getX();
		y = ship.getY();
		for (int i = 0;i < ship.getSize();i++)
		{
			if (ship.getOrientation() == Orientation.HORIZONTAL)
			{
				fields[x + i][y].setShip(null);
			}
			else
			{
				fields[x][y + i].setShip(null);
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
	 * Shoot at an enemy ship
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	void shoot(int x, int y)
	{
		if (!yourturn)
		{
			return;
		}
		connection.sendShot(x, y);
		yourturn = false;
	}
	
	/**
	 * Sends a diceroll to the enemy
	 */
	void sendDiceroll()
	{
		Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(true);
		// TODO
//		builder.setOnCancelListener(onCancelListener)
		FrameLayout view = ViewFactory.createView(R.layout.progress_with_text, activity);
		TextView text = (TextView) view.findViewById(R.id.progessText);
		text.setText(R.string.waiting_for_opponent);
		builder.setView(view);
		waitingDialog = builder.show();
		connection.sendDiceroll();
	}

	@Override
	public void onGameStart(@SuppressWarnings("hiding") boolean yourturn)
	{
		waitingDialog.dismiss();
		waitingDialog = null;
		gameStarted = true;
		this.yourturn = yourturn;
		if (yourturn)
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					drawView(opponentFields);
				}
			});
		}
		final int toastText = yourturn ? R.string.yourturn : R.string.enemysturn;
		final Context context = activity;
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	@Override
	public void onShotResult(int x, int y, Result result, Ship ship)
	{
		PlaygroundField field = opponentFields[x][y];
		field.setHit(true);
		field.setIsShip(result == Result.SHIP);
		if (ship != null)
		{
			ImageView imageView = new ImageView(activity);
			imageView.setImageResource(ship.getDrawable());
			ship.setView(imageView);
			for (int i = 0;i < ship.getSize();i++)
			{
				int iX, iY;
				if (ship.getOrientation() == Orientation.HORIZONTAL)
				{
					iX = ship.getX() + i;
					iY = ship.getY();
				}
				else
				{
					iX = ship.getX();
					iY = ship.getY() + i;
				}
				opponentFields[iX][iY].setShip(ship);
			}
		}
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				drawView(opponentFields);
			}
		});
		final Activity paramActivity = activity;
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				paramActivity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						drawView(fields);
					}
				});
			}
		}, 1000);
	}

	@Override
	public void onOpponentShot(int x, int y)
	{
		yourturn = true;
		PlaygroundField field = fields[x][y];
		field.setHit(true);
		Ship ship = field.getShip();
		if (ship == null)
		{
			connection.sendResult(x, y, Result.WATER, null);
		}
		else
		{
			ship.destroyField(x, y);
			if (!ship.areAllFieldsDestroyed())
			{
				ship = null;
			}
			connection.sendResult(x, y, Result.SHIP, ship);
			boolean allShipsDestroyed = true;
			for (Ship tship : ships)
			{
				if (!tship.areAllFieldsDestroyed())
				{
					allShipsDestroyed = false;
					break;
				}
			}
			if (allShipsDestroyed)
			{
				connection.sendGamestate(false);
				SwitchToMainMenuListener switchToMainMenuListener = new SwitchToMainMenuListener();
				final Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(R.string.youlost_title);
				builder.setMessage(R.string.youlost_message);
				builder.setCancelable(true);
				builder.setOnCancelListener(switchToMainMenuListener);
				builder.setNeutralButton(R.string.ok, switchToMainMenuListener);
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						builder.show();
					}
				});
			}
		}
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				drawView(fields);
			}
		});
		final Activity paramActivity = activity;
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				paramActivity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						drawView(opponentFields);
					}
				});
			}
		}, 1000);
	}
	
	@Override
	public void onOpponentLost()
	{
		connection.sendGamestate(true);
		SwitchToMainMenuListener switchToMainMenuListener = new SwitchToMainMenuListener();
		final Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.youwon_title);
		builder.setMessage(R.string.youwon_message);
		builder.setCancelable(true);
		builder.setOnCancelListener(switchToMainMenuListener);
		builder.setNeutralButton(R.string.ok, switchToMainMenuListener);
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				builder.show();
			}
		});
	}
	
	@Override
	public void onOpponentDisconnected()
	{
		SwitchToMainMenuListener switchToMainMenuListener = new SwitchToMainMenuListener();
		final Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.opponent_quit_title);
		builder.setMessage(R.string.opponent_quit_message);
		builder.setCancelable(true);
		builder.setOnCancelListener(switchToMainMenuListener);
		builder.setNeutralButton(R.string.ok, switchToMainMenuListener);
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (waitingDialog != null) {
					waitingDialog.dismiss();
					waitingDialog = null;
				}
				builder.show();
			}
		});
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
			if (gameStarted)
			{
				shoot(x, y);
				return false;
			}
			if (fields[x][y].getShip() != null)
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
	
	/**
	 * A listener for switching to the main menu
	 *
	 * @author Manuel Vögele
	 */
	private class SwitchToMainMenuListener implements android.content.DialogInterface.OnClickListener, OnCancelListener
	{
		/**
		 * Initializes a new SwitchToMainMenuListener
		 */
		public SwitchToMainMenuListener()
		{
			// Nothing to do
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			switchToMainMenu();
		}
		
		@Override
		public void onCancel(DialogInterface dialog)
		{
			switchToMainMenu();
		}
		
		/**
		 * Switchs to the main menu
		 */
		private void switchToMainMenu()
		{
			ScreenManager.setScreen(new BuddyOverview(), R.anim.right_out, R.anim.left_in);
		}
	}
	
//	/**
//	 * Opens
//	 * 
//	 *
//	 * @author Manuel Vögele
//	 */
//	private class CancelWaitingDialogListener implements OnCancelListener
//	{
//		public CancelWaitingDialogListener()
//		{
//			// Nothing to do
//		}
//		
//		@Override
//		public void onCancel(DialogInterface dialog)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//	}
}
