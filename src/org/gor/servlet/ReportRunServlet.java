//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.data.LabelLocales;
import org.gor.data.Labels;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Query;
import org.gor.util.ReportGenerator;
import org.gor.util.Site;

/**
 * This servlet interface provides an interface for running a report.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportRunServlet extends HttpServlet {
		
	private static final long serialVersionUID = -631841261962454030L;

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
		HtmlOutput htmlOutput = new HtmlOutput("2.5", session);

		// Check user name, report, edit etc
		if (userName != null) { // please logon
			String report = (String) session.getAttribute("report");

			if (report != null) { // please open a report

				// ResourceBundle stuff
				ResourceBundle rb = ResourceBundle.getBundle("resources.labels", Site.getLocale(session));

				String rbFullScreen = rb.getString("FullScreen");
				String rbYes = rb.getString("Yes");
				String rbNo = rb.getString("No");
				String rbLanguage = rb.getString("Language");
				String rbRefresh = rb.getString("Refresh");

				// Create final HTML output.
				htmlOutput.setMenu("2.5");

				ReportFile reportFile = new ReportFile(report);

				// Check to see if a report can be run.
				// There must be a least 1 table and 1 visible column declared.
				TablesAndAlias tablesAliases = reportFile.getTablesAndAlias();
				Query query = reportFile.getQuery();
				Labels labels = reportFile.getLabels();

				boolean aliasesExist = (tablesAliases.size() > 0);
				boolean visibleColumnsExist = (query.getNumOfVisibile() > 0);
				boolean labelsExist = (labels.getNumOfLabelSets() > 0);

				boolean success = aliasesExist && visibleColumnsExist
						&& labelsExist;

				if (!success) {
					// create error message
					StringBuffer html = new StringBuffer(htmlOutput.errorTop());

					if (!aliasesExist)
						html.append("<li>" + htmlOutput.error("251")
								+ "</li><br>");
					if (!visibleColumnsExist)
						html.append("<li>" + htmlOutput.error("252")
								+ "</li><br>");
					if (!labelsExist)
						html.append("<li>" + htmlOutput.error("253")
								+ "</li><br>");

					html.append(htmlOutput.errorBottom());

					out.println(html.toString());
				} else {
					try {
						LabelLocales labelLocales = new LabelLocales();

						// declare default values for viewing report full screen
						// / langauge
						int fullScreen = 0; // no
						int labelLocale = labels.getDefaultLocale();

						if (req.getParameter("FULLSCREEN") != null) {
							fullScreen = Integer.parseInt(req
									.getParameter("FULLSCREEN"));
							labelLocale = Integer.parseInt(req
									.getParameter("LOCALE"));
						}

						// Create QueryGenerator (use ReportGenerator)
						ReportGenerator rg = new ReportGenerator(report);

						// Create final HTML output.
						StringBuffer html = new StringBuffer();

						if (fullScreen == 0) {
							html = new StringBuffer(
									htmlOutput.pageTop()
											+ "  <form method=\"post\" name=\"mainForm\" action=\"\">\n"
											+ "  <tr bgcolor=\"#CCCCCC\"> \n"
											+ "    <td> \n"
											+ "      <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
											+ "        <tr bgcolor=\"#CCCCCC\"> \n"
											+ "          <td><font face='Veranda, Arial' size='2' color='#000000'><b>"
											+ rbFullScreen
											+ ":</b> \n"
											+ "            <input type=\"radio\" name=\"FULLSCREEN\" value=\"1\"><img src=\"/gor/images/icon_printer.gif\" width=\"18\" height=\"16\">"
											+ rbYes
											+ "\n"
											+ "            <input type=\"radio\" name=\"FULLSCREEN\" value=\"0\" checked>"
											+ rbNo
											+ "\n"
											+ "          </td>\n"
											+ "          <td><font face='Veranda, Arial' size='2' color='#000000'><b>"
											+ rbLanguage
											+ ":</b> \n"
											+ "            <select name=\"LOCALE\">\n");

							for (int i = 0; i < labels.getNumOfLabelSets(); i++) {
								int num = labels.getLocaleUsed(i);
								String text = labelLocales.getName(num);
								if (labelLocale == num)
									html.append("              <option value='"
											+ num + "' selected>" + text
											+ "</option>\n");
								else
									html
											.append("              <option value='"
													+ num
													+ "'>"
													+ text
													+ "</option>\n");
							}

							html
									.append("            </select>\n"
											+ "            </font>\n"
											+ "          </td>\n"
											+ "          <td width=\"50\"> \n"
											+ "            <input type=\"submit\" name=\"Submit\" value=\""
											+ rbRefresh
											+ "\">\n"
											+ "          </td>\n"
											+ "        </tr>\n"
											+ "      </table>\n"
											+ "    </td>\n"
											+ "  </tr>\n"
											+ "  <tr bgcolor=\"#000000\"> \n"
											+ "    <td><img src=\"images/1p.gif\" width=\"1\" height=\"1\"></td>\n"
											+ "  </tr>\n"
											+ "  <tr><td>&nbsp;</td></tr>\n"
											+ "  <tr><td>"
											+ rg.getReportHtml(labelLocale)
											+ "</td></tr>\n" + "</form>\n"
											+ htmlOutput.pageBottom());
						} else
							// Full Screen
							html.append("<html>\n" + "<head>\n" + "<title>"
									+ report + "</title>\n" + "</head>\n"
									+ "<body bgColor='#ffffff'>\n"
									+ rg.getReportHtml(labelLocale)
									+ "</body>\n" + "</html>\n");

						out.println(html.toString());
					} catch (SQLException sqlEx) {
						out.println(htmlOutput.errorSQL());
					} catch (Exception generalEx) {
						out.println(htmlOutput.errorGeneral());
					}
				}
			} // if (report != null)
			else
				out.println(htmlOutput.errorNoOpenReport());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}