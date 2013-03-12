
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

import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.EnemyBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.PlacementMenu;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.UserBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.GameState;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.SaveService;
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
	protected ButtonSprite fireButton;
	protected boolean ready;
	protected boolean hosting;
	protected ConnectionHandler btHandler;
	protected UserBattlefield userBattlefield;
	protected EnemyBattlefield enemyBattlefield;
	protected BattlefieldSwipeHandler swipeHandler;
	protected PlacementMenu placementMenu;
	protected GameState gameState;
	private float prevX;


	public GameInstanceScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid onBackCB, ConnectionHandler conHandler, boolean host,
			boolean loading)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		ready = false;
		hosting = host;
		btHandler = conHandler;

		if (loading)
		{
			gameState = SaveService.getInstance(activity).load();

			if (gameState == null)
			{
				gameState = new GameState(host);
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(
								activity,
								"Could not find saved game. Creating new game as host."
										+ gameState.getOppMac(),
								Toast.LENGTH_SHORT).show();
					}
				});
				hosting = true;
				loading = false;
			}
			else
			{
				BluetoothDevice dev = btHandler.getAdapter().getRemoteDevice(
						gameState.getOppMac());
				btHandler.setDevice(dev);
				// Loaded games don't need to place: initialized to ready
				ready = true;
				// Loaded games DO need to explicitly call listen for new
				// messages
				btHandler.listen();

				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(
								activity,
								"Set bluetooth device to one with MAC "
										+ gameState.getOppMac(),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
		else
		{
			gameState = new GameState(host);
		}


		// Create layers for scene and attach them
		layers = new ArrayList<Entity>();
		Entity userField = new Entity();
		layers.add(userField);
		Entity warField = new Entity(WarYachtsActivity.getCameraWidth(), 0);
		layers.add(warField);
		scene.attachChild(layers.get(USER_FIELD));
		scene.attachChild(layers.get(ENEMY_FIELD));

		userBattlefield = new UserBattlefield(gameState);
		enemyBattlefield = new EnemyBattlefield(gameState);

		fireButton = ButtonFactory.createButton(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (gameState.getMyTurn())
				{
					ControlMessage msg = ControlMessage.createShootMessage(
							enemyBattlefield.getSelectedRow(),
							enemyBattlefield.getSelectedCol(),
							new CallbackVoid()
							{
								@Override
								public void onCallback()
								{
									gameState.updateTurn(true);
									fireButton.setEnabled(true);
								}
							});
					gameState.updateTurn(false);
					fireButton.setEnabled(false);
					btHandler.sendMsg(msg);
				}
			}
		});
		fireButton.setY(WarYachtsActivity.getCameraHeight()
				- fireButton.getHeightScaled());
		fireButton.setX(WarYachtsActivity.getCameraWidth() / 2.0f
				- fireButton.getWidthScaled() / 2.0f);

		if (loading)
		{
			fireButton.setEnabled(gameState.getMyTurn());
			placementMenu = null;
		}
		else
		{
			fireButton.setEnabled(false);
			placementMenu = new PlacementMenu(layers.get(USER_FIELD),
					userBattlefield, onReadyCallback);
		}

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

		// fireButton.setEnabled(true);
	}

	@Override
	public void prepareStart()
	{
		// User Battlefield
		layers.get(USER_FIELD).attachChild(userBattlefield.getSprite());
		if (placementMenu != null)
			placementMenu.attachSprites();

		// War Battlefield
		layers.get(ENEMY_FIELD).attachChild(fireButton);
		layers.get(ENEMY_FIELD).attachChild(enemyBattlefield.getSprite());

		// Chat (no chat stuff now)
	}

	@Override
	public void start()
	{
		// Don't want the fireButton clickable til' we're all the way in
		scene.registerTouchArea(fireButton);
		if (placementMenu != null)
		{
			placementMenu.registerButton(scene);
			scene.setOnSceneTouchListener(placementMenu);
		}
		else
		{
			scene.registerTouchArea(enemyBattlefield);
			scene.setOnSceneTouchListener(GameInstanceScenario.this);
			scene.registerUpdateHandler(swipeHandler);
		}
	}

	private void onShipsPlaced()
	{
		gameState.updateMyYachts(placementMenu.getPlacedYachts());

		activity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				placementMenu.onSuccess();
			}
		});
		scene.registerTouchArea(enemyBattlefield);
		scene.setOnSceneTouchListener(GameInstanceScenario.this);
		scene.registerUpdateHandler(swipeHandler);

		SaveService.getInstance(activity).save(gameState);
	}

	private CallbackVoid onReadyCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			if (!hosting)
			{
				gameState.updateMac(btHandler.getOppMac());
				placementMenu.setReadyButtonEnabled(false);
				activity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						btHandler.sendMsg(ControlMessage
								.createReadyMessage(enableReadyButtonCallback));
					}
				});
			}
			else
			{
				onShipsPlaced();
			}
		}
	};

	private CallbackVoid enableReadyButtonCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			placementMenu.setReadyButtonEnabled(true);
		}
	};

	@Override
	public void prepareEnd()
	{
		scene.setOnSceneTouchListener(null);
		scene.unregisterTouchArea(fireButton);
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
			if (!ready)
			{
				ready = true;
				gameState.updateMac(btHandler.getOppMac());
				respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
				btHandler.sendMsg(respMsg);
				fireButton.setEnabled(gameState.getMyTurn());
			}
			else
			{
				respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
				btHandler.sendMsg(respMsg);
			}
			break;
		case DESTROYED:
			gameState.updateTurn(false);
			fireButton.setEnabled(false);
			enemyBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			YachtInfo yi = ctrlMsg.getYachtInfo();
			Yacht destroyedYacht = null;
			switch (yi.yachtType)
			{
			case HEL_CAR:
				destroyedYacht = YachtFactory.createCarrierYacht(yi);
				break;
			case OLD_REL:
				destroyedYacht = YachtFactory.createDestroyerYacht(yi);
				break;
			case WAR_YAT:
				destroyedYacht = YachtFactory.createWarYacht(yi);
				break;
			case POSI:
				destroyedYacht = YachtFactory.createSubYacht(yi);
				break;
			case SKUNK:
				destroyedYacht = YachtFactory.createSkunkerYacht(yi);
				break;
			}
			final Yacht y = destroyedYacht;
			activity.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					enemyBattlefield.addYacht(y);
					gameState.addOppYacht(y.getInfo());

					// VICTORY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					if (enemyBattlefield.defeatedAllEnemies())
					{
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(activity, "YOU WIN!",
										Toast.LENGTH_LONG).show();
							}
						});
						handleBackPress();
					}
					else
					{
						SaveService.getInstance(activity).save(gameState);
					}
				}
			});
			break;
		case HIT:
			gameState.updateTurn(false);
			fireButton.setEnabled(false);
			enemyBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			SaveService.getInstance(activity).save(gameState);
			break;
		case MISS:
			gameState.updateTurn(false);
			fireButton.setEnabled(false);
			enemyBattlefield.miss(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			SaveService.getInstance(activity).save(gameState);
			break;
		case SHOOT:

			if (!ready)
			{
				ready = true;
				onShipsPlaced();
			}

			// If hit, send hit message. Otherwise, send miss message
			YachtInfo info = userBattlefield.shoot(ctrlMsg.getRow(),
					ctrlMsg.getCol());
			if (info == null)
			{
				respMsg = ControlMessage.createMissMessage(ctrlMsg.getRow(),
						ctrlMsg.getCol());
			}
			else
			{
				info.numHits--;
				if (info.numHits > 0)
					respMsg = ControlMessage.createHitMessage(ctrlMsg.getRow(),
							ctrlMsg.getCol());
				else
				{
					info.numHits = 0;
					respMsg = ControlMessage.createDestroyedMessage(
							ctrlMsg.getRow(), ctrlMsg.getCol(), info);
				}
			}
			btHandler.sendMsg(respMsg);
			break;
		case ACK:
			char firstChar = ctrlMsg.getMessage().charAt(2);
			if (firstChar == 'M')
			{
				userBattlefield.miss(ctrlMsg.getRow(), ctrlMsg.getCol());
				gameState.updateTurn(true);
				fireButton.setEnabled(true);
				SaveService.getInstance(activity).save(gameState);
			}
			else if (firstChar == 'H')
			{
				userBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
				gameState.updateTurn(true);
				fireButton.setEnabled(true);
				SaveService.getInstance(activity).save(gameState);
			}
			else if (firstChar == 'D')
			{
				userBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
				gameState.updateTurn(true);
				fireButton.setEnabled(true);
				if (userBattlefield.isDefeated())
				{
					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							Toast.makeText(activity, "YOU LOOOOSEEE!",
									Toast.LENGTH_LONG).show();
						}
					});
					handleBackPress();
				}
				else
					SaveService.getInstance(activity).save(gameState);
			}
			else if (firstChar == 'R')
			{
				if (!ready)
				{
					ready = true;
					onShipsPlaced();
					gameState.updateTurn(false);
				}
				ready = true;
			}

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
