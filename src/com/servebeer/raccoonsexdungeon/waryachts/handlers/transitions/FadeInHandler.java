
package com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.scenario.IScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class FadeInHandler implements ITransitionHandler, IUpdateHandler
{
	protected BaseGameActivity activity;
	protected IScenario scenario;
	protected Rectangle fadeBox;
	protected HUD hud;
	protected CallbackVoid onFinishedCallback;
	protected float totalTimeElapsed;

	public FadeInHandler(BaseGameActivity bga, Camera cam, IScenario scno,
			CallbackVoid onFinishedCB)
	{
		activity = bga;
		scenario = scno;
		onFinishedCallback = onFinishedCB;
		hud = cam.getHUD();
		fadeBox = new Rectangle(0, 0, cam.getWidth(), cam.getHeight(),
				activity.getVertexBufferObjectManager());
		fadeBox.setColor(Color.BLACK);
		fadeBox.setAlpha(1.0f);
		hud.attachChild(fadeBox);
		totalTimeElapsed = 0;
		scenario.getScene().registerUpdateHandler(this);
		scenario.prepareStart();
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		totalTimeElapsed += pSecondsElapsed;
		if (totalTimeElapsed < FADE_TIME)
		{
			fadeBox.setAlpha(1.0f - (totalTimeElapsed / FADE_TIME));
		}
		else
		{
			fadeBox.setAlpha(0.0f);
			hud.detachChild(fadeBox);
			scenario.getScene().unregisterUpdateHandler(this);
			scenario.start();
			onFinishedCallback.onCallback();
		}

	}

	@Override
	public void reset()
	{
		if (fadeBox != null)
		{
			fadeBox.setAlpha(1.0f);
			totalTimeElapsed = 0;
		}
		else if (onFinishedCallback != null)
		{
			onFinishedCallback.onCallback();
		}
	}

}
