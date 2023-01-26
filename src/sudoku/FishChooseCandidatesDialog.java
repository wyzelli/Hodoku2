/*
 * Copyright (C) 2008-12  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */
package sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author hobiwan
 */
public class FishChooseCandidatesDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private JCheckBox[] checkBoxes;
	private String fishCandidates;

	/**
	 * Creates new form FishChooseCandidatesDialog
	 * 
	 * @param parent
	 * @param fishCandidates
	 */
	public FishChooseCandidatesDialog(java.awt.Frame parent, String fishCandidates) {
		super(parent, true);
		initComponents();

		this.fishCandidates = fishCandidates;
		checkBoxes = new JCheckBox[] { cand1CheckBox, cand2CheckBox, cand3CheckBox, cand4CheckBox, cand5CheckBox,
				cand6CheckBox, cand7CheckBox, cand8CheckBox, cand9CheckBox };

		setCheckBoxes(fishCandidates);

		getRootPane().setDefaultButton(okButton);

		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		cand1CheckBox = new javax.swing.JCheckBox();
		cand2CheckBox = new javax.swing.JCheckBox();
		cand3CheckBox = new javax.swing.JCheckBox();
		cand4CheckBox = new javax.swing.JCheckBox();
		cand5CheckBox = new javax.swing.JCheckBox();
		cand6CheckBox = new javax.swing.JCheckBox();
		cand7CheckBox = new javax.swing.JCheckBox();
		cand8CheckBox = new javax.swing.JCheckBox();
		cand9CheckBox = new javax.swing.JCheckBox();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		clearAllButton = new javax.swing.JButton();
		setAllButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog"); // NOI18N
		setTitle(bundle.getString("FishChooseCandidatesDialog.title")); // NOI18N

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle.getString("FishChooseCandidatesDialog.jPanel1.border.title"))); // NOI18N

		cand1CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand1CheckBox.mnemonic").charAt(0));
		cand1CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand1CheckBox.text")); // NOI18N

		cand2CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand2CheckBox.mnemonic").charAt(0));
		cand2CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand2CheckBox.text")); // NOI18N

		cand3CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand3CheckBox.mnemonic").charAt(0));
		cand3CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand3CheckBox.text")); // NOI18N

		cand4CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand4CheckBox.mnemonic").charAt(0));
		cand4CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand4CheckBox.text")); // NOI18N

		cand5CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand5CheckBox.mnemonic").charAt(0));
		cand5CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand5CheckBox.text")); // NOI18N

		cand6CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand6CheckBox.mnemonic").charAt(0));
		cand6CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand6CheckBox.text")); // NOI18N

		cand7CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand7CheckBox.mnemonic").charAt(0));
		cand7CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand7CheckBox.text")); // NOI18N

		cand8CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand8CheckBox.mnemonic").charAt(0));
		cand8CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand8CheckBox.text")); // NOI18N

		cand9CheckBox.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cand9CheckBox.mnemonic").charAt(0));
		cand9CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand9CheckBox.text")); // NOI18N

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(cand1CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand2CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand3CheckBox))
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(cand4CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand5CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand6CheckBox))
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(cand7CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand8CheckBox)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(cand9CheckBox)))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cand1CheckBox).addComponent(cand2CheckBox).addComponent(cand3CheckBox))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cand4CheckBox).addComponent(cand5CheckBox).addComponent(cand6CheckBox))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cand7CheckBox).addComponent(cand8CheckBox).addComponent(cand9CheckBox))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		okButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("okButton.mnemonic").charAt(0));
		okButton.setText(bundle.getString("FishChooseCandidatesDialog.okButton.text")); // NOI18N
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		cancelButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("cancelButton.mnemonic").charAt(0));
		cancelButton.setText(bundle.getString("FishChooseCandidatesDialog.cancelButton.text")); // NOI18N
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		clearAllButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("clearAllButton.mnemonic").charAt(0));
		clearAllButton.setText(bundle.getString("FishChooseCandidatesDialog.clearAllButton.text")); // NOI18N
		clearAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearAllButtonActionPerformed(evt);
			}
		});

		setAllButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/FishChooseCandidatesDialog")
				.getString("setAllButton.mnemonic").charAt(0));
		setAllButton.setText(bundle.getString("FishChooseCandidatesDialog.setAllButton.text")); // NOI18N
		setAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setAllButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap(76, Short.MAX_VALUE).addComponent(okButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton)
						.addContainerGap())
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(clearAllButton).addComponent(setAllButton))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { cancelButton, okButton });

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { clearAllButton, setAllButton });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
										layout.createSequentialGroup().addGap(5, 5, 5).addComponent(setAllButton)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(clearAllButton)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(okButton).addComponent(cancelButton))
						.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
		String result = "";
		for (int i = 0; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isSelected()) {
				result += "1";
			} else {
				result += "0";
			}
		}
		fishCandidates = result;
		setVisible(false);
	}// GEN-LAST:event_okButtonActionPerformed

	private void setAllButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setAllButtonActionPerformed
		setCheckBoxes("111111111");
	}// GEN-LAST:event_setAllButtonActionPerformed

	private void clearAllButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clearAllButtonActionPerformed
		setCheckBoxes("000000000");
	}// GEN-LAST:event_clearAllButtonActionPerformed

	private void setCheckBoxes(String values) {
		for (int i = 0; i < checkBoxes.length; i++) {
			boolean check = false;
			if (values.length() > i && values.charAt(i) == '1') {
				check = true;
			}
			checkBoxes[i].setSelected(check);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	/*
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				FishChooseCandidatesDialog dialog = new FishChooseCandidatesDialog(new javax.swing.JFrame(),
						"101010101");
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}*/

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JCheckBox cand1CheckBox;
	private javax.swing.JCheckBox cand2CheckBox;
	private javax.swing.JCheckBox cand3CheckBox;
	private javax.swing.JCheckBox cand4CheckBox;
	private javax.swing.JCheckBox cand5CheckBox;
	private javax.swing.JCheckBox cand6CheckBox;
	private javax.swing.JCheckBox cand7CheckBox;
	private javax.swing.JCheckBox cand8CheckBox;
	private javax.swing.JCheckBox cand9CheckBox;
	private javax.swing.JButton clearAllButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton okButton;
	private javax.swing.JButton setAllButton;
	// End of variables declaration//GEN-END:variables

	public String getFishCandidates() {
		return fishCandidates;
	}

}
