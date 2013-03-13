
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.EnemyBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.PlacementMenu;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.UserBattlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Carrier;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Destroyer;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Skunker;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.SubYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.WarYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.YachtType;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.GameState;
import com.servebeer.raccoonsexdungeon.waryachts.gamestate.SaveService;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.BattlefieldSwipeHandler;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.TextFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.YachtFactory;

public class GameInstanceScenario implements IScenario, IOnSceneTouchListener
{
	protected final int USER_FIELD = 0;
	protected final int ENEMY_FIELD = 1;

	protected BaseGameActivity activity;
	protected Scene scene;
	protected ArrayList<Entity> layers;
	protected CallbackVoid onBackCallback;
	protected CallbackVoid onVictoryCallback;
	protected CallbackVoid onDefeatCallback;
	protected ButtonSprite fireButton;
	protected boolean ready;
	protected boolean hosting;
	protected ConnectionHandler btHandler;
	protected UserBattlefield userBattlefield;
	protected EnemyBattlefield enemyBattlefield;
	protected BattlefieldSwipeHandler swipeHandler;
	protected PlacementMenu placementMenu;
	protected GameState gameState;
	protected Text selectedText;
	protected Text selectedTextBG;
	private float prevX;

	public GameInstanceScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid onBackCB, CallbackVoid onVictoryCB,
			CallbackVoid onDefeatCB, ConnectionHandler conHandler,
			boolean host, boolean loading)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		onVictoryCallback = onVictoryCB;
		onDefeatCallback = onDefeatCB;
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
				final BluetoothDevice dev = btHandler.getAdapter()
						.getRemoteDevice(gameState.getOppMac());
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
						Toast.makeText(activity,
								"Resuming game with " + dev.getName(),
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
		enemyBattlefield = new EnemyBattlefield(gameState, this);

		// Text indicating location of targeter
		selectedTextBG = TextFactory.createSimpleText("");
		selectedText = TextFactory.createText(-2.0f, -2.0f, "", 1.0f);
		selectedTextBG.attachChild(selectedText);
		selectedTextBG.setColor(Color.BLACK);
		selectedText.setColor(Color.WHITE);

		fireButton = ButtonFactory.createFireButton(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (gameState.getMyTurn() && enemyBattlefield.isTargeterSet())
				{
					gameState.updateTurn(false);
					fireButton.setEnabled(false);
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

									activity.runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{
											Toast.makeText(
													activity,
													"Could not connect to opponent. Try again later",
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
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

	public void setTargeter(int row, int col)
	{
		String text = "Selected: " + (char) (col + 'A')
				+ String.valueOf(row + 1);
		selectedText.setText(text);
		selectedTextBG.setText(text);
		selectedTextBG.setX(WarYachtsActivity.getCameraWidth() / 2.0f
				- selectedTextBG.getWidth() / 2.0f);
		selectedTextBG.setY(fireButton.getY() - selectedTextBG.getHeight()
				- 5.0f);
		if (!selectedTextBG.hasParent())
			layers.get(ENEMY_FIELD).attachChild(selectedTextBG);
	}

	public void unsetTargeter()
	{
		if (selectedTextBG.hasParent())
			layers.get(ENEMY_FIELD).detachChild(selectedTextBG);
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
		layers.get(ENEMY_FIELD).attachChild(selectedTextBG);

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
				btHandler.sendMsg(ControlMessage
						.createReadyMessage(enableReadyButtonCallback));
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

			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity,
							"Could not connect to opponent. Try again later",
							Toast.LENGTH_SHORT).show();
				}
			});
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
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(activity,
								"The enemy is ready! Fire at will!",
								Toast.LENGTH_SHORT).show();
					}
				});
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
					gameState.updateSunkShips(y.getInfo().yachtType);
					enemyBattlefield.addYacht(y);
					gameState.addOppYacht(y.getInfo());

					// VICTORY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					if (gameState.isVictory())
					{
						onVictoryCallback.onCallback();
					}
					else
					{
						SaveService.getInstance(activity).save(gameState);
					}
				}
			});
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (y.getType() == YachtType.OLD_REL)
						Toast.makeText(activity, "You sunk Old Reliable!",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(activity,
								"You sunk their " + y.getShortName() + "!",
								Toast.LENGTH_SHORT).show();
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
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity, "Hit!", Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case MISS:
			gameState.updateTurn(false);
			fireButton.setEnabled(false);
			enemyBattlefield.miss(ctrlMsg.getRow(), ctrlMsg.getCol());
			respMsg = ControlMessage.createAckMessage(ctrlMsg.getMessage());
			btHandler.sendMsg(respMsg);
			SaveService.getInstance(activity).save(gameState);
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity, "Miss!", Toast.LENGTH_SHORT)
							.show();
				}
			});
			break;
		case SHOOT:
			if (!ready)
			{
				ready = true;
				onShipsPlaced();
			}
			
			int row = ctrlMsg.getRow();
			int col = ctrlMsg.getCol();

			// If hit, send hit message. Otherwise, send miss message
			YachtInfo info = userBattlefield.shoot(row,
					col);
			if (info == null)
			{
				respMsg = ControlMessage.createMissMessage(row,
						col);
			}
			else
			{
				if (!info.hasBeenHit(row, col))
				{
					info.registerHit(row, col);
					info.numHits--;
				}
				if (info.numHits > 0)
					respMsg = ControlMessage.createHitMessage(row,
							col);
				else
				{
					info.numHits = 0;
					respMsg = ControlMessage.createDestroyedMessage(
							row, col, info);
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
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(activity, "Enemy Miss!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			else if (firstChar == 'H')
			{
				userBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
				gameState.updateTurn(true);
				fireButton.setEnabled(true);
				SaveService.getInstance(activity).save(gameState);
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(activity, "Enemy Hit!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			else if (firstChar == 'D')
			{
				userBattlefield.hit(ctrlMsg.getRow(), ctrlMsg.getCol());
				gameState.updateTurn(true);
				fireButton.setEnabled(true);
				if (userBattlefield.isDefeated())
				{
					onDefeatCallback.onCallback();
				}
				else
				{
					String ctrlStr = ctrlMsg.getMessage();
					int delimiter = ctrlStr.indexOf('|');
					final YachtType yachtType = YachtType.valueOf(ctrlStr
							.substring(8, delimiter));
					SaveService.getInstance(activity).save(gameState);
					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							String dispStr = "";
							switch (yachtType)
							{
							case HEL_CAR:
								dispStr = "your " + Carrier.SHORT_NAME;
								break;
							case OLD_REL:
								dispStr = Destroyer.SHORT_NAME;
								break;
							case WAR_YAT:
								dispStr = "your " + WarYacht.SHORT_NAME;
								break;
							case POSI:
								dispStr = "your " + SubYacht.SHORT_NAME;
								break;
							case SKUNK:
								dispStr = "your " + Skunker.SHORT_NAME;
								break;
							}
							Toast.makeText(activity,
									"They sunk " + dispStr + "!",
									Toast.LENGTH_SHORT).show();

						}
					});
				}
			}
			else if (firstChar == 'R')
			{
				if (!ready)
				{
					ready = true;
					onShipsPlaced();
					gameState.updateTurn(false);
					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							Toast.makeText(
									activity,
									"The Game has begun! Wait for your turn to fire.",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
				ready = true;
			}
			break;
		default:
			break;
		}
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
