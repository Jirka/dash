package cz.vutbr.fit.dashapp.view.tools;

import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.action.BasicDashAction;
import cz.vutbr.fit.dashapp.view.action.web.DownloadActionUI;


/**
 * Download tool UI.
 * 
 * @author Adriana Jelencikova
 * @author Jiri Hynek
 *
 */
public class DownloadTool extends AbstractGUITool implements IGUITool {
	
	private static final String LABEL = "Download";
	private static final String ICON = "/icons/Globe.png";
	
	BasicDashAction downloadAction;
	
	public DownloadTool() {
		this(false);
	}
	
	public DownloadTool(boolean addSeparator) {
		super(addSeparator);
		downloadAction = new BasicDashAction(new DownloadActionUI());
	}
	
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		menuBar.addItem(subMenu, LABEL, downloadAction);
	}
	

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, ICON, downloadAction, 0);
	}

}
