package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.sprite.Sprite;

public class Destroyer extends Yacht
{
	public final static int UNITS = 4;

	public Destroyer(int r, int c, Orientation o, Sprite sprite)
	{
		super(r, c, o, UNITS, sprite);
	}
}
