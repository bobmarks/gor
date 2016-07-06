//==============================================================================
//
//  Generic Online Reporting
//
//	The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

/**
 * This class provides an interface to all the group information.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class Groups {

	public static final int MAX_NUM_OF_GROUPS = 5;

	private int[] groupAlias, groupColumn;
	private int numOfGroups;

	/**
	 * Constructor which takes no parameters. Sets up arrays to hold information
	 * about a groups.
	 */
	public Groups() {
		groupAlias = new int[MAX_NUM_OF_GROUPS];
		groupColumn = new int[MAX_NUM_OF_GROUPS];

		numOfGroups = 0;
	}

	/**
	 * Adds a new group
	 * 
	 * @param newGroupAlias
	 *            alias number of a visible column ...
	 * @param newGroupColumn
	 *            ... and its column number
	 */
	public void add(int newGroupAlias, int newGroupColumn) {
		groupAlias[numOfGroups] = newGroupAlias;
		groupColumn[numOfGroups] = newGroupColumn;
		numOfGroups++;
	}

	/**
	 * Overloaded version of add which reads all the data in from one String.
	 * 
	 * @param line
	 *            All the data is in one String e.g 1,3 means alias 1, column 3
	 */
	public void add(String line) {
		int pos = line.lastIndexOf(',');
		String num1 = line.substring(0, pos);
		String num2 = line.substring(pos + 1);

		try {
			add(Integer.parseInt(num1), Integer.parseInt(num2));
		} catch (NumberFormatException exNFE) {} // no nothing (don't add)
	}

	/**
	 * Converts all the data in the arrays into a String which can be directly
	 * saved onto a file.
	 * 
	 * @return returns all the query data as a String
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < numOfGroups; i++)
			temp.append(groupAlias[i] + "," + groupColumn[i] + "\n");
		return temp.toString();
	}

	/**
	 * Check to see if the same alias.column is used more than once.
	 * 
	 * @return true if valid set of groups.
	 */
	public boolean valid() {
		boolean success = true; // assume all different
		for (int i = 0; i < numOfGroups; i++)
			for (int j = i + 1; j < numOfGroups; j++)
				if (groupAlias[i] == groupAlias[j]
						&& groupColumn[i] == groupColumn[j])
					success = false;
		return success;
	}

	/**
	 * Returns the alias number for a specified group.
	 * 
	 * @return alias number (0 to number of aliases - 1)
	 */
	public int getGroupAlias(int num) {
		return groupAlias[num];
	}

	/**
	 * Returns the column number for a specified group.
	 * 
	 * @return column number (0 to number of columns in table - 1)
	 */
	public int getGroupColumn(int num) {
		return groupColumn[num];
	}

	/**
	 * Returns number of current groups.
	 * 
	 * @return number of current groups.
	 */
	public int size() {
		return numOfGroups;
	}
}