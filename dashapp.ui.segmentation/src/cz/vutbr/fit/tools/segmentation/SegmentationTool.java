package cz.vutbr.fit.tools.segmentation;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.SegmentationAlgorithm2;
import cz.vutbr.fit.dashapp.segmenation.XYCut;
import cz.vutbr.fit.dashapp.segmenation.XYCut2;
import cz.vutbr.fit.dashapp.segmenation.XYCut4;
import cz.vutbr.fit.dashapp.segmenation.XYCutFinal;
import cz.vutbr.fit.dashapp.segmenation.BottomUp;
import cz.vutbr.fit.dashapp.segmenation.BottomUpRefactorized;
import cz.vutbr.fit.dashapp.segmenation.SegmentationAlgorithm1;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationTool extends AbstractGUITool implements IGUITool {
	
	private static XYCut xyCutAlg;
	private static XYCut2 xyCutAlg2;
	private static XYCut4 xyCutAlg4;
	private static XYCutFinal xyCutAlgFinal;
	private static BottomUp _BottomUp;
	private static BottomUpRefactorized _BottomUpRefactorized;
	
	private static final ISegmentationAlgorithm[] ALGORITHMS = new ISegmentationAlgorithm[] {
			new SegmentationAlgorithm1(),
			new SegmentationAlgorithm2(),
			xyCutAlg = new XYCut(),
			xyCutAlg2 = new XYCut2(),
			xyCutAlg4 = new XYCut4(),
			xyCutAlgFinal = new XYCutFinal(),
			_BottomUp = new BottomUp(),
			_BottomUpRefactorized = new BottomUpRefactorized(),
	};
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("XY-cut", "/icons/Application form.png", new SegmentationAction(xyCutAlg), 0);
		toolbar.addButton("XY-cut 2", "/icons/Application form.png", new SegmentationAction(xyCutAlg2), 0);
		toolbar.addButton("XY-cut 4", "/icons/Application form.png", new SegmentationAction(xyCutAlg4), 0);
		toolbar.addButton("XY-cut final", "/icons/Application form.png", new SegmentationAction(xyCutAlgFinal), 0);
		toolbar.addSeparator();
		toolbar.addButton("BottomUp", "/icons/Application form.png", new SegmentationAction(_BottomUp), 0);
		toolbar.addButton("BottomUpRefactorized", "/icons/Application form.png", new SegmentationAction(_BottomUpRefactorized), 0);
	}
	
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
