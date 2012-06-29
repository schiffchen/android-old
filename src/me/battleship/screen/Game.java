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
import me.battleship.Playground;
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
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	 * A list containing the ships which have to be placed on the playground
	 */
	List<ShipType> shipsToPlace;
	
	/**
	 * The currently visible red views
	 */
	Set<View> redViews;
	
	/**
	 * The playground of the player
	 */
	Playground playerPlayground;
	
	/**
	 * The playground for the opponent
	 */
	Playground opponentPlayground;
	
	/**
	 * The root view
	 */
	private View root;

	/**
	 * The grid to place the ships in
	 */
	RelativeLayout playgroundView;

	/**
	 * The activity
	 */
	private Activity activity;

	/**
	 * The connection to the opponent
	 */
	final OpponentConnection connection;

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
	 * The sound pool
	 */
	private SoundPool soundPool;

	/**
	 * The id of the splash sound
	 */
	private int splashSound;

	/**
	 * The id of the explosion sound
	 */
	private int explosionSound;

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
		shipsToPlace.add(ShipType.SUBMARINE);
		shipsToPlace.add(ShipType.DESTROYER);
		playerPlayground = new Playground();
		opponentPlayground = new Playground();
	}

	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		splashSound = soundPool.load(activity, R.raw.splash, 1);
		explosionSound = soundPool.load(activity, R.raw.explosion, 1);
		root = ViewFactory.createView(R.layout.game, activity);
		playgroundView = (RelativeLayout) root.findViewById(R.id.playgroundGrid);
		playerPlayground.drawView(playgroundView, new FieldTouchListener());
		refreshPreview();
		return root;
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
		if (!playerPlayground.validateShipPos(x, y, orientation, type))
		{
			HashSet<View> views = new HashSet<View>();
			for (int i = 0;i < Ship.getSizeForType(type);i++)
			{
				boolean horizontal = orientation == Orientation.HORIZONTAL;
				if (horizontal && !Playground.isPosOnPlaygroud(x + i, y))
				{
					continue;
				}
				if (!horizontal && !Playground.isPosOnPlaygroud(x, y + i))
				{
					continue;
				}
				int viewId = Playground.getViewId((horizontal ? x + i : x), (horizontal ? y : y + i));
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
		imageView.setLayoutParams(Ship.getLayoutParamsForShip(ship));
		for (int i = 0;i < ship.getSize();i++)
		{
			if (orientation == Orientation.HORIZONTAL) 
			{
				playerPlayground.getField(x + i, y).setShip(ship);
			}
			else
			{
				playerPlayground.getField(x, y + i).setShip(ship);
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
			LayoutParams layoutParams = Playground.getLayoutParamsForPos(Playground.getXFromId(id), Playground.getYFromId(id));
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
	 * Removes a ship from the playground
	 * @param x the x position of the ship
	 * @param y the y position of the ship
	 */
	void removeShip(int x, int y)
	{
		markFieldsRed(null);
		Ship ship = playerPlayground.getField(x, y).getShip();
		ships.remove(ship);
		x = ship.getX();
		y = ship.getY();
		for (int i = 0;i < ship.getSize();i++)
		{
			if (ship.getOrientation() == Orientation.HORIZONTAL)
			{
				playerPlayground.getField(x + i, y).setShip(null);
			}
			else
			{
				playerPlayground.getField(x, y + i).setShip(null);
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
		builder.setOnCancelListener(new ReallyQuitListener(activity));
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
					opponentPlayground.drawView(playgroundView, new FieldTouchListener());
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
		PlaygroundField field = opponentPlayground.getField(x, y);
		field.setHit(true);
		boolean isShip = (result == Result.SHIP);
		field.setIsShip(isShip);
		if (isShip)
		{
			soundPool.play(explosionSound, 1, 1, 0, 0, 1);
		}
		else
		{
			soundPool.play(splashSound, 1, 1, 0, 0, 1);
		}
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
				opponentPlayground.getField(iX, iY).setShip(ship);
			}
		}
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				opponentPlayground.drawView(playgroundView, new FieldTouchListener());
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
						playerPlayground.drawView(playgroundView, new FieldTouchListener());
					}
				});
			}
		}, 1000);
	}

	@Override
	public void onOpponentShot(int x, int y)
	{
		yourturn = true;
		PlaygroundField field = playerPlayground.getField(x, y);
		field.setHit(true);
		Ship ship = field.getShip();
		if (ship == null)
		{
			soundPool.play(splashSound, 1, 1, 0, 0, 1);
			connection.sendResult(x, y, Result.WATER, null);
		}
		else
		{
			soundPool.play(explosionSound, 1, 1, 0, 0, 1);
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
				connection.cleanup();
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
				playerPlayground.drawView(playgroundView, new FieldTouchListener());
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
						opponentPlayground.drawView(playgroundView, new FieldTouchListener());
					}
				});
			}
		}, 1000);
	}
	
	@Override
	public void onOpponentLost()
	{
		connection.sendGamestate(true);
		connection.cleanup();
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
				builder.show();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			new ReallyQuitListener(activity).onCancel(null);
			return true;
		}
		return false;
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
			int x = Playground.getXFromId(view.getId());
			int y = Playground.getYFromId(view.getId());
			if (gameStarted)
			{
				shoot(x, y);
				return false;
			}
			if (playerPlayground.getField(x, y).getShip() != null)
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
	private class SwitchToMainMenuListener implements DialogInterface.OnClickListener, OnCancelListener
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
			if (waitingDialog != null) {
				waitingDialog.dismiss();
				waitingDialog = null;
			}
			connection.cleanup();
			ScreenManager.setScreen(new BuddyOverview(), R.anim.right_out, R.anim.left_in);
		}
	}
	
	/**
	 * Opens a dialog asking whether the opponent really wants to quit the game
	 * 
	 *
	 * @author Manuel Vögele
	 */
	private class ReallyQuitListener implements OnCancelListener
	{
		/**
		 * The context
		 */
		private final Context context;

		/**
		 * Initializes a new really quit listener
		 * @param context the context
		 */
		public ReallyQuitListener(Context context)
		{
			this.context = context;
		}
		
		@Override
		public void onCancel(DialogInterface dialog)
		{
			Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.really_leave_game_title);
			builder.setMessage(R.string.really_leave_game_message);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.yes, new SwitchToMainMenuListener());
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(@SuppressWarnings("hiding") DialogInterface dialog, int which)
				{
					if (waitingDialog != null) 
					{
						waitingDialog.show();
					}
				}
			});
			builder.show();
		}
	}
}