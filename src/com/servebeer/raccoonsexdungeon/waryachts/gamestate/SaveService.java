package com.servebeer.raccoonsexdungeon.waryachts.gamestate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;

public class SaveService {

	private String SAVE_FILENAME = "TheSaveFile.ser";

	private GameState saveData;

	private static SaveService saveService;
	private Activity context;

	private FileOutputStream fOut = null;
	private ObjectOutputStream  osw = null;
	
	private FileInputStream fin = null;
	private ObjectInputStream sin = null;

	public SaveService(Context context){
		this.context = (Activity)context;
	}

	public static SaveService getInstance(Context context){
		if(saveService == null){
			saveService = new SaveService(context);

		}
		return saveService;
	}

	public  void save(GameState object){
		try {
			fOut = context.openFileOutput(SAVE_FILENAME, Activity.MODE_PRIVATE);
			osw = new ObjectOutputStream(fOut);
			osw.writeObject(object);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public GameState load(){
			try {
				fin = context.openFileInput(SAVE_FILENAME);
				sin = new ObjectInputStream(fin);
				saveData = (GameState) sin.readObject();
				sin.close();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			return saveData;
	}
}