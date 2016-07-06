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
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * Provides a web interface for creating a database connection file. See also
 * the DatabaseConnectionFile class.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class DatabaseAddServlet extends HttpServlet {

	private static final long serialVersionUID = 6556857990474988698L;

	/**
	 * Do get method.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * Do post.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("3.1", session);

		// check user has logged on (or session hasn't run out)
		if (userName != null) {
			if (Site.newDatabasePriv(session)) {
				try {
					ResourceBundle rb = ResourceBundle.getBundle(
							"resources.labels", Site.getLocale(session));

					String rbAddDatabase = rb.getString("AddDatabase");
					String rbFileName = rb.getString("FileName");
					String rbDatabase = rb.getString("Database");
					String rbDriver = rb.getString("Driver");
					String rbConnectionURL = rb.getString("ConnectionURL");
					String rbUserName = rb.getString("UserName");
					String rbPassword = rb.getString("Password");
					String rbDatabaseConnectionWasSuccessful = rb
							.getString("DatabaseConnectionWasSuccessful");
					String rbTotalTablesInDatabase = rb
							.getString("TotalTablesInDatabase");
					String rbTotalColumnsInDatabase = rb
							.getString("TotalColumnsInDatabase");
					String rbOK = rb.getString("OK");
					String sNewFile = req.getParameter("FILENAME");

					if (sNewFile != null) {
						try {
							sNewFile = sNewFile.toLowerCase();
							String[] dbInfo = new String[5];

							boolean bURLPulldown = req.getParameter("URL_TYPE")
									.equals("pulldown");

							// populate array
							dbInfo[0] = req.getParameter("DATABASE");
							dbInfo[1] = req.getParameter("DRIVER");
							if (bURLPulldown)
								dbInfo[2] = req.getParameter("CONURL") + req.getParameter("CONNAME");
							else
								dbInfo[2] = req.getParameter("CONURLTEXT");							
							dbInfo[3] = req.getParameter("USERNAME");
							dbInfo[4] = req.getParameter("PASSWORD");

							// do validation (ensure they have valid values)
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

							boolean validDatabase = (!dbInfo[0].equals("-1"));
							boolean validDriver = (!dbInfo[1].equals("-1"));
							boolean validConURL;
							if (bURLPulldown)
								validConURL = (!dbInfo[2].equals("-1"));
							else
								validConURL = (!dbInfo[2].equals(""));

							// create overall sucess boolean (AND all validation
							// booleans)
							boolean success = (validFileName
									&& validFileNameChars && validDatabase
									&& validDriver && validConURL);

							// if not sucessful create an error output screen
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
								if (!validDriver)
									html.append("<li>"
											+ htmlOutput.error("314")
											+ "</li><br>");
								if (!validConURL)
									html.append("<li>"
											+ htmlOutput.error("315")
											+ "</li><br>");

								html.append(htmlOutput.errorBottom());

								// Output error message
								out.println(html.toString());
							}

							// Otherwise ... (no form validation errors have
							else {
								// Create instance of the DatabaseConnectionFile
								// class. It
								// contains methods for creating a new file.
								DatabaseConnectionFile dcf = new DatabaseConnectionFile();

								// try and create database connection file. If
								// true is returned
								// then the file has been sucessfully created.
								if (dcf.createFile(sNewFile + ".dbd", dbInfo)) {

									// if sucessful show confirmation HTML
									StringBuffer html = new StringBuffer(
											htmlOutput.pageTop()
													+ "  <form method=\"post\" action=\"\">\n"
													+ "  <tr><td>&nbsp;</td></tr>\n"
													+ "  <tr>\n"
													+ "    <td>\n"
													+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n"
													+ "        <tr bgcolor=\"#000000\"> \n"
													+ "          <td> \n"
													+ "            <table border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\" width=\"400\">\n"
													+ "              <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\">\n"
													+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
													+ rbAddDatabase
													+ "</b></font>\n"
													+ "                </td>\n"
													+ "              </tr>\n"
													+ "              <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                <td colspan=\"2\" height=\"23\">\n"
													+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
													+ rbDatabaseConnectionWasSuccessful
													+ "</b></font>\n"
													+ "                </td>\n"
													+ "              </tr>\n"
													+ "              <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                <td colspan=\"2\" align=\"left\"> \n"
													+ "                  <ul>\n"
													+ "                    <li><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
													+ rbTotalTablesInDatabase
													+ ": <b>"
													+ dcf.getNumOfTables()
													+ "</b></font></li>\n"
													+ "                    <li><font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\">"
													+ rbTotalColumnsInDatabase
													+ ": <b>"
													+ dcf.getNumOfColumns()
													+ "</b></font><br>\n"
													+ "                    </li>\n"
													+ "                  </ul>\n"
													+ "                </td>\n"
													+ "              </tr>\n"
													+ "              <tr bgcolor=\"#CCCCCC\"> \n"
													+ "                <td colspan=\"2\" align=\"middle\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\" color=\"#FFFFFF\"><b> \n"
													+ "                  <input type=\"submit\" name=\"Submit\" value=\""
													+ rbOK
													+ "\">\n"
													+ "                  </b></font> </td>\n"
													+ "              </tr>\n"
													+ "            </table>\n"
													+ "          </td>\n"
													+ "        </tr>\n"
													+ "      </table>\n"
													+ "    </td>\n"
													+ "  </tr>\n" + "</form>\n"
													+ htmlOutput.pageBottom());

									// output to scren
									out.println(html.toString());
								} // output error
								// "creating database connection file."
								else
									out.println(htmlOutput.errorPage("316"));
							}
						} catch (Exception ge) {
							out.println(htmlOutput.errorGeneral());
						}
					} // if (sNewFile != null)

					// if (sNewFile == null) then Servlet loaded for the first
					// time ...
					else {
						// ... so create input form
						out.println(htmlOutput.pageTop()
										+ "  <tr><td>&nbsp;</td></tr>\n"
										+ "  <tr>\n"
										+ "    <td>\n"
										+ "      <form method=\"post\" action=\"\">\n"
										+ "      <table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100\">\n"
										+ "        <tr bgcolor=\"#000000\"> \n"
										+ "          <td> \n"
										+ "            <table width=\"700\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" bgcolor=\"#000099\" align=\"center\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#FFFFFF\"><b>"
										+ rbAddDatabase
										+ "</b></font></td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td width=\"200\" align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>*"
										+ rbFileName
										+ "</b></font></td>\n"
										+ "                <td bgcolor=\"#CCCCCC\"> \n"
										+ "                  <input type=\"text\" name=\"FILENAME\">\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td width=\"200\" align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>*"
										+ rbDatabase
										+ "</b></font></td>\n"
										+ "                <td> \n"
										+ "                  <select name=\"DATABASE\">\n"
										+ "                    <option value=\"-1\" selected>-</option>\n"
										+ "                    <option value=\"Access\">Access</option>\n"
										+ "                    <option value=\"HSQLDB\">HSQLDB</option>\n"
										+ "                    <option value=\"Oracle\">Oracle</option>\n"
										+ "                    <option value=\"MySQL\">MySQL</option>\n"
										+ "                    <option value=\"MySQL\">Other</option>\n"
										+ "                  </select>\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td width=\"200\" align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>*"
										+ rbDriver
										+ "</b></font>\n"
										+ "                </td>\n"
										+ "                <td> \n"
										+ "                  <select name=\"DRIVER\">\n"
										+ "                    <option value=\"-1\" selected>-</option>\n"
										+ "                    <option value=\"org.hsqldb.jdbcDriver\">HSQLDB (org.hsqldb.jdbcDriver)</option>\n"
										+ "                    <option value=\"sun.jdbc.odbc.JdbcOdbcDriver\">JDBC-ODBC (sun.jdbc.odbc.JdbcOdbcDriver)</option>\n"
										+ "                    <option value=\"twz1.jdbc.mysql.jdbcMysqlDriver\">MySQL (twz1.jdbc.mysql.jdbcMysqlDriver)</option>\n"
										+ "                    <option value=\"com.mysql.jdbc.Driver\">MySQL (com.mysql.jdbc.Driver)</option>\n"
										+ "                    <option value=\"oracle.jdbc.driver.OracleDriver\">Oracle (oracle.jdbc.driver.OracleDriver)</option>\n"
										+ "                  </select>\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "             <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>*"
										+ rbConnectionURL
										+ "</b></font>\n"
										+ "                </td>\n"
										+ "                <td> \n"
										+ "                  <div align=\"left\"> \n"
										+ "                    <input type=\"radio\" name=\"URL_TYPE\" value=\"pulldown\" checked>\n"
										+ "                    <select name=\"CONURL\">\n"
										+ "                      <option value=\"-1\" selected>-</option>\n"
										+ "                      <option value=\"jdbc:odbc:\">jdbc:odbc:</option>\n"
										+ "                      <option value=\"jdbc:oracle:\">jdbc:oracle:</option>\n"
										+ "                      <option value=\"jdbc:z1MySQL:\">jdbc:z1MySQL:</option>\n"
										+ "                    </select> <input type=\"text\" name=\"CONNAME\"size=\"15\">\n"
										+ "                  </div>\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td align=\"right\" valign=\"top\">&nbsp;</td>\n"
										+ "                <td> \n"
										+ "                  <input type=\"radio\" name=\"URL_TYPE\" value=\"textbox\">\n"
										+ "                  <input type=\"text\" name=\"CONURLTEXT\" size=\"50\" maxlength=\"100\">\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td width=\"200\" align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbUserName
										+ "</b></font>\n"
										+ "                </td>\n"
										+ "                <td bgcolor=\"#CCCCCC\">\n"
										+ "                  <input type=\"text\" name=\"USERNAME\">\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td width=\"200\" align=\"right\">\n"
										+ "                  <font size=\"2\" face=\"Verdana, Arial\" color=\"#000000\"><b>"
										+ rbPassword
										+ "</b></font>\n"
										+ "                </td>\n"
										+ "                <td bgcolor=\"#CCCCCC\">\n"
										+ "                  <input type=\"text\" name=\"PASSWORD\">\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "              <tr bgcolor=\"#CCCCCC\"> \n"
										+ "                <td colspan=\"2\" align=\"middle\">\n"
										+ "                  <input type=\"submit\" name=\"Submit\" value=\""
										+ rbOK + "\">\n"
										+ "                </td>\n"
										+ "              </tr>\n"
										+ "            </table>\n"
										+ "          </td>\n"
										+ "        </tr>\n"
										+ "      </table>\n" + "    </td>\n"
										+ "  </tr>\n" + "</form>\n"
										+ htmlOutput.pageBottom());
					}
				} catch (Exception e) {
					e.printStackTrace();
					out.println(htmlOutput.errorGeneral());
				}
			} // if (Site.editReportPriv(session))
			else
				out.println(htmlOutput.errorNoPrivilege());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}
