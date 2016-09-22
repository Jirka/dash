package cz.vutbr.fit.dash.view.tools;

import cz.vutbr.fit.dash.view.Canvas;
import cz.vutbr.fit.dash.view.MenuBar;
import cz.vutbr.fit.dash.view.SideBar;
import cz.vutbr.fit.dash.view.ToolBar;

public interface IGUITool {
	
	void provideMenuItems(MenuBar menuBar);

	void provideToolbarItems(ToolBar toolbar);

	void provideSidebarItems(SideBar sideBar);

	void providePopupItems(Canvas canvas);

}
