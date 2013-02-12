
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.ui.activity.BaseGameActivity;

import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ButtonFactory;

public class GameInstanceScenario implements IScenario
{
	protected BaseGameActivity activity;
	protected Scene scene;
	protected CallbackVoid onBackCallback;
	protected ButtonSprite button;
	protected boolean ready;

	public GameInstanceScenario(BaseGameActivity bga, Scene scn, CallbackVoid onBackCB)
	{
		activity = bga;
		scene = scn;
		onBackCallback = onBackCB;
		ready = false;

		button = ButtonFactory.createButton(new OnClickListener()
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
						Toast.makeText(activity, "Clicked the button!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		Background bg = BackgroundFactory.createStartBackground();
		scene.setBackground(bg);
	}

	public boolean isReady()
	{
		return ready;
	}

	@Override
	public void prepareStart()
	{
		scene.attachChild(button);
	}

	@Override
	public void start()
	{
		// Don't want the button clickable til' we're all the way in
		scene.registerTouchArea(button);
		ready = true;
	}

	@Override
	public void prepareEnd()
	{
		scene.unregisterTouchArea(button);
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
		onBackCallback.onCallback();
		return true;
	}
}
