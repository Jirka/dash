package cz.vutbr.fit.dashapp.util;

import java.io.File;

/**
 * 
 * @author Jiri Hynek
 *
 */
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
	 * default private workspace dashboard.samples path for debug purposes
	 */
	public static final String DASHBOARD_SAMPLES_RELATIVE_PATH = "/dash.samples";
	
	/**
	 * default public workspace dashboard.eval path
	 */
	public static final String DASHBOARD_EVAL_RELATIVE_PATH = "/dash.eval";
	
	/**
	 * default workspace dashboard.web path for web tools
	 */
	public static final String DASHBOARD_WEB_RELATIVE_PATH = "/dash.web";
	
	/**
	 * For debug purposes.
	 * 
	 * @return
	 */
	public static String getDashSamplesPath() {
		return getDashWorkspacePath(DASHBOARD_SAMPLES_RELATIVE_PATH);
	}
	
	/**
	 * For eval purposes.
	 * 
	 * @return
	 */
	public static String getDashEvalPath() {
		return getDashWorkspacePath(DASHBOARD_EVAL_RELATIVE_PATH);
	}
	
	/**
	 * For web purposes.
	 * 
	 * @return
	 */
	public static String getDashWebPath() {
		return getDashWorkspacePath(DASHBOARD_WEB_RELATIVE_PATH);
	}
	
	public static String getActualWorkspacePath() {
		return ACTUAL_PATH;
	}
	
	private static String getDashWorkspacePath(String path) {
		File actFile = new File(ACTUAL_PATH);
		if(actFile.getParentFile() != null) {
			File gitDirectory = actFile.getParentFile().getParentFile();
			File dash_samples_dir = new File(gitDirectory.getPath() + path);
			if(dash_samples_dir.exists() && dash_samples_dir.isDirectory()) {
				return dash_samples_dir.getAbsolutePath();
			}
		}
		return null;
	}

}
