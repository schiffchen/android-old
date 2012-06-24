package me.battleship.communication;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.battleship.Orientation;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.communication.messages.BattleshipPacketExtension;
import me.battleship.communication.messages.DicerollMessage;
import me.battleship.communication.messages.ExtensionElements;
import me.battleship.communication.messages.MessageUtil;
import me.battleship.communication.messages.PingMessage;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;

/**
 * A class managing the connection to the opponent 
 *
 * @author Manuel Vögele
 */
public class OpponentConnection extends TimerTask implements MessageListener
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "OpponentConnection";
	
	/**
	 * The time between two pings
	 */
	public static final int PINGDELAY = 10000;

	/**
	 * The own diceroll
	 */
	int diceroll;

	/**
	 * The diceroll of the opponent
	 */
	int opponentDiceroll;

	/**
	 * The random used to roll the dice
	 */
	Random random;

	/**
	 * The chat to communicate with the opponent
	 */
	private Chat chat;
	
	/**
	 * The listener called when the game starts
	 */
	private OpponentConnectionListener listener;

	/**
	 * The timer
	 */
	private Timer timer;
	
	/**
	 * The timestamp when the last ping from the opponent was received
	 */
	private long lastping;

	/**
	 * Establishes a new connection
	 * @param opponentJID the jabber id of the opponent
	 */
	public OpponentConnection(String opponentJID)
	{
		diceroll = 0;
		opponentDiceroll = 0;
		random = new Random();
		Log.i(LOG_TAG, "Establishing connection to " + opponentJID);
		chat = Connection.INSTANCE.connection.getChatManager().createChat(opponentJID, this);
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, PINGDELAY);
		lastping = Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Set the OpponentConnectionListener
	 * @param listener the opponent connection listener
	 */
	public void setListener(OpponentConnectionListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void run()
	{
		try
		{
			chat.sendMessage(new PingMessage());
		}
		catch (XMPPException e)
		{
			cancel();
			timer.scheduleAtFixedRate(this, 1000, PINGDELAY);
			Log.w(LOG_TAG, "Error while sending ping. Retry in 1 second.", e);
		}
		long now = Calendar.getInstance().getTimeInMillis();
		if (now - lastping > PINGDELAY * 2)
		{
			cancel();
			listener.onOpponentDisconnected();
		}
	}

	/**
	 * Sends a diceroll to the opponent
	 */
	public void sendDiceroll()
	{
		diceroll = random.nextInt(5) + 1;
		Log.i(LOG_TAG, "Sending diceroll: " + diceroll);
		try
		{
			chat.sendMessage(new DicerollMessage(diceroll));
			checkDicerolls();
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An error occured while sending the diceroll", e);
		}
	}

	/**
	 * Compares the own diceroll and the opponents diceroll if both players have already rolled the dice
	 */
	private void checkDicerolls()
	{
		if (diceroll != 0 && opponentDiceroll != 0)
		{
			Log.i(LOG_TAG, "Comparing rolls. Own: " + diceroll + " Opponent: " + opponentDiceroll);
			if (diceroll == opponentDiceroll)
			{
				Log.i(LOG_TAG, "Rerolling...");
				opponentDiceroll = 0;
				diceroll = 0;
				sendDiceroll();
				return;
			}
			listener.onGameStart(diceroll > opponentDiceroll);
		}
	}
	
	/**
	 * Sends a shoot to the opponent
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	public void sendShot(int x, int y)
	{
		try
		{
			chat.sendMessage(new ShootMessage(x, y));
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An error occured while sending a shot to the opponenet.", e);
		}
	}
	
	/**
	 * Sends a shot result to the opponent
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @param result
	 *           the result
	 * @param ship
	 *           the ship that was sunk or <code>null</code> if no ship was sunk
	 */
	public void sendResult(int x, int y, Result result, Ship ship)
	{
		try
		{
			chat.sendMessage(new ShootMessage(x, y, result, ship));
		}
		catch (XMPPException e)
		{
			Log.e(LOG_TAG, "An error occured while sending a shot to the opponent.", e);
		}
	}

	@Override
	public void processMessage(@SuppressWarnings("hiding") Chat chat, Message message)
	{
		BattleshipPacketExtension extension = MessageUtil.getPacketExtension(message, ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension dicerollExtension = extension.getSubElement(ExtensionElements.DICEROLL);
		BattleshipPacketExtension shoot = extension.getSubElement(ExtensionElements.SHOOT);
		BattleshipPacketExtension ping = extension.getSubElement(ExtensionElements.PING);
		if (dicerollExtension != null)
		{
			Map<String, String> attributes = dicerollExtension.getAttributes();
			opponentDiceroll = Integer.parseInt(attributes.get("dice"));
			Log.i(LOG_TAG, "Received diceroll from opponent: " + opponentDiceroll);
			checkDicerolls();
		}
		else if (shoot != null)
		{
			Map<String, String> attributes = shoot.getAttributes();
			int x = Integer.parseInt(attributes.get("x"));
			int y = Integer.parseInt(attributes.get("y"));
			if (attributes.containsKey("result"))
			{
				Result result = Result.getResultForString(attributes.get("result"));
				BattleshipPacketExtension shipExtension = extension.getSubElement(ExtensionElements.SHIP);
				Ship ship = null;
				if (shipExtension != null)
				{
					attributes = shipExtension.getAttributes();
					ShipType type = Ship.getTypeForSize(Integer.parseInt(attributes.get("size")));
					int sx = Integer.parseInt(attributes.get("x"));
					int sy = Integer.parseInt(attributes.get("y"));
					String sOrientation = attributes.get("attributes");
					Orientation orientation;
					if ("horizontal".equals(sOrientation))
					{
						orientation = Orientation.HORIZONTAL;
					}
					else
					{
						orientation = Orientation.VERTICAL;
					}
					ship = new Ship(type, sx, sy, orientation, null);
				}
				listener.onShotResult(x, y, result, ship);
			}
			else
			{
				listener.onOpponentShot(x, y);
			}
		}
		else if (ping != null)
		{
			Log.i(LOG_TAG, "Received ping from opponent");
		}
		else
		{
			Log.w(LOG_TAG, "Unexpected message:\n" + message.toXML());
		}
		lastping = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * A listener called when both players have placed their ships and the game can start 
	 *
	 * @author Manuel Vögele
	 */
	public interface OpponentConnectionListener
	{
		/**
		 * Indicates that both players have placed their ships and the game can start.
		 * 
		 * @param yourturn whether you have the first turn
		 */
		public void onGameStart(boolean yourturn);
		
		/**
		 * This event will be fired when the enemy sent the result of your last shot
		 * 
		 * @param x the x position of the shot
		 * @param y the y position of the shot
		 * @param result the result
		 * @param ship the ship if it was sunken, <code>null</code> otherwise
		 */
		public void onShotResult(int x, int y, Result result, Ship ship);
		
		/**
		 * This event will be fired when the opponent shot
		 * 
		 * @param x the x position
		 * @param y the y position
		 */
		public void onOpponentShot(int x, int y);
		
		/**
		 * This event will be fired when the opponent disconnected
		 */
		public void onOpponentDisconnected();
	}
}
