package me.battleship.screen;

import me.battleship.LoginCredentials;
import me.battleship.R;
import me.battleship.communication.ConnectFinishedListener;
import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.util.ViewFactory;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
/**
 * A login screen
 *
 * @author Manuel Vögele
 */
public class LoginScreen implements Screen, OnClickListener, ConnectFinishedListener
{
	public static final String LOG_TAG = "LoginScreen";
	
	private Activity activity;
	
	private volatile Dialog dialog;
	
	private volatile View view;
	
	private EditText jabberid;
	
	private EditText password;
	
	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		view = ViewFactory.createView(R.layout.login, activity);
		jabberid = (EditText) view.findViewById(R.id.editJabberId);
		jabberid.setText(LoginCredentials.USERNAME);
		password = (EditText) view.findViewById(R.id.editPassword);
		password.setText(LoginCredentials.PASSWORD);
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
		FrameLayout root = ViewFactory.createView(R.layout.progress_with_text, activity);
		TextView label = (TextView) root.findViewById(R.id.progessText);
		label.setText(R.string.logging_in);
		builder.setView(root);
		builder.setCancelable(false);
		dialog = builder.show();
		Connection connection;
		if (v.getId() == R.id.buttonLogin)
		{
			connection = new Connection(new JID(jabberid.getText().toString()), 80, password.getText().toString());
		}
		else
		{
			connection = new Connection();
		}
		connection.connect(this);
	}

	@Override
	public void onConnectFinished(XMPPException e)
	{
		dialog.dismiss();
		dialog = null;
		if (e == null)
		{
			ScreenManager.setScreen(new BuddyOverview(), R.anim.left_out, R.anim.right_in);
		}
		else
		{
			Log.e(LOG_TAG, "Error while logging in", e);
			Connection.INSTANCE.disconnect();
			String errorMessage;
			if (e.getMessage().contains("authentication failed"))
			{
				errorMessage = activity.getString(R.string.authentication_failed);
			}
			else
			{
				errorMessage = e.getWrappedThrowable().getMessage();
			}
			TextView textView = (TextView) view.findViewById(R.id.errorMessage);
			textView.setText(errorMessage);
			return;
		}
	}
}
