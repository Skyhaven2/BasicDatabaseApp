package database.view;

import javax.swing.JFrame;

import database.controller.DatabaseAppController;

public class DatabaseFrame extends JFrame
{
	private DatabasePanel startPanel;
	
	public DatabaseFrame(DatabaseAppController basecontroller)
	{
		startPanel = new DatabasePanel(basecontroller);

		setupFrame();
	}

	private void setupFrame()
	{
		this.setContentPane(startPanel);
		this.setSize(1200, 900);
		this.setResizable(false);
		this.setVisible(true);
	}
}
