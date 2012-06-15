package me.battleship.communication;

import java.util.Map;
import java.util.Random;

import me.battleship.communication.messages.BattleshipPacketExtension;
import me.battleship.communication.messages.DicerollMessage;
import me.battleship.communication.messages.ExtensionElements;
import me.battleship.communication.messages.MessageUtil;

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
public class OpponentConnection implements MessageListener
{
	/**
	 * The tag for the logger
	 */
	public static final String LOG_TAG = "OpponentConnection";

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
	private GameStartListener listener;

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
	}

	/**
	 * Sends a diceroll to the opponent
	 * @param listener the listener that will be called when the game starts
	 */
	public void sendDiceroll(@SuppressWarnings("hiding") GameStartListener listener)
	{
		this.listener = listener;
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
				sendDiceroll(listener);
				return;
			}
			listener.onGameStart(diceroll > opponentDiceroll);
			listener = null;
		}
	}

	@Override
	public void processMessage(@SuppressWarnings("hiding") Chat chat, Message message)
	{
		BattleshipPacketExtension extension = MessageUtil.getPacketExtension(message, ExtensionElements.BATTLESHIP);
		BattleshipPacketExtension dicerollExtension = extension.getSubElement(ExtensionElements.DICEROLL);
		if (dicerollExtension != null)
		{
			Map<String, String> attributes = dicerollExtension.getAttributes();
			opponentDiceroll = Integer.parseInt(attributes.get("dice"));
			Log.i(LOG_TAG, "Received diceroll from opponent: " + opponentDiceroll);
			checkDicerolls();
		}
		else
		{
			Log.w(LOG_TAG, "Unexpected message:\n" + message.toXML());
		}
	}

	/**
	 * A listener called when both players have placed their ships and the game can start 
	 *
	 * @author Manuel Vögele
	 */
	public interface GameStartListener
	{
		/**
		 * Indicates that both players have placed their ships and the game can start.
		 * 
		 * @param yourturn whether you have the first turn
		 */
		public void onGameStart(boolean yourturn);
	}
}
