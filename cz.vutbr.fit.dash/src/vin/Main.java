package vin;

import javax.swing.UIManager;

import vin.gui.ViewGui;

/**
 * Main class which initiates GUI.
 * 
 * @author jurij
 *
 */
public class Main {
	
	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ahoj");
		
		try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        } catch (Exception e) {
           System.err.println("Oops!  Something went wrong!");
        }
        
        ViewGui newGui = new ViewGui();
        newGui.launchApplication();
	}

}
