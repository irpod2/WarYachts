
package com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions;

import org.andengine.ui.activity.BaseGameActivity;

import com.servebeer.raccoonsexdungeon.waryachts.scenario.IScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;

public class ComposedTransitionHandler implements ITransitionHandler
{
	protected BaseGameActivity activity;
	protected IScenario scenarioA;
	protected IScenario scenarioB;
	protected ITransitionHandler currentHandler;
	protected CallbackVoid onFinishedCallback;

	public ComposedTransitionHandler(BaseGameActivity bga, IScenario scnA,
			IScenario scnB, CallbackVoid onFinishedCB)
	{
		activity = bga;
		scenarioA = scnA;
		scenarioB = scnB;
		onFinishedCallback = onFinishedCB;
		currentHandler = new FadeOutHandler(activity, scenarioA, fadeInCallback);
	}

	protected CallbackVoid fadeInCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					activity.getEngine().setScene(scenarioB.getScene());
				}
			});
			currentHandler = new FadeInHandler(activity, scenarioB,
					onFinishedCallback);
		}
	};
}
