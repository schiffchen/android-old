package me.battleship.screen;

import me.battleship.LoginCredentials;
import me.battleship.R;
import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.util.ViewFactory;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
/**
 * A login screen
 *
 * @author Manuel Vögele
 */
public class LoginScreen implements Screen, OnClickListener
{
	public static final String LOG_TAG = "LoginScreen";
	
	@Override
	public View getView(final Activity activity)
	{
		FrameLayout root = new FrameLayout(activity);
		ViewFactory.<View>createView(R.layout.login, root, activity);
		Button loginButton = (Button) root.findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(this);
		Button anonymousLoginButton = (Button) root.findViewById(R.id.buttonAnonymousLogin);
		anonymousLoginButton.setOnClickListener(this);
		return root;
	}

	@Override
	public void onClick(View v)
	{
		
		Connection connection;
		if (v.getId() == R.id.buttonLogin)
		{
			connection = new Connection(new JID(LoginCredentials.USERNAME), 80, LoginCredentials.PASSWORD);
		}
		else
		{
			connection = new Connection();
		}
		try
		{
			connection.connect();
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An exception occured while logging in", e);
			connection.disconnect();
		}
	}
}
