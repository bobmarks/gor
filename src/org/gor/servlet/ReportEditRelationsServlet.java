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
import org.gor.data.Relationships;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This servlet displays the relations section of the system and provides a
 * means for updating a report file for relations.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditRelationsServlet extends HttpServlet {

	private static final long serialVersionUID = 930630270857614372L;

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
	 * Do post.
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
		HtmlOutput htmlOutput = new HtmlOutput("2.4.2", session);

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

						String rbParentAliasesColumns = rb
								.getString("ParentAliasesColumns");
						String rbChildAliasesColumns = rb
								.getString("ChildAliasesColumns");
						String rbOK = rb.getString("OK");

						// Check if servlet is being run for first time.
						if (req.getParameter("NUM_OF_ROWS") != null) {
							int numOfRows = Integer.parseInt(req
									.getParameter("NUM_OF_ROWS"));
							Relationships newRelationships = new Relationships();

							int[] relsInfo = new int[4];

							// go through all the rows and
							for (int row = 0; row < numOfRows; row++) {
								int item = row * 2;
								relsInfo[0] = Integer.parseInt(req
										.getParameter("ALIAS" + item));
								relsInfo[1] = Integer.parseInt(req
										.getParameter("COLUMN" + item));
								relsInfo[2] = Integer.parseInt(req
										.getParameter("ALIAS" + (item + 1)));
								relsInfo[3] = Integer.parseInt(req
										.getParameter("COLUMN" + (item + 1)));

								boolean okToAdd = true;
								for (int i = 0; i < 4; i++)
									if (relsInfo[i] == 999)
										okToAdd = false;

								// selected add to
								if (okToAdd)
									newRelationships.add(relsInfo);
							}

							// update report file
							ReportFile.update(report, newRelationships);
						}

						// Show selection page.
						ReportFile rf = new ReportFile(report);
						TablesAndAlias tablesAliases = rf.getTablesAndAlias();
						// make sure there are more than 1
						if (tablesAliases.size() > 1) {

							DatabaseConnectionFile dcf = new DatabaseConnectionFile(
									rf.getDatabase());
							Relationships rels = rf.getRelationships();

							StringBuffer html = new StringBuffer(htmlOutput
									.pageTop()
									+ "<script language=JavaScript>\n"
									+ "var COLUMNS_999 = new Array();\n");

							// create JavaScript array for
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
											+ "  var docPulldown = eval(\"document.mainForm.COLUMN\" + colPulldown);\n"
											+ "\n"
											+ "  docPulldown.length = 0;\n"
											+ "  docPulldown[0] = new Option('-');\n"
											+ "  docPulldown[0].value = 999;\n"
											+ "  for (i = 0; i < colsArray.length; i++) {\n"
											+ "    docPulldown[i+1] = new Option(colsArray[i]);\n"
											+ "    docPulldown[i+1].value = i;\n"
											+ "  }    \n"
											+ "}\n"
											+ "</script>\n"
											+ "  <form method=\"post\" action=\"\" name=\"mainForm\">\n"
											+ "  <tr><td>&nbsp;</td></tr>\n"
											+ "    <tr> \n"
											+ "      <td> \n"
											+ "        <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#000000\">\n"
											+ "          <tr> \n"
											+ "            <td> \n"
											+ "              <table width=\"700\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "                <tr bgcolor=\"#000099\"> \n"
											+ "                  <td nowrap align=\"center\"> \n"
											+ "                    <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbParentAliasesColumns
											+ "</b></font>\n"
											+ "                  </td>\n"
											+ "                  <td nowrap align=\"center\"> \n"
											+ "                    <font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbChildAliasesColumns
											+ "</b></font>\n"
											+ "                  </td>\n"
											+ "                </tr>\n");

							for (int row = 0; row < tablesAliases.size(); row++) {
								int item = row * 2;

								html
										.append("                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td width=\"300\"> \n"
												+ "                    <select name='ALIAS"
												+ item
												+ "' onChange=updateColumnsPulldown("
												+ item
												+ ")>\n"
												+ "                      <option value=\"999\" selected>-</option>\n");

								int intPrimaryAlias = -1;
								int intPrimaryColumn = -1;
								int intSecondaryAlias = -1;
								int intSecondaryColumn = -1;

								if (row < rels.size()) {
									intPrimaryAlias = rels.getPrimaryAlias(row);
									intPrimaryColumn = rels
											.getPrimaryColumn(row);
									intSecondaryAlias = rels
											.getSecondaryAlias(row);
									intSecondaryColumn = rels
											.getSecondaryColumn(row);
								}

								for (int a = 0; a < tablesAliases.size(); a++) {
									String sAlias = tablesAliases.getAlias(a);
									if (intPrimaryAlias == a)
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
												+ "                    <select name='COLUMN"
												+ item
												+ "'>\n"
												+ "                      <option value=\"999\" selected>-</option>\n");

								if (row < rels.size()) {
									int table = tablesAliases
											.getTable(intPrimaryAlias);

									for (int c = 0; c < dcf
											.getNumOfColumns(table); c++) {
										if (c == intPrimaryColumn)
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
												+ "                  <td width=\"300\"> \n"
												+ "                    <select name='ALIAS"
												+ (item + 1)
												+ "' onChange=updateColumnsPulldown("
												+ (item + 1)
												+ ")>\n"
												+ "                      <option value=\"999\" selected>-</option>\n");

								for (int a = 0; a < tablesAliases.size(); a++) {
									String sAlias = tablesAliases.getAlias(a);
									if (intSecondaryAlias == a)
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
												+ "                    <select name='COLUMN"
												+ (item + 1)
												+ "'>\n"
												+ "                      <option value=\"999\" selected>-</option>\n");

								if (row < rels.size()) {
									int table = tablesAliases
											.getTable(intSecondaryAlias);

									for (int c = 0; c < dcf
											.getNumOfColumns(table); c++) {
										if (c == intSecondaryColumn)
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

								html.append("                    </select>\n"
										+ "                  </td>\n"
										+ "                </tr>\n");
							}

							html
									.append("                <tr bgcolor=\"#CCCCCC\" align=\"middle\"> \n"
											+ "                  <td colspan=\"2\"> \n"
											+ "                    <input type=\"hidden\" name=\"NUM_OF_ROWS\" value=\""
											+ tablesAliases.size()
											+ "\">\n"
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
							out.println(htmlOutput.errorPage("2421"));
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