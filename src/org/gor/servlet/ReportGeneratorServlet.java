//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import org.gor.util.ReportGenerator;

import java.io.*;

/**
 * This class can run a report without using the Generic Online Reporting
 * interface.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportGeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = -5636149111397002731L;

	/**
	 * Do get.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Set up session and connection
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();

		String report = req.getParameter("report");
		int labelNum = Integer.parseInt(req.getParameter("label"));

		try {
			ReportGenerator rg = new ReportGenerator(report);

			out.println("<html>\n"
					+ "<head><title>Simple report</title></head>\n"
					+ "<body>\n" + "<tr><td width='635'>\n"
					+ rg.getReportHtml(labelNum) + "</tr></td>\n"
					+ "</table>\n" + "</body>\n" + "</html>\n");
		} catch (Exception e) {
			out.println("There has been an error running this report");
		}
	}
}