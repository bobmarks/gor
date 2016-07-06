//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.data;

import java.util.StringTokenizer;
import java.util.Vector;

import org.gor.util.Query;

/**
 * This class loads all the labels from a report file. Each set of labels has a
 * title, subtitle, footer, number of labels, localeNum used (from
 * LabelLocales).
 * 
 * @author $Author: Bob Marks (marksie531@yahoo.com)
 * @version $Revision: 1.0
 */
@SuppressWarnings("unchecked")
public class Labels {

	// small internal structure to hold information on ONE set of labels
	private class SingleLocaleLabelData {
		int MAX_NUM_OF_COLS = Query.MAX_NUM_OF_COLS;

		public String title, subTitle, footer;
		public int numOfLabels, localeNum;
		public int[] labelAlias = new int[MAX_NUM_OF_COLS];
		public int[] labelColumn = new int[MAX_NUM_OF_COLS];
		public String[] labelText = new String[MAX_NUM_OF_COLS];
	}

	private static int defaultLocale, currentSet;

	private static Vector allLabelData;
	private static Vector labelLocaleNum;
	private static SingleLocaleLabelData curLabelData;

	/**
	 * Creates a new set of empty labels. 
	 */
	public Labels() {
		currentSet = 0; // no sets of labels et
		allLabelData = new Vector(); // holds ALL information on labels
		labelLocaleNum = new Vector(); // holds just locale numbers;
	}

	/**
	 * Updates the existing title, subtitle and footer of a set of labels.
	 * 
	 * @param title
	 *            name of new title
	 * @param subTitle
	 *            name of new subtitle
	 * @param footer
	 *            name of footer
	 */
	public void updateLabelData(String title, String subTitle, String footer) {
		curLabelData.title = title;
		curLabelData.subTitle = subTitle;
		curLabelData.footer = footer;
	}

	/**
	 * Creates a new set of labels, apart from the data labels.
	 * 
	 * @param localeNum
	 *            Number of locale from LabelLocales class.
	 * @param title
	 *            name of new title
	 * @param subTitle
	 *            name of new subtitle
	 * @param footer
	 *            name of footer
	 */
	public void createNewLabelData(int localeNum, String title,
			String subTitle, String footer) {
		curLabelData = new SingleLocaleLabelData();

		curLabelData.localeNum = localeNum;
		// also add to list
		Integer i = new Integer(localeNum);
		labelLocaleNum.addElement(i);

		curLabelData.title = title;
		curLabelData.subTitle = subTitle;
		curLabelData.footer = footer;
		curLabelData.numOfLabels = 0; // no data/runtime labels yet.
	}

	/**
	 * Populates the arrays for holding data label information from a single
	 * line read in from a report file.
	 * 
	 * @param alias
	 *            number of the alias
	 * @param column
	 *            number of the column
	 * @param text
	 *            Textual label given to alias.column
	 */
	public void addLabel(int alias, int column, String text) {
		int labelNum = curLabelData.numOfLabels;

		curLabelData.labelAlias[labelNum] = alias;
		curLabelData.labelColumn[labelNum] = column;
		curLabelData.labelText[labelNum] = text;

		curLabelData.numOfLabels++;
	}

	/**
	 * Overloaded version of adding a label. It reads all the label information
	 * fronm a single line read in from a report file.
	 * 
	 * @param line
	 *            Text from report file. e.g. 1,3,"ID"
	 */
	public void addLabel(String line) {
		int alias, column;
		String text = "";
		StringTokenizer st = new StringTokenizer(line);

		alias = Integer.parseInt(st.nextToken(","));
		column = Integer.parseInt(st.nextToken(","));
		if (st.hasMoreElements()) // text may be an empty value
			text = st.nextToken(",");

		addLabel(alias, column, text);
	}

	/**
	 * Sets the current set of labels.
	 * 
	 * @param text
	 *            Textual label given to alias.column
	 */
	public int addCurLabelToSet() {
		int index = currentSet;
		int position = 0;

		for (int i = 0; i < currentSet; i++)
			if (getLocaleUsed(i) < curLabelData.localeNum)
				position = i;

		allLabelData.insertElementAt(curLabelData, position);
		currentSet++;

		return index;
	}

	/**
	 * Adds current set of labels to the complete list of labels.
	 */
	public void addCurLabelToList() {
		allLabelData.add(curLabelData);
		currentSet++;
	}

	/**
	 * Sets the default set of locales.
	 */
	public void setDefaultLocale(int newDefaultLocale) {
		defaultLocale = newDefaultLocale;
	}

	/**
	 * Displays the total number of locales used.
	 * 
	 * @return Number of locales.
	 */
	public int getNumOfLocalesUsed() {
		return allLabelData.size();
	}

	/**
	 * Shows which locale used for a specified set of labels.
	 * 
	 * @param num
	 *            Number of interface labels.
	 * @return Locale used.
	 */
	public int getLocaleUsed(int num) {
		if (labelLocaleNum.size() != 0) {
			Integer intLocaleUsed = (Integer) labelLocaleNum.elementAt(num);
			return intLocaleUsed.intValue();
		} else
			return -1;
	}

	/**
	 * Displays the default locale.
	 * 
	 * @return Locale number.
	 */
	public int getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Gets name of data label from a specified number.
	 * 
	 * @param num
	 *            Number of interface labels.
	 * @return Label.
	 */
	public String getLabel(int index) {
		return curLabelData.labelText[index];
	}

	/**
	 * Gets name of data label using the alias and query number.
	 * 
	 * @param intQueryAlias
	 *            Number of query alias.
	 * @param intQueryColumn
	 *            Number of query column
	 * @return Text for this alias.column.
	 */
	public String getLabel(int intQueryAlias, int intQueryColumn) {
		try {
			for (int i = 0; i < curLabelData.numOfLabels; i++) {
				if (curLabelData.labelAlias[i] == intQueryAlias
						&& curLabelData.labelColumn[i] == intQueryColumn)
					return curLabelData.labelText[i];
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Updates information for all the DATA labels.
	 * 
	 * @param intQueryAlias
	 *            Number of query alias.
	 * @param intQueryColumn
	 *            Number of query column
	 * @param labelText
	 *            Text of label.
	 */
	public void updateLabel(int intQueryAlias, int intQueryColumn,
			String labelText) {
		for (int i = 0; i < curLabelData.numOfLabels; i++) {
			if (curLabelData.labelAlias[i] == intQueryAlias
					&& curLabelData.labelColumn[i] == intQueryColumn) {
				curLabelData.labelText[i] = labelText;
				return;
			}
		}
		addLabel(intQueryAlias, intQueryColumn, labelText);
	}

	/**
	 * Converts all the information from all the arrays/vectors into a String
	 * which can be saved in the [LABELS] section of a report file.
	 * 
	 * @return All report labels information in 1 String.
	 */
	public String toString() {
		StringBuffer text = new StringBuffer();
		text.append("DEFAULT_LOCALE=" + defaultLocale + "\n");

		for (int i = 0; i < getNumOfLabelSets(); i++) {
			curLabelData = (SingleLocaleLabelData) allLabelData.elementAt(i);

			text.append("\n[LABELS_" + curLabelData.localeNum + "]");
			text.append("\nLOCALE=" + curLabelData.localeNum);
			text.append("\nTITLE=" + curLabelData.title);
			text.append("\nSUBTITLE=" + curLabelData.subTitle);
			text.append("\nFOOTER=" + curLabelData.footer + "\n");

			for (int j = 0; j < curLabelData.numOfLabels; j++) {
				text.append(curLabelData.labelAlias[j]);
				text.append("," + curLabelData.labelColumn[j]);
				text.append("," + curLabelData.labelText[j] + "\n");
			}
		}
		return text.toString();
	}

	/**
	 * @param
	 * @return
	 */
	public void setCurrentSetOfLabels(int localeNum) {
		for (int i = 0; i < getNumOfLabelSets(); i++) {
			curLabelData = (SingleLocaleLabelData) allLabelData.elementAt(i);
			if (curLabelData.localeNum == localeNum)
				return;
		}
	}

	/**
	 * Returns number of labels sets.
	 * 
	 * @return number of label sets.
	 */
	public int getNumOfLabelSets() {
		return currentSet;
	}

	/**
	 * Returns title of current set of labels.
	 * 
	 * @return Title.
	 */
	public String getTitle() {
		return curLabelData.title;
	}

	/**
	 * Returns subtitle of current set of labels.
	 * 
	 * @return Subtitle.
	 */
	public String getSubTitle() {
		return curLabelData.subTitle;
	}

	/**
	 * Returns footer of current set of labels.
	 * 
	 * @return Footer.
	 */
	public String getFooter() {
		return curLabelData.footer;
	}
}
