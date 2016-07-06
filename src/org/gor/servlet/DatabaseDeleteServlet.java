//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This servlet displays a list of database connection files (*.dbd) which a
 * user can select and delete off the server
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class DatabaseDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = -2177461936418841063L;

	/**
	 * Do get method.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}
	
	/**
	 * Do post method.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Set up session and retrieve username and locale from it.
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("3.3", session);

		// check user has logged on (or session hasn't run out)
		if (userName != null) {
			if (Site.deleteDatabaseConnPriv(session)) {
				try {
					// Read from resource bundle
					ResourceBundle rb = ResourceBundle.getBundle(
							"resources.labels", Site.getLocale(session));

					String rbDeleteDatabase = rb.getString("DeleteDatabase");
					String rbDatabaseSelector = rb
							.getString("DatabaseSelector");
					String rbNoDatabasesAvailable = rb
							.getString("NoDatabasesAvailable");
					String rbAreYouSure = rb.getString("AreYouSure");
					String rbYes = rb.getString("Yes");
					String rbNo = rb.getString("No");
					String rbOK = rb.getString("OK");

					// Check to see if servlet opened for first time.
					String dbFile = req.getParameter("DATABASE");
					String sure = req.getParameter("SURE");

					if (dbFile != null) { // user trying to delete database file
						if (sure.equals("1")) { // ensure "Are you sure?" = Yes
							File fileToDelete = new File(Site.getDatabaseDir() + dbFile);
							try { // try and delete file
								fileToDelete.delete();
							} catch (Exception e) {} // if error don't do anything
						}
					}

					// create file selection screen
					StringBuffer html = new StringBuffer(
							htmlOutput.pageTop()
									+ "  <tr><td>&nbsp;</td></tr>\n"
									+ "  <tr>\n"
									+ "    <td>\n"
									+ "      <form method=\"post\" action=\"\">\n"
									+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
									+ "        <tr bgcolor=\"#000000\"> \n"
									+ "          <td> \n"
									+ "            <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"300\">\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\">\n"
									+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
									+ rbDeleteDatabase
									+ "</b></font></td>\n"
									+ "              </tr>\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td colspan=\"2\">\n"
									+ "                  <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbDatabaseSelector
									+ ":</b></font>\n"
									+ "                </td>\n"
									+ "              </tr>\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td colspan=\"2\" align=\"left\"> \n"
									+ "                  <select name=\"DATABASE\" size=\"10\">\n");

					// retrieve list of filenames into an array of Strings
					String[] dbFiles = Site
							.fileList(Site.getDatabaseDir());
					if (dbFiles.length == 0)
						html
								.append("                    <option value=\"-1\" selected>"
										+ rbNoDatabasesAvailable
										+ "</option>\n");
					else
						for (int i = 0; i < dbFiles.length; i++)
							html.append("                    <option value='"
									+ dbFiles[i] + "'>" + dbFiles[i]
									+ "</option>\n");

					html.append("                  </select>\n"
							+ "                </td>\n"
							+ "              </tr>\n");

					if (dbFiles.length != 0)
						html
								.append("              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"center\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbAreYouSure
										+ "</b> \n"
										+ "                  <input type=\"radio\" name=\"SURE\" value=\"1\">"
										+ rbYes
										+ "                  <input type=\"radio\" name=\"SURE\" checked value=\"0\">"
										+ rbNo
										+ "</font>\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"middle\">\n"
										+ "                  <input type=\"submit\" name=\"Submit\" value=\""
										+ rbOK
										+ "\">\n"
										+ "                </td>\n"
										+ "              </tr>\n");

					html.append("            </table>\n" + "          </td>\n"
							+ "        </tr>\n" + "      </table>\n"
							+ "    </td>\n" + "  </tr>\n" + "  </form>\n"
							+ htmlOutput.pageBottom());

					out.println(html.toString());
				} catch (Exception e) {
					e.printStackTrace();
					out.println(htmlOutput.errorGeneral());
				}
			} else
				out.println(htmlOutput.errorNoPrivilege());
		} else
			out.println(htmlOutput.errorNoSession());
	}
}