package cz.vutbr.fit.dash.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.apache.batik.util.gui.xmleditor.XMLTextEditor;

import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.PropertyChangeEvent;
import cz.vutbr.fit.dash.model.PropertyChangeListener;

public class XMLView implements IComponent, PropertyChangeListener, KeyListener, ActionListener {
	
	//private JTextArea text;
	private XMLTextEditor editor;
	private Timer timer;
	private Map<Dashboard, History> historyCache;
	private int HISTORY_LIMIT = 20;
	private boolean currentlyTyping;
	private JScrollPane scrollPane;

	public XMLView() {
		initGUI();
		initListeners();
		
		historyCache = new HashMap<Dashboard, XMLView.History>();
	}

	private void initGUI() {
		// text field
		editor = new XMLTextEditor();//new JTextArea();
		scrollPane = new JScrollPane(editor);
	}

	private void initListeners() {
		DashAppModel.getInstance().addPropertyChangeListener(this);
		
		timer = new Timer(1000, this);
		editor.addKeyListener(this);
	}

	@Override
	public JComponent getComponent() {
		return scrollPane;
	}
	
	public void updateModel(Dashboard selectedDashboard) {
		// text changed -> deserialize -> update model -> model event
		currentlyTyping = true;
		try {
			// update serialized dashboard
			selectedDashboard.getSerializedDashboard().setXml(editor.getText(), true);
			// update model
			selectedDashboard.deserialize(true);
			// successful deserialization
			setEditorStatus(true);
		} catch (Exception e) {
			// unsuccessful deserialization
			setEditorStatus(false);
		} finally {
			currentlyTyping = false;
		}
	}
	
	public void updateEditor(Dashboard selectedDashboard) {
		String xml = selectedDashboard.getSerializedDashboard().getXml();
		String oldXml = editor.getText();
		boolean sameXML = oldXml.equals(xml);
		if(!sameXML) {
			History history = getHistory(selectedDashboard);
			history.save(oldXml);
			editor.setText(xml);
		}
	}

	private History getHistory(Dashboard selectedDashboard) {
		History history = historyCache.get(selectedDashboard);
		if(history == null) {
			history = new History(HISTORY_LIMIT);
			historyCache.put(selectedDashboard, history);
		}
		return history;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == PropertyKind.XML || e.propertyKind == PropertyKind.DASHBOARD_SELECTION) {
			Dashboard selectedDasboard = DashAppModel.getInstance().getSelectedDashboard();
			if(selectedDasboard != null) {
				// this is one way how to update text editor
				// workflow 1: change of model -> serialization -> xml change event -> proper editor text update
				if(!currentlyTyping) {
					updateEditor(selectedDasboard);
				}
				// else - workflow 2: text input -> proper editor text update -> change of serialized XML -> model change event
			}
		} else if(e.propertyKind == PropertyKind.DASHBOARD_ELEMENTS || e.propertyKind == PropertyKind.GRAPHICAL_ELEMENT) {
			Dashboard selectedDasboard = DashAppModel.getInstance().getSelectedDashboard();
			// prevent loop (input text change -> deserialization -> change of model -> serialization -> input text change)
			// prevent double change (reload from file -> change of xml -> deserialization -> change of model
			//                        -> serialization -> same input text change)
			if(!currentlyTyping && !selectedDasboard.isReloadingFromFile()) {
				try {
					// model has not been changed by editor (needs serialization and editor refresh)
					selectedDasboard.serialize();
				} catch (Exception e1) {
					// this can be caused only by some bug in model
					e1.printStackTrace();
				}
			}
		}
	}

	public void undo() {
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		History history = getHistory(selectedDashboard);
		if(history != null) {
			editor.setText(history.undo(editor.getText()));
			// update model
			updateModel(selectedDashboard);
		}
	}
	
	public void redo() {
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		History history = getHistory(selectedDashboard);
		if(history != null) {
			editor.setText(history.redo(editor.getText()));
			// update model
			updateModel(selectedDashboard);
		}
	}

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void setEditorStatus(boolean isOK) {
		if(isOK) {
			editor.setBackground(Color.WHITE);
		} else {
			editor.setForeground(Color.RED);
			editor.setBackground(Color.RED);
			editor.repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer) {
			timer.stop();
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			if(selectedDashboard != null) {
				// store change in history
				History history = getHistory(selectedDashboard);
				history.save(selectedDashboard.getSerializedDashboard().getXml());
				// update serialized dashboard
				updateModel(selectedDashboard);
			}
		}
	}
	
	private static class History {
		private Stack<String> undo;
		private Stack<String> redo;
		private int limit;
		
		public History(int limit) {
			this.limit = limit;
			undo = new Stack<String>();
			redo = new Stack<String>();
		}
		
		public boolean canUndo() {
			return !undo.isEmpty();
		}
		
		public boolean canRedo() {
			return !redo.isEmpty();
		}
		
		public void save(String text) {
			undo.push(text);
			if(undo.size() > limit) {
				// TODO
			}
			redo.clear();
		}
		
		public String undo(String actualString) {
			if(canUndo()) {
				redo.push(actualString);
				return undo.pop();
			} else {
				return actualString;
			}
			
		}
		
		public String redo(String actualString) {
			if(canRedo()) {
				undo.push(actualString);
				return redo.pop();
			} else {
				return actualString;
			}
		}
	}

}
