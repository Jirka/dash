package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cz.vutbr.fit.dash.model.DashAppModel;

public class ZoomAction extends AbstractAction {
	
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 8636377644255186208L;
	
	public static final int ZOOM_IN = 0;
	public static final int ZOOM_OUT = 1;
	
	private int kind;
	
	public ZoomAction(int kind) {
		this.kind = kind;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		int zoomLevel = DashAppModel.getInstance().getZoomLevel();
		
		if(kind == ZOOM_IN) {
			zoomLevel++;
		} else if(kind == ZOOM_OUT) {
			zoomLevel--;
		}
		
		DashAppModel.getInstance().setZoomLevel(zoomLevel);
	}
}