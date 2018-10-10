package cz.vutbr.fit.dashapp.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SelectionDashAction extends AbstractAction {
	
	private IDashActionUI[] actionUIs;
	
	public SelectionDashAction(IDashActionUI[] actionUIs) {
		this.actionUIs = actionUIs;
	}
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8793205852749826603L;

	@Override
	public void actionPerformed(ActionEvent e) {
		IDashActionUI analysis = chooseAction();
		if(analysis != null) {
			// create analysis
			analysis.perform(e);
		}
	}
	
	protected IDashActionUI chooseAction() {
		IDashActionUI resultAnalysis = null;
		if(actionUIs.length > 0) {
			resultAnalysis = (IDashActionUI) JOptionPane.showInputDialog(null, "Choose action",
					"The Choice of an Action", JOptionPane.QUESTION_MESSAGE, null,
					actionUIs, actionUIs[0]);
		    System.out.println(resultAnalysis);
		}
		return resultAnalysis;
	}

}
