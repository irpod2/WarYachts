
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.sprite.Sprite;

public class WarYacht extends Yacht
{
	public final static int UNITS = 3;

	public WarYacht(int r, int c, Orientation o, Sprite sprite)
	{
		super(r, c, o, UNITS, sprite);
	}
}
