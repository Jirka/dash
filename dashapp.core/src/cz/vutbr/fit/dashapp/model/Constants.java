package cz.vutbr.fit.dashapp.model;

/**
 * Common constants
 * 
 * @author Jiri Hynek
 *
 */
public interface Constants {

	// dimension
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	
	// quadrants
	public enum Quadrant {
		I(0),
		II(1),
		III(2),
		IV(3);
		
		private int index;

		private Quadrant(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	// quadrants
	public enum Side {
		LEFT(0),
		RIGHT(1),
		UP(2),
		DOWN(3);
		
		private int index;

		private Side(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}

	public static int getComplementDimension(int dm) {
		return dm == X ? Y : X;
	}
}
