package cz.vutbr.fit.tools.segmentation;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.SegmentationAlgorithm2;
import cz.vutbr.fit.dashapp.segmenation.SegmentationAlgorithm1;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationTool extends AbstractGUITool implements IGUITool {
	
	private static final ISegmentationAlgorithm[] ALGORITHMS = new ISegmentationAlgorithm[] {
			new SegmentationAlgorithm1(),
			new SegmentationAlgorithm2()
	};
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Segmentation");
		
		for (ISegmentationAlgorithm algorithm : ALGORITHMS) {
			menuBar.addItem(subMenu, algorithm.getName(), new SegmentationAction(algorithm));
		}
	}
	
	public class SegmentationAction extends AbstractAction {
		
		private ISegmentationAlgorithm algorithm;
		
		public SegmentationAction(ISegmentationAlgorithm algorithm) {
			this.algorithm = algorithm;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2831520241288913997L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Dashboard selectedDashboard = DashAppUtils.getSelectedDashboard();
			if(selectedDashboard != null) {
				// get image from file (not from canvas)
				BufferedImage image = selectedDashboard.getDashboardFile().getImage();;
				// get actual image from canvas (working copy)
				// BufferedImage image = DashAppView.getInstance().getDashboardView().getCanvas().getImage();
				if(image != null) {
					Dashboard newDashboard = algorithm.processImage(image);
					if(newDashboard != null) {
						// update dashboard
						DashAppController.getEventManager().updateDashboard(newDashboard);
					}
					// repaint canvas
					DashAppView.getInstance().getDashboardView().getCanvas().repaint();
				}
			}
		}
	}

}
