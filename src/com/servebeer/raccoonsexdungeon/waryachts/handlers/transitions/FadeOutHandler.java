
package com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.IScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class FadeOutHandler implements ITransitionHandler, IUpdateHandler
{
	protected BaseGameActivity activity;
	protected IScenario scenario;
	protected HUD hud;
	protected Rectangle fadeBox;
	protected CallbackVoid onFinishedCallback;
	protected float totalTimeElapsed;

	public FadeOutHandler(BaseGameActivity bga, Camera cam,
			IScenario iScenario, CallbackVoid onFinishedCB)
	{
		activity = bga;
		scenario = iScenario;
		onFinishedCallback = onFinishedCB;
		hud = cam.getHUD();
		fadeBox = new Rectangle(0, 0, WarYachtsActivity.getCameraWidth(),
				WarYachtsActivity.getCameraHeight(),
				activity.getVertexBufferObjectManager());
		fadeBox.setColor(Color.BLACK);
		fadeBox.setAlpha(0.0f);
		hud.attachChild(fadeBox);
		totalTimeElapsed = 0;
		scenario.prepareEnd();
		scenario.getScene().registerUpdateHandler(this);
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		totalTimeElapsed += pSecondsElapsed;
		if (totalTimeElapsed < FADE_TIME)
		{
			fadeBox.setAlpha(totalTimeElapsed / FADE_TIME);
		}
		else
		{
			scenario.getScene().unregisterUpdateHandler(this);
			fadeBox.setAlpha(1.0f);
			scenario.end();
			onFinishedCallback.onCallback();
			hud.detachChild(fadeBox);
		}
	}

	@Override
	public void reset()
	{
		if (fadeBox != null)
		{
			fadeBox.setAlpha(0.0f);
			totalTimeElapsed = 0;
		}
		else if (onFinishedCallback != null)
		{
			onFinishedCallback.onCallback();
		}
	}
}
