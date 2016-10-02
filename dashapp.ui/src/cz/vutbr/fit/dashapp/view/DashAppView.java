package cz.vutbr.fit.dashapp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import cz.vutbr.fit.dashapp.view.config.IViewConfiguration;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * Class which contains frame of application.
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppView {
	
	/**
	 * singleton
	 */
	private static DashAppView gui;

	/**
	 * singleton
	 * 
	 * @return Dashboard application GUI instance
	 */
	public static DashAppView getInstance() {
		if(gui == null) {
			gui = new DashAppView();
		}
		return gui;
	}
	
	/**
	 * GUI settings
	 */
	private IViewConfiguration viewConfiguration;
	
	/**
	 * top level frame
	 */
	protected JFrame frame;
	
	/**
	 * menu bar
	 */
	private MenuBar menubar;
	
	/**
	 * tool bar
	 */
	private ToolBar toolbar;
	
	/**
	 * side bar
	 */
	private SideBar sidebar;
	
	/**
	 * view which stores dashboard editor
	 */
	private ScreenPanel dashboardView;
	
	/**
	 * split pane between dashboard editor and views
	 */
	private JSplitPane splitPane;
	
	/**
	 * Method launches GUI.
	 * @wbp.parser.entryPoint
	 */
	public void launchApplication(IViewConfiguration viewConfiguration) {
		this.viewConfiguration = viewConfiguration;		
		List<IGUITool> plugins = viewConfiguration.getGUITools();
		
		// creates new Frame //
		frame = new JFrame(viewConfiguration.getAppName());
		
		// set position (in the middle of screen) //
		Toolkit toolkit = frame.getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setBounds((screenSize.width/2-viewConfiguration.getWidth()/2), screenSize.height/2-viewConfiguration.getHeight()/2,
				viewConfiguration.getWidth(), viewConfiguration.getHeight());
		
		// menu
		menubar = new MenuBar(plugins);
		frame.getContentPane().add(menubar.getComponent(), BorderLayout.NORTH);
		
		// toolbar
		toolbar = new ToolBar(plugins);
		frame.getContentPane().add(toolbar.getComponent(), BorderLayout.SOUTH);
		
		// split pane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		// tabbed pane
		sidebar = new SideBar(plugins);
		
		// dashboard view
		dashboardView = new ScreenPanel(plugins);
		
		// add views to split pane
		splitPane.add(dashboardView.getComponent());
		splitPane.add(sidebar.getComponent());
		
		// add to top level frame
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		// complete top level frame //
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // needs to be set after frame is created
        splitPane.setDividerLocation(0.75);
	}
	
	public IViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
	
	/**
	 * 
	 * @return menu bar
	 */
	public MenuBar getMenubar() {
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
	 * @return view which stores dashboard editor
	 */
	public ScreenPanel getDashboardView() {
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
