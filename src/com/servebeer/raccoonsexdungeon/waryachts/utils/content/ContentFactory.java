
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;

public class ContentFactory
{
	protected static final int FONT_SIZE = 64;
	protected static final float SIZE_RATIO = WarYachtsActivity.CAMERA_WIDTH
			/ WarYachtsActivity.getCameraWidth();

	protected static BaseGameActivity activity;
	protected static int cameraWidth;
	protected static int cameraHeight;

	protected static Font deadHand;

	protected static boolean LOAD_COMPLETE = false;

	public static void init(BaseGameActivity bga)
	{
		activity = bga;
		FontFactory.setAssetBasePath("fonts/");

		cameraWidth = WarYachtsActivity.getCameraWidth();
		cameraHeight = WarYachtsActivity.getCameraHeight();

		loadContent();
		SpriteFactory.loadContent();
		LOAD_COMPLETE = true;
	}

	protected static void loadContent()
	{
		deadHand = FontFactory.createFromAsset(activity.getFontManager(),
				activity.getTextureManager(), 256, 256, activity.getAssets(),
				"TMDeadhand.ttf", FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
		deadHand.load();
	}
}
