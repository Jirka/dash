package cz.vutbr.fit.dashapp.view.config;

import java.util.List;

import cz.vutbr.fit.dashapp.view.tools.IGUITool;

public interface IViewConfiguration {

	List<IGUITool> getGUITools();

	String getAppName();

	int getHeight();

	int getWidth();

	String getDefaultWorkspacePath();

	String getVersion();

}
