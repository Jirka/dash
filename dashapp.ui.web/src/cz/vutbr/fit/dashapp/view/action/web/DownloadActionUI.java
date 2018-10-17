package cz.vutbr.fit.dashapp.view.action.web;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.action.IDashActionUI;
import cz.vutbr.fit.dashapp.view.dialog.BoxLayoutFormDialog;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog.DashAppTask;
import cz.vutbr.fit.dashapp.view.dialog.SimpleDialogs;
import cz.vutbr.fit.dashapp.view.dialog.widgets.SimpleCheckBox;
import cz.vutbr.fit.dashapp.view.dialog.widgets.SimpleFileChooser;
import cz.vutbr.fit.dashapp.view.dialog.widgets.SimpleTextField;
import cz.vutbr.fit.dashapp.web.DownloadPage;
import cz.vutbr.fit.dashapp.web.DownloadPageUtils;
import cz.vutbr.fit.dashapp.web.PhantomConfiguration;

/**
 * 
 * @author Adriana Jelencikova
 * @author Jiri Hynek
 *
 */
public class DownloadActionUI implements IDashActionUI {

	public DownloadActionUI() {
		super();
	}

	@Override
	public void perform(ActionEvent e) 
	{
		// open modal dialog to get configuration settings
		// (ask user to fill a phantom configuration form)
		PhantomConfiguration phantomConfiguration = new PhantomConfiguration();
		if(getSettings(phantomConfiguration)) {
			// perform task on background
			DownloadTask task = new DownloadTask(phantomConfiguration);
			DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
			monitor.execute();
		}
	}
	
	private boolean getSettings(PhantomConfiguration phantomConfiguration) {
		DownloadActionFormDialog settingsDialog = new DownloadActionFormDialog(phantomConfiguration);
		return settingsDialog.showConfirmDialog();
	}
	
	/**
	 * Ask user to fill a phantom configuration form.
	 * 
	 * @author Adriana Jelencikova
	 * @author Jiri Hynek
	 *
	 */
	public static class DownloadActionFormDialog extends BoxLayoutFormDialog {
		
		private static final String URL_PREFIX = "http://";
		
		private PhantomConfiguration phantomConfiguration;
		
		SimpleFileChooser configFile;
		SimpleFileChooser phantomBin;
		SimpleFileChooser phantomMain;
		SimpleFileChooser outputFolder;
		SimpleTextField urlAddress;
		SimpleTextField maximumHierarchyLevel;
		SimpleTextField fileName;
		SimpleTextField imageFormat;
		SimpleTextField selector;
		SimpleTextField width;
		SimpleTextField height;
		SimpleTextField marginX;
		SimpleTextField marginY;
		SimpleTextField timeout;
		SimpleCheckBox onlyScreen;
		SimpleCheckBox generateWidgetScreenshots;
		SimpleCheckBox enableCustomValues;
		
		public DownloadActionFormDialog(PhantomConfiguration phantomConfiguration) {
			this.phantomConfiguration = phantomConfiguration;
		}

		@Override
		protected void addCustomWidgets(JPanel panel) {
			super.addCustomWidgets(panel);
			
			// ------ phantom bin
			phantomBin = new SimpleFileChooser("*Phantom.js bin:", DownloadPageUtils.getPreferredPhantomBin(), JFileChooser.FILES_ONLY, null);
			phantomMain = new SimpleFileChooser("*Phantom.js main:", DownloadPageUtils.getPreferredPhantomMain(), JFileChooser.FILES_ONLY, new FileNameExtensionFilter("Javascript files", "js"));
			outputFolder = new SimpleFileChooser("Output folder:", DownloadPageUtils.getPreferredOutputfolder(), JFileChooser.DIRECTORIES_ONLY, null);
			fileName = new SimpleTextField("Result filename:", "download");
			configFile = new SimpleFileChooser("Config file:", DownloadPageUtils.getPreferredConfigFile(), JFileChooser.FILES_ONLY, new FileNameExtensionFilter("JSON files", "json"));
			urlAddress = new SimpleTextField("URL address:", URL_PREFIX);
			imageFormat = new SimpleTextField("Image format:", "png");
			width = new SimpleTextField("Width:", "1280");
			height = new SimpleTextField("Height:", "1024");
			marginX = new SimpleTextField("Margin X:", "0");
			marginY = new SimpleTextField("Margin Y:", "0");
			maximumHierarchyLevel = new SimpleTextField("Maximum hierarchy level", "1");
			selector = new SimpleTextField("Selector:", "body");
			timeout = new SimpleTextField("Timeout", "10000");
			onlyScreen = new SimpleCheckBox("Generate only screen", false);
			generateWidgetScreenshots = new SimpleCheckBox("Generate Widget screenshots", false);
			
			enableCustomValues = new SimpleCheckBox("Override config values", true);
			enableCustomValues.createPanel();
			enableCustomValues.getCheckBox().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getSource() == enableCustomValues.getCheckBox()) {
						boolean selected = enableCustomValues.isCheckBoxSet();
						urlAddress.setEnabled(selected);
						imageFormat.setEnabled(selected);
						width.setEnabled(selected);
						height.setEnabled(selected);
						marginX.setEnabled(selected);
						marginY.setEnabled(selected);
						maximumHierarchyLevel.setEnabled(selected);
						selector.setEnabled(selected);
						timeout.setEnabled(selected);
						onlyScreen.setEnabled(selected);
						generateWidgetScreenshots.setEnabled(selected);
					}
				}
			});
			
			panel.add(phantomBin.createPanel());
			panel.add(phantomMain.createPanel());
			panel.add(outputFolder.createPanel());
			panel.add(fileName.createPanel());
			panel.add(configFile.createPanel());
			panel.add(enableCustomValues.getPanel());
			panel.add(urlAddress.createPanel());
			panel.add(imageFormat.createPanel());
			panel.add(width.createPanel());
			panel.add(height.createPanel());
			panel.add(marginX.createPanel());
			panel.add(marginY.createPanel());
			panel.add(maximumHierarchyLevel.createPanel());
			panel.add(selector.createPanel());
			panel.add(timeout.createPanel());
			panel.add(onlyScreen.createPanel());
			panel.add(generateWidgetScreenshots.createPanel());
		}
		
		@Override
		protected boolean processCustomWidgets() {
			super.processCustomWidgets();
			
			// validate inputs...
			StringBuffer validationMessage = new StringBuffer();
			boolean validationResult = validateInput(phantomConfiguration, validationMessage);
			if(!validationResult) {
				SimpleDialogs.report(validationMessage.toString());
			}
			
			return validationResult;
		}
		
		private boolean validateInput(PhantomConfiguration phantomConfiguration, StringBuffer validationMessage) {
			boolean isValid = true;
			
			// phantom bin path
			String phantomBinInput = phantomBin.getResultText();
			if(!isExistingFileOrDirectory(phantomBinInput) || phantomBinInput.isEmpty()) {
				validationMessage.append(generateInvalidValueMessage(phantomBin.getLabel(), phantomBinInput));
				isValid = false;
			} else {
				phantomConfiguration.setPhantomBinPath(phantomBinInput);
			}
			
			// phantom main file path
			String phantomMainInput = phantomMain.getResultText();
			if(!isExistingFileOrDirectory(phantomMainInput) || phantomMainInput.isEmpty()) {
				validationMessage.append(generateInvalidValueMessage(phantomMain.getLabel(), phantomMainInput));
				isValid = false;
			} else {
				phantomConfiguration.setPhantomMainPath(phantomMainInput);
			}
			
			// output folder path
			String outputFolderInput = outputFolder.getResultText();
			if(!isExistingFileOrDirectory(outputFolderInput) && !outputFolderInput.isEmpty()) {
				validationMessage.append(generateInvalidValueMessage(outputFolder.getLabel(), outputFolderInput));
				isValid = false;
			} else {
				phantomConfiguration.setOutputFolder(outputFolderInput);
			}
			
			// file name
			phantomConfiguration.setFileName(fileName.getResultText());
			
			// config file path
			String configFileInput = configFile.getResultText();
			if(!isExistingFileOrDirectory(configFileInput) && !configFileInput.isEmpty()) {
				validationMessage.append(generateInvalidValueMessage(configFile.getLabel(), configFileInput));
				isValid = false;
			} else {
				phantomConfiguration.setConfigPath(configFileInput);
			}
			
			if(enableCustomValues.isCheckBoxSet()) {
				// url
				String urlAddressInput = urlAddress.getResultText();
				if(!urlAddressInput.isEmpty() && !urlAddressInput.equals(URL_PREFIX)) {
					try {
						URL url = new URL(urlAddressInput);
						url.toURI();
						phantomConfiguration.setUrlAddress(urlAddressInput);
					} catch (MalformedURLException | URISyntaxException e) {
						validationMessage.append(generateInvalidValueMessage(urlAddress.getLabel(), urlAddressInput));
						isValid = false;
					}
				}
				
				// image format
				phantomConfiguration.setImageFormat(imageFormat.getResultText());
				
				// screen width
				String widthInput = width.getResultText();
				if(!widthInput.isEmpty()) {
					try {
						phantomConfiguration.setWidth(Integer.parseInt(widthInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(width.getLabel(), widthInput));
						isValid = false;
					}
				}
				
				// screen height
				String heightInput = height.getResultText();
				if(!heightInput.isEmpty()) {
					try {
						phantomConfiguration.setHeight(Integer.parseInt(heightInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(height.getLabel(), heightInput));
						isValid = false;
					}
				}
				
				// screen width
				String marginXInput = marginX.getResultText();
				if(!marginXInput.isEmpty()) {
					try {
						phantomConfiguration.setMarginX(Integer.parseInt(marginXInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(marginX.getLabel(), marginXInput));
						isValid = false;
					}
				}
				
				// screen width
				String marginYInput = marginY.getResultText();
				if(!marginYInput.isEmpty()) {
					try {
						phantomConfiguration.setMarginY(Integer.parseInt(marginYInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(marginY.getLabel(), marginYInput));
						isValid = false;
					}
				}
				
				// hierarchy level
				String maximumHierarchyLevelInput = maximumHierarchyLevel.getResultText();
				if(!maximumHierarchyLevelInput.isEmpty()) {
					try {
						phantomConfiguration.setMaximumHierarchyLevel(Integer.parseInt(maximumHierarchyLevelInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(maximumHierarchyLevel.getLabel(), maximumHierarchyLevelInput));
						isValid = false;
					}
				}
				
				// selector
				phantomConfiguration.setSelector(selector.getResultText());
				
				// timeout
				String timeoutInput = timeout.getResultText();
				if(!timeoutInput.isEmpty()) {
					try {
						phantomConfiguration.setTimeout(Integer.parseInt(timeoutInput));
					} catch (Exception e) {
						validationMessage.append(generateInvalidValueMessage(timeout.getLabel(), timeoutInput));
						isValid = false;
					}
				}
				
				// only screen
				phantomConfiguration.setOnlyScreen(onlyScreen.isCheckBoxSet());
				
				// generate widget screenshots
				phantomConfiguration.setGenerateWidgetScreenshots(generateWidgetScreenshots.isCheckBoxSet());
			}
			
			return isValid;
		}
		
		private boolean isExistingFileOrDirectory(String path) {
			return new File(path).exists();
		}
		
		private String generateInvalidValueMessage(String key, String value) {
			return "Invalid '"
					+ (key.endsWith(":") ? key.substring(0, key.length()-1) : key)
					+ "' value: "
					+ value
					+ "\n";
		}
	}
	
	/**
	 * Task which downloads a dashboard. It runs on background.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class DownloadTask extends DashAppTask {
		
		private String message = "";
		private PhantomConfiguration phantomConfiguration;
		
		public DownloadTask(PhantomConfiguration phantomConfiguration) {
			this.phantomConfiguration = phantomConfiguration;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// build command
			Thread.sleep(1000); // FIXME (problem with monitor pop-up)
			message = "preparing";
			setProgress(1);
			String command = DownloadPage.buildCommand(phantomConfiguration);
			
			// execute command
			message = "downloading";
			setProgress(2);
			
			// -- get timeout
			Integer timeout = phantomConfiguration.getTimeout();
			if(timeout == null) {
				timeout = 10000; // 10 seconds by default
			}
			
			// -- run command until file is downloaded
			StringBuffer result = new StringBuffer();
			int status = -1;
			boolean downloaded = false;
			while(!downloaded) {
				status = DownloadPage.runPhantomScript(command, result, timeout);
				if(status == DownloadPage.STATUS_OK) {
					downloaded = true;
				} else {
					downloaded = SimpleDialogs.YesNoCancel("Task was not able to download page ('" + DownloadPage.getErrorMessage(status) + "'). Try again?") != JOptionPane.YES_OPTION;
				}
			}
			
			if(status == DownloadPage.STATUS_OK) {
				// update workspace
				message = "updating workspace";
				setProgress(99);
				// TODO switch to output folder and refresh workspace
				refreshWorkspace(result);
				SimpleDialogs.report("File has been successfully downloaded");
			}
			
			// done
			setProgress(100);
			
			return null;
		}

		private void refreshWorkspace(StringBuffer result) {
			// process result output
			String resultPath = findValue(result, "resultPath::");
			String fileName = findValue(result, "fileName::");
			String fileExtension = findValue(result, "imageFormat::");
			
			if(resultPath != null && fileName != null && fileExtension != null ) {
				// update workspace
				File workspacePath = new File(resultPath);
				if(workspacePath.exists() && workspacePath.isDirectory()) {
					DashAppController.getEventManager().
					updateWorkspaceFolder(new WorkspaceFolder(DashAppModel.getInstance(), workspacePath), true);
					
					// select file
					DashAppController.getEventManager().
					changeSelectedWorkspaceFile(fileName, fileExtension);
					
					// format XML
					DashAppController.getEventManager().
					formatDashboardXml();
				}
			}
		}

		@Override
		public Object getMainLabel() {
			return "Downloading dashoard";
		}

		@Override
		public Object getMessage() {
			return message;
		}
		
		private String findValue(StringBuffer consoleOutput, String key) {					    	
	    	int start = consoleOutput.indexOf(key);
	        if(start != -1) {
	        	start += key.length();
	        	int end = consoleOutput.indexOf("\n", start);
	        	if(end != -1) {
	        		return consoleOutput.substring(start, end);
	        	}
	        }
	        return null;
		}
	}
}
