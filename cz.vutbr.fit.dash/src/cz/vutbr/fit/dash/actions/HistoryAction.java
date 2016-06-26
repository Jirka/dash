package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cz.vutbr.fit.dash.gui.DashAppGUI;

public class HistoryAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6014412330042783023L;
	
	public static final int UNDO = 0;
	public static final int REDO = 1;
	
	private int kind;
	
	public HistoryAction(int kind) {
		this.kind = kind;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch (kind) {
			case UNDO:
				DashAppGUI.getInstance().getXMLView().undo();
				break;
			case REDO:
				DashAppGUI.getInstance().getXMLView().redo();
				break;
		}
	}
}