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

import org.gor.data.Groups;
import org.gor.data.Labels;
import org.gor.data.Relationships;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Query;
import org.gor.util.Site;

/**
 * This servlet provides an interface for openening an existing report.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportOpenServlet extends HttpServlet {

	private static final long serialVersionUID = -6331492284209861314L;

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

		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.2", session);

		if (userName != null) {

			// Check to see if servlet opened for first time.
			String reportFileName = req.getParameter("REPORT");
			String edit = req.getParameter("EDIT");
			if (edit == null)
				edit = "0";

			//==================================================================
			// ====
			// ResourceBundle stuff
			//==================================================================
			// ====

			ResourceBundle rb = ResourceBundle.getBundle("resources.labels",
					Site.getLocale(session));

			String rbOpenReport = rb.getString("OpenReport");
			String rbSelectReportFromList = rb
					.getString("SelectReportFromList");
			String rbEditReport = rb.getString("EditReport");
			String rbNoReportsAvailable = rb.getString("NoReportsAvailable");
			String rbYes = rb.getString("Yes");
			String rbNo = rb.getString("No");
			String rbOK = rb.getString("OK");
			String rbFileName = rb.getString("FileName");
			String rbDatabaseConnection = rb.getString("DatabaseConnection");
			String rbCreatedBy = rb.getString("CreatedBy");
			String rbCreatedDate = rb.getString("CreatedDate");
			String rbCurrentEditor = rb.getString("CurrentEditor");
			String rbTables = rb.getString("Tables");
			String rbRelations = rb.getString("Relations");
			String rbColumns = rb.getString("Columns");
			String rbGroups = rb.getString("Groups");
			String rbLabels = rb.getString("Labels");
			String rbNote = rb.getString("Note");
			String rbFileIsLocked = rb.getString("FileIsLocked");

			if (reportFileName != null) {
				
				// if user was working on another report then unlock that report
				// so other uses can edit it if they want
				String prevReportFileName = (String) session.getAttribute("report");
				String prevReportEdit = (String) session.getAttribute("report_edit");
				if (prevReportEdit == null)
					prevReportEdit = "0";

				if (prevReportFileName != null && prevReportEdit.equals("1")) {
					ReportFile prevReport = new ReportFile(prevReportFileName);
					prevReport.editUnLock();
				}
				
				ReportFile reportFile = new ReportFile(reportFileName);
				boolean editOK = true;
				if (edit.equals("1")) {
					// Check noone else is working on that report.
					if (reportFile.getEdittedBy().equals("")
							|| reportFile.getEdittedBy().equals(userName))
						reportFile.editLock(userName);
					else
						editOK = false;
				}

				if (editOK) {
					// update session
					session.setAttribute("report", reportFileName);
					session.setAttribute("report_edit", edit);

					TablesAndAlias tablesAliases = reportFile
							.getTablesAndAlias();
					Relationships relationships = reportFile.getRelationships();
					Query query = reportFile.getQuery();
					Groups groups = reportFile.getGroups();
					Labels labels = reportFile.getLabels();

					StringBuffer html = new StringBuffer(
							htmlOutput.pageTop()
									+ "    <tr> <td>&nbsp; </td></tr>\n"
									+ "    <form method=\"post\" name=\"mainForm\" action=\"\">\n"
									+ "    <tr> \n"
									+ "      <td> \n"
									+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
									+ "          <tr bgcolor=\"#000000\"> \n"
									+ "            <td> \n"
									+ "              <table  width=\"500\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><b>"
									+ rbOpenReport
									+ " </b></font></td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbFileName
									+ "</b></font></td>\n"
									+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ reportFileName
									+ "</font> \n"
									+ "                  </td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbDatabaseConnection
									+ " </b></font></td>\n"
									+ "                  <td align=\"left\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ reportFile.getDatabase()
									+ "</font> \n"
									+ "                  </td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbCreatedBy
									+ "</b></font></td>\n"
									+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ reportFile.getCreatedBy()
									+ "</font> \n"
									+ "                  </td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" valign=\"top\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbCreatedDate
									+ "</b></font></td>\n"
									+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ reportFile.getCreatedDate()
									+ "</font></td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
									+ rbCurrentEditor
									+ "</b></font></td>\n"
									+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ reportFile.getEdittedBy()
									+ "</font></td>\n"
									+ "                </tr>\n"
									+ "                <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" colspan=\"2\">\n"
									+ "                    <table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
									+ "                      <tr bgcolor=\"#CCCCCC\">\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <b><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ rbTables
									+ "</font></b>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <b><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ rbRelations
									+ "</font></b>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <b><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ rbColumns
									+ "</font></b>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <b><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ rbGroups
									+ "</font></b>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <b><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
									+ rbLabels
									+ "</font></b>\n"
									+ "                        </td>\n"
									+ "                      </tr>\n"
									+ "                      <tr bgcolor=\"#CCCCCC\">\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <font color=\"#000000\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
									+ tablesAliases.size()
									+ "</font></font>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <font color=\"#000000\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
									+ relationships.size()
									+ "</font></font>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <font color=\"#000000\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
									+ query.size()
									+ "</font></font>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <font color=\"#000000\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
									+ groups.size()
									+ "</font></font>\n"
									+ "                        </td>\n"
									+ "                        <td width=\"20%\" align=\"center\">\n"
									+ "                          <font color=\"#000000\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">"
									+ labels.getNumOfLocalesUsed()
									+ "</font></font>\n"
									+ "                        </td>\n"
									+ "                      </tr>\n"
									+ "                    </table>\n"
									+ "                  </td>\n"
									+ "                </tr>\n");

					if (edit.equals("1"))
						html
								.append("                <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                  <td colspan=\"2\" align=\"left\"> \n"
										+ "                    <p><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#ff0000\"><b>"
										+ rbNote
										+ ":</b> \n"
										+ "<font color=\"#000000\">"
										+ rbFileIsLocked
										+ " </font></font></p>\n"
										+ "                  </td>\n"
										+ "                </tr>\n");

					html.append("              </table>\n"
							+ "            </td>\n" + "          </tr>\n"
							+ "        </table>\n" + "      </td>\n"
							+ "    </tr>\n" + "  </form>\n"
							+ htmlOutput.pageBottom());

					out.println(html.toString());
				} else
					out.println(htmlOutput.errorNoEdit(reportFile
							.getEdittedBy()));
			}
			// Display list of reports.
			else {
				try {
					StringBuffer html = new StringBuffer(
							htmlOutput.pageTop()
									+ "  <tr><td>&nbsp;</td></tr>\n"
									+ "  <tr>\n"
									+ "    <td>\n"
									+ "    <form method=\"post\" action=\"\">\n"
									+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
									+ "        <tr bgcolor=\"#000000\"> \n"
									+ "          <td>\n"
									+ "            <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"300\">\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td bgcolor=\"#000099\" align=\"center\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><b>"
									+ rbOpenReport
									+ "</b></font></td>\n"
									+ "              </tr>\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><font color=\"#000000\"><b>"
									+ rbSelectReportFromList
									+ ":</b></font></font></td>\n"
									+ "              </tr>\n"
									+ "              <tr bgcolor=\"#CCCCCC\"> \n"
									+ "                <td align=\"left\"> \n"
									+ "                  <select name=\"REPORT\" size=\"10\">\n");

					String[] gorFiles = Site
							.fileList(Site.getReportsDir());
					if (gorFiles.length == 0)
						html
								.append("                    <option value=\"-1\" selected>"
										+ rbNoReportsAvailable + "</option>\n");
					else
						for (int i = 0; i < gorFiles.length; i++)
							html.append("                    <option value='"
									+ gorFiles[i] + "'>" + gorFiles[i]
									+ "</option>\n");

					html.append("                  </select>\n"
							+ "                </td>\n"
							+ "              </tr>\n");

					if (gorFiles.length != 0) {
						if (Site.editReportPriv(session))
							html
									.append("              <tr bgcolor=\"#CCCCCC\"> \n"
											+ "                <td colspan=\"2\" align=\"center\"><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
											+ rbEditReport
											+ "</b> \n"
											+ "                  <input type=\"radio\" name=\"EDIT\" value=\"1\">"
											+ rbYes
											+ "\n"
											+ "                  <input type=\"radio\" name=\"EDIT\" checked value=\"0\">"
											+ rbNo
											+ "</font>\n"
											+ "                  </td>\n"
											+ "              </tr>\n");

						html
								.append("              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"middle\">\n"
										+ "                  <input type=\"submit\" name=\"Submit\" value=\""
										+ rbOK
										+ "\">\n"
										+ "                </td>\n"
										+ "              </tr>\n");
					}

					html.append("            </table>\n" + "          </td>\n"
							+ "        </tr>\n" + "      </table>\n"
							+ "    </td>\n" + "  </tr>\n" + "</form>\n"
							+ htmlOutput.pageBottom());

					out.println(html.toString());
				} catch (Exception e) {
					out.println(htmlOutput.errorGeneral());
				}
			}
		} else
			out.println(htmlOutput.errorNoSession());
	}
}