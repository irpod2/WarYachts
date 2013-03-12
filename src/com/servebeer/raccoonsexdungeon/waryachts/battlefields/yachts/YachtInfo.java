
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

	public YachtInfo(YachtType yt, Orientation o, int r, int c, int u, String n)
	{
		yachtType = yt;
		orientation = o;
		row = r;
		col = c;
		units = u;
		name = n;
		numHits = units;
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
			break;
		case OLD_REL:
			units = Destroyer.UNITS;
			name = Destroyer.NAME;
			break;
		case WAR_YAT:
			units = WarYacht.UNITS;
			name = WarYacht.NAME;
			break;
		case POSI:
			units = SubYacht.UNITS;
			name = SubYacht.NAME;
			break;
		case SKUNK:
			units = Skunker.UNITS;
			name = Skunker.NAME;
			break;
		}

		numHits = units;
	}

}
