package cz.vutbr.fit.view.tools.image;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.view.util.Histogram;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HistogramTool extends AbstractGUITool implements IGUITool {
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Image");
		menuBar.addItem(subMenu, "Histogram", new HistogramAction());
	}
	
	public class HistogramAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2831520241288913997L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Dashboard selectedDashboard = DashAppUtils.getSelectedDashboard();
			if(selectedDashboard != null) {
				Canvas canvas = DashAppView.getInstance().getDashboardView().getCanvas();
				BufferedImage image = canvas.getImage();
				if(image != null) {
					int[][] matrix = ColorMatrix.printImageToMatrix(image, selectedDashboard);
					ColorMatrix.toGrayScale(matrix, true, false);
					int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
					new Histogram(histogram).openWindow();
				}
			}
		}
	}

}
