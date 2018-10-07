package cz.vutbr.fit.dashapp.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class BasicDashAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4355179342520999325L;
	
	private IDashActionUI actionUI;

	public BasicDashAction(IDashActionUI actionUI) {
		this.actionUI = actionUI;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.actionUI.perform();
	}

}
