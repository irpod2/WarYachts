
package com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts;

import java.io.Serializable;


public class YachtInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8437010708330731997L;

	public enum YachtType
	{
		HEL_CAR, OLD_REL, POSI, WAR_YAT, SKUNK,
	}

	public enum Orientation
	{
		HORIZONTAL, VERTICAL
	}

	public YachtType yachtType;
	public Orientation orientation;
	public int row;
	public int col;
	public int units;
	public int numHits;
	public String name;
	public String shortName;

	public boolean yachtHits[];

	public YachtInfo(YachtType yt, Orientation o, int r, int c, int u,
			String n, String sn)
	{
		yachtType = yt;
		orientation = o;
		row = r;
		col = c;
		units = u;
		name = n;
		shortName = sn;
		numHits = units;

		yachtHits = new boolean[units];
		for (int i = 0; i < units; i++)
		{
			yachtHits[i] = false;
		}
	}

	public YachtInfo(YachtType yt, Orientation o, int r, int c)
	{
		yachtType = yt;
		orientation = o;
		row = r;
		col = c;

		switch (yachtType)
		{
		case HEL_CAR:
			units = Carrier.UNITS;
			name = Carrier.NAME;
			shortName = Carrier.SHORT_NAME;
			break;
		case OLD_REL:
			units = Destroyer.UNITS;
			name = Destroyer.NAME;
			shortName = Destroyer.SHORT_NAME;
			break;
		case WAR_YAT:
			units = WarYacht.UNITS;
			name = WarYacht.NAME;
			shortName = WarYacht.SHORT_NAME;
			break;
		case POSI:
			units = SubYacht.UNITS;
			name = SubYacht.NAME;
			shortName = SubYacht.SHORT_NAME;
			break;
		case SKUNK:
			units = Skunker.UNITS;
			name = Skunker.NAME;
			shortName = Skunker.SHORT_NAME;
			break;
		}

		numHits = units;
	}

	public boolean hasBeenHit(int r, int c)
	{
		if (orientation == Orientation.HORIZONTAL)
		{
			return yachtHits[c - col];
		}
		else 
		{
			return yachtHits[r - row];
		}
	}

	public void registerHit(int r, int c)
	{
		if (orientation == Orientation.HORIZONTAL)
		{
			yachtHits[c - col] = true;
		}
		else 
		{
			yachtHits[r - row] = true;
		}
	}

}
