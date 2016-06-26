package cz.vutbr.fit.dash.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.vutbr.fit.dash.analyses.WidgetAnalysis;
import cz.vutbr.fit.dash.gui.DashAppGUI;
import cz.vutbr.fit.dash.gui.Histogram;
import cz.vutbr.fit.dash.gui.Surface;
import cz.vutbr.fit.dash.analyses.ActualAnalysis;
import cz.vutbr.fit.dash.analyses.ColorAnalysis;
import cz.vutbr.fit.dash.analyses.ColorfulnessAnalysis;
import cz.vutbr.fit.dash.analyses.ComplexRasterAnalysis;
import cz.vutbr.fit.dash.analyses.GrayscaleAnalysis;
import cz.vutbr.fit.dash.analyses.IAnalysis;
import cz.vutbr.fit.dash.analyses.NgoLayoutAnalysis;
import cz.vutbr.fit.dash.analyses.RasterAnalysis;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.DashboardFile;
import cz.vutbr.fit.dash.util.DashboardFileFilter;
import cz.vutbr.fit.dash.util.MatrixUtils;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel.ColorChannelType;

public class ImageAction extends AbstractAction {
	
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 694051171102731606L;
	
	//public static final int NGO_ANALYSES = 0;

	public static final int RESET = 0;
	public static final int ADAPTIVE1 = 1;
	public static final int ADAPTIVE2 = 2;
	public static final int GRAY_SCALE = 3;
	public static final int POSTERIZE = 4;
	public static final int HISTOGRAM = 5;
	public static final int ANALYSES = 6;
	public static final int HSB_SATURATION = 7;
	public static final int LCH_SATURATION = 8;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;
	
	private int kind;
	
	public ImageAction(int kind) {
		this.kind = kind;
	}
	
	private int askForInteger(String message, String title, int defaultValue) {
		Object input = JOptionPane.showInputDialog(null, 
		        message, 
		        title, 
		        JOptionPane.QUESTION_MESSAGE, null, null, defaultValue
		    );
		int range = defaultValue;
		if(input != null) {
			try {
				range = Integer.parseInt(input.toString());
			} catch(NumberFormatException e) {
				// default value
			}
		}
		return range >= 0 ? range : defaultValue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Surface surface = DashAppGUI.getInstance().getDashboardView().getSurface();
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		
		if(selectedDashboard != null) {
			if(kind == RESET) {
				BufferedImage image = selectedDashboard.getImage();
				surface.updateImage(image);
			} else {
				BufferedImage image = surface.getImage();
				if(image != null) {
					int[][] matrix = MatrixUtils.printBufferedImage(image, selectedDashboard);
					if(kind == ADAPTIVE1) {
						int s = askForInteger("Select s", "Threshold option", 8);
						int t = askForInteger("Select t", "Threshold option", 6);
						MatrixUtils.adaptiveThreshold(matrix, false, s, t, false);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						surface.updateImage(image);
					} else if(kind == ADAPTIVE2) {
						int s = askForInteger("Select s", "Threshold option", 8);
						int t = askForInteger("Select t", "Threshold option", 6);
						MatrixUtils.adaptiveThreshold(matrix, true, s, t, false);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						surface.updateImage(image);
					} else if(kind == GRAY_SCALE) {
						MatrixUtils.grayScale(matrix, false, false);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						surface.updateImage(image);
					} else if(kind == POSTERIZE) {
						int range = askForInteger("color bit width", "Posterization option", 4);
						MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, range)), false);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						surface.updateImage(image);
					} else if(kind == HSB_SATURATION) {
						ColorChannel[][] matrixHSB = MatrixUtils.RGBtoHSB(matrix);
						MatrixUtils.normalizeColorChannel(matrixHSB, matrix, ColorChannelType.SATURATION);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						MatrixUtils.grayScale(matrix, true, false);
						int[] histogram = MatrixUtils.getGrayscaleHistogram(matrix);
						new Histogram(histogram).openWindow();
						surface.updateImage(image);
					} else if(kind == LCH_SATURATION) {
						ColorChannel[][] matrixLCH = MatrixUtils.RGBtoLCH(matrix);
						MatrixUtils.normalizeColorChannel(matrixLCH, matrix, ColorChannelType.SATURATION);
						MatrixUtils.updateBufferedImage(image, matrix, selectedDashboard);
						MatrixUtils.grayScale(matrix, true, false);
						int[] histogram = MatrixUtils.getGrayscaleHistogram(matrix);
						new Histogram(histogram).openWindow();
						surface.updateImage(image);
					} else if(kind == HISTOGRAM) {
						MatrixUtils.grayScale(matrix, true, false);
						int[] histogram = MatrixUtils.getGrayscaleHistogram(matrix);
						new Histogram(histogram).openWindow();
					} else if(kind == ANALYSES) {
						DashAppModel model = DashAppModel.getInstance();
						String path = model.getFolderPath();
						File folder = new File(path);
						if(folder.exists() && folder.isDirectory()) {
							File[] files = folder.listFiles(new DashboardFileFilter());
							Arrays.sort(files);
							if(files != null) {
								List<DashboardFile> dashboardFiles = new ArrayList<>();
								DashboardFile previousDashboardFile = null;
								for (File file : files) {
									String name = file.getName();
									int dotPosition = name.lastIndexOf('.');
									name = name.substring(0, dotPosition);
									if(previousDashboardFile != null && previousDashboardFile.toString().equals(name)) {
										previousDashboardFile.setFile(file);
									} else {
										previousDashboardFile = new DashboardFile(file);
										dashboardFiles.add(previousDashboardFile);
									}
								}
								try {
									Dashboard dashboard;
									model.setListenersDisabled(true);
									String report = "";
									for (DashboardFile dashboardFile : dashboardFiles) {
										dashboard = new Dashboard(model, dashboardFile);
										dashboard.reloadFromFile();
										//report += new ActualAnalysis(selectedDashboard).analyse();
										System.out.println("Analysing " + dashboardFile.toString() + "...");
										report += dashboardFile.toString()+ "\t\t";
										report += new ComplexRasterAnalysis(dashboard).analyse() + "\n";
										//report += "\n\n\n\n=============== " + dashboardFile.toString() + " ===============\n\n";
										//report += new ColorfulnessAnalysis(dashboard).analyse() + "\n";
										//report += new RasterAnalysis(dashboard).analyse() + "\n";
										//report += new ColorAnalysis(dashboard).analyse() + "\n";
										//report += new GrayscaleAnalysis(dashboard).analyse() + "\n";
										//break;
									}
									JFrame frame = new JFrame("Report");
									
									Toolkit toolkit = frame.getToolkit();
									Dimension screenSize = toolkit.getScreenSize();
									frame.setBounds((screenSize.width/2-WIDTH/2), screenSize.height/2-HEIGHT/2, WIDTH, HEIGHT);
									
									JTextArea area = new JTextArea(report);
									area.setEditable(false);
									frame.add(new JScrollPane(area), BorderLayout.CENTER);
									frame.setVisible(true);
								} catch (Exception e1) {
									e1.printStackTrace();
									// no information 
								} finally {
									model.setListenersDisabled(false);
								}
							}
						}
					}
				}
			}
		}
	}
}