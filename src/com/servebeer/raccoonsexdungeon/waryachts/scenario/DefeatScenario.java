
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage.ControlType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.TextFactory;

public class DefeatScenario implements IScenario
{
	private final String DEFEAT_MESSAGE_LINE1 = "You Lose!";
	private final String DEFEAT_MESSAGE_LINE2 = "I AM";
	private final String DEFEAT_MESSAGE_LINE3 = "SO SORRY!";

	private Scene scene;
	private Text defeatTextLine1;
	private Text defeatTextBackgroundLine1;
	private Text defeatTextLine2;
	private Text defeatTextBackgroundLine2;
	private Text defeatTextLine3;
	private Text defeatTextBackgroundLine3;
	private CallbackVoid onBackPressCallback;
	private ConnectionHandler btHandler;

	public DefeatScenario(Scene scn, CallbackVoid onBackPressCB,
			ConnectionHandler concHandler)
	{
		scene = scn;
		onBackPressCallback = onBackPressCB;
		btHandler = concHandler;
		scene.setBackground(new Background(0.0f, 0.4f, 0.8f));

		// Line 1
		defeatTextLine1 = TextFactory.createText(-2.0f, -2.0f,
				DEFEAT_MESSAGE_LINE1, 1.0f);
		defeatTextLine1.setColor(Color.WHITE);
		defeatTextBackgroundLine1 = TextFactory
				.createSimpleText(DEFEAT_MESSAGE_LINE1);
		defeatTextBackgroundLine1.setColor(Color.BLACK);
		defeatTextBackgroundLine1.attachChild(defeatTextLine1);
		defeatTextBackgroundLine1.setPosition(
				WarYachtsActivity.getCameraWidth() / 2.0f
						- defeatTextBackgroundLine1.getWidth() / 2.0f,
				WarYachtsActivity.getCameraHeight() / 2.0f
						- defeatTextBackgroundLine1.getHeight() * 2.0f);

		// Line 2
		defeatTextLine2 = TextFactory.createText(-2.0f, -2.0f,
				DEFEAT_MESSAGE_LINE2, 1.0f);
		defeatTextLine2.setColor(Color.WHITE);
		defeatTextBackgroundLine2 = TextFactory
				.createSimpleText(DEFEAT_MESSAGE_LINE2);
		defeatTextBackgroundLine2.setColor(Color.BLACK);
		defeatTextBackgroundLine2.attachChild(defeatTextLine2);
		defeatTextBackgroundLine2.setPosition(
				WarYachtsActivity.getCameraWidth() / 2.0f
						- defeatTextBackgroundLine2.getWidth() / 2.0f,
				WarYachtsActivity.getCameraHeight() / 2.0f
						- defeatTextBackgroundLine2.getHeight() * 0.5f);

		// Line 3
		defeatTextLine3 = TextFactory.createText(-2.0f, -2.0f,
				DEFEAT_MESSAGE_LINE3, 1.0f);
		defeatTextLine3.setColor(Color.WHITE);
		defeatTextBackgroundLine3 = TextFactory
				.createSimpleText(DEFEAT_MESSAGE_LINE3);
		defeatTextBackgroundLine3.setColor(Color.BLACK);
		defeatTextBackgroundLine3.attachChild(defeatTextLine3);
		defeatTextBackgroundLine3.setPosition(
				WarYachtsActivity.getCameraWidth() / 2.0f
						- defeatTextBackgroundLine3.getWidth() / 2.0f,
				WarYachtsActivity.getCameraHeight() / 2.0f
						+ defeatTextBackgroundLine3.getHeight());
	}

	@Override
	public void prepareStart()
	{
		scene.attachChild(defeatTextBackgroundLine1);
		scene.attachChild(defeatTextBackgroundLine2);
		scene.attachChild(defeatTextBackgroundLine3);
	}

	@Override
	public void start()
	{}

	@Override
	public void prepareEnd()
	{}

	@Override
	public void end()
	{
		scene.detachChild(defeatTextBackgroundLine1);
		scene.detachChild(defeatTextBackgroundLine2);
		scene.detachChild(defeatTextBackgroundLine3);
	}

	@Override
	public Scene getScene()
	{
		return scene;
	}

	@Override
	public boolean handleBackPress()
	{
		onBackPressCallback.onCallback();
		return true;
	}

	@Override
	public void handleControlMessage(ControlMessage ctrlMsg)
	{
		if (ctrlMsg.getType() == ControlType.SHOOT)
		{
			ControlMessage respMsg = ControlMessage.createMissMessage(
					ctrlMsg.getRow(), ctrlMsg.getCol());
			btHandler.sendMsg(respMsg);
		}
		else if (ctrlMsg.getType() != ControlType.ACK)
		{
			ControlMessage respMsg = ControlMessage.createAckMessage(ctrlMsg
					.getMessage());
			btHandler.sendMsg(respMsg);
		}
	}

	@Override
	public void onNetworkNowFree()
	{

	}
}
