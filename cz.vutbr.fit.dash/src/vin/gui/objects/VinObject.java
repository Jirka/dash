package vin.gui.objects;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * First object which draws random lines.
 * 
 * @author jurij
 *
 */
public class VinObject implements VinIObject {
	
	protected int x;
	protected int y;
	protected int type;
	protected List<Point> listOfPoints;
	protected List<Point> listOfHelpPoints;
	protected BufferedImage image;
	protected boolean start;
	protected Random random;
	protected Color defaultColor;
	protected int color1;
	protected int addcolor;
	
	/**
	 * Creates new object.
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param color
	 */
	public VinObject(BufferedImage image, int x, int y, Color color) {
		this.x = x;
		this.y = y;
	    this.type = VinIObject.RANDOM;
	    this.image = image;
	    this.start = true;
	    this.random = new Random();
	    this.defaultColor = color;
	    color1 = color.getRed() * 65536 + color.getGreen() * 256 + color.getBlue();
	    //System.out.println("color1: " + color1);
	    //System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
	    addcolor = (color.getRed()/64)*65536+(color.getGreen()/64)*256+color.getBlue()/64; 
	    //System.out.println("rgb:" + addcolor);
	    
	    
	    listOfPoints = new ArrayList<Point>();
	    listOfPoints.add(new Point(x, y));
	    listOfHelpPoints = new ArrayList<Point>();
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
		
		listOfHelpPoints.clear();
		int a,/* color,*/ count, x, y, j;
		j = 0;
		
		//color1 += 0x000001;
		color1 += addcolor;
		color1 %= 0xffffff;
		//System.out.println(color1);
		//color1 += 0x000100;
		//color1 += 0x010000;
	    
		for(Point point : listOfPoints) {
			
			//color = image.getRGB(point.x, point.y);
			image.setRGB(point.x, point.y, color1/*0x012978*/);/*(4*color+0x985654-color)%16777216)*/;
			
			if(start) {
				count = 4;
				start = false;
			}
			else count = 1/*(random.nextInt(4))*/;
			a = 0;
			
			for(int i = 0; i < count; i++) {
				a += random.nextInt(9); /*(4*color+0x985654-color)%16777216*/
				x = (point.x-1)+(a/3);
				y = (point.y-1)+(a%3);
				if(isInArea(x,y)) {
					Point p1 = new Point(x, y);
					if(!listOfHelpPoints.contains(p1))
						listOfHelpPoints.add(new Point(x, y));
				}
			}
			j++;
		}
		
		listOfPoints.clear();
		for(Point p : listOfHelpPoints) {
			listOfPoints.add(p);
		}
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
