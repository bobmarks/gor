//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.data.DatabaseConnectionFile;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This servlet displays a list of database connection files (*.dbd) which a
 * user can open and display its details.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class DatabaseOpenServlet extends HttpServlet {
	
	private static final long serialVersionUID = 8044112739096560637L;

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
		HtmlOutput htmlOutput = new HtmlOutput("3.2", session);

		// check user has logged on (or session hasn't run out)
		if (userName != null) {
			if (Site.openDatabaseConnPriv(session)) {

				try {
					// ResourceBundle stuff
					ResourceBundle rb = ResourceBundle.getBundle(
							"resources.labels", Site.getLocale(session));

					String rbOpenDatabase = rb.getString("OpenDatabase");
					String rbDatabaseSelector = rb
							.getString("DatabaseSelector");
					String rbNoDatabasesAvailable = rb
							.getString("NoDatabasesAvailable");
					String rbOK = rb.getString("OK");
					String rbBack = rb.getString("Back");
					String rbDatabase = rb.getString("Database");
					String rbDriver = rb.getString("Driver");
					String rbConnectionURL = rb.getString("ConnectionURL");
					String rbTables = rb.getString("Tables");
					String rbColumns = rb.getString("Columns");
					String rbTotalColumnsInDatabase = rb.getString("TotalColumnsInDatabase");

					// Check to see if servlet opened for first time.
					String dbFile = req.getParameter("DATABASE");

					if (dbFile != null) { // if not null then try and open file
						try {
							// open database connection file
							DatabaseConnectionFile dcf = new DatabaseConnectionFile();
							dcf.readFromFile(dbFile);

							// Show database file details
							StringBuffer html = new StringBuffer(htmlOutput
									.pageTop()
									+ "<script language=\"JavaScript\">\n");

							for (int t = 0; t < dcf.getNumOfTables(); t++) {
								html.append("var COLUMNS_" + dcf.getTable(t)
										+ " = new Array(");
								for (int c = 0; c < dcf.getNumOfColumns(t); c++) {
									html.append("'" + dcf.getColumnName(t, c)
											+ "'");
									if (c != dcf.getNumOfColumns(t) - 1)
										html.append(", ");
								}
								html.append(");\n");
							}

							html.append("var COLUMNS_HOUSE = new Array('HOUSE_ID', 'ADDRESS', 'TOWN_ID', 'POSTCODE', 'TYPE_ID');\n"
											+ "\n"
											+ "function updateColumnsPulldown() {\n"
											+ "  var arrayName = \"COLUMNS_\" + document.mainForm.TABLES.value;\n"
											+ "  var colsArray = eval(arrayName);\n"
											+ "  var docPulldown = eval(\"document.mainForm.COLUMNS\");\n"
											+ "  docPulldown.length = 0;\n"
											+ "  for (i = 0; i < colsArray.length; i++) {\n"
											+ "    docPulldown[i] = new Option(colsArray[i]);\n"
											+ "    docPulldown[i].value = colsArray[i];\n"
											+ "  }\n"
											+ "}\n"
											+ "\n"
											+ "</script>\n"
											+ "<form method=\"post\" name=\"mainForm\" action=\"\">\n"
											+ "  <tr> <td>&nbsp; </td></tr>\n"
											+ "  <tr> \n"
											+ "    <td> \n"
											+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
											+ "        <tr bgcolor=\"#000000\"> \n"
											+ "          <td> \n"
											+ "            <table  width=\"500\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\">\n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
											+ rbOpenDatabase
											+ "</b></font></td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td align=\"right\" bgcolor=\"#CCCCCC\" nowrap>\n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbDatabase
											+ "</b></font>\n"
											+ "                </td>\n"
											+ "                <td align=\"left\" nowrap> \n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
											+ dcf.getDatabaseName()
											+ "</font> \n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td align=\"right\" bgcolor=\"#CCCCCC\" nowrap>\n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbDriver
											+ "</b></font>\n"
											+ "                </td>\n"
											+ "                <td align=\"left\" nowrap> \n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
											+ dcf.getDatabaseDriver()
											+ "</font> \n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td align=\"right\" bgcolor=\"#CCCCCC\" nowrap>\n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbConnectionURL
											+ "</b></font>\n"
											+ "                </td>\n"
											+ "                <td align=\"left\" nowrap> \n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
											+ dcf.getConnURL()
											+ "</font> \n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td align=\"right\" bgcolor=\"#CCCCCC\" nowrap>\n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbTables
											+ "</b></font>\n"
											+ "                </td>\n"
											+ "                <td nowrap align=\"left\"> \n"
											+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbColumns
											+ "</b></font> \n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td align=\"right\" bgcolor=\"#CCCCCC\" onClick=\"updateColumnsPulldown();\" width=\"10\"> \n"
											+ "                  <select name=\"TABLES\" size=\"10\">\n");

							// List database tables
							for (int i = 0; i < dcf.getNumOfTables(); i++)
								html.append("                    <option value='"
												+ dcf.getTable(i)
												+ "'>"
												+ dcf.getTable(i)
												+ "</option>\n");

							html.append("                  </select>\n"
											+ "                </td>\n"
											+ "                <td width=\"350\"> \n"
											+ "                  <select noresize name=\"COLUMNS\" size=\"10\">\n"
											+ "                  </select>\n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td colspan=\"2\" align=\"left\"> \n"
											+ "                  <ul>\n"
											+ "                    <li><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
											+ rbTotalColumnsInDatabase
											+ ": <b>"
											+ dcf.getNumOfTables()
											+ "</b></font></li>\n"
											+ "                    <li><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
											+ rbTotalColumnsInDatabase
											+ ": <b>"
											+ dcf.getNumOfColumns()
											+ "</b></font><br>\n"
											+ "                    </li>\n"
											+ "                  </ul>\n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td colspan=\"2\" align=\"middle\"> \n"
											+ "                  <input onClick=\"history.back()\" border='0' type=\"button\" value=\""
											+ rbBack
											+ "\" name=\"button\">\n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "            </table>\n"
											+ "          </td>\n"
											+ "        </tr>\n"
											+ "      </table>\n"
											+ "      </form>\n"
											+ "    </td> \n"
											+ "  </tr> \n"
											+ htmlOutput.pageBottom());

							out.println(html.toString());
						} catch (Exception ge) {
							ge.printStackTrace();
							out.println(htmlOutput.errorGeneral());
						}
					}

					// Page must be opened for first time so show list of
					// database files
					else { // if (dbFile != null)
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
										+ rbOpenDatabase
										+ "</b></font></td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbDatabaseSelector
										+ ":</b></font>\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"left\"> \n"
										+ "                  <select name=\"DATABASE\" size=\"10\">\n");

						// retrieve list of filenames into an array of Strings
						String[] dbFiles = Site.fileList(Site.getDatabaseDir());
						if (dbFiles.length == 0)
							html.append("                    <option value=\"-1\" selected>"
											+ rbNoDatabasesAvailable
											+ "</option>\n");
						else
							for (int i = 0; i < dbFiles.length; i++)
								html.append("                    <option value='"
												+ dbFiles[i]
												+ "'>"
												+ dbFiles[i] + "</option>\n");

						html.append("                  </select>\n"
								+ "                  <br>\n"
								+ "                </td>\n"
								+ "              </tr>\n");
						if (dbFiles.length != 0)
							html.append("              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td colspan=\"2\" align=\"middle\">\n"
											+ "                  <input type=\"submit\" name=\"Submit\" value=\""
											+ rbOK
											+ "\">\n"
											+ "                </td>\n"
											+ "              </tr>\n");

						html.append("            </table>\n"
								+ "          </td>\n" + "        </tr>\n"
								+ "      </table>\n" + "    </td>\n"
								+ "  </tr>\n" + "  </form>\n"
								+ htmlOutput.pageBottom());

						out.println(html.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					out.println(htmlOutput.errorGeneral());
				}
			} // if (Site.openDatabaseConnPriv(session))
			else
				out.println(htmlOutput.errorNoPrivilege());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}
