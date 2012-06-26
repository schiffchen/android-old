package me.battleship.screen;

import java.util.ArrayList;
import java.util.List;

import me.battleship.R;
import me.battleship.communication.Connection;
import me.battleship.communication.MatchmakerConnection;
import me.battleship.communication.MatchmakerConnection.OpponentAssignedListener;
import me.battleship.communication.OpponentConnection;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A screen offering to play against a buddy or a random opponent or to log out 
 *
 * @author Manuel Vögele
 */
public class BuddyOverview implements Screen, OnClickListener
{
	@Override
	public View getView(Activity activity)
	{
		View view = ViewFactory.createView(R.layout.buddy_overview, activity);
		ListView list = (ListView) view.findViewById(R.id.buddyList);
		list.setAdapter(new BuddyListAdapter(activity));
		Button logoutButton = (Button) view.findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v)
	{
		Connection.INSTANCE.disconnect();
		ScreenManager.setScreen(new LoginScreen(), R.anim.right_out, R.anim.left_in);
	}
	
	/**
	 * The adapter for collecting data for the buddy list 
	 *
	 * @author Manuel Vögele
	 */
	private class BuddyListAdapter extends BaseAdapter implements OnClickListener, OpponentAssignedListener
	{
		/**
		 * A list containing the users buddys
		 */
		private List<String> buddys;
		
		/**
		 * The context
		 */
		private final Activity activity;
		
		/**
		 * The id of the view containing the selection for 'random opponent'
		 */
		int randomOpponentId;
		
		/**
		 * The dialog showing the progress
		 */
		Dialog dialog;

		private MatchmakerConnection matchmakerConnection;
		
		/**
		 * Instantiates a new BuddyListAdapter
		 * @param activity the context
		 */
		public BuddyListAdapter(Activity activity)
		{
			this.activity = activity;
			buddys = new ArrayList<String>();
		}
		
		@Override
		public int getCount()
		{
			return buddys.size() + 1;
		}

		@Override
		public String getItem(int position)
		{
			if (position == 0)
			{
				return null;
			}
			return buddys.get(position + 1);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TextView text;
			if (convertView instanceof TextView)
			{
				 text = (TextView) convertView;
			}
			else
			{
				text = new TextView(activity);
			}
			if (position == 0)
			{
				text.setTypeface(Typeface.DEFAULT_BOLD);
				text.setText(R.string.random_opponent);
				randomOpponentId = text.getId();
			}
			else
			{
				text.setTypeface(Typeface.DEFAULT);
				text.setText(buddys.get(position + 1));
			}
			text.setPadding(0, 15, 0, 15);
			text.setOnClickListener(this);
			return text;
		}

		@Override
		public void onClick(View v)
		{
			if (v.getId() == randomOpponentId)
			{
				Builder builder = new Builder(activity);
				FrameLayout root = ViewFactory.createView(R.layout.progress_with_text, activity);
				TextView label = (TextView) root.findViewById(R.id.progessText);
				label.setText(R.string.searching_for_opponent);
				builder.setView(root);
				builder.setCancelable(false);
				dialog = builder.show();
				matchmakerConnection = new MatchmakerConnection();
				matchmakerConnection.queue(this);
			}
			else
			{
				startGame(((TextView) v).getText().toString());
			}
		}

		@Override
		public void onOpponentAssigned(String jid, String matchId)
		{
			startGame(jid);
		}
		
		/**
		 * Starts a game against the specified opponent
		 * @param opponentJID the jabber id of the opponent
		 */
		public void startGame(String opponentJID)
		{
			Game game = new Game(new OpponentConnection(opponentJID, matchmakerConnection));
			matchmakerConnection = null;
			dialog.dismiss();
			dialog = null;
			ScreenManager.setScreen(game, R.anim.left_out, R.anim.right_in);
		}
	}
}
