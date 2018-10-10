package cz.vutbr.fit.dashapp.view.dialog;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Custom settings dialog
 * 
 * @author Jiri Hynek
 *
 */
public class BoxLayoutFormDialog extends DashAppFormDialog {
	
	public BoxLayoutFormDialog() {
		super(null);
	}

	public BoxLayoutFormDialog(String label) {
		super(null, label);
	}
	
	@Override
	protected JPanel createPanel() {
		JPanel panel = new JPanel(); // TODO use better layout
		layoutManager = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layoutManager);
		//panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		return panel;
	}

}
