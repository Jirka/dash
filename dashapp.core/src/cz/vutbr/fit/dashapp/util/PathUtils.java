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
	public static final String DASHBOARD_SAMPLES_RELATIVE_PATH = File.separator + "dash.samples";
	
	/**
	 * default public workspace dashboard.eval path
	 */
	public static final String DASHBOARD_EVAL_RELATIVE_PATH = File.separator + "dash.eval";
	
	/**
	 * default public workspace dashboard.thesis path
	 */
	public static final String DASHBOARD_THESIS_RELATIVE_PATH = File.separator + "dash.thesis";
	
	/**
	 * default workspace dashboard.web path for web tools
	 */
	public static final String DASHBOARD_WEB_RELATIVE_PATH = File.separator + "web-download-tool";
	
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
	 * For thesis purposes.
	 * 
	 * @return
	 */
	public static String getDashThesisPath() {
		return getDashWorkspacePath(DASHBOARD_THESIS_RELATIVE_PATH);
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
	
	private static String getDashWorkspacePath(String workspaceName) {
		// test act folder (jar file)
		File workspaceParentFolder = new File(ACTUAL_PATH);
		String workspacePath = getDashWorkspacePath(workspaceParentFolder, workspaceName);
		
		// test parent path
		if(workspacePath == null) {
			workspaceParentFolder = workspaceParentFolder.getParentFile();
			workspacePath = getDashWorkspacePath(workspaceParentFolder, workspaceName);
		}
		
		// test grand parent path (git folder)
		if(workspacePath == null) {
			if(workspaceParentFolder != null) {
				workspacePath = getDashWorkspacePath(workspaceParentFolder.getParentFile(), workspaceName);
			}
		}
		
		// test home folder
		if(workspacePath == null) {
			workspacePath = getDashWorkspacePath(new File(DEFAULT_WORKSPACE_HOME_PATH).getParentFile(), workspaceName);
		}
		
		return workspacePath;
	}
	
	private static String getDashWorkspacePath(File workspaceParentFolder, String workspaceName) {
		if(workspaceParentFolder != null) {
			File workskapceFolder = new File(workspaceParentFolder.getPath() + workspaceName);
			
			// debug
			//System.out.println(workspaceParentFolder.getAbsolutePath());
			//System.out.println("-> "  + workskapceFolder.getAbsolutePath());
			
			if(workskapceFolder.exists() && workskapceFolder.isDirectory()) {
				return workskapceFolder.getAbsolutePath();
			}
		}
		return null;
	}
	
	public static String replaceSeparators(String path) {
		String separator = File.separator;
		if(separator.equals("\\")) {
			separator = "\\\\";
		}
		return path.replaceAll("/", separator);
	}

}
