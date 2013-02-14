
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;

import com.servebeer.raccoonsexdungeon.waryachts.PreferencesActivity;
import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler.BusyType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;


public class StartMenuScenario implements IScenario
{
	protected BaseGameActivity activity;
	protected Scene scene;
	protected Background oceanBG;
	protected ConnectionHandler btHandler;

	protected ButtonSprite hostGameButton;
	protected ButtonSprite findGameButton;
	protected ButtonSprite preferencesButton;
	protected ButtonSprite quitButton;

	public StartMenuScenario(BaseGameActivity bga, Scene scn)
	{
		activity = bga;
		scene = scn;
		oceanBG = BackgroundFactory.createStartBackground();
		btHandler = WarYachtsActivity.getConnectionHandler();
		createButtons();
	}

	protected void createButtons()
	{
		hostGameButton = ButtonFactory.createMenuButton("Host New Game", 1,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (btHandler.getBusyType() == BusyType.NOT_BUSY)
								{
									btHandler.requestEnableDiscovery();
								}
							}
						});
					}
				});

		findGameButton = ButtonFactory.createMenuButton("Find Game", 2,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (btHandler.getBusyType() == BusyType.NOT_BUSY)
								{
									btHandler.requestEnableBluetooth();
								}
							}
						});
					}
				});

		preferencesButton = ButtonFactory.createMenuButton("Preferences", 3,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{
						activity.startActivity(new Intent(activity,
								PreferencesActivity.class));
					}
				});

		quitButton = ButtonFactory.createMenuButton("Quit", 4,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{

						btHandler.reset();
						activity.finish();
					}
				});
	}

	@Override
	public void prepareStart()
	{
		scene.setBackground(oceanBG);
		scene.attachChild(hostGameButton);
		scene.attachChild(findGameButton);
		scene.attachChild(preferencesButton);
		scene.attachChild(quitButton);
	}

	@Override
	public void start()
	{
		scene.registerTouchArea(hostGameButton);
		scene.registerTouchArea(findGameButton);
		scene.registerTouchArea(preferencesButton);
		scene.registerTouchArea(quitButton);
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterTouchArea(hostGameButton);
		scene.unregisterTouchArea(findGameButton);
		scene.unregisterTouchArea(preferencesButton);
		scene.unregisterTouchArea(quitButton);
	}

	@Override
	public void end()
	{
		scene.detachChildren();
	}

	@Override
	public Scene getScene()
	{
		return scene;
	}

	@Override
	public boolean handleBackPress()
	{
		return false;
	}
}
