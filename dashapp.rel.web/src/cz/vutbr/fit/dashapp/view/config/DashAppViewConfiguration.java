package cz.vutbr.fit.dashapp.view.config;

import cz.vutbr.fit.dashapp.view.tools.DownloadTool;

public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String WEB_VERSION = "rel-web";	
	
	protected void initTools() {
		super.initTools();
		// download tool
		guiTools.add(new DownloadTool());
	}
	
	@Override
	public String getVersion() {
		return WEB_VERSION;
	}

}
