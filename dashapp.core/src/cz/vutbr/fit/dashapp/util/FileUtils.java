package cz.vutbr.fit.dashapp.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import cz.vutbr.fit.dashapp.model.Dashboard;

public class FileUtils {
	
	public static File createFile(String folderPath, String fileName, String fileExtension) {
		File folder = new File(folderPath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return new File(folderPath + "/" + fileName + "." + fileExtension);
	}
	
	public static void saveDashboard(Dashboard dashboard, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, "xml");
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(XMLUtils.serialize(dashboard).getBytes());
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void saveImage(BufferedImage image, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, "png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void saveTextFile(String output, String folderPath, String fileName) {
		File outputFile = createFile(folderPath, fileName, "txt");
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(output.getBytes());
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
