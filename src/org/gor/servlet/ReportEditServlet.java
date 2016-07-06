//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * Expands the Report - Edit menu of the system.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditServlet extends HttpServlet {

	private static final long serialVersionUID = 8187629558122676406L;

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
		HtmlOutput htmlOutput = new HtmlOutput("2", session);

		// Check user name, report, edit etc
		if (userName != null) { // please logon
			if (Site.editReportPriv(session)) {
				String report = (String) session.getAttribute("report");
				String edit = (String) session.getAttribute("report_edit");

				if (report != null) { // please open a report
					if (edit.equals("1")) { // sorry you cannot edit this report

						// allow users into edditing section
						htmlOutput.setMenu("2.4");

						out.println(htmlOutput.pageTop() +

						htmlOutput.pageBottom());
					} // if (edit.equals("1"))
					else
						out.println(htmlOutput.errorNoReportEditting());
				} // if (report != null)
				else
					out.println(htmlOutput.errorNoOpenReport());
			} // if (Site.editReportPriv(session))
			else
				out.println(htmlOutput.errorNoPrivilege());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}