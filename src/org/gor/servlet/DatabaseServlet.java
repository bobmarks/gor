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
 * Expands the database section of the menu
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class DatabaseServlet extends HttpServlet {

	private static final long serialVersionUID = -3861212254651015138L;

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
		HtmlOutput htmlOutput = new HtmlOutput("3", session);

		// check user has logged on (or session hasn't run out)
		if (userName != null) {
			// if user doesn't have permission to do any of the following then
			// don't allow him into this section at all
			boolean accessOK = (Site.newDatabasePriv(session)
					|| Site.openDatabaseConnPriv(session) || Site
					.deleteDatabaseConnPriv(session));

			if (accessOK) {
				try {
					// Display menu system - HtmlOutput.pageTop() and ...
					// ... quick help at bottom of page HtmlOutput.pageBottom()
					out.println(htmlOutput.pageTop() + htmlOutput.pageBottom());
				} catch (Exception e) {
					e.printStackTrace();
					out.println(htmlOutput.errorGeneral());
				}
			} else
				out.println(htmlOutput.errorNoPrivilege());
		} else
			out.println(htmlOutput.errorNoSession());
	}
}
