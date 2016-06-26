package cz.vutbr.fit.dash.gui;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

public class FileInfoView implements IComponent {

	private JFormattedTextField text;

	public FileInfoView() {
		text = new JFormattedTextField();
	}
	
	@Override
	public JComponent getComponent() {
		return text;
	}

}
