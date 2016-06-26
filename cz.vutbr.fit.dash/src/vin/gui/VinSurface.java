package vin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import vin.dialogs.VinDialogs;
import vin.gui.objects.VinIObject;
import vin.gui.objects.VinObject;
import vin.gui.objects.VinObject2;
import vin.gui.objects.VinObject3;

/**
 * Class which contains surface for picture.
 * 
 * @author jurij
 *
 */
@SuppressWarnings("serial")
public class VinSurface extends JPanel implements ActionListener {
	
	protected Timer timer;
	protected Point position;
	protected int width;
	protected int height;
	protected boolean timeEnabled;
	protected int time;
	protected List<VinIObject> listOfObjects;
	protected double[] zoomField = { 0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0 };
	protected int zoom;
	protected Color background;
	protected boolean grayScale;
	
	protected BufferedImage image;
	protected WritableRaster raster;
	
	/**
	 * Creates new surface.
	 * 
	 * @param width
	 * @param height
	 */
	public VinSurface(int width, int height) {
		super();
		
		this.background = Color.BLACK;
		setBackground(Color.BLACK);
		
		listOfObjects = new ArrayList<VinIObject>();
		
		newImage(width, height, 100);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if(ViewGui.getActions().isDrawEnabled()) {
					
					Point point = new Point((int) (e.getPoint().x/zoomField[zoom]), (int) (e.getPoint().y/zoomField[zoom]));
					
					if(point.x < getImageWidth() && point.y < getImageHeight()) {
						
						//Color color = ViewGui.getActions().getColor();
						//int rgb = color.getRed() * 256 + color.getGreen() * 16 + color.getBlue();
						//System.out.println(rgb);
						if(ViewGui.getPalletebar().getSelection() == VinIObject.RANDOM) {
							listOfObjects.add(new VinObject(image, point.x, point.y, ViewGui.getActions().getColor()));
						} else if(ViewGui.getPalletebar().getSelection() == VinIObject.SECOND) {
							listOfObjects.add(new VinObject2(image, point.x, point.y, ViewGui.getActions().getColor()));
						} else if(ViewGui.getPalletebar().getSelection() == VinIObject.THIRD) {
							listOfObjects.add(new VinObject3(image, point.x, point.y, ViewGui.getActions().getColor()));
						}
						//System.out.println("add");
						repaint();
					}
				}
			}
		});
    }
	
	/**
	 * Clears surface.
	 * 
	 * @param color
	 */
	private void clear(int color) {
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				image.setRGB(i, j, color);
			}
		}
		
	}
	
	/**
	 * Returns image.
	 * 
	 * @return image
	 */
	public BufferedImage getRaster() {
		return image;
	}

	/**
	 * Checks if time is enabled.
	 * 
	 * @return timeEnabled
	 */
	public boolean isTimeEnabled() {
    	return timeEnabled;
    }

	/**
	 * Sets time enabled.
	 * 
	 * @param timeEnabled
	 */
	public void setTimeEnabled(boolean timeEnabled) {
		if(timeEnabled != this.timeEnabled) {
			
			VinToolBar toolbar = ViewGui.getVinToolbar();
			
			this.timeEnabled = timeEnabled;
	    	if(timeEnabled) {
	    		timer.setDelay(time);
	    		timer.restart();
	    		toolbar.enablePlay(false);
	    		toolbar.enableStop(true);
	    	} else {
	    		toolbar.enablePlay(true);
	    		toolbar.enableStop(false);
	    	}
		}
    }
	
	/**
	 * Initializes variables.
	 * 
	 * @param width
	 * @param height
	 * @param time
	 */
	private void initializeVariables(int width, int height, int time) {
		this.width = width;
		this.height = height;
		this.time = time;
		this.zoom = 3;
		this.listOfObjects.clear();
		this.grayScale = false;
		
		ViewGui.getVinToolbar().enableZoomIn(true);
		ViewGui.getVinToolbar().enableZoomOut(true);
		
		// size //
		setSize(width,height);
		setPreferredSize(new Dimension(width, height));
	}
	
	/**
	 * Creates new image.
	 * 
	 * @param width
	 * @param height
	 * @param time
	 */
	public void newImage(int width, int height, int time) {
		
		initializeVariables(width, height, time);
		
		// raster //
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		raster = image.getRaster();
		
		clear(background.getRGB());
		
		// timer //
		timer = new Timer(time, this);
		timer.start();
	}

	/**
	 * Saves image.
	 * 
	 * @param selFile
	 * @param ext
	 */
	public void save(File selFile, String ext) {
	    
		try {
	        ImageIO.write(image, ext, selFile);
        } catch (IOException e) {
	        VinDialogs.report("Saving file has failed.");
        }
    }

	/**
	 * Opens image.
	 * 
	 * @param selFile
	 */
	public void open(File selFile) {
		
		try {
	        image = ImageIO.read(selFile);
	        
	        repaint();
        } catch (IOException e) {
        	VinDialogs.report("Opening file has failed.");
        	return;
        }
        
        this.width = image.getWidth();
        this.height = image.getHeight();
        
        initializeVariables(width, height, time);
	    
    }

	/**
	 * Returns image width.
	 * 
	 * @return width
	 */
	public int getImageWidth() {
    	return width;
    }

	/**
	 * Returns image height.
	 * 
	 * @return height
	 */
	public int getImageHeight() {
    	return height;
    }
	
	@Override
	public int getWidth() {
    	return (int) (width*zoomField[zoom]);
    }

	@Override
	public int getHeight() {
    	return (int) (height*zoomField[zoom]);
    }
	
	/**
	 * Returns how fast is image repainted.
	 * 
	 * @return time
	 */
	public int getTime() {
    	return time;
    }

	/**
	 * Set how fast will be image repainted.
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		//System.out.println(time);
	    this.time = time;
	    
	    boolean b = timer.isRunning();
	    if(b) { timer.stop(); }
	    timer = new Timer(time, this);
	    if(b) { timer.start(); };
	    
    }

	/**
	 * Zooms image (if i > 0, zoom in ... zoom out otherwise)
	 * 
	 * @param i
	 */
	public void zoom(int i) {
		
	    if(i > 0) {
	    	zoom++;
	    	if(zoom == (zoomField.length-1)) {
	    		ViewGui.getVinToolbar().enableZoomIn(false);
	    	} else if(zoom == 1) {
	    		ViewGui.getVinToolbar().enableZoomOut(true);
	    	}
	    } else if(i < 0) {
	    	zoom--;
	    	if(zoom == (zoomField.length-2)) {
	    		ViewGui.getVinToolbar().enableZoomIn(true);
	    	} else if(zoom == 0) {
	    		ViewGui.getVinToolbar().enableZoomOut(false);
	    	}
	    }
	    
	    // size //
		setSize((int) (width*zoomField[zoom]), (int) (height*zoomField[zoom]));
		setPreferredSize(new Dimension((int) (width*zoomField[zoom]), (int) (height*zoomField[zoom])));
		//getParent().repaint();
	    
	    repaint();
    }
	
	/**
	 * Deletes points.
	 */
	public void deletePoints() {
	    listOfObjects.clear();
	    repaint();
    }
	
	/**
	 * Returns image background.
	 * 
	 * @return background
	 */
	public Color getImageBackground() {
    	return background;
    }

	/**
	 * Sets image background.
	 * 
	 * @param background
	 */
	public void setImageBackground(Color background) {
    	this.background = background;
    }
	
	/**
	 * Changes color to grayscale.
	 */
	public void changeToGrayScale() {
		
		if(grayScale) return;
		
		BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
	    Graphics g = image1.getGraphics();  
	    g.drawImage(image, 0, 0, null);  
	    g.dispose();
	    
	    image = image1;	
		
		grayScale = true;
		
		repaint();
	}
	
	/**
	 * author Jiri Janda - xjanda17 - ZPO
	 * 
	 * Converts 2D image coordinates to 1D array.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return num
	 */
	private int point_num(int x, int y, int width, int height) {
	    if (x < 0) {
	        x = 0;
	    }
	    if (y < 0) {
	        y = 0;
	    }
	    width--;
	    height--;
	    if (x > width) {
	        x = width;
	    }
	    if (y > height) {
	        y = height;
	    }

	    int num = x + (y * (width + 1));

	    return num;
	}
	
	/**
	 * Converts color to gray scale.
	 * 
	 * @param my_color
	 * @return gray
	 */
	public int getGray(int my_color) {
		
		Color color = new Color(my_color);
		//System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		int a = (int) ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114));
		//if(a > 255 || a < 0)
		  //System.out.println(a);
		
		return a;

		/*int red = (my_color&0xff0000 ) >> 24;
		int green = (my_color&0x00ff00 ) >> 16;
		int blue = (my_color&0x0000ff ) >> 8; 

        int l = (int) (.299 * red + .587 * green + .114 * blue);*/
        //System.out.println(l);
        
        //return 0;
	}
	
	/**
	 * author Ivo Skolek - xskole00 - ZPO
	 * 
	 * Makes adaptive treshold.
	 * 
	 * @param z
	 */
	public void prah_integral(int z) {
		
		System.out.println("ahoj");
		
		/*for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				System.out.println(256+image.getRGB(i, j)%256);
			}
		}*/
		
		int white = Color.white.getRGB();
		int black = Color.black.getRGB();
		int[] g_integral = new int[width*height];
		
		int sum, pos;
		int x1, x2, y1, y2, count;
		int s = height/8;
		int t = 6;

        for (int i = 0; i < width; i++) {
            sum = 0;
            for (int j = 0; j < height; j++) {
                pos = point_num(i, j, width, height);
                if(z == 0) {
                	sum += getGray(image.getRGB(i, j));
                } else {
                	sum += (image.getRGB(i, j)%256)*(-1);
                }
                if (i == 0)
                    g_integral[pos] = sum;
                else
                    g_integral[pos] = sum +  g_integral[point_num(i - 1, j, width, height)];
            }
        }

		for (int i = 0; i < width; i++) {

			x1 = Math.max(i - s/2, 0);
			x2 = Math.min(i + s/2, width-1);

			for (int j = 0; j < height; j++) {
				y1 = Math.max(j - s/2, 0);
				y2 = Math.min(j + s/2, height-1);
				pos = point_num(i, j, width, height);
				sum = g_integral[point_num(x2, y2, width, height)] - g_integral[point_num(x2, y1 - 1, width, height)] - g_integral[point_num(x1 - 1, y2, width, height)] + g_integral[point_num(x1 - 1, y1 - 1, width, height)];
				count = (x2 - x1) * (y2 - y1);

				if(z == 0) {
					int gray = getGray(image.getRGB(i, j));
					
					if ((gray * count) <= (sum * (100-t)/100)) {
						image.setRGB(i, j, black);
					}
					else {
						image.setRGB(i, j, white);
					}
				} else {
					if ((((image.getRGB(i, j)%256*-1)) * count) <= (sum * (100-t)/100))
						image.setRGB(i, j, white);
					else
					{
						image.setRGB(i, j, 0);
					}
				}
			}
		}
		
		repaint();
		
	    return;
	}
	
	/**
	 * Updates raster.
	 */
	private void updateRaster() {
		for(VinIObject object : listOfObjects) {
			object.makeStep();
		}
		/*position.x++;
		position.y++;
		image.setRGB(position.x, position.y, 0xffca68);*/
    }
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
		//super.paintBorder(g);
		Graphics2D g1 = (Graphics2D) g;
		g1.scale(zoomField[zoom], zoomField[zoom]);
		g1.drawImage(image, 0, 0, Color.white, null);
		if(ViewGui.getActions().isDrawEnabled()) {
			for(VinIObject object : listOfObjects) {
	        	if(object.getType() == VinIObject.RANDOM) {
	        		g1.setColor(object.getDefaultColor());
	        		g1.drawOval(object.getX()-2, object.getY()-4, 4, 4);
	        	} else if(object.getType() == VinIObject.SECOND) {
	        		g1.setColor(object.getDefaultColor());
	        		g1.drawRect(object.getX()-2, object.getY()-4, 4, 4);
	        	} else if(object.getType() == VinIObject.THIRD) {
	        		g1.setColor(object.getDefaultColor());
	        		g1.drawRect(object.getX()-2, object.getY()-4, 4, 4);
	        	}
	        }
		}
        
    }
	
	@Override
    public void actionPerformed(ActionEvent arg0) {
		if(timeEnabled) {
			updateRaster();
			repaint();
		    timer.restart();
		}
    }
	
	

}
