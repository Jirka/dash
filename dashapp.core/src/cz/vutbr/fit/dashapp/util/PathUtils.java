package cz.vutbr.fit.dashapp.util;

import java.io.File;

public class PathUtils {
	
	/**
	 * default project home path
	 */
	public static final String DEFAULT_WORKSPACE_HOME_PATH = System.getProperty("user.home");
	
	/**
	 * actual path
	 */
	public static final String ACTUAL_PATH = System.getProperty("user.dir");
	
	/**
	 * default workspace dashboard.samples path
	 */
	public static final String DASHBOARD_SAMPLES_RELATIVE_PATH = "/dash.samples";
	
	/**
	 * For debug purposes.
	 * 
	 * @return
	 */
	public static String getDashSamplesPath() {
		File actFile = new File(ACTUAL_PATH);
		if(actFile.getParentFile() != null) {
			File gitDirectory = actFile.getParentFile().getParentFile();
			File dash_samples_dir = new File(gitDirectory.getPath() + DASHBOARD_SAMPLES_RELATIVE_PATH);
			if(dash_samples_dir.exists() && dash_samples_dir.isDirectory()) {
				return dash_samples_dir.getAbsolutePath();
			}
		}
		return null;
	}

}
