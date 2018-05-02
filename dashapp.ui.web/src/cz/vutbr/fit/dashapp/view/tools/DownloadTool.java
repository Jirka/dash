package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.URIResolver;

import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog.DashAppTask;
import cz.vutbr.fit.dashapp.web.DownloadPage;
import cz.vutbr.fit.dashapp.web.DownloadPageUtils;
import cz.vutbr.fit.dashapp.web.PhantomConfiguration;


/**
 * Download tool UI.
 * 
 * @author Adriana Jelencikova
 * @author Jiri Hynek
 *
 */
public class DownloadTool extends AbstractGUITool implements IGUITool {
	
	private static final String LABEL = "Download";
	private static final String ICON = "/icons/Globe.png";
	
	DownloadAction downloadAction;
	
	public DownloadTool() {
		downloadAction = new DownloadAction();
	}
	
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, LABEL, downloadAction);
	}
	

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, ICON, downloadAction, 0);
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class DownloadAction extends AbstractAction {
		
		PhantomConfiguration phantomConfiguration;
		
		SimpleFileChooser configFile;
		SimpleFileChooser phantomBin;
		SimpleFileChooser phantomMain;
		SimpleFileChooser outputFolder;
		SimpleTextField urlAddress;
		SimpleTextField maximumHierarchyLevel;
		SimpleTextField fileName;
		SimpleTextField selector;
		SimpleTextField height;
		SimpleTextField width;
		SimpleTextField timeout;
		SimpleCheckBox onlyScreen;
		SimpleCheckBox generateWidetScreenshots;
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -7806859795031384430L;

		public DownloadAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// open modal dialog to get configuration settings
			try {
				if(getSettings()) {
					// perform task on background
					DownloadTask task = new DownloadTask(phantomConfiguration);
					DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
					monitor.execute();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/**
		 * Ask user to fill a phantom configuration form.
		 * 
		 * @return
		 * @throws Exception 
		 */
		private boolean getSettings() throws Exception {
			// dialog panel
			JPanel panel = new JPanel(); // TODO use better layout
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			//panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			// UI
			createDialogUI(panel);
		    
			// wait for result (modal dialog)
		    int option = JOptionPane.showConfirmDialog(null, panel, "Download settings", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				
				// TODO validate inputs...
				if(!validateInput()) {
					throw new Exception("Input fields are not valid");
				}
				
				phantomConfiguration = new PhantomConfiguration();
				phantomConfiguration.setConfigPath(configFile.getResultText());
				phantomConfiguration.setPhantomBinPath(phantomBin.getResultText());
				phantomConfiguration.setPhantomMainPath(phantomMain.getResultText());
				phantomConfiguration.setOutputFolder(outputFolder.getResultText());
				phantomConfiguration.setUrlAddress(urlAddress.getResultText());
				phantomConfiguration.setFileName(fileName.getResultText());
				phantomConfiguration.setSelector(selector.getResultText());
				
				//not sure if it all following values should be integer or string
				String heightInput = height.getResultText();
				if(!heightInput.isEmpty()) {
					phantomConfiguration.setHeight(Integer.parseInt(heightInput));
				}
				
				String widthInput = width.getResultText();
				if(!widthInput.isEmpty()) {
					phantomConfiguration.setHeight(Integer.parseInt(widthInput));
				}
				
				String timeoutInput = timeout.getResultText();
				if(!timeoutInput.isEmpty()) {
					phantomConfiguration.setTimeout(Integer.parseInt(timeoutInput));
				}
				
				String maximumHierarchyLevelInput = maximumHierarchyLevel.getResultText();
				if(!maximumHierarchyLevelInput.isEmpty()) {
					phantomConfiguration.setTimeout(Integer.parseInt(maximumHierarchyLevelInput));
				}
				
				phantomConfiguration.setOnlyScreen(onlyScreen.isCheckBoxSet());
				phantomConfiguration.setGenerateWidetScreenshots(generateWidetScreenshots.isCheckBoxSet());
				
				return true;
			}
			
			// cancel action
			return false;
		}

		private boolean validateInput() {
			boolean isValid = true;
			
			String configFileInput = configFile.getResultText();
			if(!isExistingFileOrDirectory(configFileInput) && !configFileInput.isEmpty()) {
				isValid = false;
			}
			
			String phantomBinInput = phantomBin.getResultText();
			if(!isExistingFileOrDirectory(phantomBinInput) || phantomBinInput.isEmpty()) {
				isValid = false;
			}
			
			String phantomMainInput = phantomMain.getResultText();
			if(!isExistingFileOrDirectory(phantomMainInput) || phantomMainInput.isEmpty()) {
				isValid = false;
			}
			
			String outputFolderInput = outputFolder.getResultText();
			if(!isExistingFileOrDirectory(outputFolderInput) && !outputFolderInput.isEmpty()) {
				isValid = false;
			}
			
			try {
				validateValueAsInteger(maximumHierarchyLevel.getResultText());
				validateValueAsInteger(height.getResultText());
				validateValueAsInteger(width.getResultText());
				validateValueAsInteger(timeout.getResultText());
			} catch (NumberFormatException e) {
				isValid = false;
			}
			
			try {
				String urlAddressInput = urlAddress.getResultText();
				if(!urlAddressInput.isEmpty()) {
					URL url = new URL(urlAddressInput);
					url.toURI();
				}
			} catch (MalformedURLException e) {
				isValid = false;
			} catch (URISyntaxException e) {
				isValid = false;
			}
			
			return isValid;
		}
		
		private void validateValueAsInteger(String value) throws NumberFormatException {
			if(!value.isEmpty()) {
				Integer.parseInt(value);
			}
		}
		
		private boolean isExistingFileOrDirectory(String path) {
			return new File(path).exists();
		}
		
		/**
		 * form elements
		 * 
		 * @param panel
		 */
		private void createDialogUI(JPanel panel) {
			
			// ------ phantom bin
			phantomBin = new SimpleFileChooser();
			panel.add(phantomBin.createPanel("Phantom.js bin:", DownloadPageUtils.getPreferredPhantomBin(), JFileChooser.FILES_ONLY, null));
			
			// ------ phantom main
			phantomMain = new SimpleFileChooser();
			panel.add(phantomMain.createPanel("Phantom.js main:", DownloadPageUtils.getPreferredPhantomMain(), JFileChooser.FILES_ONLY, new FileNameExtensionFilter("Javascript files", "js")));
			
			// ------ configuration file
			configFile = new SimpleFileChooser();
			panel.add(configFile.createPanel("Config file:", DownloadPageUtils.getPreferredConfigFile(), JFileChooser.FILES_ONLY, new FileNameExtensionFilter("JSON files", "json")));
			
			// ------ output file
			outputFolder = new SimpleFileChooser();
			panel.add(outputFolder.createPanel("Output folder:", DownloadPageUtils.getPreferredOutputfolder(), JFileChooser.DIRECTORIES_ONLY, null));
			
			// TODO add fields for other settings
			// ------  url address
			urlAddress = new SimpleTextField();
			panel.add(urlAddress.createPanel("Url address:", "http://"));
			
			// ------ reslut file name
			fileName = new SimpleTextField();
			panel.add(fileName.createPanel("Result filename:", ""));
			
			// ------ element selector
			selector = new SimpleTextField();
			panel.add(selector.createPanel("Selector:", ""));
			
			// ------ window height
			height = new SimpleTextField();
			panel.add(height.createPanel("Height:", ""));
			
			// ------ window width
			width = new SimpleTextField();
			panel.add(width.createPanel("Width:", ""));
			
			// ------ timeout
			timeout = new SimpleTextField();
			panel.add(timeout.createPanel("Timeout", ""));
			
			// ------ maximum hierarchy level
			maximumHierarchyLevel = new SimpleTextField();
			panel.add(maximumHierarchyLevel.createPanel("Maximum hierarchy level", "1"));
			
			// ------ only screen flag
			onlyScreen = new SimpleCheckBox();
			panel.add(onlyScreen.createPanel("Generate only screen"));
			
			generateWidetScreenshots = new SimpleCheckBox();
			panel.add(generateWidetScreenshots.createPanel("Generate Widget screenshots"));
			
		}
		
		/**
		 * Help widget
		 * 
		 * @author Jiri Hynek
		 *
		 */
		private class SimpleFileChooser {
			
			JTextField textField;
			
			public SimpleFileChooser() {
			}
			
			public JPanel createPanel(String label, String preferredText, int selectionMode, FileNameExtensionFilter filter) {
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
				//configPanel.setLayout(new FlowLayout());
				//configPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
				
				// chosen file text field
				panel.add(new JLabel(label));
				textField = new JTextField("", 30);
				if(preferredText != null) {
					textField.setText(preferredText);
				}
				panel.add(textField);
				
				// open button
				JButton openButton = new JButton("...");
				openButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(selectionMode);
						if(filter != null) {
							fc.setFileFilter(filter);
						}
						fc.showOpenDialog(null);
						try {
							File selectedFile = fc.getSelectedFile();
							if(selectedFile != null) {
								textField.setText(selectedFile.getAbsolutePath());
							}
						} catch(Exception ex) {
							//do nothing
						}
					}
				});
				panel.add(openButton);
				
				return panel;
			}
			
			public String getResultText() {
				return textField.getText();
			}
		}
	}

	private class SimpleTextField {
		JTextField textField;
		
		public JPanel createPanel(String label, String preferredText) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			panel.add(new JLabel(label));
			textField = new JTextField("", 30);
			if(preferredText != null) {
				textField.setText(preferredText);
			}
			panel.add(textField);
			
			return panel;
		}
		
		public String getResultText() {
			return textField.getText();
		}
	}
	
	private class SimpleCheckBox {
		JCheckBox checkBox;
		
		public JPanel createPanel(String label) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			checkBox = new JCheckBox();
			panel.add(new JLabel(label));
			panel.add(checkBox);
			
			return panel;
		}
		
		public boolean isCheckBoxSet() {
			return checkBox.isSelected();
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
			message = "preparing";
			setProgress(1);
			String command = DownloadPage.buildCommand(phantomConfiguration);
			
			// execute command
			message = "downloading";
			setProgress(2);
			DownloadPage.runPhantomScript(command);
			
			// update workspace
			message = "updating workspace";
			setProgress(99);
			// TODO switch to output folder and refresh workspace
			
			// done
			setProgress(100);
			
			return null;
		}

		@Override
		public Object getMainLabel() {
			return "Downloading dashoard";
		}

		@Override
		public Object getMessage() {
			return message;
		}
		
	}

}
