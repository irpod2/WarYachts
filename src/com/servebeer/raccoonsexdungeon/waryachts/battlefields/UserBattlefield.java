package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.input.touch.TouchEvent;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;

public class UserBattlefield extends Battlefield
{
	public UserBattlefield()
	{
		super();
	}

	// ===========================================================
	// For when enemy shoots our battlefield
	public boolean shoot(int row, int col)
	{
		for (Yacht y : yachts)
		{
			if (y.shoot(row, col))
			{
				shots[row][col] = new Shot(row, col, ShotType.HIT, this);
				return true;
			}
		}
		shots[row][col] = new Shot(row, col, ShotType.MISS, this);
		return false;
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
		
		if(pSceneTouchEvent.isActionUp())
			touchedDown = false;
		return true;
	}
}
