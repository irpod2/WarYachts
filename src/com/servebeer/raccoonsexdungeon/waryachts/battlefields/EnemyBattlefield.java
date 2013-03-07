package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.input.touch.TouchEvent;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.utils.content.SpriteFactory;

public class EnemyBattlefield extends Battlefield
{
	protected Targeter targeter;
	protected Targeter selectedLocation;
	
	public EnemyBattlefield()
	{
		super();
		targeter = SpriteFactory.createTargeter();
		selectedLocation = SpriteFactory.createTargetSelector();
	}

	protected void attachTargeter(int row, int col)
	{
		targeter.setLocation(row, col);
		if (!targeter.hasParent())
			gridSprite.attachChild(targeter);
	}

	protected void setTargeter(int row, int col)
	{
		targeter.setLocation(row, col);
	}

	protected void selectLocation(int row, int col)
	{
		selectedLocation.setLocation(row, col);

		if (!selectedLocation.hasParent())
			gridSprite.attachChild(selectedLocation);
	}

	public int getSelectedRow()
	{
		return selectedLocation.getRow();
	}

	public int getSelectedCol()
	{
		return selectedLocation.getCol();
	}

	// ===========================================================
	// For confirmation of hit or miss on enemy battlefield
	public void hit(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.HIT, this);
	}

	public void miss(int row, int col)
	{
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
	}

	// ===========================================================

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (pSceneTouchEvent.isActionDown())
			touchedDown = true;
		if (!touchedDown)
			return false;

		int row = getCellFromPosition(pTouchAreaLocalY);
		int col = getCellFromPosition(pTouchAreaLocalX);
		if (0 <= row && row < GRID_SIZE && 0 <= col && col < GRID_SIZE)
		{
			if (pSceneTouchEvent.isActionDown())
				attachTargeter(row, col);
			else if (pSceneTouchEvent.isActionMove()
					&& (targeter.getRow() != row || targeter.getCol() != col))
			{
				if (!targeter.hasParent())
					gridSprite.attachChild(targeter);

				setTargeter(row, col);
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				touchedDown = false;
				setTargeter(row, col);
				selectLocation(row, col);
				if (targeter.hasParent())
					gridSprite.detachChild(targeter);
			}
		}
		else if (targeter.hasParent())
		{
			gridSprite.detachChild(targeter);
		}
		return true;
	}
}
