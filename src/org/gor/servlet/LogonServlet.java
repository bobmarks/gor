//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.util.HtmlOutput;
import org.gor.util.SimpleENC;
import org.gor.util.Site;

/**
 * This is the first screen a user will see when they enter the system. It
 * requires them to enter a username and password which is stored in the
 * users.dat file.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class LogonServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2268655077018321615L;

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

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();

		// Set up session and connection
		HttpSession session = req.getSession(true);
		HtmlOutput htmlOutput = new HtmlOutput("0", session);

		try {
			// Create final HTML output.
			if (req.getParameter("USERNAME") == null) {
				// ResourceBundle stuff
				ResourceBundle rb = ResourceBundle.getBundle("resources.labels", Site.getLocale(session));

				String rbTitle = rb.getString("LogonTitle");
				String rbLogon = rb.getString("Logon");
				String rbUserName = rb.getString("UserName");
				String rbPassword = rb.getString("Password");
				String rbOK = rb.getString("OK");

				// Output logon HTML
				out.println      ("<html>\n" + "<head>\n" + "<title>"
								+ rbTitle
								+ "</title>\n"
								+ "</head>\n"
								+ "<body bgcolor=\"#FFFFFF\">\n"
								+ "<form method=\"post\" action=\"\">\n"
								+ "  <img src='/gor/images/title.gif' width='300' height='50'>\n"
								+ "  <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
								+ "    <tr><td>&nbsp;</td></tr>\n"
								+ "    <tr bgcolor=\"#000000\"> \n"
								+ "      <td>\n"
								+ "        <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"400\">\n"
								+ "          <tr bgcolor=\"#CCCCCC\"> \n"
								+ "            <td bgcolor=\"#000099\" align=\"center\" colspan=\"2\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><b>"
								+ rbLogon
								+ "</b></font></td>\n"
								+ "          </tr>\n"
								+ "          <tr bgcolor=\"#CCCCCC\"> \n"
								+ "            <td align=\"right\">\n"
								+ "              <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
								+ rbUserName
								+ "</b></font>\n"
								+ "            </td>\n"
								+ "            <td>\n"
								+ "              <input type=\"text\" name=\"USERNAME\" maxlength=\"20\" size=\"20\">\n"
								+ "            </td>\n"
								+ "          </tr>\n"
								+ "          <tr bgcolor=\"#CCCCCC\"> \n"
								+ "            <td align=\"right\">\n"
								+ "              <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
								+ rbPassword
								+ "</b></font>\n"
								+ "            </td>\n"
								+ "            <td>\n"
								+ "              <input type=\"password\" name=\"PASSWORD\" maxlength=\"20\" size=\"20\">\n"
								+ "            </td>\n"
								+ "          </tr>\n"
								+ "          <tr bgcolor=\"#CCCCCC\"> \n"
								+ "            <td align=\"middle\" colspan=\"2\">\n"
								+ "              <input type=\"submit\" name=\"Submit\" value=\""
								+ rbOK + "\">\n" + "            </td>\n"
								+ "          </tr>\n" + "        </table>\n"
								+ "      </td>\n" + "    </tr>\n"
								+ "  </table>\n" + "</form>\n" + "</body>\n"
								+ "</html>\n");
			}
			// process logon information
			else {
				String sUserName = req.getParameter("USERNAME").trim();
				String sPassword = req.getParameter("PASSWORD").trim();

				// loop through username file until a match is found
				boolean matchFound = false; // assume doesn't exist
				String priv = "0"; // 0 means no priviledges

				String file = Site.getSystemDir() + "users.dat";
				BufferedReader fileIn = new BufferedReader(new FileReader(file));

				String fileLine = "";

				while (fileIn.ready()) {
					fileLine = fileIn.readLine();
					int length = fileLine.length();
					String userNameAndPassword = fileLine.substring(0,
							length - 2);

					if (length > 6) {
						if (userNameAndPassword.equals(SimpleENC.normToEnc(sUserName) + SimpleENC.normToEnc(sPassword))) {
							matchFound = true;
							// priv is a 2 digit number from 00 to 63.
							try {
								priv = SimpleENC.encToNorm(fileLine).substring(
										length - 2, length);

								// make sure it is a number and in the right
								// range
								int intPriv = Integer.parseInt(priv);

								if (intPriv < 0 || intPriv > 63)
									matchFound = false;
							}
							// if not a valid number then possible a hacker
							catch (Exception e) {
								matchFound = false;
							}
						}
					}
				}

				fileIn.close();

				if (matchFound) {
					// put variables in the the session variable
					session.setAttribute("username", sUserName);
					// change these so country / language goes to
					// session
					session.setAttribute("interface", "0");
					session.setAttribute("language", "en");
					session.setAttribute("country", "GB");
					session.setAttribute("priv", priv);
					res.sendRedirect("/gor/Home");
				} else
					out.println(htmlOutput.errorPage("7"));
			}
		} catch (IOException ioe) {
			out.println(htmlOutput.errorIO());
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace(new PrintStream(out));
			out.println(htmlOutput.errorGeneral());
		}
	}
}
