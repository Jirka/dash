package cz.vutbr.fit.dashapp.view.dialog.widgets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleCheckBox extends SimpleLabelWidget implements ISimpleSettingsWidget {
	
	protected JCheckBox checkBox;
	protected boolean selected;
	
	public SimpleCheckBox(String label) {
		this(label, false);
	}

	public SimpleCheckBox(String label, boolean selected) {
		super(label);
		this.selected = selected;
	}

	public JPanel createPanel() {
		JPanel panel = super.createPanel();

		checkBox = new JCheckBox();
		checkBox.setSelected(selected);
		panel.add(checkBox);

		return panel;
	}

	public boolean isCheckBoxSet() {
		return checkBox.isSelected();
	}
	
	public JCheckBox getCheckBox() {
		return checkBox;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		checkBox.setEnabled(enabled);
	}
}