
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

public class BackgroundFactory extends ContentFactory
{
	public static Background createStartBackground()
	{
		BitmapTextureAtlas oceanAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 1024, 2048,
				TextureOptions.DEFAULT);
		TextureRegion oceanRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(oceanAtlas, activity, "backgrounds/ocean.png",
						0, 0);
		oceanAtlas.load();
		Sprite oceanSprite = new Sprite(0, 0, oceanRegion,
				activity.getVertexBufferObjectManager());
		oceanSprite.setScaleCenter(0, 0);
		oceanSprite.setScale(SIZE_RATIO);
		SpriteBackground oceanBGSprite = new SpriteBackground(oceanSprite);
		return oceanBGSprite;
	}
}
