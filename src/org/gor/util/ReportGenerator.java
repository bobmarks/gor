//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gor.data.DatabaseConnectionFile;
import org.gor.data.Format;
import org.gor.data.Groups;
import org.gor.data.Labels;
import org.gor.data.ReportFile;

/**
 * This class is responsible for running the algorithm for generating the report
 * HTML.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportGenerator {

	private String report;

	private final int LINE_TOP = 6;
	private final int LINE_BOTTOM = 7;
	private final String SINGLE_PIXEL = "<img src=/gor/images/1p.gif width='1' height='1'>";

	private StringBuffer reportHtml;

	
	private ReportFile reportFile;
	private DatabaseConnectionFile dcf;
	private QueryGenerator queryGenerator;
	private Query query;
	private Groups groups;
	private Format format;

	/**
	 * Constructor takes the name of a report file.
	 * 
	 * @param report
	 *            Name of a report file (*.gor)
	 */
	public ReportGenerator(String report) {
		this.report = report;
	}

	/**
	 * Little method for creating the HTML which creates a line.
	 * 
	 * @param line
	 *            Position of line (i.e. 0=title, 1=subtitle etc)
	 * @param topOrBottom
	 *            Line can be at the top or at the bottom
	 * @param colSpan
	 *            Needed to know how many columns it has to span
	 * @return Returns the line as an HTML String
	 */
	private String addLine(int line, int topOrBottom, int colSpan) {
		int[] heights = { 1, 2, 4, 6, 10, 15 };
		int index = format.getLineIndex(line, topOrBottom);
		if (index > 0 && index < 7) {
			return ("<tr bgColor='" + format.getColourLine(line, topOrBottom)
					+ "' height='" + heights[index - 1] + "'>"
					+ "<td colspan='" + colSpan + "'>" + SINGLE_PIXEL + "</td></tr>\n");
		} else if (index > 7 && index < 12) {
			StringBuffer temp = new StringBuffer();
			for (int i = 7; i < index; i++)
				temp.append("<tr bgColor='"
						+ format.getColourLine(line, topOrBottom) + "'>"
						+ "<td colspan='" + colSpan + "'>&nbsp;</td></tr>");
			return temp.toString() + "\n";
		} else
			return "\n";
	}

	// Overloaded version used for single column lines.
	private String addLine(int line, int topOrBottom) {
		return addLine(line, topOrBottom, 1);
	}

	//==========================================================================
	// ==
	/**
	 * Main method which performs the ReportGenerator algorithm and returns the
	 * report as an HTML String.
	 * 
	 * @param localeNum
	 *            Number of locale to display report in
	 * @return Entire report as a String.
	 */
	public String getReportHtml(int localeNum) throws SQLException, Exception {
		
		// Declare report output String and connection to database
		reportHtml = new StringBuffer();
		reportFile = new ReportFile(report);
		dcf = new DatabaseConnectionFile(reportFile.getDatabase());
		queryGenerator = new QueryGenerator(report);
		query = reportFile.getQuery();
		groups = reportFile.getGroups();

		Connection conn = null;

		// First major step is to create and run SQL string.
		
		// retrieve SQL from QueryGenerator object
		String sqlString = queryGenerator.getSQL();

		// Create connection to database, create sql statement and data
		// resultset
		conn = dcf.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sqlString);

		// Retrieve label and format objects
		Labels labels = reportFile.getLabels();
		labels.setCurrentSetOfLabels(localeNum);
		format = reportFile.getFormat();

		// Start creating the SQL string.
		String tableWidth = format.getPageWidth();
		int curRow;

		// create table depending on orientation (portrait or landscape or 100%)
		reportHtml.append("<table width='" + tableWidth
				+ "' border='0' cellspacing='0' cellpadding='0'>\n");

		// ROW 0: Add Title row (line at top, detail and line and bottom).
		curRow = 0;
		reportHtml.append("  " + addLine(curRow, LINE_TOP) + "  <tr>\n"
				+ "    <td align='" + format.getAlign(curRow) + "' bgcolor='"
				+ format.getColourBackground(curRow) + "'>"
				+ format.getHtmlTextStart(curRow) + labels.getTitle()
				+ format.getHtmlTextEnd(curRow) + "</td>\n" + "  </tr>\n"
				+ "  " + addLine(curRow, LINE_BOTTOM));

		// ROW 1: Do the same for the sub title.
		curRow = 1;
		reportHtml.append("  " + addLine(curRow, LINE_TOP) + "  <tr>\n"
				+ "    <td align='" + format.getAlign(curRow) + "' bgcolor='"
				+ format.getColourBackground(curRow) + "'>"
				+ format.getHtmlTextStart(curRow) + labels.getSubTitle()
				+ format.getHtmlTextEnd(curRow) + "</td>\n" + "  </tr>\n"
				+ "  " + addLine(curRow, LINE_BOTTOM));

		// Loop through all the rows of the resultset
		int numOfGroups = groups.size();
		int numOfVisCols = query.getNumOfVisibile();
		int numOfDetailColumns = numOfVisCols - numOfGroups;

		// Declare arrays to hold group information
		String[] groupsCurText = new String[5];
		String[] groupsPrevText = new String[5];

		// Loop through all the rows of the resultset

		int rsRow = 0;
		int alternRow = 0;
		// declare two booleans.
		boolean drawDetailLabels = true;

		// loop through every row in the result set
		while (rs.next()) {
			// Start creating group text

			int startAtGroup = 5; // 5 = Detail
			if (rsRow != 0)
				drawDetailLabels = false;

			// Populate
			for (int g = 0; g < numOfGroups; g++)
				groupsCurText[g] = rs.getString(g + 1);

			// do check to see which group differs (so as to know where to
			// start)
			for (int g = 0; g < numOfGroups; g++) {
				if (!groupsCurText[g].equals(groupsPrevText[g])) {
					startAtGroup = g;
					drawDetailLabels = true;

					// If this isn't the first row then end the preview detail
					// section
					if (rsRow != 0)
						reportHtml.append("      </table>\n" + "    </td>\n"
								+ "  </tr>\n");
					break;
				}
			}

			// Loop through each of the groups
			for (int g = startAtGroup; g < numOfGroups; g++) {
				alternRow = 0; // reset alternate row counter

				// Find out current group row

				// Retrieve group label
				int alias = groups.getGroupAlias(g);
				int column = groups.getGroupColumn(g);
				String curGroupLabel = labels.getLabel(alias, column);

				int curGroupLayout = format.getGroupLayout(g);

				curRow = (g * 2) + 2;

				if (curGroupLayout == 0) {
					reportHtml
							.append("  " + addLine(curRow, LINE_TOP)
									+ "  <tr>\n" + "    <td align='"
									+ format.getAlign(curRow) + "' bgcolor='"
									+ format.getColourBackground(curRow) + "'>"
									+ format.getHtmlTextStart(curRow)
									+ curGroupLabel
									+ format.getHtmlTextEnd(curRow) + "</td>\n"
									+ "  </tr>\n" + "  "
									+ addLine(curRow, LINE_BOTTOM));
				}

				//
				curRow++;
				reportHtml.append("  " + addLine(curRow, LINE_TOP) + "  <tr>\n"
						+ "    <td align='" + format.getAlign(curRow)
						+ "' bgcolor='" + format.getColourBackground(curRow)
						+ "'>" + format.getHtmlTextStart(curRow));

				if (curGroupLayout == 1)
					reportHtml.append(curGroupLabel + ": ");

				reportHtml.append(groupsCurText[g]
						+ format.getHtmlTextEnd(curRow) + "</td>\n"
						+ "  </tr>\n" + "  " + addLine(curRow, LINE_BOTTOM));
			}

			// ROW 12: Detail - Labels (at top)
			// Only draw this if at first time in loop or if a group has been
			// drawn
			if (drawDetailLabels) {
				curRow = 12;

				reportHtml
						.append("  <tr>\n"
								+ "    <td>\n"
								+ "      <table border='0' cellspacing='0' cellpadding='0'>\n"
								+ "        "
								+ addLine(curRow, LINE_BOTTOM,
										(numOfDetailColumns * 2) - 1)
								+ "        <tr bgcolor='"
								+ format.getColourBackground(curRow) + "'>\n");
				int alias, column;

				for (int i = 0; i < numOfDetailColumns; i++) {
					alias = queryGenerator.getSortedQueryAlias(i + numOfGroups);
					column = queryGenerator.getSortedQueryColumn(i
							+ numOfGroups);

					reportHtml
							.append("          <td nowrap valign='top' align='"
									+ format.getAlign(curRow) + "'>"
									+ format.getHtmlTextStart(curRow)
									+ labels.getLabel(alias, column)
									+ format.getHtmlTextEnd(curRow) + "</td>\n");
					// create seperator column of 10 pixels.
					if (i != numOfDetailColumns - 1)
						reportHtml
								.append("          <td width='10'>&nbsp;</td>\n");
				}

				reportHtml.append("        </tr>\n"
						+ "        "
						+ addLine(curRow, LINE_BOTTOM,
								(numOfDetailColumns * 2) - 1));
			}

			// ROW 13: Detail - Data
			curRow = 13;

			boolean useAlternateLine = (format.getLineIndex(curRow, LINE_TOP) == 13);
			String bgColour = "";

			// Change background colour depen
			if (useAlternateLine && (alternRow % 2 == 1))
				bgColour = format.getColourLine(curRow, LINE_TOP);
			else
				bgColour = format.getColourBackground(curRow);

			reportHtml.append("        "
					+ addLine(curRow, LINE_TOP, (numOfDetailColumns * 2) - 1)
					+ "        <tr valign='top' bgcolor='" + bgColour + "'>\n");

			for (int i = numOfGroups; i < numOfVisCols; i++) {
				String data = rs.getString(i + 1);
				if (data == null)
					data = "";
				reportHtml.append("          <td align='"
						+ format.getAlign(curRow) + "'>"
						+ // result set bit will be formatted
						format.getHtmlTextStart(curRow) + data
						+ format.getHtmlTextEnd(curRow) + "</td>\n");
				if (i != numOfVisCols - 1)
					reportHtml.append("          <td width='10'>&nbsp;</td>\n");
			}
			reportHtml
					.append("        </tr>\n"
							+ "        "
							+ addLine(curRow, LINE_BOTTOM,
									(numOfDetailColumns * 2) - 1));

			// make prev values of groups equal to new values
			for (int g = 0; g < numOfGroups; g++)
				groupsPrevText[g] = groupsCurText[g];
			rsRow++;
			alternRow++;
		} // end of result set

		// finish off detail table
		reportHtml.append("      </table>\n" + "    </td>\n" + "  </tr>\n");

		// ROW 14: Create footer (bottom of report)
		curRow = 14;

		reportHtml.append("  " + addLine(curRow, LINE_TOP) + "  <tr>\n"
				+ "    <td align='" + format.getAlign(curRow) + "' bgcolor='"
				+ format.getColourBackground(curRow) + "'>"
				+ format.getHtmlTextStart(curRow) + labels.getFooter()
				+ format.getHtmlTextEnd(curRow) + "</td>\n" + "  </tr>\n"
				+ "  " + addLine(curRow, LINE_BOTTOM) + "</table>\n");

		// Close connection to database
		try { // try once
			conn.close();
		} catch (Exception e) { // and again
			try {
				conn.close();
			} catch (Exception x) {
			}
		}

		return (reportHtml.toString());
	}
}