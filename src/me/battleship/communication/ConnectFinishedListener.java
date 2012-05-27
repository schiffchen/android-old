package me.battleship.communication;

import org.jivesoftware.smack.XMPPException;

/**
 * A listener called either when the connection was successfull or an error occured while connecting 
 *
 * @author Manuel Vögele
 */
public interface ConnectFinishedListener
{
	/**
	 * Called when the connection was successful or an error occurred while connecting.
	 * e will be null if connecting was successful. If an error occurred e will contain the exception.
	 * @param e the exception if one was caught or null if connecting was successful
	 */
	public void onConnectFinished(XMPPException e);
}
