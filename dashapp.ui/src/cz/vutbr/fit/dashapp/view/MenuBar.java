package cz.vutbr.fit.dashapp.view;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * Main text menu.
 * 
 * @author Jiri Hynek
 *
 */
public class MenuBar implements IComponent {
	
	/**
	 * Main GUI component
	 */
	protected JMenuBar menuBar;
	
	@Override
    public JComponent getComponent() {
	    return menuBar;
    }
	
	/**
	 * Creates menu.
	 */
	public MenuBar(List<IGUITool> plugins) {
		// create new menu bar
		menuBar = new JMenuBar();
        //menuBar.setBackground(Color.GRAY);

        // basic file menu items //
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        // generic menu items
        for (IGUITool plugin : plugins) {
        	plugin.provideMenuItems(this);
		}
        
        // exit application menu item
        if(fileMenu.getItemCount() > 0) {
        	fileMenu.addSeparator();
        }
        addItem(fileMenu, "Exit", new ExitAction());
    }

	/**
	 * Finds existing JMenu containing selected name.
	 * 
	 * @param menuBar
	 * @param menuGroupName
	 * 
	 * @return JMenu
	 */
	public JMenu getSubMenu(String menuGroupName) {
		// get existing one
		MenuElement[] elements = menuBar.getSubElements();
		for (MenuElement menuElement : elements) {
			if(menuElement instanceof JMenu) {
				String menuName = ((JMenu) menuElement).getText();
				if(menuName != null && menuName.equals(menuGroupName)) {
					return (JMenu) menuElement;
				}
			}
		}
		// it does not exists yet, create the new one
		JMenu newSubMenu = new JMenu(menuGroupName);
        menuBar.add(newSubMenu);
		return newSubMenu;
	}
	
	/**
	 * Adds separator.
	 * 
	 */
	public void addSeparator(JMenu menu) {
		menu.addSeparator();
        return;
	}

	/**
	 * Adds new menu item.
	 * 
	 * @param menu
	 * @param string
	 * @param action
	 * @return
	 */
	public JMenuItem addItem(JMenu menu, String string, AbstractAction action) {
		JMenuItem menuItem = menu.add(string);
        menuItem.addActionListener(action);
        return menuItem;
	}

	@SuppressWarnings("serial")
	private static class ExitAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			DashAppView view = DashAppView.getInstance();
			view.getCloseEvent().windowClosing(new WindowEvent(view.getFrame(), WindowEvent.WINDOW_CLOSING));
		}
	}
}
