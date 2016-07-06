//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.data.DatabaseConnectionFile;
import org.gor.data.LabelLocales;
import org.gor.data.Labels;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Query;
import org.gor.util.Site;

/**
 * This servlet is one of the most complex in the system. It is split into two
 * input screens. The first screen prompts the user to select an existing set of
 * labels or to select a new set of labels from the supplied pull-down. Once the
 * user has selected this a screen is displayed consisting of textboxes for
 * items like the title, subtitle, and visible column names.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditLabelsServlet extends HttpServlet {

	private static final long serialVersionUID = -2559730891435375798L;

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
		HtmlOutput htmlOutput = new HtmlOutput("2.4.6", session);

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
						// ResourceBundle interface labels
						//======================================================
						// ============

						ResourceBundle rb = ResourceBundle.getBundle(
								"resources.labels", Site.getLocale(session));

						String rbLabels = rb.getString("Labels");
						String rbEdit = rb.getString("Edit");
						String rbor = rb.getString("or");
						String rbNew = rb.getString("New");
						String rbReset = rb.getString("Reset");
						String rbOK = rb.getString("OK");
						String rbTitle = rb.getString("Title");
						String rbSubtitle = rb.getString("Subtitle");
						String rbFooter = rb.getString("Footer");
						String rbBack = rb.getString("Back");

						//======================================================
						// ============
						// Create report instance and retrieve tables / aliases,
						// columns /
						// conditions and labels
						//======================================================
						// ============

						ReportFile rf = new ReportFile(report);
						DatabaseConnectionFile dcf = new DatabaseConnectionFile(
								rf.getDatabase());
						TablesAndAlias tablesAliases = rf.getTablesAndAlias();
						Query query = rf.getQuery();
						Labels labels = rf.getLabels();

						// Create an instance of the label locales (list of all
						// the
						// langauges the user can choose from in the "New"
						// pulldown)
						LabelLocales labelLocales = new LabelLocales();

						if (req.getParameter("NEW") == null) {
							StringBuffer html = new StringBuffer(
									htmlOutput.pageTop()
											+ "<form method=\"post\" action=\"\" name=\"mainForm\">\n"
											+ "    <tr><td>&nbsp; </td></tr>\n"
											+ "    <tr> \n"
											+ "      <td> \n"
											+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
											+ "          <tr> \n"
											+ "            <td> \n"
											+ "              <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"500\">\n"
											+ "                <tr bgcolor=\"#000099\" align=\"center\"> \n"
											+ "                  <td height=\"20\" bgcolor=\"#000099\" colspan=\"2\"> \n"
											+ "                    <p><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbLabels
											+ "</b></font></p>\n"
											+ "                  </td>\n"
											+ "                </tr>\n"
											+ "                <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                  <td valign=\"top\" align=\"right\" width=\"150\">\n"
											+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
											+ rbEdit
											+ "</b></font></td>\n"
											+ "                  <td align=\"left\" valign=\"top\" width=\"350\">\n"
											+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\">"
											+ "                    <input type='radio' name='EDIT' value='-1' checked>-\n");

							int numOfLocalesUsed = labels.getNumOfLocalesUsed();

							for (int i = 0; i < numOfLocalesUsed; i++) {
								int num = labels.getLocaleUsed(i);
								html
										.append("<br><input type=\"radio\" name=\"EDIT\" value='"
												+ num
												+ "'>"
												+ labelLocales.getName(num));
							}

							html
									.append("</font></td>\n"
											+ "                </tr>\n"
											+ "                <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                  <td align=\"right\" width=\"150\"><font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"> \n"
											+ "                    <i>"
											+ rbor
											+ "</i> <b>"
											+ rbNew
											+ "</b></font></td>\n"
											+ "                  <td align=\"left\" width=\"350\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
											+ "                    <select name=\"NEW\">\n"
											+ "                      <option value=\"-1\" selected>-</option>\n");

							int p = 0;
							for (int i = 0; i < labelLocales.getNumOfLabels(); i++) {

								if (labels.getLocaleUsed(p) == i) {
									if (p < labels.getNumOfLocalesUsed() - 1)
										p++;
								} else
									html
											.append("                      <option value='"
													+ i
													+ "'>"
													+ labelLocales.getName(i)
													+ "</option>\n");
							}

							html
									.append("                    </select>\n"
											+ "                  </font> </td>\n"
											+ "                </tr>\n"
											+ "                <tr bgcolor=\"#CCCCCC\" align=\"center\"> \n"
											+ "                  <td colspan=\"2\"><font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b> \n"
											+ "                    </b></font><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
											+ "                    <input type=\"reset\" name=\"Reset\" value=\""
											+ rbReset
											+ "\">\n"
											+ "                    <input type=\"submit\" name=\"Submit\" value=\""
											+ rbOK
											+ "\">\n"
											+ "                    </font> </td>\n"
											+ "                </tr>\n"
											+ "              </table>\n"
											+ "            </td>\n"
											+ "          </tr>\n"
											+ "        </table>\n"
											+ "      </td>\n" + "    </tr>\n"
											+ "</form>\n"
											+ htmlOutput.pageBottom());

							out.println(html.toString());
						} 
						else if (req.getParameter("TITLE") == null) 
						{
							boolean editting = false;
							int intLocale;

							int intEdit = Integer.parseInt(req
									.getParameter("EDIT"));
							int intNew = Integer.parseInt(req
									.getParameter("NEW"));

							if (intEdit == -1 && intNew == -1)
								out.println(htmlOutput.errorPage("2461"));
							else if (intEdit != -1 && intNew != -1)
								out.println(htmlOutput.errorPage("2462"));
							else {
								if (intEdit != -1)
									editting = true;

								if (editting)
									intLocale = intEdit;
								else
									intLocale = intNew;

								String title = "";
								String subTitle = "";
								String footer = "";
								String newOrEdit = "N"; // N = new, E = edit

								if (editting) {
									labels.setCurrentSetOfLabels(intLocale);
									title = labels.getTitle();
									subTitle = labels.getSubTitle();
									footer = labels.getFooter();
									newOrEdit = "E";
								}

								String localeText = labelLocales
										.getName(intLocale);

								StringBuffer html = new StringBuffer(
										htmlOutput.pageTop()
												+ "<form method=\"post\" action=\"\" name=\"mainForm\">\n"
												+ "    <tr> <td>&nbsp; </td></tr>\n"
												+ "    <tr> \n"
												+ "      <td> \n"
												+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
												+ "          <tr> \n"
												+ "            <td> \n"
												+ "              <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"700\">\n"
												+ "                <tr bgcolor=\"#000099\" align=\"center\"> \n"
												+ "                  <td height=\"20\" bgcolor=\"#000099\" colspan=\"2\"> \n"
												+ "                    <p><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b> \n"
												+ rbLabels
												+ " - "
												+ localeText
												+ "</b></font></p>\n"
												+ "                  </td>\n"
												+ "                </tr>\n"
												+ "                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td align=\"right\" width=\"250\">\n"
												+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
												+ rbTitle
												+ "</b></font>\n"
												+ "                  </td>\n"
												+ "                  <td align=\"left\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
												+ "                    <input type=\"text\" name=\"TITLE\" size=\"30\" maxlength=\"50\" value='"
												+ title
												+ "'>\n"
												+ "                    </font> </td>\n"
												+ "                </tr>\n"
												+ "                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td align=\"right\" width=\"250\" valign=\"top\">\n"
												+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
												+ rbSubtitle
												+ "</b></font>\n"
												+ "                  </td>\n"
												+ "                  <td align=\"left\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
												+ "                    <input type=\"text\" name=\"SUBTITLE\" size=\"60\" maxlength=\"256\" value='"
												+ subTitle
												+ "'>\n"
												+ "                    </font> </td>\n"
												+ "                </tr>\n"
												+ "                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td align=\"right\" width=\"250\" valign=\"top\">\n"
												+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
												+ rbFooter
												+ "</b></font>\n"
												+ "                  </td>\n"
												+ "                  <td align=\"left\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
												+ "                    <input type=\"text\" name=\"FOOTER\" size=\"60\" maxlength=\"256\" value='"
												+ footer
												+ "'>\n"
												+ "                    </font> </td>\n"
												+ "                </tr>\n");

								int intCol, intQueryAlias, intQueryTable, intQueryColumn;
								String aliasAndColumnNum, aliasAndColumnText, labelText;

								for (int i = 0; i < query.getNumOfVisibile(); i++) {
									intCol = query.getIndexFromVisible(i);
									intQueryAlias = query.getAlias(intCol);
									intQueryTable = tablesAliases
											.getTable(intQueryAlias);
									intQueryColumn = query.getColumn(intCol);

									aliasAndColumnNum = intQueryAlias + ","
											+ intQueryColumn;
									aliasAndColumnText = tablesAliases
											.getAlias(intQueryAlias)
											+ "."
											+ dcf.getColumnName(intQueryTable,
													intQueryColumn);

									labelText = "";
									if (editting)
										labelText = labels.getLabel(
												intQueryAlias, intQueryColumn);

									html
											.append("                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td align=\"right\" width=\"250\">\n"
													+ "                    <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
													+ aliasAndColumnText
													+ "</b></font>\n"
													+ "                 </td>\n"
													+ "                  <td align=\"left\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
													+ "                    <input type=\"text\" name='"
													+ aliasAndColumnNum
													+ "' + size=\"20\" maxlength=\"60\" value=\""
													+ labelText
													+ "\">\n"
													+ "                    </font> </td>\n"
													+ "                </tr>\n");
								}

								html
										.append("                <tr bgcolor=\"#CCCCCC\"> \n"
												+ "                  <td align=\"middle\" colspan=\"2\"><font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b> \n"
												+ "                    </b></font><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\"> \n"
												+ "                    <input type=\"hidden\" name=\"NEW_OR_EDIT\" value=\""
												+ newOrEdit
												+ "\">\n"
												+ "                    <input type=\"hidden\" name=\"NEW\" value=\"\">\n"
												+ "                    <input type=\"hidden\" name=\"LOCALE\" value='"
												+ intLocale
												+ "'>\n"
												+ "                    <input onClick=\"history.back()\" border='0' type=\"button\" value=\""
												+ rbBack
												+ "\" name=\"button\">\n"
												+ "                    <input type=\"submit\" name=\"Submit\" value=\""
												+ rbOK
												+ "\">\n"
												+ "                    </font> </td>\n"
												+ "                </tr>\n"
												+ "              </table>\n"
												+ "            </td>\n"
												+ "          </tr>\n"
												+ "        </table>\n"
												+ "      </td>\n"
												+ "    </tr>\n"
												+ "</form>\n"
												+ htmlOutput.pageBottom());

								out.println(html.toString());
							}
						} else {
							int intLocale = Integer.parseInt(req
									.getParameter("LOCALE"));
							String title = req.getParameter("TITLE");
							String subTitle = req.getParameter("SUBTITLE");
							String footer = req.getParameter("FOOTER");
							String text;
							boolean newMode = req.getParameter("NEW_OR_EDIT")
									.equals("N");

							int intCol, intQueryAlias, intQueryColumn;

							if (newMode) {
								labels.createNewLabelData(intLocale, title,
										subTitle, footer);

								for (int i = 0; i < query.getNumOfVisibile(); i++) {
									intCol = query.getIndexFromVisible(i);
									intQueryAlias = query.getAlias(intCol);
									intQueryColumn = query.getColumn(intCol);

									String aliasAndColumnNum = intQueryAlias
											+ "," + intQueryColumn;
									text = req.getParameter(aliasAndColumnNum);

									labels.addLabel(intQueryAlias,
											intQueryColumn, text);
								}
								labels.addCurLabelToList();
								ReportFile.update(report, labels);
							} // if not creating new labels then user is
								// editting them
							else {
								// move to the correct set of labels.
								labels.setCurrentSetOfLabels(intLocale);
								labels.updateLabelData(title, subTitle, footer);

								for (int i = 0; i < query.getNumOfVisibile(); i++) {
									intCol = query.getIndexFromVisible(i);
									intQueryAlias = query.getAlias(intCol);
									intQueryColumn = query.getColumn(intCol);

									String aliasAndColumnNum = intQueryAlias
											+ "," + intQueryColumn;
									text = req.getParameter(aliasAndColumnNum);

									labels.updateLabel(intQueryAlias,
											intQueryColumn, text);
								}

								ReportFile.update(report, labels);
							}

							res.sendRedirect("/gor/ReportEditLabels");
						}
					} catch (Exception e) {
						e.printStackTrace(new PrintStream(out));
						// htmlOutput.errorGeneral());
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
