//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;
import java.util.MissingResourceException;
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
 * Displays the first page the user see whens he logs into the sysetm.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 3044387414331083869L;

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
		HtmlOutput htmlOutput = new HtmlOutput("1", session);

		// Check user name, report, edit etc
		if (userName != null) { // please logon

			ResourceBundle rb = ResourceBundle.getBundle("resources.labels",
					Site.getLocale(session));

			String rbHomeTitle = rb.getString("HomeTitle");
			String rbHomeSubTitle = rb.getString("HomeSubTitle");
			String rbGenericOnlineReporting = rb
					.getString("GenericOnlineReporting");

			// Create HTML
			StringBuffer html = new StringBuffer(
					htmlOutput.pageTop()
							+ "  <tr><td>&nbsp;</td></tr>\n"
							+ "  <tr>\n"
							+ "    <td>\n"
							+ "<table width=\"750\" border=\"0\" cellspacing=\"2\" cellpadding=\"0\">\n"
							+ "  <tr align=\"center\"> \n"
							+ "    <td colspan=\"2\"> \n"
							+ "      <font face=\"Verdana, Arial\" size=\"5\" color=\"#00CC00\"><b><i>"
							+ rbHomeTitle
							+ ": "
							+ userName
							+ "</i></b></font>\n"
							+ "    </td>\n"
							+ "  </tr>\n"
							+ "  <tr> \n"
							+ "    <td colspan=\"2\">&nbsp;</td>\n"
							+ "  </tr>\n"
							+ "  <tr> \n"
							+ "    <td valign=\"top\"> \n"
							+ "      <p><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#000000\"><b><font color=\"#000099\" size=\"4\">&quot;"
							+ rbGenericOnlineReporting
							+ "&quot;</font></b></font></p>\n"
							+ "      <p><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#000000\">"
							+ rbHomeSubTitle + "</font></p>\n" + "      <ul>\n");

			try {
				int i = 1;
				while (true) {
					html.append("        <li><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#000000\">"
									+ rb.getString("HomePoint" + i++)
									+ "          </font>\n" + "        </li>\n");
				}
			} catch (MissingResourceException mreEx) {
			}

			html.append      ("      </ul>\n"
							+ "    </td>\n"
							+ "    <td valign=\"top\"><img src=\"/gor/images/home_image.gif\"></td>\n"
							+ "  </tr>\n" + "</table>\n" + "    </td>\n"
							+ "  </tr>\n" + htmlOutput.pageBottom());

			out.println(html.toString());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}