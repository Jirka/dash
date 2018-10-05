package cz.vutbr.fit.dashapp.view.tools.segmentation;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationDebugListener;
import cz.vutbr.fit.dashapp.segmenation.methods.DashboardSegmentation;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental1;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental2;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental4;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUp;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUpRefactorized;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.util.Dialogs;
import extern.ImagePreview;
import tmp.Histogram;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationTool extends AbstractGUITool implements IGUITool {
	
	private static DashboardSegmentation dashboardSegmentation = new DashboardSegmentation();
	
	private static final ISegmentationAlgorithm[] EXPERIMENTAL_ALGORITHMS = new ISegmentationAlgorithm[] {
			new Experimental1(),
			new Experimental2(),
			new Experimental4(),
			new BottomUp(),
			new BottomUpRefactorized(),
	};
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(dashboardSegmentation.getName(), "/icons/Application form.png", new SegmentationAction(dashboardSegmentation), 0);
	}
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Segmentation");
		
		menuBar.addItem(subMenu, dashboardSegmentation.getName(), new SegmentationAction(dashboardSegmentation));
		menuBar.addSeparator(subMenu);
		
		for (ISegmentationAlgorithm algorithm : EXPERIMENTAL_ALGORITHMS) {
			menuBar.addItem(subMenu, algorithm.getName(), new SegmentationAction(algorithm));
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class SegmentationAction extends AbstractAction implements ISegmentationDebugListener {
		
		private ISegmentationAlgorithm algorithm;
		private boolean debug = false;
		
		public SegmentationAction(ISegmentationAlgorithm algorithm) {
			this.algorithm = algorithm;
			algorithm.addSegmentationDebugListener(this);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2831520241288913997L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int userAnswer = Dialogs.YesNoCancel("Would you like to enable debug mode?");
			if(userAnswer == JOptionPane.YES_OPTION || userAnswer == JOptionPane.NO_OPTION) {
				debug = userAnswer == JOptionPane.YES_OPTION;
				
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
				}return;
			}
		}

		@Override
		public void debugImage(String label, BufferedImage image, ISegmentationAlgorithm segmentationAlgorithm) {
			if(debug) {
				new ImagePreview(image, label).openWindow(800,600,0.8);
			}
		}

		@Override
		public void debugHistogram(String label, int[] histogram, ISegmentationAlgorithm segmentationAlgorithm) {
			if(debug) {
				new Histogram(label, histogram).openWindow();
			}
		}
	}

}
