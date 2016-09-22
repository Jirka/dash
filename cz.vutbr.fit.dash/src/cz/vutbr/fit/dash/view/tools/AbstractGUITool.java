package cz.vutbr.fit.dash.view.tools;

import cz.vutbr.fit.dash.view.Canvas;
import cz.vutbr.fit.dash.view.MenuBar;
import cz.vutbr.fit.dash.view.SideBar;
import cz.vutbr.fit.dash.view.ToolBar;

public class AbstractGUITool implements IGUITool {

	@Override
	public void provideMenuItems(MenuBar menuBar) {
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
	}

	@Override
	public void provideSidebarItems(SideBar sideBar) {
	}

	@Override
	public void providePopupItems(Canvas canvas) {
	}

}
