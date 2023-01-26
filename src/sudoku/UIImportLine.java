/**
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20 PseudoFish
 */

package sudoku;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UIImportLine extends JFrame implements ActionListener, WindowListener, KeyListener {

	private static final long serialVersionUID = 8648013595000698919L;

	private JLabel label;
	private JTextField textField;
	private JButton okButton;
	private MainFrame mainFrame;

	public UIImportLine(MainFrame mainFrame) {

		super();
		
		ResourceBundle bundle = ResourceBundle.getBundle("intl/UIImportLine");

		this.setVisible(false);
		this.setLocation(200, 200);
		this.setLayout(new FlowLayout());
		this.setTitle(bundle.getString("UIImportLine.title"));
		this.setResizable(false);

		this.mainFrame = mainFrame;

		label = new JLabel(bundle.getString("UIImportLine.label.text"));
		this.add(label, FlowLayout.LEFT);

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 32));
		textField.addKeyListener(this);
		this.add(textField, FlowLayout.CENTER);

		okButton = new JButton(bundle.getString("UIImportLine.okButton.text"));
		okButton.addActionListener(this);
		this.add(okButton, FlowLayout.RIGHT);

		this.addWindowListener(this);

		this.revalidate();
		this.pack();
		this.repaint();
	}

	public void focusCursor() {
		this.textField.requestFocus();
	}

	private void importLine() {
		
		boolean wasImported = this.mainFrame.loadFromImportLine(this.textField.getText());
		
		if (wasImported) {
			this.textField.setText("");
			this.setVisible(false);
			this.mainFrame.repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {
			importLine();
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.textField.setText("");
		this.setVisible(false);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			importLine();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
