
package com.servebeer.raccoonsexdungeon.waryachts.gamestate;

import java.io.Serializable;
import java.util.ArrayList;

import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Battlefield;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.Shot.ShotType;
import com.servebeer.raccoonsexdungeon.waryachts.battlefields.yachts.Yacht;


public class GameState implements Serializable
{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = -5076861011317608345L;

	protected Boolean myTurn;
	protected String oppMac;

	protected ArrayList<Yacht> myYachts;
	protected ArrayList<Yacht> oppYachts;


	protected ShotType[][] myShots;
	protected ShotType[][] oppShots;


	public GameState(boolean turn)
	{
		myTurn = turn;

		myYachts = new ArrayList<Yacht>(Battlefield.NUM_YACHTS);
		oppYachts = new ArrayList<Yacht>(Battlefield.NUM_YACHTS);
		
		myShots = new ShotType[Battlefield.GRID_SIZE][Battlefield.GRID_SIZE];
		oppShots = new ShotType[Battlefield.GRID_SIZE][Battlefield.GRID_SIZE];

		for (int row = 0; row < Battlefield.GRID_SIZE; row++)
		{
			for (int col = 0; col < Battlefield.GRID_SIZE; col++)
			{
				myShots[row][col] = ShotType.NONE;
				oppShots[row][col] = ShotType.NONE;
			}
		}
		
		
	}

	
	// Mutators
	public void updateTurn(boolean turn)
	{
		myTurn = turn;
	}
	
	public void updateMac(String mac)
	{
		oppMac = mac;
	}

	public void addMyYacht(Yacht yacht)
	{
		myYachts.add(yacht);
	}

	public void addOppYacht(Yacht yacht)
	{
		oppYachts.add(yacht);
	}

	public void addMyShot(int row, int col, Shot.ShotType type)
	{
		myShots[row][col] = type;
	}

	public void addOppShot(int row, int col, Shot.ShotType type)
	{
		oppShots[row][col] = type;
	}
	
	//Accessors
	public Boolean getMyTurn()
	{
		return myTurn;
	}
	
	public String getOppMac()
	{
		return oppMac;
	}
	
	public ArrayList<Yacht> getMyYachts()
	{
		return myYachts;
	}
	
	public ArrayList<Yacht> getOppYachts()
	{
		return oppYachts;
	}
	
	public ShotType[][] getMyShots()
	{
		return myShots;
	}
	
	public ShotType[][] getOppShots()
	{
		return oppShots;
	}

}
