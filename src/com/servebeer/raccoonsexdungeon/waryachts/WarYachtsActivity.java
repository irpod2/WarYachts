
package com.servebeer.raccoonsexdungeon.waryachts;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.call.Callback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.controlmessages.ControlMessage;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions.ComposedTransitionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions.FadeInHandler;
import com.servebeer.raccoonsexdungeon.waryachts.handlers.transitions.ITransitionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.DefeatScenario;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.GameInstanceScenario;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.IScenario;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.StartMenuScenario;
import com.servebeer.raccoonsexdungeon.waryachts.scenario.VictoryScenario;
import com.servebeer.raccoonsexdungeon.waryachts.utils.CallbackVoid;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.ContentFactory;

public class WarYachtsActivity extends BaseGameActivity
{

	// ===========================================================
	// Constants
	// ===========================================================
	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera camera;
	protected HUD hud;
	public static int cameraWidth = CAMERA_WIDTH;
	public static int cameraHeight = CAMERA_HEIGHT;
	protected Scene startMenuScene;
	protected Scene gameInstanceScene;
	protected Scene endGameScene;
	protected static ConnectionHandler btHandler;

	protected IScenario currentScenario;
	protected IScenario nextScenario;
	protected ITransitionHandler transitionHandler;


	// Warning suppression due to deprecation on finding window size
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		// For Android API 13+ getWidth() and getHeight() are deprecated
		if (android.os.Build.VERSION.SDK_INT >= 13)
		{
			Point size = new Point();
			display.getSize(size);
			cameraWidth = size.x;
			cameraHeight = size.y;
		}
		// For APIs < 13, getSize(Point) doesn't exist. Nice forethought,
		// Android.
		else
		{
			cameraWidth = display.getWidth();
			cameraHeight = display.getHeight();
		}

		// Initialize the camera
		camera = new Camera(0, 0, cameraWidth, cameraHeight);

		// Create the engine options
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(cameraWidth, cameraHeight), camera);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception
	{
		// CONTENT FACTORY INITIALIZATION
		ContentFactory.init(this);

		btHandler = new ConnectionHandler(this, messageHandlerCallback,
				prepareClientGameInstanceCallback);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception
	{
		startMenuScene = new Scene();
		gameInstanceScene = new Scene();
		endGameScene = new Scene();
		currentScenario = new StartMenuScenario(this, startMenuScene,
				prepareLoadGameInstanceCallback);
		hud = new HUD();
		camera.setHUD(hud);

		pOnCreateSceneCallback.onCreateSceneFinished(startMenuScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		transitionHandler = new FadeInHandler(this, camera, currentScenario,
				doNothingCallback);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	// =================================================================
	// Connection Callbacks
	// =================================================================

	Callback<ControlMessage> messageHandlerCallback = new Callback<ControlMessage>()
	{
		@Override
		public void onCallback(final ControlMessage ctrlMsg)
		{
			runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (ctrlMsg != null)
						currentScenario.handleControlMessage(ctrlMsg);
					currentScenario.onNetworkNowFree();
				}
			});
		}
	};

	// =================================================================
	// Routing Callbacks
	// =================================================================

	protected CallbackVoid doNothingCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{

		}
	};

	protected CallbackVoid switchScenarioCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			currentScenario = nextScenario;
		}
	};

	protected CallbackVoid prepareLoadGameInstanceCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			nextScenario = new GameInstanceScenario(WarYachtsActivity.this,
					gameInstanceScene, prepareStartMenuCallback,
					prepareVictoryCallback, prepareDefeatCallback, btHandler,
					false, true);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	protected CallbackVoid prepareClientGameInstanceCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			btHandler.listen();
			nextScenario = new GameInstanceScenario(WarYachtsActivity.this,
					gameInstanceScene, prepareStartMenuCallback,
					prepareVictoryCallback, prepareDefeatCallback, btHandler,
					false, false);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	protected CallbackVoid prepareHostGameInstanceCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			nextScenario = new GameInstanceScenario(WarYachtsActivity.this,
					gameInstanceScene, prepareStartMenuCallback,
					prepareVictoryCallback, prepareDefeatCallback, btHandler,
					true, false);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	protected CallbackVoid prepareStartMenuCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{
			btHandler.reset();
			nextScenario = new StartMenuScenario(WarYachtsActivity.this,
					startMenuScene, prepareLoadGameInstanceCallback);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	protected CallbackVoid prepareVictoryCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{

			nextScenario = new VictoryScenario(endGameScene,
					prepareStartMenuCallback, btHandler);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	protected CallbackVoid prepareDefeatCallback = new CallbackVoid()
	{
		@Override
		public void onCallback()
		{

			nextScenario = new DefeatScenario(endGameScene,
					prepareStartMenuCallback, btHandler);
			transitionHandler = new ComposedTransitionHandler(
					WarYachtsActivity.this, camera, currentScenario,
					nextScenario, switchScenarioCallback);
		}
	};

	// =================================================================

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{

		case ConnectionHandler.REQUEST_ENABLE_BT:
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Toast.makeText(this, "Successfully enabled bluetooth.",
						Toast.LENGTH_SHORT).show();
				btHandler.onBtEnabled();
			}
			else
			{
				Toast.makeText(
						this,
						"Could not enable bluetooth, War Yachts will now exit.",
						Toast.LENGTH_LONG).show();
				btHandler.reset();
				finish();
			}
			break;
		}
		case ConnectionHandler.REQUEST_ENABLE_DISCOVERY:
		{
			if (resultCode == ConnectionHandler.DEFAULT_DISCOVERY_TIME)
			{
				// Notify the user that discovery was enabled,
				Toast.makeText(
						this,
						"Successfully enabled discovery, waiting for connection",
						Toast.LENGTH_SHORT).show();
				// Set up connection listener in ConnectionHandler
				btHandler.onDiscoveryEnabled();
				// Create game for when a connection is established
				prepareHostGameInstanceCallback.onCallback();
			}
			else
			{
				Toast.makeText(
						this,
						"Could not enable discovery. Try searching instead of hosting.",
						Toast.LENGTH_SHORT).show();
				btHandler.reset();
			}
			break;
		}
		default:
		{
			// We obviously don't know what this is about, maybe someone
			// above does

			// TAKE ME OUT
			// HOW TO PRINT TO SCREEN
			Toast.makeText(getBaseContext(),
					"UNHANDLED REQUEST CODE, WE SHOULD NOT BE HERE!",
					Toast.LENGTH_SHORT).show();

			super.onActivityResult(requestCode, resultCode, data);
		}
		}
	}

	public static ConnectionHandler getConnectionHandler()
	{
		return btHandler;
	}

	public static int getCameraWidth()
	{
		return cameraWidth;
	}

	public static int getCameraHeight()
	{
		return cameraHeight;
	}

	// Called when user pushes the back button
	@Override
	public void onBackPressed()
	{
		// Reset busy and stop performing connection activities
		btHandler.reset();

		// If the scenario doesn't handle back presses, pass it up
		if (currentScenario == null || !currentScenario.handleBackPress())
			super.onBackPressed();
	}

	@Override
	public void onDestroy()
	{
		// unregisterReceiver(deviceReceiver);
		super.onDestroy();
	}
}
