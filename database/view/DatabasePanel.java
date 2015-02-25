package database.view;

import java.awt.Color;

import javax.swing.JPanel;

import database.controller.DatabaseAppController;

public class DatabasePanel extends JPanel
{

	private DatabaseAppController basecontroller;
	
	public DatabasePanel(DatabaseAppController basecontroller)
	{
		this.basecontroller = basecontroller;
		
		setupPanel();
		setupLayout();
		setupListners();
	}
	
	private void setupPanel()
	{
		this.setBackground(Color.WHITE);
	}
	
	private void setupLayout()
	{
		this.setSize(900, 400);
		this.setFocusable(true);
	}
	
	private void setupListners()
	{
		
	}
	
}
