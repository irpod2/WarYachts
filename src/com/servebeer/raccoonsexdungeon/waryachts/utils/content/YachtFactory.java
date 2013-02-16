
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Yacht.Orientation;

public class YachtFactory extends SpriteFactory
{
	protected static Sprite createYachtSprite(int row, int col, Orientation or,
			String path)
	{
		BitmapTextureAtlas yachtAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 128);
		TextureRegion yachtRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(), path, 0, 0);
		yachtAtlas.load();
		Sprite yachtSprite = new Sprite(getCellLocation(row),
				getCellLocation(col), yachtRegion,
				activity.getVertexBufferObjectManager());
		yachtSprite.setScaleCenter(0, 0);
		yachtSprite.setScale(GRID_CELL_SIZE / yachtSprite.getHeight());
		switch (or)
		{
		case HORIZONTAL:
			break;
		case VERTICAL:
			yachtSprite.setRotationCenter(0, yachtSprite.getHeightScaled());
			yachtSprite.setRotation(90.0f);
			break;
		}
		return yachtSprite;
	}

	public static Sprite createWarYacht(int row, int col, Orientation or)
	{
		return createYachtSprite(row, col, or, "sprites/WarYacht.png");
	}

	public static Sprite createSubYacht(int row, int col, Orientation or)
	{
		return createYachtSprite(row, col, or, "sprites/SubYacht.png");
	}
}
