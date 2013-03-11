
package com.servebeer.raccoonsexdungeon.waryachts.utils.content;

import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Carrier;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Destroyer;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Skunker;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.SubYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.WarYacht;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.Orientation;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.YachtInfo.YachtType;

public class YachtFactory extends SpriteFactory
{
	protected static ArrayList<TextureRegion> blueYachtRegions;
	protected static ArrayList<TextureRegion> redYachtRegions;


	public static void loadContent()
	{
		blueYachtRegions = new ArrayList<TextureRegion>(5);
		redYachtRegions = new ArrayList<TextureRegion>(5);

		BitmapTextureAtlas yachtAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 512, 512);

		// Blue Team
		TextureRegion blueCarrier = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueCarrier.setTextureWidth(320);
		blueCarrier.setTextureHeight(64);
		blueYachtRegions.add(blueCarrier);
		TextureRegion blueDestroyer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueDestroyer.setTextureWidth(256);
		blueDestroyer.setTextureHeight(64);
		blueDestroyer.setTextureY(64);
		blueYachtRegions.add(blueDestroyer);
		TextureRegion blueWarYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueWarYacht.setTextureWidth(192);
		blueWarYacht.setTextureHeight(64);
		blueWarYacht.setTextureY(128);
		blueYachtRegions.add(blueWarYacht);
		TextureRegion blueSubYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueSubYacht.setTextureWidth(192);
		blueSubYacht.setTextureHeight(64);
		blueSubYacht.setTextureY(192);
		blueYachtRegions.add(blueSubYacht);
		TextureRegion blueSkunker = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		blueSkunker.setTextureWidth(128);
		blueSkunker.setTextureHeight(64);
		blueSkunker.setTextureY(256);
		blueYachtRegions.add(blueSkunker);

		// Red Team
		TextureRegion redCarrier = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redCarrier.setTextureWidth(320);
		redCarrier.setTextureHeight(64);
		redCarrier.setTextureX(192);
		redCarrier.setTextureY(256);
		redYachtRegions.add(redCarrier);
		TextureRegion redDestroyer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redDestroyer.setTextureWidth(256);
		redDestroyer.setTextureHeight(64);
		redDestroyer.setTextureX(256);
		redDestroyer.setTextureY(192);
		redYachtRegions.add(redDestroyer);
		TextureRegion redSubYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redSubYacht.setTextureWidth(192);
		redSubYacht.setTextureHeight(64);
		redSubYacht.setTextureX(320);
		redSubYacht.setTextureY(128);
		redYachtRegions.add(redSubYacht);
		TextureRegion redWarYacht = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redWarYacht.setTextureWidth(192);
		redWarYacht.setTextureHeight(64);
		redWarYacht.setTextureX(320);
		redWarYacht.setTextureY(64);
		redYachtRegions.add(redWarYacht);
		TextureRegion redSkunker = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yachtAtlas, activity.getAssets(),
						"sprites/Yachts.png", 0, 0);
		redSkunker.setTextureWidth(128);
		redSkunker.setTextureHeight(64);
		redSkunker.setTextureX(384);
		redYachtRegions.add(redSkunker);
		yachtAtlas.load();
	}

	public static Sprite createBlueYacht(YachtInfo yi)
	{
		int row = yi.row;
		int col = yi.col;
		Orientation or = yi.orientation;
		int shipNo = yi.yachtType.ordinal();

		Sprite yachtSprite = new Sprite(getCellLocation(col),
				getCellLocation(row - (or == Orientation.VERTICAL ? 1 : 0)),
				blueYachtRegions.get(shipNo),
				activity.getVertexBufferObjectManager());
		yachtSprite.setScaleCenter(0, 0);
		yachtSprite.setScale(GRID_CELL_SIZE / yachtSprite.getHeight());
		yachtSprite.setRotationCenter(0, yachtSprite.getHeightScaled());
		switch (or)
		{
		case HORIZONTAL:
			break;
		case VERTICAL:
			// Rotate from lower left-corner as anchor point
			yachtSprite.setRotation(90.0f);
			// Sprite appears displaced vertically by 1 row, so counter this
			// by adding 1 row
			break;
		}
		return yachtSprite;
	}

	public static Carrier createCarrierYacht(YachtInfo yi)
	{
		Carrier y = new Carrier(yi, createBlueYacht(yi));
		return y;
	}

	public static Destroyer createDestroyerYacht(YachtInfo yi)
	{	
		Destroyer y = new Destroyer(yi, createBlueYacht(yi));
		return y;
	}

	public static SubYacht createSubYacht(YachtInfo yi)
	{
		SubYacht y = new SubYacht(yi, createBlueYacht(yi));
		return y;
	}

	public static WarYacht createWarYacht(YachtInfo yi)
	{
		WarYacht y = new WarYacht(yi, createBlueYacht(yi));
		return y;
	}

	public static Skunker createSkunkerYacht(YachtInfo yi)
	{
		Skunker y = new Skunker(yi, createBlueYacht(yi));
		return y;
	}
}
