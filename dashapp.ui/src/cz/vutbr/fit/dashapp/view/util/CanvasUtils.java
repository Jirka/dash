package cz.vutbr.fit.dashapp.view.util;

import java.awt.Cursor;
import java.awt.Point;
import java.util.List;

import cz.vutbr.fit.dashapp.model.GraphicalElement;

public class CanvasUtils {
	
	public static final int BORDER_RANGE = 10;

	public static boolean isInElement(GraphicalElement element, int x, int y) {
		return (x >= element.absoluteX() && x <= element.absoluteX() + element.width && y >= element.absoluteY()
				&& y <= element.absoluteY() + element.height);
	}

	public static boolean isInElement(WorkingCopy element, int x, int y) {
		return (x >= element.x1 && x <= element.x2 && y >= element.y1 && y <= element.y2);
	}

	public static boolean isInRange(int value, int border, int range) {
		return value <= border + range && value >= border - range;
	}

	public static GraphicalElement findGraphicalElement(List<GraphicalElement> elements, int x, int y) {
		GraphicalElement foundElement = null;
		for (GraphicalElement element : elements) {
			if (isInElement(element, x, y)) {
				foundElement = element;
			}
		}
		return foundElement;
	}

	public static int getPreferredX(GraphicalElement parentElement, List<GraphicalElement> elements, int x,
			GraphicalElement elementToIgnore, WrappedBoolean changeMade, int attachTolerance, int surfaceWidth) {
		int result = x;
		int dx = attachTolerance + 1;
		for (GraphicalElement element : elements) {
			if (element != elementToIgnore) {
				if (isInRange(x, element.absoluteX(), attachTolerance)) {
					int d = Math.abs(x - element.absoluteX());
					if (d < dx) {
						dx = d;
						result = element.absoluteX();
						if (changeMade != null)
							changeMade.value = true;
					}
				}
				if (isInRange(x, element.absoluteX() + element.width, attachTolerance)) {
					int d = Math.abs(x - (element.absoluteX() + element.width));
					if (d < dx) {
						dx = d;
						result = element.absoluteX() + element.width;
						if (changeMade != null)
							changeMade.value = true;
					}
				}
			}
		}
		if (result < parentElement.absoluteX()) {
			result = parentElement.absoluteX();
		}
		if (result > parentElement.absoluteX() + parentElement.width) {
			result = parentElement.absoluteX() + surfaceWidth;
		}
		return result;
	}

	public static int getPreferredY(GraphicalElement parentElement, List<GraphicalElement> elements, int y,
			GraphicalElement elementToIgnore, WrappedBoolean changeMade, int attachTolerance, int surfaceHeight) {
		int result = y;
		int dy = attachTolerance + 1;
		for (GraphicalElement element : elements) {
			if (element != elementToIgnore) {
				if (isInRange(y, element.absoluteY(), attachTolerance)) {
					int d = Math.abs(y - element.absoluteY());
					if (d < dy) {
						dy = d;
						result = element.absoluteY();
						if (changeMade != null)
							changeMade.value = true;
					}
				}
				if (isInRange(y, element.absoluteY() + element.height, attachTolerance)) {
					int d = Math.abs(y - (element.absoluteY() + element.height));
					if (d < dy) {
						dy = d;
						result = element.absoluteY() + element.height;
						if (changeMade != null)
							changeMade.value = true;
					}
				}
			}
		}
		if (result < parentElement.absoluteY()) {
			result = parentElement.absoluteY();
		}
		if (result > parentElement.absoluteY() + parentElement.height) {
			result = parentElement.absoluteY() + surfaceHeight;
		}
		return result;
	}

	public static void updatePosition(GraphicalElement parentElement, List<GraphicalElement> elements,
			GraphicalElement elementToIgnore, WorkingCopy selectedElement, int dx, int dy, Point p, int cursorType,
			int attachSize, int width, int height) {
		switch (cursorType) {
		case Cursor.HAND_CURSOR:
			selectedElement.x1 -= dx - p.x;
			selectedElement.y1 -= dy - p.y;
			selectedElement.x2 -= dx - p.x;
			selectedElement.y2 -= dy - p.y;
			if (attachSize > 0) {
				WrappedBoolean changeMade = new WrappedBoolean(false);
				int p1 = selectedElement.x1 - getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, changeMade, attachSize, width);
				if (changeMade.value) {
					changeMade.value = false;
					p.x = selectedElement.x2 - getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, changeMade, attachSize, width);
					if (changeMade.value) {
						p.x = Math.min(p1, p.x);
					} else {
						p.x = p1;
					}
				} else {
					p.x = selectedElement.x2 - getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null, attachSize, width);
				}
				selectedElement.x1 -= p.x;
				selectedElement.x2 -= p.x;
				changeMade.value = false;
				p1 = selectedElement.y1 - getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, changeMade, attachSize, height);
				if (changeMade.value) {
					changeMade.value = false;
					p.y = selectedElement.y2 - getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, changeMade, attachSize, height);
					if (changeMade.value) {
						p.y = Math.min(p1, p.y);
					} else {
						p.y = p1;
					}
				} else {
					p.y = selectedElement.y2 - getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null, attachSize, height);
				}
				selectedElement.y1 -= p.y;
				selectedElement.y2 -= p.y;
			}
			break;
		case Cursor.N_RESIZE_CURSOR:
			selectedElement.y1 -= dy - p.y;
			if (attachSize > 0) {
				p.y = selectedElement.y1 - getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null, attachSize, height);
				selectedElement.y1 -= p.y;
			}
			break;
		case Cursor.NE_RESIZE_CURSOR:
			selectedElement.x2 -= dx - p.x;
			selectedElement.y1 -= dy - p.y;
			if (attachSize > 0) {
				p.x = selectedElement.x2 - getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null, attachSize, width);
				p.y = selectedElement.y1 - getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null, attachSize, height);
				selectedElement.x2 -= p.x;
				selectedElement.y1 -= p.y;
			}
			break;
		case Cursor.NW_RESIZE_CURSOR:
			selectedElement.x1 -= dx - p.x;
			selectedElement.y1 -= dy - p.y;
			if (attachSize > 0) {
				p.x = selectedElement.x1 - getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null, attachSize, width);
				p.y = selectedElement.y1 - getPreferredY(parentElement, elements, selectedElement.y1, elementToIgnore, null, attachSize, height);
				selectedElement.x1 -= p.x;
				selectedElement.y1 -= p.y;
			}
			break;
		case Cursor.S_RESIZE_CURSOR:
			selectedElement.y2 -= dy - p.y;
			if (attachSize > 0) {
				p.y = selectedElement.y2 - getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null, attachSize, height);
				selectedElement.y2 -= p.y;
			}
			break;
		case Cursor.SE_RESIZE_CURSOR:
			selectedElement.x2 -= dx - p.x;
			selectedElement.y2 -= dy - p.y;
			if (attachSize > 0) {
				p.x = selectedElement.x2 - getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null, attachSize, width);
				p.y = selectedElement.y2 - getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null, attachSize, height);
				selectedElement.x2 -= p.x;
				selectedElement.y2 -= p.y;
			}
			break;
		case Cursor.SW_RESIZE_CURSOR:
			selectedElement.x1 -= dx - p.x;
			selectedElement.y2 -= dy - p.y;
			if (attachSize > 0) {
				p.x = selectedElement.x1 - getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null, attachSize, width);
				p.y = selectedElement.y2 - getPreferredY(parentElement, elements, selectedElement.y2, elementToIgnore, null, attachSize, height);
				selectedElement.x1 -= p.x;
				selectedElement.y2 -= p.y;
			}
			break;
		case Cursor.W_RESIZE_CURSOR:
			selectedElement.x1 -= dx - p.x;
			if (attachSize > 0) {
				p.x = selectedElement.x1 - getPreferredX(parentElement, elements, selectedElement.x1, elementToIgnore, null, attachSize, width);
				selectedElement.x1 -= p.x;
			}
			break;
		case Cursor.E_RESIZE_CURSOR:
			selectedElement.x2 -= dx - p.x;
			if (attachSize > 0) {
				p.x = selectedElement.x2 - getPreferredX(parentElement, elements, selectedElement.x2, elementToIgnore, null, attachSize, width);
				selectedElement.x2 -= p.x;
			}
			break;
		}
	}
	
	public static int getRecommendedCursorType(WorkingCopy element, int x, int y) {
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

	public static class WorkingCopy {
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
			return Math.abs(x2 - x1);
		}

		public int height() {
			return Math.abs(y2 - y1);
		}
	}

	public static class WrappedBoolean {

		public boolean value;

		public WrappedBoolean(boolean value) {
			this.value = value;
		}
	}

}
