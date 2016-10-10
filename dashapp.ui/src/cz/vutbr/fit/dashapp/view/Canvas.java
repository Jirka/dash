package cz.vutbr.fit.dashapp.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.tools.canvas.AbstractCanvasTool;

/**
 * Visualization of model.
 * 
 * @author Jiri Hynek
 *
 */
public class Canvas extends JPanel implements IPropertyChangeListener, MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * Canvas tools which provides UI functionality regarding with canvas. 
	 */
	private List<AbstractCanvasTool> canvasTools;
	
	/**
	 * Active canvas tool which is used to handle UI events.
	 */
	protected AbstractCanvasTool activeCanvasTool;
	
	/**
	 * 
	 * @return active canvas tool
	 */
	public AbstractCanvasTool getActiveCanvasTool() {
		return activeCanvasTool;
	}
	
	/**
	 * Changes active canvas tool.
	 * 
	 * @param canvasTool
	 */
	public void setActiveCanvasTool(AbstractCanvasTool canvasTool) {
		this.activeCanvasTool = canvasTool;
	}
	
	/**
	 * Image representing dashboard raster.
	 */
	private BufferedImage image;

	/**
	 * Prevent image update.
	 * It can be used by some image filter which can be explicitly applied.
	 */
	private boolean externalImageUpdateLocked;
	
	/**
	 * Returns dashboard raster image.
	 * 
	 * @return image
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Updates dashboard raster image
	 * 
	 * @param image
	 */
	public void updateImage(BufferedImage image, boolean force, boolean lock) {
		if(!externalImageUpdateLocked || force) {
			this.image = image;
			repaint();
			externalImageUpdateLocked = lock;
		}
	}
	
	/**
	 * gray scale mode
	 */
	private boolean grayScale;
	
	/**
	 * Updates image according to grayScale mode
	 * if external image update is not locked.
	 */
	public void setGrayScale(boolean grayScale) {
		if(this.grayScale != grayScale) {
			this.grayScale = grayScale;
			if(!externalImageUpdateLocked && grayScaleToolEnabled) {
				Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
				if(selectedDashboard != null) {
					BufferedImage image = selectedDashboard.getImage();
					if(image != null) {
						if(grayScale) {
							convertToGrayScale(image, selectedDashboard);
						}
						updateImage(image, false, false);
					}
				}
			}
		}
	}
	
	private void convertToGrayScale(BufferedImage image, Dashboard dashboard) {
		int[][] matrix = MatrixUtils.printBufferedImage(image);
		MatrixUtils.grayScale(matrix, false, false);
		MatrixUtils.updateBufferedImage(image, matrix);
	}
	
	private boolean grayScaleToolEnabled;
	
	public void setGrayScaleToolEnabled(boolean grayScaleToolEnabled) {
		this.grayScaleToolEnabled = grayScaleToolEnabled;
	}
	
	/**
	 * Width of canvas
	 */
	public int width;
	
	@Override
	public int getWidth() {
    	return scale(width);
    }
	
	/**
	 * height of canvas
	 */
	public int height;
	
	@Override
	public int getHeight() {
    	return scale(height);
    }
	
	/**
	 * Scale rate used to zoom canvas elements. 
	 */
	protected double scaleRate;
	
	/**
	 * Updates scale rate and repaint canvas.
	 * 
	 * @param scaleRate
	 */
	public void updateScaleRate(double scaleRate) {
		this.scaleRate = scaleRate;
		setSize(scale(width), scale(height));
		setPreferredSize(new Dimension(scale(width), scale(height)));
		repaint();
	}
	
	/**
	 * Takes non-scaled value and applies scale rate.
	 * 
	 * @param value
	 * @return
	 */
	public int scale(int value) {
		return (int) (value*scaleRate);
	}
	
	/**
	 * Takes scaled value and removes scale rate.
	 * 
	 * @param value
	 * @return
	 */
	public int unscale(int value) {
		return (int) (value/scaleRate);
	}
	
	/**
	 * Size of graphical element attach constraint.
	 */
	protected int attachSize;
	
	/**
	 * Updates attach size.
	 * 
	 * @param attachSize
	 */
	public void updateAttachSize(int attachSize) {
		this.attachSize = attachSize;
	}
	
	/**
	 * Returns size of attach constraint.
	 * 
	 * @return attach size
	 */
	public int getAttachSize() {
		return attachSize;
	}
	
	/**
	 * PopUp Menu
	 */
	private JPopupMenu popUpMenu;
	
	/**
	 * 
	 * @return PopUp Menu
	 */
	public JPopupMenu getPopUpMenu() {
		return popUpMenu;
	}
	
	/**
	 * Currently selected graphical element.
	 */
	public GraphicalElement selectedElement;
	
	/**
	 * Returns currently selected element.
	 * 
	 * @return selected element
	 */
	public GraphicalElement getSelectedElement() {
		return selectedElement;
	}
	
	/**
	 * Updates selected element.
	 * 
	 * @param selectedElement
	 */
	public void setSelectedElement(GraphicalElement selectedElement) {
		this.selectedElement = selectedElement;
	}

	/**
	 * UID
	 */
	private static final long serialVersionUID = 3338976302390224035L;
	
	public Canvas(List<IGUITool> tools) {
		super(true);
		initVariables(0, 0);
		initCanvasTools(tools);
		initTools(tools);
		initPopup(tools);
		initListeners();
		setAutoscrolls(true);
	}
	
	private void initTools(List<IGUITool> tools) {
		for (IGUITool tool : tools) {
			tool.init(this);
		}
	}

	/**
	 * Initializes variables (this is done only at start of application)
	 * 
	 * @param width
	 * @param height
	 */
	private void initVariables(int width, int height) {
		resetVariables(width, height);
		this.attachSize = 0;
		this.scaleRate = 1.0;
		this.grayScaleToolEnabled = false;
	}
	
	/**
	 * Resets variables.
	 * 
	 * @param width
	 * @param height
	 * @param time
	 */
	private void resetVariables(int width, int height) {
		this.width = width;
		this.height = height;
		this.canvasTools = new ArrayList<>();
		this.externalImageUpdateLocked = false;
		//this.listOfObjects.clear();
		
		selectedElement = null;
		
		// size //
		setSize(scale(width), scale(height));
		setPreferredSize(new Dimension(scale(width), scale(height)));
	}
	
	public void resetSelections() {
		selectedElement = null;
		activeCanvasTool.resetSelections();
	}
	
	/**
	 * Initializes canvas tools which provide UI manipulation with model.
	 * 
	 * @param plugins
	 */
	private void initCanvasTools(List<IGUITool> plugins) {
		for (IGUITool canvasTool : plugins) {
			if(canvasTool instanceof AbstractCanvasTool) {
				((AbstractCanvasTool) canvasTool).init(this);
				canvasTools.add((AbstractCanvasTool) canvasTool);
			}
		}
	}
	
	private void initPopup(List<IGUITool> tools) {
		popUpMenu = new JPopupMenu();
		for (IGUITool tool : tools) {
			tool.providePopupItems(this);
		}
		//setComponentPopupMenu(popUpMenu);
	}
	
	/**
	 * Adds new PopUp item.
	 * 
	 * @param name
	 * @param action
	 * @return 
	 */
	public JMenuItem addPopUpItem(String name, AbstractAction action) {
		JMenuItem histogramMenuItem = popUpMenu.add(new JMenuItem(name));
		histogramMenuItem.addActionListener(action);
		return histogramMenuItem;
	}

	/**
	 * Initializes listeners which handles UI and model events.
	 */
	private void initListeners() {
		DashAppController.getInstance().addPropertyChangeListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}
	
	/**
	 * Opens dashboard image.
	 * 
	 * @param file
	 */
	public void openDashboardImage(Dashboard dashboard) {
		if(dashboard == null) {
			image = null;
			resetVariables(0, 0);
		} else {
			image = dashboard.getImage();
			if(grayScale && grayScaleToolEnabled) {
				convertToGrayScale(image, dashboard);
			}
			int width, height;
			
			// get serialized dashboard width and height
			if(dashboard.isSizeInitialized()) {
				width = dashboard.x + dashboard.width;
				height = dashboard.y + dashboard.height;
			} else if(image != null) {
				width = image.getWidth();
		        height = image.getHeight();
		        dashboard.setDimension(0, 0, width, height);
			} else {
				return;
			}
	        
			// initialize canvas variables
	        resetVariables(width, height);
		}
    }
	
	public void updateCursor(int cursorType) {
		if(getCursor().getType() != cursorType) {
			setCursor(Cursor.getPredefinedCursor(cursorType));
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
		Graphics2D g1 = (Graphics2D) g;
		g1.scale(scaleRate, scaleRate);
		g1.setBackground(Color.WHITE);
		g1.setColor(Color.WHITE);
		g1.fillRect(0, 0, width, height);
		if(image != null) {
			g1.drawImage(image, 0, 0, Color.white, null);
		}
		
		activeCanvasTool.paintComponent(g1);
    }

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		if(selectedDashboard == e.selectedDashboard) {
			// another dashboard is selected
			if(e.propertyKind == EventKind.DASHBOARD_SELECTION_CHANGED) {
				resetSelections();
				Dashboard dashboard = (Dashboard) e.modelChange.newValue;
				openDashboardImage(dashboard);
			} else if(EventKind.isModelChanged(e)) {
				resetSelections();
			}
			
			activeCanvasTool.firePropertyChange(e);
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		activeCanvasTool.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!hasFocus()) {
			// acquire focus
			requestFocus();
		}
		
		activeCanvasTool.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		activeCanvasTool.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		activeCanvasTool.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		activeCanvasTool.mouseExited(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		activeCanvasTool.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		activeCanvasTool.mouseMoved(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		activeCanvasTool.keyTyped(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		activeCanvasTool.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		activeCanvasTool.keyReleased(e);
	}
}
