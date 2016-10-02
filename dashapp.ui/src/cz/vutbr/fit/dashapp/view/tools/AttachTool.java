package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.util.ViewUtils;

/**
 * Draw attach support. 
 * 
 * @author Jiri Hynek
 *
 */
public class AttachTool extends AbstractGUITool implements IGUITool {
	
	public static final int ATTACH_TOLERANCE = 4;

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addToggleButton("View Image", "/icons/Open lock.png",
        		new AttachAction(new ImageIcon(ViewUtils.class.getResource("/icons/Lock.png")),
        						new ImageIcon(ViewUtils.class.getResource("/icons/Open lock.png"))), 0);
	}
	
	/**
	 * Attach action which handles attach update event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
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
					updateCanvasAttachRate();
				} else {
					if(releasedIcon != null) {
						toggleButton.setIcon(releasedIcon);
					}
					updateCanvasAttachRate();
				}
			}
		}
		
		private void updateCanvasAttachRate() {
			DashAppView.getInstance().getDashboardView().getCanvas().updateAttachSize(enabled ? ATTACH_TOLERANCE : 0);
		}
	}

}
