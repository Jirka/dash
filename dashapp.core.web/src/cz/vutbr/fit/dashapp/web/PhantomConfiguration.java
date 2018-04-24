package cz.vutbr.fit.dashapp.web;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class PhantomConfiguration {
	
	private String configPath = null;
	private String phantomBinPath = null;
	private String phantomMainPath = null;
	private String outputFolder = null;
	
	public PhantomConfiguration() {
	}
	
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	public String getConfigPath() {
		return configPath;
	}
	
	public void setPhantomBinPath(String phantomBinPath) {
		this.phantomBinPath = phantomBinPath;
	}
	
	public String getPhantomBinPath() {
		return phantomBinPath;
	}
	
	public void setPhantomMainPath(String phantomMainPath) {
		this.phantomMainPath = phantomMainPath;
	}
	
	public String getPhantomMainPath() {
		return phantomMainPath;
	}
	
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	public String getOutputFolder() {
		return outputFolder;
	}
}
