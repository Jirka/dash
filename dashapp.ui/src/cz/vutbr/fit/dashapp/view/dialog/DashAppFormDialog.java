package cz.vutbr.fit.dashapp.view.dialog;

import java.awt.LayoutManager;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppFormDialog {

	public static final String DEFAULT_LABEL = "Settings";

	protected LayoutManager layoutManager;
	protected String label;
	
	public DashAppFormDialog(LayoutManager layoutManager) {
		this(layoutManager, DEFAULT_LABEL);
	}

	public DashAppFormDialog(LayoutManager layoutManager, String label) {
		this.layoutManager = layoutManager;
		this.label = label;
	}
	
	protected JPanel createPanel() {
		return new JPanel(layoutManager);
	}

	public boolean showConfirmDialog() {
		// dialog panel
		JPanel panel = createPanel();

		// custom settings
		addCustomWidgets(panel);

		int option = JOptionPane.showConfirmDialog(null, panel, label, JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			return processCustomWidgets();
		}
		return false;
	}

	protected void addCustomWidgets(JPanel panel) {
		// modify according to requirements
	}

	protected boolean processCustomWidgets() {
		// modify according to requirements
		return true;
	}

}
