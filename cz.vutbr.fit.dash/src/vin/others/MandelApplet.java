package vin.others;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**************************************************************
 *  The top level applet.
 *
 *  It is used primarily to display a button to launch the
 *  Frame where the real application is displayed. This is
 *  done so that the user can control window resizing and the
 *  applet can respond by rescaling the picture.
 **************************************************************/

public class MandelApplet extends JApplet implements ActionListener {
    private MandelFrame GUIFrame;
    private JButton launchButton = new JButton("Launch Applet");

    // initialize the applet
    public void init() {
        setLayout(new BorderLayout());

        // button to launch the Mandelbrot explorer
        launchButton.setToolTipText("Launch in new window");
        launchButton.addActionListener(this);
        add(launchButton, BorderLayout.CENTER);
    }

    // when the user clicks the button, launch a frame with the Mandelbrot explorer
    public void actionPerformed(ActionEvent e) {
        GUIFrame = new MandelFrame(this.getCodeBase());
        GUIFrame.setVisible(true);
    }

}
