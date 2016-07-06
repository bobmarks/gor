//==============================================================================
//
//  Generic Online Reporting
//
//	The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * This class is reponsible for creating the menu at the top of the system. The
 * menu is read from the menu.properties resource bundle. This ensures that the
 * menu is multi-language compatiable.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class HtmlMenu {

	// Declare private variables that are need to construct the page menu.
	private String PAGE_MENU, username, reportFileName;
	private Locale PAGE_LOCALE;

	// Declare some html String to aid menu design
	String HTML_SINGLE_BLACK_LINE_HOR = "  <tr bgcolor='#000000'><td><img src='/gor/images/1p.gif' width='1' height='1'></td></tr>\n";

	/**
	 * Constructor for menu class.
	 * 
	 * @param pageMenu
	 *            current position in the menu e.g. "2.4.3"
	 * @param locale
	 *            current locale e.g. english, uk
	 * @param username
	 *            current username. This gets displayed in Logout
	 * @param reportFileName
	 *            current report file (if opened). displayed beside Report - Run
	 */
	public HtmlMenu(String pageMenu, Locale locale, String username,
			String reportFileName) {
		this.PAGE_MENU = pageMenu;
		this.PAGE_LOCALE = locale;
		this.username = username;
		this.reportFileName = reportFileName;
	}

	/**
	 * Creates the entire HTML menu using the menu resource bundle files.
	 * 
	 * @return returns a String containing HTML
	 */
	public String toString() {
		// create a string tokenizer to split up the code (each number in code
		// is a seperate level)
		StringTokenizer st = new StringTokenizer(PAGE_MENU, ".");

		if (st.hasMoreElements()) // if no elements quit (invalid menu call)
		{
			// populate Code array (used to match up codes in resource bundle
			// file)
			int numOfItems = st.countTokens();
			int[] codes = new int[numOfItems];

			for (int i = 0; i < numOfItems; i++)
				codes[i] = Integer.parseInt(st.nextToken()); // fill code array
																// up.

			// Load Resource bundle for this particular session
			ResourceBundle rb = ResourceBundle.getBundle("resources.menu",
					PAGE_LOCALE);

			// Start creating menu html
			StringBuffer menuHtml = new StringBuffer(HTML_SINGLE_BLACK_LINE_HOR
					+ "  <tr bgcolor='#000099'>\n" + "    <td height='22'>\n");

			String tempText = "";
			String tempLink = "";
			String tempImage = ""; // used to create menu

			// Do first section (Home | Report | Database | English | Log Off |
			// Help)
			int numOfMainElements = 6; //

			for (int i = 1; i <= numOfMainElements; i++) {
				tempText = rb.getString("text." + i);
				if (i == 5 && username != null) // logoff
					tempText += " (" + username + ")";
				tempLink = rb.getString("link." + i);

				menuHtml.append("&nbsp;");

				if (codes[0] == i)
					menuHtml
							.append("<font class='white'>" + tempText + "</font>");
				else
					menuHtml.append("<a class='blue' href='" + tempLink + "'>" + tempText + "</a>");

				// dividing line
				if (i != numOfMainElements)
					menuHtml
							.append("&nbsp;<font face='Verdana, Arial' color='#CCCCFF'><b>|</b></font>");
			}

			menuHtml.append("    </td>\n" + "  </tr>\n"
					+ HTML_SINGLE_BLACK_LINE_HOR);

			// Do second and third section of menu (if they exists)
			for (int row = 1; row <= numOfItems; row++) {
				// construct code
				String selectedItemCode = String.valueOf(codes[0]);
				if (row == 2)
					selectedItemCode += "." + String.valueOf(codes[1]);

				// check to see if row actually exists from resource bundle file
				boolean rowExists = true;
				try {
					rb.getString("text." + selectedItemCode + ".1");
				} catch (MissingResourceException mre) {
					rowExists = false;
				}

				if (rowExists && row != 3) // row exists
				{
					int i = 1;
					boolean itemExists = true;

					do {
						boolean imageExists = true; // presume image exists
						// grab the text and link
						try {
							tempText = rb.getString("text." + selectedItemCode
									+ "." + i);
							// display name of current file open (if not null
							// then file is open)
							if (i == 5 && row == 1 && reportFileName != null)
								tempText += " (" + reportFileName + ")";
							tempLink = rb.getString("link." + selectedItemCode
									+ "." + i);
						} catch (MissingResourceException mre) {
							itemExists = false;
						}

						if (itemExists) {
							if (i == 1)
								menuHtml
										.append("  <tr bgcolor='#CCCCCC'>\n"
												+ "    <td valign='middle'>\n"
												+ "      <table border='0' cellspacing='0' cellpadding='0'>\n"
												+ "        <tr>\n"
												+ "          <td bgcolor='#000000' width='1'>"
												+ "<img src='/gor/images/1p.gif' width='1' height='1'></td>\n");

							// grab the image (optional)
							try {
								tempImage = rb.getString("image."
										+ selectedItemCode + "." + i);
							} catch (MissingResourceException mre) {
								imageExists = false;
							}

							// check to see if this row matches up.
							boolean itemSelected = false;
							if (numOfItems > row)
								itemSelected = (codes[row] == i);

							// select the correct font, depending on which row
							// the code is at.
							if (itemSelected) {
								String bgColor = (row == 1) ? "999999"
										: "666666";
								menuHtml
										.append("<td bgcolor='#"
												+ bgColor
												+ "'><font color='#FFFFFF' face='Verdana, Arial' size='2'><b>&nbsp;");
							} else if (row == 1)
								menuHtml
										.append("<td bgcolor='#CCCCCC'>&nbsp;<a class='grey_dark' href='"
												+ tempLink + "'>");
							else
								menuHtml
										.append("<td bgcolor='#999999'>&nbsp;<a class='grey_dark' href='"
												+ tempLink + "'>");

							if (imageExists)
								menuHtml.append("<img border='0' src='"
										+ tempImage + "'>");
							menuHtml.append(tempText);

							if (itemSelected)
								menuHtml.append("</font></b>");
							else
								menuHtml.append("</a>");

							menuHtml
									.append("&nbsp;</td>\n"
											+ "<td bgcolor='#000000' width='1'>"
											+ "<img src='/gor/images/1p.gif' width='1' height='1'></td>\n");
						}
						i++;
					} while (itemExists);

					menuHtml.append("        </tr>\n" + "      </table>\n"
							+ "    </td>\n" + "  </tr>\n"
							+ HTML_SINGLE_BLACK_LINE_HOR);
				} // if (rowExists)
			}

			return (menuHtml.toString());
		} else
			return "Invalid Menu";
	}
}