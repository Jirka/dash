package cz.vutbr.fit.dashapp.view.util;

import java.awt.Container;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ViewUtils {
	
	/**
	 * Adds new button.
	 * 
	 * @param tooltip
	 * @param iconPath
	 * @param action
	 * @param mnemonic
	 * @return button
	 */
	public static JButton addButton(Container bar, String tooltip, String iconPath, Action action, int mnemonic) {
    	
    	JButton button = new JButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(ViewUtils.class.getResource(iconPath)));
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
	 * @param selected 
	 * @return button
	 */
	public static JToggleButton addToggleButton(Container bar, String tooltip, String iconPath, Action action, int mnemonic) {
    	
    	JToggleButton button = new JToggleButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(ViewUtils.class.getResource(iconPath)));
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	bar.add(button);
    	
    	return button;
    }

}
