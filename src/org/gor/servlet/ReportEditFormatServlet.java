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
import org.gor.data.Format;
import org.gor.data.Groups;
import org.gor.data.ReportFile;
import org.gor.data.TablesAndAlias;
import org.gor.util.HtmlOutput;
import org.gor.util.Site;

/**
 * This class provides an interface to format a report. The bulk of this class
 * is HTML code. The majority of the functionality of this class is provided
 * using JavaScript. It only passes two variables back to servlet one is all the
 * format and group information as a comma delimited String. The other is the
 * page orientation of the page.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class ReportEditFormatServlet extends HttpServlet {

	private static final long serialVersionUID = 74312969608356467L;

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
		
		// Create session
		HttpSession session = req.getSession(true);
		String userName = (String) session.getAttribute("username");

		// Set the content type for the HTTP response
		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		HtmlOutput htmlOutput = new HtmlOutput("2.4.7", session);

		//======================================================================
		// ====
		// Check user name, report, edit etc
		//======================================================================
		// ====

		// check user has logged on (or session hasn't run out)
		if (userName != null) { // please logon
			// retrieve variables from session
			String report = (String) session.getAttribute("report");
			String edit = (String) session.getAttribute("report_edit");

			if (report != null) { // please open a report
				if (edit.equals("1")) { // sorry you cannot edit this report
					try {
						// ResourceBundle stuff
						ResourceBundle rb = ResourceBundle.getBundle(
								"resources.labels", Site.getLocale(session));

						String rbFont = rb.getString("Font");
						String rbBold = rb.getString("Bold");
						String rbItalics = rb.getString("Italics");
						String rbUnderline = rb.getString("Underline");
						String rbLines = rb.getString("Lines");
						String rbTop = rb.getString("Top");
						String rbBottom = rb.getString("Bottom");
						String rbText = rb.getString("Text");
						String rbBackground = rb.getString("Background");
						String rbLineTop = rb.getString("LineTop");
						String rbLineBottom = rb.getString("LineBottom");
						String rbFormatClickOnALineToFormatIt = rb
								.getString("FormatClickOnALineToFormatIt");
						String rbExampleTitle = rb.getString("ExampleTitle");
						String rbExampleSubTitle = rb
								.getString("ExampleSubTitle");
						String rbExampleFooter = rb.getString("ExampleFooter");
						String rbGroup = rb.getString("Group");
						String rbLabel = rb.getString("Label");
						String rbData = rb.getString("Data");
						String rbDetail = rb.getString("Detail");
						String rbPage = rb.getString("Page");
						String rbProtrait = rb.getString("Protrait");
						String rbLandscape = rb.getString("Landscape");
						String rbAlternateLines = rb
								.getString("AlternateLines");
						String rbOneHundredPercent = rb
								.getString("OneHundredPercent");
						String rbTemplates = rb.getString("Templates");
						String rbfont0 = rb.getString("font0");
						String rbfont1 = rb.getString("font1");
						String rbfont2 = rb.getString("font2");
						String rbfont3 = rb.getString("font3");
						String rbfont4 = rb.getString("font4");
						String rbfont5 = rb.getString("font5");
						String rbtemplate0 = rb.getString("template0");
						String rbtemplate1 = rb.getString("template1");
						String rbtemplate2 = rb.getString("template2");
						String rbtemplate3 = rb.getString("template3");
						String rbtemplate4 = rb.getString("template4");
						String rbtemplate5 = rb.getString("template5");
						String rbtemplate6 = rb.getString("template6");
						String rbOK = rb.getString("OK");
						String rbReset = rb.getString("Reset");

						// Check if servlet is being run for first time.
						if (req.getParameter("FONT") != null) {
							// if it is, then retrieve line/group data and page
							// orientation
							String newFormatData = req
									.getParameter("ARRAY_DATA");
							String newPageOrientation = req
									.getParameter("PAGE");

							// create new format object from data
							Format newFormat = new Format();
							newFormat.setAll(newFormatData, newPageOrientation);
							// and update the report file with new data
							ReportFile.update(report, newFormat);
						}

						// display main page

						ReportFile reportFile = new ReportFile(report);
						DatabaseConnectionFile dcf = new DatabaseConnectionFile(
								reportFile.getDatabase());

						TablesAndAlias tablesAliases = reportFile
								.getTablesAndAlias();
						Groups groups = reportFile.getGroups();
						Format format = reportFile.getFormat();

						StringBuffer formatLinesJavaScript = new StringBuffer();
						StringBuffer formatGroupsJavaScript = new StringBuffer();

						for (int i = 0; i < 15; i++) {
							formatLinesJavaScript.append("  [");
							for (int j = 0; j < 12; j++) {
								formatLinesJavaScript.append(format.getItem(i,
										j));
								if (j != 11)
									formatLinesJavaScript.append(",");
							}
							formatLinesJavaScript.append("]");
							if (i != 15)
								formatLinesJavaScript.append(",");

							formatLinesJavaScript.append("\n");
						}

						for (int g = 0; g < 5; g++) {
							formatGroupsJavaScript.append(format
									.getGroupLayout(g));
							if (g != 4)
								formatGroupsJavaScript.append(",");
						}

						StringBuffer html = new StringBuffer(
								htmlOutput.pageTop()
										+ "  <script language=\"JavaScript\">\n"
										+ "// declare 2 dimensional array to hold information about each \n"
										+ "// line in report format\n"
										+ "// HORIZONTAL =\n"
										+ "// (0=font, 1=font_size, 2=bold, 3=italics, 4=underine,\n"
										+ "// 5=align, 6=t.line thickness, 7=b.line thickness,\n"
										+ "// 8=text_colour, 9=background_colour, \n"
										+ "// 10=t.line colour, 11=b.line colour)\n"
										+ "// VERTICAL = each row in report\n"
										+ "lines_info = [\n"
										+ formatLinesJavaScript.toString()
										+ "];\n"
										+ "groups = ["
										+ formatGroupsJavaScript.toString()
										+ "];\n"
										+ "\n"
										+ "template_0 = [\n"
										+ formatLinesJavaScript.toString()
										+ "];\n"
										+ "template_groups_0 = ["
										+ formatGroupsJavaScript.toString()
										+ "];\n"
										+ "\n"
										+ "// !!! The following should be in a file somewhere. \n"
										+ "// read in using javascript include\n"
										+ "// plain black and white veranda, very little bold, lines etc.\n"
										+ "template_1 = [\n"
										+ "  [5,5,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,0,0,0,0,0,8,1,0,0,0],\n"
										+ "  [5,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,2,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,2,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,2,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,2,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,2,1,0,0,1],\n"
										+ "  [5,2,1,0,0,0,0,1,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,0,1,1,0,0,1],\n"
										+ "  [5,2,0,0,0,0,8,0,1,0,0,0]\n"
										+ "];\n"
										+ "template_groups_1 = [1,0,0,0,0];\n"
										+ "\n"
										+ "// Veranda - causal \n"
										+ "template_2 = [\n"
										+ "  [5,6,1,0,0,0,2,2,11,12,1,1],\n"
										+ "  [5,2,1,0,0,0,0,8,3,0,13,0],\n"
										+ "  [5,2,0,0,0,0,0,0,8,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,0,8,0,1,1],\n"
										+ "  [5,2,0,0,0,0,0,0,8,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,0,8,0,1,1],\n"
										+ "  [5,2,0,0,0,0,0,0,8,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,0,8,0,1,1],\n"
										+ "  [5,2,0,0,0,0,0,0,8,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,0,8,0,1,1],\n"
										+ "  [5,2,0,0,0,0,0,0,8,0,0,0],\n"
										+ "  [5,2,1,0,0,0,0,0,8,0,1,1],\n"
										+ "  [5,2,1,0,0,0,2,2,8,9,1,1],\n"
										+ "  [5,2,0,0,0,0,0,1,3,0,4,3],\n"
										+ "  [5,2,0,0,0,0,8,1,1,0,0,8]\n"
										+ "];\n"
										+ "template_groups_2 = [1,0,0,0,0];\n"
										+ "\n"
										+ "// Times new roman - Forest greens\n"
										+ "template_3 = [\n"
										+ "  [2,7,0,1,0,2,4,4,7,15,7,7],\n"
										+ "  [2,3,0,1,0,2,0,8,1,0,0,0],\n"
										+ "  [2,2,0,0,0,0,0,0,11,0,0,0],\n"
										+ "  [2,3,1,1,0,2,8,8,1,0,0,0],\n"
										+ "  [2,2,0,0,0,0,0,0,11,0,0,0],\n"
										+ "  [2,3,1,1,0,2,8,8,10,0,0,0],\n"
										+ "  [2,2,0,0,0,0,0,0,11,0,0,0],\n"
										+ "  [2,3,1,1,0,2,8,8,10,0,0,0],\n"
										+ "  [2,2,0,0,0,0,0,0,11,0,0,0],\n"
										+ "  [2,3,1,1,0,2,8,8,10,0,0,0],\n"
										+ "  [2,2,0,0,0,0,0,0,11,0,0,0],\n"
										+ "  [2,3,1,1,0,2,8,8,10,0,0,0],\n"
										+ "  [2,2,1,0,0,0,1,1,1,15,7,7],\n"
										+ "  [2,2,0,0,0,0,0,1,1,0,0,4],\n"
										+ "  [2,2,0,1,0,1,9,1,1,0,0,7]\n"
										+ "];\n"
										+ "template_groups_3 = [1,0,0,0,0];\n"
										+ "\n"
										+ "// Times - coperates navy\n"
										+ "template_4 = [\n"
										+ "  [2,6,0,1,0,1,3,3,0,8,4,4],\n"
										+ "  [2,2,0,1,1,1,8,9,1,0,0,0],\n"
										+ "  [2,4,1,1,0,2,0,3,1,0,0,8],\n"
										+ "  [2,3,0,1,0,2,0,0,1,0,0,1],\n"
										+ "  [2,4,1,1,0,2,0,3,1,0,0,8],\n"
										+ "  [2,3,0,1,0,2,0,0,1,0,0,1],\n"
										+ "  [2,4,1,1,0,2,0,3,1,0,0,8],\n"
										+ "  [2,3,0,1,0,2,0,0,1,0,0,1],\n"
										+ "  [2,4,1,1,0,2,0,3,1,0,0,8],\n"
										+ "  [2,3,0,1,0,2,0,0,1,0,0,1],\n"
										+ "  [2,4,1,1,0,2,0,3,1,0,0,8],\n"
										+ "  [2,3,0,1,0,2,0,0,1,0,0,1],\n"
										+ "  [2,2,1,0,0,0,1,1,1,4,1,1],\n"
										+ "  [2,2,0,0,0,0,0,1,1,0,0,4],\n"
										+ "  [2,2,0,1,0,1,8,1,1,0,0,4]\n"
										+ "];\n"
										+ "template_groups_4 = [0,0,0,0,0];\n"
										+ "\n"
										+ "// Courier - Greys\n"
										+ "template_5 = [\n"
										+ "  [3,7,1,0,0,1,1,1,1,4,1,1],\n"
										+ "  [3,2,0,0,0,1,0,8,1,0,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [3,4,1,0,0,0,0,0,1,0,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,1,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,0,0,0,0,0,0,0,8,0,0],\n"
										+ "  [3,2,1,0,0,0,0,0,0,1,0,0],\n"
										+ "  [3,2,0,0,0,0,0,1,1,0,0,4],\n"
										+ "  [3,2,0,0,0,0,8,0,1,0,0,0]\n"
										+ "];\n"
										+ "template_groups_5 = [1,0,0,0,0];\n"
										+ "\n"
										+ "// Swinging 60s baby, yeah!\n"
										+ "template_6 = [\n"
										+ "  [1,7,1,1,1,1,5,5,7,12,10,10],\n"
										+ "  [1,2,1,0,0,1,0,8,11,15,0,15],\n"
										+ "  [1,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [1,4,1,0,0,0,0,0,5,15,0,0],\n"
										+ "  [1,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [1,4,1,0,0,0,0,0,5,15,0,0],\n"
										+ "  [1,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [1,4,1,0,0,0,0,0,5,15,0,0],\n"
										+ "  [1,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [1,4,1,0,0,0,0,0,5,15,0,0],\n"
										+ "  [1,2,0,0,0,0,0,0,1,0,0,0],\n"
										+ "  [1,4,1,0,0,0,0,0,5,15,0,0],\n"
										+ "  [1,2,1,0,0,0,2,2,8,9,1,1],\n"
										+ "  [1,2,1,0,0,0,0,1,6,13,0,1],\n"
										+ "  [1,2,1,1,1,0,8,5,7,12,12,10]\n"
										+ "];\n"
										+ "template_groups_6 = [1,0,0,0,0];\n"
										+ "\n"
										+ "function showJS() {\n"
										+ "  temp = \"template_ = [\\n\";\n"
										+ "  for (i = 0; i < 15; i++) {\n"
										+ "    temp += \"  [\";\n"
										+ "    for (j = 0; j < 12; j++) {\n"
										+ "      temp += lines_info[i][j];\n"
										+ "      if (j != 11)\n"
										+ "        temp += \",\";\n"
										+ "    }\n"
										+ "    temp += \"]\";\n"
										+ "    if (i != 14)\n"
										+ "      temp += \",\";\n"
										+ "    temp += \"\\n\";\n"
										+ "  }\n"
										+ "  temp += \"];\\ntemplate_groups_ = [\";\n"
										+ "  for (g = 0; g < 5; g++) {\n"
										+ "    temp += groups[g];\n"
										+ "    if (g != 4) temp+=\",\";\n"
										+ "  }\n"
										+ "  temp += \"];\"\n"
										+ "  document.mainForm.JSTEXT.value = temp;\n"
										+ "}\n"
										+ "\n"
										+ "function resetFromTemplate () {\n"
										+ "  index = document.mainForm.TEMPLATES.selectedIndex;\n"
										+ "  temp_lines = eval(\"template_\" + index);\n"
										+ "  temp_groups = eval(\"template_groups_\" + index);\n"
										+ "  for (i = 0; i < 15; i++)            // go through each line\n"
										+ "    for (j = 0; j < 12; j++)         // go through each element\n"
										+ "      lines_info[i][j] = temp_lines[i][j];\n"
										+ "  for (g = 0; g < 5; g++)\n"
										+ "    groups[g] = temp_groups[g];\n"
										+ "  focus = -1;\n"
										+ "  updateHiddenField();\n"
										+ "  refreshGroups();\n"
										+ "}\n"
										+ "\n"
										+ "// variable to hold which element is in focus\n"
										+ "// -1 = nothing\n"
										+ "// 0, 1, .., x = page elements\n"
										+ "// and variable to hold which colour element is in focus.\n"
										+ "\n"
										+ "var focus=-1;   // default focus (not focused on anything\n"
										+ "var numOfGroups = "
										+ groups.size()
										+ ";\n"
										+ "var colourFocus=0;    // focus of which colour is selected.\n"
										+ "\n"
										+ "// This is the little function for updating the right \n"
										+ "// selection for each colour\n"
										+ "function showColour() {\n"
										+ "  var index = lines_info[document.mainForm.COLOURS.selectedIndex];\n"
										+ "  document.mainForm.COLOUR[index].checked = true;\n"
										+ "}\n"
										+ "\n"
										+ "// If a textbox was clicked then you must update the font settings etc.\n"
										+ "function lineFocus(line) {\n"
										+ "  focus = line;       // declare which line is in focus.\n"
										+ "\n"
										+ "  document.mainForm.FONT.selectedIndex = lines_info[focus][0];        // font\n"
										+ "  document.mainForm.FONT_SIZE.selectedIndex = lines_info[focus][1];   // font size\n"
										+ "  if (lines_info[focus][2]== 1)                                       // bold\n"
										+ "    document.mainForm.BOLD.checked = true;\n"
										+ "  else\n"
										+ "  document.mainForm.BOLD.checked = false;\n"
										+ "  if (lines_info[focus][3]== 1)                                       // italics\n"
										+ "    document.mainForm.ITALICS.checked = true;\n"
										+ "  else\n"
										+ "  document.mainForm.ITALICS.checked = false;\n"
										+ "  if (lines_info[focus][4]== 1)                                       // underline\n"
										+ "    document.mainForm.UNDERLINE.checked = true;\n"
										+ "  else\n"
										+ "  document.mainForm.UNDERLINE.checked = false;\n"
										+ "  document.mainForm.ALIGN[lines_info[focus][5]].checked = true;       // align\n"
										+ "  document.mainForm.LINE_TOP.selectedIndex = lines_info[focus][6];    // lines top and ...\n"
										+ "  document.mainForm.LINE_BOTTOM.selectedIndex = lines_info[focus][7]; // ... bottom\n"
										+ "  refreshColours();\n"
										+ "}\n"
										+ "\n"
										+ "// methods to respond to setting a font, font size, bold, align, italics,\n"
										+ "// alignment, lines and various colours\n"
										+ "function setFont() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    lines_info[focus][0] = document.mainForm.FONT.selectedIndex;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setFontSize() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    lines_info[focus][1] = document.mainForm.FONT_SIZE.selectedIndex;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setBold() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    if (document.mainForm.BOLD.checked == true)\n"
										+ "      lines_info[focus][2] = 1;\n"
										+ "    else\n"
										+ "      lines_info[focus][2] = 0;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setItalics() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    if (document.mainForm.ITALICS.checked == true)\n"
										+ "      lines_info[focus][3] = 1;\n"
										+ "    else\n"
										+ "      lines_info[focus][3] = 0;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setUnderLine() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    if (document.mainForm.UNDERLINE.checked == true)\n"
										+ "      lines_info[focus][4] = 1;\n"
										+ "    else\n"
										+ "      lines_info[focus][4] = 0;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setAlign(align) {\n"
										+ "  if (focus >= 0) {\n"
										+ "    lines_info[focus][5] = align;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setColourChooser() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    colourFocus=document.mainForm.CURCOLOUR.selectedIndex;\n"
										+ "    refreshColours();\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setLineTop()\n"
										+ "{\n"
										+ "  if (focus >= 0) {\n"
										+ "    lines_info[focus][6] = document.mainForm.LINE_TOP.selectedIndex;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setLineBottom()\n"
										+ "{\n"
										+ "  if (focus >= 0) {\n"
										+ "    lines_info[focus][7] = document.mainForm.LINE_BOTTOM.selectedIndex;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "function setColour(colour) {\n"
										+ "  if (focus >= 0) {       //  CURCOLOUR\n"
										+ "    lines_info[focus][8 + colourFocus] = colour;\n"
										+ "    updateHiddenField();\n"
										+ "  }\n"
										+ "}\n"
										+ "\n"
										+ "// updates the corect column for colour\n"
										+ "function updateColourChooser(item) {\n"
										+ "  if (focus >= 0) {\n"
										+ "    colourFocus=item;\n"
										+ "    document.mainForm.CURCOLOUR[item].selected = true;\n"
										+ "    refreshColours();\n"
										+ "  }\n"
										+ "}\n"
										+ "// refreshes colours\n"
										+ "function refreshColours() {\n"
										+ "  if (focus >= 0) {\n"
										+ "    document.mainForm.COLOUR[lines_info[focus][8 + colourFocus]].checked = true;      // colours\n"
										+ "  }\n"
										+ "}\n"
										+ "// update group.\n"
										+ "function setGroup (index, value){\n"
										+ "  groups[index] = value;\n"
										+ "  updateHiddenField();\n"
										+ "}\n"
										+ "\n"
										+ "// refreshs groups depending on array values.\n"
										+ "function refreshGroups (){\n"
										+ "  for (i = 0; i < numOfGroups; i++)   // show each group\n"
										+ "    eval(\"document.mainForm.GROUP_\" + i + \"[groups[\" + i + \"]].checked = true\");\n"
										+ "}\n"
										+ "\n"
										+ "// It updates the hidden field so the server can read all the\n"
										+ "// array data for each line (fonts, size, etc )\n"
										+ "function updateHiddenField() {\n"
										+ "  showPreview();      // display update to screen\n"
										+ "  var temp = \"\";\n"
										+ "  for (i = 0; i < 15; i++) {           // go through each line\n"
										+ "    for (j = 0; j < 12; j++)         // go through each element\n"
										+ "      temp += lines_info[i][j] + \", \";\n"
										+ "  }\n"
										+ "  for (g = 0; g < 5; g++)\n"
										+ "    temp += groups [g] + \",\";\n"
										+ "  document.mainForm.ARRAY_DATA.value=temp;\n"
										+ "}\n"
										+ "\n"
										+ "function showPreview() {\n"
										+ "  //=====================================================================================\n"
										+ "  // Constant array data\n"
										+ "  //=====================================================================================\n"
										+ "\n"
										+ "  colours = [\n"
										+ "    \"FFFFFF\", \"000000\", \"666666\", \"999999\", \"CCCCCC\", \"FF0000\", \"0000FF\",\"009900\",\n"
										+ "    \"000099\", \"99CCFF\", \"990099\", \"FF9900\", \"FFFFCC\", \"CCCCFF\", \"CCFFFF\", \"CCFFCC\"\n"
										+ "  ];\n"
										+ "  fonts = [\n"
										+ "    \"Times New Roman, Times\", \"Arial\", \"Times New Roman, Times\", \"Courier New\", \"Georgia\", \"Verdana, Arial\"\n"
										+ "  ];\n"
										+ "  align = [\"left\", \"middle\", \"right\"];\n"
										+ "  textTop = [\""
										+ rbExampleTitle
										+ "\", \""
										+ rbExampleSubTitle
										+ "\"];\n"
										+ "  textBottom = \""
										+ rbExampleFooter
										+ "\";\n"
										+ "  sData = \""
										+ rbData
										+ "\";\n"
										+ "  sLabel = \""
										+ rbLabel
										+ "\";\n"
										+ "  sGroup = \""
										+ rbGroup
										+ "\";\n"
										+ "  sDetail=\""
										+ rbDetail
										+ "\";\n"
										+ "\n"
										+ "  //=====================================================================================\n"
										+ "  // Start creating html String\n"
										+ "  //=====================================================================================\n"
										+ "\n"
										+ "  line = 0;\n"
										+ "  html =\n"
										+ "    \"<table cellspacing='0' cellpadding='0' width='100%'>\\n\";\n"
										+ "\n"
										+ "  // do first 2 lines - Title & Sub Title\n"
										+ "  for (line = 0; line < 2; line++)\n"
										+ "    html +=\n"
										+ "      addLine (line, 6) +\n"
										+ "      \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\" +\n"
										+ "      \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\" +\n"
										+ "      fontStart (line) + textTop[line] +  fontEnd (line) +\n"
										+ "      \"</td></tr>\\n\" +\n"
										+ "      addLine (line, 7);\n"
										+ "\n"
										+ "  // create group html (if there is any)\n"
										+ "  for (g = 0; g < numOfGroups; g++) {\n"
										+ "    line = 2 + (g * 2);\n"
										+ "    if (groups[g] == 0)\n"
										+ "      html +=\n"
										+ "        addLine (line, 6) +\n"
										+ "        \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\\n\" +\n"
										+ "        \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\\n\" +\n"
										+ "        fontStart (line) + sGroup + \" \" + (g+1) + \" - \" + sLabel + fontEnd (line) +\n"
										+ "        \"</td></tr>\\n\" +\n"
										+ "        addLine (line, 7);\n"
										+ "    line++;\n"
										+ "    html +=\n"
										+ "      addLine (line, 6) +\n"
										+ "      \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\\n\" +\n"
										+ "      \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\" +\n"
										+ "      fontStart (line) + sGroup + \" \" + (g+1) + \" - \";\n"
										+ "    if (groups [g] == 1)\n"
										+ "      html += sLabel + \": \";\n"
										+ "    html += sData + fontEnd (line) +\n"
										+ "      \"</td></tr>\\n\" +\n"
										+ "      addLine (line, 7);\n"
										+ "  }\n"
										+ "\n"
										+ "  // create html for detail\n"
										+ "  line = 12;\n"
										+ "  html +=\n"
										+ "    addLine (line, 6) +\n"
										+ "    \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\\n\" +\n"
										+ "    \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\\n\" +\n"
										+ "    fontStart (line) + sDetail + \" - \" + sLabel + fontEnd (line) +\n"
										+ "    \"</td></tr>\\n\" +\n"
										+ "    addLine (line, 7);\n"
										+ "line++;\n"
										+ "html +=\n"
										+ "  addLine (line, 6) +\n"
										+ "  \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\\n\" +\n"
										+ "  \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\" +\n"
										+ "  fontStart (line) + sDetail + \" - \" + sData + fontEnd (line) +\n"
										+ "  \"</td></tr>\\n\" +\n"
										+ "  addLine (line, 7);\n"
										+ "  // Alternate line sections\n"
										+ "  if (lines_info[line][6] == 13) {\n"
										+ "    html +=\n"
										+ "      \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][10]] + \"'>\" +\n"
										+ "      \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\" +\n"
										+ "      fontStart (line) + sDetail + \" - \" + sData + \" ("
										+ rbAlternateLines
										+ ")\" + fontEnd (line) +\n"
										+ "    \"</td></tr>\" + addLine (line, 7);\n"
										+ "  }\n"
										+ "\n"
										+ "  // Create rest\n"
										+ "  line++;\n"
										+ "  html +=\n"
										+ "    addLine (line, 6) +\n"
										+ "    \"<tr align=\" + align[lines_info[line][5]] + \" bgColor='\" + colours[lines_info[line][9]] + \"'>\\n\" +\n"
										+ "    \"<td onClick=\\\"lineFocus(\" + line +\");\\\">\\n\" +\n"
										+ "    fontStart (line) + textBottom +  fontEnd (line) +\n"
										+ "    \"</td></tr>\\n\" +\n"
										+ "    addLine (line, 7);\n"
										+ "\n"
										+ "  // finish and update DIVs\n"
										+ "  html += \"</table>\\n\";\n"
										+ "  previewHtml.innerHTML=html;\n"
										+ "\n"
										+ "  //=====================================================================================\n"
										+ "  // addLine\n"
										+ "  //=====================================================================================\n"
										+ "\n"
										+ "  function addLine (line, tOrB) {\n"
										+ "    var heights=[1,2,4,6,10,15];\n"
										+ "    index = lines_info[line][tOrB];\n"
										+ "    if (index > 0 && index < 7) {\n"
										+ "      return (\n"
										+ "        \"<tr bgColor='\" + colours[lines_info[line][(tOrB + 4)]] +\n"
										+ "        \"' height='\" + heights[lines_info[line][tOrB] -1] + \"'>\" +\n"
										+ "        \"<td><img src='images/1p.gif' width='1' height='1'></td></tr>\\n\"\n"
										+ "       );\n"
										+ "    }\n"
										+ "    else if (index > 7 && index < 12) {\n"
										+ "      index = lines_info[line][tOrB];\n"
										+ "      temp = \"\";\n"
										+ "      for (i = 7; i < index; i++)\n"
										+ "        temp += \"<tr bgColor='\" + colours[lines_info[line][(tOrB + 4)]] + \"'><td>&nbsp;</td></tr>\\n\";\n"
										+ "      return temp;\n"
										+ "    }\n"
										+ "    else return \"\";\n"
										+ "  }\n"
										+ "\n"
										+ "  //=====================================================================================\n"
										+ "  // fontStart\n"
										+ "  //=====================================================================================\n"
										+ "\n"
										+ "  function fontStart(line) {\n"
										+ "    fontHtml = \"\";\n"
										+ "    if (lines_info[line][2] == 1) fontHtml += \"<b>\";\n"
										+ "    if (lines_info[line][3] == 1) fontHtml += \"<i>\";\n"
										+ "    if (lines_info[line][4] == 1) fontHtml += \"<u>\";\n"
										+ "    fontHtml +=\n"
										+ "     \"<font color='\" + colours[lines_info[line][8]] +\n"
										+ "     \"' face='\" + fonts[lines_info[line][0]] + \"' \" +\n"
										+ "     \"size = '\" + lines_info[line][1] + \"'>\";\n"
										+ "\n"
										+ "    return fontHtml;\n"
										+ "  }\n"
										+ "\n"
										+ "  //=====================================================================================\n"
										+ "  // fontEnd\n"
										+ "  //=====================================================================================\n"
										+ "\n"
										+ "  function fontEnd(line) {\n"
										+ "    fontHtml = \"\";\n"
										+ "    if (lines_info[line][4] == 1) fontHtml += \"</u>\";\n"
										+ "    if (lines_info[line][3] == 1) fontHtml += \"</i>\";\n"
										+ "    if (lines_info[line][2] == 1) fontHtml += \"</b>\";\n"
										+ "    fontHtml += \"</font>\";\n"
										+ "\n"
										+ "    return fontHtml;\n"
										+ "  }\n"
										+ "}\n"
										+ "\n"
										+ "</script>\n"
										+ "  <form method='post' action='' name='mainForm'>\n"
										+ "    <tr>\n"
										+ "      <td bgcolor=\"#CCCCCC\">\n"
										+ "        <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#999999\">\n"
										+ "          <tr bgcolor=\"#CCCCCC\">\n"
										+ "            <td colspan=\"2\" nowrap>\n"
										+ "              <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\"><b>&nbsp;"
										+ rbFont
										+ "</font></b>\n"
										+ "              <select name=\"FONT\" onChange=\"setFont();\">\n"
										+ "                <option value=\"0\" selected>"
										+ rbfont0
										+ "</option>\n"
										+ "                <option value=\"1\">"
										+ rbfont1
										+ "</option>\n"
										+ "                <option value=\"2\">"
										+ rbfont2
										+ "</option>\n"
										+ "                <option value=\"3\">"
										+ rbfont3
										+ "</option>\n"
										+ "                <option value=\"4\">"
										+ rbfont4
										+ "</option>\n"
										+ "                <option value=\"5\">"
										+ rbfont5
										+ "</option>\n"
										+ "              </select>\n"
										+ "              <select name=\"FONT_SIZE\" onChange=\"setFontSize();\">\n"
										+ "                <option value=\"2\" selected>-</option>\n"
										+ "                <option value=\"1\">1</option>\n"
										+ "                <option value=\"2\">2</option>\n"
										+ "                <option value=\"3\">3</option>\n"
										+ "                <option value=\"4\">4</option>\n"
										+ "                <option value=\"5\">5</option>\n"
										+ "                <option value=\"6\">6</option>\n"
										+ "                <option value=\"7\">7</option>\n"
										+ "              </select>\n"
										+ "              </font></td>\n"
										+ "            <td align=\"center\" nowrap>\n"
										+ "              <font color=\"#00000\" face=\"Verdana, Arial\" size=\"2\"><b>"
										+ rbLines
										+ "</b></font>\n"
										+ "            </td>\n"
										+ "            <td rowspan=\"2\" nowrap>\n"
										+ "              <select  onChange=\"setColourChooser();\" name=\"CURCOLOUR\" size=\"3\">\n"
										+ "                <option value=\"0\" selected>"
										+ rbText
										+ "</option>\n"
										+ "                <option value=\"1\">"
										+ rbBackground
										+ "</option>\n"
										+ "                <option value=\"2\">"
										+ rbLineTop
										+ "</option>\n"
										+ "                <option value=\"3\">"
										+ rbLineBottom
										+ "</option>\n"
										+ "              </select>\n"
										+ "            </td>\n"
										+ "            <td rowspan=\"2\" nowrap>\n"
										+ "              <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#000000\">\n"
										+ "                <tr bgcolor=\"#FFFFFF\" align=\"center\" valign=\"middle\">\n"
										+ "                  <td bgcolor=\"#FFFFFF\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(0);\">\n"
										+ "                  </td>\n"
										+ "                  <td bgcolor=\"#000000\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(1);\">\n"
										+ "                  </td>\n"
										+ "                  <td bgcolor=\"#666666\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(2);\">\n"
										+ "                  </td>\n"
										+ "                  <td bgcolor=\"#999999\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(3);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#CCCCCC\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(4);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#FF0000\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(5);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#0000FF\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(6);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#009900\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(7);\">\n"
										+ "                  </td>\n"
										+ "                </tr>\n"
										+ "                <tr>\n"
										+ "                  <td width=\"25\" bgcolor=\"#000099\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(8);\">\n"
										+ "                  </td>\n"
										+ "                  <td bgcolor=\"#99CCFF\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(9);\">\n"
										+ "                  </td>\n"
										+ "                  <td bgcolor=\"#990099\" width=\"25\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(10);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#FF9900\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(11);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#FFFFCC\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(12);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#CCCCFF\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(13);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#CCFFFF\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(14);\">\n"
										+ "                  </td>\n"
										+ "                  <td width=\"25\" bgcolor=\"#CCFFCC\" height=\"25\">\n"
										+ "                    <input type=\"radio\" name=\"COLOUR\" onClick=\"setColour(15);\">\n"
										+ "                  </td>\n"
										+ "                </tr>\n"
										+ "              </table>\n"
										+ "            </td>\n"
										+ "          </tr>\n"
										+ "          <tr bgcolor=\"#CCCCCC\">\n"
										+ "            <td width=\"77\" nowrap>\n"
										+ "              <table border=\"0\" cellspacing=\"0\" cellpadding=\"3\">\n"
										+ "                <tr>\n"
										+ "                  <td>\n"
										+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\"><b>"
										+ rbBold
										+ "</b></font>\n"
										+ "                  </td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"checkbox\" name=\"BOLD\" value=\"1\" onClick=\"setBold();\">\n"
										+ "                  </td>\n"
										+ "                  <td>\n"
										+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\"><i>"
										+ rbItalics
										+ "</i></font>\n"
										+ "                  </td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"checkbox\" name=\"ITALICS\" value=\"1\" onClick=\"setItalics();\">\n"
										+ "                  </td>\n"
										+ "                  <td>\n"
										+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\"><u>"
										+ rbUnderline
										+ "</u></font>\n"
										+ "                  </td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"checkbox\" name=\"UNDERLINE\" value=\"1\" onClick=\"setUnderLine();\">\n"
										+ "                  </td>\n"
										+ "                </tr>\n"
										+ "              </table>\n"
										+ "            </td>\n"
										+ "            <td nowrap>\n"
										+ "              <table border=\"0\" cellspacing=\"0\" cellpadding=\"3\">\n"
										+ "                <tr>\n"
										+ "                  <td><img src=\"/gor/images/icon_align_left.gif\" width=\"14\" height=\"11\"></td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"radio\" name=\"ALIGN\" value=\"left\" onClick=\"setAlign(0);\">\n"
										+ "                  </td>\n"
										+ "                  <td><img src=\"/gor/images/icon_align_middle.gif\" width=\"14\" height=\"11\"></td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"radio\" name=\"ALIGN\" value=\"middle\" onClick=\"setAlign(1);\">\n"
										+ "                  </td>\n"
										+ "                  <td><img src=\"/gor/images/icon_align_right.gif\" width=\"14\" height=\"11\"></td>\n"
										+ "                  <td>\n"
										+ "                    <input type=\"radio\" name=\"ALIGN\" value=\"right\" onClick=\"setAlign(2);\">\n"
										+ "                  </td>\n"
										+ "                </tr>\n"
										+ "              </table>\n"
										+ "            </td>\n"
										+ "            <td nowrap>\n"
										+ "              <font color=\"#00000\" face=\"Verdana, Arial, Helvetica\" size=\"2\">"
										+ rbTop
										+ ":</font>\n"
										+ "              <select name=\"LINE_TOP\" onFocus=\"updateColourChooser(2);\" onChange=\"setLineTop();\">\n"
										+ "                <option value=\"0\" selected>-</option>\n"
										+ "                <option value=\"1\">1</option>\n"
										+ "                <option value=\"2\">2</option>\n"
										+ "                <option value=\"3\">4</option>\n"
										+ "                <option value=\"4\">6</option>\n"
										+ "                <option value=\"5\">10</option>\n"
										+ "                <option value=\"6\">15</option>\n"
										+ "                <option value=\"7\">-</option>\n"
										+ "                <option value=\"8\">S1</option>\n"
										+ "                <option value=\"9\">S2</option>\n"
										+ "                <option value=\"10\">S3</option>\n"
										+ "                <option value=\"11\">S4</option>\n"
										+ "                <option value=\"12\">-</option>\n"
										+ "                <option value=\"13\">AL</option>\n"
										+ "              </select>\n"
										+ "              <font color=\"#000000\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\">"
										+ rbBottom
										+ ":</font>\n"
										+ "              <select name=\"LINE_BOTTOM\" onFocus=\"updateColourChooser(3);\" onChange=\"setLineBottom();\">\n"
										+ "                <option value=\"0\" selected>-</option>\n"
										+ "                <option value=\"1\">1</option>\n"
										+ "                <option value=\"2\">2</option>\n"
										+ "                <option value=\"3\">4</option>\n"
										+ "                <option value=\"4\">6</option>\n"
										+ "                <option value=\"5\">10</option>\n"
										+ "                <option value=\"6\">15</option>\n"
										+ "                <option value=\"7\">-</option>\n"
										+ "                <option value=\"8\">S1</option>\n"
										+ "                <option value=\"9\">S2</option>\n"
										+ "                <option value=\"10\">S3</option>\n"
										+ "                <option value=\"11\">S4</option>\n"
										+ "              </select>\n"
										+ "            </td>\n"
										+ "          </tr>\n"
										+ "        </table>\n"
										+ "      </td>\n"
										+ "    </tr>\n"
										+ "    <tr>\n"
										+ "      <td bgcolor=\"#000000\"><img src=\"/GOR/images/1p.gif\" width=\"1\" height=\"1\"></td>\n"
										+ "    </tr>\n"
										+ "    <tr>\n"
										+ "      <td>&nbsp;</td>\n"
										+ "    </tr>\n"
										+ "    <tr>\n"
										+ "      <td>\n"
										+ "        <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#000000\">\n"
										+ "          <tr>\n"
										+ "            <td>\n"
										+ "              <table width=\"700\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\" bgcolor=\"#999999\">\n"
										+ "                <tr align=\"center\">\n"
										+ "                  <td colspan=\"2\" bgcolor=\"#000099\">\n"
										+ "                    <font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"2\"><b>"
										+ rbFormatClickOnALineToFormatIt
										+ "</b></font></td>\n"
										+ "                </tr>\n"
										+ "                <tr>\n"
										+ "                  <td bgcolor=\"#ffffff\" colspan=\"2\">\n"
										+ "                    <div id=previewHtml></div>\n"
										+ "                  </td>\n"
										+ "                </tr>\n");

						for (int g = 0; g < groups.size(); g++) {
							int intAlias = groups.getGroupAlias(g);
							int intColumn = groups.getGroupColumn(g);
							int intTable = tablesAliases.getTable(intAlias); // need
																				// to
																				// get
																				// column

							String groupText = tablesAliases.getAlias(intAlias)
									+ "."
									+ dcf.getColumnName(intTable, intColumn);

							html
									.append("                <tr align=\"center\">\n"
											+ "                  <td width=\"350\" bgcolor=\"#CCCCCC\" align=\"right\">\n"
											+ "                    <font face=\"Verdana, Arial\" size=\"2\" color=\"#000000\"><b>\n"
											+ rbGroup
											+ " "
											+ (g + 1)
											+ " - <font color=\"#0000FF\">"
											+ groupText
											+ "</font>:</b></font><font size=\"2\" face=\"Verdana, Arial\">\n"
											+ "                    </font>\n"
											+ "                  </td>\n"
											+ "                  <td align=\"left\" bgcolor=\"#CCCCCC\">\n"
											+ "                    <input type=\"radio\" name=\"GROUP_"
											+ g
											+ "\" onClick=\"setGroup("
											+ g
											+ ",0);\">\n"
											+ "                    <img src=\"/gor/images/icon_format_tb.gif\" width=\"14\" height=\"11\">\n"
											+ "                    <input type=\"radio\" name=\"GROUP_"
											+ g
											+ "\" value=\"1\" onClick=\"setGroup("
											+ g
											+ ",1);\">\n"
											+ "                    <img src=\"/gor/images/icon_format_ss.gif\" width=\"14\" height=\"11\">\n"
											+ "                    <input type=\"radio\" name=\"GROUP_"
											+ g
											+ "\" value=\"1\" onClick=\"setGroup("
											+ g
											+ ",2);\">\n"
											+ "                    <img src=\"/gor/images/icon_format_data.gif\" width=\"14\" height=\"11\">\n"
											+ "                  </td>\n"
											+ "                </tr>\n");
						}

						html
								.append("                <tr>\n"
										+ "                  <td colspan=\"2\" bgcolor=\"#CCCCCC\" >\n"
										+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\"><b>"
										+ rbPage
										+ ":</b>\n"
										+ "                    <input type=\"radio\" name=\"PAGE\" value=\"0\"");
						// put value of thing on thing!!!!
						if (format.getPageOrientation() == 0)
							html.append("checked");
						html
								.append("                    >\n<img src=\"/gor/images/icon_page_portrait.gif\" width=\"11\" height=\"13\">\n"
										+ "                    "
										+ rbProtrait
										+ "\n"
										+ "                    <input type=\"radio\" name=\"PAGE\" value=\"1\"");
						if (format.getPageOrientation() == 1)
							html.append("checked");
						html
								.append("                    >\n<img src=\"/gor/images/icon_page_landscape.gif\" width=\"13\" height=\"13\">\n"
										+ "                    "
										+ rbLandscape
										+ "\n"
										+ "                    <input type=\"radio\" name=\"PAGE\" value=\"2\"");
						if (format.getPageOrientation() == 2)
							html.append("checked");
						html
								.append("                      >\n<img src=\"/gor/images/icon_page_100.gif\" width=\"13\" height=\"13\">\n"
										+ "                    "
										+ rbOneHundredPercent
										+ "</font> </td>\n"
										+ "                </tr>\n"
										+ "                <tr>\n"
										+ "                  <td colspan=\"2\" bgcolor=\"#CCCCCC\" >\n"
										+ "                    <font color=\"#000000\" face=\"Verdana, Arial\" size=\"2\">\n"
										+ "                    <b>"
										+ rbTemplates
										+ ": </b>\n"
										+ "                    <select name=\"TEMPLATES\">\n"
										+ "                      <option value=\"0\" selected>"
										+ rbtemplate0
										+ "</option>\n"
										+ "                      <option value=\"1\">"
										+ rbtemplate1
										+ "</option>\n"
										+ "                      <option value=\"2\">"
										+ rbtemplate2
										+ "</option>\n"
										+ "                      <option value=\"3\">"
										+ rbtemplate3
										+ "</option>\n"
										+ "                      <option value=\"4\">"
										+ rbtemplate4
										+ "</option>\n"
										+ "                      <option value=\"5\">"
										+ rbtemplate5
										+ "</option>\n"
										+ "                      <option value=\"6\">"
										+ rbtemplate6
										+ "</option>\n"
										+ "                    </select>\n"
										+ "                    </font>\n"
										+ "                    <input type=\"button\" name=\"Submit\" value=\""
										+ rbReset
										+ "\" onClick=\"resetFromTemplate();\">\n"
										+ "                  </td>\n"
										+ "                </tr>\n"
										+ "                <tr align=\"center\">\n"
										+ "                  <td colspan=\"2\" bgcolor=\"#CCCCCC\"><font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">\n"
										+ "                    <input type=\"submit\" name=\"Submit\" value=\""
										+ rbOK
										+ "\">\n"
										+ "                    </font></td>\n"
										+ "                </tr>\n"
										+
										// "                <tr><td colspan=\"2\" bgcolor=\"#CCCCCC\"><textarea rows=\"10\" cols=\"40\" name=\"JSTEXT\" onClick=\"showJS();\"></textarea></td></tr>\n"
										// +
										"              </table>\n"
										+ "            </td>\n"
										+ "          </tr>\n"
										+ "        </table>\n"
										+ "      </td>\n"
										+ "    </tr>\n"
										+ "    <tr>\n"
										+ "      <td>\n"
										+ "        <input type=\"hidden\" name=\"ARRAY_DATA\" value=\"default\">\n"
										+ "      </td>\n"
										+ "    </tr>\n"
										+ "  </form>\n"
										+ "<script language = \"JavaScript\">\n"
										+ "// show groups once page has loaded up.\n"
										+ "updateHiddenField();\n"
										+ "refreshGroups();\n"
										+ "</script>\n"
										+ htmlOutput.pageBottom());
						out.print(html.toString());
					} catch (Exception exGen) {
						out.println(htmlOutput.errorGeneral());
					}

				} // if (edit.equals("1"))
				else
					out.println(htmlOutput.errorNoReportEditting());
			} // if (report != null)
			else
				out.println(htmlOutput.errorNoOpenReport());
		} // if (userName != null)
		else
			out.println(htmlOutput.errorNoSession());
	}
}