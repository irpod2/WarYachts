
package com.servebeer.raccoonsexdungeon.waryachts;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.BaseGameActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.servebeer.raccoonsexdungeon.waryachts.bluetooth.ConnectionHandler;
import com.servebeer.raccoonsexdungeon.waryachts.utils.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.ContentFactory;

public class WarYachtsActivity extends BaseGameActivity
{

	// ===========================================================
	// Constants
	// ===========================================================
	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	public final float BUTTON_TEXT_SIZE = 36.0f;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera camera;
	public static int cameraWidth = CAMERA_WIDTH;
	public static int cameraHeight = CAMERA_HEIGHT;
	protected Scene scene;
	protected ConnectionHandler btHandler;
	protected View buttonView;

	protected Button hostGameButton;
	protected Button findGameButton;
	protected Button preferencesButton;
	protected Button quitButton;

	Background oceanBG;

	@Override
	protected void onSetContentView()
	{
		super.onSetContentView();

		// Inflate gesture_layout xml
		LayoutInflater vi = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		buttonView = vi.inflate(R.layout.start_screen, null);

		addContentView(buttonView,
				BaseGameActivity.createSurfaceViewLayoutParams());
	}

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
		// CONTENT FACTORY AND BACKGROUND CREATION
		ContentFactory.init(this);
		btHandler = new ConnectionHandler(this);
		oceanBG = BackgroundFactory.createStartBackground();

		// BUTTON CREATION
		hostGameButton = (Button) findViewById(R.id.host_game_button);
		findGameButton = (Button) findViewById(R.id.find_game_button);
		preferencesButton = (Button) findViewById(R.id.preferences_button);
		quitButton = (Button) findViewById(R.id.quit_game_button);

		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Typeface buttonTypeface = Typeface.createFromAsset(getAssets(),
						"fonts/TMDeadhand.ttf");
				hostGameButton.setTypeface(buttonTypeface);
				hostGameButton.setTextSize(BUTTON_TEXT_SIZE);
				findGameButton.setTypeface(buttonTypeface);
				findGameButton.setTextSize(BUTTON_TEXT_SIZE);
				preferencesButton.setTypeface(buttonTypeface);
				preferencesButton.setTextSize(BUTTON_TEXT_SIZE);
				quitButton.setTypeface(buttonTypeface);
				quitButton.setTextSize(BUTTON_TEXT_SIZE);
			}
		});

		findGameButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!btHandler.isDiscovering())
				{
					btHandler.requestEnableBluetooth();
				}
			}
		});

		quitButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (btHandler.isDiscovering())
				{
					btHandler.kill();
				}
				finish();
			}
		});

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception
	{
		scene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		scene.setBackground(oceanBG);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
 
		case ConnectionHandler.REQUEST_ENABLE_BT:
		{
			Toast.makeText(this,
					"Successfully enabled bluetooth, searching for devices.",
					Toast.LENGTH_SHORT).show();

			btHandler.onBtEnabled(resultCode);
			break;
		}
		default:
		{
			// We obviously don't know what this is about, maybe someone
			// above does

			// TAKE ME OUT
			// HOW TO PRINT TO SCREEN
			Toast.makeText(getBaseContext(), "UNHANDLED REQUEST CODE",
					Toast.LENGTH_SHORT).show();

			super.onActivityResult(requestCode, resultCode, data);
		} // default

		} // Switch
	}

	@Override
	public void onDestroy()
	{
		// unregisterReceiver(deviceReceiver);
		super.onDestroy();
	}
}
