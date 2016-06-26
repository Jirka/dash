package vin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import vin.dialogs.VinNewFileDialog;

/**
 * Class which contains frame of application.
 * 
 * @author jurij
 *
 */
public class ViewGui {
	
	protected static JFrame frame;
	protected static VinActions actions;
	protected static VinToolBar toolbar;
	protected static VinPalleteBar palletebar;
	protected static VinTimeSlider timeslider;
	protected static VinScrollPane panel;
	protected static VinNewFileDialog dialogNewFile;
	protected static VinMenu menubar;
	public final static int width = 900;
	public final static int height = 600;
	
	/**
	 * Method launches GUI.
	 */
	public void launchApplication() {
		
		// initialize actions //
		actions = new VinActions();
		
		// creates new Frame //
		frame = new JFrame("ani nevim, jak to pojmenovat | Projekt VIN 2012");
		//frame.setResizable(false);
		
		// set position (in the middle of screen) //
		Toolkit nastroje = frame.getToolkit();
		Dimension screenSize = nastroje.getScreenSize();
		frame.setBounds((screenSize.width/2-450), screenSize.height/2-300, 900, 600);
		
		menubar = new VinMenu();
		frame.getContentPane().add(menubar.getWidget(), BorderLayout.NORTH);
		
		toolbar = new VinToolBar();
		frame.getContentPane().add(toolbar.getWidget(), BorderLayout.WEST);
		
		palletebar = new VinPalleteBar();
		frame.getContentPane().add(palletebar.getWidget(), BorderLayout.EAST);
		
		panel = new VinScrollPane();
		frame.getContentPane().add(panel.getWidget(), BorderLayout.CENTER);
		
		timeslider = new VinTimeSlider();
		frame.getContentPane().add(timeslider.getWidget(), BorderLayout.SOUTH);
		
		dialogNewFile = new VinNewFileDialog();
		
		// ... //
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}

	/**
	 * Returns static reference to actions.
	 * 
	 * @return actions
	 */
	public static VinActions getActions() {
	    return actions;
    }
	
	/**
	 * Returns static reference to drawing panel.
	 * 
	 * @return panel
	 */
	public static VinScrollPane getVinScrollPane() {
	    return panel;
    }

	/**
	 * Returns static reference to new file dialog.
	 * 
	 * @return dialogNewFile
	 */
	public static VinNewFileDialog getVinDialogNewFile() {
    	return dialogNewFile;
    }

	/**
	 * Returns static reference to toolbar.
	 * 
	 * @return toolbar
	 */
	public static VinToolBar getVinToolbar() {
    	return toolbar;
    }

	/**
	 * Returns static reference to pallete bar.
	 * 
	 * @return palletebar
	 */
	public static VinPalleteBar getPalletebar() {
    	return palletebar;
    }
	
	
	
	
}
