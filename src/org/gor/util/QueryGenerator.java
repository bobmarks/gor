//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.sql.Types;

import org.gor.data.DatabaseConnectionFile;
import org.gor.data.Groups;
import org.gor.data.Relationships;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;

/**
 * This important class uses the TablesAndAliases, Relationships,
 * ColumnsAndConditions and Groups information from a report file and creates an
 * SQL statement from it. This SQL statement is critical for running a query
 * from the database.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class QueryGenerator {

	// This strong contains the full SQL statement
	private String fullSQLString;
	// This can be accessed by the ReportGenerator class
	private String[] sortedVisibleColumns;
	private int[] sortedQueryAliasList;
	private int[] sortedQueryColumnList;

	/**
	 * This the constructor to the QueryGenerator class. It takes all the
	 * information from the gor files and converts it into a SQL String which
	 * will run against a database. If grouping levels are being used then
	 * the columns are ordered. A note of these ordered visible columns are
	 * stored in a String array.
	 * 
	 * @param reportFileName
	 */
	public QueryGenerator(String reportFileName) {
		// read in report file, database connection file and retrieve tables and
		// aliases, relationships, columns and conditions (query) and groups.
		ReportFile reportFile = new ReportFile(reportFileName);
		DatabaseConnectionFile dcf = new DatabaseConnectionFile(reportFile
				.getDatabase());

		TablesAndAlias tablesAliases = reportFile.getTablesAndAlias();
		Relationships relationships = reportFile.getRelationships();
		Query query = reportFile.getQuery();
		Groups groups = reportFile.getGroups();

		// Declare StringBuffers which will make up complete SQL String
		StringBuffer selectSQL = new StringBuffer();
		StringBuffer fromSQL = new StringBuffer();
		StringBuffer whereSQL = new StringBuffer();
		StringBuffer orderBySQL = new StringBuffer();

		// the selectSQL and fromSQL MUST be created but the whereSQL and
		// orderBySQL
		// are optional. Presume initially they won't be used.
		boolean useWhereSQL = false;
		boolean useOrderBySQL = false;

		// Read number of aliases, columns, etc in file as these will be used
		// extensivly throughout method.
		int numOfAliases = tablesAliases.size();
		int numOfRelationships = relationships.size();
		int numOfVisibleColumns = query.getNumOfVisibile();
		int numOfConditions = query.getNumOfConditions();
		int numOfSortedColumns = query.getNumOfSorts();
		int numOfGroups = groups.size();

		sortedVisibleColumns = new String[numOfVisibleColumns];

		// 1. Create selectSQL i.e. "SELECT " + selectSQL + " FROM " ....
		//
		// This is the first part of an SQL statement. It involves creating a
		// String using the visible columns (show is ticked) from the query
		// section. If groups are declared then this affects the ordering or the
		// columns. The group with the highest priority goes first. Note groups
		// are visible columns also.
		//
		// syntax: queryAlais1.queryColumn1, queryAlias2.queryColumn2, ... , n

		// create
		sortedQueryAliasList = new int[numOfVisibleColumns];
		sortedQueryColumnList = new int[numOfVisibleColumns];

		int intCol, intQueryAlias, intQueryTable, intQueryColumn;
		String sAliasAndColumn;

		// populate group array first of all
		for (int g = 0; g < numOfGroups; g++) {
			sortedQueryAliasList[g] = groups.getGroupAlias(g);
			sortedQueryColumnList[g] = groups.getGroupColumn(g);
		}
		// fill with remaining visible columns (check not a group)
		int curCol = numOfGroups;
		for (int i = 0; i < numOfVisibleColumns; i++) {
			intCol = query.getIndexFromVisible(i);
			intQueryAlias = query.getAlias(intCol);
			intQueryColumn = query.getColumn(intCol);

			// check it hasn't been added
			boolean colIsntGroup = true;
			for (int g = 0; g < numOfGroups; g++)
				if (groups.getGroupAlias(g) == intQueryAlias
						&& groups.getGroupColumn(g) == intQueryColumn)
					colIsntGroup = false;

			// if column isn't a group then add to list
			if (colIsntGroup) {
				sortedQueryAliasList[curCol] = intQueryAlias;
				sortedQueryColumnList[curCol] = intQueryColumn;
				curCol++;
			}
		}

		// create sql string
		for (int i = 0; i < numOfVisibleColumns; i++) {
			intQueryAlias = sortedQueryAliasList[i];
			intQueryTable = tablesAliases.getTable(intQueryAlias);
			intQueryColumn = sortedQueryColumnList[i];

			sAliasAndColumn = tablesAliases.getAlias(intQueryAlias) + "."
					+ dcf.getColumnName(intQueryTable, intQueryColumn);

			selectSQL.append(sAliasAndColumn);
			// add to array
			sortedVisibleColumns[i] = sAliasAndColumn;
			if (i < numOfVisibleColumns - 1)
				selectSQL.append(", ");
		}

		// 2. Create fromSQL i.e. ... " FROM " + fromSQL + " WHERE " ....
		//
		// This is the second part of an SQL statement. It involves creating a
		// String using the tables and aliases from the tables section. This is
		// a fairly straight forward part.
		//
		// syntax: table1 AS alias1, table2 AS alias2, ... , n

		int intTable;
		String sTable;

		for (int i = 0; i < numOfAliases; i++) {
			intTable = tablesAliases.getTable(i);
			sTable = dcf.getTable(intTable);

			fromSQL.append(sTable + " AS " + tablesAliases.getAlias(i));
			if (i < numOfAliases - 1)
				fromSQL.append(", ");
		}

		// 3. Create whereSQL i.e. ... " WHERE " + whereSQL + " ORDER BY " ....
		//
		// This is the third part of an SQL statement (optional). It is
		// probobally
		// the most complex part. If any relationships have been declared in the
		// system then these are declared.
		//
		// syntax:
		// parentAlias1.parentColumn1 = childAlias1.childColumn2 AND ... n
		//
		// The second part of the where statement involves creating a condition
		// from a alias and column, a condition type and a condition value.
		// e.g WHERE P.PART_ID < 939
		//
		// synatax:
		// queryAlias1.queryColumn1 conditionType1 conditionValue1 AND ... n

		int intPrimaryAlias, intPrimaryTable, intPrimaryColumn;
		int intSecondaryAlias, intSecondaryTable, intSecondaryColumn;
		String sPrimaryAliasAndColumn, sSecondaryAliasAndColumn;

		// check to see if whereSQL is going to be used.
		if (numOfConditions != 0 || numOfRelationships != 0)
			useWhereSQL = true;

		// Do relations first (joins using where conditions)

		for (int r = 0; r < numOfRelationships; r++) {
			intPrimaryAlias = relationships.getPrimaryAlias(r);
			intPrimaryTable = tablesAliases.getTable(intPrimaryAlias);
			intPrimaryColumn = relationships.getPrimaryColumn(r);

			intSecondaryAlias = relationships.getSecondaryAlias(r);
			intSecondaryTable = tablesAliases.getTable(intSecondaryAlias);
			intSecondaryColumn = relationships.getSecondaryColumn(r);

			sPrimaryAliasAndColumn = tablesAliases.getAlias(intPrimaryAlias)
					+ "."
					+ dcf.getColumnName(intPrimaryTable, intPrimaryColumn);

			sSecondaryAliasAndColumn = tablesAliases
					.getAlias(intSecondaryAlias)
					+ "."
					+ dcf.getColumnName(intSecondaryTable, intSecondaryColumn);

			whereSQL.append(sPrimaryAliasAndColumn + "="
					+ sSecondaryAliasAndColumn);
			if (r < numOfRelationships - 1)
				whereSQL.append(" AND ");
		}

		// Then do conditions

		String[] conTypeList = { "-", "=", "<>", "<", ">", "<=", ">=" };

		if (numOfConditions != 0 && numOfRelationships != 0)
			whereSQL.append(" AND ");

		for (int c = 0; c < numOfConditions; c++) {
			intCol = query.getIndexFromCondition(c);
			intQueryAlias = query.getAlias(intCol);
			intQueryTable = tablesAliases.getTable(intQueryAlias);
			intQueryColumn = query.getColumn(intCol);

			sAliasAndColumn = tablesAliases.getAlias(intQueryAlias) + "."
					+ dcf.getColumnName(intQueryTable, intQueryColumn);

			String conType = conTypeList[query.getConditionType(intCol)];

			// check database connection file to see if the column is text-based
			// or number-based. If it text based wrap value with single quotes.
			int valueType = Integer.parseInt(dcf.getColumnType(intQueryTable,
					intQueryColumn));

			String conValue = "";

			if (valueType == Types.CHAR || valueType == Types.DATE || // do some
					// locale
					// stuff
					// for
					// this
					// date
					// bit
					valueType == Types.TIME || valueType == Types.VARCHAR)
				conValue = "'" + query.getConditionValue(intCol) + "'";
			else
				conValue = query.getConditionValue(intCol);

			whereSQL.append(sAliasAndColumn + conType + conValue);
			if (c < numOfConditions - 1) // sort this out.
				whereSQL.append(" AND ");
		}

		// Create orderSQL i.e. ... " ORDER BY " + orderBySQL
		//
		// This is the last part of an SQL statement. It involves creating a
		// String using the sorted columns from the query section.
		// If groups are declared then this affects the ordering or the
		// columns. This section is quite similar to then selectSQL section.
		// The group with the highest priority goes first etc.
		//
		// syntax: queryAlais1.queryColumn1, queryAlias2.queryColumn2, ... , n
		//
		// tempQueryAliasList and tempQueryColumnList are to be used again to
		// create
		// the ordered alias and column. (from selectSQL section).

		// introduce new column to store sort (1 - asc, 2 - desc). size is num
		// of
		int[] sortedOrderBySortNum = new int[numOfSortedColumns + numOfGroups];
		int[] sortedOrderByAliases = new int[numOfSortedColumns + numOfGroups];
		int[] sortedOrderByColumns = new int[numOfSortedColumns + numOfGroups];
		int index, intSort;

		for (int g = 0; g < numOfGroups; g++) {
			index = query.getIndex(groups.getGroupAlias(g), groups
					.getGroupColumn(g));
			intSort = query.getSort(index);

			if (intSort == 0)
				intSort = 1; // asc
			sortedOrderBySortNum[g] = intSort;
			sortedOrderByAliases[g] = groups.getGroupAlias(g);
			sortedOrderByColumns[g] = groups.getGroupColumn(g);
		}

		// Groups will stay the same (first part, but the secondary part is
		// different (sortedColumn as opposed to visible columns)
		// fill with remaining visible columns (check not a group)

		curCol = numOfGroups; // reset curCol
		for (int i = 0; i < numOfSortedColumns; i++) {
			intCol = query.getIndexFromSort(i);
			intQueryAlias = query.getAlias(intCol);
			intQueryColumn = query.getColumn(intCol);

			// check it hasn't been added
			boolean colIsntGroup = true;
			for (int g = 0; g < numOfGroups; g++)
				if (groups.getGroupAlias(g) == intQueryAlias
						&& groups.getGroupColumn(g) == intQueryColumn)
					colIsntGroup = false;

			// if column isn't a group then add to list
			if (colIsntGroup) {
				sortedOrderByAliases[curCol] = intQueryAlias;
				sortedOrderByColumns[curCol] = intQueryColumn;
				sortedOrderBySortNum[curCol] = query.getSort(intCol);
				curCol++;
			}
			// get sort
		}

		// create sql string
		numOfSortedColumns = curCol;
		if (numOfSortedColumns > 0)
			useOrderBySQL = true;

		String[] sortDesc = { "", "ASC", "DESC" }; // textual representation
		for (int i = 0; i < numOfSortedColumns; i++) {
			intQueryAlias = sortedOrderByAliases[i];
			intQueryTable = tablesAliases.getTable(intQueryAlias);
			intQueryColumn = sortedOrderByColumns[i];
			intSort = sortedOrderBySortNum[i];

			sAliasAndColumn = tablesAliases.getAlias(intQueryAlias) + "."
					+ dcf.getColumnName(intQueryTable, intQueryColumn);

			orderBySQL.append(sAliasAndColumn + " " + sortDesc[intSort]);
			if (i < numOfSortedColumns - 1)
				orderBySQL.append(", ");
		}

		// Compose final SQL string
		StringBuffer fullSQL = new StringBuffer("SELECT "
				+ selectSQL.toString() + " FROM " + fromSQL.toString());

		if (useWhereSQL)
			fullSQL.append(" WHERE " + whereSQL.toString());
		if (useOrderBySQL)
			fullSQL.append(" ORDER BY " + orderBySQL.toString());

		this.fullSQLString = fullSQL.toString();
	}
	
	/**
	 * Return the SQL.
	 * 
	 * @return
	 */
	public String getSQL() {
		return fullSQLString;
	}

	/**
	 * Return sorted visible column names.
	 * 
	 * @param index
	 * @return
	 */
	public String getSortedVisibleColumnName(int index) {
		return sortedVisibleColumns[index];
	}

	/**
	 * Return sorted query alias.
	 * 
	 * @param index
	 * @return
	 */
	public int getSortedQueryAlias(int index) {
		return sortedQueryAliasList[index];
	}

	/**
	 * Return sorted query column.
	 * 
	 * @param index
	 * @return
	 */
	public int getSortedQueryColumn(int index) {
		return sortedQueryColumnList[index];
	}
}