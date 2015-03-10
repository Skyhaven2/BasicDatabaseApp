package database.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
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

	private DatabaseAppController basecontroller;
	private SpringLayout myLayout;
	private JTable dataTable;
	private JScrollPane dataTablePane;
	private JTextArea dataArea;
	private JScrollPane dataAreaPane;
	private JButton tableQueryButton;
	private JButton queryButton;
	private JButton displayDatabasesButton;
	private JButton displayTablesButton;
	private JTextField tableInputQueryField;
	private JTextField areaInputQueryField;
	private DefaultTableModel dataTableModel;
	
	public DatabasePanel(DatabaseAppController basecontroller)
	{
		this.basecontroller = basecontroller;
		myLayout = new SpringLayout();
		queryButton = new JButton("Send Query");
		tableQueryButton = new JButton("Send SELECT Query");
		displayDatabasesButton = new JButton("Available Databases");
		displayTablesButton = new JButton("Available Tables");
		dataArea = new JTextArea(10, 50);
		dataAreaPane = new JScrollPane(dataArea);
		dataTableModel = new DefaultTableModel();
		dataTable = new JTable();
		dataTablePane = new JScrollPane(dataTable);
		Dimension dataTablePaneDimension = new Dimension(700, 400);
		dataTablePane.setPreferredSize(dataTablePaneDimension);
		areaInputQueryField = new JTextField(39);
		tableInputQueryField = new JTextField(49);
		
		
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
		this.setLayout(myLayout);;
		this.add(queryButton);
		this.add(dataAreaPane);
		this.add(areaInputQueryField);
		this.add(dataTablePane);
		this.add(tableQueryButton);
		this.add(tableInputQueryField);
		this.add(displayDatabasesButton);
		this.add(displayTablesButton);
	}
	
	private void setupLayout()
	{
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
//				String databaseAnswer = basecontroller.getDatabase().describeTable();
				String databaseAnswer = basecontroller.getDatabase().runSELECTQuery(areaInputQueryField.getText());
				dataArea.setText(databaseAnswer);
				areaInputQueryField.setText("");
//				int databaseAnswer = basecontroller.getDatabase().runINSERTQuery(areaInputQueryField.getText());
//				dataArea.setText(Integer.toString(databaseAnswer));
			}
			
		});
		
		tableQueryButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent click)
			{
				String[][] databaseAnswer = basecontroller.getDatabase().runSELECTQueryTwoGetTable(tableInputQueryField.getText());
				String[] databaseHeaderAnswer = basecontroller.getDatabase().runSELECTQueryTwoGetColumnNames(tableInputQueryField.getText());
				refreshTable(databaseAnswer, databaseHeaderAnswer);
				tableInputQueryField.setText("");
			}
			
		});
		
		dataTableModel.addTableModelListener(new TableModelListener()
		{

			@Override
			public void tableChanged(TableModelEvent change)
			{
				//This gets fired when the SELECT statement is sent too!
				if(change.getType() == TableModelEvent.UPDATE)
				{
					try
					{
						int row = change.getFirstRow();
						int column = change.getColumn();
						Object newData = dataTableModel.getValueAt(row, column);
						
						basecontroller.getDatabase().runUPDATEQuery(newData.toString(), column, row);
					}
					catch(Exception currentException)
					{
						
					}
					
				}
				else if(change.getType() == TableModelEvent.INSERT)
				{
					System.out.println("I have been inserted");
				}
				else if(change.getType() == TableModelEvent.DELETE)
				{
					System.out.println("I have been deleteded");
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
		
	}
	
	public void refreshTable(String[][] newData, String[] columnHeaders)
	{
		dataTableModel.setDataVector(newData, columnHeaders);
		dataTable.setModel(dataTableModel);
	}
}
