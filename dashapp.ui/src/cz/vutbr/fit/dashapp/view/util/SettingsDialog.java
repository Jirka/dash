package cz.vutbr.fit.dashapp.view.util;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Custom settings dialog
 * 
 * @author Jiri Hynek
 *
 */
public class SettingsDialog {
	
	public static final String DEFAULT_LABEL = "Settings";
	
	private String label;

	public SettingsDialog() {
		this(DEFAULT_LABEL);
	}
	
	public SettingsDialog(String label) {
		this.label = label;
	}
	
	public boolean showConfirmDialog() {
		// dialog panel
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // TODO use better layout
		
		 // custom settings
		getCustomSettings(panel);

		int option = JOptionPane.showConfirmDialog(null, panel, label, JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			processCustomSettings();
			return true;
		}
		return false;
	}

	protected void getCustomSettings(JPanel panel) {
		// modify according to requirements
	}
	
	protected void processCustomSettings() {
		// modify according to requirements
	}

}
