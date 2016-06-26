package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import cz.vutbr.fit.dash.gui.Surface;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class ChangeTypeAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3387699921952819493L;
	
	Surface surface;

	public ChangeTypeAction(Surface surface) {
		this.surface = surface;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JMenuItem) {
			JMenuItem menuItem = (JMenuItem) e.getSource();
			if(surface.getSelectedElement() != null) {
				surface.getSelectedElement().update(Type.values()[menuItem.getMnemonic()]);
			}
		}
	}

}
