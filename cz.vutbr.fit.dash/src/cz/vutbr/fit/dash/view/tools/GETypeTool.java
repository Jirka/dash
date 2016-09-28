package cz.vutbr.fit.dash.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;
import cz.vutbr.fit.dash.view.Canvas;

/**
 * Draw attach support. 
 * 
 * @author Jiri Hynek
 *
 */
public class GETypeTool extends AbstractGUITool implements IGUITool {
	
	@Override
	public void providePopupItems(Canvas canvas) {
		ChangeTypeAction action = new ChangeTypeAction(canvas);
		for (GEType geType : GEType.values()) {
			canvas.addPopUpItem(geType.name(), action);
		}
	}
	
	public class ChangeTypeAction extends AbstractAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3387699921952819493L;
		
		Canvas surface;

		public ChangeTypeAction(Canvas canvas) {
			this.surface = canvas;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof JMenuItem && surface.getSelectedElement() != null) {
				String itemName = ((JMenuItem) e.getSource()).getText();
				GEType geType = GEType.getValue(itemName);
				if(geType != null) {
					DashAppController.getEventManager().updateGraphicalElement(surface.getSelectedElement(),
																				GEType.getValue(itemName));
				}
			}
		}

	}

}
