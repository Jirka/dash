package cz.vutbr.fit.dash.view.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Dialogs {
	
	/**
	 * Yes/no dialog.
	 * 
	 * @param question
	 * @return result
	 */
	public static int YesNoCancel(String question) {
		JFrame frame = new JFrame();
		int result = JOptionPane.showConfirmDialog(frame, question);
		return result;
	}
	
	
	/**
	 * Default report.
	 *
	 * @param s
	 */
	public static void report(String s) {
		JFrame parent = new JFrame();
		JOptionPane.showMessageDialog(parent, s);
	}

}
