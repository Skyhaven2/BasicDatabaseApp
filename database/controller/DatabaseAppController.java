package database.controller;

import database.view.DatabaseFrame;

public class DatabaseAppController
{

	private DatabaseFrame appFrame;
	private DatabaseController myDBController;
	
	public DatabaseAppController()
	{
		myDBController = new DatabaseController(this);
		appFrame = new DatabaseFrame(this);
	}
	
	public void start()
	{
		myDBController.runSELECTQueryTableGetTable("SELECT * FROM characters");
		myDBController.runSELECTQueryTableGetColumnNames("SELECT * FROM characters");
		System.out.println(myDBController.buildSELECTQuery("characters"));
	}
	
	public DatabaseFrame getFrame()
	{
		return appFrame;
	}
	
	public DatabaseController getDatabase()
	{
		return myDBController;
	}
	
}
