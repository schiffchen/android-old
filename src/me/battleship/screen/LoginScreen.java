package me.battleship.screen;

import me.battleship.LoginCredentials;
import me.battleship.R;
import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.util.ViewFactory;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
/**
 * A login screen
 *
 * @author Manuel Vögele
 */
public class LoginScreen implements Screen, OnClickListener
{
	public static final String LOG_TAG = "LoginScreen";
	
	private Activity activity;
	
	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		View view = ViewFactory.createView(R.layout.login, activity);
		Button loginButton = (Button) view.findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(this);
		Button anonymousLoginButton = (Button) view.findViewById(R.id.buttonAnonymousLogin);
		anonymousLoginButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v)
	{
		Builder builder = new AlertDialog.Builder(activity);
		FrameLayout root = new FrameLayout(activity);
		ViewFactory.createView(R.layout.progress_with_text, root, activity);
		TextView label = (TextView) root.findViewById(R.id.progessText);
		label.setText(R.string.logging_in);
		builder.setView(root);
		builder.show();
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
