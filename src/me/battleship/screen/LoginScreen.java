package me.battleship.screen;

import me.battleship.R;
import me.battleship.communication.ConnectFinishedListener;
import me.battleship.communication.Connection;
import me.battleship.communication.JID;
import me.battleship.communication.JID.JIDFormatException;
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
	 * The default port
	 */
	private static final int DEFAULT_PORT = 5222;

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
	private EditText jabberidInput;
	
	/**
	 * The text field for the password
	 */
	private EditText passwordInput;
	
	/**
	 * The text field for the port
	 */
	private EditText portInput;
	
	@Override
	public View getView(@SuppressWarnings("hiding") Activity activity)
	{
		this.activity = activity;
		view = ViewFactory.createView(R.layout.login, activity);
		jabberidInput = (EditText) view.findViewById(R.id.editJabberId);
		passwordInput = (EditText) view.findViewById(R.id.editPassword);
		portInput = (EditText) view.findViewById(R.id.editPort);
		portInput.setText(String.valueOf(DEFAULT_PORT));
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
		try
		{
			String portstr = portInput.getText().toString();
			int port = (portstr.equals("") ? DEFAULT_PORT : Integer.parseInt(portstr));
			Connection connection = new Connection(new JID(jabberidInput.getText().toString()), port, passwordInput.getText().toString());
			connection.connect(this);
			dialog = builder.show();
		}
		catch (JIDFormatException e)
		{
			builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.invalid_jid_title);
			builder.setMessage(R.string.invalid_jid_message);
			builder.setCancelable(true);
			builder.setNeutralButton(R.string.ok, null);
			builder.show();
		}
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
			Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.login_failed_title);
			builder.setMessage(R.string.login_failed_message);
			builder.setCancelable(true);
			builder.setNeutralButton(R.string.ok, null);
			builder.show();
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
