package me.battleship.screen;

import java.util.ArrayList;
import java.util.List;

import me.battleship.R;
import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.communication.JID.JIDFormatException;
import me.battleship.communication.MatchmakerConnection;
import me.battleship.communication.MatchmakerConnection.OpponentAssignedListener;
import me.battleship.communication.OpponentConnection;
import me.battleship.util.ViewFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		onClick(null);
		return true;
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
		 * The id of the view containing the selection for 'direct connect'
		 */
		private int directConnectId;
		
		/**
		 * The dialog showing the progress
		 */
		Dialog dialog;

		/**
		 * The connection to the matchmaker
		 */
		MatchmakerConnection matchmakerConnection;
		
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
			return buddys.size() + 2;
		}

		@Override
		public String getItem(int position)
		{
			if (position == 0)
			{
				return null;
			}
			return buddys.get(position + 2);
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
				randomOpponentId = 0;
				text.setId(randomOpponentId);
			}
			else if (position == 1)
			{
				text.setTypeface(Typeface.DEFAULT_BOLD);
				text.setText(R.string.direct_connect);
				directConnectId = 1;
				text.setId(directConnectId);
			}
			else
			{
				text.setTypeface(Typeface.DEFAULT);
				text.setText(buddys.get(position + 2));
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
				builder.setCancelable(true);
				builder.setOnCancelListener(new CancelSearchListener());
				dialog = builder.show();
				matchmakerConnection = new MatchmakerConnection();
				matchmakerConnection.queue(this);
			}
			else if (v.getId() == directConnectId)
			{
				final Context context = activity;
				final EditText editText = new EditText(activity);
				editText.setHint(R.string.jabberid);
				editText.requestFocus();
				Builder builder = new Builder(activity);
				builder.setTitle(R.string.direct_connect);
				builder.setView(editText);
				builder.setCancelable(true);
				builder.setNegativeButton(R.string.cancel, null);
				builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int which)
					{
						String jidstring = editText.getText().toString();
						try
						{
							JID opponentJID = new JID(jidstring);
							startGame(opponentJID.getId());
						}
						catch (JIDFormatException e)
						{
							@SuppressWarnings("hiding")
							Builder builder = new AlertDialog.Builder(context);
							builder.setTitle(R.string.invalid_jid_title);
							builder.setMessage(R.string.invalid_jid_message);
							builder.setCancelable(true);
							builder.setOnCancelListener(new OnCancelListener()
							{
								@Override
								@SuppressWarnings("hiding")
								public void onCancel(DialogInterface dialogInterface)
								{
									dialog.show();
								}
							});
							builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
							{
								@Override
								@SuppressWarnings("hiding")
								public void onClick(DialogInterface dialogInterface, int which)
								{
									dialog.show();
								}
							});
							builder.show();
						}
					}
				});
				dialog = builder.show();
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
		
		/**
		 * Listener for canceling the queueing on the matchmaker
		 *
		 * @author Manuel Vögele
		 */
		private class CancelSearchListener implements OnCancelListener
		{
			/**
			 * Instantiates a new cancel search listener
			 */
			public CancelSearchListener()
			{
				// Nothing to do
			}
			
			@Override
			public void onCancel(@SuppressWarnings("hiding") DialogInterface dialog)
			{
				matchmakerConnection.cleanup();
				matchmakerConnection = null;
			}
		}
	}
}
