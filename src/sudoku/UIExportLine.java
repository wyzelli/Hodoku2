/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UIExportLine extends JFrame implements ActionListener {

	private static final long serialVersionUID = 2164682681182463141L;

	private JLabel label;
	private JTextField textField;
	private JButton copyButton;
	private SudokuPanel sudokuPanel;

	public UIExportLine(SudokuPanel sudokuPanel) {

		super();

		this.sudokuPanel = sudokuPanel;
		
		ResourceBundle bundle = ResourceBundle.getBundle("intl/UIExportLine");

		this.setVisible(false);
		this.setLocation(200, 200);
		this.setLayout(new FlowLayout());
		this.setTitle(bundle.getString("UIExportLine.title"));
		this.setResizable(false);
		
		label = new JLabel(bundle.getString("UIExportLine.label.text"));
		this.add(label, FlowLayout.LEFT);

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 32));
		this.add(textField, FlowLayout.CENTER);

		copyButton = new JButton(bundle.getString("UIExportLine.copyButton.text"));
		copyButton.addActionListener(this);
		this.add(copyButton, FlowLayout.RIGHT);

		this.revalidate();
		this.pack();
		this.repaint();
	}

	@Override
	public void setVisible(boolean isVisible) {

		super.setVisible(isVisible);

		if (isVisible) {
			//String line = sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
			String line = sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY);
			this.textField.setText(line);
			this.textField.selectAll();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == copyButton) {

			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(this.textField.getText());
			clipboard.setContents(stringSelection, null);

			this.textField.setText("");
			this.setVisible(false);
		}
	}
}
