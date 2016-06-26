package vin.gui.objects;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Third object which draws random lines.
 * 
 * @author jurij
 *
 */
public class VinObject3 implements VinIObject {
	
	protected int x;
	protected int y;
	protected int type;
	protected Color defaultColor;
	protected BufferedImage image;
	protected Random random;
	protected int color1;
	protected int addcolor;
	protected int move;
	protected boolean enabled;
	protected List<MyPoint> listOfPoints;
	
	/**
	 * Creates new object.
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param color
	 */
	public VinObject3(BufferedImage image, int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.type = VinIObject.THIRD;
		this.defaultColor = color;
		this.image = image;
		this.random = new Random();
		this.move = random.nextInt(4);
		this.enabled = true;
		this.listOfPoints = new ArrayList<VinObject3.MyPoint>();
		listOfPoints.add(new MyPoint(move, x, y, 50));
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
		
		List<MyPoint> helpList = new ArrayList<VinObject3.MyPoint>();
		
		int a;
		
		for(MyPoint point : listOfPoints) {
			
			point.move();
			a = random.nextInt(50);
			if(a==4) {
				//System.out.println(a + " " + listOfPoints.size());
				if(point.liveLength == 1) continue;
				if(point.getType() < 2) {
					helpList.add(new MyPoint(2, point.p.x, point.p.y, point.liveLength-4));
					helpList.add(new MyPoint(3, point.p.x, point.p.y, point.liveLength-4));
				} else {
					helpList.add(new MyPoint(0, point.p.x, point.p.y, point.liveLength-4));
					helpList.add(new MyPoint(1, point.p.x, point.p.y, point.liveLength-4));
				}
			}
			if(!isInArea(point.p.x, point.p.y) || !point.isAlive()) {
				//listOfPoints.remove(point);
			} else {
				helpList.add(point);
				image.setRGB(point.p.x, point.p.y, color1);
			}
		}
		
		listOfPoints.clear();
		
		for(MyPoint point : helpList) {
			listOfPoints.add(point);
		}
		helpList.clear();
		
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
	
	/**
	 * My help points.
	 * 
	 * @author jurij
	 *
	 */
	class MyPoint {
		
		protected Point p;
		protected int type;
		protected int live;
		protected int liveLength;
		
		/**
		 * Creates new help point.
		 * 
		 * @param type
		 * @param x
		 * @param y
		 * @param liveLength
		 */
		public MyPoint(int type, int x, int y, int liveLength) {
			this.type = type;
			p = new Point(x,y);
			this.live = 0;
			this.liveLength = liveLength;
		}
		
		/**
		 * Makes move.
		 */
		public void move() {
			
			if(live > liveLength) return;
			
			if(type == 0) {
				p.x++;
			} else if(type == 1) {
				p.x--;
			} else if(type == 2) {
				p.y++;
			} else if(type == 3) {
				p.y--;
			}
			live++;
		}
		
		/**
		 * Returns point
		 * 
		 * @return point
		 */
		public Point getPoint() {
			return p;
		}
		
		/**
		 * Checks if point is alive-
		 * 
		 * @return isAlive
		 */
		public boolean isAlive() {
			if(live > liveLength) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Return life length.
		 * 
		 * @return live
		 */
		public int getLive() {
			return live;
		}
		
		/**
		 * Returns type.
		 * 
		 * @return type
		 */
		public int getType() {
			return type;
		}
	}

}
