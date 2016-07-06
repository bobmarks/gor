//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

import java.util.StringTokenizer;

/**
 * This class holds format information. This includes the format data Matrix
 * which is a 12 x 15 array of numbers. It's columns (12) hold the following
 * information:<br>
 * <br>
 * <ol>
 * <li>font</li>
 * <li>font size</li>
 * <li>bold</li>
 * <li>italics</li>
 * <li>underine</li>
 * <li>align</li>
 * <li>top line thickness</li>
 * <li>bottom line thickness</li>
 * <li>text colour</li>
 * <li>background colour</li>
 * <li>top line colour</li>
 * <li>b.line colour</li>
 * </ol>
 * It's (rows) contain this information for each row in the report.<br>
 * It also contains the groups arrays which is an array of 5 numbers and the
 * page orientation which is a single integer.
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
public class Format {
	// declare 2 dimensional array [15][12] to hold data about each line in
	// report
	private static int[][] formatData;
	// declare 1 dimensional array to hold group information [5]
	private static int[] groups;
	// page orientation (integer)
	private static int pageOrientation;

	private static final String[] fontSelection = { "Times New Roman", "Arial",
			"Times New Roman", "Courier New", "Georgia", "Veranda, Arial" };
	private static final String[] colourList = { "FFFFFF", "000000", "666666",
			"999999", "CCCCCC", "FF0000", "0000FF", "009900", "000099",
			"99CCFF", "990099", "FF9900", "FFFFCC", "CCCCFF", "CCFFFF",
			"CCFFCC" };
	private static final String[] align = { "left", "middle", "right" };

	/**
	 * Constructor simple initilises arrays
	 */	
	public Format() {
		// TESTING
		formatData = new int[15][12];
		groups = new int[5];
	}

	/**
	 * Sets up both arrays (formatData & groups) and the pageOrientation
	 * variable from the 2 files in the report file.
	 * 
	 * @param newFormatData
	 *            Comma delimited string containing 185 numbers (12 x 15 + 5)
	 *            which populate the formatData and groups arrays.
	 * @param newPageOrientation
	 *            number which sets the pageOrientation int. 0 = portrait, 1 =
	 *            landscape, 2 = 100%
	 */
	public void setAll(String newFormatData, String newPageOrientation) {
		// Create StringTokenizer to go through all the numbers in the string
		StringTokenizer st = new StringTokenizer(newFormatData);

		try {
			// set up data for format data (first 180 numbers in
			// StringTokenizer) ...
			for (int i = 0; i < 15; i++)
				for (int j = 0; j < 12; j++)
					formatData[i][j] = Integer.parseInt(st.nextToken(",")
							.trim());

			// ... set up groups from remaining 5 numbers in StringTokenizer
			for (int g = 0; g < 5; g++)
				groups[g] = Integer.parseInt(st.nextToken(",").trim());
			// set up page orientation
			pageOrientation = Integer.parseInt(newPageOrientation);
		} catch (NumberFormatException exNFE) {
		} // no nothing
	}

	/**
	 * Converts all the data from the formatData / groups arrays and
	 * pageOrientation into a String which can be saved to the report file
	 * (opposite of setAll() method).
	 * 
	 * @return entire format information in String format (2 lines)
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("PAGE_ORIENTATION=" + pageOrientation + "\n");
		// set up data for format data
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 12; j++)
				temp.append(formatData[i][j] + ",");
		for (int g = 0; g < 5; g++)
			temp.append(groups[g] + ",");
		temp.append("\n");

		return temp.toString();
	}

	/**
	 * Uses the font[0], font size[1], bold[2], italics[3], underline[4] and
	 * text colour [8] to return a String of HTML describing the text.<br>
	 * e.g. if font = Arial, font size = 5, bold = 1, italics = 0, underline = 1
	 * and colour = 9 then HTML will look like:<br>
	 * "<font face='Arial' size='5' color='99CCFF'><b><u>"<br>
	 * To use this you would write e.g. getHtmlTextStart (3) + "Some Text" +
	 * getHtmlTextEnd ().
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return HTML representation of start of font string
	 */
	public String getHtmlTextStart(int row) {
		StringBuffer temp = new StringBuffer("<font " + "face='" + getFont(row)
				+ "' " + "size='" + getFontSize(row) + "' " + "color='"
				+ getColourText(row) + "'>");
		if (getBold(row))
			temp.append("<b>");
		if (getItalics(row))
			temp.append("<i>");
		if (getUnderline(row))
			temp.append("<u>");
		return temp.toString();
	}

	/**
	 * Complements the getHtmlTextStart(row) method. Only worrys about values of
	 * bold, italics and underline at a row. e.g. if bold = 1, italics = 0,
	 * underline = 1 then the html will look: "</b></u>" (note there is no </i>)<br>
	 * To use this you would write e.g. getHtmlTextStart (3) + "Some Text" +
	 * getHtmlTextEnd ().
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return HTML representation of end of font string
	 */
	public String getHtmlTextEnd(int row) {
		StringBuffer temp = new StringBuffer();
		if (getUnderline(row))
			temp.append("</u>");
		if (getItalics(row))
			temp.append("</i>");
		if (getBold(row))
			temp.append("</b>");
		temp.append("</font>");
		return temp.toString();
	}

	/**
	 * Accesses formatData array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @param column
	 *            (0 to 11)
	 * @return value at position
	 */
	public int getItem(int row, int column) {
		return formatData[row][column];
	}

	/**
	 * Accesses group array.
	 * 
	 * @param number
	 *            of group (0 to 4)
	 * @return group orientation
	 */
	public int getGroupLayout(int group) {
		return groups[group];
	}

	/**
	 * Returns textual description of a font at a specified row. Note: It gets
	 * the text from the fontSelection array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return textual description of font. 1 = Arial etc
	 */
	public String getFont(int row) {
		return fontSelection[formatData[row][0]];
	}

	/**
	 * Returns size of font at a specified row.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return size of font
	 */
	public int getFontSize(int row) {
		return formatData[row][1];
	}

	/**
	 * Returns true if a font is in bold at a specified row.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return true if bold is 1, false other wise
	 */
	public boolean getBold(int row) {
		return (formatData[row][2] == 1);
	}

	/**
	 * Returns true if a font is in italics at a specified row.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return true if italics is 1, false other wise
	 */
	public boolean getItalics(int row) {
		return (formatData[row][3] == 1);
	}

	/**
	 * Returns true if a font is underlined at a specified row.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return true if underline is 1, false other wise
	 */
	public boolean getUnderline(int row) {
		return (formatData[row][4] == 1);
	}

	/**
	 * Returns textual description of the alignment at a specified row. Note: It
	 * gets the text from the align array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return textual description of an alignment. 0 = left, 1 = middle, 2 =
	 *         right
	 */
	public String getAlign(int row) {
		return align[formatData[row][5]];
	}

	/**
	 * Returns the size of a line at a particular row. Line can be top or
	 * bottom.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @param topOrBottom
	 *            (6 = top or 7 = bottom)
	 * @return size of a line
	 */
	public int getLineIndex(int row, int topOrBottom) {
		return formatData[row][topOrBottom];
	}

	/**
	 * Returns textual description of the font colour at a specified row. Note:
	 * It gets the text from the colourList array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return textual description of colour e.g. #99CCFF
	 */
	public String getColourText(int row) {
		return colourList[formatData[row][8]];
	}

	/**
	 * Returns textual description of the background colour at a specified row.
	 * Note: It gets the text from the colourList array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @return textual description of colour e.g. #99CCFF
	 */
	public String getColourBackground(int row) {
		return colourList[formatData[row][9]];
	}

	/**
	 * Returns textual description of the font colour at a specified row. Note:
	 * It gets the text from the colourList array.
	 * 
	 * @param row
	 *            of report (0 to 14)
	 * @param topOrBottom
	 *            (6 = top or 7 = bottom). Similar in nature to getLineIndex()
	 * @return textual description of colour e.g. #99CCFF
	 */
	public String getColourLine(int row, int topOrBottom) {
		return colourList[formatData[row][topOrBottom + 4]];
	}

	/**
	 * Returns pageOrientation
	 * 
	 * @return number between 0 and 2
	 */
	public int getPageOrientation() {
		return pageOrientation;
	}

	/**
	 * Returns textual description of the page orientation.
	 * 
	 * @return textual description of page width: 0 = 635, 1 = 1005, 2 = 100%
	 */
	public String getPageWidth() {
		switch (pageOrientation) {
		case 0:
			return "635";
		case 1:
			return "1005";
		case 2:
			return "100%";
		default:
			return "635";
		}
	}
}