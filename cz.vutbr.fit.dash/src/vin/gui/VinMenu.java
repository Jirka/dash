package vin.gui;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Class for menu.
 * 
 * @author jurij
 *
 */
public class VinMenu extends VinActions implements VinIComponent {
	
	private JMenuBar menuBar;
	
	/**
	 * Creates menu.
	 */
	public VinMenu() {
	    
		menuBar = new JMenuBar();
        //menuBar.setBackground(Color.GRAY);
        
        VinActions actions = ViewGui.getActions();

        // 1.) FILE //
        JMenu fileMenu = new JMenu("File");
        
        // -- New profile //
        JMenuItem menuNewProfile = fileMenu.add("New");
        menuNewProfile.addActionListener(actions.getActionNewProfile());
        
        // -- Save //
        JMenuItem menuSave = fileMenu.add("Save");
        menuSave.setMnemonic(0);
        menuSave.addActionListener(actions.getActionSaveProfile());

        // -- Save as //
        JMenuItem menuSaveAs = fileMenu.add("Save as");
        menuSaveAs.setMnemonic(1);
        menuSaveAs.addActionListener(actions.getActionSaveProfile());
        
        // -- Exit //
        JMenuItem menuExitFile = fileMenu.add("Exit");
        menuExitFile.addActionListener(actions.getActionExitProgram());
        menuBar.add(fileMenu);

    }

	@Override
    public JComponent getWidget() {
	    return menuBar;
    }

}
