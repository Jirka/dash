package cz.vutbr.fit.dash.model;

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
}
