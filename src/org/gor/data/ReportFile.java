//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

//  Package Name.
package org.gor.data;

//  Import Files

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.gor.util.Query;
import org.gor.util.Site;

/**
 * This file provides a means for handing a report file in the system. Report
 * files can be created, opened and updated using this class. Individual parts
 * of a report file can be retrieved such as TablesAndAlias, Relationships etc.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportFile {
	
	private String fileName;
	private String database, createdBy, createdDate, edittedBy;

	// all the classes that this report file can read in
	private TablesAndAlias tableAlias;
	private Relationships relations;
	private Query query;
	private Groups groups;
	private Labels labels;
	private Format format;

	/**
	 * This construcotr has no parameters and is used when create a new file.
	 */
	public ReportFile() {
	}

	/**
	 * This construcotr reads in a report file.
	 * 
	 * @param fileName
	 *            Name of a report filename.
	 */
	public ReportFile(String fileName) {
		this.fileName = fileName;
		open(fileName);
	}

	/**
	 * This construcotr opens a report file and populates all of the classes.
	 * e.g. TablesAlias.
	 * 
	 * @param fileName
	 *            Name of a report filename.
	 */
	public void open(String fileName) {
		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(Site.getReportsDir() + fileName));
			String fileLine; // use to read each line in file

			// read report file general info - database, creator, created data
			// etc.

			fileIn.readLine(); // [GENERAL_INFO]
			fileLine = fileIn.readLine(); // DATABASE=
			database = fileLine.substring(fileLine.indexOf('=') + 1);
			fileLine = fileIn.readLine(); // CREATED_BY=
			createdBy = fileLine.substring(fileLine.indexOf('=') + 1);
			fileLine = fileIn.readLine(); // CREATED_DATE
			createdDate = fileLine.substring(fileLine.indexOf('=') + 1);
			fileLine = fileIn.readLine(); // EDITTED_BY
			edittedBy = fileLine.substring(fileLine.indexOf('=') + 1);
			fileIn.readLine(); // blank

			// TablessAndAlias

			fileIn.readLine(); // [TABLES_ALIAS]
			tableAlias = new TablesAndAlias();

			do {
				fileLine = fileIn.readLine();
				if (!fileLine.equals(""))
					tableAlias.add(fileLine);
			} while (!fileLine.equals(""));

			// Relationships

			fileIn.readLine(); // [RELATIONSHIPS]
			relations = new Relationships();

			do {
				fileLine = fileIn.readLine();
				if (!fileLine.equals(""))
					relations.add(fileLine);
			} while (!fileLine.equals(""));

			// Query

			fileIn.readLine(); // [QUERY]
			query = new Query();

			do {
				fileLine = fileIn.readLine();
				if (!fileLine.equals(""))
					query.add(fileLine);
			} while (!fileLine.equals(""));

			// Groups

			fileIn.readLine(); // [GROUPS]
			groups = new Groups();

			do {
				fileLine = fileIn.readLine();
				if (!fileLine.equals(""))
					groups.add(fileLine);
			} while (!fileLine.equals(""));

			// Labels

			fileIn.readLine(); // [LABELS]
			labels = new Labels();

			try {
				fileLine = fileIn.readLine();
				String sDefaultLocale = fileLine.substring(fileLine
						.indexOf('=') + 1);

				int intDefaultLocale = Integer.parseInt(sDefaultLocale);
				labels.setDefaultLocale(intDefaultLocale);

				fileIn.readLine();
				boolean labelsExist = true;

				while (labelsExist) {
					// check and see if file has got to the format section yet
					if (!fileIn.readLine().equals("[FORMAT]")) {
						fileLine = fileIn.readLine();
						int locale = Integer.parseInt(fileLine
								.substring(fileLine.indexOf('=') + 1));
						fileLine = fileIn.readLine();
						String title = fileLine
								.substring(fileLine.indexOf('=') + 1);
						fileLine = fileIn.readLine();
						String subTitle = fileLine.substring(fileLine
								.indexOf('=') + 1);
						fileLine = fileIn.readLine();
						String footer = fileLine.substring(fileLine
								.indexOf('=') + 1);

						labels.createNewLabelData(locale, title, subTitle,
								footer);

						fileLine = fileIn.readLine();
						while (!fileLine.equals("")) {
							labels.addLabel(fileLine);
							fileLine = fileIn.readLine();
						}
						labels.addCurLabelToList();
					} else
						labelsExist = false;
				}
			} catch (NumberFormatException exNFE) {
			} // do nothing

			// fileIn.readLine ();

			// Format

			// fileIn.readLine (); // [FORMAT]
			format = new Format();

			fileLine = fileIn.readLine();
			String newPageOrientation = fileLine.substring(fileLine
					.indexOf('=') + 1);
			String newFormatData = fileIn.readLine();

			format.setAll(newFormatData, newPageOrientation);

			// ============================================================
			// End of file - [END]
			// ============================================================

			fileIn.close();
		} catch (FileNotFoundException exFNF) {
			System.err.println("File doesn't exist");
		} catch (IOException exIO) {
			System.err.println("IOException");
		}
	}

	//==========================================================================
	// ==
	/**
	 * This method creates a blank report file. It only requires certain fields
	 * such as file name, database and user.
	 * 
	 * @param newFileName
	 *            Name of the report file name.
	 * @param database
	 *            Name of the database connection.
	 * @param user
	 *            Name of user who created report file.
	 * @return Returns true if file created sucessfully.
	 */
	//==========================================================================
	// ==
	public boolean createFile(String newFileName, String database, String user) {
		boolean sucess = true; // assume sucesss
		try {
			BufferedWriter fileout = new BufferedWriter(new FileWriter(
					Site.getReportsDir() + newFileName));

			Date d = new Date();

			fileout.write("[GENERAL_INFO]\n" + "DATABASE=" + database + "\n"
					+ "CREATED_BY=" + user + "\n" + "CREATED_DATE="
					+ d.toString() + "\n" + "EDITTED_BY=" + user + "\n\n"
					+ "[TABLES_ALIAS]\n\n" + "[RELATIONSHIPS]\n\n"
					+ "[QUERY]\n\n" + "[GROUPS]\n\n" + "[LABELS]\n"
					+ "DEFAULT_LOCALE=0\n\n" + "[FORMAT]\n"
					+ "PAGE_ORIENTATION=0\n" + "5,5,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,0,0,0,0,0,8,1,0,0,0," + "5,2,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,1,0,0,0,0,2,1,0,0,1," + "5,2,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,1,0,0,0,0,2,1,0,0,1," + "5,2,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,1,0,0,0,0,2,1,0,0,1," + "5,2,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,1,0,0,0,0,2,1,0,0,1," + "5,2,0,0,0,0,0,0,1,0,0,0,"
					+ "5,2,1,0,0,0,0,2,1,0,0,1," + "5,2,1,0,0,0,0,1,1,0,0,1,"
					+ "5,2,0,0,0,0,0,1,1,0,0,1," + "5,2,0,0,0,0,8,0,1,0,0,0,"
					+ "1,0,0,0,0,0\n\n" + "[END]\n");
			fileout.close();

			// open file (this refreshes all variables)
			open(newFileName);
		} catch (Exception exFile) {
			System.err.print("Problem creating file");
			sucess = false;
		}
		;

		return sucess;
	}

	/**
	 * Updates the [TABLES_ALIAS] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newTablesAliases
	 *            New set of tables and aliases.
	 */
	public static void update(String report, TablesAndAlias newTablesAliases) {
		update(report, newTablesAliases, "[TABLES_ALIAS]", "[RELATIONSHIPS]");
	}

	/**
	 * Updates the [RELATIONSHIPS] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newRelationships
	 *            New set of relationships.
	 */
	public static void update(String report, Relationships newRelationships) {
		update(report, newRelationships, "[RELATIONSHIPS]", "[QUERY]");
	}

	/**
	 * Updates the [QUERY] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newQuery
	 *            New set of query information.
	 */
	public static void update(String report, Query newQuery) {
		update(report, newQuery, "[QUERY]", "[GROUPS]");
	}

	/**
	 * Updates the [GROUPS] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newGroups
	 *            New set of group information
	 */
	public static void update(String report, Groups newGroups) {
		update(report, newGroups, "[GROUPS]", "[LABELS]");
	}

	/**
	 * Updates the [LABELS] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newLabels
	 *            New set of label information
	 */
	public static void update(String report, Labels newLabels) {
		update(report, newLabels, "[LABELS]", "[FORMAT]");
	}

	/**
	 * Updates the [FORMAT] section of a report file.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param newFormat
	 *            New set of format information
	 */
	public static void update(String report, Format newFormat) {
		update(report, newFormat, "[FORMAT]", "[END]");
	}

	/**
	 * Updates a report file is passed to it.
	 * 
	 * @param report
	 *            This is the name of a report.
	 * @param objectToUpdate
	 *            This can be any of the following objects: TablesAndAlias,
	 *            Relationships, Query, Groups, Labels, Format (all of these
	 *            have a .toString() method)
	 * @param start
	 *            Where to start writing in a file .e.g [QUERY]
	 * @param end
	 *            Where to end writing in a file e.g. [GROUPS]
	 */
	private static void update(String report, Object objectToUpdate,
			String start, String end) {
		StringBuffer top = new StringBuffer();
		StringBuffer bottom = new StringBuffer();

		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(
					Site.getReportsDir() + report));
			String fileLine; // use to read each line in file

			do {
				fileLine = fileIn.readLine();
				top.append(fileLine + "\n");
			} while (!fileLine.equals(start));

			do {
				fileLine = fileIn.readLine();
			} while (!fileLine.equals(end));
			bottom.append("\n" + fileLine + "\n");

			if (!end.equals("[END]")) {
				do {
					fileLine = fileIn.readLine();
					bottom.append(fileLine + "\n");
				} while (!fileLine.equals("[END]"));
				fileIn.close();
			}

			// -------------------------
			try {
				BufferedWriter fileout = new BufferedWriter(new FileWriter(
						Site.getReportsDir() + report));

				fileout.write(top.toString() + objectToUpdate
						+ bottom.toString());
				fileout.close();
			} catch (Exception exFile) {
				System.err.print("Problem creating file");
			}
			;
		} catch (IOException exIO) {
			System.err.println("IOException");
		}
	}

	/**
	 * Updates a single text value in a report file. Used mainly to update the
	 * EDITTED_BY=user field in a report file to lock a file.
	 * 
	 * @param textName
	 *            This is the name of a text
	 * @param value
	 *            This is the new value of the text
	 */
	private void updateText(String textName, String value) {
		StringBuffer fileSB = new StringBuffer();

		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(
					Site.getReportsDir() + fileName));
			String fileLine, curTextName; // use to read each line in file
			int pos;

			do {
				fileLine = fileIn.readLine();
				if (fileLine != null) {
					pos = fileLine.indexOf('=');
					curTextName = "";

					if (pos != -1)
						curTextName = fileLine.substring(0, pos);

					if (curTextName.equals(textName))
						fileSB.append(textName + "=" + value + "\n");
					else
						fileSB.append(fileLine + "\n");
				}
			} while (fileLine != null);
			fileIn.close();

			// -------------------------
			try {
				BufferedWriter fileout = new BufferedWriter(new FileWriter(Site.getReportsDir() + fileName));

				fileout.write(fileSB.toString());
				fileout.close();
			} catch (Exception exFile) {
				System.err.print("Problem creating file");
			}
			;
		} catch (IOException exIO) {
			System.err.println("IOException");
		}
	}

	/**
	 * Returns a TablesAndAlias class read in from a report file.
	 * 
	 * @return TablesAndAlias
	 */
	public TablesAndAlias getTablesAndAlias() {
		return tableAlias;
	}

	/**
	 * Returns a Relationships class read in from a report file.
	 * 
	 * @return Relationships
	 */
	public Relationships getRelationships() {
		return relations;
	}

	/**
	 * Returns a Query class read in from a report file.
	 * 
	 * @return Query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Returns a Groups class read in from a report file.
	 * 
	 * @return Groups
	 */
	public Groups getGroups() {
		return groups;
	}

	/**
	 * Returns a Labels class read in from a report file.
	 * 
	 * @return Labels
	 */
	public Labels getLabels() {
		return labels;
	}

	/**
	 * Returns a Format class read in from a report file.
	 * 
	 * @return Format
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Returns the name of a database connection.
	 * 
	 * @return database connection name.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Returns name of user who created a report file.
	 * 
	 * @return user who created file.
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Returns data report file was created.
	 * 
	 * @return date report was created.
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * Returns data report file was created.
	 * 
	 * @return date report was created.
	 */
	public String getEdittedBy() {
		return edittedBy;
	}

	/**
	 * Locks a reportfile by populating the EDITTED_BY field
	 * 
	 * @param user
	 *            Name of user currently editting a report file.
	 */
	public void editLock(String user) {
		this.edittedBy = user;
		updateText("EDITTED_BY", user); // update file
	}

	/**
	 * Unlocks a reportfile by blanking the EDITTED_BY field in report file.
	 */
	public void editUnLock() {
		this.edittedBy = "";
		updateText("EDITTED_BY", ""); // update file
	}
}
