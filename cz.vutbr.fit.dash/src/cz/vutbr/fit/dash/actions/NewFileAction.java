package cz.vutbr.fit.dash.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import cz.vutbr.fit.dash.model.DashAppModel;

public class NewFileAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7806859795031384430L;

	public NewFileAction() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// open new file dialog
		JTextField nameInput = new JTextField("new_file", 20);
		JSpinner spinnerWidth = new JSpinner();
		spinnerWidth.setValue(640);
		((JSpinner.DefaultEditor) spinnerWidth.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		spinnerWidth.setPreferredSize(new Dimension(60, 20));
		JSpinner spinnerHeight = new JSpinner();
		((JSpinner.DefaultEditor) spinnerHeight.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		spinnerHeight.setValue(480);
		spinnerHeight.setPreferredSize(new Dimension(60, 20));
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Name"),
				nameInput,
				new JLabel("Width"),
				spinnerWidth,
				new JLabel("Height"),
				spinnerHeight
		};
		if(JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
				== JOptionPane.OK_OPTION) {
			
			try {
				int width = new Integer(spinnerWidth.getValue().toString());
				int height = new Integer(spinnerHeight.getValue().toString());
				if(DashAppModel.getInstance().createEmptyDashboard(width, height, nameInput.getText())) {
					//TODO report problem;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}