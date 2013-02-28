
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.ui.activity.BaseGameActivity;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Battlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public class GameInstanceScenario implements IScenario
{
	protected final int USER_FIELD = 0;
	protected final int WAR_FIELD = 1;
	protected final int CHAT_FIELD = 2;

	protected BaseGameActivity activity;
	protected Scene scene;
	protected ArrayList<Entity> layers;
	protected CallbackVoid onBackCallback;
	protected ButtonSprite button;
	protected boolean ready;
	protected ConnectionHandler btHandler;
	protected Battlefield userBattlefield;
	protected Battlefield enemyBattlefield;


	public GameInstanceScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid onBackCB, ConnectionHandler conHandler)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		ready = false;
		btHandler = conHandler;
		userBattlefield = new Battlefield();
		enemyBattlefield = new Battlefield();
		userBattlefield.addYacht(YachtFactory.createSubYacht(3, 4,
				Orientation.HORIZONTAL));
		userBattlefield.addYacht(YachtFactory.createWarYacht(6, 5,
				Orientation.VERTICAL));
		userBattlefield.addYacht(YachtFactory.createDestroyerYacht(0, 0,
				Orientation.HORIZONTAL));
		userBattlefield.addYacht(YachtFactory.createCarrierYacht(1, 2,
				Orientation.HORIZONTAL));
		userBattlefield.addYacht(YachtFactory.createSkunkerYacht(2, 4,
				Orientation.HORIZONTAL));

		// Create layers for scene and attach them
		layers = new ArrayList<Entity>();
		Entity userField = new Entity();
		layers.add(userField);
		Entity warField = new Entity(WarYachtsActivity.getCameraWidth(), 0);
		layers.add(warField);
		Entity chatField = new Entity(WarYachtsActivity.getCameraWidth() * 2, 0);
		layers.add(chatField);
		scene.attachChild(layers.get(USER_FIELD));
		scene.attachChild(layers.get(WAR_FIELD));
		scene.attachChild(layers.get(CHAT_FIELD));

		button = ButtonFactory.createButton(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				ControlMessage msg = ControlMessage.createShootMessage(
						userBattlefield.getSelectedRow(),
						userBattlefield.getSelectedCol());
				button.setEnabled(false);
				btHandler.sendMsg(msg);
			}
		});
		button.setY(WarYachtsActivity.getCameraHeight()
				- button.getHeightScaled());
		button.setX(WarYachtsActivity.getCameraWidth() / 2.0f
				- button.getWidthScaled() / 2.0f);
		button.setEnabled(false);

		Background bg = BackgroundFactory.createStartBackground();
		scene.setBackground(bg);
	}

	public boolean isReady()
	{
		return ready;
	}

	public void onNetworkNowFree()
	{
		button.setEnabled(true);
	}

	@Override
	public void prepareStart()
	{
		// User Battlefield
		layers.get(USER_FIELD).attachChild(button);
		layers.get(USER_FIELD).attachChild(userBattlefield.getSprite());

		// War Battlefield
		layers.get(WAR_FIELD).attachChild(enemyBattlefield.getSprite());

		// Chat (no chat stuff now)
	}

	@Override
	public void start()
	{
		// Don't want the button clickable til' we're all the way in
		scene.registerTouchArea(button);
		scene.registerTouchArea(userBattlefield);
		ready = true;
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterTouchArea(button);
		scene.unregisterTouchArea(userBattlefield);
	}

	@Override
	public void end()
	{
		// Get rid of all attached objects
		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				for (Entity layer : layers)
				{
					layer.detachChildren();
				}
				// Must detach layers or they won't go away
				scene.detachChildren();
			}
		});
	}

	@Override
	public Scene getScene()
	{
		return scene;
	}

	@Override
	public boolean handleBackPress()
	{
		onBackCallback.onCallback();
		return true;
	}
}
