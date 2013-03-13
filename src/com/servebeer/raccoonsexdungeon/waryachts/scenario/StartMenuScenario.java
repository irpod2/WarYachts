
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler.BusyType;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.TextFactory;


public class StartMenuScenario implements IScenario
{
	protected final String WAR_YACHTS = "War Yachts";
	protected BaseGameActivity activity;
	protected Scene scene;
	protected Background oceanBG;
	protected ConnectionHandler btHandler;

	protected Text warYachtsText;
	protected Text warYachtsTextBG;

	protected ButtonSprite hostGameButton;
	protected ButtonSprite findGameButton;
	protected ButtonSprite loadButton;
	protected ButtonSprite quitButton;

	public StartMenuScenario(BaseGameActivity bga, Scene scn,
			CallbackVoid loadGameCallback)
	{
		activity = bga;
		scene = scn;
		oceanBG = BackgroundFactory.createStartBackground();
		btHandler = WarYachtsActivity.getConnectionHandler();
		createButtons(loadGameCallback);
	}

	protected void createButtons(final CallbackVoid loadGameCallback)
	{
		warYachtsTextBG = TextFactory.createWarYachtsBannerTextShadow();
		warYachtsText = TextFactory.createWarYachtsBannerText();
		warYachtsTextBG.attachChild(warYachtsText);

		hostGameButton = ButtonFactory.createMenuButton("Host New Game", 2,
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

		findGameButton = ButtonFactory.createMenuButton("Find Game", 3,
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

		loadButton = ButtonFactory.createMenuButton("Continue Game", 4,
				new OnClickListener()
				{
					@Override
					public void onClick(ButtonSprite pButtonSprite,
							float pTouchAreaLocalX, float pTouchAreaLocalY)
					{
						btHandler.setAdapter();
						loadGameCallback.onCallback();
					}
				});

		quitButton = ButtonFactory.createMenuButton("Quit", 5,
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
		scene.attachChild(loadButton);
		scene.attachChild(quitButton);
		scene.attachChild(warYachtsTextBG);

	}

	@Override
	public void start()
	{
		scene.registerTouchArea(hostGameButton);
		scene.registerTouchArea(findGameButton);
		scene.registerTouchArea(loadButton);
		scene.registerTouchArea(quitButton);
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterTouchArea(hostGameButton);
		scene.unregisterTouchArea(findGameButton);
		scene.unregisterTouchArea(loadButton);
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

	@Override
	public void onNetworkNowFree()
	{}

	public void handleControlMessage(final ControlMessage ctrlMsg)
	{

	}
}
