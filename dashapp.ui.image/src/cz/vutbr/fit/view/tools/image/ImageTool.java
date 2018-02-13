package cz.vutbr.fit.view.tools.image;

import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ImageTool extends AbstractGUITool implements IGUITool {
	
	private AbstractImageToolAction[] actions;

	public ImageTool(AbstractImageToolAction[] actions) {
		super();
		this.actions = actions;
	}
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Image");
		if(subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		for (AbstractImageToolAction action : actions) {
			menuBar.addItem(subMenu, action.getName(), action);
		}
	}

}
