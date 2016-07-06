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

//==============================================================================
//  Class : TablesAndAlias
/**
 *  This class holds table and alias information.  
 *
 *  @author   $Author:   Bob Marks (marksie531@yahoo.com)
 *  @version  $Revision:   1.0
 */
//==============================================================================

public class TablesAndAlias
{
  public static final int MAX_NUM_OF_TABLES_ALIAS = 15;

  private static int [] table;
  private static String [] alias;
  private static int numOfAlias;

  //============================================================================
  /**  Constructor which takes no parameters.  Sets up arrays to hold
    *  information about a tables and aliases.
    */
  //============================================================================
  public TablesAndAlias() {
    table = new int [MAX_NUM_OF_TABLES_ALIAS];
    alias = new String [MAX_NUM_OF_TABLES_ALIAS];
    numOfAlias = 0;
  }

  //============================================================================
  /**  Add a new table and alias to the class.
    *
    *  @param  newTable          New table number
    *  @param  newAlias          New Alias number
    */
  //============================================================================
  public void add (int newTable, String newAlias) {
    table [numOfAlias] = newTable;
    alias [numOfAlias] = newAlias;
    numOfAlias++;
  }

  //============================================================================
  /**  Overloaded version of add which reads all the data in from one String.
    *
    *  @param  lineFromFile  All the data is in one String e.g "1,Emp"
    *                        means table 1, alias "Emp".
    */
  //============================================================================
  public void add (String lineFromFile) {
    int index = lineFromFile.indexOf(",");
    try {
      add (
        Integer.parseInt (lineFromFile.substring(0, index)),
        lineFromFile.substring(index + 1)
      );
    }
    catch (NumberFormatException nfe) {}      // do nothing (don't add)
  }

  //============================================================================
  /**  Converts all the data in the arrays into a String which can be directly
    *  saved onto a file.
    *
    *  @return  returns all the query data as a String
    */
  //============================================================================
  public String toString () {
    StringBuffer temp = new StringBuffer ();
    for (int i = 0; i < numOfAlias; i++)
      temp.append(table [i] + "," + alias[i] + "\n");
    return temp.toString();
  }

  /**  Check to see if the same alias is used more than once.
    *  @return  true if valid set of tables/aliases.
    */
  public boolean valid () {
    boolean success = true;     // assume all different
    for (int i = 0; i < numOfAlias; i++)
      for (int j = i + 1; j < numOfAlias; j++)
        if (alias[i].toUpperCase().equals(alias[j].toUpperCase()))
          success = false;
    return success;
  }
  /**  Method to retrieve a table number.
    *  @param   index (0 to number of size() - 1)
    *  @return  returns table number
    */
  public int getTable (int index) {
    return table[index];
  }
  /**  Method to retrieve the alias.
    *  @param   index (0 to indexber of size() - 1)
    *  @return  returns alias indexber
    */
  public String getAlias (int index) {
    return alias[index];
  }
  /**  Retrieves </i>total</i> number of tables/aliases.
    *  @return  returns current number of total tables/aliases.
    */
  public int size () {
    return numOfAlias;
  }
}