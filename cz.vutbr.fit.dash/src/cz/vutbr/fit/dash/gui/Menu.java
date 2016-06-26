package cz.vutbr.fit.dash.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cz.vutbr.fit.dash.actions.HistoryAction;
import cz.vutbr.fit.dash.actions.ImageAction;
import cz.vutbr.fit.dash.actions.OpenFolderAction;
import cz.vutbr.fit.dash.actions.WidgetAction;
import cz.vutbr.fit.dash.actions.XMLAction;
import cz.vutbr.fit.dash.actions.ZoomAction;
import cz.vutbr.fit.dash.model.DashAppModel.WidgetActionKind;

/**
 * Class for menu.
 * 
 * @author jurij
 *
 */
public class Menu implements IComponent {
	
	private JMenuBar menuBar;
	
	/**
	 * Creates menu.
	 */
	public Menu() {
	    
		menuBar = new JMenuBar();
        //menuBar.setBackground(Color.GRAY);

        // 1.) FILE //
        JMenu fileMenu = new JMenu("File");
        
        addItem(fileMenu, "Open", new OpenFolderAction());
        addItem(fileMenu, "Refresh", new XMLAction(XMLAction.REFRESH));
        addItem(fileMenu, "Save", new XMLAction(XMLAction.SAVE));
        addItem(fileMenu, "Save all", new XMLAction(XMLAction.SAVE_ALL));
        addItem(fileMenu, "Exit", new ExitAction());
        menuBar.add(fileMenu);
        
        // 2.) VIEW //
        JMenu viewMenu = new JMenu("View");
        addItem(viewMenu, "Zoom in", new ZoomAction(ZoomAction.ZOOM_IN));
        addItem(viewMenu, "Zoom out", new ZoomAction(ZoomAction.ZOOM_OUT));
        menuBar.add(viewMenu);
        
        // 3.) EDIT //
        JMenu editMenu = new JMenu("Edit");
        
        addItem(editMenu, "Undo", new HistoryAction(HistoryAction.UNDO));
        addItem(editMenu, "Redo", new HistoryAction(HistoryAction.REDO));
        editMenu.addSeparator();
        addItem(editMenu, "Select", new WidgetAction(WidgetActionKind.SELECT));
        addItem(editMenu, "Insert", new WidgetAction(WidgetActionKind.INSERT));
        addItem(editMenu, "Bound", new WidgetAction(WidgetActionKind.BOUND));
        menuBar.add(editMenu);
        
        // 4.) IMAGE //
        JMenu imageMenu = new JMenu("Image");
        
        addItem(imageMenu, "Reset", new ImageAction(ImageAction.RESET));
        addItem(imageMenu, "Adaptive 1", new ImageAction(ImageAction.ADAPTIVE1));
        addItem(imageMenu, "Adaptive 2", new ImageAction(ImageAction.ADAPTIVE2));
        addItem(imageMenu, "Gray", new ImageAction(ImageAction.GRAY_SCALE));
        addItem(imageMenu, "Posterize", new ImageAction(ImageAction.POSTERIZE));
        addItem(imageMenu, "HSB Saturation", new ImageAction(ImageAction.HSB_SATURATION));
        addItem(imageMenu, "LCH Saturation", new ImageAction(ImageAction.LCH_SATURATION));
        addItem(imageMenu, "Histogram", new ImageAction(ImageAction.HISTOGRAM));
        addItem(imageMenu, "Make reports", new ImageAction(ImageAction.ANALYSES));
        menuBar.add(imageMenu);

    }

	private JMenuItem addItem(JMenu fileMenu, String string, AbstractAction action) {
		JMenuItem menuItem = fileMenu.add(string);
        menuItem.addActionListener(action);
        return menuItem;
	}

	@Override
    public JComponent getComponent() {
	    return menuBar;
    }

	@SuppressWarnings("serial")
	private static class ExitAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
		
	}
}
