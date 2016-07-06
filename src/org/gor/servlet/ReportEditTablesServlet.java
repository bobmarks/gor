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
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This servlet displays the tables and alias section of the system and provides
 * a means for updating a report file for tables and aliases.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditTablesServlet extends HttpServlet {
	
	private static final long serialVersionUID = 4599304578320186420L;

	/**
	 * Do get.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	//==========================================================================
	// ==
	// doPost
	//==========================================================================
	// ==

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Set up session and connection
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.4.1", session);

		// Check user name, report, edit etc
		if (userName != null) { // please logon
			String report = (String) session.getAttribute("report");
			String edit = (String) session.getAttribute("report_edit");

			if (report != null) { // please open a report
				if (edit.equals("1")) { // sorry you cannot edit this report
					try {
						// ResourceBundle stuff
						ResourceBundle rb = ResourceBundle.getBundle(
								"resources.labels", Site.getLocale(session));

						String rbTable = rb.getString("Table");
						String rbAlias = rb.getString("Alias");
						String rbOK = rb.getString("OK");

						// Check if servlet is being run for first time.
						boolean showSelection = true;

						if (req.getParameter("TABLE1") != null) {
							// basically each table must have a corresponding
							// alias.
							TablesAndAlias newTablesAliases = new TablesAndAlias();
							int curTable;
							String curAlias;

							try {
								// go through all the rows and
								for (int row = 0; row < TablesAndAlias.MAX_NUM_OF_TABLES_ALIAS; row++) {
									curTable = Integer.parseInt(req
											.getParameter("TABLE" + row));
									curAlias = req.getParameter("ALIAS" + row);

									// selected add to
									if (curTable != -1 && !curAlias.equals(""))
										newTablesAliases
												.add(curTable, curAlias);
								}
							} catch (Exception e) {
							}

							if (!newTablesAliases.valid()) {
								out.println(htmlOutput.errorPage("2411"));
								showSelection = false;
							} else
								ReportFile.update(report, newTablesAliases);
						}

						// Show selection page.
						if (showSelection) {
							StringBuffer html = new StringBuffer(
									htmlOutput.pageTop()
											+ "  <tr><td>&nbsp;</td></tr>\n"
											+ "  <form method=\"post\" action=\"\" name=\"mainForm\">\n"
											+ "  <tr>\n"
											+ "    <td>\n"
											+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "        <tr> \n"
											+ "          <td> \n"
											+ "            <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "              <tr bgcolor=\"#000099\"> \n"
											+ "                <td align=\"center\"> \n"
											+ "                  <font color=\"#FFFFFF\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\">"
											+ rbTable
											+ "</font></b></font>\n"
											+ "                </td>\n"
											+ "                <td align=\"center\"> \n"
											+ "                  <font color=\"#FFFFFF\"><b><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\">"
											+ rbAlias + "</font></b></font>\n"
											+ "                </td>\n"
											+ "              </tr>\n");

							ReportFile rf = new ReportFile(report);
							DatabaseConnectionFile dcf = new DatabaseConnectionFile(
									rf.getDatabase());
							TablesAndAlias tablesAliases = rf
									.getTablesAndAlias();

							String alias;
							int tableNum;
							int maxNum = tablesAliases.size() + 4;
							if (maxNum > TablesAndAlias.MAX_NUM_OF_TABLES_ALIAS)
								maxNum = TablesAndAlias.MAX_NUM_OF_TABLES_ALIAS;

							for (int row = 0; row < maxNum; row++) {
								alias = "";
								tableNum = -1;

								if (row < tablesAliases.size()) {
									alias = tablesAliases.getAlias(row);
									tableNum = tablesAliases.getTable(row);
								}

								html
										.append("              <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                <td> \n"
												+ "                  <select name=\"TABLE"
												+ row
												+ "\">\n"
												+ "                    <option value=\"-1\" selected>-</option>\n");

								for (int t = 0; t < dcf.getNumOfTables(); t++) {
									if (tableNum == t)
										html
												.append("                    <option selected value='"
														+ t
														+ "'>"
														+ dcf.getTable(t)
														+ "</option>\n");
									else
										html
												.append("                    <option value='"
														+ t
														+ "'>"
														+ dcf.getTable(t)
														+ "</option>\n");
								}

								html
										.append("                  </select>\n"
												+ "                </td>\n"
												+ "                <td> \n"
												+ "                  <input type=\"text\" name=\"ALIAS"
												+ row
												+ "\" size=\"10\" maxlength=\"8\" value='"
												+ alias + "'>\n"
												+ "                </td>\n"
												+ "              </tr>\n");
							}

							html
									.append("              <tr bgcolor=\"#CCCCCC\" align=\"middle\"> \n"
											+ "                <td colspan=\"2\"> \n"
											+ "                  <input type=\"submit\" name=\"Submit\" value=\""
											+ rbOK
											+ "\">\n"
											+ "                </td>\n"
											+ "              </tr>\n"
											+ "            </table>\n"
											+ "          </td>\n"
											+ "        </tr>\n"
											+ "      </table>\n"
											+ "    </td>\n"
											+ "  </tr>\n"
											+ "  </form>\n"
											+ htmlOutput.pageBottom());

							out.print(html.toString());
						}
					} catch (Exception e) {
						out.println(htmlOutput.errorGeneral());
					}

				} // if (edit.equals("1"))
				else
					out.println(htmlOutput.errorNoReportEditting());
			} // if (report != null)
			else
				out.println(htmlOutput.errorNoOpenReport());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}