package cz.vutbr.fit.dash;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import cz.vutbr.fit.dash.gui.DashAppGUI;
import cz.vutbr.fit.dash.model.DashAppModel;

/**
 * Main class which initiates GUI.
 * 
 * @author Jiri Hynek
 *
 */
public class Main {

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ahoj");

		// set look and feel
		setLookAndFeel();

		// initialize GUI
		DashAppGUI.getInstance().launchApplication();

		// initialize model
		DashAppModel.getInstance().initModel();
	}

	private static void setLookAndFeel() {
		try {
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
			// UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, we let default look and feel.
			logError("Look and feel could not have been initialized.", e);
		}
	}

	/**
	 * Method logs error and print exception.
	 * 
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Exception e) {
		logError(message);
		e.printStackTrace();
	}

	/**
	 * Method logs error.
	 * 
	 * @param message
	 */
	public static void logError(String message) {
		System.err.println(message);
	}

}
