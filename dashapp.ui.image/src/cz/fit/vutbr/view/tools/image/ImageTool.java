package cz.fit.vutbr.view.tools.image;

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
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.fit.vutbr.view.util.Histogram;
import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.ColorChannelUtils;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardFileFilter;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import extern.AdaptiveThreshold;
import cz.vutbr.fit.dashapp.view.Canvas;

public class ImageTool extends AbstractGUITool implements IGUITool {

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Image");
		menuBar.addItem(subMenu, "Reset", new ImageAction(ImageAction.RESET));
		menuBar.addItem(subMenu, "Adaptive 1", new ImageAction(ImageAction.ADAPTIVE1));
		menuBar.addItem(subMenu, "Adaptive 2", new ImageAction(ImageAction.ADAPTIVE2));
		menuBar.addItem(subMenu, "Gray", new ImageAction(ImageAction.GRAY_SCALE));
		menuBar.addItem(subMenu, "Posterize", new ImageAction(ImageAction.POSTERIZE));
		menuBar.addItem(subMenu, "HSB Saturation", new ImageAction(ImageAction.HSB_SATURATION));
		menuBar.addItem(subMenu, "LCH Saturation", new ImageAction(ImageAction.LCH_SATURATION));
		menuBar.addItem(subMenu, "Histogram", new ImageAction(ImageAction.HISTOGRAM));
		menuBar.addItem(subMenu, "Make raster reports", new ImageAction(ImageAction.RASTER_ANALYSIS));
		menuBar.addItem(subMenu, "Make widget reports", new ImageAction(ImageAction.WIDGET_ANALYSIS));
	}
	
	/**
	 * TODO
	 * 
	 * @author Jiri Hynek
	 *
	 */
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
		public static final int HSB_SATURATION = 5;
		public static final int LCH_SATURATION = 6;
		public static final int HISTOGRAM = 7;
		public static final int RASTER_ANALYSIS = 8;
		public static final int WIDGET_ANALYSIS = 9;
		
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
			
			Canvas surface = DashAppView.getInstance().getDashboardView().getCanvas();
			DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
			
			if(selectedDashboardFile != null) {
				if(kind == RESET) {
					BufferedImage image = selectedDashboardFile.getImage();
					surface.updateImage(image, true, true);
				} else {
					BufferedImage image = surface.getImage();
					Dashboard dashboard = selectedDashboardFile.getDashboard(false);
					if(image != null) {
						int[][] matrix = ColorMatrix.printImageToMatrix(image, dashboard);
						if(kind == ADAPTIVE1) {
							int s = askForInteger("Select s", "Threshold option", 8);
							int t = askForInteger("Select t", "Threshold option", 6);
							AdaptiveThreshold.adaptiveThreshold(matrix, false, s, t, false);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							surface.updateImage(image, true, true);
						} else if(kind == ADAPTIVE2) {
							int s = askForInteger("Select s", "Threshold option", 8);
							int t = askForInteger("Select t", "Threshold option", 6);
							AdaptiveThreshold.adaptiveThreshold(matrix, true, s, t, false);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							surface.updateImage(image, true, true);
						} else if(kind == GRAY_SCALE) {
							ColorMatrix.toGrayScale(matrix, false, false);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							surface.updateImage(image, true, true);
						} else if(kind == POSTERIZE) {
							int range = askForInteger("color bit width", "Posterization option", 4);
							PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, range)), false);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							surface.updateImage(image, true, true);
						} else if(kind == HSB_SATURATION) {
							ColorSpace[][] matrixHSB = HSB.fromRGB(matrix);
							ColorChannelUtils.normalizeColorChannel(matrixHSB, matrix, HSB.CHANNEL_SATURATION);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							ColorMatrix.toGrayScale(matrix, true, false);
							int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
							new Histogram(histogram).openWindow();
							surface.updateImage(image, true, true);
						} else if(kind == LCH_SATURATION) {
							ColorSpace[][] matrixLCH = CIE.fromRGB(matrix);
							ColorChannelUtils.normalizeColorChannel(matrixLCH, matrix, CIE.CHANNEL_SATURATION);
							ColorMatrix.printMatrixToImage(image, matrix, dashboard);
							ColorMatrix.toGrayScale(matrix, true, false);
							int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
							new Histogram(histogram).openWindow();
							surface.updateImage(image, true, true);
						} else if(kind == HISTOGRAM) {
							ColorMatrix.toGrayScale(matrix, true, false);
							int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
							new Histogram(histogram).openWindow();
						} else if(kind == RASTER_ANALYSIS || kind == WIDGET_ANALYSIS) {
							DashAppModel model = DashAppModel.getInstance();
							File folder = model.getWorkspaceFolder().getFile();
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
											previousDashboardFile = new DashboardFile(model, file);
											dashboardFiles.add(previousDashboardFile);
										}
									}
									DashAppController controller = DashAppController.getInstance();
									try {
										DashAppController.getInstance().setListenersDisabled(true);
										String report = "";
										for (DashboardFile dashboardFile : dashboardFiles) {
											dashboard = dashboardFile.getDashboard(true);
											//report += new ActualAnalysis(selectedDashboard).analyse();
											System.out.println("Analysing " + dashboardFile.toString() + "...");
											report += dashboardFile.toString()+ "\t\t";
											if(kind == RASTER_ANALYSIS) {
												// TODO
												//report += new ComplexRasterAnalysis(dashboard).analyse() + "\n";
											} else if(kind == WIDGET_ANALYSIS) {
												// TODO
												//report += new ComplexWidgetAnalysis(dashboard).analyse() + "\n";
											}
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
										controller.setListenersDisabled(false);
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
