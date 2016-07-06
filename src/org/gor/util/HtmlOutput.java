//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

/**
 * This class helps create the HTML quickly and easily. It has methods for
 * creating the HTML at the top of the screen as well as the bottom (quick
 * help). The class also can display errors. It centralises most of the screen
 * code and thus prevents recurring code.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class HtmlOutput {

	private static String pageTitle; // title of page "Report - Delete"
	private static String menuPosition; // menu position e.g. "2.3"
	private static Locale pageLocale; // e.g. new Locale ("en", "GB")
	private static HttpSession session;

	/**
	 * Constructor of class.
	 * 
	 * @param menuPos
	 *            Position of menu (e.g. "3.1" = Database - Add)
	 * @param session
	 *            Session variable in which information can be retrieved from.
	 */
	public HtmlOutput(String menuPos, HttpSession session) {
		menuPosition = menuPos;
		HtmlOutput.session = session;
		pageLocale = Site.getLocale(session);
	}

	/**
	 * changes the position of the menu
	 * 
	 * @param menuPos
	 *            New Position of menu
	 */
	public void setMenu(String menuPos) {
		menuPosition = menuPos;
	}

	/**
	 * changes the locale of the system
	 * 
	 * @param locale
	 *            New locale of the system
	 */
	public void setLocale(Locale locale) {
		pageLocale = locale;
	}

	/**
	 * This method creates HTML for the top of the page. This includes the the
	 * title and the menu (calls the HtmlMenu class).
	 * 
	 * @return returns a String containing HTML
	 */
	public String pageTop() {
		String username = (String) session.getAttribute("username");
		String reportFileName = (String) session.getAttribute("report");
		HtmlMenu htmlMenu = new HtmlMenu(menuPosition, pageLocale, username,
				reportFileName);

		ResourceBundle rb = ResourceBundle.getBundle("resources.menu",
				pageLocale);
		pageTitle = rb.getString("title." + menuPosition);

		return ("<html>\n"
				+ "<head>\n"
				+ "<title>"
				+ pageTitle
				+ "</title>\n"
				+ "<link href='/gor/styles.css' type=text/css rel=stylesheet>"
				+ "<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>\n"
				+ "</head>\n"
				+ "<body bgcolor='#FFFFFF' text='#000000'>\n"
				+ "<table width='100%' border='0' cellpadding='0' cellspacing='0'>\n"
				+ "  <tr>\n"
				+ "    <td><img src='/gor/images/title.gif' width='300' height='50'></td>\n"
				+ "  </tr>\n" + htmlMenu);
	}

	/**
	 * Creates html for the bottom of the page. This is mainly the quickhelp
	 * section of the system.
	 * 
	 * @return returns a String containing HTML
	 */
	public String pageBottom() {
		return ("  <tr><td><hr noshade width='100%' size='1' align='left'></td></tr>\n"
				+ quickHelp() + "</table>\n" + "</body>\n" + "</html>\n");
	}

	/**
	 * Creates the top of an error page. ie. "The following erros have occured."
	 * 
	 * @return returns a String containing HTML
	 */
	public String errorTop() {
		ResourceBundle rb = ResourceBundle.getBundle("resources.errors", pageLocale);

		return (pageTop()
				+ "  <tr><td>&nbsp;</td></tr>\n"
				+ "  <tr>\n"
				+ "    <td>\n"
				+ "      <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#000000\">\n"
				+ "        <tr> \n"
				+ "          <td> \n"
				+ "            <table width=\"400\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
				+ "              <tr bgcolor=\"#000099\" align=\"center\"> \n"
				+ "                <td height=\"20\">\n"
				+ "                   <b><font size=\"2\" color=\"#FFFFFF\" face=\"Verdana, Arial\">"
				+ rb.getString("ErrorPage")
				+ " </font></b></td>\n"
				+ "              </tr>\n"
				+ "              <tr bgcolor=\"#CCCCCC\"> \n"
				+ "                <td><font color='#000000' face=\"Verdana, Arial\" size=\"2\"><b>"
				+ rb.getString("FollowingErrors")
				+ "</b></font> </td>\n"
				+ "              </tr>\n"
				+ "              <tr bgcolor=\"#CCCCCC\"> \n"
				+ "                <td><font color='#000000' face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"> \n" + "                  <ul>\n");
	}

	/**
	 * Creates the bottom of an error page.
	 * 
	 * @return returns a String containing HTML
	 */
	public String errorBottom() {
		ResourceBundle rb = ResourceBundle.getBundle("resources.errors",
				pageLocale);

		return ("                  </ul></font>\n"
				+ "                </td>\n"
				+ "              </tr>\n"
				+ "              <tr bgcolor=\"#CCCCCC\" align=\"center\"> \n"
				+ "                <td><font color='#000000' face=\"Verdana, Arial\" size=\"2\"><i>"
				+ rb.getString("PressBackButton")
				+ "</i></font> </td>\n"
				+ "              </tr>\n"
				+ "              <tr bgcolor=\"#CCCCCC\" align=\"middle\"> \n"
				+ "                <td> \n"
				+ "                  <input onClick=\"history.back()\" border='0' type=\"button\" value=\""
				+ rb.getString("Back")
				+ "\" name=\"button\">\n"
				+ "                </td>\n"
				+ "              </tr>\n"
				+ "            </table>\n"
				+ "          </td>\n"
				+ "        </tr>\n"
				+ "      </table>\n"
				+ "    </td>\n"
				+ "  </tr>\n"
				+ "  <tr><td><hr noshade width='100%' size='1' align='left'></td></tr>\n"
				+ "</table>\n" + "</body>\n" + "</html>\n");
	}

	/**
	 * Returns an error in a particular language depending on the locale.
	 * 
	 * @param errorCode
	 *            number of an error message
	 * @return returns a error.
	 */
	public String error(String errorCode) {
		ResourceBundle rb = ResourceBundle.getBundle("resources.errors",
				pageLocale);
		return (rb.getString(errorCode));
	}

	/**
	 * Creates a full HTML error pageerror message.
	 * 
	 * @param errorCode
	 *            number of an error message
	 * @return returns a String containing HTML
	 */
	public String errorPage(String errorCode) {
		return (errorTop() + "<li>" + error(errorCode) + "</li>" + errorBottom());
	}

	// Declare commonly used errors to speed implementation.
	public String errorGeneral() {
		return (errorPage("1"));
	}

	public String errorNoSession() {
		return (errorPage("2"));
	}

	public String errorIO() {
		return (errorPage("3"));
	}

	public String errorSQL() {
		return (errorPage("4"));
	}

	public String errorNoOpenReport() {
		return (errorPage("5"));
	}

	public String errorNoReportEditting() {
		return (errorPage("6"));
	}

	public String errorNoPrivilege() {
		return (errorPage("8"));
	}

	public String errorNoEdit(String user) {
		return (errorTop() + "<li>" + error("221") + "<b>" + user + "</b>"
				+ "</li>" + errorBottom());

	}

	/**
	 * Creates the quick help HTML at the bottom of each page.
	 * 
	 * @return returns a String containing HTML
	 */
	private String quickHelp() {
		// Load Resource bundle for this particular locale

		ResourceBundle rb = ResourceBundle.getBundle("resources.quickhelp",
				pageLocale);

		StringBuffer quickHelpHtml = new StringBuffer();

		try {
			rb.getString(menuPosition + ".t1");

			quickHelpHtml
					.append("  <tr>\n"
							+ "    <td>\n"
							+ "      <table width='400' border='0' cellspacing='1' cellpadding='2' bgcolor='#000000'>\n"
							+ "        <tr bgcolor='#666666' align='center'>\n"
							+ "          <td><font color='#FFFFFF' size='2' face='Verdana, Arial'><b>*"
							+ rb.getString("Title") + "</b></font></td>\n"
							+ "        </tr>\n"
							+ "        <tr bgcolor='#FFFFCC'>\n"
							+ "          <td>\n" + "            <ul>\n");

			int i = 1;
			try {
				while (true) {
					quickHelpHtml
							.append("              <li><font size='1' face='Verdana, Arial' color='#000099'>"
									+ rb.getString(menuPosition + ".t" + i)
									+ " </font></li>\n");
					i++;
				}
			} catch (MissingResourceException mre) {
			}

			quickHelpHtml.append("             </ul>\n" + "          </td>\n"
					+ "        </tr>\n" + "      </table>\n" + "    </td>\n"
					+ "  </tr>\n");
		} catch (MissingResourceException nmre) {
		}

		return quickHelpHtml.toString();
	}
}
