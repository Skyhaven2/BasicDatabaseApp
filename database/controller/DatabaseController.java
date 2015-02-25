package database.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DatabaseController
{
	/**
	 * This is the URL for the database.
	 */
	private String connectionString;
	/**
	 * This is the connection to the database.
	 */
	private Connection databaseConnection;
	private DatabaseAppController basecontroller;
	
	public DatabaseController(DatabaseAppController basecontroller)
	{
		connectionString = "jdbc:mysql://localhost/dungeons_and_dragons?user=root";
		this.basecontroller = basecontroller;
		checkDriver();
		setupConnection();
	}
	
	/**
	 * Checks if the driver exsists.
	 * If it doesn't, it will display the error code and exit.
	 */
	private void checkDriver()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(Exception currentException)
		{
			displayErrors(currentException);
			System.exit(1);
		}
	}
	
	/**
	 * Attempts to connect to the database.
	 * If fails, displays the error code.
	 */
	private void setupConnection()
	{
		try
		{
			databaseConnection = DriverManager.getConnection(connectionString);
		}
		catch(SQLException currentException)
		{
			displayErrors(currentException);
		}
	}
	
	/**
	 * Displays the general error code, SQL state, and SQL error code.
	 * @param currentException
	 */
	public void displayErrors(Exception currentException)
	{
		JOptionPane.showMessageDialog(basecontroller.getFrame(), currentException.getMessage());
		
		if(currentException instanceof SQLException)
		{
			JOptionPane.showMessageDialog(basecontroller.getFrame(), "SQL State: " + ((SQLException) currentException).getSQLState());
			JOptionPane.showMessageDialog(basecontroller.getFrame(), "SQL Error Code: " + ((SQLException) currentException).getErrorCode());
		}
	}
	
	/**
	 * Breaks the connection with the database server.
	 */
	public void closeConnection()
	{
		try
		{
			databaseConnection.close();
		}
		catch (Exception currentException)
		{
			displayErrors(currentException);
		}
	}
}
