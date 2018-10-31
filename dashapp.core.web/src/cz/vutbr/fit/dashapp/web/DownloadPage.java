package cz.vutbr.fit.dashapp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		String outputFolder = phantomConfiguration.getOutputFolder();
		String url = phantomConfiguration.getUrlAddress();
		String fileName = phantomConfiguration.getFileName();
		String imageFormat = phantomConfiguration.getImageFormat();
		String selector = phantomConfiguration.getSelector();
		Integer width = phantomConfiguration.getWidth();
		Integer height = phantomConfiguration.getHeight();
		Integer timeout = phantomConfiguration.getTimeout();
		Integer maximumHierarchyLevel = phantomConfiguration.getMaximumHierarchyLevel();
		Integer marginX = phantomConfiguration.getMarginX();
		Integer marginY = phantomConfiguration.getMarginY();
		Boolean isOnlyScreen = phantomConfiguration.isOnlyScreen();
		Boolean isGenerateWidgetScreenshots = phantomConfiguration.isGenerateWidgetScreenshots();
		
		String command = phantomBin + " " + phantomMain;
		
		// configuration file can be missing and parameters can be specified manually (or they can override configuration file)
		command += isStringSet(configFile) ? " -c " + configFile  : "";
		command += isStringSet(outputFolder) ? " -resultPath " + outputFolder : "";
		command += isStringSet(url) ? " -url " + url  : "";
		command += isStringSet(fileName) ? " -fileName " + fileName : "";
		command += isStringSet(imageFormat) ? " -imageFormat " + imageFormat : "";
		command += isStringSet(selector)? " -selector " + selector : "";
		
		command += height != null ? " -height " + height : "";
		command += width != null ? " -width " + width : "";
		command += timeout != null ?  " -timeout " + timeout : "";
		command += maximumHierarchyLevel != null ? " -maximumHierarchyLevel " + maximumHierarchyLevel : "";
		command += marginX != null ? " -marginX " + marginX : "";
		command += marginY != null ?  " -marginY " + marginY : "";
		
		command += isOnlyScreen != null ? (" -onlyScreen " + (isOnlyScreen ? "true " : "false ")) : "";
		command += isGenerateWidgetScreenshots != null ? (" -generateWidgetScreenshots " + (isGenerateWidgetScreenshots ? "true " : "false ")) : "";
		
		return command;
	}
	
	private static boolean isStringSet(String value) {
		return value != null && !value.isEmpty();
	}
	
	/**
	 * Function runs command and process result.
	 * 
	 * @param command
	 * @return
	 */
	public static int runPhantomScript(String command, StringBuffer result, int timeout) {
		int status = -1;
		
		// run in new thread with limited time
		RunCommandTask timeoutTask = new RunCommandTask(command, result);
		timeoutTask.start();
		try {
			// the timeout should be used by render method of phantom.js
			// there are other stuff which need to be done
			timeout += 2000;
			
			// wait for timeout
			timeoutTask.join(timeout);
		} catch (InterruptedException e) {
			// interrupted (OK)
		}
		
		if(timeoutTask.isAlive()) {
			timeoutTask.interrupt();
			status = STATUS_Timeout;
		} else {
			status = timeoutTask.getStatus();
		}
		
		return status;
	}
	
	/**
	 * Task which runs command using timeout.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	private static class RunCommandTask extends Thread {
		
		private int status = -1;
		private String command;
		private StringBuffer result;
		Process runCommandProcess;
		
		public RunCommandTask(String command, StringBuffer result) {
			this.command = command;
			this.result = result;
		}
		
		@Override
		public final void run() {
			// Function runs command and process result.
			System.out.println("command: " + command);
			
			try {
				runCommandProcess = Runtime.getRuntime().exec(command);
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(runCommandProcess.getInputStream()));
							
			    while ((line = reader.readLine()) != null) {
			    	result.append(line);
			    	result.append('\n');
		        }
			    
			    System.out.println(result.toString());
			    
			    status = runCommandProcess.waitFor();
			} catch (IOException e) {
				status = STATUS_IOException;
			} catch (InterruptedException e) {
				status = STATUS_InterruptedException;
			}
		}
		
		public int getStatus() {
			return status;
		}
		
		@Override
		public void interrupt() {
			// kill command
			runCommandProcess.destroy();
			super.interrupt();
		}
	}
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_IOException = -10;
	public static final int STATUS_InterruptedException = -20;
	public static final int STATUS_Timeout = -30;
	
	public static String getErrorMessage(int status) {
		String message;
		switch (status) {
		case STATUS_IOException:
			message = "problem with reading input.";
			break;
		case STATUS_InterruptedException:
			message = "interruted";
			break;
		case STATUS_Timeout:
			message = "timeout";
			break;
		default:
			message = "unknown problem";
			break;
		}
		
		return message;
	}
}
