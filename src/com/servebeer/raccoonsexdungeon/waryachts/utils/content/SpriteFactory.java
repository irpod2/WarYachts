
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.servebeer.raccoonsexdungeon.waryachts.WarYachtsActivity;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Targeter;

public class SpriteFactory extends ContentFactory
{
	protected static TextureRegion gridRegion;
	protected static TextureRegion hitRegion;
	protected static TextureRegion missRegion;
	protected static TextureRegion targeterRegion;
	protected static final float GRID_RATIO = SIZE_RATIO
			* WarYachtsActivity.getCameraWidth() / 512;
	public static final float GRID_PADDING = GRID_RATIO * 34.0f;
	public static final float GRID_CELL_SIZE = GRID_RATIO * 44.4f;
	protected static final float SPRITE_PADDING = 1.0f;

	public static void loadContent()
	{
		// Grid
		BitmapTextureAtlas gridAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 512, 512);
		gridRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gridAtlas, activity.getAssets(), "sprites/Grid.png", 0, 0);
		gridAtlas.load();

		// Hit
		BitmapTextureAtlas hitAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 64, 64);
		hitRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				hitAtlas, activity.getAssets(), "sprites/HitCircle.png", 0, 0);
		hitAtlas.load();

		// Miss
		BitmapTextureAtlas missAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 64, 64);
		missRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(missAtlas, activity.getAssets(),
						"sprites/MissCircle.png", 0, 0);
		missAtlas.load();

		// Targeter
		BitmapTextureAtlas targeterAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 64, 64);
		targeterRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(targeterAtlas, activity.getAssets(),
						"sprites/TargetCircle.png", 0, 0);
		targeterAtlas.load();
		
		YachtFactory.loadContent();
	}

	public static Sprite createGrid()
	{
		Sprite gridSprite = new Sprite(0, 0, cameraWidth, cameraWidth,
				gridRegion, activity.getVertexBufferObjectManager());
		return gridSprite;
	}

	public static float getCellLocation(int index)
	{
		return GRID_PADDING + GRID_CELL_SIZE * index;
	}

	public static Sprite createHitSprite(int row, int col)
	{
		Sprite hitSprite = new Sprite(getCellLocation(col),
				getCellLocation(row), GRID_CELL_SIZE - SPRITE_PADDING,
				GRID_CELL_SIZE - SPRITE_PADDING, hitRegion,
				activity.getVertexBufferObjectManager());
		return hitSprite;
	}

	public static Sprite createMissSprite(int row, int col)
	{
		Sprite missSprite = new Sprite(getCellLocation(col),
				getCellLocation(row), GRID_CELL_SIZE - SPRITE_PADDING,
				GRID_CELL_SIZE - SPRITE_PADDING, missRegion,
				activity.getVertexBufferObjectManager());
		return missSprite;
	}

	public static Targeter createTargeter()
	{
		Targeter targeter = new Targeter(0, 0, 0, 0, GRID_CELL_SIZE
				- SPRITE_PADDING, GRID_CELL_SIZE - SPRITE_PADDING,
				targeterRegion, activity.getVertexBufferObjectManager());
		return targeter;
	}
}
