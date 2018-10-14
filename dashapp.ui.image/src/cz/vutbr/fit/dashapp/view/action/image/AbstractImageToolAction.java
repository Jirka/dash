package cz.vutbr.fit.dashapp.view.action.image;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.DashAppView;

/**
 * Abstract image tool action.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractImageToolAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6415630569483436850L;
	
	protected Canvas canvas;
	protected DashboardFile df;
	protected BufferedImage image;
	protected Dashboard dashboard;

	@Override
	public void actionPerformed(ActionEvent e) {
		canvas = DashAppView.getInstance().getDashboardView().getCanvas();
		df = DashAppUtils.getSelectedDashboardFile();
		
		if(df != null) {
			dashboard = df.getDashboard(false);
			image = df.getImage();
			BufferedImage canvasImage = canvas.getImage(); // actual image can be different from image stored in file
			if(canvasImage != null) {
				int[][] matrix = ColorMatrix.printImageToMatrix(canvasImage, dashboard);
				canvasImage = processImage(matrix);
				if(canvasImage != null) {
					canvas.updateImage(canvasImage, true, true);
				}
			}
		}
	}
	
	protected String askForString(String message, String title, String defaultValue) {
		Object input = JOptionPane.showInputDialog(null, 
		        message, 
		        title, 
		        JOptionPane.QUESTION_MESSAGE, null, null, defaultValue
		    );
		if(input == null) {
			// TODO cancel action
			input = defaultValue;
		}
		return input.toString();
	}
	
	protected double askForDouble(String message, String title, double defaultValue) {
		String input = askForString(message, title, Double.toString(defaultValue));
		double result = defaultValue;
		if(input != null) {
			try {
				result = Integer.parseInt(input.toString());
			} catch(NumberFormatException e) {
				// default value
			}
		}
		return result;
	}
	
	protected int askForInteger(String message, String title, int defaultValue) {
		String input = askForString(message, title, Integer.toString(defaultValue));
		int result = defaultValue;
		if(input != null) {
			try {
				result = Integer.parseInt(input.toString());
			} catch(NumberFormatException e) {
				// default value
			}
		}
		//return range >= 0 ? range : defaultValue;
		return result;
	}
	
	public abstract String getName();

	protected abstract BufferedImage processImage(int[][] matrix);
	
}