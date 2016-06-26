package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.DashAppModel.WidgetActionKind;

public class WidgetAction extends AbstractAction {
	
	private WidgetActionKind kind;
	
	public WidgetAction(WidgetActionKind kind) {
		this.kind = kind;
	}

	/**
	 * UID 
	 */
	private static final long serialVersionUID = 5963443114469200675L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageAction imageAction;
			DashAppModel.getInstance().setWidgetAction(kind);
			if(kind == WidgetActionKind.VIEW) {
				imageAction = new ImageAction(ImageAction.RESET);
			} else {
				imageAction = new ImageAction(ImageAction.GRAY_SCALE);
			}
			imageAction.actionPerformed(null);
		}
	}