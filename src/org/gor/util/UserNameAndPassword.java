//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

// This little file will be used for offline testing
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class provides a method for creating a new entry in the user.dat file.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class UserNameAndPassword {

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String userName, password;
		boolean editReportPriv, newReportPriv, deleteReportPriv;
		boolean newDatabasePriv, openDatabaseConnPriv, deleteDatabaseConnPriv;

		System.out.println("---------------------------------------------");
		System.out.println("    Generic Online Reporting - User Admin    ");
		System.out.println("---------------------------------------------\n");

		System.out.print("Username: ");
		System.out.flush();
		userName = GetString();
		System.out.print("Password: ");
		System.out.flush();
		password = GetString();

		// read priviledges straight into booleans
		System.out.print("Privilege - Edit Report (y/n): ");
		System.out.flush();
		editReportPriv = (GetString().equalsIgnoreCase("Y"));
		System.out.print("Privilege - New Report (y/n): ");
		System.out.flush();
		newReportPriv = (GetString().equalsIgnoreCase("Y"));
		System.out.print("Privilege - Delete Report (y/n): ");
		System.out.flush();
		deleteReportPriv = (GetString().equalsIgnoreCase("Y"));
		System.out.print("Privilege - New Database Connection (y/n): ");
		System.out.flush();
		newDatabasePriv = (GetString().equalsIgnoreCase("Y"));
		System.out.print("Privilege - Open Database Connection (y/n): ");
		System.out.flush();
		openDatabaseConnPriv = (GetString().equalsIgnoreCase("Y"));
		System.out.print("Privilege - Delete Database Connection (y/n): ");
		System.out.flush();
		deleteDatabaseConnPriv = (GetString().equalsIgnoreCase("Y"));

		int priv = 0;

		if (editReportPriv)
			priv += 1;
		if (newReportPriv)
			priv += 2;
		if (deleteReportPriv)
			priv += 4;
		if (newDatabasePriv)
			priv += 8;
		if (openDatabaseConnPriv)
			priv += 16;
		if (deleteDatabaseConnPriv)
			priv += 32;

		String sPriv = String.valueOf(priv);
		if (sPriv.length() == 1)
			sPriv = "0" + sPriv;

		String encUsername = SimpleENC.normToEnc(userName);
		String encPassword = SimpleENC.normToEnc(password);

		String encUserAndPswd = encUsername + encPassword;

		int length = encUserAndPswd.length();
		String encPriv = SimpleENC.normToEnc(encUserAndPswd + sPriv).substring(
				length, length + 2);

		System.out.println("\n\nEncryped String: [" + encUserAndPswd + encPriv
				+ "]");
		System.out
				.println("\nCopy encrypted string between the square brackets into the user.dat file");

		System.out.flush();
	}

	/**
	 * Method provides a method for inputting a String from the keyboard
	 */
	public static String GetString() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String outputString = "";

		try {
			outputString = in.readLine();
		} catch (IOException ioe) {
			outputString = "";
		}

		return outputString;
	}
}
