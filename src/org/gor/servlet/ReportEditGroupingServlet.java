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
import org.gor.data.Groups;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Query;
import org.gor.util.Site;

/**
 * This class provides an interface for changing the current groups for a
 * report. The first thing it does is display the current groups and allows the
 * user to create new set of groups.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditGroupingServlet extends HttpServlet {

	private static final long serialVersionUID = -7385304365338683504L;

	/**
	 * Do get.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * Do post
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Set up session and connection
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.4.4", session);

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

						String rbPriority = rb.getString("Priority");
						String rbVisibleColumns = rb
								.getString("VisibleColumns");
						String rbOK = rb.getString("OK");

						// Check if servlet is being run for first time.
						boolean showSelection = true;

						if (req.getParameter("GROUP1") != null) {
							// basically each table must have a corresponding
							// alias.
							Groups groups = new Groups();
							String curGroup;

							// go through all the rows and
							for (int i = 0; i < 5; i++) {
								curGroup = req.getParameter("GROUP" + i);

								// selected add to
								if (!curGroup.equals("-1"))
									groups.add(curGroup);
							}

							if (!groups.valid()) {
								out.println(htmlOutput.errorPage("2441"));
								showSelection = false;
							} else
								ReportFile.update(report, groups);
						}

						if (showSelection) { // (no errors have occured)
							ReportFile rf = new ReportFile(report);
							Query query = rf.getQuery();

							if (query.getNumOfVisibile() > 0) {
								DatabaseConnectionFile dcf = new DatabaseConnectionFile(
										rf.getDatabase());
								TablesAndAlias tablesAliases = rf
										.getTablesAndAlias();
								Groups groups = rf.getGroups();

								// Create final HTML output.
								StringBuffer html = new StringBuffer(
										htmlOutput.pageTop()
												+ "    <tr><td>&nbsp; </td></tr>\n"
												+ "    <form method=\"get\" action=\"\" name=\"mainForm\">\n"
												+ "    <tr> \n"
												+ "      <td> \n"
												+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
												+ "          <tr> \n"
												+ "            <td> \n"
												+ "              <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
												+ "                <tr bgcolor=\"#000099\"> \n"
												+ "                  <td height=\"20\" bgcolor=\"#000099\" align=\"right\" width=\"100\"> \n"
												+ "                    <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
												+ rbPriority
												+ "</b></font>\n"
												+ "                  </td>\n"
												+ "                  <td height=\"20\" align=\"left\" width=\"300\"> \n"
												+ "                    <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
												+ rbVisibleColumns
												+ "</b></font>\n"
												+ "                  </td>\n"
												+ "                </tr>\n");

								for (int row = 0; row < Groups.MAX_NUM_OF_GROUPS; row++) {
									html
											.append("                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td height=\"20\" align=\"right\" width=\"100\">\n"
													+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
													+ (row + 1)
													+ "</b></font>\n"
													+ "                  </td>\n"
													+ "                  <td height=\"20\" align=\"left\" width=\"300\"> \n"
													+ "                    <select name='GROUP"
													+ row
													+ "'>\n"
													+ "                      <option value=\"-1\" selected>-</option>\n");

									int intGroupAlias = -1;
									int intGroupColumn = -1;
									int intCol, intQueryAlias, intQueryTable, intQueryColumn;
									String optionValue, optionText;

									if (row < groups.size()) {
										intGroupAlias = groups
												.getGroupAlias(row);
										intGroupColumn = groups
												.getGroupColumn(row);
									}

									for (int i = 0; i < query
											.getNumOfVisibile(); i++) {
										intCol = query.getIndexFromVisible(i);
										intQueryAlias = query.getAlias(intCol);
										intQueryTable = tablesAliases
												.getTable(intQueryAlias);
										intQueryColumn = query
												.getColumn(intCol);

										optionValue = intQueryAlias + ","
												+ intQueryColumn;
										optionText = tablesAliases
												.getAlias(intQueryAlias)
												+ "."
												+ dcf.getColumnName(
														intQueryTable,
														intQueryColumn);

										if (intGroupAlias == intQueryAlias
												&& intGroupColumn == intQueryColumn)
											html
													.append("                      <option selected value='"
															+ optionValue
															+ "'>"
															+ optionText
															+ "</option>\n");
										else
											html
													.append("                      <option value='"
															+ optionValue
															+ "'>"
															+ optionText
															+ "</option>\n");
									}

									html
											.append("                    </select>\n"
													+ "                  </td>\n"
													+ "                </tr>\n");
								} // for (int row = 0; ...

								html
										.append("                <tr bgcolor=\"#CCCCCC\" align=\"middle\"> \n"
												+ "                  <td colspan=\"2\"> \n"
												+ "                    <input type=\"submit\" name=\"Submit\" value=\""
												+ rbOK
												+ "\">\n"
												+ "                  </td>\n"
												+ "                </tr>\n"
												+ "              </table>\n"
												+ "            </td>\n"
												+ "          </tr>\n"
												+ "        </table>\n"
												+ "      </td>\n"
												+ "    </tr>\n"
												+ "  </form>\n"
												+ htmlOutput.pageBottom());

								out.print(html.toString());
							} else
								out.println(htmlOutput.errorPage("252"));
						} // if (showSelection)
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