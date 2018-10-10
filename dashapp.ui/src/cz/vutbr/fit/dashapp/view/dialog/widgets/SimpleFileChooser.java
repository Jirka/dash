package cz.vutbr.fit.dashapp.view.dialog.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleFileChooser extends SimpleTextField implements ISimpleSettingsWidget {

	protected JButton openButton;
	protected String preferredText;
	protected int selectionMode;
	protected FileNameExtensionFilter filter;

	public SimpleFileChooser(String label, String preferredText, int selectionMode, FileNameExtensionFilter filter) {
		super(label, preferredText);
		this.selectionMode = selectionMode;
		this.filter = filter;
	}

	public JPanel createPanel() {
		JPanel panel = super.createPanel();

		// open button
		openButton = new JButton("...");
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(selectionMode);
				if (filter != null) {
					fc.setFileFilter(filter);
				}
				fc.showOpenDialog(null);
				try {
					File selectedFile = fc.getSelectedFile();
					if (selectedFile != null) {
						textField.setText(selectedFile.getAbsolutePath());
					}
				} catch (Exception ex) {
					// do nothing
				}
			}
		});
		panel.add(openButton);

		return panel;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		openButton.setEnabled(enabled);
	}
}