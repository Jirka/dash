package cz.vutbr.fit.dashapp.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cz.vutbr.fit.dashapp.model.Dashboard;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FileUtils {
	
	public static final String IMAGE_EXTENSION = "png";
	public static final String DASHBOARD_EXTENSION = "xml";
	public static final String TEXT_EXTENSION = "txt";
	
	public static File createFile(String folderPath, String fileName, String fileExtension) {
		File folder = new File(folderPath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return new File(folderPath + "/" + fileName + "." + fileExtension);
	}
	
	public static File saveDashboard(Dashboard dashboard, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, DASHBOARD_EXTENSION);
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(XMLUtils.serialize(dashboard).getBytes());
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return outputFile;
	}
	
	public static File saveImage(BufferedImage image, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, IMAGE_EXTENSION);
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return outputFile;
	}
	
	public static File saveTextFile(String output, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, TEXT_EXTENSION);
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(output.getBytes());
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return outputFile;
	}

}
