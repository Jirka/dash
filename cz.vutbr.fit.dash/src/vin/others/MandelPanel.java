package vin.others;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;

import javax.swing.*;

public class MandelPanel extends JPanel implements Runnable {
    private int width, height;              // width and height of Panel
    private int size;
    private Image offscreenImage;           // double buffered image
    private Graphics offscreen;             // double buffered graphics handle
    private int currentStep;                // number of pixels drawn to screen
    private int dwell;                      // dwell parameter
    private DoubleSquare square;            // Mandelbrot square to be drawn (math coordinates)
    private IntSquare selectionSquare;      // user's selected square (Java coordinates)
    private volatile boolean isSuspended;   // flag to see if we should continue computing
    private volatile Thread worker;         // thread to do the computation
    private ColorMap colorMap;              // palette of colors for drawing

    // constructor
    MandelPanel() {
        setSquare(new DoubleSquare(-1.5, -1.0, 1.0));        // default region
        resetSelectionSquare();
        setBackground(Color.BLACK);
        colorMap = new ColorMap();
    }
   

    // number of pixels drawn
    public synchronized int getCurrentStep() { return currentStep; }

    // number of pixels needed to draw
    public synchronized int getTotalSteps()  { return size * size; }

    // load in the colormap
    public void setColorMap(ColorMap colorMap) { this.colorMap = colorMap; }

    // set the region to zoom in on (math coordinates)
    public synchronized void setDwell(int dwell) { this.dwell = dwell; }

    // set the region to zoom in on (math coordinates)
    public synchronized void setSquare(DoubleSquare square) { this.square = square; }

    // set the selection square (Java coordinates)
    public synchronized void setSelectionSquare(IntSquare selectionSquare) {
        this.selectionSquare = selectionSquare;
    }

    // make the selection square empty
    public synchronized void resetSelectionSquare() {
        this.selectionSquare = new IntSquare();
    }

    // stop the thread
    public synchronized void stop() {
        worker = null;
        notify();
    }

    // pause the thread
    public synchronized void pause() {
        isSuspended = !isSuspended;
        if (!isSuspended)
            notify();
    }

    // start the thread
    public synchronized void start() {
        isSuspended = false;
        currentStep = 0;
      
        if (worker == null) {
            worker = new Thread(this);
            worker.start();
        }
    }
    
    // perform the Mandelbrot iteration for (x0, y0)
    // slightly optimized version that avoids temporary variable
    private int mand(double x0, double y0) {
        int i;
        double x = x0, y = y0;
        double x2 = x*x, y2 = y*y;
        for (i = 0; (i < dwell - 1) && (x2 + y2 <= 4.0); i++) {
            y = 2*x*y + y0;
            x = x2 - y2 + x0;
            x2 = x*x;
            y2 = y*y;
        }

        // if max iterations return last color in colormap
        if (i == dwell - 1) return colorMap.size() - 1; 
        else return i % colorMap.size();
    }
   
      
    // run the Thread
    public void run() {
        resetImage();
        clearImage();

        // get a local copy of size
        int N;
        synchronized (this) { 
            N = size;
        } 
      
        Thread thisThread = Thread.currentThread();
        for (int ypix = 0; ypix < N; ypix++) {
            // Sun's recommend code for pausing a thread
            try { 
                synchronized (this) { 
                    while (isSuspended && worker == thisThread) 
                        wait(); 
                } 
            }
            catch (InterruptedException e) { /* do nothing */  } 
         
            if (worker != thisThread)
                break;   // let thread die gracefully
         
            for (int xpix = 0; xpix < N; xpix++) {
                double x = scaleX(xpix);
                double y = scaleY(ypix);
                int t = mand(x, y);

                synchronized (this) {
                    offscreen.setColor(colorMap.getColor(t));
                    offscreen.fillRect(xpix, ypix, 1, 1);  // draw a single pixel
                    currentStep++;
                } 
            } 
         
            thisThread.yield();
        }
        repaint();

    }
   


    // convert from Java screen coordinates to math coordinates
    public synchronized DoubleSquare toMathCoordinates(IntSquare r) {
        IntPoint p1 = new IntPoint(r.x, r.y);
        DoublePoint q1 = toMathCoordinates(p1);
        return new DoubleSquare(q1.x, q1.y, r.size * square.size / size);
    }

    // convert a Java screen coordinate to math coordinate
    public synchronized DoublePoint toMathCoordinates(IntPoint p) {
        return new DoublePoint(scaleX(p.x), scaleY(p.y));
    }

    // helper function for converting from Java screen coordinate to math coordinate
    protected synchronized double scaleX(int x) {
        return (double) x * square.size / size + square.x;
    }

    // helper function for converting from Java screen coordinate to math coordinate
    protected synchronized double scaleY(int y) {
        return (double) y * square.size / size + square.y;
    }
   
       
    // clear image
    protected synchronized void clearImage() {
        offscreen.setColor(Color.BLACK);
        offscreen.fillRect(0, 0, size, size);
    }

    // create a new image if necessary
    protected synchronized void resetImage() {

        // System.out.println("width = " + width + ", size = " + size);

        // window was resized
        if (width != getWidth() || height != getHeight()) {
            width  = getWidth();
            height = getHeight();

            size = Math.min(width, height);
            offscreenImage = createImage(size, size);

            if (offscreen != null)
                offscreen.dispose();
            offscreen = offscreenImage.getGraphics();
        }
    }

    // copy buffered image to screen, draw selection square
    public synchronized void paintComponent(Graphics g) {
        // paint background
        super.paintComponent(g);

        resetImage();

        // draw current rendering of Mandelbrot set
        g.drawImage(offscreenImage, 0, 0, this);

        // draw the selection square using XOR color mode
        g.setXORMode(Color.YELLOW);
        g.fillRect(selectionSquare.x, selectionSquare.y, selectionSquare.size, selectionSquare.size);
        g.setPaintMode();
    }

}
