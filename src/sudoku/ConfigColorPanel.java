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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.UIManager;

/**
 *
 * @author hobiwan
 */
public class ConfigColorPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1L;

	private JButton[] buttons = null;
	private Color[] colors = null;
	
	private javax.swing.JButton alsHintBGButton1;
	private javax.swing.JButton alsHintBGButton2;
	private javax.swing.JButton alsHintBGButton3;
	private javax.swing.JButton alsHintBGButton4;
	private javax.swing.JButton alsHintFGButton1;
	private javax.swing.JButton alsHintFGButton2;
	private javax.swing.JButton alsHintFGButton3;
	private javax.swing.JButton alsHintFGButton4;
	private javax.swing.JLabel alsLabel1;
	private javax.swing.JLabel alsLabel2;
	private javax.swing.JLabel alsLabel3;
	private javax.swing.JLabel alsLabel4;
	private javax.swing.JButton alternateBGButton;
	private javax.swing.JLabel alternateBGLabel;
	private javax.swing.JButton arrowButton;
	private javax.swing.JLabel arrowLabel;
	private javax.swing.JPanel backGroundPanel;
	private javax.swing.JButton candidatesButton;
	private javax.swing.JLabel candidatesLabel;
	private javax.swing.JButton cannibalisticHintBGButton;
	private javax.swing.JLabel cannibalisticHintBGLabel;
	private javax.swing.JButton cannibalisticHintFGButton;
	private javax.swing.JButton cluesButton;
	private javax.swing.JLabel cluesLabel;
	private javax.swing.JPanel coloringPanel;
	private javax.swing.JButton cursorBGButton;
	private javax.swing.JLabel cursorBGLabel;
	private javax.swing.JButton delHintBGButton;
	private javax.swing.JLabel delHintBGLabel;
	private javax.swing.JButton delHintFGButton;
	private javax.swing.JButton endoFinsHintBGButton;
	private javax.swing.JLabel endoFinsHintBGLabel;
	private javax.swing.JButton endoFinsHintFGButton;
	private javax.swing.JButton finsHintBGButton;
	private javax.swing.JLabel finsHintBGLabel;
	private javax.swing.JButton finsHintFGButton;
	private javax.swing.JPanel frameAndDigitsPanel;
	private javax.swing.JLabel frameLabel;
	private javax.swing.JButton frameStrongButton;
	private javax.swing.JButton frameWeakButton;
	private javax.swing.JPanel hintPanel;
	private javax.swing.JButton invalidBGButton;
	private javax.swing.JLabel invalidBGLabel;
	private javax.swing.JButton invalidFGButton;
	private javax.swing.JLabel invalidFGLabel;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JLabel normHintBGLabel;
	private javax.swing.JButton normalBGButton;
	private javax.swing.JLabel normalBGLabel;
	private javax.swing.JButton normalHintBGButton;
	private javax.swing.JButton normalHintFGButton;
	private javax.swing.JButton resetButton;
	private javax.swing.JButton stiftButtonA;
	private javax.swing.JButton stiftButtonB;
	private javax.swing.JButton stiftButtonC;
	private javax.swing.JButton stiftButtonD;
	private javax.swing.JButton stiftButtonE;
	private javax.swing.JButton stiftButtonF;
	private javax.swing.JButton stiftButtona;
	private javax.swing.JButton stiftButtonb;
	private javax.swing.JButton stiftButtonc;
	private javax.swing.JButton stiftButtond;
	private javax.swing.JButton stiftButtone;
	private javax.swing.JButton stiftButtonf;
	private javax.swing.JLabel stiftLabelA;
	private javax.swing.JLabel stiftLabelB;
	private javax.swing.JLabel stiftLabelC;
	private javax.swing.JLabel stiftLabelD;
	private javax.swing.JLabel stiftLabelE;
	private javax.swing.JLabel stiftLabelF;
	private javax.swing.JButton validBGButton;
	private javax.swing.JLabel validBGLabel;
	private javax.swing.JButton filterDigitBGButton;
	private javax.swing.JLabel filterDigitBGButtonLabel;
	private javax.swing.JButton valuesButton;
	private javax.swing.JLabel valuesLabel;
	private javax.swing.JButton wrongButton;
	private javax.swing.JLabel wrongLabel;

	/** Creates new form ConfigColorPanel */
	public ConfigColorPanel() {
		
		initComponents();

		buttons = new JButton[] { 
			frameStrongButton, frameWeakButton, cluesButton, valuesButton, candidatesButton,
			invalidFGButton, wrongButton, normalBGButton, cursorBGButton, invalidBGButton, validBGButton, 
			normalHintFGButton, normalHintBGButton, delHintFGButton, delHintBGButton, 
			finsHintFGButton, finsHintBGButton, endoFinsHintFGButton, endoFinsHintBGButton, cannibalisticHintFGButton,
			cannibalisticHintBGButton, arrowButton, alsHintFGButton1, alsHintBGButton1, alsHintFGButton2,
			alsHintBGButton2, alsHintFGButton3, alsHintBGButton3, alsHintFGButton4, alsHintBGButton4, stiftButtona,
			stiftButtonA, stiftButtonb, stiftButtonB, stiftButtonc, stiftButtonC, stiftButtond, stiftButtonD,
			stiftButtone, stiftButtonE, alternateBGButton, stiftButtonf, stiftButtonF, filterDigitBGButton
		};

		initAll(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		frameAndDigitsPanel = new javax.swing.JPanel();
		frameLabel = new javax.swing.JLabel();
		cluesLabel = new javax.swing.JLabel();
		valuesLabel = new javax.swing.JLabel();
		candidatesLabel = new javax.swing.JLabel();
		invalidFGLabel = new javax.swing.JLabel();
		wrongLabel = new javax.swing.JLabel();
		frameStrongButton = new javax.swing.JButton();
		frameWeakButton = new javax.swing.JButton();
		cluesButton = new javax.swing.JButton();
		valuesButton = new javax.swing.JButton();
		candidatesButton = new javax.swing.JButton();
		invalidFGButton = new javax.swing.JButton();
		wrongButton = new javax.swing.JButton();
		backGroundPanel = new javax.swing.JPanel();
		normalBGLabel = new javax.swing.JLabel();
		cursorBGLabel = new javax.swing.JLabel();
		invalidBGLabel = new javax.swing.JLabel();
		validBGLabel = new javax.swing.JLabel();
		filterDigitBGButtonLabel = new javax.swing.JLabel();
		normalBGButton = new javax.swing.JButton();
		cursorBGButton = new javax.swing.JButton();
		invalidBGButton = new javax.swing.JButton();
		validBGButton = new javax.swing.JButton();
		filterDigitBGButton = new javax.swing.JButton();
		alsLabel4 = new javax.swing.JLabel();
		alsHintFGButton4 = new javax.swing.JButton();
		alsHintBGButton4 = new javax.swing.JButton();
		alsHintBGButton3 = new javax.swing.JButton();
		alsHintFGButton3 = new javax.swing.JButton();
		alsLabel3 = new javax.swing.JLabel();
		alsLabel2 = new javax.swing.JLabel();
		alsHintFGButton2 = new javax.swing.JButton();
		alsHintBGButton2 = new javax.swing.JButton();
		alsHintBGButton1 = new javax.swing.JButton();
		alsHintFGButton1 = new javax.swing.JButton();
		alsLabel1 = new javax.swing.JLabel();
		alternateBGLabel = new javax.swing.JLabel();
		alternateBGButton = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		hintPanel = new javax.swing.JPanel();
		normHintBGLabel = new javax.swing.JLabel();
		delHintBGLabel = new javax.swing.JLabel();
		finsHintBGLabel = new javax.swing.JLabel();
		endoFinsHintBGLabel = new javax.swing.JLabel();
		cannibalisticHintBGLabel = new javax.swing.JLabel();
		arrowLabel = new javax.swing.JLabel();
		normalHintFGButton = new javax.swing.JButton();
		delHintFGButton = new javax.swing.JButton();
		finsHintFGButton = new javax.swing.JButton();
		endoFinsHintFGButton = new javax.swing.JButton();
		cannibalisticHintFGButton = new javax.swing.JButton();
		arrowButton = new javax.swing.JButton();
		normalHintBGButton = new javax.swing.JButton();
		delHintBGButton = new javax.swing.JButton();
		finsHintBGButton = new javax.swing.JButton();
		endoFinsHintBGButton = new javax.swing.JButton();
		cannibalisticHintBGButton = new javax.swing.JButton();
		coloringPanel = new javax.swing.JPanel();
		stiftLabelA = new javax.swing.JLabel();
		stiftButtona = new javax.swing.JButton();
		stiftButtonA = new javax.swing.JButton();
		stiftButtonB = new javax.swing.JButton();
		stiftButtonb = new javax.swing.JButton();
		stiftLabelB = new javax.swing.JLabel();
		stiftLabelC = new javax.swing.JLabel();
		stiftButtonc = new javax.swing.JButton();
		stiftButtonC = new javax.swing.JButton();
		stiftButtonD = new javax.swing.JButton();
		stiftButtond = new javax.swing.JButton();
		stiftLabelD = new javax.swing.JLabel();
		stiftLabelE = new javax.swing.JLabel();
		stiftLabelF = new javax.swing.JLabel();
		stiftButtone = new javax.swing.JButton();
		stiftButtonf = new javax.swing.JButton();
		stiftButtonE = new javax.swing.JButton();
		stiftButtonF = new javax.swing.JButton();
		resetButton = new javax.swing.JButton();

		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/ConfigColorPanel");
		frameAndDigitsPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle.getString("ConfigColorPanel.frameAndDigitsPanel.border.title")));

		frameLabel.setText(bundle.getString("ConfigColorPanel.frameLabel.text"));

		cluesLabel.setText(bundle.getString("ConfigColorPanel.cluesLabel.text"));

		valuesLabel.setText(bundle.getString("ConfigColorPanel.valuesLabel.text"));

		candidatesLabel.setText(bundle.getString("ConfigColorPanel.candidatesLabel.text"));

		invalidFGLabel.setText(bundle.getString("ConfigColorPanel.invalidFGLabel.text"));

		wrongLabel.setText(bundle.getString("ConfigColorPanel.wrongLabel.text"));

		frameStrongButton.setText("...");
		frameStrongButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				frameStrongButtonActionPerformed(evt);
			}
		});

		frameWeakButton.setText("...");
		frameWeakButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				frameWeakButtonActionPerformed(evt);
			}
		});

		cluesButton.setText("...");
		cluesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cluesButtonActionPerformed(evt);
			}
		});

		valuesButton.setText("...");
		valuesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				valuesButtonActionPerformed(evt);
			}
		});

		candidatesButton.setText("...");
		candidatesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				candidatesButtonActionPerformed(evt);
			}
		});

		invalidFGButton.setText("...");
		invalidFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				invalidFGButtonActionPerformed(evt);
			}
		});

		wrongButton.setText("...");
		wrongButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wrongButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout frameAndDigitsPanelLayout = new javax.swing.GroupLayout(frameAndDigitsPanel);
		frameAndDigitsPanel.setLayout(frameAndDigitsPanelLayout);
		frameAndDigitsPanelLayout.setHorizontalGroup(frameAndDigitsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(frameAndDigitsPanelLayout.createSequentialGroup().addContainerGap()
						.addGroup(frameAndDigitsPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(frameLabel)
								.addComponent(cluesLabel).addComponent(valuesLabel).addComponent(candidatesLabel)
								.addComponent(invalidFGLabel).addComponent(wrongLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								frameAndDigitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(frameAndDigitsPanelLayout.createSequentialGroup()
												.addComponent(frameStrongButton, javax.swing.GroupLayout.PREFERRED_SIZE,
														40, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(frameWeakButton, javax.swing.GroupLayout.PREFERRED_SIZE,
														40, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(cluesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(valuesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(candidatesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(invalidFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(wrongButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(73, Short.MAX_VALUE)));

		frameAndDigitsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
				candidatesButton, cluesButton, frameWeakButton, invalidFGButton, valuesButton, wrongButton });

		frameAndDigitsPanelLayout.setVerticalGroup(
				frameAndDigitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(frameAndDigitsPanelLayout.createSequentialGroup()
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(frameLabel)
										.addComponent(frameWeakButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(frameStrongButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(cluesLabel).addComponent(cluesButton,
												javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(valuesLabel).addComponent(valuesButton,
												javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(candidatesLabel).addComponent(candidatesButton,
												javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(invalidFGLabel).addComponent(invalidFGButton,
												javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(frameAndDigitsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(wrongLabel).addComponent(wrongButton,
												javax.swing.GroupLayout.PREFERRED_SIZE, 21,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		backGroundPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				bundle.getString("ConfigColorPanel.backGroundPanel.border.title")));
		
		normalBGLabel.setText(bundle.getString("ConfigColorPanel.normalBGLabel.text"));

		cursorBGLabel.setText(bundle.getString("ConfigColorPanel.cursorBGLabel.text"));

		invalidBGLabel.setText(bundle.getString("ConfigColorPanel.invalidBGLabel.text"));

		validBGLabel.setText(bundle.getString("ConfigColorPanel.validBGLabel.text"));
		
		filterDigitBGButtonLabel.setText(bundle.getString("ConfigColorPanel.filterDigitBGButtonLabel.text"));

		normalBGButton.setText("...");
		normalBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				normalBGButtonActionPerformed(evt);
			}
		});

		cursorBGButton.setText("...");
		cursorBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cursorBGButtonActionPerformed(evt);
			}
		});

		invalidBGButton.setText("...");
		invalidBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				invalidBGButtonActionPerformed(evt);
			}
		});

		validBGButton.setText("...");
		validBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				validBGButtonActionPerformed(evt);
			}
		});
		
		filterDigitBGButton.setText("...");
		filterDigitBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				filterDigitBGButtonActionPerformed(evt);
			}
		});

		alsLabel4.setText(bundle.getString("ConfigColorPanel.alsLabel4.text"));

		alsHintFGButton4.setText("...");
		alsHintFGButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintFGButton4ActionPerformed(evt);
			}
		});

		alsHintBGButton4.setText("...");
		alsHintBGButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintBGButton4ActionPerformed(evt);
			}
		});

		alsHintBGButton3.setText("...");
		alsHintBGButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintBGButton3ActionPerformed(evt);
			}
		});

		alsHintFGButton3.setText("...");
		alsHintFGButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintFGButton3ActionPerformed(evt);
			}
		});

		alsLabel3.setText(bundle.getString("ConfigColorPanel.alsLabel3.text"));

		alsLabel2.setText(bundle.getString("ConfigColorPanel.alsLabel2.text"));

		alsHintFGButton2.setText("...");
		alsHintFGButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintFGButton2ActionPerformed(evt);
			}
		});

		alsHintBGButton2.setText("...");
		alsHintBGButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintBGButton2ActionPerformed(evt);
			}
		});

		alsHintBGButton1.setText("...");
		alsHintBGButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintBGButton1ActionPerformed(evt);
			}
		});

		alsHintFGButton1.setText("...");
		alsHintFGButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alsHintFGButton1ActionPerformed(evt);
			}
		});

		alsLabel1.setText(bundle.getString("ConfigColorPanel.alsLabel1.text"));

		alternateBGLabel.setText(bundle.getString("ConfigColorPanel.alternateBGLabel.text"));

		alternateBGButton.setText("...");
		alternateBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alternateBGButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout backGroundPanelLayout = new javax.swing.GroupLayout(backGroundPanel);
		backGroundPanel.setLayout(backGroundPanelLayout);
		backGroundPanelLayout.setHorizontalGroup(backGroundPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(backGroundPanelLayout.createSequentialGroup()
						.addGroup(backGroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(normalBGLabel).addComponent(validBGLabel).addComponent(invalidBGLabel)
								.addComponent(cursorBGLabel).addComponent(alternateBGLabel).addComponent(filterDigitBGButtonLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(backGroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(filterDigitBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(alternateBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(validBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(invalidBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(cursorBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(normalBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(backGroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(backGroundPanelLayout.createSequentialGroup().addComponent(alsLabel1)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(alsHintFGButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(alsHintBGButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(backGroundPanelLayout.createSequentialGroup().addComponent(alsLabel4)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(alsHintFGButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(alsHintBGButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(backGroundPanelLayout.createSequentialGroup()
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(alsLabel2).addComponent(alsLabel3))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(backGroundPanelLayout.createSequentialGroup()
														.addComponent(alsHintFGButton2,
																javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(alsHintBGButton2,
																javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(backGroundPanelLayout.createSequentialGroup()
														.addComponent(alsHintFGButton3,
																javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(alsHintBGButton3,
																javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)))))
						.addGap(10, 10, 10)));
		backGroundPanelLayout.setVerticalGroup(backGroundPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(backGroundPanelLayout.createSequentialGroup()
						.addGroup(backGroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(backGroundPanelLayout.createSequentialGroup()
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(normalBGLabel).addComponent(normalBGButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 21,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(cursorBGLabel).addComponent(cursorBGButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 21,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(invalidBGLabel).addComponent(invalidBGButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 21,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(validBGLabel).addComponent(validBGButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 21,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(filterDigitBGButtonLabel).addComponent(filterDigitBGButton,
														javax.swing.GroupLayout.PREFERRED_SIZE, 21,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGroup(backGroundPanelLayout.createSequentialGroup()
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(alsLabel1)
												.addComponent(alsHintFGButton1, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(alsHintBGButton1, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(alsLabel2)
												.addComponent(alsHintFGButton2, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(alsHintBGButton2, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(alsLabel3)
												.addComponent(alsHintBGButton3, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(alsHintFGButton3, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(backGroundPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(alsLabel4)
												.addComponent(alsHintFGButton4, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(alsHintBGButton4, javax.swing.GroupLayout.PREFERRED_SIZE,
														21, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(backGroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(alternateBGLabel).addComponent(alternateBGButton,
										javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(backGroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(frameAndDigitsPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))));

		jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { backGroundPanel, frameAndDigitsPanel });

		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(frameAndDigitsPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(backGroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(91, Short.MAX_VALUE)));		

		hintPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle.getString("ConfigColorPanel.hintPanel.border.title")));

		normHintBGLabel.setText(bundle.getString("ConfigColorPanel.normHintBGLabel.text"));

		delHintBGLabel.setText(bundle.getString("ConfigColorPanel.delHintBGLabel.text"));

		finsHintBGLabel.setText(bundle.getString("ConfigColorPanel.finsHintBGLabel.text"));

		endoFinsHintBGLabel.setText(bundle.getString("ConfigColorPanel.endoFinsHintBGLabel.text"));

		cannibalisticHintBGLabel.setText(bundle.getString("ConfigColorPanel.cannibalisticHintBGLabel.text"));

		arrowLabel.setText(bundle.getString("ConfigColorPanel.arrowLabel.text"));

		normalHintFGButton.setText("...");
		normalHintFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				normalHintFGButtonActionPerformed(evt);
			}
		});

		delHintFGButton.setText("...");
		delHintFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				delHintFGButtonActionPerformed(evt);
			}
		});

		finsHintFGButton.setText("...");
		finsHintFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				finsHintFGButtonActionPerformed(evt);
			}
		});

		endoFinsHintFGButton.setText("...");
		endoFinsHintFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endoFinsHintFGButtonActionPerformed(evt);
			}
		});

		cannibalisticHintFGButton.setText("...");
		cannibalisticHintFGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cannibalisticHintFGButtonActionPerformed(evt);
			}
		});

		arrowButton.setText("...");
		arrowButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				arrowButtonActionPerformed(evt);
			}
		});

		normalHintBGButton.setText("...");
		normalHintBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				normalHintBGButtonActionPerformed(evt);
			}
		});

		delHintBGButton.setText("...");
		delHintBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				delHintBGButtonActionPerformed(evt);
			}
		});

		finsHintBGButton.setText("...");
		finsHintBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				finsHintBGButtonActionPerformed(evt);
			}
		});

		endoFinsHintBGButton.setText("...");
		endoFinsHintBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endoFinsHintBGButtonActionPerformed(evt);
			}
		});

		cannibalisticHintBGButton.setText("...");
		cannibalisticHintBGButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cannibalisticHintBGButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout hintPanelLayout = new javax.swing.GroupLayout(hintPanel);
		hintPanel.setLayout(hintPanelLayout);
		hintPanelLayout.setHorizontalGroup(hintPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(hintPanelLayout.createSequentialGroup().addContainerGap().addGroup(hintPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(normHintBGLabel)
						.addComponent(delHintBGLabel).addComponent(finsHintBGLabel).addComponent(endoFinsHintBGLabel)
						.addComponent(cannibalisticHintBGLabel).addComponent(arrowLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(arrowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(hintPanelLayout.createSequentialGroup()
										.addComponent(cannibalisticHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE,
												40, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(cannibalisticHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE,
												40, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(hintPanelLayout.createSequentialGroup()
										.addComponent(endoFinsHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(endoFinsHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(hintPanelLayout.createSequentialGroup()
										.addComponent(finsHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(finsHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(hintPanelLayout.createSequentialGroup()
										.addComponent(delHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(delHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(hintPanelLayout.createSequentialGroup()
										.addComponent(normalHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(normalHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(86, Short.MAX_VALUE)));
		hintPanelLayout.setVerticalGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(hintPanelLayout.createSequentialGroup()
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(normHintBGLabel)
								.addComponent(normalHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(normalHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(delHintBGLabel)
								.addComponent(delHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(delHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(finsHintBGLabel)
								.addComponent(finsHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(finsHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(endoFinsHintBGLabel)
								.addComponent(endoFinsHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(endoFinsHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cannibalisticHintBGLabel)
								.addComponent(cannibalisticHintFGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(cannibalisticHintBGButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(hintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(arrowLabel).addComponent(arrowButton,
										javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		coloringPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle.getString("ConfigColorPanel.coloringPanel.border.title")));

		stiftLabelA.setText(bundle.getString("ConfigColorPanel.stiftLabelA.text"));

		stiftButtona.setText("...");
		stiftButtona.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonaActionPerformed(evt);
			}
		});

		stiftButtonA.setText("...");
		stiftButtonA.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonAActionPerformed(evt);
			}
		});

		stiftButtonB.setText("...");
		stiftButtonB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonBActionPerformed(evt);
			}
		});

		stiftButtonb.setText("...");
		stiftButtonb.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonbActionPerformed(evt);
			}
		});

		stiftLabelB.setText(bundle.getString("ConfigColorPanel.stiftLabelB.text"));

		stiftLabelC.setText(bundle.getString("ConfigColorPanel.stiftLabelC.text"));

		stiftButtonc.setText("...");
		stiftButtonc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtoncActionPerformed(evt);
			}
		});

		stiftButtonC.setText("...");
		stiftButtonC.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonCActionPerformed(evt);
			}
		});

		stiftButtonD.setText("...");
		stiftButtonD.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonDActionPerformed(evt);
			}
		});

		stiftButtond.setText("...");
		stiftButtond.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtondActionPerformed(evt);
			}
		});

		stiftLabelD.setText(bundle.getString("ConfigColorPanel.stiftLabelD.text"));

		stiftLabelE.setText(bundle.getString("ConfigColorPanel.stiftLabelE.text"));
		
		stiftLabelF.setText(bundle.getString("ConfigColorPanel.stiftLabelF.text"));

		stiftButtone.setText("...");
		stiftButtone.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtoneActionPerformed(evt);
			}
		});

		stiftButtonE.setText("...");
		stiftButtonE.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonEActionPerformed(evt);
			}
		});
		
		stiftButtonf.setText("...");
		stiftButtonf.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonfActionPerformed(evt);
			}
		});

		stiftButtonF.setText("...");
		stiftButtonF.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stiftButtonFActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout coloringPanelLayout = new javax.swing.GroupLayout(coloringPanel);
		coloringPanel.setLayout(coloringPanelLayout);
		coloringPanelLayout.setHorizontalGroup(coloringPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(coloringPanelLayout.createSequentialGroup().addContainerGap().addGroup(coloringPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(coloringPanelLayout.createSequentialGroup().addComponent(stiftLabelA)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtona, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtonA, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(coloringPanelLayout.createSequentialGroup().addComponent(stiftLabelD)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtond, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtonD, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(coloringPanelLayout.createSequentialGroup().addGroup(
								coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(stiftLabelB).addComponent(stiftLabelC))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(coloringPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(coloringPanelLayout.createSequentialGroup()
												.addComponent(stiftButtonb, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(stiftButtonB, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(coloringPanelLayout.createSequentialGroup()
												.addComponent(stiftButtonc, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(stiftButtonC, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
														javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGroup(coloringPanelLayout.createSequentialGroup().addComponent(stiftLabelE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtone, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtonE, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(coloringPanelLayout.createSequentialGroup().addComponent(stiftLabelF)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtonf, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(stiftButtonF, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(114, Short.MAX_VALUE)));
		coloringPanelLayout.setVerticalGroup(coloringPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(coloringPanelLayout.createSequentialGroup()
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelA)
								.addComponent(stiftButtona, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonA, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelB)
								.addComponent(stiftButtonb, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonB, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelC)
								.addComponent(stiftButtonC, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonc, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelD)
								.addComponent(stiftButtond, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonD, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelE)
								.addComponent(stiftButtone, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonE, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stiftLabelF)
								.addComponent(stiftButtonf, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stiftButtonF, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		resetButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/ConfigColorPanel")
				.getString("ConfigColorPanel.resetButton.mnemonic").charAt(0));
		resetButton.setText(bundle.getString("ConfigColorPanel.resetButton.text"));
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel2Layout.createSequentialGroup().addContainerGap(141, Short.MAX_VALUE)
								.addComponent(resetButton).addGap(10, 10, 10))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(coloringPanel, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(hintPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(hintPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(coloringPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
						.addComponent(resetButton).addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)
				.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));		
	}

	private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
		initAll(true);
	}

	private void alsHintBGButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(29);
	}

	private void alsHintFGButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(28);
	}

	private void alsHintBGButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(27);
	}

	private void alsHintFGButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(26);
	}

	private void alsHintBGButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(25);
	}

	private void alsHintFGButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(24);
	}

	private void alsHintBGButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(23);
	}

	private void alsHintFGButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(22);
	}

	private void cannibalisticHintBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(20);
	}

	private void endoFinsHintBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(18);
	}

	private void finsHintBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(16);
	}

	private void delHintBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(14);
	}

	private void normalHintBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(12);
	}

	private void arrowButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(21);
	}

	private void cannibalisticHintFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(19);
	}

	private void endoFinsHintFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(17);
	}

	private void finsHintFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(15);
	}

	private void delHintFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(13);
	}

	private void normalHintFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(11);
	}
	
	private void filterDigitBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(43);
	}

	private void validBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(10);
	}

	private void invalidBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(9);
	}

	private void cursorBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(8);
	}

	private void normalBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(7);
	}

	private void wrongButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(6);
	}

	private void invalidFGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(5);
	}

	private void candidatesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(4);
	}

	private void valuesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(3);
	}

	private void cluesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(2);
	}

	private void frameWeakButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(1);
	}

	private void frameStrongButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(0);
	}

	private void stiftButtonaActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(30);
	}

	private void stiftButtonAActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(31);
	}

	private void stiftButtonBActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(33);
	}

	private void stiftButtonbActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(32);
	}

	private void stiftButtoncActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(34);
	}

	private void stiftButtonCActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(35);
	}

	private void stiftButtonDActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(37);
	}

	private void stiftButtondActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(36);
	}

	private void stiftButtoneActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(38);
	}

	private void stiftButtonEActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(39);
	}
	
	private void stiftButtonFActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(42);
	}
	
	private void stiftButtonfActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(41);
	}

	private void alternateBGButtonActionPerformed(java.awt.event.ActionEvent evt) {
		chooseColor(40);
	}

	private void chooseColor(int index) {
		Color init = colors[index];
		Color color = JColorChooser.showDialog(this,
				java.util.ResourceBundle.getBundle("intl/ConfigColorPanel").getString("ConfigColorPanel.choose_color"),
				init);
		if (color != null) {
			colors[index] = color;
			initButton(buttons[index], color);
		}
	}

	public void okPressed() {
		
		Options.getInstance().setGridColor(colors[0]);
		Options.getInstance().setInnerGridColor(colors[1]);
		Options.getInstance().setCellFixedValueColor(colors[2]);
		Options.getInstance().setCellValueColor(colors[3]);
		Options.getInstance().setCandidateColor(colors[4]);
		Options.getInstance().setWrongValueColor(colors[5]);
		Options.getInstance().setDeviationColor(colors[6]);
		Options.getInstance().setDefaultCellColor(colors[7]);
		Options.getInstance().setAktCellColor(colors[8]);
		Options.getInstance().setInvalidCellColor(colors[9]);
		Options.getInstance().setPossibleCellColor(colors[10]);
		Options.getInstance().setHintCandidateColor(colors[11]);
		Options.getInstance().setHintCandidateBackColor(colors[12]);
		Options.getInstance().setHintCandidateDeleteColor(colors[13]);
		Options.getInstance().setHintCandidateDeleteBackColor(colors[14]);
		Options.getInstance().setHintCandidateFinColor(colors[15]);
		Options.getInstance().setHintCandidateFinBackColor(colors[16]);
		Options.getInstance().setHintCandidateEndoFinColor(colors[17]);
		Options.getInstance().setHintCandidateEndoFinBackColor(colors[18]);
		Options.getInstance().setHintCandidateCannibalisticColor(colors[19]);
		Options.getInstance().setHintCandidateCannibalisticBackColor(colors[20]);
		Options.getInstance().setArrowColor(colors[21]);
		Options.getInstance().getHintCandidateAlsColors()[0] = colors[22];
		Options.getInstance().getHintCandidateAlsBackColors()[0] = colors[23];
		Options.getInstance().getHintCandidateAlsColors()[1] = colors[24];
		Options.getInstance().getHintCandidateAlsBackColors()[1] = colors[25];
		Options.getInstance().getHintCandidateAlsColors()[2] = colors[26];
		Options.getInstance().getHintCandidateAlsBackColors()[2] = colors[27];
		Options.getInstance().getHintCandidateAlsColors()[3] = colors[28];
		Options.getInstance().getHintCandidateAlsBackColors()[3] = colors[29];
		Options.getInstance().getColoringColors()[0] = colors[30];
		Options.getInstance().getColoringColors()[1] = colors[31];
		Options.getInstance().getColoringColors()[2] = colors[32];
		Options.getInstance().getColoringColors()[3] = colors[33];
		Options.getInstance().getColoringColors()[4] = colors[34];
		Options.getInstance().getColoringColors()[5] = colors[35];
		Options.getInstance().getColoringColors()[6] = colors[36];
		Options.getInstance().getColoringColors()[7] = colors[37];
		Options.getInstance().getColoringColors()[8] = colors[38];
		Options.getInstance().getColoringColors()[9] = colors[39];
		Options.getInstance().setAlternateCellColor(colors[40]);
		Options.getInstance().getColoringColors()[10] = colors[41];
		Options.getInstance().getColoringColors()[11] = colors[42];
		Options.getInstance().setPossibleFixedCellColor(colors[43]);
	}

	private void initAll(boolean setDefault) {
		
		if (colors == null) {
			colors = new Color[buttons.length];
		}
		
		if (setDefault) {
			colors[0] = Options.GRID_COLOR;
			colors[1] = Options.INNER_GRID_COLOR;
			colors[2] = Options.CELL_FIXED_VALUE_COLOR;
			colors[3] = Options.CELL_VALUE_COLOR;
			colors[4] = Options.CANDIDATE_COLOR;
			colors[5] = Options.WRONG_VALUE_COLOR;
			colors[6] = Options.DEVIATION_COLOR;
			colors[7] = Options.DEFAULT_CELL_COLOR;
			colors[8] = Options.AKT_CELL_COLOR;
			colors[9] = Options.INVERSE_FILTER_COLOR;
			colors[10] = Options.FILTER_COLOR;
			colors[11] = Options.HINT_CANDIDATE_COLOR;
			colors[12] = Options.HINT_CANDIDATE_BACK_COLOR;
			colors[13] = Options.HINT_CANDIDATE_DELETE_COLOR;
			colors[14] = Options.HINT_CANDIDATE_DELETE_BACK_COLOR;
			colors[15] = Options.HINT_CANDIDATE_FIN_COLOR;
			colors[16] = Options.HINT_CANDIDATE_FIN_BACK_COLOR;
			colors[17] = Options.HINT_CANDIDATE_ENDO_FIN_COLOR;
			colors[18] = Options.HINT_CANDIDATE_ENDO_FIN_BACK_COLOR;
			colors[19] = Options.HINT_CANDIDATE_CANNIBALISTIC_COLOR;
			colors[20] = Options.HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR;
			colors[21] = Options.ARROW_COLOR;
			colors[22] = Options.HINT_CANDIDATE_ALS_COLORS[0];
			colors[23] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[0];
			colors[24] = Options.HINT_CANDIDATE_ALS_COLORS[1];
			colors[25] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[1];
			colors[26] = Options.HINT_CANDIDATE_ALS_COLORS[2];
			colors[27] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[2];
			colors[28] = Options.HINT_CANDIDATE_ALS_COLORS[3];
			colors[29] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[3];
			colors[30] = Options.COLORING_COLORS[0];
			colors[31] = Options.COLORING_COLORS[1];
			colors[32] = Options.COLORING_COLORS[2];
			colors[33] = Options.COLORING_COLORS[3];
			colors[34] = Options.COLORING_COLORS[4];
			colors[35] = Options.COLORING_COLORS[5];
			colors[36] = Options.COLORING_COLORS[6];
			colors[37] = Options.COLORING_COLORS[7];
			colors[38] = Options.COLORING_COLORS[8];
			colors[39] = Options.COLORING_COLORS[9];
			colors[40] = Options.ALTERNATE_CELL_COLOR;
			colors[41] = Options.COLORING_COLORS[10];
			colors[42] = Options.COLORING_COLORS[11];
			colors[43] = Options.FILTER_GIVEN_CELL_COLOR;
		} else {
			colors[0] = Options.getInstance().getGridColor();
			colors[1] = Options.getInstance().getInnerGridColor();
			colors[2] = Options.getInstance().getCellFixedValueColor();
			colors[3] = Options.getInstance().getCellValueColor();
			colors[4] = Options.getInstance().getCandidateColor();
			colors[5] = Options.getInstance().getWrongValueColor();
			colors[6] = Options.getInstance().getDeviationColor();
			colors[7] = Options.getInstance().getDefaultCellColor();
			colors[8] = Options.getInstance().getAktCellColor();
			colors[9] = Options.getInstance().getInvalidCellColor();
			colors[10] = Options.getInstance().getPossibleCellColor();
			colors[11] = Options.getInstance().getHintCandidateColor();
			colors[12] = Options.getInstance().getHintCandidateBackColor();
			colors[13] = Options.getInstance().getHintCandidateDeleteColor();
			colors[14] = Options.getInstance().getHintCandidateDeleteBackColor();
			colors[15] = Options.getInstance().getHintCandidateFinColor();
			colors[16] = Options.getInstance().getHintCandidateFinBackColor();
			colors[17] = Options.getInstance().getHintCandidateEndoFinColor();
			colors[18] = Options.getInstance().getHintCandidateEndoFinBackColor();
			colors[19] = Options.getInstance().getHintCandidateCannibalisticColor();
			colors[20] = Options.getInstance().getHintCandidateCannibalisticBackColor();
			colors[21] = Options.getInstance().getArrowColor();
			colors[22] = Options.getInstance().getHintCandidateAlsColors()[0];
			colors[23] = Options.getInstance().getHintCandidateAlsBackColors()[0];
			colors[24] = Options.getInstance().getHintCandidateAlsColors()[1];
			colors[25] = Options.getInstance().getHintCandidateAlsBackColors()[1];
			colors[26] = Options.getInstance().getHintCandidateAlsColors()[2];
			colors[27] = Options.getInstance().getHintCandidateAlsBackColors()[2];
			colors[28] = Options.getInstance().getHintCandidateAlsColors()[3];
			colors[29] = Options.getInstance().getHintCandidateAlsBackColors()[3];
			colors[30] = Options.getInstance().getColoringColors()[0];
			colors[31] = Options.getInstance().getColoringColors()[1];
			colors[32] = Options.getInstance().getColoringColors()[2];
			colors[33] = Options.getInstance().getColoringColors()[3];
			colors[34] = Options.getInstance().getColoringColors()[4];
			colors[35] = Options.getInstance().getColoringColors()[5];
			colors[36] = Options.getInstance().getColoringColors()[6];
			colors[37] = Options.getInstance().getColoringColors()[7];
			colors[38] = Options.getInstance().getColoringColors()[8];
			colors[39] = Options.getInstance().getColoringColors()[9];
			colors[40] = Options.getInstance().getAlternateCellColor();
			colors[41] = Options.getInstance().getColoringColors()[10];
			colors[42] = Options.getInstance().getColoringColors()[11];
			colors[43] = Options.getInstance().getPossibleFixedCellColor();
		}

		for (int i = 0; i < buttons.length; i++) {
			initButton(buttons[i], colors[i]);
		}
	}

	private void initButton(JButton button, Color color) {
		// button.setText(" ");
		Image img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 10, 10);
		button.setIcon(new ImageIcon(img));
		if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
			button.setBackground(color);
		}
	}
}
