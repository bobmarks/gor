//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import org.gor.util.Query;
import org.gor.util.QueryGenerator;

/**
 * This class provides a means for testing the Tables, Relations and Query
 * section of the system. It displays the visible columns, SQL and data.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditTestServlet extends HttpServlet {

	private static final long serialVersionUID = -7497115152306694853L;

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
		
		// Set up session and connection
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		//======================================================================
		// ====
		// Set the content type for the HTTP response
		//======================================================================
		// ====

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.4.5", session);

		//======================================================================
		// ====
		// Check user name, report, edit etc
		//======================================================================
		// ====

		if (userName != null) { // please logon
			String report = (String) session.getAttribute("report");
			String edit = (String) session.getAttribute("report_edit");

			if (report != null) { // please open a report
				if (edit.equals("1")) { // sorry you cannot edit this report
					try {
						//======================================================
						// ================
						// Create final HTML output.
						//======================================================
						// ================

						ReportFile reportFile = new ReportFile(report);
						DatabaseConnectionFile dcf = new DatabaseConnectionFile(
								reportFile.getDatabase());
						Connection conn = null;

						//======================================================
						// ================
						// Check to see if a report can be run.
						// There must be a least 1 table and 1 visible column
						// declared.
						//======================================================
						// ================

						TablesAndAlias tablesAliases = reportFile
								.getTablesAndAlias();
						Query query = reportFile.getQuery();
						int numOfVisCols = query.getNumOfVisibile();

						boolean aliasesExist = (tablesAliases.size() > 0);
						boolean visibleColumnsExist = (query.getNumOfVisibile() > 0);

						boolean success = aliasesExist && visibleColumnsExist;

						if (!success) {
							// create error message
							StringBuffer html = new StringBuffer(htmlOutput
									.errorTop());

							if (!aliasesExist)
								html.append("<li>" + htmlOutput.error("251")
										+ "</li><br>");
							if (!visibleColumnsExist)
								html.append("<li>" + htmlOutput.error("252")
										+ "</li><br>");

							html.append(htmlOutput.errorBottom());

							out.println(html.toString());
						} else {
							QueryGenerator qg = new QueryGenerator(report);
							conn = dcf.getConnection();
							Statement st = conn.createStatement();
							String sql = qg.getSQL();
							ResultSet rs = st.executeQuery(sql);

							StringBuffer html = new StringBuffer(
									htmlOutput.pageTop()
											+ "<tr><td>&nbsp;</td></tr>\n"
											+ "<tr>\n"
											+ "  <td> \n"
											+ "    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
											+ "      <tr>\n"
											+ "        <td valign=\"top\">\n"
											+ "          <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "            <tr>\n"
											+ "              <td>\n"
											+ "                <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n");

							for (int i = 1; i <= numOfVisCols; i++)
								html
										.append("                  <tr>\n"
												+ "                    <td bgColor=\"#000099\"><b><font color=\"#ffffff\" size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
												+ i
												+ "</font></b></td>\n"
												+ "                    <td bgColor=\"#cccccc\"><font color=\"#000000\" size=\"1\" face=\"Verdana, Arial, Helvetica, sans-serif\"><b>"
												+ qg
														.getSortedVisibleColumnName(i - 1)
												+ "</font></b></td>\n"
												+ "                  </tr>\n");

							html
									.append("                </table>\n"
											+ "              </td>\n"
											+ "            </tr>\n"
											+ "          </table>\n"
											+ "        </td>\n"
											+ "        <td width=\"10\">&nbsp;</td>\n"
											+ "        <td valign=\"top\" width=\"400\">\n"
											+ "          <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "            <tr>\n"
											+ "              <td> \n"
											+ "                <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "                  <tr bgColor=\"#000099\">\n"
											+ "                    <td><font color=\"#ffffff\" size=\"1\" face=\"Verdana, Arial\"><b>SQL</font></b></td>\n"
											+ "                  </tr>\n"
											+ "                  <tr bgColor=\"#ffffff\">\n"
											+ "                    <td><font color=\"#000000\" size=\"1\" face=\"Verdana, Arial\">"
											+ sql
											+ "</font></td>\n"
											+ "                  </tr>\n"
											+ "                </table>\n"
											+ "              </td>\n"
											+ "            </tr>\n"
											+ "          </table>\n"
											+ "        </td>\n"
											+ "      </tr>\n"
											+ "    </table>\n"
											+ "  </td>\n"
											+ "</tr>\n"
											+ "<tr><td>&nbsp;</td></tr>\n"
											+ "<tr> \n"
											+ "  <td> \n"
											+ "    <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "      <tr> \n"
											+ "        <td> \n"
											+ "          <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "            <tr bgColor=\"#000099\">\n"
											+ "              <td><font color=\"#ffffff\" size=\"1\" face=\"Verdana, Arial\">#</font></b></td>\n");

							for (int i = 1; i <= numOfVisCols; i++)
								html
										.append("<td><b>"
												+ "<font color=\"#ffffff\" size=\"1\" face=\"Verdana, Arial\">"
												+ i + "</font></b></td>\n");

							html.append("            </tr>\n");

							int row = 1;
							while (rs.next()) {
								html
										.append("            <tr>\n"
												+ "              <td bgColor=\"#666666\"><b>"
												+ "<font color=\"#ffffff\" size=\"1\" face=\"Verdana, Arial\">"
												+ row++ + "</font></b></td>\n");

								for (int i = 1; i <= numOfVisCols; i++)
									html
											.append("              <td bgcolor=\"#CCCCCC\" valign='top'><font face='Veranda, Arial' size='1' color='#000000'>"
													+ rs.getString(i)
													+ " </font>\n"
													+ "              </td>\n");

								html.append("            </tr>\n");
							}

							html.append("          </table>\n"
									+ "        </td>\n" + "      </tr>\n"
									+ "    </table>\n" + "  </td>\n"
									+ "</tr>\n" + "</form>\n"
									+ htmlOutput.pageBottom());

							// Try and close the connection to the database.
							try {
								conn.close();
							} catch (SQLException sqlEx1) {
								try {
									conn.close();
								} catch (SQLException sqlEx2) {
								}
							}

							out.println(html.toString());
						}
					} catch (SQLException sqle) {
						out.println(htmlOutput.errorSQL());
					} catch (Exception ge) {
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
