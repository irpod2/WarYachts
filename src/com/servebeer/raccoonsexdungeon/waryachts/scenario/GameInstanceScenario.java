
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;

import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.EnemyBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.UserBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.BattlefieldSwipeHandler;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public class GameInstanceScenario implements IScenario, IOnSceneTouchListener
{
	protected final int USER_FIELD = 0;
	protected final int ENEMY_FIELD = 1;

	protected BaseGameActivity activity;
	protected Scene scene;
	protected ArrayList<Entity> layers;
	protected CallbackVoid onBackCallback;
	protected ButtonSprite button;
	protected boolean ready;
	protected ConnectionHandler btHandler;
	protected UserBattlefield userBattlefield;
	protected EnemyBattlefield enemyBattlefield;
	protected BattlefieldSwipeHandler swipeHandler;
	private float prevX;


	public GameInstanceScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid onBackCB, ConnectionHandler conHandler)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		ready = false;
		btHandler = conHandler;
		userBattlefield = new UserBattlefield();
		enemyBattlefield = new EnemyBattlefield();
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
		scene.attachChild(layers.get(USER_FIELD));
		scene.attachChild(layers.get(ENEMY_FIELD));

		button = ButtonFactory.createButton(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				ControlMessage msg = ControlMessage.createShootMessage(
						enemyBattlefield.getSelectedRow(),
						enemyBattlefield.getSelectedCol());
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
		swipeHandler = new BattlefieldSwipeHandler(layers.get(0), layers.get(1));
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
		layers.get(USER_FIELD).attachChild(userBattlefield.getSprite());

		// War Battlefield
		layers.get(ENEMY_FIELD).attachChild(button);
		layers.get(ENEMY_FIELD).attachChild(enemyBattlefield.getSprite());

		// Chat (no chat stuff now)
	}

	@Override
	public void start()
	{
		// Don't want the button clickable til' we're all the way in
		scene.registerTouchArea(button);
		scene.registerTouchArea(userBattlefield);
		scene.registerTouchArea(enemyBattlefield);
		scene.setOnSceneTouchListener(this);
		scene.registerUpdateHandler(swipeHandler);
		ready = true;
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterTouchArea(button);
		scene.unregisterTouchArea(userBattlefield);
		scene.unregisterTouchArea(enemyBattlefield);
		scene.unregisterUpdateHandler(swipeHandler);
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

	public void handleControlMessage(final ControlMessage ctrlMsg)
	{
		ControlMessage respMsg = null;
		String msgType = "E:";
		switch (ctrlMsg.getType())
		{
		case CHAT:
			msgType = "C:";
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			break;
		case HIT:
			msgType = "H:";
			enemyBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			break;
		case MISS:
			msgType = "M:";
			enemyBattlefield.miss(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			break;
		case SHOOT:
			msgType = "S:";
			// If hit, send hit message. Otherwise, send miss message
			if (userBattlefield.shoot(ctrlMsg.getRow(), ctrlMsg.getCol()))
				respMsg = ControlMessage.createHitMessage(ctrlMsg.getRow(),
						ctrlMsg.getCol());
			else
				respMsg = ControlMessage.createMissMessage(ctrlMsg.getRow(),
						ctrlMsg.getCol());
			btHandler.sendMsg(respMsg);
			break;
		default:
			msgType = "D:";
			break;
		}
		final String mt = msgType;
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(activity, mt + "(" + ctrlMsg.getMessage() + ")",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if(pSceneTouchEvent.isActionDown())
		{
			prevX = pSceneTouchEvent.getX();
			swipeHandler.setEnabled(false);
		}
		else if(pSceneTouchEvent.isActionMove())
		{
			swipeHandler.moveByOffset(pSceneTouchEvent.getX() - prevX);
			prevX = pSceneTouchEvent.getX();
		}
		else if(pSceneTouchEvent.isActionUp())
		{
			swipeHandler.moveByOffset(pSceneTouchEvent.getX() - prevX);
			swipeHandler.setEnabled(true);
		}
		return true;
	}
}
