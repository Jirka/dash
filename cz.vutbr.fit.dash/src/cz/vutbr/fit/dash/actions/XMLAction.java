package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import cz.vutbr.fit.dash.gui.Dialogs;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;

public class XMLAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int REFRESH = 0;
	public static final int SAVE = 1;
	public static final int SAVE_ALL = 2;
	
	private int kind;
	
	public XMLAction(int kind) {
		this.kind = kind;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		if(selectedDashboard != null) {
			switch (kind) {
			case SAVE:
				try {
					selectedDashboard.saveToFile();
				} catch (IOException e1) {
					Dialogs.report("Unable to save dashboard file.");
				}
				break;
			case REFRESH:
					try {
						selectedDashboard.reloadFromFile();
					} catch (Exception e1) {
						Dialogs.report("Unable to load dashboard file.");
					}
				break;
		}
		}
	}
}