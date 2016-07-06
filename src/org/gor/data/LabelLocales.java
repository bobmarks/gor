//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class loads all the information off the label_locales.dat file to
 * produce a list of locales that labels can be specified in. This includes the
 * name, language and country. e.g. "German (Switzerland)", "de" and "CH".
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
@SuppressWarnings("unchecked")
public class LabelLocales {

	private int numOfLabels;
	private Vector name, language, country;

	/**
	 * This construcotr reads in the "interface_locale.dat" file and populates
	 * the text, language, country and image arrays.
	 */
	public LabelLocales() {
		name = new Vector();
		language = new Vector();
		country = new Vector();
		numOfLabels = 0;

		try {
			URL url = LabelLocales.class.getResource("/resources/label_locales.properties");
			File file = new File (url.getFile());
			BufferedReader fileIn = new BufferedReader(new FileReader(file));					
			String fileLine; // use to read each line in file

			do {
				fileLine = fileIn.readLine();
				if (fileLine != null)
					add(fileLine);
			} while (fileLine != null);

		} catch (IOException exIO) {
			exIO.printStackTrace();
		}
	}

	/**
	 * This method uses a String read from the file and populates the arrays
	 * with its values.
	 */
	private void add(String fileLine) {
		StringTokenizer st = new StringTokenizer(fileLine);

		name.addElement(st.nextToken(","));
		language.addElement(st.nextToken(","));
		country.addElement(st.nextToken(","));

		numOfLabels++;
	}

	/**
	 * Returns the name of the locale (e.g. English (USA))
	 * 
	 * @param index
	 *            0 to number of label locales - 1
	 * @return Textual description of locale.
	 */
	public String getName(int index) {
		return (String) name.get(index);
	}

	/**
	 * Returns the name of the locale e.g. ("en","gb")
	 * 
	 * @param index
	 *            0 to number of label locales - 1
	 * @return Locale of label
	 */
	public Locale getLocale(int index) {
		String sLanguage = (String) language.get(index);
		String sCountry = (String) language.get(index);

		return (new Locale(sLanguage, sCountry));
	}

	/**
	 * Returns the total number of labels
	 * 
	 * @param index
	 *            0 to number of interface locales - 1
	 * @return Textual description of locale.
	 */
	public int getNumOfLabels() {
		return numOfLabels;
	}
}