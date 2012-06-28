package me.battleship.screen;

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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.view.KeyEvent;
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
public class LoginScreen implements Screen, OnClickListener, ConnectFinishedListener, OnCancelListener
{
	/**
	 * The log tag
	 */
	public static final String LOG_TAG = "LoginScreen";
	
	/**
	 * The context
	 */
	private Activity activity;
	
	/**
	 * A dialog for showing the progress
	 */
	private volatile Dialog dialog;
	
	/**
	 * The main view of this screen
	 */
	private volatile View view;
	
	/**
	 * The text field for the jabber id
	 */
	private EditText jabberid;
	
	/**
	 * The text field for the password
	 */
	private EditText password;
	
	/**
	 * The text field for the port
	 */
	private EditText port;
	
	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		view = ViewFactory.createView(R.layout.login, activity);
		jabberid = (EditText) view.findViewById(R.id.editJabberId);
		password = (EditText) view.findViewById(R.id.editPassword);
		port = (EditText) view.findViewById(R.id.editPort);
		port.setText("6667");
		Button loginButton = (Button) view.findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(this);
		return view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return false;
	}

	@Override
	public void onClick(View v)
	{
		Builder builder = new AlertDialog.Builder(activity);
		FrameLayout root = ViewFactory.createView(R.layout.progress_with_text, activity);
		TextView label = (TextView) root.findViewById(R.id.progessText);
		label.setText(R.string.logging_in);
		builder.setView(root);
		builder.setCancelable(true);
		builder.setOnCancelListener(this);
		dialog = builder.show();
		Connection connection;
		connection = new Connection(new JID(jabberid.getText().toString()), Integer.parseInt(port.getText().toString()), password.getText().toString());
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
	
	@Override
	public void onCancel(@SuppressWarnings("hiding") DialogInterface dialog)
	{
		Connection.INSTANCE.cleanup();
		Connection.INSTANCE = null;
	}
}
