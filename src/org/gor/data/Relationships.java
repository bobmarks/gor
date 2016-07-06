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

import java.util.*;

//==============================================================================
//  Class : Relationships
/**
 * This class holds relationship information. Each set of relationships consist
 * of a pair of aliases and columns to create a join.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
//==============================================================================
public class Relationships {
	
	public static final int MAX_NUM_OF_RELATIONSHIPS = 15;

	private int[] primaryAlias;
	private int[] primaryColumn;
	private int[] secondaryAlias;
	private int[] secondaryColumn;

	private int numOfRelationships;

	/**
	 * Constructor which takes no parameters. Sets up arrays to hold information
	 * about all the relationships of a reportfile.
	 */
	public Relationships() {
		primaryAlias = new int[MAX_NUM_OF_RELATIONSHIPS];
		primaryColumn = new int[MAX_NUM_OF_RELATIONSHIPS];
		secondaryAlias = new int[MAX_NUM_OF_RELATIONSHIPS];
		secondaryColumn = new int[MAX_NUM_OF_RELATIONSHIPS];

		// ultimately provide methods for reading these in.
		numOfRelationships = 0;
	}

	/**
	 * Adds a new set of relationships to the class.
	 * 
	 * @param integer
	 *            array (4 big) of relations
	 */
	public void add(int[] relationsInfo) {
		primaryAlias[numOfRelationships] = relationsInfo[0];
		primaryColumn[numOfRelationships] = relationsInfo[1];
		secondaryAlias[numOfRelationships] = relationsInfo[2];
		secondaryColumn[numOfRelationships] = relationsInfo[3];

		numOfRelationships++;
	}

	/**
	 * Adds a new set of relations from a String (read from a report file)
	 * 
	 * @param Line
	 *            from file.
	 */
	public void add(String lineFromFile) {
		StringTokenizer st = new StringTokenizer(lineFromFile);
		int[] temp = new int[4];

		try {
			for (int i = 0; i < 4; i++)
				temp[i] = Integer.parseInt(st.nextToken(","));
			add(temp);
		} catch (NumberFormatException exNFE) {
		} // don't add
	}

	/**
	 * Converts all the data in the arrays into a String which can be directly
	 * saved onto a report file.
	 * 
	 * @return returns all the query data as a String
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < numOfRelationships; i++)
			temp.append(primaryAlias[i] + "," + primaryColumn[i] + ","
					+ secondaryAlias[i] + "," + secondaryColumn[i] + "\n");
		return temp.toString();
	}

	/**
	 * Returns the primary alias of a relationships.
	 * 
	 * @param index
	 *            0 to number of relationships - 1
	 * @return returns primary alias of relationship
	 */
	public int getPrimaryAlias(int index) {
		return primaryAlias[index];
	}

	/**
	 * Returns the primary alias of a relationships.
	 * 
	 * @param index
	 *            0 to number of relationships - 1
	 * @return returns primary alias of relationship
	 */
	public int getPrimaryColumn(int index) {
		return primaryColumn[index];
	}

	/**
	 * Returns the secondary alias of a relationships.
	 * 
	 * @param index
	 *            0 to number of relationships - 1
	 * @return returns secondary alias of relationship
	 */
	public int getSecondaryAlias(int index) {
		return secondaryAlias[index];
	}

	/**
	 * Returns the secondary column of a relationships.
	 * 
	 * @param index
	 *            0 to number of relationships - 1
	 * @return returns secondary column of relationship
	 */
	public int getSecondaryColumn(int index) {
		return secondaryColumn[index];
	}

	/**
	 * Returns the number of total relationships.
	 * 
	 * @return returns the total number of relationships.
	 */
	public int size() {
		return numOfRelationships;
	}
}
