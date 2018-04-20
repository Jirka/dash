package cz.vutbr.fit.dashapp.view.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.XMLUtils;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

public class DownloadTool extends AbstractGUITool implements IGUITool {
	
	DownloadAction downloadAction;
	
	public DownloadTool() {
		downloadAction = new DownloadAction();
	}
	
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, "Download", downloadAction);
	}
	

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Download", "/icons/Document.png", downloadAction, 0);
	}
	
	public class DownloadAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -7806859795031384430L;

		public DownloadAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{	
			JFrame frame = new JFrame();
			frame.setSize(700, 300);
			frame.setLocationRelativeTo(null);
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(3,0));
			
			JLabel statusBar = new JLabel("");
			panel.add(statusBar);
		    
			JPanel fileChooserPanel = new JPanel();
			JTextField chosenFile = new JTextField("");
			chosenFile.setEditable(false);
			chosenFile.setPreferredSize(new Dimension(400, 60));
			JButton openButton = new JButton("Open Config");
			openButton.setPreferredSize(new Dimension(200, 60));
			
			fileChooserPanel.add(chosenFile);
			fileChooserPanel.add(openButton);
			
			panel.add(fileChooserPanel);
		    
			JTextField chosenPathHidden = new JTextField("");
			JTextField resultPathHidden = new JTextField("");
			
			
			openButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
					
					fc.showOpenDialog(null);
					try {
						File selectedFile = fc.getSelectedFile();
						chosenFile.setText(selectedFile.getName());
						chosenPathHidden.setText(selectedFile.getAbsolutePath());
					} catch(Exception ex) {
						//do nothing
					}
				}
			});			
			
		    JButton button = new JButton("Send");
		    
		    button.addActionListener(new ActionListener() {
		    	
				@Override
				public void actionPerformed(ActionEvent e) {			
					runPhantomScript();
				}

				private void runPhantomScript() {
					try {
						String repositoryPath= getAbsolutePathOfScriptRepository();
						String relativePath = "/dash.samples/phantom/src/";
						String absolutePath = repositoryPath + relativePath;
						
						//setting paths for subprocess
						String phantomJs = absolutePath + "main.js";
						String phantomBin = absolutePath + "phantomjs ";
						String configFile = chosenPathHidden.getText();
						
						String subprocess = phantomBin + phantomJs + " -c " + configFile + " -a " + absolutePath;
						
						Process p = Runtime.getRuntime().exec(subprocess);
						
						String line;
						String resultPath = "";
						String fileName = "";
						String imageFormat = "";
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					    while ((line = reader.readLine()) != null) {
					    	String resultPathToFind = "resultPath::";					    	
					    	int resultPathindex = line.indexOf(resultPathToFind);
					        if(resultPathindex != -1) {
					        	resultPath = line.substring(resultPathindex + resultPathToFind.length());
					        	resultPathHidden.setText(resultPath);
					        }

					        String fileNameToFind = "fileName::";
					        int fileNameIndex = line.indexOf(fileNameToFind);
					        if(fileNameIndex != -1) {
					        	fileName = line.substring(fileNameIndex + fileNameToFind.length());
					        }
					        
					        String imageFormatToFind = "imageFormat::";
					        int imageFormatIndex = line.indexOf(imageFormatToFind);
					        if(imageFormatIndex != -1) {
					        	imageFormat = line.substring(imageFormatIndex + imageFormatToFind.length());
					        }
				        }
					    
					    int status = p.waitFor();
					    
					    String statusText = (status == 0) ? "Success" : "Error";
					    statusBar.setText(statusText);
					    
					    if(status == 0) {
						    File filee = new File(resultPath);
						    DashAppController.getEventManager().updateWorkspaceFolder(
					    		/*
					    		 * set folder where downloaded image and xml are located
					    		 */
				        		new WorkspaceFolder(DashAppModel.getInstance(), new File(resultPath)), true
					        );

				    		File file = new File(resultPath + "/"+ fileName + "."+ imageFormat);
						    File xmlFile = new File(resultPath + "/" + fileName +".xml");
						    if(file.exists() && xmlFile.exists()) {
						    	/*
						    	 * should show downloaded image to canvas
						    	 */
						    	DashAppModel model = DashAppModel.getInstance();
						    	IWorkspaceFile[] children = model.getWorkspaceFolder().getChildren(true);
						    	DashboardFile df = null;
						    	for (IWorkspaceFile child : children) {
									if(child.getFileName().equals(fileName + "." + imageFormat) && child instanceof DashboardFile) {
										df = (DashboardFile) child;
										break;
									}
								}
						    	if(df != null ) {
						    		DashAppController.getEventManager().updateSelectedWorkspaceFile(df);
						    	}					    
						    }
						    
						    /*
						     * should format generated xml to proper structure
						     */
						    
					    }	
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch(InterruptedException e) {
						//TODO
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				private String getAbsolutePathOfScriptRepository() {
					File currentDirFile = new File(".");
					String absolutePath = currentDirFile.getAbsolutePath();
					int index = absolutePath.indexOf("/dash/");
					absolutePath = absolutePath.substring(0, index);
				
					return absolutePath;
				}
		    	
		    } );
		    
		    panel.add(button);
		    frame.add(panel);
		    frame.setVisible(true);
		}
		
	}

}
