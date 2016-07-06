//==============================================================================
//
//  Generic Online Reporting
//
//  The following source code is for a final year computing science project.
//
//==============================================================================

package org.gor.test;

import org.gor.data.Labels;
import org.gor.data.ReportFile;

/**
 * This little program performs an offline test on gernal classes.
 */
public class OfflineTestGeneral {

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ReportFile reportFile = new ReportFile("northwind.gor");

		Labels labels = reportFile.getLabels();
		labels.setCurrentSetOfLabels(0); // English

		System.out.println("Title: " + labels.getTitle());
		System.out.println("Subtitle: " + labels.getSubTitle());
		System.out.println("Footer: " + labels.getFooter());
	}
}