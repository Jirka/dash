package cz.vutbr.fit.dashapp.web;

import java.io.File;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.util.PathUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DownloadPageUtils {
	
	public static final String PHANTOM_BIN = "phantomjs";
	public static final String PHANTOM_MAIN = "main.js";
	public static final String CONFIG_FILE = "config.json";
	
	/**
	 * for debug purposes
	 */
	public static final String DASH_SAMPLES_PHANTOM_INSTALLATION = PathUtils.replaceSeparators("/phantom/src/");
	
	/**
	 * for debug purposes
	 */
	public static final String DASH_SAMPLES_CONFIG_LOCATION = PathUtils.replaceSeparators("/phantom/src/config/");
	
	
	public static String getPreferredPhantomBin() {
		String preferredPhantomBin = PHANTOM_BIN;
		
		// TODO check if Phantom.js installed		
		if(!isPhantomInstalled()) {
			// TODO else check actual folder
			String candidatePhantomBin = "./" + PHANTOM_BIN;
			if(isExistingFile(candidatePhantomBin)) {
				preferredPhantomBin = new File(candidatePhantomBin).getAbsolutePath();
			} else {
				// else take dash.samples installation (only for debug purposes if exists)
				String dashWorkspacePath = PathUtils.getDashSamplesPath();
				if(dashWorkspacePath == null) {
					dashWorkspacePath = PathUtils.getDashWebPath();
				}
				if(dashWorkspacePath != null) {
					candidatePhantomBin = dashWorkspacePath + DASH_SAMPLES_PHANTOM_INSTALLATION + PHANTOM_BIN;
					if(isExistingFile(candidatePhantomBin)) {
						preferredPhantomBin = candidatePhantomBin;
					}
				}
			}
		}
		
		return preferredPhantomBin;
	}
	
	public static String getPreferredPhantomMain() {
		String preferredPhantomMain = PHANTOM_MAIN;
		
		// TODO check actual folder
		String candidatePhantomMain = "./" + PHANTOM_MAIN;
		if(isExistingFile(candidatePhantomMain)) {
			preferredPhantomMain = new File(candidatePhantomMain).getAbsolutePath();
		} else {
			// else take dash.samples installation (only for debug purposes if exists)
			String dashWorkspacePath = PathUtils.getDashSamplesPath();
			if(dashWorkspacePath == null) {
				dashWorkspacePath = PathUtils.getDashWebPath();
			}
			if(dashWorkspacePath != null) {
				candidatePhantomMain = dashWorkspacePath + DASH_SAMPLES_PHANTOM_INSTALLATION + PHANTOM_MAIN;
				if(isExistingFile(candidatePhantomMain)) {
					preferredPhantomMain = candidatePhantomMain;
				}
			}
		}
		
		return preferredPhantomMain;
	}
	
	public static String getPreferredConfigFile() {
		String preferredConfigFile = CONFIG_FILE;
		
		// TODO check actual folder
		String candidateConfigFile = "./" + CONFIG_FILE;
		if(isExistingFile(preferredConfigFile)) {
			preferredConfigFile = new File(candidateConfigFile).getAbsolutePath();
		} else {
			// else take dash.samples configuration file (only for debug purposes if exists)
			String dashWorkspacePath = PathUtils.getDashSamplesPath();
			if(dashWorkspacePath == null) {
				dashWorkspacePath = PathUtils.getDashWebPath();
			}
			if(dashWorkspacePath != null) {
				candidateConfigFile = dashWorkspacePath + DASH_SAMPLES_CONFIG_LOCATION + CONFIG_FILE;
				if(isExistingFile(candidateConfigFile)) {
					preferredConfigFile = candidateConfigFile;
				}
			}
		}
		
		return preferredConfigFile;
	}
	
	private static boolean isPhantomInstalled() {
		//TODO
		return false;
	}
	
	public static String getPreferredOutputfolder() {
		return DashAppModel.getInstance().getWorkspaceFolder().getPath();
	}
	
	private static boolean isExistingFile(String path) {
		return new File(path).exists();
	}
	
	@SuppressWarnings("unused")
	private static boolean isPhantomBinDirectory(String path) {
		File phantomBinDirectory = new File(path);
		if(phantomBinDirectory.exists() && phantomBinDirectory.isDirectory()) {
			File phantomBinFile = new File(path + PHANTOM_BIN);
			return phantomBinFile.exists();
		}
		
		return false;
	}

}
