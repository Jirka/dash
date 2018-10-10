package cz.vutbr.fit.dashapp.view.dialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleDialogs {
	
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
	
	/**
	 * Default report.
	 *
	 * @param s
	 * @return 
	 */
	public static String inputText(String s, String implicitValue) {
		return JOptionPane.showInputDialog(s, implicitValue);
	}

}
