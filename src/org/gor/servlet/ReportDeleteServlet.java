//==============================================================================
//
//  Generic Online Reporting
//
//	The following source code is for a final year computing science project.
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
 * This servlet displays a list of available reports and enables the user to
 * select one and delete it off the server.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 4408410517340738653L;

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
		HtmlOutput htmlOutput = new HtmlOutput("2.3", session);

		if (userName != null) {
			if (Site.deleteReportPriv(session)) {

				// ResourceBundle stuff
				ResourceBundle rb = ResourceBundle.getBundle("resources.labels",
						Site.getLocale(session));
				
				String rbDeleteReport = rb.getString("DeleteReport");
				String rbSelectReportFromList = rb
						.getString("SelectReportFromList");
				String rbAreYouSure = rb.getString("AreYouSure");
				String rbNoReportsAvailable = rb
						.getString("NoReportsAvailable");
				String rbYes = rb.getString("Yes");
				String rbNo = rb.getString("No");
				String rbOK = rb.getString("OK");

				// Check to see if servlet opened for first time.
				String rbFile = req.getParameter("REPORT");
				String sure = req.getParameter("SURE");

				if (rbFile != null) {
					if (sure.equals("1")) {
						File fileToDelete = new File(Site.getReportsDir() + rbFile);
						try {
							fileToDelete.delete();
						} catch (Exception e) {
						} // don't do anything
					}
				}

				try {
					// Create final HTML output.
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
									+ rbDeleteReport
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

					String[] gorFiles = Site.fileList(Site.getReportsDir());
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

					if (gorFiles.length != 0)
						html
								.append("              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"center\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#000000\"><b>"
										+ rbAreYouSure
										+ "</b>\n"
										+ "                  <input type=\"radio\" name=\"SURE\" value=\"1\">"
										+ rbYes
										+ "                  <input type=\"radio\" name=\"SURE\" checked value=\"0\">"
										+ rbNo
										+ "                </font></td>\n"
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
							+ "    </td>\n" + "  </tr>\n" + "</form>\n"
							+ htmlOutput.pageBottom());

					out.println(html.toString());
				} catch (Exception e) {
					out.println(htmlOutput.errorGeneral());
				}
			} else
				// if (Site.deleteReportPriv(session))
				out.println(htmlOutput.errorNoPrivilege());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}