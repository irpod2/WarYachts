
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import org.andengine.entity.sprite.Sprite;

public class Carrier extends Yacht
{
	public final static int UNITS = 5;

	public Carrier(int r, int c, Orientation o, Sprite sprite)
	{
		super("Helicopter\nCarrier", r, c, o, UNITS, sprite);
	}
}
