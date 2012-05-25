package me.battleship.communication;


import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * A class for handling connections
 *
 * @author Manuel Vögele
 */
public class Connection
{
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
	 * @param password the password
	 */
	public Connection(JID jid, String password)
	{
		this.jid = jid;
		connection = new XMPPConnection(jid.getDomain());
		this.password = password;
	}
	
	/**
	 * Establishes a connection
	 * 
	 * @throws XMPPException if an error occurs while connecting
	 */
	public void connect() throws XMPPException
	{
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
