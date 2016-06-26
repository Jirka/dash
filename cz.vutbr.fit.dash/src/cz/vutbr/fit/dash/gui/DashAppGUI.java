package cz.vutbr.fit.dash.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import com.java2s.tutorials.verticallabelui.VerticalLabelUI

/**
 * Class which contains frame of application.
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppGUI {
	
	/**
	 * singleton
	 */
	private static DashAppGUI gui;

	/**
	 * singleton
	 * 
	 * @return Dashboard application GUI instance
	 */
	public static DashAppGUI getInstance() {
		if(gui == null) {
			gui = new DashAppGUI();
		}
		return gui;
	}
	
	/**
	 * default width
	 */
	public static final int WIDTH = 900;
	
	/**
	 * default height
	 */
	public static final int HEIGHT = 600;
	
	/**
	 * application name
	 */
	public static final String APP_NAME = "Dashboard analyzer";
	
	/**
	 * top level frame
	 */
	protected JFrame frame;
	
	/**
	 * menu bar
	 */
	private Menu menubar;
	
	/**
	 * tool bar
	 */
	private ToolBar toolbar;
	
	/**
	 * view which contains list of dashboard files stored in particular folder
	 */
	private FolderListView folderListView;
	
	/**
	 * view which contains dashboard file info
	 */
	private FileInfoView fileInfoView;
	
	/**
	 * view which contains XML representation of dashboard
	 */
	private XMLView xmlView;
	
	/**
	 * view which stores dashboard editor
	 */
	private DashboardView dashboardView;
	
	/**
	 * split pane between dashboard editor and views
	 */
	private JSplitPane splitPane;
	
	/**
	 * Method launches GUI.
	 * @wbp.parser.entryPoint
	 */
	public void launchApplication() {
		
		// creates new Frame //
		frame = new JFrame(APP_NAME);
		
		// set position (in the middle of screen) //
		Toolkit toolkit = frame.getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setBounds((screenSize.width/2-WIDTH/2), screenSize.height/2-HEIGHT/2, WIDTH, HEIGHT);
		
		// menu
		menubar = new Menu();
		frame.getContentPane().add(menubar.getComponent(), BorderLayout.NORTH);
		
		// toolbar
		toolbar = new ToolBar();
		frame.getContentPane().add(toolbar.getComponent(), BorderLayout.SOUTH);
		
		// split pane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		// tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
		
		// -- folder
		folderListView = new FolderListView();
		tabbedPane.add("", folderListView.getComponent());
		JLabel labTab = new JLabel("  folder  ");
		labTab.setUI(new VerticalLabelUI(false));
		tabbedPane.setTabComponentAt(0, labTab);
		
		// -- file
		fileInfoView = new FileInfoView();
		tabbedPane.add("", fileInfoView.getComponent());
		labTab = new JLabel("  file  ");
		labTab.setUI(new VerticalLabelUI(false));
		tabbedPane.setTabComponentAt(1, labTab);
		
		// -- xml
		xmlView = new XMLView();
		tabbedPane.add("", xmlView.getComponent());
		labTab = new JLabel("  XML  ");
		labTab.setUI(new VerticalLabelUI(false));
		tabbedPane.setTabComponentAt(2, labTab);
		
		// dashboard view
		dashboardView = new DashboardView();
		
		// add views to split pane
		splitPane.add(dashboardView.getComponent());
		splitPane.add(tabbedPane);
		
		// add to top level frame
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		// complete top level frame //
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // needs to be set after frame is created
        splitPane.setDividerLocation(0.75);
	}
	
	/**
	 * 
	 * @return menu bar
	 */
	public Menu getMenubar() {
		return menubar;
	}
	
	/**
	 * 
	 * @return tool bar
	 */
	public ToolBar getToolbar() {
		return toolbar;
	}
	
	/**
	 * 
	 * @return top level frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * 
	 * @return view which contains XML representation of dashboard
	 */
	public XMLView getXMLView() {
		return xmlView;
	}
	
	/**
	 * 
	 * @return view which stores dashboard editor
	 */
	public DashboardView getDashboardView() {
		return dashboardView;
	}
	
	/**
	 * 
	 * @return split pane between dashboard editor and views
	 */
	public JSplitPane getSplitPane() {
		return splitPane;
	}
}
