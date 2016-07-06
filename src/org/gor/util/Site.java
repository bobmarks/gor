//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpSession;

/**
 * This class has useful methods such as position of files on the server,
 * default locale, and priviledges of users.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class Site {
	
	private static final String ENV_GOR = "GOR";
	private static Locale defaultLocale = new Locale("en", "GB");

	// This may change at some stage.
	private static String databaseDir = null;
	private static String reportsDir = null;
	private static String systemDir = null;

	/**
	 * Initilise folders.
	 * 
	 * @throws IOException
	 */
	private static void init () throws IOException {
		if (databaseDir == null) {
			String envGorDir = System.getProperty (ENV_GOR);
			if (envGorDir == null)
				throw new IOException("\"GOR\" system property is not set.  Ensure this is set and points to the location of the gor data folder.");
			
		    File file = new File (envGorDir);
		
		    if (!file.exists()) {
		        throw new IOException("GOR data folder defined using GOR system property does not exist at location: " + file.getAbsolutePath());
		    }
		    
		    // Set up dirs.
		    Site.databaseDir = file.getAbsolutePath() + File.separator + "database" + File.separator;
		    Site.reportsDir = file.getAbsolutePath() + File.separator + "reports" + File.separator;
		    Site.systemDir = file.getAbsolutePath() + File.separator + "system" + File.separator;		    
		}        
	}
	
	/**
	 * Get database directory.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getDatabaseDir () throws IOException {
		Site.init ();
		return Site.databaseDir;
	}
	
	/**
	 * Get report directory.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getReportsDir () throws IOException {
		Site.init ();
		return Site.reportsDir;
	}
	
	/**
	 * Get system directory.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getSystemDir () throws IOException {
		Site.init ();
		return Site.systemDir;
	}
	
	/**
	 * Returns a list of files (e.g. database or report files)
	 * 
	 * @param directory
	 *            Directory of files.
	 * @return Returns list of files as an array of Strings
	 */
	public static String[] fileList(String directory) {
		File fd = new File(directory);
		return fd.list();
	}

	/**
	 * Returns the currect locale from two variables in the Session. These are
	 * language and country.
	 * 
	 * @param directory
	 *            Current session of a user
	 * @return Retrieves the locale from the Session
	 */
	public static Locale getLocale(HttpSession session) {
		String curLanguage = (String) session.getAttribute("language");
		String curCountry = (String) session.getAttribute("country");

		if (curLanguage != null && curCountry != null)
			return new Locale(curLanguage, curCountry);
		else
			return defaultLocale;
	}

	// Privileges
	// All users have the priviledge of opening and running a report
	// Additional priviledges are:
	// - editing a report
	// - creating a new report
	// - deleting a report
	// - create a new database connection
	// - viewing an existing database connection
	// - deleting an existing database connection
	// Privileges are stored in a single number. Bit operations are used to
	// determine if a particular priviledge is true or false. e.g. 5 means
	// edit report and delete report priviledges are true.

	/**
	 * Edit report priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean editReportPriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 1) != 0);
	}

	/**
	 * New database report priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean newReportPriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 2) != 0);
	}

	/**
	 * Delete database report priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean deleteReportPriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 4) != 0);
	}

	/**
	 * New database connection priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean newDatabasePriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 8) != 0);
	}

	/**
	 * Open database connection priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean openDatabaseConnPriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 16) != 0);
	}

	/**
	 * Delete database connection priviledge.
	 * 
	 * @param session
	 * @return
	 */
	public static boolean deleteDatabaseConnPriv(HttpSession session) {
		String privNum = (String) session.getAttribute("priv");
		return ((Integer.parseInt(privNum) & 32) != 0);
	}
}