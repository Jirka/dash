package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
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
	
	protected AbstractButton btn;

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		btn = toolbar.addToggleButton("Enable attach function", "/icons/magnet_off.png",
        		new AttachAction(new ImageIcon(ViewUtils.class.getResource("/icons/magnet_on.png")),
        						new ImageIcon(ViewUtils.class.getResource("/icons/magnet_off.png")),
        						"Disable attach function",
        						"Enable attach function"), 0);
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
		private String pressedToolTip;
		private String releasedToolTip;

		public AttachAction() {
		}
		
		public AttachAction(ImageIcon pressedIcon, ImageIcon releasedIcon, String pressedToolTip, String releasedToolTip) {
			this.pressedIcon = pressedIcon;
			this.releasedIcon = releasedIcon;
			this.pressedToolTip = pressedToolTip;
			this.releasedToolTip = releasedToolTip;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JToggleButton) {
				JToggleButton toggleButton = ((JToggleButton) e.getSource());
				if(toggleButton.isSelected()) {
					if(pressedIcon != null) {
						toggleButton.setIcon(pressedIcon);
					}
					if(pressedToolTip != null) {
						toggleButton.setToolTipText(pressedToolTip);
					}
					updateCanvasAttachRate();
				} else {
					if(releasedIcon != null) {
						toggleButton.setIcon(releasedIcon);
					}
					if(releasedToolTip != null) {
						toggleButton.setToolTipText(releasedToolTip);
					}
					updateCanvasAttachRate();
				}
			}
		}
		
		private void updateCanvasAttachRate() {
			DashAppView.getInstance().getDashboardView().getCanvas().updateAttachSize(btn.isSelected() ? ATTACH_TOLERANCE : 0);
		}
	}

}
