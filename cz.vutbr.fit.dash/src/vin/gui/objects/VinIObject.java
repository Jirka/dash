package vin.gui.objects;

import java.awt.Color;

/**
 * Interface for objects.
 * 
 * @author jurij
 *
 */
public interface VinIObject {
	
	public final int RANDOM = 0;
	public final int SECOND = 1;
	public final int THIRD = 2;
	
	/**
	 * Returns x coordinate.
	 * 
	 * @return x
	 */
	public int getX();
	
	/**
	 * Returns y coordinate.
	 * 
	 * @return y
	 */
	public int getY();
	
	/**
	 * Sets new position of object.
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y);
	
	/**
	 * Returns type of object.
	 * 
	 * @return type
	 */
	public int getType();
	
	/**
	 * Makes one step called by timer.
	 */
	public void makeStep();
	
	/**
	 * Returns default color of point.
	 * 
	 * @return defaultColor
	 */
	public Color getDefaultColor();

}
