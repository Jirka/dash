package cz.vutbr.fit.dashapp.view.dialog.widgets;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleLabelWidget implements ISimpleSettingsWidget {

	protected JPanel panel;
	protected String label;

	public SimpleLabelWidget(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
	public JPanel createPanel() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		// configPanel.setLayout(new FlowLayout());
		// configPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		// label
		panel.add(new JLabel(label));
		
		return panel;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// do nothing
	};
}