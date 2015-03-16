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
	/**
	 * This is whether or not INSERT, DELETE, UPDATE statements can be used on
	 * the current table
	 */
	private boolean isEditable;
	/**
	 * This is the primary key column name of the table that a column comes from
	 * example "Player" is column name that comes from the "Players" table the
	 * "Players" table's primary key is "Player_id" so the value of
	 * primaryKeyColumnName is "Player_id"
	 */
	private String primaryKeyColumnName;
	/**
	 * This is an array of the column header names of the current selected table
	 */
	private String[] columnNames;
	/**
	 * This is an array of what table each column comes from
	 */
	private String[] columnTableFromNames;
	/**
	 * This is an array of what database each column comes from
	 */
	private String[] columnDatabaseFromNames;
	/**
	 * This is the information used to create the current selected table
	 */
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

	// Connection and error methods
	// ----------------------------------------------------------------------------------------------------------------------

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
	 * checks if a query contains something that will destroy data
	 * 
	 * @param currentQuery
	 *            the query to be sent
	 * @return true if contains destructive query
	 */
	private boolean checkForDataViolation(String currentQuery)
	{
		if (currentQuery.toUpperCase().contains(" DROP ") || currentQuery.toUpperCase().contains(" TRUNCATE ") || currentQuery.toUpperCase().contains(" SET ") || currentQuery.toUpperCase().contains(" ALTER "))
		{
			return true;
		}
		else
		{
			return false;
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

	// Text area methods
	// -----------------------------------------------------------------------------------------------------------------------

	/**
	 * Sends a pop up requesting the user to enter in the desired database to be
	 * used
	 * 
	 * @return the database name the user selected
	 */
	private String chooseDatabase()
	{
		boolean hasSelectedExistingDatabase = false;
		String database = "";
		while (!hasSelectedExistingDatabase)
		{
			database = JOptionPane.showInputDialog("Which database would you like to search?" + "\n" + this.displayDatabases());

			if (database != null)
			{
				if (this.displayDatabases().contains(database))
				{
					hasSelectedExistingDatabase = true;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please choose an existing database and write in the format it appears in");
				}
			}
			else
			{
				return "";
			}
		}
		return database;
	}

	/**
	 * This method returns a String containing a list of available tables
	 * located in the user selected database.
	 * 
	 * @return a list of available tables in selected database
	 */
	public String displayTables()
	{
		String results = "";
		String query = "SHOW TABLES FROM ";
		String database = chooseDatabase();
		if (database.equals(""))
		{
			return "";
		}
		query += database;

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
	 * This method returns a String containing a list of available tables
	 * located in the dungeons_and_dragons database
	 * 
	 * @return a list of available tables in dungeons_and_dragons database
	 */
	public String displayTablesDandD()
	{
		String results = "";
		String query = "SHOW TABLES FROM ";
		String database = "dungeons_and_dragons";
		query += database;

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
	 * displays a pop up requesting the user to select a table from the
	 * dugeons_and_dragons database
	 * 
	 * @return the user selected table name
	 */
	private String chooseTableFromDandD()
	{
		String table = "";
		boolean hasSelectedExistingTable = false;
		String tablesInSelectedDatabase = this.displayTablesDandD();
		while (!hasSelectedExistingTable)
		{
			table = JOptionPane.showInputDialog("Which table would you like to search?" + "\n" + tablesInSelectedDatabase);

			if (table != null)
			{

				if (tablesInSelectedDatabase.contains(table))
				{
					hasSelectedExistingTable = true;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please choose an existing table and write in the format it appears in");
				}
			}
			else
			{
				return "";
			}
		}
		return table;
	}

	/**
	 * This method returns a String, formatted in a table form, containing
	 * information about each column in a table. The user is prompted to select
	 * a table from the dungeons_and_dragons database
	 * 
	 * @return a String, formatted in a table form, containing information about
	 *         each column in the table.
	 */
	public String describeTable()
	{
		String results = "";
		String query = "DESCRIBE ";
		String table = chooseTableFromDandD();
		if (table.equals(""))
		{
			return "";
		}
		query += table;

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
	 * This method returns a String, formatted in a table form, containing all
	 * of the information retrieved from the query written in the dataTextField
	 * box.
	 * 
	 * @param query
	 * @return
	 */
	public String runSELECTQueryTextArea(String query)
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

	// JTable Helper Methods
	// --------------------------------------------------------------------------------------------------------------------

	/**
	 * Finds if the user is allowed to edit the database via the JTable. Will
	 * set the query received to the lastSELECTQuery received.
	 * 
	 * @param query
	 *            The SELECT query the JTable is based off of.
	 */
	private void checkAndSetEditabilty(String query)
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

	/**
	 * Sets the primary key column name variable
	 * 
	 * @param column
	 *            The column number of the column with the parent table to be
	 *            searched for a primary key
	 */
	private void setPrimaryKeyColumnName(int column)
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
		}
	}

	/**
	 * This method finds the number of rows in a ResultSet
	 * 
	 * @param searchResultSet
	 *            The set to search in.
	 * @return The number of rows in the set.
	 */
	private int findNumberOfRows(ResultSet searchResultSet)
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
	 * Finds the data type of each column in the table that the selected column
	 * of the selected table is from
	 * 
	 * @return the data type of each column
	 */
	private String[] findTypes(int column)
	{
		String query = "DESCRIBE ";
		String table = columnTableFromNames[column];
		query += table;

		try
		{
			Statement firstStatement = databaseConnection.createStatement();
			ResultSet answer = firstStatement.executeQuery(query);
			String[] columnTypes = new String[columnNames.length];
			int currentCol = 0;
			while (answer.next())
			{
				columnTypes[currentCol] = answer.getString(2);
				currentCol++;
			}
			answer.close();
			firstStatement.close();
			return columnTypes;
		}
		catch (SQLException currentSQLError)
		{
			displayErrors(currentSQLError);
			return new String[] { "" };
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
		String cellValue = "";

		for (int col = 0; col < columnNames.length; col++)
		{
			if (columnNames[col].equals(columnHeaderName))
			{
				cellValue = tableData[row][col];
			}
		}

		return cellValue;
	}

	/**
	 * Lists all of the non-primary key column names of the currently selected
	 * table in this format (tableName1, tableName2) this is used in INSERT
	 * queries
	 * 
	 * @return a String containing the non-primary key column names
	 */
	private String listNonPrimaryKeyColumnNames()
	{
		String nonPrimaryKeyColumnNames = "(";
		for (int col = 0; col < columnNames.length; col++)
		{
			if (!(primaryKeyColumnName.equals(columnNames[col])))
			{
				if (col == (columnNames.length - 1))
				{
					nonPrimaryKeyColumnNames += "`" + columnNames[columnNames.length - 1] + "`)";
				}
				else
				{
					nonPrimaryKeyColumnNames += "`" + columnNames[col] + "`,";
				}
			}
			else if (col == (columnNames.length - 1))
			{
				nonPrimaryKeyColumnNames += ")";
			}
		}

		return nonPrimaryKeyColumnNames;
	}

	/**
	 * Creates a list of default values of each column in the selected table
	 * Example: if column 1 is the type int and column 2 is type string then
	 * ('0', 'A') is returned. This is used in INSERT queries.
	 * 
	 * @return a list of default values of each column
	 */
	private String createListOfDefaultValues()
	{
		String listOfDefaultValues = "(";
		String[] types = findTypes(0);
		for (int col = 0; col < types.length; col++)
		{
			if (!(primaryKeyColumnName.equals(columnNames[col])))
			{
				if (types[col].contains("int"))
				{
					if (col == (types.length - 1))
					{
						listOfDefaultValues += "'1')";
					}
					else
					{
						listOfDefaultValues += "'1', ";
					}
				}
				else if (types[col].contains("varchar"))
				{
					if (col == (types.length - 1))
					{
						listOfDefaultValues += "'A')";
					}
					else
					{
						listOfDefaultValues += "'A', ";
					}
				}
				else
				{
					if (col == (types.length - 1))
					{
						listOfDefaultValues += "NULL)";
					}
					else
					{
						listOfDefaultValues += "NULL, ";
					}
				}
			}
			else if (col == (types.length - 1))
			{
				listOfDefaultValues += ")";
			}
		}

		return listOfDefaultValues;
	}

	// JTable Main Methods
	// --------------------------------------------------------------------------------------------------------------------

	/**
	 * This method runs an Insert Query and returns the number of rows effected.
	 * 
	 * @param query
	 *            The query to be sent to the database.
	 * @return The number of rows effected by the insert.
	 */
	public int runINSERTQueryTable()
	{
		if (isEditable)
		{
			try
			{
				setPrimaryKeyColumnName(0);
				String insertQuery = ("INSERT INTO `" + columnDatabaseFromNames[0] + "`.`" + columnTableFromNames[0] + "` " + listNonPrimaryKeyColumnNames() + " VALUES" + createListOfDefaultValues());
				Statement insertStatement = databaseConnection.createStatement();
				int rowsAffected = insertStatement.executeUpdate(insertQuery);
				insertStatement.close();
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
			return 0;
		}
	}

	/**
	 * This method sends a update query to the database that changes a single
	 * cell.
	 * 
	 * @param newData
	 *            The new value to set the cell to
	 * @param column
	 *            The column that the new data is located in the 2d string array
	 *            of database values.
	 * @param row
	 *            The row that the new data is located in the 2d string array of
	 *            database values.
	 * @return The number of rows affected by the update call. Will return 0 if
	 *         update failed.
	 */
	public int runUPDATEQueryTable(String newData, int column, int row)
	{
		if (isEditable)
		{
			setPrimaryKeyColumnName(column);
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
	 * This method returns a 2d Array of the values retrieved from the query
	 * call.
	 * 
	 * @param query
	 *            The query to be sent to the database.
	 * @return 2d array of the values retrieved from the query
	 */
	public String[][] runSELECTQueryTableGetTable(String query)
	{
		checkAndSetEditabilty(query);

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
	public String[] runSELECTQueryTableGetColumnNames(String query)
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
}
