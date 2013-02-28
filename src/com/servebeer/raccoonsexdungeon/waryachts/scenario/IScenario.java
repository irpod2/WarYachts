
package com.servebeer.raccoonsexdungeon.waryachts.scenario;

import org.andengine.entity.scene.Scene;

public interface IScenario
{
	// Get ready to fade in
	public void prepareStart();

	// Scenario has faded in completely
	public void start();

	// Get ready to fade out
	public void prepareEnd();

	// Scenario has faded out completely
	public void end();

	// Returns the scene to be faded in or out
	public Scene getScene();
	
	public boolean handleBackPress();
	
	public void onNetworkNowFree();
}
