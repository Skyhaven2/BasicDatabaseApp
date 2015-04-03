package database.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import database.controller.DatabaseAppController;

public class DatabasePanel extends JPanel
{

	/**
	 * This is the internal reference to the appController.
	 */
	private DatabaseAppController basecontroller;
	/**
	 * The layout of the panel
	 */
	private SpringLayout myLayout;
	/**
	 * The table that will display database information
	 */
	private JTable dataTable;
	/**
	 * The pane that holds the dataTable
	 */
	private JScrollPane dataTablePane;
	/**
	 * The text area that will display database information
	 */
	private JTextArea dataArea;
	/**
	 * The pane that holds the dataArea
	 */
	private JScrollPane dataAreaPane;
	/**
	 * This button will send the query text located in the tableInputQueryField and fire an update to it
	 */
	private JButton tableQueryButton;
	/**
	 * This button will send the query text located in the areaInputQueryField and fire an update to it
	 */
	private JButton queryButton;
	/**
	 * When pressed, runs the displayDatabases method located in the DatabaseController
	 */
	private JButton displayDatabasesButton;
	/**
	 * When pressed, runs the displayTablesButton method located in the DatabaseController
	 * Will prompt the user to choose a database
	 */
	private JButton displayTablesButton;
	/**
	 * When pressed, runs the describeTableButton method located in the DatbaseController
	 * Will prompt the user to choose a table from the dungeons_and_dragons database
	 */
	private JButton describeTableButton;
	/**
	 * When pressed, inserts a new row into the dataTable and creates a new entry in the database with default values
	 */
	private JButton insertRowButton;
	/**
	 * When pressed, uses the information in the userNameField and passwordField to atempt to connect to the database
	 */
	private JButton loginButton;
	/**
	 * Used to store queries that effect the dataTable
	 */
	private JTextField tableInputQueryField;
	/**
	 * Used to store queries that effect the dataArea
	 */
	private JTextField areaInputQueryField;
	/**
	 * Used to store the username of the user
	 */
	private JTextField userNameField;
	/**
	 * The model of the dataTable
	 */
	private DefaultTableModel dataTableModel;
	/**
	 * The last SELECT query sent to the database that effected the dataTable
	 */
	private String lastTableSELECTQuery;
	/**
	 * Used to stroe the password of the user
	 */
	private JPasswordField passwordField;

	public DatabasePanel(DatabaseAppController basecontroller)
	{
		this.basecontroller = basecontroller;
		myLayout = new SpringLayout();
		queryButton = new JButton("Send Query");
		tableQueryButton = new JButton("Send SELECT Query");
		displayDatabasesButton = new JButton("Available Databases");
		displayTablesButton = new JButton("Available Tables");
		describeTableButton = new JButton("Table Info");
		insertRowButton = new JButton("Insert Row");
		loginButton = new JButton("Log on");
		dataArea = new JTextArea(10, 50);
		dataAreaPane = new JScrollPane(dataArea);
		dataTableModel = new DefaultTableModel();
		dataTable = new JTable();
		dataTablePane = new JScrollPane(dataTable);
		Dimension dataTablePaneDimension = new Dimension(700, 400);
		dataTablePane.setPreferredSize(dataTablePaneDimension);
		areaInputQueryField = new JTextField(39);
		tableInputQueryField = new JTextField(49);
		userNameField = new JTextField(20);
		passwordField = new JPasswordField(null, 20);

		

		setupPanel();
		setupLayout();
		setupListners();
	}

	private void setupPanel()
	{
		this.setBackground(Color.lightGray);
		this.setSize(1200, 900);
		this.setFocusable(true);
		dataArea.setEditable(false);
		this.setLayout(myLayout);
		this.add(queryButton);
		this.add(dataAreaPane);
		this.add(areaInputQueryField);
		this.add(dataTablePane);
		this.add(tableQueryButton);
		this.add(tableInputQueryField);
		this.add(displayDatabasesButton);
		this.add(displayTablesButton);
		this.add(describeTableButton);
		this.add(insertRowButton);
		this.add(passwordField);
		this.add(userNameField);
		this.add(loginButton);
		passwordField.setEchoChar('*');
	}

	private void setupLayout()
	{
		myLayout.putConstraint(SpringLayout.WEST, loginButton, 0, SpringLayout.WEST, queryButton);
		myLayout.putConstraint(SpringLayout.SOUTH, loginButton, 0, SpringLayout.SOUTH, this);
		myLayout.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, this);
		myLayout.putConstraint(SpringLayout.SOUTH, userNameField, -6, SpringLayout.NORTH, passwordField);
		myLayout.putConstraint(SpringLayout.EAST, userNameField, 0, SpringLayout.EAST, passwordField);
		myLayout.putConstraint(SpringLayout.SOUTH, passwordField, -6, SpringLayout.NORTH, loginButton);
		myLayout.putConstraint(SpringLayout.NORTH, describeTableButton, 6, SpringLayout.SOUTH, displayTablesButton);
		myLayout.putConstraint(SpringLayout.WEST, describeTableButton, 6, SpringLayout.EAST, dataAreaPane);
		myLayout.putConstraint(SpringLayout.NORTH, insertRowButton, 0, SpringLayout.NORTH, dataTablePane);
		myLayout.putConstraint(SpringLayout.WEST, insertRowButton, 6, SpringLayout.EAST, dataTablePane);
		myLayout.putConstraint(SpringLayout.NORTH, queryButton, 6, SpringLayout.SOUTH, dataAreaPane);
		myLayout.putConstraint(SpringLayout.WEST, queryButton, 0, SpringLayout.WEST, dataAreaPane);
		myLayout.putConstraint(SpringLayout.WEST, dataTablePane, 0, SpringLayout.WEST, this);
		myLayout.putConstraint(SpringLayout.NORTH, tableQueryButton, 6, SpringLayout.SOUTH, dataTablePane);
		myLayout.putConstraint(SpringLayout.WEST, tableQueryButton, 0, SpringLayout.WEST, queryButton);
		myLayout.putConstraint(SpringLayout.NORTH, dataTablePane, 210, SpringLayout.NORTH, this);
		myLayout.putConstraint(SpringLayout.NORTH, tableInputQueryField, 6, SpringLayout.SOUTH, dataTablePane);
		myLayout.putConstraint(SpringLayout.EAST, tableInputQueryField, 0, SpringLayout.EAST, dataTablePane);
		myLayout.putConstraint(SpringLayout.NORTH, areaInputQueryField, 6, SpringLayout.SOUTH, dataAreaPane);
		myLayout.putConstraint(SpringLayout.EAST, areaInputQueryField, 0, SpringLayout.EAST, dataAreaPane);
		myLayout.putConstraint(SpringLayout.NORTH, displayTablesButton, 6, SpringLayout.SOUTH, displayDatabasesButton);
		myLayout.putConstraint(SpringLayout.NORTH, displayDatabasesButton, 10, SpringLayout.NORTH, this);
		myLayout.putConstraint(SpringLayout.WEST, displayTablesButton, 6, SpringLayout.EAST, dataAreaPane);
		myLayout.putConstraint(SpringLayout.WEST, displayDatabasesButton, 6, SpringLayout.EAST, dataAreaPane);
	}

	private void setupListners()
	{
		queryButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String databaseAnswer = basecontroller.getDatabase().runSELECTQueryTextArea(areaInputQueryField.getText());
				dataArea.setText(databaseAnswer);
				areaInputQueryField.setText("");
				// int databaseAnswer =
				// basecontroller.getDatabase().runINSERTQuery(areaInputQueryField.getText());
				// dataArea.setText(Integer.toString(databaseAnswer));
			}

		});

		tableQueryButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String[][] databaseAnswer = basecontroller.getDatabase().runSELECTQueryTableGetTable(tableInputQueryField.getText());
				String[] databaseHeaderAnswer = basecontroller.getDatabase().runSELECTQueryTableGetColumnNames(tableInputQueryField.getText());
				refreshTable(databaseAnswer, databaseHeaderAnswer);
				lastTableSELECTQuery = tableInputQueryField.getText();
				tableInputQueryField.setText("");
			}

		});

		dataTableModel.addTableModelListener(new TableModelListener()
		{

			@Override
			public void tableChanged(TableModelEvent change)
			{
				// This gets fired when the SELECT statement is sent too!
				if (change.getType() == TableModelEvent.UPDATE)
				{
					try
					{
						int row = change.getFirstRow();
						int column = change.getColumn();
						Object newData = dataTableModel.getValueAt(row, column);

						basecontroller.getDatabase().runUPDATEQueryTable(newData.toString(), column, row);
					}
					catch (Exception currentException)
					{

					}

				}
			}

		});

		displayDatabasesButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String databaseAnswer = basecontroller.getDatabase().displayDatabases();
				dataArea.setText(databaseAnswer);
				areaInputQueryField.setText("");
			}

		});

		displayTablesButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String databaseAnswer = basecontroller.getDatabase().displayTables();
				dataArea.setText(databaseAnswer);
				areaInputQueryField.setText("");
			}

		});

		describeTableButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String databaseAnswer = basecontroller.getDatabase().describeTable();
				dataArea.setText(databaseAnswer);
				areaInputQueryField.setText("");
			}

		});

		insertRowButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				basecontroller.getDatabase().runINSERTQueryTable();
				String[][] databaseAnswer = basecontroller.getDatabase().runSELECTQueryTableGetTable(lastTableSELECTQuery);
				String[] databaseHeaderAnswer = basecontroller.getDatabase().runSELECTQueryTableGetColumnNames(lastTableSELECTQuery);
				refreshTable(databaseAnswer, databaseHeaderAnswer);
			}

		});
		
		loginButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				basecontroller.getDatabase().connectionStringBuilder("localhost", "dungeons_and_dragons", userNameField.getText(), passwordField.getText());
				basecontroller.getDatabase().setupConnection();
			}
			
		});

	}

	/**
	 * Reloads the dataTable JTable so that it is showing the current database information
	 * @param newData The new data to be displayed
	 * @param columnHeaders The new column headers to be displayed
	 */
	public void refreshTable(String[][] newData, String[] columnHeaders)
	{
		dataTableModel.setDataVector(newData, columnHeaders);
		dataTable.setModel(dataTableModel);
	}
}
