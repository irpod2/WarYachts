
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
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.servebeer.raccoonsexdungeon.waryachts.utils.BackgroundFactory;
import com.servebeer.raccoonsexdungeon.waryachts.utils.ContentFactory;

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
	public static int cameraWidth = CAMERA_WIDTH;
	public static int cameraHeight = CAMERA_HEIGHT;
	protected Scene scene;
	protected View buttonView;

	Background oceanBG;

	@Override
	protected void onSetContentView()
	{
		super.onSetContentView();

		// Inflate gesture_layout xml
		LayoutInflater vi = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		buttonView = vi.inflate(R.layout.start_screen, null);

		// Add gesture view to activity
		addContentView(buttonView,
				BaseGameActivity.createSurfaceViewLayoutParams());
		// buttonView.setVisibility(View.GONE);
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
		ContentFactory.init(this);
		oceanBG = BackgroundFactory.createStartBackground();

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

}
