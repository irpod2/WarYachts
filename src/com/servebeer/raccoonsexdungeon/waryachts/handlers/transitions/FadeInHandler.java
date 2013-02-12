
package com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.IScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class FadeInHandler implements ITransitionHandler, IUpdateHandler
{
	protected BaseGameActivity activity;
	protected IScenario scenario;
	protected Scene scene;
	protected CallbackVoid onFinishedCallback;
	protected Rectangle fadeBox;
	protected float totalTimeElapsed;

	public FadeInHandler(BaseGameActivity bga, IScenario scno,
			CallbackVoid onFinishedCB)
	{
		activity = bga;
		scenario = scno;
		scene = scenario.getScene();
		onFinishedCallback = onFinishedCB;
		int cameraWidth = WarYachtsActivity.getCameraWidth();
		int cameraHeight = WarYachtsActivity.getCameraHeight();
		fadeBox = new Rectangle(0, 0, cameraWidth, cameraHeight,
				activity.getVertexBufferObjectManager());
		fadeBox.setColor(Color.BLACK);
		fadeBox.setAlpha(1.0f);
		scene.attachChild(fadeBox);
		totalTimeElapsed = 0;
		scene.registerUpdateHandler(this);
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
			scene.detachChild(fadeBox);
			scene.unregisterUpdateHandler(this);
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
