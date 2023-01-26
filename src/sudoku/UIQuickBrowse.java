/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import sudoku.MainFrame;

public class UIQuickBrowse extends JFrame {

	private static final long serialVersionUID = -983785287111188756L;

	JPanel importLinesPanel;
	JPanel navigationPanel;
	JComboBox<String> comboBox;
	MainFrame mainFrame;
	ResourceBundle bundle;
	
	public UIQuickBrowse(MainFrame mainFrame) {
		
		super();
		setLayout(new FlowLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(false);
		
		this.mainFrame = mainFrame;
		this.bundle = ResourceBundle.getBundle("intl/UIQuickBrowse");
		
		setTitle(bundle.getString("title"));		
		
		initUI(400, 200);
	}
	
	private void initUI(int w, int h) {
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		
		constraints.gridy = 0;
		importLinesPanel = createImportLinesPanel(w, h);
		panel.add(importLinesPanel, constraints);
		
		constraints.gridy = 1;
		navigationPanel = createNavigationPanel();
		panel.add(navigationPanel, constraints);
		
		add(panel);
		pack();
	}
	
	private String[] splitIntoLines(String text) {
		
		List<String> lines = new ArrayList<String>();
		
		StringReader stringReader = new StringReader(text);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		String line = "";
		
		try {
			while ((line = bufferedReader.readLine()) != null){
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new String[] {};
		}
		
		String[] asArray = new String[lines.size()];
		lines.toArray(asArray);
		return asArray;
	}
	
	private boolean cleanAndValidateLines(String[] lines) {
		
		for (int i = 0; i < lines.length; i++) {
			
			String line = lines[i];
			String[] tokens = line.split(" ");
			String importLine = tokens[0];

			if (MainFrame.ValidateImportLine(importLine)) {
				lines[i] = importLine;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	private void onUpdate(JTextArea textArea, JPanel panel) {
		
		String[] lines = splitIntoLines(textArea.getText());
		
		if (cleanAndValidateLines(lines)) {
			comboBox.removeAllItems();
			comboBox.setModel(new DefaultComboBoxModel<String>(lines));
			navigationPanel.setPreferredSize(new Dimension(panel.getWidth(), navigationPanel.getHeight()));
			mainFrame.loadFromImportLine(comboBox.getItemAt(0));
			repaint();
		}
	}
	
	private JPanel createImportLinesPanel(int w, int h) {
		
		JPanel panel;
		JTextArea textArea;
		JScrollPane textAreaScrollPane;
		JButton buttonLoad;
		JButton buttonImport;
		JButton buttonClear;
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(bundle.getString("import_lines")),
					BorderFactory.createEmptyBorder(0,0,0,0)
				),
				panel.getBorder()
			)
		);
		
		textArea = new JTextArea();
		textArea.setLineWrap(false);
		textAreaScrollPane = new JScrollPane(textArea);
		textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textAreaScrollPane.setPreferredSize(new Dimension(w, h));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 3;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		constraints.gridx = 0;
		panel.add(textAreaScrollPane, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.weightx = 0.3;
		constraints.gridy = 1;
		constraints.insets.top = 10;
		
		buttonLoad = new JButton(bundle.getString("btn_load"));
		buttonLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(null);
				if (choice == JFileChooser.APPROVE_OPTION) {
					
					File file = chooser.getSelectedFile();
					
					try {
						
						byte[] fileBuffer = Files.readAllBytes(file.toPath());
						String text = new String(fileBuffer);
						textArea.setText(text);
						onUpdate(textArea, panel);
						
					} catch (IOException e1) {
						e1.printStackTrace();
						return;
					}
				}
			}			
		});
		
		constraints.gridx = 0;
		panel.add(buttonLoad, constraints);
		buttonImport = new JButton(bundle.getString("btn_import"));
		buttonImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				onUpdate(textArea, panel);
			}
		});
		constraints.gridx = 1;
		panel.add(buttonImport, constraints);
		buttonClear = new JButton(bundle.getString("btn_clear"));
		buttonClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				comboBox.removeAllItems();				
				repaint();
			}			
		});
		constraints.gridx = 2;
		panel.add(buttonClear, constraints);

		return panel;
	}
	
	private void scrollComboBoxIndex(int amount, JComboBox<String> comboBox) {
		
		if (comboBox.getItemCount() < 1 || amount == 0) {
			return;
		}
		
		int index = comboBox.getSelectedIndex() + amount;
		
		if (index < 0) {
			index = 0;
		} else if (index >= comboBox.getItemCount()) {
			index = comboBox.getItemCount()-1;
		}
		
		mainFrame.loadFromImportLine(comboBox.getItemAt(index));
		
		comboBox.setSelectedIndex(index);
	}
	
	private JPanel createNavigationPanel() {
		
		RelativeLayout layout = new RelativeLayout(RelativeLayout.X_AXIS);
		layout.setFill(true);	
		
		JPanel panel = new JPanel();		
		panel.setLayout(layout);
		panel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(bundle.getString("navigation")),
					BorderFactory.createEmptyBorder(0,0,0,0)
				),
				panel.getBorder()
			)
		);
		
		JButton leftBtn = new JButton("<");
		leftBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollComboBoxIndex(-1, comboBox);
			}
		});
		
		comboBox = new JComboBox<String>();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBox.getSelectedIndex() >= 0 && comboBox.getSelectedIndex() < comboBox.getItemCount()) {
					mainFrame.loadFromImportLine(comboBox.getItemAt(comboBox.getSelectedIndex()));
				}
			}			
		});
		comboBox.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollComboBoxIndex(e.getWheelRotation(), comboBox);
			}		
		});
		
		JButton rightBtn = new JButton(">");
		rightBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollComboBoxIndex(1, comboBox);
			}
		});
		
		panel.add(leftBtn);
		panel.add(comboBox);
		panel.add(rightBtn);
		
		return panel;
	}
}
