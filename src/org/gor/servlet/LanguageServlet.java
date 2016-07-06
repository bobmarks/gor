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

import org.gor.data.InterfaceLabels;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This servlet allows a user to choose the specified langauge they would like
 * to view the system in.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class LanguageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7878084317369014353L;

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
		HtmlOutput htmlOutput = new HtmlOutput("4", session);

		// check user has logged on (or session hasn't run out)
		if (userName != null) {
			try {
				// ResourceBundle stuff
				ResourceBundle rb = ResourceBundle.getBundle("resources.labels",
						Site.getLocale(session));

				String rbLanguageSelector = rb.getString("LanguageSelector");
				String rbOK = rb.getString("OK");

				//==============================================================
				// ========
				// Create final HTML output.
				//==============================================================
				// ========

				InterfaceLabels iLabels = new InterfaceLabels();
				int numOfLocales = iLabels.getNumOfInterfaceLocales();

				String sLocale = req.getParameter("LOCALE");
				int intLocale;

				if (sLocale != null) {
					intLocale = Integer.parseInt(sLocale);

					session.setAttribute("interface", sLocale);
					session.setAttribute("language", iLabels.getLanguage(intLocale));
					session.setAttribute("country", iLabels.getCountry(intLocale));

					// refresh htmloutput, resource bundles and strings
					htmlOutput.setLocale(Site.getLocale(session));
					rb = ResourceBundle.getBundle("resources.labels", Site
							.getLocale(session));
					rbLanguageSelector = rb.getString("LanguageSelector");
					rbOK = rb.getString("OK");
				} else {
					sLocale = (String) session.getAttribute("interface");
					intLocale = Integer.parseInt(sLocale);
				}

				StringBuffer html = new StringBuffer(
						htmlOutput.pageTop()
								+ "  <form method='post' action=''>\n"
								+ "  <tr><td>&nbsp;</td></tr>\n"
								+ "  <tr>\n"
								+ "    <td>\n"
								+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#000000\">\n"
								+ "        <tr>\n"
								+ "          <td>\n"
								+ "            <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
								+ "              <tr bgcolor=\"#000099\" align=\"center\">\n"
								+ "                <td colspan=\""
								+ numOfLocales
								+ "\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>"
								+ rbLanguageSelector
								+ " </b></font>\n"
								+ "                </td>\n"
								+ "              </tr>\n"
								+ "              <tr width='70' bgcolor=\"#CCCCCC\" align=\"center\">\n");

				// Display all locales used already
				for (int i = 0; i < numOfLocales; i++) {
					if (i == intLocale)
						html.append("                <td><input type=\"radio\" name=\"LOCALE\" checked value='"
							  + i + "'></td>\n");
					else
						html.append("                <td><input type=\"radio\" name=\"LOCALE\" value='"
										+ i + "'></td>\n");
				}

				html.append("              </tr>\n"
								+ "              <tr bgcolor=\"#CCCCCC\" align=\"center\" valign=\"top\">\n");

				// Show all the available locales.
				for (int i = 0; i < numOfLocales; i++) {
					html
							.append("                <td width='70'  bgcolor=\"#CCCCCC\">\n"
									+ "                  <font face=\"Verdana, Arial\" size=\"2\" color=\"#000000\">\n"
									+ "                  <img src=\"/gor/images/"
									+ iLabels.getImage(i)
									+ "\" width=\"32\" height=\"22\"><br>\n");

					if (i == intLocale)
						html.append("<b>" + iLabels.getText(i) + "</b>");
					else
						html.append(iLabels.getText(i));

					html.append("</font>\n" + "                </td>\n");
				}

				html.append      ("              </tr>\n"
								+ "              <tr bgcolor=\"#CCCCCC\" align=\"center\">\n"
								+ "                <td colspan='"
								+ numOfLocales
								+ "'>\n"
								+ "                  <input type=\"submit\" name=\"Submit\" value=\""
								+ rbOK + "\">\n" + "                </td>\n"
								+ "              </tr>\n"
								+ "            </table>\n"
								+ "          </td>\n" + "        </tr>\n"
								+ "      </table>\n" + "    </td>\n"
								+ "  </tr>\n" + "</form>\n"
								+ htmlOutput.pageBottom());

				out.println(html.toString());
			} catch (Exception e) {
				e.printStackTrace();
				out.println(htmlOutput.errorGeneral());
			}
		} else
			out.println(htmlOutput.errorNoSession());
	}
}
