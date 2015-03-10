package database.controller;

import java.sql.*;

import javax.swing.JOptionPane;

/**
 * This controls interactions with the database server.
 * 
 * @author Camron Heaps
 * @version 1.4 3/10/2015 Continued development of UPDATE method
 */
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
	/**
	 * This is the internal reference to the appController.
	 */
	private DatabaseAppController basecontroller;
	private boolean isEditable;
	private String primaryKeyColumnName;
	private String[] columnNames;
	private String[] columnTableFromNames;
	private String[] columnDatabaseFromNames;
	private String[][] tableData;

	/**
	 * This builds the database controller. It handles connecting to the
	 * database.
	 * 
	 * @param basecontroller
	 */
	public DatabaseController(DatabaseAppController basecontroller)
	{
		connectionString = "jdbc:mysql://localhost/dungeons_and_dragons?user=root";
		this.basecontroller = basecontroller;
		checkDriver();
		setupConnection();
	}

	/**
	 * Checks if the driver exists. If it doesn't, it will display the error
	 * code and exit.
	 */
	private void checkDriver()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (Exception currentException)
		{
			displayErrors(currentException);
			System.exit(1);
		}
	}

	/**
	 * Attempts to connect to the database. If fails, displays the error code.
	 */
	private void setupConnection()
	{
		try
		{
			databaseConnection = DriverManager.getConnection(connectionString);
		}
		catch (SQLException currentException)
		{
			displayErrors(currentException);
		}
	}

	/**
	 * Displays the general error code, SQL state, and SQL error code.
	 * 
	 * @param currentException
	 */
	public void displayErrors(Exception currentException)
	{
		JOptionPane.showMessageDialog(basecontroller.getFrame(), currentException.getMessage());

		if (currentException instanceof SQLException)
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

	/**
	 * This method returns a String containing a list of available tables
	 * located in the database.
	 * 
	 * @return
	 */
	public String displayTables()
	{
		String results = "";
		String query = "SHOW TABLES FROM ";
		boolean hasSelectedExistingDatabase = false;
		String database = "";
		while (!hasSelectedExistingDatabase)
		{
			database = JOptionPane.showInputDialog("Which database would you like to search?" + "\n" + this.displayDatabases());

			if (this.displayDatabases().contains(database))
			{
				hasSelectedExistingDatabase = true;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please choose an existing database and write in the format it appears in");
			}
		}
		query += database;

		try
		{
			Statement firstStatement = databaseConnection.createStatement();
			ResultSet answer = firstStatement.executeQuery(query);
			// .getString takes the info out (Destructive)
			// This means answer will be empty when the while loop is done.
			while (answer.next())
			{
				results += answer.getString(1) + "\n";
			}
			// close in reverse order
			answer.close();
			firstStatement.close();
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
		}

		return results;
	}

	/**
	 * This method returns a String containing a list of available databases on
	 * the server.
	 * 
	 * @return
	 */
	public String displayDatabases()
	{
		String results = "";
		String query = "SHOW DATABASES";

		try
		{
			Statement firstStatement = databaseConnection.createStatement();
			ResultSet answer = firstStatement.executeQuery(query);
			while (answer.next())
			{
				results += answer.getString(1) + "\n";
			}
			answer.close();
			firstStatement.close();
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
		}

		return results;
	}

	/**
	 * This method returns a String, formatted in a table form, containing
	 * information about each column in a table.
	 * 
	 * @return
	 */
	public String describeTable()
	{
		String results = "";
		String query = "DESCRIBE characters";

		try
		{
			Statement firstStatement = databaseConnection.createStatement();
			ResultSet answer = firstStatement.executeQuery(query);
			ResultSetMetaData metaDataAnswer = answer.getMetaData();
			int numberOfColumns = metaDataAnswer.getColumnCount();
			while (answer.next())
			{
				for (int currentCol = 1; currentCol < numberOfColumns; currentCol++)
				{
					results += answer.getString(currentCol) + "\t";
				}
				results += "\n";
			}
			answer.close();
			firstStatement.close();
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
		}

		return results;
	}

	/**
	 * This method runs an Insert Query and returns the number of rows effected.
	 * 
	 * @param query
	 *            The query to be sent to the database.
	 * @return The number of rows effected by the insert.
	 */
	public int runINSERTQuery(String query)
	{
		try
		{
			Statement insertStatement = databaseConnection.createStatement();
			int rowsAffected = insertStatement.executeUpdate(query);
			insertStatement.close();
			return rowsAffected;
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
			return 0;
		}
	}

	/**
	 * Finds a cell string value
	 * 
	 * @param columnHeaderName
	 *            The name of the column header of the column to be selected
	 *            from
	 * @param row
	 *            The row to be selected from
	 * @return A string value of a selected cell
	 */
	private String findCellValue(String columnHeaderName, int row)
	{
		String primaryKeyValue = "";

		for (int col = 0; col < columnNames.length; col++)
		{
			if (columnNames[col].equals(columnHeaderName))
			{
				primaryKeyValue = tableData[row][col];
			}
		}

		return primaryKeyValue;
	}

	/**
	 * This method sends a update query to the database that changes a single cell.
	 * @param newData The new value to set the cell to
	 * @param column The column that the new data is located in the 2d string array of database values.
	 * @param row The row that the new data is located in the 2d string array of database values.
	 * @return The number of rows affected by the update call. Will return 0 if update failed.
	 */
	public int runUPDATEQuery(String newData, int column, int row)
	{
		if (isEditable)
		{
			try
			{
				DatabaseMetaData metaDataOfDatabase = databaseConnection.getMetaData();
				ResultSet primaryKeyResultSet = metaDataOfDatabase.getPrimaryKeys(null, null, columnTableFromNames[column]);
				primaryKeyResultSet.next();
				primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
				primaryKeyResultSet.close();
			}
			catch (SQLException currentSQLError)
			{
				displayErrors(currentSQLError);
				return 0;
			}

			String UPDATEquery = ("UPDATE `" + columnDatabaseFromNames[column] + "`.`" + columnTableFromNames[column] + "` SET `" + columnNames[column] + "` = '" + newData + "' WHERE `" + columnTableFromNames[column] + "`.`" + primaryKeyColumnName + "` = " + findCellValue(
					primaryKeyColumnName, row));

			try
			{
				Statement updateStatement = databaseConnection.createStatement();
				int rowsAffected = updateStatement.executeUpdate(UPDATEquery);
				updateStatement.close();
				return rowsAffected;
			}
			catch (SQLException currentSQLError)
			{
				displayErrors(currentSQLError);
				return 0;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "This is in a non-editable form." + "\n" + "SELECT * FROM table_name is the only editable query.");
			basecontroller.getFrame().getPanel().refreshTable(tableData, columnNames);
			return 0;
		}
	}

	/**
	 * This method returns a String, formatted in a table form, containing all
	 * of the information retrieved from the query written in the dataTextField
	 * box.
	 * 
	 * @param query
	 * @return
	 */
	public String runSELECTQuery(String query)
	{
		String results = "";

		try
		{
			Statement firstStatement = databaseConnection.createStatement();
			ResultSet answer = firstStatement.executeQuery(query);
			ResultSetMetaData metaDataAnswer = answer.getMetaData();
			int numberOfColumns = metaDataAnswer.getColumnCount();
			for (int currentCol = 0; currentCol < numberOfColumns; currentCol++)
			{
				results += metaDataAnswer.getColumnLabel(currentCol + 1) + "\t";
			}
			results += "\n";

			while (answer.next())
			{
				for (int currentCol = 0; currentCol < numberOfColumns; currentCol++)
				{
					results += answer.getString(currentCol + 1) + "\t";
				}
				results += "\n";
			}
			answer.close();
			firstStatement.close();
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
		}

		return results;
	}

	/**
	 * This method returns a 2d Array of the values retrieved from the query
	 * call.
	 * 
	 * @param query
	 *            The query to be sent to the database.
	 * @return 2d array of the values retrieved from the query
	 */
	public String[][] runSELECTQueryTwoGetTable(String query)
	{
		setEditabilty(query);
		
		try
		{
			Statement SELECTStatement = databaseConnection.createStatement();
			ResultSet answer = SELECTStatement.executeQuery(query);
			ResultSetMetaData metaDataOfAnswer = answer.getMetaData();
			int numberOfColumns = metaDataOfAnswer.getColumnCount();
			int numberOfRows = findNumberOfRows(answer);
			String[][] results = new String[numberOfRows][numberOfColumns];
			int currentRow = 0;
			while (answer.next())
			{
				for (int col = 0; col < numberOfColumns; col++)
				{
					results[currentRow][col] = answer.getString(col + 1);
				}
				currentRow++;
			}
			answer.close();
			SELECTStatement.close();
			tableData = results;
			return results;
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
			return (new String[][] { { "An error occured" } });
		}

	}

	/**
	 * This method returns a string array of the column headers retrieved form
	 * the query call
	 * 
	 * @param query
	 *            The query to be sent to the database.
	 * @return a string array of the column headers
	 */
	public String[] runSELECTQueryTwoGetColumnNames(String query)
	{

		try
		{
			Statement SELECTStatement = databaseConnection.createStatement();
			ResultSet answer = SELECTStatement.executeQuery(query);
			ResultSetMetaData metaDataOfAnswer = answer.getMetaData();
			int numberOfColumns = metaDataOfAnswer.getColumnCount();
			columnNames = new String[numberOfColumns];
			columnTableFromNames = new String[numberOfColumns];
			columnDatabaseFromNames = new String[numberOfColumns];
			for (int currentCol = 0; currentCol < numberOfColumns; currentCol++)
			{
				columnNames[currentCol] = metaDataOfAnswer.getColumnLabel(currentCol + 1);
				columnTableFromNames[currentCol] = metaDataOfAnswer.getTableName(currentCol + 1);
				columnDatabaseFromNames[currentCol] = metaDataOfAnswer.getCatalogName(currentCol + 1);
			}
			answer.close();
			SELECTStatement.close();
			return columnNames;
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
			return null;
		}
	}

	/**
	 * This method finds the number of rows in a ResultSet
	 * 
	 * @param searchResultSet
	 *            The set to search in.
	 * @return The number of rows in the set.
	 */
	public int findNumberOfRows(ResultSet searchResultSet)
	{
		int numberOfRows = 0;
		try
		{
			searchResultSet.last();
			numberOfRows = searchResultSet.getRow();
			searchResultSet.beforeFirst();
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
		}
		return numberOfRows;
	}
	
	/**
	 * Finds if the user is allowed to edit the database via the JTable.
	 * Will set the query received to the lastSELECTQuery received.
	 * @param query The SELECT query the JTable is based off of.
	 */
	public void setEditabilty(String query)
	{
		if (query.indexOf(",") == -1)
		{
			if (query.toLowerCase().contains("select * from"))
			{
				isEditable = true;
			}
			else
			{
				isEditable = false;
			}
		}
		else
		{
			isEditable = false;
		}
	}
}
