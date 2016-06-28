package cz.vutbr.fit.dash.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import cz.vutbr.fit.dash.Main;
import cz.vutbr.fit.dash.actions.ChangeTypeAction;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.DashAppModel.WidgetActionKind;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.DashboardFile;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;
import cz.vutbr.fit.dash.util.MatrixUtils;
import cz.vutbr.fit.dash.model.PropertyChangeEvent;
import cz.vutbr.fit.dash.model.PropertyChangeListener;

public class Surface extends JPanel implements PropertyChangeListener, MouseListener, MouseMotionListener, KeyListener {
	
	private int width;
	private int height;
	private BufferedImage image;
	private int zoomLevel;
	private WidgetActionKind widgetActionKind;
	private WorkingCopy candidateElement;
	private WorkingCopy selectedElement;
	private GraphicalElement backupElement;
	private Point previousPoint;
	private Point p;
	private JPopupMenu popUpMenu;
	public static final int BORDER_RANGE = 10;
	public static final int ATTACH_TOLERANCE = 4;
	public final static float DASH[] = { 10.0f };
	public final static Stroke areaStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, DASH, 0.0f);
	public static Color BROWN = new Color(219, 78, 205);
	public static Color LIGHT_BLUE = new Color(0, 204, 255);

	/**
	 * UID
	 */
	private static final long serialVersionUID = 3338976302390224035L;
	
	public Surface() {
		initListeners();
		initVariables(0, 0);
		setAutoscrolls(true);
		popUpMenu = new JPopupMenu();
		ChangeTypeAction typeAction = new ChangeTypeAction(this);
		for (Type type : Type.values()) {
			JMenuItem histogramMenuItem = popUpMenu.add(new JMenuItem(type.name(), type.ordinal()));
			histogramMenuItem.addActionListener(typeAction);
		}
		//setComponentPopupMenu(popUpMenu);
	}
	
	public GraphicalElement getSelectedElement() {
		return backupElement;
	}
	
	/**
	 * Initializes variables.
	 * 
	 * @param width
	 * @param height
	 * @param time
	 */
	private void initVariables(int width, int height) {
		this.width = width;
		this.height = height;
		this.widgetActionKind = DashAppModel.getInstance().getWidgetAction();
		//this.listOfObjects.clear();
		
		// size //
		setSize(scale(width),scale(height));
		setPreferredSize(new Dimension(scale(width), scale(height)));
	}

	private void initListeners() {
		DashAppModel.getInstance().addPropertyChangeListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}
	
	/**
	 * Opens image.
	 * 
	 * @param file
	 */
	public void open(Dashboard dashboard) {
		
		if(dashboard == null) {
			image = null;
			initVariables(0, 0);
		} else {
			image = dashboard.getImage();
			if(widgetActionKind != WidgetActionKind.VIEW) {
				MatrixUtils.convertBufferedImageToRGB(image, dashboard);
			}
			int width, height;
			
			// get serialized dashboard width and height
			if(dashboard.isSizeInitialized()) {
				width = dashboard.x + dashboard.width;
				height = dashboard.y + dashboard.height;
			} else if(image != null) {
				width = image.getWidth();
		        height = image.getHeight();
		        dashboard.initSize(0, 0, width, height);
			} else {
				return;
			}
	        
			// initialize surface variables
	        initVariables(width, height);
		}
    }
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public void updateImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
	
	private void updateCursor(int cursorType) {
		if(getCursor().getType() != cursorType) {
			setCursor(Cursor.getPredefinedCursor(cursorType));
		}
	}
	
	private int scale(int value) {
		return (int) (value*DashAppModel.zoomField[zoomLevel]);
	}
	
	private int unscale(int value) {
		return (int) (value/DashAppModel.zoomField[zoomLevel]);
	}
	
	private boolean isInElement(GraphicalElement element, int x, int y) {
		return (x >= element.absoluteX() && x <= element.absoluteX()+element.width
				&& y >= element.absoluteY() && y <= element.absoluteY()+element.height);
	}
	
	private boolean isInElement(WorkingCopy element, int x, int y) {
		return (x >= element.x1 && x <= element.x2 && y >= element.y1 && y <= element.y2);
	}
	
	private boolean isInRange(int value, int border, int range) {
		return value <= border+range && value >= border-range;
	}
	
	private GraphicalElement findGraphicalElement(List<GraphicalElement> elements, int x, int y) {
		GraphicalElement foundElement = null;
		for (GraphicalElement element : elements) {
			if(isInElement(element, x, y)) {
				foundElement = element;
			}
		}
		return foundElement;
	}
	
	private int getRecommendedCursorType(WorkingCopy element, int x, int y) {
		int recommendedCursorType = Cursor.DEFAULT_CURSOR;
		if(isInElement(element, x, y)) {
			if(isInRange(y, element.y1, BORDER_RANGE)) {
				if(isInRange(x, element.x2, BORDER_RANGE)) {
					recommendedCursorType = Cursor.NE_RESIZE_CURSOR;
				} else if(isInRange(x, element.x1, BORDER_RANGE)) {
					recommendedCursorType = Cursor.NW_RESIZE_CURSOR;
				} else {
					recommendedCursorType = Cursor.N_RESIZE_CURSOR;
				}
			} else if(isInRange(y, element.y2, BORDER_RANGE)) {
				if(isInRange(x, element.x2, BORDER_RANGE)) {
					recommendedCursorType = Cursor.SE_RESIZE_CURSOR;
				} else if(isInRange(x, element.x1, BORDER_RANGE)) {
					recommendedCursorType = Cursor.SW_RESIZE_CURSOR;
				} else {
					recommendedCursorType = Cursor.S_RESIZE_CURSOR;
				}
			} else if(isInRange(x, element.x2, BORDER_RANGE)) {
				recommendedCursorType = Cursor.E_RESIZE_CURSOR;
			} else if(isInRange(x, element.x1, BORDER_RANGE)) {
				recommendedCursorType = Cursor.W_RESIZE_CURSOR;
			}
		}
		return recommendedCursorType;
	}
	
	private void updatePosition(GraphicalElement parentElement, List<GraphicalElement> elements, GraphicalElement elementToIgnore, WorkingCopy selectedElement,
								int dx, int dy, Point p, int cursorType, boolean attachEnabled) {
		
		switch (cursorType) {
			case Cursor.HAND_CURSOR:
				selectedElement.x1 -= dx-p.x;
				selectedElement.y1 -= dy-p.y;
				selectedElement.x2 -= dx-p.x;
				selectedElement.y2 -= dy-p.y;
				if(attachEnabled) {
					WrappedBoolean changeMade = new WrappedBoolean(false);
					int p1 = selectedElement.x1-getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, changeMade);
					if(changeMade.value) {
						changeMade.value = false;
						p.x = selectedElement.x2-getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, changeMade);
						if(changeMade.value) {
							p.x = Math.min(p1, p.x);
						} else {
							p.x = p1;
						}
					} else {
						p.x = selectedElement.x2-getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null);
					}
					selectedElement.x1 -= p.x;
					selectedElement.x2 -= p.x;
					changeMade.value = false;
					p1 = selectedElement.y1-getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, changeMade);
					if(changeMade.value) {
						changeMade.value = false;
						p.y = selectedElement.y2-getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, changeMade);
						if(changeMade.value) {
							p.y = Math.min(p1, p.y);
						} else {
							p.y = p1;
						}
					} else {
						p.y = selectedElement.y2-getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null);
					}
					selectedElement.y1 -= p.y;
					selectedElement.y2 -= p.y;
				}
				break;
			case Cursor.N_RESIZE_CURSOR:
				selectedElement.y1 -= dy-p.y;
				if(attachEnabled) {
					p.y = selectedElement.y1-getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null);
					selectedElement.y1 -= p.y;
				}
				break;
			case Cursor.NE_RESIZE_CURSOR:
				selectedElement.x2 -= dx-p.x;
				selectedElement.y1 -= dy-p.y;
				if(attachEnabled) {
					p.x = selectedElement.x2-getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null);
					p.y = selectedElement.y1-getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null);
					selectedElement.x2 -= p.x;
					selectedElement.y1 -= p.y;
				}
				break;
			case Cursor.NW_RESIZE_CURSOR:
				selectedElement.x1 -= dx-p.x;
				selectedElement.y1 -= dy-p.y;
				if(attachEnabled) {
					p.x = selectedElement.x1-getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null);
					p.y = selectedElement.y1-getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null);
					selectedElement.x1 -= p.x;
					selectedElement.y1 -= p.y;
				}
				break;
			case Cursor.S_RESIZE_CURSOR:
				selectedElement.y2 -= dy-p.y;
				if(attachEnabled) {
					p.y = selectedElement.y2-getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null);
					selectedElement.y2 -= p.y;
				}
				break;
			case Cursor.SE_RESIZE_CURSOR:
				selectedElement.x2 -= dx-p.x;
				selectedElement.y2 -= dy-p.y;
				if(attachEnabled) {
					p.x = selectedElement.x2-getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null);
					p.y = selectedElement.y2-getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null);
					selectedElement.x2 -= p.x;
					selectedElement.y2 -= p.y;
				}
				break;
			case Cursor.SW_RESIZE_CURSOR:
				selectedElement.x1 -= dx-p.x;
				selectedElement.y2 -= dy-p.y;
				if(attachEnabled) {
					p.x = selectedElement.x1-getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null);
					p.y = selectedElement.y2-getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null);
					selectedElement.x1 -= p.x;
					selectedElement.y2 -= p.y;
				}
				break;
			case Cursor.W_RESIZE_CURSOR:
				selectedElement.x1 -= dx-p.x;
				if(attachEnabled) {
					p.x = selectedElement.x1-getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null);
					selectedElement.x1 -= p.x;
				}
				break;
			case Cursor.E_RESIZE_CURSOR:
				selectedElement.x2 -= dx-p.x;
				if(attachEnabled) {
					p.x = selectedElement.x2-getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null);
					selectedElement.x2 -= p.x;
				}
				break;
		}
	}
	
	private int getPreferredX(GraphicalElement parentElement, List<GraphicalElement> elements, int x, GraphicalElement elementToIgnore, WrappedBoolean changeMade) {
		int result = x;
		int dx = ATTACH_TOLERANCE+1;
		for (GraphicalElement element : elements) {
			if(element != elementToIgnore) {
				if(isInRange(x, element.absoluteX(), ATTACH_TOLERANCE)) {
					int d = Math.abs(x-element.absoluteX());
					if(d < dx) {
						dx = d;
						result = element.absoluteX();
						if(changeMade != null) changeMade.value = true;
					}
				}
				if(isInRange(x, element.absoluteX()+element.width, ATTACH_TOLERANCE)) {
					int d = Math.abs(x-(element.absoluteX()+element.width));
					if(d < dx) {
						dx = d;
						result = element.absoluteX()+element.width;
						if(changeMade != null) changeMade.value = true;
					}
				}
			}
		}
		if(result < parentElement.absoluteX()) {
			result = parentElement.absoluteX();
		}
		if(result > parentElement.absoluteX()+parentElement.width) {
			result = parentElement.absoluteX()+width;
		}
		return result;
	}
	
	private int getPreferredY(GraphicalElement parentElement, List<GraphicalElement> elements, int y, GraphicalElement elementToIgnore, WrappedBoolean changeMade) {
		int result = y;
		int dy = ATTACH_TOLERANCE+1;
		for (GraphicalElement element : elements) {
			if(element != elementToIgnore) {
				if(isInRange(y, element.absoluteY(), ATTACH_TOLERANCE)) {
					int d = Math.abs(y-element.absoluteY());
					if(d < dy) {
						dy = d;
						result = element.absoluteY();
						if(changeMade != null) changeMade.value = true;
					}
				}
				if(isInRange(y, element.absoluteY()+element.height, ATTACH_TOLERANCE)) {
					int d = Math.abs(y-(element.absoluteY()+element.height));
					if(d < dy) {
						dy = d;
						result = element.absoluteY()+element.height;
						if(changeMade != null) changeMade.value = true;
					}
				}
			}
		}
		if(result < parentElement.absoluteY()) {
			result = parentElement.absoluteY();
		}
		if(result > parentElement.absoluteY()+parentElement.height) {
			result = parentElement.absoluteY()+height;
		}
		return result;
	}
	
	private void resetSelections(WidgetActionKind widgetActionKind) {
		if(widgetActionKind == WidgetActionKind.BOUND) {
			backupElement = DashAppModel.getInstance().getSelectedDashboard();
			if(backupElement != null) {
				selectedElement = new WorkingCopy(backupElement.absoluteX(), backupElement.absoluteY(),
						backupElement.absoluteX()+backupElement.width, backupElement.absoluteY()+backupElement.height);
			}
		} else {
			selectedElement = null;
			backupElement = null;
			candidateElement = null;
		}
	}
	
	@Override
	public int getWidth() {
    	return scale(width);
    }

	@Override
	public int getHeight() {
    	return scale(height);
    }
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
		Graphics2D g1 = (Graphics2D) g;
		g1.scale(DashAppModel.zoomField[zoomLevel], DashAppModel.zoomField[zoomLevel]);
		g1.setBackground(Color.WHITE);
		g1.setColor(Color.WHITE);
		g1.fillRect(0, 0, width, height);
		if(image != null) {
			g1.drawImage(image, 0, 0, Color.white, null);
		}
		// graphical elements
		Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
		if(dashboard != null && widgetActionKind != WidgetActionKind.VIEW) {
			if(widgetActionKind != WidgetActionKind.BOUND) {
				// graphical elements
				List<GraphicalElement> elements = dashboard.getGraphicalElements(Type.ALL_TYPES);
				g1.setComposite(AlphaComposite.SrcOver.derive(0.4f));
				for (GraphicalElement element : elements) {
					if(element != backupElement) {
						Color widgetColor = getWidgetColor(element);
						g1.setColor(widgetColor);
						//g1.setStroke(new BasicStroke(2));
						g1.setBackground(widgetColor);
						g1.fillRect(element.absoluteX(), element.absoluteY(), element.width, element.height);
					}
				}
				// candidate element
				if(candidateElement != null) {
					g1.setColor(Color.RED);
					g1.setBackground(Color.RED);
					g1.fillRect(candidateElement.x(), candidateElement.y(), candidateElement.width(), candidateElement.height());
				}
			}
			// selected element
			if(selectedElement != null) {
				if(backupElement instanceof Dashboard) {
					g1.setColor(Color.LIGHT_GRAY);
					g1.setComposite(AlphaComposite.SrcOver.derive(0.70f));
					g1.fillRect(0, 0, width, selectedElement.y());
					g1.fillRect(0, selectedElement.y(), selectedElement.x(), selectedElement.height());
					g1.fillRect(selectedElement.x2(), selectedElement.y(), width-selectedElement.x2(), selectedElement.height());
					g1.fillRect(0, selectedElement.y2(), width, height-selectedElement.y2());
					g1.setColor(Color.YELLOW);
					g1.setStroke(areaStroke);
					g1.setComposite(AlphaComposite.SrcOver.derive(0.40f));
					g1.drawRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
					g1.fillRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
				} else {
					g1.setColor(Color.YELLOW);
					g1.fillRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
					g1.setColor(Color.ORANGE);
					g1.setComposite(AlphaComposite.SrcOver.derive(1f));
					g1.setStroke(new BasicStroke(2));
					g1.drawRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
				}
			}
			
			if(selectedElement == null || !(backupElement instanceof Dashboard)) {
				// dashboard area
				g1.setColor(Color.LIGHT_GRAY);
				g1.setComposite(AlphaComposite.SrcOver.derive(0.70f));
				g1.fillRect(0, 0, width, dashboard.y);
				g1.fillRect(0, dashboard.y, dashboard.x, dashboard.height);
				g1.fillRect(dashboard.x+dashboard.width, dashboard.y, width-dashboard.x-dashboard.width, dashboard.height);
				g1.fillRect(0, dashboard.y+dashboard.height, width, height-dashboard.y-dashboard.height);
				g1.setColor(Color.BLACK);
				g1.setStroke(areaStroke);
				g1.drawRect(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
			}
		}
    }

	private Color getWidgetColor(GraphicalElement graphicalElement) {
		Color color;
		switch (graphicalElement.type) {
			case BUTTON:
			case TOOLBAR:
				color = BROWN;
				break;
			case HEADER:
				color = Color.BLUE;
				break;
			case LABEL:
				color = LIGHT_BLUE;
				break;
			case DECORATION:
				color = Color.RED;
				break;
			default:
				color = Color.GREEN;
				break;
			}
		return color;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		switch (e.propertyKind) {
			case DASHBOARD_SELECTION:
				Dashboard dashboard = (Dashboard) e.newValue;
				open(dashboard);
				//break;
			case DASHBOARD_ELEMENTS:
			//case GRAPHICAL_ELEMENT:
				resetSelections(widgetActionKind);
				repaint();
				break;
			case ZOOM_LEVEL:
				this.zoomLevel = (Integer) e.newValue;
				setSize(scale(width), scale(height));
				setPreferredSize(new Dimension(scale(width), scale(height)));
				repaint();
				break;
			case WIDGET_ACTION:
				widgetActionKind = (WidgetActionKind) e.newValue;
				resetSelections(widgetActionKind);
				if(widgetActionKind == WidgetActionKind.INSERT || widgetActionKind == WidgetActionKind.WAND) {
					updateCursor(Cursor.CROSSHAIR_CURSOR);
				} else {
					updateCursor(Cursor.DEFAULT_CURSOR);
				}
				repaint();
				break;
			}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(widgetActionKind == WidgetActionKind.SELECT || widgetActionKind == WidgetActionKind.BOUND) {
			if(e.getButton() == MouseEvent.BUTTON3) {
				popUpMenu.show(this, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!hasFocus()) {
			// acquire focus
			requestFocus();
		}
		
		if(DashAppModel.getInstance().getSelectedDashboard() != null) {
			if(widgetActionKind == WidgetActionKind.INSERT || widgetActionKind == WidgetActionKind.WAND) {
				// create candidate element at selected position
				Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
				List<GraphicalElement> elements = dashboard.getGraphicalElements(Type.ALL_TYPES);
				candidateElement = new WorkingCopy(getPreferredX(dashboard, elements, unscale(e.getX()), null, null),
													getPreferredY(dashboard, elements, unscale(e.getY()), null, null));
			} else if(widgetActionKind == WidgetActionKind.SELECT || widgetActionKind == WidgetActionKind.BOUND
					|| widgetActionKind == WidgetActionKind.VIEW) {
				// store actual position 
				int x = unscale(e.getX());
				int y = unscale(e.getY());
				// try to locate existing element
				if(widgetActionKind == WidgetActionKind.SELECT) {
					backupElement = findGraphicalElement(DashAppModel.getInstance().getSelectedDashboard().getGraphicalElements(Type.ALL_TYPES), x, y);
				}/* else {
					Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
					if(isInElement(dashboard, x, y)) {
						backupElement = dashboard;
					} else {
						backupElement = null;
					}
				}*/
				if(backupElement != null) {
					// select found element
					selectedElement = new WorkingCopy(backupElement.absoluteX(), backupElement.absoluteY(),
														backupElement.absoluteX()+backupElement.width,
														backupElement.absoluteY()+backupElement.height);
					// change cursor (if move action is not offered right now)
					if(getCursor().getType() == Cursor.DEFAULT_CURSOR) {
						updateCursor(Cursor.HAND_CURSOR);
					}
					// initialize help point used by attaching to borders
					p = new Point(0, 0);
				} else {
					// cancel element selection
					selectedElement = null;
				}
				if(selectedElement != null) {
					previousPoint = new Point(x, y);
				} else {
					previousPoint = new Point(e.getX(), e.getY());
				}
				repaint();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(widgetActionKind == WidgetActionKind.INSERT) {
			if(candidateElement != null && candidateElement.x2 != -1 && candidateElement.y2 != -1) {
				// create new graphical element
				DashAppModel.getInstance().getSelectedDashboard().createGrapicalElement(
						candidateElement.x(), candidateElement.y(), 
						candidateElement.width(), candidateElement.height(), true);
				// release candidate element
				candidateElement = null;
				// draw as a basic graphical element (not as a candidate element)
				repaint();
			} else {
				// no need to repaint if candidate position of element has not been initialized
				candidateElement = null;
			}
		} else if(widgetActionKind == WidgetActionKind.WAND) {
			// TODO
			Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
			BufferedImage image = DashAppModel.getInstance().getSelectedDashboard().getImage();
			int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
			MatrixUtils.grayScale(matrix, true, false);
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
			DashAppModel.getInstance().getSelectedDashboard().createGrapicalElement(
					candidateElement.x(), candidateElement.y(), 
					candidateElement.width(), candidateElement.height(), true);
			// release candidate element
			candidateElement = null;
		} else if(widgetActionKind == WidgetActionKind.SELECT || widgetActionKind == WidgetActionKind.BOUND) {
			if(selectedElement != null) {
				// selected element could have been moved or resized 
				backupElement.update(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height(), true);
				// element is still selected but no operation is active
				updateCursor(Cursor.DEFAULT_CURSOR);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(widgetActionKind == WidgetActionKind.INSERT) {
			if(candidateElement != null) {
				// calculate and update candidate element size (consider border of existing elements)
				Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
				List<GraphicalElement> elements = dashboard.getGraphicalElements(Type.ALL_TYPES);
				if(DashAppModel.getInstance().isAttachEnabled()) {
					candidateElement.x2 = getPreferredX(dashboard, elements, unscale(e.getX()), null, null);
					candidateElement.y2 = getPreferredY(dashboard, elements, unscale(e.getY()), null, null);
				} else {
					candidateElement.x2 = unscale(e.getX());
					candidateElement.y2 = unscale(e.getY());
				}
				repaint();
			}
		} else if(widgetActionKind == WidgetActionKind.SELECT || widgetActionKind == WidgetActionKind.BOUND
				|| widgetActionKind == WidgetActionKind.VIEW) {
			if(selectedElement != null) {
				int x = unscale(e.getX());
				int y = unscale(e.getY());
				int dx = previousPoint.x-x;
				int dy = previousPoint.y-y;
				// calculate change of mouse position
				if(dx != 0 || dy != 0) {
					// calculate and update selected element position (move and resize action) and size (resize action)
					Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
					updatePosition(dashboard, dashboard.getGraphicalElements(Type.ALL_TYPES), backupElement,
									selectedElement, dx, dy, p, getCursor().getType(), DashAppModel.getInstance().isAttachEnabled());
					// save actual position that can be used in next possible drag event 
					previousPoint.x = x;
					previousPoint.y = y;
					repaint();
				}
			} else if(previousPoint != null) {
				JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
				if(viewPort != null) {
					int x = e.getX();
					int y = e.getY();
					int dx = previousPoint.x-x;
					int dy = previousPoint.y-y;
					Rectangle view = viewPort.getViewRect();
                    view.x += dx;
                    view.y += dy;
					if(dx != 0 || dy != 0) {
						scrollRectToVisible(view);
						//DashAppGUI.getDashboardView().move(dx, dy);
						repaint();
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(widgetActionKind == WidgetActionKind.SELECT || widgetActionKind == WidgetActionKind.BOUND) {
			if(selectedElement != null) {
				// possible mouse hover over border of selected element
				int x = unscale(e.getX());
				int y = unscale(e.getY());
				int recommendedCursorType = getRecommendedCursorType(selectedElement, x, y);
				// offer move action to user if this action is available
				updateCursor(recommendedCursorType);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(candidateElement != null) {
				candidateElement = null;
				repaint();
			}
			if(selectedElement != null) {
				if(getCursor().getType() != Cursor.DEFAULT_CURSOR) {
					selectedElement.restore(backupElement.absoluteX(), backupElement.absoluteY(),
											backupElement.absoluteX()+backupElement.width,
											backupElement.absoluteY()+backupElement.height);
					updateCursor(Cursor.DEFAULT_CURSOR);
				} else {
					if(widgetActionKind != WidgetActionKind.BOUND) {
						selectedElement = null;
						backupElement = null;
					}
				}
				repaint();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(selectedElement != null && widgetActionKind != WidgetActionKind.BOUND) {
				if(getCursor().getType() == Cursor.DEFAULT_CURSOR) {
					DashAppModel.getInstance().getSelectedDashboard().deleteGrapicalElement(backupElement);
					selectedElement = null;
					backupElement = null;
					repaint();
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private static class WorkingCopy {
		public int x1 = -1;
		public int y1 = -1;
		public int x2 = -1;
		public int y2 = -1;
		
		public WorkingCopy(int x1, int y1) {
			this.x1 = x1;
			this.y1 = y1;
		}

		public WorkingCopy(int x1, int y1, int x2, int y2) {
			this(x1, y1);
			this.x2 = x2;
			this.y2 = y2;
		}
		
		public void restore(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		public int x() {
			return Math.min(x1, x2);
		}
		
		public int y() {
			return Math.min(y1, y2);
		}
		
		public int x2() {
			return Math.max(x1, x2);
		}
		
		public int y2() {
			return Math.max(y1, y2);
		}
		
		public int width() {
			return Math.abs(x2-x1);
		}
		
		public int height() {
			return Math.abs(y2-y1);
		}
	}
	
	private class WrappedBoolean {
		
		public boolean value;
		
		public WrappedBoolean(boolean value) {
			this.value = value;
		}
	}
}
