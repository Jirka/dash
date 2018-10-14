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
public class AbstractGUITool implements IGUITool {
	
	protected Canvas canvas;
	protected boolean addSeparator;
	
	public AbstractGUITool() {
		this(false);
	}
	
	public AbstractGUITool(boolean addSeparator) {
		this.addSeparator = addSeparator;
	}

	@Override
	public void init(Canvas canvas) {
		this.canvas = canvas;
	}

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

	@Override
	public boolean windowsClosing(WindowEvent e) {
		return false;
	}

}
