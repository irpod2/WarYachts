
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

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

	public Yacht getYacht(float pX, float pY)
	{
		for (Yacht y : yachts)
		{
			if (y.getSprite().contains(pX, pY))
			{
				yachts.remove(y);
				return y;
			}
		}
		return null;
	}

	public boolean contains(float pX, float pY)
	{
		return gridSprite.contains(pX, pY);
	}
}
