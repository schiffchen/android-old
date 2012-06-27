package me.battleship.communication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import me.battleship.util.StringUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import android.os.AsyncTask;
import android.util.Log;

/**
 * A class for handling connections
 *
 * @author Manuel Vögele
 */
public class Connection
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "Connection";
	
	/**
	 * The only instance of connection
	 */
	public static Connection INSTANCE;
	
	/**
	 * The connection
	 */
	XMPPConnection connection;
	
	/**
	 * The Jabber id
	 */
	JID jid;
	
	/**
	 * The port
	 */
	int port;
	
	/**
	 * The password
	 */
	String password;

	AsyncTask<ConnectFinishedListener, Void, XMPPException> connectTask;

	/**
	 * Creates a new connection which is set to login anonymously
	 */
	public Connection()
	{
		// Nothing to do
	}
	
	/**
	 * Creates a new connection using the specified login credentials
	 * 
	 * @param jid the jabber id
	 * @param port the port to connect to
	 * @param password the password
	 */
	public Connection(JID jid, int port, String password)
	{
		fillInstance();
		this.jid = jid;
		this.port = port;
		this.password = password;
	}
	
	/**
	 * This will assign the current instance to INSTANCE and closes the old one
	 */
	private void fillInstance()
	{
		if (INSTANCE != null)
		{
			INSTANCE.disconnect();
		}
		INSTANCE = this;
	}
	
	/**
	 * Establishes a connection
	 * 
	 * @param connectFinishedListener The listener called when operation is complete - if null nothing will be done and errors will be logged
	 */
	public void connect(ConnectFinishedListener connectFinishedListener)
	{
		connectTask = new AsyncTask<ConnectFinishedListener, Void, XMPPException>()
		{
			/**
			 * The listener that will be used after connecting
			 */
			private ConnectFinishedListener listener;
			
			@Override
			protected XMPPException doInBackground(ConnectFinishedListener... params)
			{
				if (jid == null)
				{
					readAnonymousLoginData();
				}
				connection = new XMPPConnection(new ConnectionConfiguration(jid.getDomain(), port));
				this.listener = params[0];
				try
				{
					Log.i(LOG_TAG, "Connecting to " + connection.getHost() + ":" + connection.getPort());
					connection.connect();
					String resource = (jid.getResource() != null ? jid.getResource() : "battleshipme");
					connection.login(jid.getNode(), password, resource);
					connection.sendPacket(new Presence(Type.available, "ready", -128, Mode.available));
					return null;
				}
				catch (XMPPException e)
				{
					return e;
				}
			}
			
			@Override
			protected void onPostExecute(XMPPException result)
			{
				connectTask = null;
				if (listener != null)
				{
					listener.onConnectFinished(result);
				}
				else if (result != null)
				{
					Log.e(LOG_TAG, "Error while logging in", result);
				}
			}
		};
		connectTask.execute(connectFinishedListener);
	}
	
	/**
	 * Reads the login data for anonymous login
	 */
	void readAnonymousLoginData()
	{
		try
		{
			URL url = new URL("http://battleship.me/anonlogin.json");
			URLConnection urlConnection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = reader.readLine();
			reader.close();
			jid = new JID(StringUtils.readAttributeFromJson(line, "jid") + "/" + StringUtils.readAttributeFromJson(line, "resource"));
			port = 5222;
			password = StringUtils.readAttributeFromJson(line, "password");
		}
		catch (MalformedURLException e)
		{
			Log.e(LOG_TAG, "An error occured while connecting anonymously", e);
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "An error occured while connecting anonymously", e);
		}
	}
	
	/**
	 * Closes all background processes. The instance will be unusable after that.
	 */
	public void cleanup()
	{
		if (connectTask != null)
		{
			connectTask.cancel(true);
			connectTask = null;
		}
	}
	
	/**
	 * Disconnects from the server
	 */
	public void disconnect()
	{
		if (connection != null)
		{
			Log.i(LOG_TAG, "Disconnecting from " + connection.getHost() + ":" + connection.getPort());
			connection.disconnect();
		}
	}
}