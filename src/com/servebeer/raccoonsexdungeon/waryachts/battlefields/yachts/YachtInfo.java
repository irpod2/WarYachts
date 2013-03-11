
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

	public YachtInfo(YachtType yt, Orientation o, int r, int c, int u, String n)
	{
		yachtType = yt;
		orientation = o;
		row = r;
		col = c;
		units = u;
		name = n;
	}

	public YachtType yachtType;
	public Orientation orientation;
	public int row;
	public int col;
	public int units;
	public String name;

}
