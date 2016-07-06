//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

/**
 * This little file provides an a simple form of encryping and decrypting
 * Strings on the system.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class SimpleENC {

	// string describing normal codes
	private static String normCodes = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_*";

	// string describing encripted codes
	private static String encCodes = "Il8ZGePoHu6*WUAxLg1h_QaiDspfXRc29TzCMqw7JjdN3SrBvnO4Kb0Vm5EkytFY";

	/**
	 * This method converts a normal String to an encrypted String.
	 * 
	 * @param normalString
	 *            A normal unencrypted String
	 * @return Method returns String in encrypted form.
	 */
	public static String normToEnc(String normalString) {
		// StringBuffer where encrypted String will go
		StringBuffer encStr = new StringBuffer();

		// Append character from encripted codes onto the StringBuffer by
		// finding
		// position of chars from the normalString in each iteration of the
		// loop.
		try {
			int pos;
			for (int i = 0; i < normalString.length(); i++) {
				pos = normCodes.lastIndexOf(normalString.charAt(i)) + i;
				if (pos > normCodes.length() - 1)
					pos -= normCodes.length();

				encStr.append(encCodes.charAt(pos));
			}
		} catch (Exception e) {
			return "-1";
		}

		return encStr.toString();
	}

	/**
	 * This method converts an encrypted String to a normal String.
	 * 
	 * @param normalString
	 *            A Strin encrypted using the normToEnc() method.
	 * @return Method returns String in normal original form.
	 */
	public static String encToNorm(String encString) {
		// StringBuffer where encrypted String will go
		StringBuffer normal = new StringBuffer();

		// append character from encripted codes onto the StringBuffer by
		// finding
		// position of chars from the encrypt String in each iteration of the
		// loop.
		try {
			int pos;
			for (int i = 0; i < encString.length(); i++) {
				pos = encCodes.lastIndexOf(encString.charAt(i)) - i;
				if (pos < 0)
					pos += normCodes.length();

				normal.append(normCodes.charAt(pos));
			}
		} catch (Exception e) {
			return "-1";
		}

		return normal.toString();
	}
}