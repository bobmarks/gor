//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * This class loads all the information off the interface files so a user can
 * select a language to view the system in. It loads things like the text,
 * language, country and image e.g "English (UK)", "en", "GB", "britian.gif".
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class InterfaceLabels {

	private int numOfIntefaceLocales;
	private String[] text, language, country, image;

	/**
	 * This constructor reads in the "interface_locale.properties" file and
	 * populates the text, language, country and image arrays.
	 */
	public InterfaceLabels() {
		ResourceBundle rb = ResourceBundle.getBundle("resources.interface_locales");
		numOfIntefaceLocales = Integer.parseInt(rb.getString("num.of.interface.locales"));

		text = new String[numOfIntefaceLocales];
		language = new String[numOfIntefaceLocales];
		country = new String[numOfIntefaceLocales];
		image = new String[numOfIntefaceLocales];

		String fileLine;
		for (int i = 0; i < numOfIntefaceLocales; i++) {
			fileLine = rb.getString("locale." + i);

			StringTokenizer st = new StringTokenizer(fileLine);
			text[i] = st.nextToken(",");
			language[i] = st.nextToken(",");
			country[i] = st.nextToken(",");
			image[i] = st.nextToken(",");
		}
	}

	/**
	 * Returns the name of the locale (e.g. English (USA))
	 * 
	 * @param index
	 *            0 to number of interface locales - 1
	 * @return Textual description of locale.
	 */
	public String getText(int index) {
		return text[index];
	}

	/**
	 * Returns the language of the locale (e.g. "en")
	 * 
	 * @param index
	 *            0 to number of interface locales - 1
	 * @return Language
	 */
	public String getLanguage(int index) {
		return language[index];
	}

	/**
	 * Returns the country of the locale (e.g. "gb")
	 * 
	 * @param num
	 *            0 to number of interface locales - 1
	 * @return Country
	 */
	public String getCountry(int index) {
		return country[index];
	}

	/**
	 * Returns the name of the locale image (e.g. "/gor/images/france.gif")
	 * 
	 * @param index
	 *            0 to number of interface locales - 1
	 * @return Name of position and image.
	 */
	public String getImage(int index) {
		return image[index];
	}

	/**
	 * Returns the number of interface locales available
	 * 
	 * @return number of interfaces available.
	 */
	public int getNumOfInterfaceLocales() {
		return numOfIntefaceLocales;
	}
}