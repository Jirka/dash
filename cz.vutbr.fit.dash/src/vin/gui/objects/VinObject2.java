package vin.gui.objects;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Second object which draws random lines.
 * 
 * @author jurij
 *
 */
public class VinObject2 implements VinIObject {
	
	protected int x;
	protected int y;
	protected int type;
	protected Color defaultColor;
	protected BufferedImage image;
	protected Random random;
	protected int color1;
	protected int addcolor;
	protected int move;
	protected Point p;
	protected boolean enabled;
	
	/**
	 * Creates new object.
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param color
	 */
	public VinObject2(BufferedImage image, int x, int y, Color color) {
		this.p = new Point(x, y);
		this.x = x;
		this.y = y;
		this.type = VinIObject.SECOND;
		this.defaultColor = color;
		this.image = image;
		this.random = new Random();
		this.move = random.nextInt(4);
		this.enabled = true;
		color1 = color.getRed() * 65536 + color.getGreen() * 256 + color.getBlue();
		addcolor = (color.getRed()/64)*65536+(color.getGreen()/64)*256+color.getBlue()/64; 
	}

	@Override
    public int getX() {
	    return x;
    }

	@Override
    public int getY() {
	    return y;
    }

	@Override
    public void setPosition(int x, int y) {
	    this.x = x;
	    this.y = y;
    }

	@Override
    public int getType() {
	    return type;
    }

	@Override
    public void makeStep() {
		
		if(!enabled) return;
	    
		color1 += addcolor;
		color1 %= 0xffffff;
		
		int newMove = random.nextInt(4);
		
		if(move != newMove) {
			move = newMove;
		}
		
		if(move == 0) {
			p.y = p.y+2;
		} else if(move == 1) {
			p.y = p.y-2;
		} else if(move == 2) {
			p.x = p.x+2;
		} else if(move == 3) {
			p.x = p.x-2;
		}
		
		if(!isInArea(p.x, p.y)) {
			enabled = false;
			return;
		}
	    
		image.setRGB(p.x, p.y, color1);
		
    }
	
	/**
	 * Checks if point is in area.
	 * 
	 * @param x
	 * @param y
	 * @return isInArea
	 */
	private boolean isInArea(int x, int y) {
		if(x < image.getWidth() && x >= 0) {
			if(y < image.getHeight() && y >= 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
    public Color getDefaultColor() {
	    return defaultColor;
    }

}
