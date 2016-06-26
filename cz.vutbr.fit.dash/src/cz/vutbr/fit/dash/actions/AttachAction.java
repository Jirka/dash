package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import cz.vutbr.fit.dash.model.DashAppModel;

public class AttachAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7185658736012162711L;
	
	private ImageIcon pressedIcon;
	private ImageIcon releasedIcon;

	public AttachAction() {
	}
	
	public AttachAction(ImageIcon pressedIcon, ImageIcon releasedIcon) {
		this.pressedIcon = pressedIcon;
		this.releasedIcon = releasedIcon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JToggleButton) {
			JToggleButton toggleButton = ((JToggleButton) e.getSource());
			if(toggleButton.isSelected()) {
				if(pressedIcon != null) {
					toggleButton.setIcon(pressedIcon);
				}
				DashAppModel.getInstance().setAttachEnabled(true);
			} else {
				if(releasedIcon != null) {
					toggleButton.setIcon(releasedIcon);
				}
				DashAppModel.getInstance().setAttachEnabled(false);
			}
		}
	}
}