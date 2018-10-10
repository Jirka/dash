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
	private String urlAddress = null;
	private String fileName = null;
	private String imageFormat = null;
	private String selector = null;
	private Integer height = null;
	private Integer width = null;
	private Integer timeout = null;
	private Integer maximumHierarchyLevel = null;
	private Integer marginX = null;
	private Integer marginY = null;
	private Boolean onlyScreen = null;
	private Boolean generateWidgetScreenshots = null;
	
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

	public String getUrlAddress() {
		return urlAddress;
	}

	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}

	public Boolean isOnlyScreen() {
		return onlyScreen;
	}

	public void setOnlyScreen(Boolean onlyScreen) {
		this.onlyScreen = onlyScreen;
	}

	public Integer getMaximumHierarchyLevel() {
		return maximumHierarchyLevel;
	}

	public void setMaximumHierarchyLevel(Integer maximumHierarchyLevel) {
		this.maximumHierarchyLevel = maximumHierarchyLevel;
	}

	public Boolean isGenerateWidgetScreenshots() {
		return generateWidgetScreenshots;
	}

	public void setGenerateWidgetScreenshots(Boolean generateWidgetScreenshots) {
		this.generateWidgetScreenshots = generateWidgetScreenshots;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	public Integer getMarginX() {
		return marginX;
	}

	public void setMarginX(Integer marginX) {
		this.marginX = marginX;
	}
	
	public Integer getMarginY() {
		return marginY;
	}

	public void setMarginY(Integer marginY) {
		this.marginY = marginY;
	}
	
	public String getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
		
	}
}
