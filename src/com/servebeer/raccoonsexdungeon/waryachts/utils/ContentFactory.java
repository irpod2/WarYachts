package com.servebeer.raccoonsexdungeon.waryachts.utils;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

public class ContentFactory
{
	protected static final int FONT_SIZE = 32;
	
	protected static BaseGameActivity activity;
	
	protected static Font deadHand;
	
	protected static boolean LOAD_COMPLETE = false;
	
	public static void init(BaseGameActivity bga)
	{
		activity = bga;
		FontFactory.setAssetBasePath("fonts/");
		
		loadContent();
		LOAD_COMPLETE = true;
	}
	
	protected static void loadContent()
	{
		deadHand = FontFactory.createFromAsset(activity.getFontManager(),
				activity.getTextureManager(), 256, 256, activity.getAssets(), "TMDeadhand.ttf",
				FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
		deadHand.load();
	}
}
