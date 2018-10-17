package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.apache.batik.util.gui.xmleditor.XMLTextEditor;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.view.IComponent;
import cz.vutbr.fit.dashapp.view.SideBar;

/**
 * View which contains XML representation of dashboard.
 * 
 * @author Jiri Hynek
 *
 */
public class XMLTool extends AbstractGUITool implements IGUITool, IComponent, IPropertyChangeListener {
	
	/**
	 * Main GUI component.
	 */
	private JScrollPane scrollPane;
	
	@Override
	public JComponent getComponent() {
		return scrollPane;
	}
	
	/**
	 * Text editor
	 */
	private XMLTextEditor editor;
	//private JTextArea text;
	
	/**
	 * Timer which monitors whether user finishes typing.
	 */
	private Timer timer;
	
	/**
	 * Lock which indicates whether user is currently typing.
	 */
	private boolean currentlyTyping;
	
	/**
	 * Timer time.
	 */
	private static final int TYPING_BREAK_TIME = 1000;

	/**
	 * Initializes XML tool.
	 */
	public XMLTool() {
		initGUI();
		initListeners();
	}

	/**
	 * Initializes GUI
	 */
	private void initGUI() {
		// construct XML editor inside scroll pane
		editor = new XMLTextEditor();//new JTextArea();
		scrollPane = new JScrollPane(editor);
	}

	/**
	 * Initializes listeners
	 */
	private void initListeners() {
		// register model change listener
		DashAppController.getInstance().addPropertyChangeListener(this);
		// initialize timer
		timer = new Timer(TYPING_BREAK_TIME, new TypingTimerListener());
		// initialize typing listener
		editor.addKeyListener(new TypingListener());
	}
	
	@Override
	public void provideSidebarItems(SideBar sideBar) {
		sideBar.addItem("XML", getComponent());
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.FILE_SELECTION_CHANGED ||
				e.propertyKind == EventKind.XML_FORMATTED ||
				EventKind.isModelChanged(e)) {
			IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
			if(selectedFile != null && selectedFile instanceof DashboardFile && selectedFile == e.selectedFile) {
				// this is one way how to update text editor
				// workflow 1: change of model -> serialization -> xml change event -> proper editor text update
				if(!currentlyTyping) {
					updateEditor((DashboardFile) selectedFile);
				}
				// else - workflow 2: text input -> proper editor text update -> change of serialized XML -> model change event
			} else {
				editor.setText("");
			}
		}
	}
	
	/**
	 * Takes model and updates XML.
	 * 
	 * @param selectedDashboard
	 */
	public void updateEditor(DashboardFile selectedDashboard) {
		String xml = selectedDashboard.getSerializedDashboard().getXml();
		String oldXml = editor.getText();
		boolean sameXML = oldXml.equals(xml);
		if(!sameXML) {
			editor.setText(xml);
		}
	}
	
	private class TypingListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// restore timer
			if(!timer.isRunning()) {
				timer.start();
			} else {
				timer.restart();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	/**
	 * Listener which handles event which is invoked by user when he finishes typing.  
	 * 
	 * @author Jiri Hynek
	 *
	 */
	private class TypingTimerListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == timer) {
				timer.stop();
				IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
				if(selectedFile != null && selectedFile instanceof DashboardFile) {
					// update serialized dashboard
					updateModel((DashboardFile) selectedFile);
				}
			}
		}
		
		/**
		 * Takes new XML defined by user and updates model.
		 * 
		 * @param selectedDashboard
		 */
		public void updateModel(DashboardFile selectedDashboard) {
			// text changed -> deserialize -> update model -> model event
			currentlyTyping = true;
			try {
				// update model
				DashAppController.getEventManager().updateDashboardXml(selectedDashboard, editor.getText());
				// successful deserialization
				setEditorStatus(true);
			} catch (Exception e) {
				// unsuccessful deserialization
				setEditorStatus(false);
			} finally {
				currentlyTyping = false;
			}
		}
		
		/**
		 * Updates editor status.
		 * 
		 * @param isOK
		 */
		private void setEditorStatus(boolean isOK) {
			if(isOK) {
				editor.setBackground(Color.WHITE);
			} else {
				editor.setForeground(Color.RED);
				editor.setBackground(Color.RED);
				editor.repaint();
			}
		}
	}
}
