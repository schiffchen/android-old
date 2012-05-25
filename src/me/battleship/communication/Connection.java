package me.battleship.communication;


import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import android.util.Log;

/**
 * A class for handling connections
 *
 * @author Manuel Vögele
 */
public class Connection
{
	public static final String LOG_TAG = "Connection";
	/**
	 * The connection
	 */
	private XMPPConnection connection;
	
	/**
	 * The Jabber id
	 */
	private JID jid;
	
	/**
	 * The password
	 */
	private String password;

	/**
	 * Creates a new connection which is set to login anonymously
	 */
	public Connection()
	{
		// TODO: Implement anonymous login
		throw new UnsupportedOperationException("Connection() is not implemented yet");
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
		this.jid = jid;
		connection = new XMPPConnection(new ConnectionConfiguration(jid.getDomain(), port));
		this.password = password;
	}
	
	/**
	 * Establishes a connection
	 * 
	 * @throws XMPPException if an error occurs while connecting
	 */
	public void connect() throws XMPPException
	{
		Log.i(LOG_TAG, "Connecting to " + connection.getHost() + ":" + connection.getPort());
		connection.connect();
		String resource = (jid.getResource() != null ? jid.getResource() : "battleshipme");
		connection.login(jid.getNode(), password, resource);
		connection.sendPacket(new Presence(Type.available, "ready", -128, Mode.available));
	}
	
	/**
	 * Disconnects from the server
	 */
	public void disconnect()
	{
		Log.i(LOG_TAG, "Disconnecting from " + connection.getHost() + ":" + connection.getPort());
		connection.disconnect();
	}
	
	/**
	 * Returns the chat manager for the current connection
	 * @return the chat manager
	 */
	public ChatManager getChatManager()
	{
		return connection.getChatManager();
	}
}
