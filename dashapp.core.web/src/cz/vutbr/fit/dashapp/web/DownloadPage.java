package cz.vutbr.fit.dashapp.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;

/**
 * 
 * @author Adriana Jelencikova
 * @author Jiri Hynek
 *
 */
public class DownloadPage {
	
	/**
	 * Function builds command using given configuration.
	 * 
	 * @param phantomConfiguration
	 * @return
	 */
	public static String buildCommand(PhantomConfiguration phantomConfiguration) {		
		String phantomBin = phantomConfiguration.getPhantomBinPath();
		String phantomMain = phantomConfiguration.getPhantomMainPath();
		String configFile = phantomConfiguration.getConfigPath();
		String outputFolder = phantomConfiguration.getOutputFolder(); // TODO
		
		String command = phantomBin + " " + phantomMain + " -c " + configFile;
		
		// TODO workspace path should be part of phantom main script and this should be removed
		String workspaceDirectory = phantomMain.substring(0, phantomMain.length()-DownloadPageUtils.PHANTOM_MAIN.length());
		command += " -a " + workspaceDirectory;
		
		// TODO configuration file can be missing and parameters can be specified manually (or they can override configuration file)
		
		return command;
	}
	
	/**
	 * Function runs command and process result.
	 * 
	 * @param command
	 * @return
	 */
	public static int runPhantomScript(String command) {
		int status = 0;
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			StringBuffer result = new StringBuffer();
		    while ((line = reader.readLine()) != null) {
		    	result.append(line);
		    	result.append('\n');
	        }
		    
		    System.out.println(result.toString());
		    
		    // TODO process result similarly as runPhantomScript2
		    
		    status = p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return status;
	}

	public static void runPhantomScriptOld(String configFilePath) {
		try {
			String repositoryPath= getAbsolutePathOfScriptRepository();
			String relativePath = "/dash.samples/phantom/src/";
			String absolutePath = repositoryPath + relativePath;
			
			//setting paths for subprocess
			String phantomJs = absolutePath + "main.js";
			String phantomBin = absolutePath + "phantomjs ";
			
			String subprocess = phantomBin + phantomJs + " -c " + configFilePath + " -a " + absolutePath;
			
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
		        	//resultPathHidden.setText(resultPath);
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
		    //statusBar.setText(statusText);
		    
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
	
	private static String getAbsolutePathOfScriptRepository() {
		File currentDirFile = new File(".");
		currentDirFile.getParent();
		String absolutePath = currentDirFile.getAbsolutePath();
		int index = absolutePath.indexOf("/dash/");
		absolutePath = absolutePath.substring(0, index);
	
		return absolutePath;
	}
}
