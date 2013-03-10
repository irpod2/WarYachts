
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
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.PlacementMenu;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.UserBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.BattlefieldSwipeHandler;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;

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
	protected boolean hosting;
	protected boolean myTurn;
	protected ConnectionHandler btHandler;
	protected UserBattlefield userBattlefield;
	protected EnemyBattlefield enemyBattlefield;
	protected BattlefieldSwipeHandler swipeHandler;
	protected PlacementMenu placementMenu;
	private float prevX;


	public GameInstanceScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid onBackCB, ConnectionHandler conHandler, boolean host)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		ready = false;
		myTurn = host;
		hosting = host;
		btHandler = conHandler;

		// Create layers for scene and attach them
		layers = new ArrayList<Entity>();
		Entity userField = new Entity();
		layers.add(userField);
		Entity warField = new Entity(WarYachtsActivity.getCameraWidth(), 0);
		layers.add(warField);
		scene.attachChild(layers.get(USER_FIELD));
		scene.attachChild(layers.get(ENEMY_FIELD));

		userBattlefield = new UserBattlefield();
		enemyBattlefield = new EnemyBattlefield();
		placementMenu = new PlacementMenu(layers.get(USER_FIELD),
				userBattlefield, onShipsPlacedCallback);

		button = ButtonFactory.createButton(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (myTurn)
				{
					ControlMessage msg = ControlMessage.createShootMessage(
							enemyBattlefield.getSelectedRow(),
							enemyBattlefield.getSelectedCol());
					myTurn = false;
					button.setEnabled(false);
					btHandler.sendMsg(msg);
				}
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

		// button.setEnabled(true);
	}

	@Override
	public void prepareStart()
	{
		// User Battlefield
		layers.get(USER_FIELD).attachChild(userBattlefield.getSprite());
		placementMenu.attachSprites();

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
		placementMenu.registerButton(scene);
		scene.setOnSceneTouchListener(placementMenu);
		ready = true;
	}

	private CallbackVoid onShipsPlacedCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			scene.registerTouchArea(enemyBattlefield);
			scene.setOnSceneTouchListener(GameInstanceScenario.this);
			scene.registerUpdateHandler(swipeHandler);
			if (!hosting)
			{
				activity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						btHandler.sendMsg(ControlMessage.createReadyMessage());
					}
				});
			}
		}
	};

	@Override
	public void prepareEnd()
	{
		scene.setOnSceneTouchListener(null);
		scene.unregisterTouchArea(button);
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
		switch (ctrlMsg.getType())
		{
		case READY:
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			button.setEnabled(myTurn);
			break;
		case HIT:
			enemyBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			break;
		case MISS:
			enemyBattlefield.miss(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			break;
		case SHOOT:
			// If hit, send hit message. Otherwise, send miss message
			if (userBattlefield.shoot(ctrlMsg.getRow(), ctrlMsg.getCol()))
				respMsg = ControlMessage.createHitMessage(ctrlMsg.getRow(),
						ctrlMsg.getCol());
			else
				respMsg = ControlMessage.createMissMessage(ctrlMsg.getRow(),
						ctrlMsg.getCol());
			btHandler.sendMsg(respMsg);
			break;
		case ACK:
			char firstChar = ctrlMsg.getMessage().charAt(2);
			if (firstChar == 'M' || firstChar == 'H')
			{
				myTurn = true;
				button.setEnabled(true);
			}
			/*
			 * else if(firstChar == 'R') { myTurn = false; }
			 */
			break;
		default:
			break;
		}
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(activity, "(" + ctrlMsg.getMessage() + ")",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if (pSceneTouchEvent.isActionDown())
		{
			prevX = pSceneTouchEvent.getX();
			swipeHandler.setEnabled(false);
		}
		else if (pSceneTouchEvent.isActionMove())
		{
			swipeHandler.moveByOffset(pSceneTouchEvent.getX() - prevX);
			prevX = pSceneTouchEvent.getX();
		}
		else if (pSceneTouchEvent.isActionUp())
		{
			swipeHandler.moveByOffset(pSceneTouchEvent.getX() - prevX);
			swipeHandler.setEnabled(true);
		}
		return true;
	}
}
