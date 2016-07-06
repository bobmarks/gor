//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.gor.util.Site;

/**
 * This class can store and retrieve information about a database connection
 * meta data such as table names and column names / types. It has methods for
 * creating a new file and for opening an existing file.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
@SuppressWarnings("unchecked")
public class DatabaseConnectionFile {
	
	// declare array to hold database connection information
	// DATABASE, DRIVER, CONN_URL, CONN_NAME, USERNAME, PASSWORD
	private String[] dbInfo;

	// number of total tables and columns
	private int numOfTables;
	private int numOfColumns;

	// Vector to hold all the database tables	
	private Vector databaseTables;

	// Two Vector arrays to hold column names and their corresponding types
	// (e.g. Integer). The size of the array is equal to the number of tables.
	private Vector[] tableColumnNames;
	private Vector[] tableColumnTypes;

	/**
	 * Default Constructor
	 */
	public DatabaseConnectionFile() {
		databaseTables = new Vector();
		dbInfo = new String[5];
	}

	/**
	 * Constructor which opens a file
	 * 
	 * @param fileName
	 *            Name of the database connection file
	 */
	public DatabaseConnectionFile(String fileName) {
		databaseTables = new Vector();
		dbInfo = new String[5];
		readFromFile(fileName);
	}

	/**
	 * Method for creating a new database connection file. The newDBInfo String
	 * array holds the first size lines of the database connection file. These
	 * lines hold information about the database in question with connection
	 * information. The method trys to connect to the database in question and
	 * retrieve the database meta data (table names with their column names and
	 * column types.
	 * 
	 * @param newFileName
	 *            Name of the new database connection file
	 * @param newDBInfo
	 *            Array of data which should be enough to connect to database.
	 * @return returns true if file was created sucessfully
	 */
	public boolean createFile(String newFileName, String[] newDBInfo) {
		// assume password is already encrypted using SimpleENC
		this.dbInfo = newDBInfo;
		boolean success = false;

		// Create connection to database and load its Meta Data.
		try {
			Connection conn = this.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();

			StringBuffer sbFile = new StringBuffer("[DATABASE_INFO]");
			sbFile.append("\nDATABASE=" + dbInfo[0]);
			sbFile.append("\nDRIVER=" + dbInfo[1]);
			sbFile.append("\nCONN_URL=" + dbInfo[2]);
			sbFile.append("\nUSERNAME=" + dbInfo[3]);
			sbFile.append("\nPASSWORD=" + dbInfo[4]);
			sbFile.append("\n\n[TABLES]");

			String[] types = new String[] { "TABLE" };
			// The following works fine for MySQL
			ResultSet rsTables = dbmd.getTables(null, null, "%", types);
			ResultSet rsColumns = null;

			numOfTables = 0;
			numOfColumns = 0;
			String tempTable;

			StringBuffer sbColumns = new StringBuffer();

			while (rsTables.next()) {
				numOfTables++;
				tempTable = rsTables.getString(3);
				sbFile.append("\n" + tempTable);

				rsColumns = dbmd.getColumns(null, null, tempTable, null);
				while (rsColumns.next()) {
					numOfColumns++;
					sbColumns.append(rsColumns.getString(4) + ","
							+ rsColumns.getInt(5) + ",");
				}
				sbColumns.append("\n");
			}
			sbFile.append("\n\n[COLUMN_NAMES_AND_TYPES]\n");
			sbFile.append(sbColumns.toString());

			try {
				BufferedWriter fileout = new BufferedWriter(new FileWriter(Site.getDatabaseDir() + newFileName));

				fileout.write(sbFile.toString());
				fileout.close();
				success = true;
			} catch (Exception exFile) {
				exFile.printStackTrace();
				System.err.print("Problem creating file");
			}
			;

			try {
				conn.close();
			} catch (Exception exClose) {
			}
		} catch (SQLException exSQL) {
			System.err.println("Database Problem");
		} catch (Exception exGen) {
			System.err.println("General Exception");
		}

		return success;
	}

	/**
	 * Method for creating a new database connection file. The newDBInfo String
	 * array holds the first size lines of the database connection file. These
	 * lines hold information about the database in question with connection
	 * information. The method trys to connect to the database in question and
	 * retrieve the database meta data (table names with their column names and
	 * column types.
	 * 
	 * @param DBFileName
	 *            Name of the new database connection file
	 */
	public void readFromFile(String DBFileName) {
		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(Site.getDatabaseDir() + DBFileName));

			String fileLine = fileIn.readLine(); // [DATABASE_INFO]

			for (int i = 0; i < 5; i++) {
				fileLine = fileIn.readLine();
				dbInfo[i] = fileLine.substring(fileLine.lastIndexOf('=') + 1);
			}

			fileLine = fileIn.readLine(); // blank
			fileLine = fileIn.readLine(); // [TABLES]

			do {
				fileLine = fileIn.readLine();
				if (fileLine.length() > 0)
					databaseTables.addElement(fileLine);
			} while (fileLine.length() > 0);

			numOfTables = databaseTables.size();
			tableColumnNames = new Vector[numOfTables];
			tableColumnTypes = new Vector[numOfTables];

			fileLine = fileIn.readLine(); // [COLUMN_NAMES_AND_TYPES]
			numOfColumns = 0;

			for (int i = 0; i < numOfTables; i++) {
				tableColumnNames[i] = new Vector();
				tableColumnTypes[i] = new Vector();

				fileLine = fileIn.readLine();
				StringTokenizer st = new StringTokenizer(fileLine, ",");

				while (st.hasMoreTokens()) {
					numOfColumns++;
					tableColumnNames[i].addElement(st.nextToken().trim());
					tableColumnTypes[i].addElement(st.nextToken().trim());
				}
			}

			fileIn.close();
		} catch (FileNotFoundException exFNF) {
			System.err.println("File doesn't exist");
		} catch (IOException exIO) {
			System.err.println("IOException");
		}
	}

	/**
	 * Method for creating a new database connection file. The newDBInfo String
	 * array holds the first size lines of the database connection file. These
	 * lines hold information about the database in question with connection
	 * information. The method trys to connect to the database in question and
	 * retrieve the database meta data (table names with their column names and
	 * column types.
	 * 
	 * @param DBFileName
	 *            Name of the new database connection file
	 */
	public Connection getConnection() {
		String sDriver = dbInfo[1];
		String sConnURL = dbInfo[2];
		String sUserName = dbInfo[3];
		String sPassword = dbInfo[4];

		try {
			Class.forName(sDriver);

			Connection conn = DriverManager.getConnection(sConnURL, sUserName,
					sPassword);

			return conn;
		} catch (ClassNotFoundException exClassNotFound) {
			System.err.println("Failed to load JDBC/ODBC driver.");
			return null;
		} catch (Exception exNoConn) {
			System.err.println("Unable to connect");
			return null;
		}
	}

	/**
	 * Returns the name of a table.
	 * 
	 * @param table
	 *            (0 to getNumOfTables() - 1)
	 * @return Name of table
	 */
	public String getTable(int table) {
		return (String) databaseTables.elementAt(table);
	}

	/**
	 * Returns the name of a column in a specified table.
	 * 
	 * @param table
	 *            (0 to getNumOfTables() - 1)
	 * @param column
	 *            (0 to getNumOfColumns (int table) - 1)
	 * @return name of column
	 */
	public String getColumnName(int table, int column) {
		return (String) tableColumnNames[table].elementAt(column);
	}

	/**
	 * Returns the type of a column in a specified table.
	 * 
	 * @param table
	 *            (0 to getNumOfTables() - 1)
	 * @param column
	 *            (0 to getNumOfColumns (int table) - 1)
	 * @return type of column
	 */
	public String getColumnType(int table, int column) {
		return (String) tableColumnTypes[table].elementAt(column);
	}

	/**
	 * Returns the number of tables in the current database connection.
	 * 
	 * @return number of tables
	 */
	public int getNumOfTables() {
		return numOfTables;
	}

	/**
	 * Returns the number of columns in the database connection.
	 * 
	 * @return total number of columns
	 */
	public int getNumOfColumns() {
		return numOfColumns;
	}

	/**
	 * Returns the number of columns in the database connection (overloaded).
	 * 
	 * @param table
	 *            (0 to getNumOfTables() - 1)
	 * @return number of columns in a specified table
	 */
	public int getNumOfColumns(int table) {
		return tableColumnNames[table].size();
	}

	/**
	 * Returns the name of the database (e.g. Access, Oracle etc).
	 * 
	 * @return name of database.
	 */
	public String getDatabaseName() {
		return dbInfo[0];
	}

	/**
	 * Returns the name of the database driver. (e.g.
	 * sun.jdbc.odbc.JdbcOdbcDriver)
	 * 
	 * @return database driver
	 */
	public String getDatabaseDriver() {
		return dbInfo[1];
	}

	/**
	 * Returns the connection URL e.g. jdbc:odbc:
	 * 
	 * @return connection URL
	 */
	public String getConnURL() {
		return dbInfo[2];
	}
}