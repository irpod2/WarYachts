
package com.servebeer.raccoonsexdungeon.waryachts.battlefields;

import org.andengine.entity.sprite.Sprite;

public class Yacht
{
	protected Sprite yachtSprite;
	protected int units;
	protected int row;
	protected int col;
	protected Orientation orientation;

	public enum Orientation
	{
		HORIZONTAL, VERTICAL
	}

	public Yacht(int r, int c, Orientation o, int u, Sprite sprite)
	{
		row = r;
		col = c;
		orientation = o;
		units = u;
		yachtSprite = sprite;
	}

	public int getRow()
	{
		return row;
	}

	public int getColumn()
	{
		return col;
	}

	public Orientation getOrientation()
	{
		return orientation;
	}

	public int getUnits()
	{
		return units;
	}
	
	public Sprite getSprite()
	{
		return yachtSprite;
	}

	public boolean shoot(int r, int c)
	{
		switch (orientation)
		{
		case HORIZONTAL:
			return ((row == r) && (col <= c) && (c <= col + units));
		case VERTICAL:
			return ((col == c) && (row <= r) && (r <= row + units));
		}
		// Will never reach here
		return false;
	}
}
