package vin.others;

/**************************************************************
 *  This is the main GUI JFrame for the Mandelbrot explorer.
 **************************************************************/

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;



public class MandelFrame extends JFrame implements ActionListener, ComponentListener {
    // flag to determine when gui initialization is completed
    private boolean isInitialized = false;

    // pathname where applet was started
    private URL codeBase;

    // filename of color map
    private ColorMap colorMap = new ColorMap();

    // timer for updating display (default = every 100ms)
    private javax.swing.Timer timer = new javax.swing.Timer(100, this);

    // Mandelbrot region
    private DoubleSquare square = new DoubleSquare(-1.5, -1.0, 2.0);
    private int dwell = 256;
    private MandelPanel displayPanel;

    // status field
    private JTextField statusTextField = new JTextField("");

    // square selection
    private JLabel xLabel      = new JLabel("x  ");
    private JLabel yLabel      = new JLabel("y  ");
    private JLabel sizeLabel   = new JLabel("size  ");
    private JTextField xTextField    = new JTextField(12);
    private JTextField yTextField    = new JTextField(12);
    private JTextField sizeTextField = new JTextField(12);

   
    // constructor
    public MandelFrame(URL codeBase) {
        super("Mandelbrot Explorer");
        this.codeBase = codeBase;
        colorMap = new ColorMap(getURL("default.map"));

        // init the size and layout of this frame
        setSize(805, 678);
        setLayout(new BorderLayout());


       /******************************************
        *  South  Panel
        ******************************************/
        statusTextField.setEditable(false);
        statusTextField.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusTextField, BorderLayout.SOUTH);


       /******************************************
        *  East  Panel
        ******************************************/
        Box eastBox = new Box(BoxLayout.Y_AXIS);

        // dwell slider
        final JSlider dwellSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 4096, 512);
        dwellSlider.setToolTipText("Adjust dwell");
        TitledBorder tb = new TitledBorder(new EtchedBorder());
        tb.setTitle("Dwell = " + dwellSlider.getValue());
        dwellSlider.setBorder(tb);
        dwellSlider.setPreferredSize(new Dimension(80, 46));
        dwellSlider.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    TitledBorder border = (TitledBorder) dwellSlider.getBorder();
                    border.setTitle("Dwell = " + dwellSlider.getValue());
                    dwell = dwellSlider.getValue();
                    dwellSlider.repaint();
                    displayPanel.setDwell(dwell);
                }
            }
        );

        // square coordinate panel
        JPanel coordinatePanel  = new JPanel(new BorderLayout());
        JPanel coordinateLabels = new JPanel(new GridLayout(4, 0, 0, 5));
        JPanel coordinateValues = new JPanel(new GridLayout(4, 0, 0, 5));

        TextFieldListener textFieldListener = new TextFieldListener();
        xTextField.addActionListener(textFieldListener);
        yTextField.addActionListener(textFieldListener);
        sizeTextField.addActionListener(textFieldListener);
        coordinateLabels.add(xLabel);
        coordinateLabels.add(yLabel);
        coordinateLabels.add(sizeLabel);
        coordinateValues.add(xTextField);
        coordinateValues.add(yTextField);
        coordinateValues.add(sizeTextField);
        setCoordinateText();

        coordinatePanel.add(coordinateLabels, BorderLayout.WEST);
        coordinatePanel.add(coordinateValues, BorderLayout.EAST);

        // color map chooser
        String[] dataFiles = {
            "default.map",
            "altern.map",
            "blues.map",
            "bw.map",
            "chroma.map",
            "defaultw.map",
            "firestrm.map",
            "gamma1.map",
            "gamma2.map",
            "glasses1.map",
            "glasses2.map",
            "green.map",
            "grey.map",
            "headache.map",
            "injector.map",
            "landscap.map",
            "lyapunov.map",
            "neon.map",
            "paintjet.map",
            "topo.map",
            "volcano.map",
        };
        final JComboBox fileList = new JComboBox(dataFiles);
        TitledBorder tbb = new TitledBorder(new EtchedBorder());
        tbb.setTitle("Select a colormap");
        fileList.setBorder(tbb);
        fileList.setPreferredSize(new Dimension(140, 50));
      
        fileList.setEditable(true);
        fileList.setSelectedIndex(0);
        fileList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String urlName = (String) fileList.getSelectedItem();
                colorMap = new ColorMap(getURL(urlName));
                stopAnimation();
                startAnimation();
            }
        });


        // add all of the componets
        eastBox.add(fileList);
        eastBox.add(javax.swing.Box.createVerticalStrut(10));
        eastBox.add(dwellSlider);
        eastBox.add(javax.swing.Box.createVerticalStrut(20));
        eastBox.add(coordinatePanel);
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(eastBox, BorderLayout.NORTH);
        eastPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      
        add(eastPanel, BorderLayout.EAST);

      
       /******************************************
        *  Main Display Panel
        ******************************************/
        JPanel centerPanel = new JPanel(new BorderLayout());
        displayPanel = new MandelPanel();
        displayPanel.addComponentListener(this);
        MouseSelectionListener selectionListener = new MouseSelectionListener();
        displayPanel.addMouseListener(selectionListener);
        displayPanel.addMouseMotionListener(selectionListener);

        centerPanel.add(displayPanel, BorderLayout.CENTER);


        // buttonbar stuff
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 0));
        ButtonListener buttonListener = new ButtonListener();
        String[] buttonGif = new String[] {
            "Pause24.gif", "Stop24.gif", "Play24.gif",
            "ZoomIn24.gif", "ZoomOut24.gif", "Refresh24.gif", "Information24.gif"};
        String[] buttonTip = new String[] {
            "Pause the computation", "Stop the computation", "Begin the computation",
            "Zoom in 2x", "Zoom out 2x", "Reset to default region", "Applet information"};
        String[] buttonAct = new String[] {
            "pause", "stop", "play", "zoom in", "zoom out", "refresh", "information"};


        for (int i = 0; i < buttonGif.length; i++) {
            JButton b = new JButton(new ImageIcon(getURL(buttonGif[i])));
            b.setActionCommand(buttonAct[i]);
            b.setToolTipText(buttonTip[i]);
            b.addActionListener(buttonListener);
            buttonPanel.add(b);
        }

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

      
        // no more GUI code allowed below here
        setVisible(true);
        isInitialized = true;
    }



    // read in URL
    private URL getURL(String filename) {
        URL url = null;
        try { url = new URL(codeBase, filename); }
        catch (Exception e) {  System.out.println("Error loading: codebase = " + codeBase + ",  " + "filename = " + filename); }
        return url;
    }

    // update x, y, and size
    private void setCoordinateText() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0000000000000");
        xTextField.setText("" + decimalFormat.format(square.x));
        yTextField.setText("" + decimalFormat.format(square.y));
        sizeTextField.setText("" + decimalFormat.format(square.size));
    }

   
    // start the animation thread
    public void startAnimation() {
        if (!timer.isRunning()) {
            displayPanel.setColorMap(colorMap);
            displayPanel.setDwell(dwell);
            displayPanel.setSquare(square);
            displayPanel.start();
            timer.start();
            setCoordinateText();
        }
    }

    // stop the animation thread
    public void stopAnimation() {
        if (timer.isRunning()) {
            timer.stop();
        }
        displayPanel.stop();
        showStatus("# pixels = " + displayPanel.getCurrentStep());
    }

    // pause the animation thread.
    public synchronized void pauseAnimation() {
        if (timer.isRunning()) {
            timer.stop();
            displayPanel.pause();
        }
        else {
            timer.start();
            displayPanel.pause();
        }

    }

    public void showStatus(String s) {
        statusTextField.setText(s);
    }
   

    // display Mandelbrot set via Timer
    public void actionPerformed(ActionEvent e) {
        if (displayPanel.getCurrentStep() == displayPanel.getTotalSteps())
            stopAnimation();

        displayPanel.repaint();
        showStatus("# pixels = " + displayPanel.getCurrentStep());
    }

    // window resized
    public void componentResized(ComponentEvent e) {
        if (isInitialized) {
            stopAnimation();
            startAnimation();
        }
    }

    public void componentHidden(ComponentEvent e) { }
    public void componentMoved(ComponentEvent e)  { }
    public void componentShown(ComponentEvent e)  { }




   /*******************************************************
    *  Handle buttons pushed
    *******************************************************/
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String ac = e.getActionCommand();
            if (ac.equals("refresh")) {
                stopAnimation();
                square = new DoubleSquare(-1.5, -1.0, 2.0);
                startAnimation();
            }
            else if (ac.equals("stop")) {
                stopAnimation();
            }
            else if (ac.equals("play")) {
                stopAnimation();
                startAnimation();
            }
            else if (ac.equals("pause")) {
                pauseAnimation();
            }
            else if (ac.equals("zoom out")) {
                stopAnimation();
                square = square.scale(2.0);
                startAnimation();
            }
            else if (ac.equals("zoom in")) {
                stopAnimation();
                square = square.scale(0.5);
                startAnimation();
            }
            else if (ac.equals("information")) {
                JOptionPane.showMessageDialog(null, "Copyright 2001, Kevin Wayne",
                                                    "About Mandelbrot Explorer",
                                                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

   /*******************************************************
    *  Handle text field entries in coordinate panel
    *******************************************************/
    private class TextFieldListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                double x    = Double.parseDouble(xTextField.getText());
                double y    = Double.parseDouble(yTextField.getText());
                double size = Double.parseDouble(sizeTextField.getText());
                square = new DoubleSquare(x, y, size);
                stopAnimation();
                startAnimation();
            }
            catch (NumberFormatException ex) {
                showStatus("Illegal coordinate entry.");
            }
        }
    }


   /*******************************************************
    *  Handle mouse selection of square region to zoom in
    *******************************************************/
    private class MouseSelectionListener extends MouseInputAdapter {
        private IntPoint p;   // initial point selected
        private IntPoint q;   // point where mouse was dragged to
        private IntSquare currentSquare;
      
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 2) {
                showStatus("Point = " + displayPanel.toMathCoordinates(new IntPoint(e.getX(), e.getY())));
            }
            else {
                p = new IntPoint(e.getX(), e.getY());
                q = p;
                currentSquare = new IntSquare(p, p);
            }
         
        }
      
        public void mouseDragged(MouseEvent e)  {
            q = new IntPoint(e.getX(), e.getY());
            currentSquare = new IntSquare(p, q);
            displayPanel.setSelectionSquare(currentSquare);
            displayPanel.repaint();
            showStatus("Square: " + displayPanel.toMathCoordinates(currentSquare));
        }
      
        public void mouseReleased(MouseEvent e) {
            Rectangle r = displayPanel.getBounds();
            if ((currentSquare.size > 0) && r.contains(new Rectangle(currentSquare.x, currentSquare.y, currentSquare.size, currentSquare.size))) {
                displayPanel.resetSelectionSquare();
                square = displayPanel.toMathCoordinates(currentSquare);
                stopAnimation();
                startAnimation();
            }
            else if (e.getClickCount() == 2) {
                // do nothing
            }
            else {
                showStatus("Square not selected");
                displayPanel.resetSelectionSquare();
                displayPanel.repaint();
            }

        }
    }

   
}
