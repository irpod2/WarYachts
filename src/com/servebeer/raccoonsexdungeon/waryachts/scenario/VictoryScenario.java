
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.engine.handler.IUpdateHandler;
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

public class VictoryScenario implements IScenario, IUpdateHandler
{
	private final String VICTORY_MESSAGE_LINE1 = "You Win!";
	private final String VICTORY_MESSAGE_LINE2 = "GOOD JOB!";

	private Scene scene;
	private Text victoryTextLine1;
	private Text victoryTextBackgroundLine1;
	private Text victoryTextLine2;
	private Text victoryTextBackgroundLine2;
	private CallbackVoid onBackPressCallback;
	private ConnectionHandler btHandler;
	private float totalTimeElapsed = 0.0f;

	public VictoryScenario(Scene scn, CallbackVoid onBackPressCB,
			ConnectionHandler concHandler)
	{
		scene = scn;
		onBackPressCallback = onBackPressCB;
		btHandler = concHandler;
		scene.setBackground(new Background(0.0f, 0.4f, 0.8f));

		// Line 1
		victoryTextLine1 = TextFactory.createText(-2.0f, -2.0f,
				VICTORY_MESSAGE_LINE1, 1.0f);
		victoryTextLine1.setColor(Color.WHITE);
		victoryTextBackgroundLine1 = TextFactory
				.createSimpleText(VICTORY_MESSAGE_LINE1);
		victoryTextBackgroundLine1.setColor(Color.BLACK);
		victoryTextBackgroundLine1.attachChild(victoryTextLine1);
		victoryTextBackgroundLine1.setPosition(
				WarYachtsActivity.getCameraWidth() / 2.0f
						- victoryTextBackgroundLine1.getWidth() / 2.0f,
				WarYachtsActivity.getCameraHeight() / 2.0f
						- victoryTextBackgroundLine1.getHeight() * 1.5f);

		// Line 2
		victoryTextLine2 = TextFactory.createText(-2.0f, -2.0f,
				VICTORY_MESSAGE_LINE2, 1.0f);
		victoryTextLine2.setColor(Color.WHITE);
		victoryTextBackgroundLine2 = TextFactory
				.createSimpleText(VICTORY_MESSAGE_LINE2);
		victoryTextBackgroundLine2.setColor(Color.BLACK);
		victoryTextBackgroundLine2.attachChild(victoryTextLine2);
		victoryTextBackgroundLine2.setPosition(
				WarYachtsActivity.getCameraWidth() / 2.0f
						- victoryTextBackgroundLine2.getWidth() / 2.0f,
				WarYachtsActivity.getCameraHeight() / 2.0f
						+ victoryTextBackgroundLine2.getHeight() * 0.5f);
	}

	@Override
	public void prepareStart()
	{
		scene.attachChild(victoryTextBackgroundLine1);
		scene.attachChild(victoryTextBackgroundLine2);
	}

	@Override
	public void start()
	{
		scene.registerUpdateHandler(this);
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterUpdateHandler(this);
	}

	@Override
	public void end()
	{
		scene.detachChild(victoryTextBackgroundLine1);
		scene.detachChild(victoryTextBackgroundLine2);
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

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		totalTimeElapsed += pSecondsElapsed;
		victoryTextLine1.setColor(new Color(0.5f + 0.5f * (float) Math.sin(2
				* Math.PI * totalTimeElapsed / 3.0f),
				0.5f + 0.5f * (float) Math.sin(2 * Math.PI * totalTimeElapsed
						/ 4.0f), 0.5f + 0.5f * (float) Math.sin(2 * Math.PI
						* totalTimeElapsed / 5.0f)));
		victoryTextLine2.setColor(new Color(0.5f + 0.5f * (float) Math.sin(2
				* Math.PI * totalTimeElapsed / 3.0f),
				0.5f + 0.5f * (float) Math.sin(2 * Math.PI * totalTimeElapsed
						/ 4.0f), 0.5f + 0.5f * (float) Math.sin(2 * Math.PI
						* totalTimeElapsed / 5.0f)));
	}

	@Override
	public void reset()
	{}

}
