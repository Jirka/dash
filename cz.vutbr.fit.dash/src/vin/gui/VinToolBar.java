package vin.gui;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * Class which contains toolbar.
 * 
 * @author jurij
 *
 */
public class VinToolBar extends VinActions implements VinIComponent {
	
	protected JToolBar bar;
	protected JButton btnPlay, btnStop, btnColor, btnZoomIn, btnZoomOut;
	
	/**
	 * Creates toolbar.
	 */
	public VinToolBar() {
		
		bar = new JToolBar(JToolBar.VERTICAL);
		
		VinActions actions = ViewGui.getActions();
		
		System.out.println(actions);
		
		// PROFILE //
        addButton("New profile", "/icons/Document.png", actions.getActionNewProfile(), 0);
        addButton("Open profile", "/icons/Open file.png", actions.getActionOpenProfile(), 0);
        addButton("Save", "/icons/Save.png", actions.getActionSaveProfile(), 0);
        
        bar.addSeparator();
        
        // PLAY //
        btnPlay = addButton("Play", "/icons/play.png", actions.getActionPlay(), 0);
        btnStop = addButton("Stop", "/icons/stop.png", actions.getActionStop(), 0);
        enableStop(false);
        
        bar.addSeparator();
        
        addTButton("Draw", "/icons/Edit.png", actions.getActionDraw(), 0);
        btnColor = addButton("Color", "/icons/Color wheel.png", actions.getActionColor(), 0);
        addButton("Delete points", "/icons/Delete.png", actions.getActionDelete(), 0);
        
        bar.addSeparator();
        
        // ZOOM //
        btnZoomIn = addButton("Zoom in", "/icons/Zoom in.png", actions.getActionZoomIn(), 0);
        btnZoomOut = addButton("Zoom out", "/icons/Zoom out.png", actions.getActionZoomOut(), 0);
	}
	
	/**
	 * Adds new button.
	 * 
	 * @param tooltip
	 * @param iconPath
	 * @param action
	 * @param mnemonic
	 * @return button
	 */
	private JButton addButton(String tooltip, String iconPath, Action action, int mnemonic) {
    	
    	JButton button = new JButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(this.getClass().getResource(iconPath)));
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	bar.add(button);
    	
    	return button;
    }
	
	/**
	 * Adds new toggle button.
	 * 
	 * @param tooltip
	 * @param iconPath
	 * @param action
	 * @param mnemonic
	 * @return button
	 */
	private JToggleButton addTButton(String tooltip, String iconPath, Action action, int mnemonic) {
    	
    	JToggleButton button = new JToggleButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(this.getClass().getResource(iconPath)));
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	bar.add(button);
    	
    	return button;
    }
	
	/**
	 * Enables play button.
	 * 
	 * @param enable
	 */
	public void enablePlay(boolean enable) {
		btnPlay.setEnabled(enable);
	}
	
	/**
	 * Enables stop button.
	 * 
	 * @param enable
	 */
	public void enableStop(boolean enable) {
		btnStop.setEnabled(enable);
	}
	
	/**
	 * Enables zoom in button.
	 * 
	 * @param enable
	 */
	public void enableZoomIn(boolean enable) {
		btnZoomIn.setEnabled(enable);
	}
	
	/**
	 * Enables zoom out button
	 * 
	 * @param enable
	 */
	public void enableZoomOut(boolean enable) {
		btnZoomOut.setEnabled(enable);
	}
	
	/**
	 * Changes color.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		btnColor.setBackground(color);
	}
    
	@Override
    public JComponent getWidget(){
        return bar;
    }

}
