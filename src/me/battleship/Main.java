package me.battleship;

import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.communication.MatchmakerConnection;
import me.battleship.communication.MatchmakerConnection.OpponentAssignedListener;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * The main activity
 * 
 * @author manuel
 */
public class Main extends Activity 
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "Main";
	
	/**
	 * The connection
	 */
	private Connection connection;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "main activity started");
		setContentView(R.layout.main); 
		connection = new Connection(new JID(LoginCredentials.USERNAME), 80, LoginCredentials.PASSWORD);
		try
		{
			connection.connect();
			MatchmakerConnection matchmakerConnection = new MatchmakerConnection(connection);
			matchmakerConnection.queue(new OpponentAssignedListener()
			{
				
				@Override
				public void onOpponentAssigned(String jid, String matchId)
				{
					System.out.println("Opponent: " + jid);
					System.out.println("Matchid: " + matchId);
				}
			});
		}
		catch (XMPPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy()
	{
		connection.disconnect();
		super.onDestroy();
	}
}