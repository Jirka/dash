package cz.vutbr.fit.dash.view;

import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import cz.vutbr.fit.dash.view.tools.IGUITool;
import cz.vutbr.fit.dash.view.util.Utils;

/**
 * Icon tool bar.
 * 
 * @author Jiri Hynek
 *
 */
public class ToolBar implements IComponent {

	/**
	 * Main GUI component.
	 */
	protected JToolBar toolbar;
	
	@Override
    public JComponent getComponent() {
        return toolbar;
    }
	
	protected JButton btnZoomIn, btnZoomOut;
	
	/**
	 * Creates toolbar.
	 */
	public ToolBar(List<IGUITool> plugins) {
		initGUI(plugins);
	}
	
	private void initGUI(List<IGUITool> plugins) {
		toolbar = new JToolBar(JToolBar.HORIZONTAL);
		
		// generic tool bar items
        for (IGUITool plugin : plugins) {
        	plugin.provideToolbarItems(this);
		}
	}
	
	/**
	 * 
	 * @return number of tool bar items
	 */
	public int getAmountOfItems() {
		return toolbar.getComponents().length;
	}
	
	/**
	 * Ads separator to a tool bar.
	 */
	public void addSeparator() {
		toolbar.addSeparator();
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
	public JButton addButton(String tooltip, String iconPath, Action action, int mnemonic) {
    	JButton button = new JButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(Utils.class.getResource(iconPath)));
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
		toolbar.add(button);
    	
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
	public JToggleButton addToggleButton(String tooltip, String iconPath, Action action, int mnemonic) {
    	JToggleButton button = new JToggleButton();
    	button.setAction(action);
    	button.setIcon(new ImageIcon(Utils.class.getResource(iconPath)));
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	toolbar.add(button);
    	
    	return button;
    }
}
