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

import org.gor.data.ReportFile;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * Little servlet which creates a new report file.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportNewServlet extends HttpServlet {

	private static final long serialVersionUID = 687594329903576441L;

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
		HtmlOutput htmlOutput = new HtmlOutput("2.1", session);

		if (userName != null) {
			if (Site.newReportPriv(session)) {
				try {
					// ResourceBundle stuff
					ResourceBundle rb = ResourceBundle.getBundle(
							"resources.labels", Site.getLocale(session));

					String rbNewReport = rb.getString("NewReport");
					String rbReportFileName = rb.getString("ReportFileName");
					String rbDatabaseSelector = rb
							.getString("DatabaseSelector");
					String rbOK = rb.getString("OK");
					String rbFileName = rb.getString("FileName");
					String rbDatabaseConnection = rb
							.getString("DatabaseConnection");
					String rbCreatedBy = rb.getString("CreatedBy");
					String rbCreatedDate = rb.getString("CreatedDate");
					String rbNote = rb.getString("Note");
					String rbFileIsLocked = rb.getString("FileIsLocked");

					// Check to see if user has submitted form
					String sNewFile = req.getParameter("FILENAME");

					if (sNewFile != null) {
						try {
							// if user was working on another report then unlock
							// that report
							// so other uses can edit it if they want
							String prevReportFileName = (String) session.getAttribute("report");
							String prevReportEdit = (String) session.getAttribute("report_edit");
							if (prevReportEdit == null)
								prevReportEdit = "0";

							if (prevReportFileName != null
									&& prevReportEdit.equals("1")) {
								ReportFile prevReport = new ReportFile(
										prevReportFileName);
								prevReport.editUnLock();
							}
							sNewFile = sNewFile.toLowerCase();

							String database = req.getParameter("DATABASE");

							// do validation of variables
							boolean validFileName = (!sNewFile.equals(""));
							// following characters arn't allowed = \/: *?"<>.
							boolean validFileNameChars = (sNewFile
									.indexOf("\\") == -1
									&& sNewFile.indexOf("/") == -1
									&& sNewFile.indexOf(":") == -1
									&& sNewFile.indexOf(" ") == -1
									&& sNewFile.indexOf("*") == -1
									&& sNewFile.indexOf("?") == -1
									&& sNewFile.indexOf("\"") == -1
									&& sNewFile.indexOf("<") == -1
									&& sNewFile.indexOf(">") == -1 && sNewFile
									.indexOf(".") == -1);
							boolean validDatabase = (database != null);

							boolean success = (validFileName
									&& validFileNameChars && validDatabase);

							if (!success) {
								StringBuffer html = new StringBuffer(htmlOutput
										.errorTop());

								if (!validFileName)
									html.append("<li>"
											+ htmlOutput.error("311")
											+ "</li><br>");
								if (!validFileNameChars)
									html.append("<li>"
											+ htmlOutput.error("312")
											+ "</li><br>");
								if (!validDatabase)
									html.append("<li>"
											+ htmlOutput.error("313")
											+ "</li><br>");

								html.append(htmlOutput.errorBottom());
								out.println(html.toString());
							} else {
								ReportFile rf = new ReportFile();

								// add file extension
								sNewFile += ".gor";
								if (rf.createFile(sNewFile, database, userName)) {
									// create output

									session.setAttribute("report", sNewFile);
									session.setAttribute("report_edit", "1");

									out
											.println(htmlOutput.pageTop()
													+ "    <tr> <td>&nbsp; </td></tr>\n"
													+ "    <form method=\"post\" name=\"mainForm\" action=\"\">\n"
													+ "    <tr> \n"
													+ "      <td> \n"
													+ "        <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
													+ "          <tr bgcolor=\"#000000\"> \n"
													+ "            <td> \n"
													+ "              <table  width=\"500\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\"><font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
													+ rbNewReport
													+ "</b></font></td>\n"
													+ "                </tr>\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
													+ rbFileName
													+ "</b></font></td>\n"
													+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
													+ sNewFile
													+ "</font> \n"
													+ "                  </td>\n"
													+ "                </tr>\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
													+ rbDatabaseConnection
													+ "</b></font></td>\n"
													+ "                  <td align=\"left\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
													+ rf.getDatabase()
													+ "</font> \n"
													+ "                  </td>\n"
													+ "                </tr>\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td align=\"right\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
													+ rbCreatedBy
													+ "</b></font></td>\n"
													+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
													+ rf.getCreatedBy()
													+ "</font> \n"
													+ "                  </td>\n"
													+ "                </tr>\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td align=\"right\" valign=\"top\" bgcolor=\"#CCCCCC\" width=\"250\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
													+ rbCreatedDate
													+ "</b></font></td>\n"
													+ "                  <td align=\"left\" width=\"250\"> <font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\">"
													+ rf.getCreatedDate()
													+ "</font></td>\n"
													+ "                </tr>\n"
													+ "                <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                  <td colspan=\"2\" align=\"left\"> \n"
													+ "                    <p><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#ff0000\"><b>"
													+ rbNote
													+ ":</b> \n"
													+ "<font color=\"#000000\">"
													+ rbFileIsLocked
													+ " </font></font></p>\n"
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
								} else
									out.println(htmlOutput.errorIO());
							}
						} catch (Exception ge) {
							out.println(htmlOutput.errorGeneral());
						}
					} else {
						//======================================================
						// ==============
						// Create final HTML output.
						//======================================================
						// ==============

						StringBuffer html = new StringBuffer(
								htmlOutput.pageTop()
										+ "<form method=\"post\" action=\"\">\n"
										+ "<tr><td>&nbsp;</td></tr>\n"
										+ "<tr><td>\n"
										+ "  <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
										+ "    <tr bgcolor=\"#000000\"> \n"
										+ "      <td>\n"
										+ "        <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"300\">\n"
										+ "          <tr bgcolor=\"#CCCCCC\"> \n"
										+ "            <td bgcolor=\"#000099\" align=\"center\">\n"
										+ "              <font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
										+ rbNewReport
										+ "</b></font></td>\n"
										+ "          </tr>\n"
										+ "          <tr bgcolor=\"#CCCCCC\"> \n"
										+ "            <td><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbReportFileName
										+ ":</b></font></font></td>\n"
										+ "          </tr>\n"
										+ "          <tr bgcolor=\"#CCCCCC\"> \n"
										+ "            <td>\n"
										+ "              <input type=\"text\" name=\"FILENAME\" maxlength=\"20\">\n"
										+ "            </td>\n"
										+ "          </tr>\n"
										+ "          <tr bgcolor=\"#CCCCCC\"> \n"
										+ "            <td><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbDatabaseSelector
										+ ":</b></font></font></td>\n"
										+ "          </tr>\n"
										+ "          <tr bgcolor=\"#CCCCCC\"> \n"
										+ "            <td align=\"left\"> \n"
										+ "              <select name=\"DATABASE\" size=\"10\">\n");

						String[] dbFiles = Site
								.fileList(Site.getDatabaseDir());
						if (dbFiles.length == 0)
							html
									.append("                    <option value=\"-1\" selected>"
											+ rbDatabaseConnection
											+ "</option>\n");
						else {
							for (int i = 0; i < dbFiles.length; i++)
								html
										.append("                    <option value='"
												+ dbFiles[i]
												+ "'>"
												+ dbFiles[i] + "</option>\n");
						}

						html.append("              </select>\n"
								+ "            </td>\n" + "          </tr>\n");
						if (dbFiles.length != 0)
							html
									.append("          <tr bgcolor=\"#CCCCCC\"> \n"
											+ "            <td align=\"middle\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><b> \n"
											+ "              <input type=\"submit\" name=\"Submit\" value=\""
											+ rbOK
											+ "\">\n"
											+ "              </b></font> </td>\n"
											+ "          </tr>\n");
						html.append("        </table>\n" + "      </td>\n"
								+ "    </tr>\n" + "  </table>\n"
								+ "</td></tr>\n" + "</form>\n"
								+ htmlOutput.pageBottom());

						out.println(html.toString());
					}
				} catch (Exception e) {
					out.println(htmlOutput.errorGeneral());
				}
			} else
				out.println(htmlOutput.errorNoPrivilege());
		} else
			out.println(htmlOutput.errorNoSession());
	}
}
