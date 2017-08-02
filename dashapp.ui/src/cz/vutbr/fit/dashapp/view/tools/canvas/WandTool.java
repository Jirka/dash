package cz.vutbr.fit.dashapp.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils.WorkingCopy;

/**
 * Canvas tool which provides support for selection area with the uniform color
 * (experimental tool).
 * 
 * @author Jiri Hynek
 *
 */
public class WandTool extends AbstractCanvasTool {
	
	public WandTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}

	/**
	 * Working copy of graphical element which is currently used.
	 */
	private WorkingCopy candidateElement;

	@Override
	protected String getLabel() {
		return "Magic Wand";
	}
	
	@Override
	protected String getTooltip() {
		return "Select area with the same color";
	}

	@Override
	protected String getImage() {
		return "/icons/Edit.png";
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		canvas.setGrayScale(false);
		canvas.updateCursor(Cursor.CROSSHAIR_CURSOR);
		canvas.repaint();
	}
	
	@Override
	public void resetSelections() {
		canvas.setSelectedElement(null);
		candidateElement = null;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// create candidate element at selected position
		int x = canvas.unscale(e.getX());
		int y = canvas.unscale(e.getY());
		// attach enabled
		int attachSize = canvas.getAttachSize();
		if(canvas.getAttachSize() > 0) {
			Dashboard dashboard = canvas.getDashboard();
			List<GraphicalElement> elements = dashboard.getChildren(GEType.ALL_TYPES);
			x = CanvasUtils.getPreferredX(dashboard, elements, x, null, null, attachSize, canvas.width);
			y = CanvasUtils.getPreferredY(dashboard, elements, y, null, null, attachSize, canvas.height);
		}
		candidateElement = new WorkingCopy(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO
		Dashboard dashboard = canvas.getDashboard();
		BufferedImage image = canvas.getDashboardFile().getImage();
		int[][] matrix = ColorMatrix.printImageToMatrix(image, dashboard);
		ColorMatrix.toGrayScale(matrix, true, false);
		int refValue = matrix[candidateElement.x1][candidateElement.y1];
		matrix[candidateElement.x1][candidateElement.y1] = -1;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] == -1) {
					for (int a = i-1; a < i+2; a++) {
						for (int b = j-1; b < j+2; b++) {
							if(a >= 0 && a < matrix.length && b >= 0 && b < matrix[i].length) {
								if(matrix[a][b] == refValue) {
									matrix[a][b] = -1;
								}
							}
						}
					}
					matrix[i][j] = -2;
				}
			}
		}
		int x1 = matrix.length, x2 = 0, y1 = matrix[0].length, y2 = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] == -2) {
					if(i < x1) {
						x1 = i;
					}
					if(i > x2) {
						x2 = i;
					}
					if(j < y1) {
						y1 = j;
					}
					if(j > y2) {
						y2 = j;
					}
				}
			}
		}
		candidateElement.x1 = x1;
		candidateElement.x2 = x2;
		candidateElement.y1 = y1;
		candidateElement.y2 = y2;
		DashAppController.getEventManager().createGrapicalElement(canvas.getDashboard(),
				candidateElement.x(), candidateElement.y(), 
				candidateElement.width(), candidateElement.height(), true);
		// release candidate element
		candidateElement = null;
	}

	@Override
	public void paintComponent(Graphics2D g) {
		Dashboard dashboard = canvas.getDashboard();
		
		// graphical elements
		paintUtil.paintGraphicalElements(g, dashboard, canvas.selectedElement);
		
		// candidate element
		if(candidateElement != null) {
			paintUtil.paintCandidateElement(g, candidateElement);
		}
		
		// dashboard area
		paintUtil.paintDashboardArea(g, dashboard);
	}

}
