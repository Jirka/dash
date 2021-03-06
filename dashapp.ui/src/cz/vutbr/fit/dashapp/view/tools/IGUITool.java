package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.WindowEvent;

import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.SideBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface IGUITool {
	
	void init(Canvas canvas);
	
	void provideMenuItems(MenuBar menuBar);

	void provideToolbarItems(ToolBar toolbar);

	void provideSidebarItems(SideBar sideBar);

	void providePopupItems(Canvas canvas);

	boolean windowsClosing(WindowEvent e);

}
