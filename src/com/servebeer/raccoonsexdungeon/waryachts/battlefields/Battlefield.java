
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class Battlefield implements ITouchArea
{
	public static final int GRID_SIZE = 10;
	protected Shot[][] shots;
	protected Sprite gridSprite;
	protected ArrayList<Yacht> yachts;

	public Battlefield()
	{
		gridSprite = SpriteFactory.createGrid();
		yachts = new ArrayList<Yacht>();
		shots = new Shot[GRID_SIZE][GRID_SIZE];
	}

	public boolean addYacht(Yacht y)
	{
		if (yachts.add(y))
		{
			gridSprite.attachChild(y.getSprite());
			return true;
		}
		else
			return false;
	}

	public void shoot(int row, int col)
	{
		for (Yacht y : yachts)
		{
			if (y.shoot(row, col))
			{
				shots[row][col] = new Shot(row, col, ShotType.HIT, this);
				return;
			}
		}
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
	}

	public void attachShot(Sprite shot)
	{
		gridSprite.attachChild(shot);
	}

	public Sprite getSprite()
	{
		return gridSprite;
	}

	public int getCellFromPosition(float pos)
	{
		return (int) (pos / SpriteFactory.GRID_CELL_SIZE);
	}

	@Override
	public boolean contains(float pX, float pY)
	{
		return gridSprite.contains(pX, pY);
	}

	@Override
	public float[] convertSceneToLocalCoordinates(float pX, float pY)
	{
		float[] coords = new float[2];
		coords[0] = pY - gridSprite.getY() - SpriteFactory.GRID_PADDING;
		coords[1] = pX - gridSprite.getX() - SpriteFactory.GRID_PADDING;
		return coords;
	}

	@Override
	public float[] convertLocalToSceneCoordinates(float pX, float pY)
	{
		float[] coords = new float[2];
		coords[0] = pY + gridSprite.getY() + SpriteFactory.GRID_PADDING;
		coords[1] = pX + gridSprite.getX() + SpriteFactory.GRID_PADDING;
		return coords;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		int row = getCellFromPosition(pTouchAreaLocalY);
		int col = getCellFromPosition(pTouchAreaLocalX);
		if (0 <= row && row < GRID_SIZE && 0 <= col && col < GRID_SIZE)
		{
			shoot(row, col);
		}
		return true;
	}
}
