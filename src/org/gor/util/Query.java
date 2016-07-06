//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.util.*;

/**
 * This class holds query information. There must be an alias and column
 * declared. Other optional things are show a column, sorts, condition types
 * (e.g. >, <, =, >= etc) and condition values.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class Query {
	
	// Constants
	
	// declare constant array to show condition types
	public static final String[] sortTypes = { "-", "ASC", "DESC" };
	// declare constant array to show condition types
	public static final String[] conditionTypes = { "-", "=", "<>", "<", ">",
			"<=", ">=" };
	public static final String[] conditionTypesHtml = { "-", "=", "&lt;&gt;",
			"&lt;", "&gt;", "&lt;=", "&gt;=" };

	
	// maximum number of columns
	public static final int MAX_NUM_OF_COLS = 20;

	// declare arrays to hold all the different data
	private boolean[] show;
	private int[] alias;
	private int[] column;
	private int[] sort;
	private int[] condition;
	private String[] value;

	// total number of columns
	private int numOfColumns;

	// holds number of visibile columns, condition columns and sorted columns
	private int numOfVisibileColumns;
	private int numOfConditions;
	private int numOfSorts;

	// declare point arrays to visible, condition and sorted columns
	private int[] visibleColumns;
	private int[] conditionColumns;
	private int[] sortColumns;

	/**
	 * Constructor which takes no parameters. Sets up arrays to hold information
	 * about a query depending on the maximum number of columns
	 */
	public Query() {
		show = new boolean[MAX_NUM_OF_COLS];
		alias = new int[MAX_NUM_OF_COLS];
		column = new int[MAX_NUM_OF_COLS];
		sort = new int[MAX_NUM_OF_COLS];
		condition = new int[MAX_NUM_OF_COLS];
		value = new String[MAX_NUM_OF_COLS];

		visibleColumns = new int[MAX_NUM_OF_COLS];
		conditionColumns = new int[MAX_NUM_OF_COLS];
		sortColumns = new int[MAX_NUM_OF_COLS];

		numOfColumns = 0;
		numOfVisibileColumns = 0;
		numOfConditions = 0;
		numOfSorts = 0;
	}

	/**
	 * Add a new column and condition to a query.
	 * 
	 * @param newShow
	 *            if true display in resultset
	 * @param newAlias
	 *            alias number
	 * @param newColumn
	 *            column number
	 * @param newSort
	 *            0 = None, 1 = ASC, 2 = DESC
	 * @param newCondition
	 *            0 = None, 1 = '=', 2 = '<>', ... etc
	 * @param newValue
	 *            value of condition
	 */
	public void add(boolean newShow, int newAlias, int newColumn, int newSort,
			int newCondition, String newValue) {
		show[numOfColumns] = newShow;
		alias[numOfColumns] = newAlias;
		column[numOfColumns] = newColumn;
		sort[numOfColumns] = newSort;
		condition[numOfColumns] = newCondition;
		value[numOfColumns] = newValue;

		// check if variable is
		if (newShow)
			visibleColumns[numOfVisibileColumns++] = numOfColumns;
		if (newCondition != 0)
			conditionColumns[numOfConditions++] = numOfColumns;
		if (newSort != 0)
			sortColumns[numOfSorts++] = numOfColumns;

		numOfColumns++;
	}

	/**
	 * Overloaded version of add which reads all the data in from one String.
	 * 
	 * @param lineFromFile
	 *            All the data is in one String e.g 1,3,6,0,3,red means show
	 *            column, alias 3, column 6, no sort, condition type "<",
	 *            condition value "red"
	 */
	public void add(String lineFromFile) {
		StringTokenizer st = new StringTokenizer(lineFromFile);
		try {
			boolean sh = st.nextToken(",").equals("1");
			int al = Integer.parseInt(st.nextToken(","));
			int col = Integer.parseInt(st.nextToken(","));
			int sort = Integer.parseInt(st.nextToken(","));
			int con = Integer.parseInt(st.nextToken(","));
			String val = ""; // values usually empty
			if (st.hasMoreElements())
				val = st.nextToken(",");

			add(sh, al, col, sort, con, val);
		} catch (NumberFormatException exNFE) {
		} // don't add
	}

	/**
	 * Converts all the data in the arrays into a String which can be directly
	 * saved onto a file.
	 * 
	 * @return returns all the query data as a String
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < numOfColumns; i++) {
			if (show[i])
				temp.append("1,");
			else
				temp.append("0,");
			temp.append(alias[i] + ",");
			temp.append(column[i] + ",");
			temp.append(sort[i] + ",");
			temp.append(condition[i] + ",");
			temp.append(value[i] + "\n");
		}
		return temp.toString();
	}

	/**
	 * Method to show if a particular column is to be displayed or not.
	 * 
	 * @param index
	 *            (0 to number of size() - 1)
	 * @return returns true / false depending if a column is to be displayed.
	 */
	public boolean showColumn(int index) {
		return show[index];
	}

	/**
	 * Method to retrieve an alias number.
	 * 
	 * @param index
	 *            (0 to number of size() - 1)
	 * @return returns alias number
	 */
	public int getAlias(int index) {
		return alias[index];
	}

	/**
	 * Method to retrieve a column number.
	 * 
	 * @param index
	 *            (0 to number of size() - 1)
	 * @return returns column number
	 */
	public int getColumn(int index) {
		return column[index];
	}

	/**
	 * Searches for an index using values of an alias and column
	 * 
	 * @param alias
	 *            number
	 * @param column
	 *            number
	 * @return index position if found (0 to size() - 1), -1 if not found.
	 */
	public int getIndex(int alias, int column) {
		// search through all the columns
		for (int i = 0; i < numOfColumns; i++)
			// if match exists then return number in loop
			if (alias == this.alias[i] && column == this.column[i])
				return i;

		return -1;
	}

	/**
	 * Retrieves a sort value.
	 * 
	 * @param index
	 *            , range is 0 to size() - 1
	 * @return returns sort type number. 0 = None, 1 = ASC, 2 = DESC
	 */
	public int getSort(int index) {
		return sort[index];
	}

	/**
	 * Retrieves a condition type.
	 * 
	 * @param index
	 *            , range is 0 to size() - 1
	 * @return returns condition type number. Numbers mean: 0 = "-", 1 = "=", 2
	 *         = "<>", 3 = "<", 4 = ">", 5 = "<=", 6 = ">="
	 */
	public int getConditionType(int index) {
		return condition[index];
	}

	/**
	 * Retrieves condition value.
	 * 
	 * @param index
	 *            , range is 0 to size() - 1
	 * @return returns condition value (String)
	 */
	public String getConditionValue(int index) {
		return value[index];
	}

	/**
	 * Retrieves </i>total</i> number of columns.
	 * 
	 * @return returns current number of total columns
	 */
	public int size() {
		return numOfColumns;
	}

	/**
	 * Method to retrieve number of <i>visible</i>columns in a query.
	 * 
	 * @return returns number of visible columns (columns where show is true)
	 */
	public int getNumOfVisibile() {
		return numOfVisibileColumns;
	}

	/**
	 * Method to retrieve number of columns in a query which have a condition.
	 * 
	 * @return returns number of columns (columns where condition <> 0)
	 */
	public int getNumOfConditions() {
		return numOfConditions;
	}

	/**
	 * Method to retrieve number of columns in a query which are being sorted.
	 * 
	 * @return returns number of columns (columns where sort <> 0)
	 */
	public int getNumOfSorts() {
		return numOfSorts;
	}

	/**
	 * Retrieve index from visible columns pointer array. Eg. query of size 7
	 * with 5 columns visible (show = true). Visible column array could look
	 * something like {0,1,3,4,6};
	 * 
	 * @param index
	 *            (0 to getNumOfVisibile() - 1).
	 * @return returns index.
	 */
	public int getIndexFromVisible(int index) {
		return visibleColumns[index];
	}

	/**
	 * Retrieve index from condition columns pointer array. Similar in principle
	 * to getIndexFromVisible().
	 * 
	 * @param index
	 *            (0 to getNumOfConditions() - 1).
	 * @return returns index.
	 */
	public int getIndexFromCondition(int index) {
		return conditionColumns[index];
	}

	/**
	 * Retrieve index from show columns pointer array. Similar in principle to
	 * getIndexFromVisible().
	 * 
	 * @param index
	 *            (0 to getNumOfSorts() - 1).
	 * @return returns index.
	 */
	public int getIndexFromSort(int index) {
		return sortColumns[index];
	}
}
