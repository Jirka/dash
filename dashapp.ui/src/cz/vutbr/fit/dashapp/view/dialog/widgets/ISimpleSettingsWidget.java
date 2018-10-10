package cz.vutbr.fit.dashapp.view.dialog.widgets;

import javax.swing.JPanel;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface ISimpleSettingsWidget {
	
	public JPanel createPanel();
	
	public JPanel getPanel();

	void setEnabled(boolean enabled);

}
