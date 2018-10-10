package cz.vutbr.fit.dashapp.view.dialog.widgets;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleTextField extends SimpleLabelWidget implements ISimpleSettingsWidget {

	protected JTextField textField;
	protected String preferredText;

	public SimpleTextField(String label, String preferredText) {
		super(label);
		this.preferredText = preferredText;
	}

	public JPanel createPanel() {
		JPanel panel = super.createPanel();

		// result text field
		textField = new JTextField("", 30);
		if (preferredText != null) {
			textField.setText(preferredText);
		}
		panel.add(textField);

		return panel;
	}

	public String getResultText() {
		return textField.getText();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		textField.setEnabled(enabled);
	}
}