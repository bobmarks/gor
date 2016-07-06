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
import org.gor.util.Query;
import org.gor.util.Site;

/**
 * This servlet displays the contents of Query section of a report file using an
 * HTML form. It also provides facilities for a user to update the current set
 * of query.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditQueryServlet extends HttpServlet {

	private static final long serialVersionUID = -578180235380423584L;

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

		// Set up session and connection
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.4.3", session);

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
						// ============
						// ResourceBundle stuff
						//======================================================
						// ============

						ResourceBundle rb = ResourceBundle.getBundle(
								"resources.labels", Site.getLocale(session));

						String rbShow = rb.getString("Show");
						String rbAliasesAndColumns = rb
								.getString("AliasesAndColumns");
						String rbSort = rb.getString("Sort");
						String rbCondition = rb.getString("Condition");
						String rbValue = rb.getString("Value");
						String rbOK = rb.getString("OK");

						//======================================================
						// ============
						// Check if servlet is being run for first time.
						//======================================================
						// ============

						if (req.getParameter("ALIAS1") != null) {
							Query newQuery = new Query();
							boolean newShow;
							int newAlias, newColumn, newSort, newCondition;
							String newValue;

							try {
								for (int row = 0; row < Query.MAX_NUM_OF_COLS; row++) {
									newShow = (req.getParameter("SHOW" + row) != null);
									newAlias = Integer.parseInt(req
											.getParameter("ALIAS" + row));
									newColumn = Integer.parseInt(req
											.getParameter("COLUMNS" + row));
									newSort = Integer.parseInt(req
											.getParameter("SORT" + row));
									// if not showing a column then reset sort
									// (i.e. you cannot sort
									// a column that isn't being displayed)
									if (!newShow)
										newSort = 0;
									newCondition = Integer.parseInt(req
											.getParameter("CON" + row));
									newValue = req.getParameter("CONVALUE"
											+ row);

									if (newAlias != 999 && newColumn != 999)
										newQuery.add(newShow, newAlias,
												newColumn, newSort,
												newCondition, newValue);
								}
							} catch (Exception e) {
							}

							// and update the report file with new data
							ReportFile.update(report, newQuery);
						}

						ReportFile rf = new ReportFile(report);
						TablesAndAlias tablesAliases = rf.getTablesAndAlias();

						if (tablesAliases.size() != 0) {
							DatabaseConnectionFile dcf = new DatabaseConnectionFile(
									rf.getDatabase());
							Query query = rf.getQuery();

							//==================================================
							// ==============
							// Create final HTML output.
							//==================================================
							// ==============

							StringBuffer html = new StringBuffer(htmlOutput
									.pageTop()
									+ "<script language=JavaScript>\n"
									+ "var COLUMNS_999 = new Array();\n");

							// create JavaScript array
							for (int a = 0; a < tablesAliases.size(); a++) {
								html.append("var COLUMNS_" + a
										+ " = new Array(");
								int table = tablesAliases.getTable(a);

								for (int c = 0; c < dcf.getNumOfColumns(table); c++) {
									html
											.append("'"
													+ dcf.getColumnName(table,
															c) + "'");
									if (c < dcf.getNumOfColumns(table) - 1)
										html.append(", ");
								}
								html.append(");\n");
							}

							html
									.append("function updateColumnsPulldown(colPulldown)\n"
											+ "{\n"
											+ "  var arrayName = \"COLUMNS_\" + eval(\"document.mainForm.ALIAS\" + colPulldown + \".value\");\n"
											+ "  var colsArray = eval(arrayName);\n"
											+ "  var docPulldown = eval(\"document.mainForm.COLUMNS\" + colPulldown);\n\n"
											+ "  docPulldown.length = 0;\n"
											+ "  docPulldown[0] = new Option('-');\n"
											+ "  docPulldown[0].value = 999;\n"
											+ "  for (i = 0; i < colsArray.length; i++) {\n"
											+ "    docPulldown[i+1] = new Option(colsArray[i]);\n"
											+ "    docPulldown[i+1].value = i;\n"
											+ "  }    \n"
											+ "}\n"
											+ "</script>\n"
											+ "<form method=\"post\" action=\"\" name=\"mainForm\">\n"
											+ "    <tr><td>&nbsp;</td></tr>\n"
											+ "    <tr> \n"
											+ "      <td> \n"
											+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "          <tr> \n"
											+ "            <td> \n"
											+ "              <table width=\"700\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "                <tr bgcolor=\"#000099\" align=\"center\"> \n"
											+ "                  <td height=\"20\" width=\"20\"> <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbShow
											+ "</b></font></td>\n"
											+ "                  <td height=\"20\" width=\"*\">  <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbAliasesAndColumns
											+ "</b></font></td>\n"
											+ "                  <td height=\"20\" width=\"40\"> <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbSort
											+ "</b></font></td>\n"
											+ "                  <td height=\"20\" width=\"40\"> <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbCondition
											+ "</b></font></td>\n"
											+ "                  <td height=\"20\" width=\"60\"> <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbValue
											+ "</b></font></td>\n"
											+ "                </tr>\n");

							int intAlias;

							int maxRows = query.size() + 5;
							if (maxRows > Query.MAX_NUM_OF_COLS)
								maxRows = Query.MAX_NUM_OF_COLS;

							for (int row = 0; row < maxRows; row++) {
								String checked = "";
								if (query.showColumn(row))
									checked = "checked";

								if (row < query.size())
									intAlias = query.getAlias(row);
								else
									intAlias = -1;

								html
										.append("                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td height=\"20\" width=\"20\"> \n"
												+ "                    <input type=\"checkbox\" "
												+ checked
												+ " name='SHOW"
												+ row
												+ "' value='1'>\n"
												+ "                  </td>\n"
												+ "                  <td height=\"20\" width=\"*\"> \n"
												+ "                    <select name=\"ALIAS"
												+ row
												+ "\" onChange=updateColumnsPulldown("
												+ row
												+ ")>\n"
												+ "                      <option value=\"999\" selected>-</option>\n");

								for (int a = 0; a < tablesAliases.size(); a++) {
									String sAlias = tablesAliases.getAlias(a);

									if (a == intAlias)
										html
												.append("                      <option selected value='"
														+ a
														+ "'>"
														+ sAlias
														+ "</option>\n");
									else
										html
												.append("                      <option value='"
														+ a
														+ "'>"
														+ sAlias
														+ "</option>\n");
								}

								html
										.append("                    </select>\n"
												+ "                    <select name=\"COLUMNS"
												+ row
												+ "\">\n"
												+ "                      <option value='999'>-</option>\n");

								if (row < query.size()) {
									int table = tablesAliases
											.getTable(intAlias);

									for (int c = 0; c < dcf
											.getNumOfColumns(table); c++) {
										if (c == query.getColumn(row))
											html
													.append("                      <option selected value='"
															+ c
															+ "'>"
															+ dcf
																	.getColumnName(
																			table,
																			c)
															+ "</option>\n");
										else
											html
													.append("                      <option value='"
															+ c
															+ "'>"
															+ dcf
																	.getColumnName(
																			table,
																			c)
															+ "</option>\n");
									}
								}

								html
										.append("                    </select>\n"
												+ "                  </td>\n"
												+ "                  <td height=\"20\" width=\"40\"> \n"
												+ "                    <select name=\"SORT"
												+ row + "\">\n");

								String[] sortDesc = { "-", "ASC", "DESC" };
								for (int c = 0; c < sortDesc.length; c++)
									if (c == query.getSort(row))
										html
												.append("                      <option selected value='"
														+ c
														+ "'>"
														+ sortDesc[c]
														+ "</option>\n");
									else
										html
												.append("                      <option value='"
														+ c
														+ "'>"
														+ sortDesc[c]
														+ "</option>\n");

								html
										.append("                    </select>\n"
												+ "                  </td>\n"
												+ "                  <td height=\"20\" width=\"40\"> \n"
												+ "                    <select name=\"CON"
												+ row + "\">\n");

								String[] consDesc = { "-", "=", "&lt;&gt;",
										"&lt;", "&gt;", "&lt;=", "&gt;=" };
								for (int c = 0; c < consDesc.length; c++)
									if (c == query.getConditionType(row))
										html
												.append("                      <option selected value='"
														+ c
														+ "'>"
														+ consDesc[c]
														+ "</option>\n");
									else
										html
												.append("                      <option value='"
														+ c
														+ "'>"
														+ consDesc[c]
														+ "</option>\n");

								String value = query.getConditionValue(row);
								if (value == null)
									value = "";

								html
										.append("                    </select>\n"
												+ "                  </td>\n"
												+ "                  <td height=\"20\" width=\"10\"> \n"
												+ "                    <input type=\"text\" name=\"CONVALUE"
												+ row
												+ "\" size=\"15\" maxlength=\"40\" value='"
												+ value
												+ "'>\n"
												+ "                  </td>\n"
												+ "                </tr>\n");
							} // for (int row ..

							html
									.append("                <tr bgcolor=\"#CCCCCC\" align=\"middle\"> \n"
											+ "                  <td colspan=\"5\"> \n"
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
							out.println(htmlOutput.errorPage("2431"));
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
