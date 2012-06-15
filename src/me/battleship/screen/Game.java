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

public class Game implements Screen, GameStartListener
{
	public static final int SIZE = 10;

	private List<ShipType> shipsToPlace;

	private View root;

	private RelativeLayout playgroundView;

	private Activity activity;

	private final OpponentConnection connection;

	public Game(OpponentConnection connection)
	{
		this.connection = connection;
		List<ShipType> ships = Arrays.asList(ShipType.AIRCRAFT_CARRIER, ShipType.BATTLESHIP, ShipType.SUBMARINE, ShipType.SUBMARINE, ShipType.DESTROYER);
		shipsToPlace = new ArrayList<ShipType>(ships);
	}

	@Override
	public View getView(Activity activity)
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

	private int getViewId(int x, int y)
	{
		return y * SIZE + x + 1;
	}

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

	private RelativeLayout.LayoutParams getShipPositionLayout(Ship ship)
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
	
	void placeShip(int x, int y) {
		Iterator<ShipType> iterator = shipsToPlace.iterator();
		ShipType type = iterator.next();
		iterator.remove();
		previewNext();
		Ship ship = new Ship(type, x, y, Orientation.VERTICAL);
		ImageView imageView = new ImageView(activity);
		imageView.setImageResource(ship.getDrawable());
		imageView.setLayoutParams(getShipPositionLayout(ship));
		playgroundView.addView(imageView);
	}

	@Override
	public void onGameStart(boolean yourturn)
	{
		System.out.println("Game started. " + yourturn);
	}

	private class FieldClickListener implements OnClickListener
	{
		private final int x;
		private final int y;

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
