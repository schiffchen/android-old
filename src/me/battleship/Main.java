package me.battleship;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.os.Bundle;

/**
 * The main activity
 * 
 * @author manuel
 */
public class Main extends Activity
{
	private XMPPConnection connection; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main);
		ConnectionConfiguration config = new ConnectionConfiguration("battleship.me");
		
		connection = new XMPPConnection(config);
		try
		{
			connection.connect();
			System.out.println("Connected - yay");
			connection.login(LoginCredentials.USERNAME, LoginCredentials.PASSWORD, "battleshipme");
			Chat chat = connection.getChatManager().createChat("manuel@battleship.me", new MessageListener()
			{
				
				@Override
				public void processMessage(Chat arg0, Message arg1)
				{
					System.out.println("Incoming message: "+arg1);
				}
			});
			chat.sendMessage("Blaaaah");
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