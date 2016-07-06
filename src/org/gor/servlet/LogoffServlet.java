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

import org.gor.data.ReportFile;
import org.gor.util.HtmlOutput;

/**
 * This servlet logs a user off the system and tidys up session variables. If a
 * user has been editting a report file then this is reset too.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class LogoffServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1848457949017900254L;

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

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("5", session);

		try {
			// if user was working on a report then unlock that report
			// so other uses can edit it if they want
			String reportFileName = (String) session.getAttribute("report");
			String reportEdit = (String) session.getAttribute("report_edit");
			if (reportEdit == null)
				reportEdit = "0";

			if (reportFileName != null && reportEdit.equals("1")) {
				ReportFile prevReport = new ReportFile(reportFileName);
				prevReport.editUnLock();
			}

			// Reset ALL session variables
			session.removeAttribute("username");
			session.removeAttribute("language");
			session.removeAttribute("country");
			session.removeAttribute("priv");
			session.removeAttribute("interface");
			session.removeAttribute("report");
			session.removeAttribute("report_edit");

			res.sendRedirect("/gor/Logon");
		} catch (Exception e) {
			out.println(htmlOutput.errorGeneral());
		}
	}
}