package cz.vutbr.fit.dashapp.view.tools;

import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.SideBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

public interface IGUITool {
	
	void provideMenuItems(MenuBar menuBar);

	void provideToolbarItems(ToolBar toolbar);

	void provideSidebarItems(SideBar sideBar);

	void providePopupItems(Canvas canvas);

}
