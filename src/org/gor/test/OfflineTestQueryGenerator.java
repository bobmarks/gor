//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.test;

import org.gor.util.QueryGenerator;

/**
 * This little program performs an offline test on the QueryGenerator Class and
 * returns an SQL String
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class OfflineTestQueryGenerator {

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		QueryGenerator queryGenerator = new QueryGenerator("northwind.gor");

		System.out.println(queryGenerator.getSQL());
	}
}