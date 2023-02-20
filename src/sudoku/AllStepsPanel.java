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

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author hobiwan
 */
public final class AllStepsPanel extends javax.swing.JPanel implements TreeSelectionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private Sudoku2 sudoku;
	private MainFrame mainFrame;
	private DefaultTreeModel model;
	private List<SolutionStep> steps;
	private JToggleButton[] toggleButtons = null;

	/**
	 * Creates new form AllStepsPanel
	 * 
	 * @param mainFrame
	 * @param sudoku
	 */
	public AllStepsPanel(MainFrame mainFrame, Sudoku2 sudoku) {
		this.mainFrame = mainFrame;
		this.setSudoku(sudoku);

		initComponents();

		toggleButtons = new JToggleButton[] { directSingleSortToggleButton, singleSortToggleButton,
				cellSortToggleButton, eliminationSortToggleButton, typeSortToggleButton };

//        stepsTree.setCellRenderer(new ScalableRenderer());
		stepsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		stepsTree.addTreeSelectionListener(this);
		FontMetrics metrics = getFontMetrics(getFont());
		int rowHeight = (int) (metrics.getHeight() * 1.0);
		stepsTree.setRowHeight(rowHeight);
		resetPanel();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel2 = new javax.swing.JPanel();
		findButton = new javax.swing.JButton();
		addToSolutionButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		stepsTree = new javax.swing.JTree();
		jToolBar1 = new javax.swing.JToolBar();
		configureButton = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JToolBar.Separator();
		directSingleSortToggleButton = new javax.swing.JToggleButton();
		singleSortToggleButton = new javax.swing.JToggleButton();
		cellSortToggleButton = new javax.swing.JToggleButton();
		eliminationSortToggleButton = new javax.swing.JToggleButton();
		typeSortToggleButton = new javax.swing.JToggleButton();

		setLayout(new java.awt.BorderLayout());

		findButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/AllStepsPanel")
				.getString("AllStepsPanel.findButton.mnemonic").charAt(0));
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("intl/AllStepsPanel"); // NOI18N
		findButton.setText(bundle.getString("AllStepsPanel.findButton.text")); // NOI18N
		findButton.setToolTipText(bundle.getString("AllStepsPanel.findButton.toolTipText")); // NOI18N
		findButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				findButtonActionPerformed(evt);
			}
		});

		addToSolutionButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/AllStepsPanel")
				.getString("AllStepsPanel.addToSolution.mnemonic").charAt(0));
		addToSolutionButton.setText(bundle.getString("AllStepsPanel.addToSolutionButton.text")); // NOI18N
		addToSolutionButton.setToolTipText(bundle.getString("AllStepsPanel.addToSolutionButton.toolTipText")); // NOI18N
		addToSolutionButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addToSolutionButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(findButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(addToSolutionButton).addContainerGap(106, Short.MAX_VALUE)));

		jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { addToSolutionButton, findButton });

		jPanel2Layout
				.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(findButton).addComponent(addToSolutionButton))
								.addContainerGap()));

		add(jPanel2, java.awt.BorderLayout.SOUTH);

		jScrollPane1.setViewportView(stepsTree);

		add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jToolBar1.setRollover(true);

		configureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/settings.png"))); // NOI18N
		configureButton.setToolTipText(bundle.getString("AllStepsPanel.configureButton.toolTipText")); // NOI18N
		configureButton.setFocusable(false);
		configureButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		configureButton.setMaximumSize(new java.awt.Dimension(36, 36));
		configureButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		configureButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				configureButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(configureButton);
		jToolBar1.add(jSeparator1);

		directSingleSortToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search_d1.png"))); // NOI18N
		directSingleSortToggleButton
				.setToolTipText(bundle.getString("AllStepsPanel.directSingleSortToggleButton.toolTipText")); // NOI18N
		directSingleSortToggleButton.setFocusable(false);
		directSingleSortToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		directSingleSortToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		directSingleSortToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				directSingleSortToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(directSingleSortToggleButton);

		singleSortToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search_s1.png"))); // NOI18N
		singleSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.singleSortToggleButton.toolTipText")); // NOI18N
		singleSortToggleButton.setFocusable(false);
		singleSortToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		singleSortToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		singleSortToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				singleSortToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(singleSortToggleButton);

		cellSortToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search_c1.png"))); // NOI18N
		cellSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.cellSortToggleButton.toolTipText")); // NOI18N
		cellSortToggleButton.setFocusable(false);
		cellSortToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		cellSortToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		cellSortToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cellSortToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(cellSortToggleButton);

		eliminationSortToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search_e1.png"))); // NOI18N
		eliminationSortToggleButton
				.setToolTipText(bundle.getString("AllStepsPanel.eliminationSortToggleButton.toolTipText")); // NOI18N
		eliminationSortToggleButton.setFocusable(false);
		eliminationSortToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		eliminationSortToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		eliminationSortToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				eliminationSortToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(eliminationSortToggleButton);

		typeSortToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search_t1.png"))); // NOI18N
		typeSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.typeSortToggleButton.toolTipText")); // NOI18N
		typeSortToggleButton.setFocusable(false);
		typeSortToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		typeSortToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		typeSortToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				typeSortToggleButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(typeSortToggleButton);

		add(jToolBar1, java.awt.BorderLayout.PAGE_START);
	}// </editor-fold>//GEN-END:initComponents

	private void addToSolutionButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addToSolutionButtonActionPerformed
		DefaultMutableTreeNode last = (DefaultMutableTreeNode) stepsTree.getLastSelectedPathComponent();
		SolutionStep actStep = null;
		if (last != null) {
			if (last.getUserObject() instanceof String) {
				// Erstes Step-Kind holen
				// @SuppressWarnings("unchecked")
                                Enumeration<TreeNode> children = last.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode act = (DefaultMutableTreeNode)children.nextElement();
					if (act.getUserObject() instanceof SolutionStep) {
						actStep = (SolutionStep) act.getUserObject();
						break;
					}
				}
			} else {
				actStep = (SolutionStep) last.getUserObject();
			}
		}
		if (actStep == null) {
			JOptionPane.showMessageDialog(this,
					java.util.ResourceBundle.getBundle("intl/AllStepsPanel")
							.getString("AllStepsPanel.no_solution_selected"),
					java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		mainFrame.getSolutionPanel().addStep(actStep);
	}// GEN-LAST:event_addToSolutionButtonActionPerformed

	private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findButtonActionPerformed
		if (sudoku == null) {
			JOptionPane.showMessageDialog(this,
					java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.sudoku_not_set"),
					java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		resetTreeNodes();
		new Thread(this).start();
	}// GEN-LAST:event_findButtonActionPerformed

	private void configureButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_configureButtonActionPerformed
		new ConfigDialog(mainFrame, true, 3).setVisible(true);
	}// GEN-LAST:event_configureButtonActionPerformed

	private void directSingleSortToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_directSingleSortToggleButtonActionPerformed
		setSortMode(0);
	}// GEN-LAST:event_directSingleSortToggleButtonActionPerformed

	private void singleSortToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_singleSortToggleButtonActionPerformed
		setSortMode(1);
	}// GEN-LAST:event_singleSortToggleButtonActionPerformed

	private void cellSortToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cellSortToggleButtonActionPerformed
		setSortMode(2);
	}// GEN-LAST:event_cellSortToggleButtonActionPerformed

	private void eliminationSortToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_eliminationSortToggleButtonActionPerformed
		setSortMode(3);
	}// GEN-LAST:event_eliminationSortToggleButtonActionPerformed

	private void typeSortToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_typeSortToggleButtonActionPerformed
		setSortMode(4);
	}// GEN-LAST:event_typeSortToggleButtonActionPerformed

	@Override
	public void run() {
		FindAllStepsProgressDialog dlg = new FindAllStepsProgressDialog(mainFrame, true, sudoku);
		dlg.setVisible(true);
		steps = dlg.getSteps();
		createTreeNodes(Options.getInstance().getAllStepsSortMode());
	}

	public void resetPanel() {
		model = new DefaultTreeModel(new DefaultMutableTreeNode(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.no_solutions")));
		steps = null;
		stepsTree.setModel(model);
		adjustToggleButtons();
		createTreeNodes(Options.getInstance().getAllStepsSortMode());
	}

	private void adjustToggleButtons() {
		if (toggleButtons == null) {
			return;
		}
		for (int i = 0; i < toggleButtons.length; i++) {
			if (i == Options.getInstance().getAllStepsSortMode()) {
				toggleButtons[i].setSelected(true);
			} else {
				toggleButtons[i].setSelected(false);
			}
		}
	}

	private void setSortMode(int sortMode) {
		Options.getInstance().setAllStepsSortMode(sortMode);
		adjustToggleButtons();
		createTreeNodes(Options.getInstance().getAllStepsSortMode());
	}

	private void resetTreeNodes() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.searching"));
		model.reload();
		stepsTree.setRootVisible(true);
		stepsTree.repaint();
	}

	private void createTreeNodes(int sortMode) {
		// reset everything and check for errors
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		if (steps == null || steps.isEmpty()) {
			root.setUserObject(
					java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.no_solution"));
			stepsTree.setRootVisible(true);
			model.reload();
			stepsTree.repaint();
			return;
		}
		// create the tree
		switch (sortMode) {
		case 0:
			// direct singles
			createTreeNodesDirectSingles(root);
			break;
		case 1:
			// singles
			createTreeNodesSingles(root);
			break;
		case 2:
			// cells
			createTreeNodesCells(root);
			break;
		case 3:
			// eliminations
			createTreeNodesNumberOfEliminations(root);
			break;
		case 4:
			// type:
			createTreeNodesTypes(root);
			break;
		default:
			Logger.getLogger(AllStepsPanel.class.getName()).log(Level.SEVERE, "Invalid sort mode ({0})", sortMode);
			break;
		}
		// show it
		stepsTree.expandPath(new TreePath(root));
		stepsTree.setShowsRootHandles(true);
		model.reload();
		stepsTree.repaint();
	}

	/**
	 * Top level: {@link SolutionType}<br>
	 * Second level: steps<br>
	 */
	private void createTreeNodesTypes(DefaultMutableTreeNode root) {
		// sort the steps
		Collections.sort(steps, new Comparator<SolutionStep>() {

			@Override
			public int compare(SolutionStep o1, SolutionStep o2) {
				int ret = o1.getType().getStepConfig().getIndex() - o2.getType().getStepConfig().getIndex();
				if (ret == 0) {
					ret = o1.getType().getStepName().compareTo(o2.getType().getStepName());
					if (ret == 0) {
						ret = o1.compareTo(o2);
					}
				}
				return ret;
			}
		});
		// now build the tree
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
		stepsTree.setRootVisible(false);
		SolutionStep lastStep = null;
		DefaultMutableTreeNode lastCat1 = null; // types
		DefaultMutableTreeNode lastEntry = null;
		for (SolutionStep step : steps) {
			// normaler Betrieb, mitten im Baum
			if (lastStep == null || step.getType() != lastStep.getType()) {
				// new top category
				// if it is not the first, change the last cat to the number
				// of different eliminations
				if (lastStep != null) {
					String dummy = (String) lastCat1.getUserObject();
					lastCat1.setUserObject(dummy + " (" + lastCat1.getChildCount() + ")");
				}
				lastCat1 = new DefaultMutableTreeNode(step.getType().getStepName());
				root.add(lastCat1);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat1.add(lastEntry);
			} else if (step.isEqualCandidate(lastStep) && step.isEqualValues(lastStep)) {
				// same step -> put step under lastStep
				lastEntry.add(new DefaultMutableTreeNode(step));
			} else {
				// new step, but in current level 2
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat1.add(lastEntry);
			}
			lastStep = step;
		}
		if (lastStep != null) {
			String dummy = (String) lastCat1.getUserObject();
			lastCat1.setUserObject(dummy + " (" + lastCat1.getChildCount() + ")");
		}
	}

	/**
	 * Top level: affected cell<br>
	 * Second level: eliminated candidate<br>
	 * third level: steps<br>
	 */
	private void createTreeNodesCells(DefaultMutableTreeNode root) {
		// every step can appear in more than one cell -> build up
		// a data structure and collect the data
		SortedMap<Integer, List<SolutionStep>> map = new TreeMap<Integer, List<SolutionStep>>();
		for (SolutionStep step : steps) {
			for (Candidate delCand : step.getCandidatesToDelete()) {
				int candIndex = delCand.getIndex() * 10 + delCand.getValue();
				List<SolutionStep> stepList = map.get(candIndex);
				if (stepList == null) {
					stepList = new ArrayList<SolutionStep>();
					map.put(candIndex, stepList);
				}
				stepList.add(step);
			}
		}
		// the steps are already sorted by cell/index, the lists for every
		// cell/index will be sorted later
		// now build the tree
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
		stepsTree.setRootVisible(false);
		DefaultMutableTreeNode lastCat1 = null; // cells
		DefaultMutableTreeNode lastCat2 = null; // candidates
		DefaultMutableTreeNode lastEntry = null;
		int lastIndex = -1;
		for (Entry<Integer, List<SolutionStep>> entry : map.entrySet()) {
			// a new Entry from map means a new cell and/or a new candidate
			int index = entry.getKey() / 10;
			int candidate = entry.getKey() % 10;
			if (index != lastIndex) {
				// new cell
				String cell = SolutionStep.getCellPrint(index, false);
				lastCat1 = new DefaultMutableTreeNode(cell);
				root.add(lastCat1);
				lastIndex = index;
			}
			// new candidate
			lastCat2 = new DefaultMutableTreeNode(
					ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.candidate") + " "
							+ candidate);
			lastCat1.add(lastCat2);
			// now the steps for that candidate: sort by technique index
			List<SolutionStep> stepList = entry.getValue();
			Collections.sort(stepList, new Comparator<SolutionStep>() {

				@Override
				public int compare(SolutionStep o1, SolutionStep o2) {
					int ret = o1.getType().getStepConfig().getIndex() - o2.getType().getStepConfig().getIndex();
					if (ret == 0) {
						ret = o1.getType().getStepName().compareTo(o2.getType().getStepName());
						if (ret == 0) {
							ret = o1.compareTo(o2);
						}
					}
					return ret;
				}
			});
			// now get the steps in
			SolutionStep lastStep = null;
			for (SolutionStep step : stepList) {
				if (lastStep != null && step.isEqual(lastStep)) {
					// same step -> put step under lastStep
					lastEntry.add(new DefaultMutableTreeNode(step));
				} else {
					// new step, but in current level 2
					lastEntry = new DefaultMutableTreeNode(step);
					lastCat2.add(lastEntry);
				}
				lastStep = step;
			}
		}
	}

	/**
	 * Top level: all unlocked singles<br>
	 * Second level: score<br>
	 * third level: steps<br>
	 */
	private void createTreeNodesSingles(DefaultMutableTreeNode root) {
		// sort the steps
		Collections.sort(steps, new Comparator<SolutionStep>() {

			@Override
			public int compare(SolutionStep o1, SolutionStep o2) {
				int ret = o2.getProgressScoreSingles() - o1.getProgressScoreSingles();
				if (ret == 0) {
					ret = o1.getProgressScore() - o2.getProgressScore();
					if (ret == 0) {
						ret = o1.compareTo(o2);
					}
				}
				return ret;
			}
		});
		// now build the tree
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
		stepsTree.setRootVisible(false);
		SolutionStep lastStep = null;
		DefaultMutableTreeNode lastCat1 = null; // singles
		DefaultMutableTreeNode lastCat2 = null; // score
		DefaultMutableTreeNode lastEntry = null;
		for (SolutionStep step : steps) {
			// normaler Betrieb, mitten im Baum
			if (lastStep == null || step.getProgressScoreSingles() != lastStep.getProgressScoreSingles()) {
				// new top category
				lastCat1 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " "
								+ step.getProgressScoreSingles());
				root.add(lastCat1);
				lastCat2 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " "
								+ step.getProgressScore());
				lastCat1.add(lastCat2);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat2.add(lastEntry);
			} else if (step.getProgressScore() != lastStep.getProgressScore()) {
				// new third level category
				lastCat2 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " "
								+ step.getProgressScore());
				lastCat1.add(lastCat2);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat2.add(lastEntry);
			} else if (step.isEqual(lastStep)) {
				// same step -> put step under lastStep
				lastEntry.add(new DefaultMutableTreeNode(step));
			} else {
				// new step, but in current level 3
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat2.add(lastEntry);
			}
			lastStep = step;
		}
	}

	/**
	 * Top level: directly unlocked singles<br>
	 * Second level: all unlocked singles<br>
	 * Third level: score<br>
	 * Forth level: steps<br>
	 */
	private void createTreeNodesDirectSingles(DefaultMutableTreeNode root) {
		// sort the steps
		Collections.sort(steps, new Comparator<SolutionStep>() {

			@Override
			public int compare(SolutionStep o1, SolutionStep o2) {
				int ret = o2.getProgressScoreSinglesOnly() - o1.getProgressScoreSinglesOnly();
				if (ret == 0) {
					ret = o2.getProgressScoreSingles() - o1.getProgressScoreSingles();
					if (ret == 0) {
						ret = o1.getProgressScore() - o2.getProgressScore();
						if (ret == 0) {
							ret = o1.compareTo(o2);
						}
					}
				}
				return ret;
			}
		});
		// now build the tree
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
		stepsTree.setRootVisible(false);
		SolutionStep lastStep = null;
		DefaultMutableTreeNode lastCat1 = null; // direct singles
		DefaultMutableTreeNode lastCat2 = null; // all singles
		DefaultMutableTreeNode lastCat3 = null; // score
		DefaultMutableTreeNode lastEntry = null;
		for (SolutionStep step : steps) {
			// normaler Betrieb, mitten im Baum
			if (lastStep == null || step.getProgressScoreSinglesOnly() != lastStep.getProgressScoreSinglesOnly()) {
				// new top category
				lastCat1 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.direct_singles") + " "
								+ step.getProgressScoreSinglesOnly());
				root.add(lastCat1);
				lastCat2 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " "
								+ step.getProgressScoreSingles());
				lastCat1.add(lastCat2);
				lastCat3 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " "
								+ step.getProgressScore());
				lastCat2.add(lastCat3);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat3.add(lastEntry);
			} else if (step.getProgressScoreSingles() != lastStep.getProgressScoreSingles()) {
				// new second level category
				lastCat2 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " "
								+ step.getProgressScoreSingles());
				lastCat1.add(lastCat2);
				lastCat3 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " "
								+ step.getProgressScore());
				lastCat2.add(lastCat3);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat3.add(lastEntry);
			} else if (step.getProgressScore() != lastStep.getProgressScore()) {
				// new third level category
				lastCat3 = new DefaultMutableTreeNode(
						ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " "
								+ step.getProgressScore());
				lastCat2.add(lastCat3);
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat3.add(lastEntry);
			} else if (step.isEqual(lastStep)) {
				// same step -> put step under lastStep
				lastEntry.add(new DefaultMutableTreeNode(step));
			} else {
				// new step, but in current level 3
				lastEntry = new DefaultMutableTreeNode(step);
				lastCat3.add(lastEntry);
			}
			lastStep = step;
		}
	}

	@SuppressWarnings("unused")
	private void createTreeNodesNumberOfEliminations(DefaultMutableTreeNode root) {
		Collections.sort(steps);
		root.setUserObject(
				java.util.ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
		stepsTree.setRootVisible(false);
		SolutionStep lastStep = null;
		DefaultMutableTreeNode lastCat = null;
		DefaultMutableTreeNode lastEntry = null;
		for (SolutionStep step : steps) {
			// Singles kommen immer ganz oben hin (jedes Single in eine eigene Kategorie);
			// das gleiche gilt für Forcing Chains, die Werte setzen
			// (eigener Durchlauf!)
			// System.out.println(step.toString(2));
			if (step.isSingle() || (step.isForcingChainSet())) {
				continue;
			}
			Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.toString(2));
			if (lastStep == null) {
				// erster Eintrag
				Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.getCandidateString());
				lastCat = new DefaultMutableTreeNode(step.getCandidateString());
				lastEntry = new DefaultMutableTreeNode(step);
				root.add(lastCat);
				lastCat.add(lastEntry);
			} else {
				// normaler Betrieb, mitten im Baum
				if (step.isEqual(lastStep)) {
					// gleich -> darunterhängen
					lastEntry.add(new DefaultMutableTreeNode(step));
				} else if (step.isEquivalent(lastStep)) {
					// äquivalent: neuer Eintrag unter lastCat
					lastEntry = new DefaultMutableTreeNode(step);
					lastCat.add(lastEntry);
				} else {
					// neue Kategorie: prüfen, ob es ein Subset eines bestehenden Eintrags ist
					Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.getCandidateString());
					lastCat = new DefaultMutableTreeNode(step.getCandidateString());
					lastEntry = new DefaultMutableTreeNode(step);
					// doch nicht untereinander
					// DefaultMutableTreeNode tmp = getParent(root, step);
					DefaultMutableTreeNode tmp = null;
					if (tmp == null) {
						// in oberster Ebene einhängen: richtigen Index suchen
						// (soll schließlich nach Anzahl zu löschender Kandidaten sortiert sein)
						root.insert(lastCat, getTopLevelIndex(root, step));
						// root.add(lastCat);
					} else {
						// ist entweder äquivalent oder Subset
						SolutionStep tmpStep = getStepFromNode(tmp);
						if (tmpStep.isEquivalent(step)) {
							// hinter dem äquivalenten Step in parent einfügen
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tmp.getParent();
							parent.insert(lastCat, parent.getIndex(tmp) + 1);
						} else {
							tmp.add(lastCat);
						}
					}
					lastCat.add(lastEntry);
				}
			}
			lastStep = step;
		}
		for (SolutionStep step : steps) {
			// Singles kommen immer ganz oben hin (jedes Single in eine eigene Kategorie)
			// ACHTUNG: Forcing Chains: Alle Setzoperationen für eine Zelle in eine
			// Kategorie
			// (eigener Durchlauf!)
			if (!step.isSingle() && !step.isForcingChainSet()) {
				continue;
			}
			if (step.isForcingChainSet() && step.isEqual(lastStep)) {
				// gleich -> darunterhängen
				lastEntry.add(new DefaultMutableTreeNode(step));
			} else {
				lastCat = new DefaultMutableTreeNode(step.getSingleCandidateString());
				lastEntry = new DefaultMutableTreeNode(step);
				root.insert(lastCat, 0);
				lastCat.add(lastEntry);
			}
			lastStep = step;
		}
		// Reihenfolge der Fische anpassen
		adjustFishes(root);
	}

//    private DefaultMutableTreeNode getParent(DefaultMutableTreeNode root, SolutionStep step) {
//        Enumeration children = root.children();
//        while (children.hasMoreElements()) {
//            DefaultMutableTreeNode act = (DefaultMutableTreeNode) children.nextElement();
//            if (act.getUserObject() instanceof String) {
//                // Ist ein Knoten für einen Step -> ersten Step suchen
//                boolean found = false;
//                Enumeration nodes = act.children();
//                while (nodes.hasMoreElements()) {
//                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
//                    if (node.getUserObject() instanceof SolutionStep) {
//                        SolutionStep nodeStep = (SolutionStep) node.getUserObject();
//                        if (step.isEquivalent(nodeStep)) {
//                            return (DefaultMutableTreeNode) node.getParent();
//                        } else if (step.isSubStep(nodeStep)) {
//                            found = true;
//                            break;
//                        } else {
//                            DefaultMutableTreeNode ret = getParent(act, step);
//                            if (ret != null) {
//                                // gefunden!
//                                return ret;
//                            }
//                        }
//                    }
//                }
//                if (found) {
//                    // gefunden
//                    return act;
//                }
//            }
//        }
//        // nichts gefunden!
//        return null;
//    }

	/**
	 * Mit Sortierung allein kann nicht sichergestellt werden, dass immer der
	 * kleinste Fisch angezeigt wird; daher wird das in einem eigenen Durchlauf
	 * erledigt:
	 *
	 * - Auf jeder Ebene wird zunächst jeder Knoten mit Kindern rekursiv behandelt
	 * (stellt sicher, dass jeder Knoten den kleinsten Step enthält) -
	 * Anschließend werden alle nicht-String-Knoten durchgegangen, der Knoten mit
	 * dem kleinsten Step-Typ wird mit dem ersten Knoten vertauscht, der
	 * String-Knoten wird angepasst
	 */
	private void adjustFishes(DefaultMutableTreeNode root) {
//        if (root.getChildCount() == 0) {
//            return;
//        }
//        
//        // zuerst die Rekursion
//        Enumeration nodes = root.children();
//        while (nodes.hasMoreElements()) {
//            DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)nodes.nextElement();
//            if (nextNode.getChildCount() > 0) {
//                adjustFishes(nextNode);
//            }
//        }
//        // wenn das erste Kind wieder ein String ist, sind wir bei root angelangt -> fertig
//        if (((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() instanceof String) {
//            return;
//        }
//        // ok, jeder Knoten enthält den kleinsten Fish -> aktuelle Ebene bereinigen
//        int typeOrdinal = -1;
//        boolean isString = false;
//        if (root.getUserObject() instanceof String) {
//            SolutionStep step = (SolutionStep)((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject();
//            typeOrdinal = step.getType().ordinal();
//            isString = true;
//        } else {
//            typeOrdinal = ((SolutionStep)root.getUserObject()).getType().ordinal();
//        }
//        int index = 0;
//        int smallestIndex = -1;
//        nodes = root.children();
//        while (nodes.hasMoreElements()) {
//            DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)nodes.nextElement();
//            if (nextNode.getUserObject() instanceof String) {
//                // SubSteps, hier uninteressant
//                continue;
//            }
//            int actOrdinal = ((SolutionStep)nextNode.getUserObject()).getType().ordinal();
//            if (actOrdinal < typeOrdinal) {
//                typeOrdinal = actOrdinal;
//                smallestIndex = index;
//            }
//            index++;
//        }
//        // ok, gefunden -> wenn nötig anpassen
//        if (smallestIndex > -1) {
//            SolutionStep step = (SolutionStep)((DefaultMutableTreeNode)root.getChildAt(smallestIndex)).getUserObject();
//            if (isString) {
//                // mit dem ersten Kind tauschen
//                ((DefaultMutableTreeNode)root.getChildAt(smallestIndex)).setUserObject(
//                        ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject());
//                ((DefaultMutableTreeNode)root.getChildAt(0)).setUserObject(step);
//                // String in root anpassen
//                root.setUserObject(step.getCandidateString());
//            } else {
//                // mit root tauschen
//                ((DefaultMutableTreeNode)root.getChildAt(smallestIndex)).setUserObject(root.getUserObject());
//                root.setUserObject(step);
//            }
//        }
	}

	/**
	 * Sucht die richtige Position in der obersten Ebene des Baumes (hinter dem
	 * letzten Step mit gleich viel zu löschenden Kandidaten)
	 */
	private int getTopLevelIndex(DefaultMutableTreeNode root, SolutionStep step) {
		int index = 0;
		// @SuppressWarnings("unchecked")
		Enumeration<TreeNode> nodes = root.children();
		while (nodes.hasMoreElements()) {
			DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)nodes.nextElement();
			SolutionStep actStep = getStepFromNode(nextNode);
			if (actStep.getCandidatesToDelete().size() < step.getCandidatesToDelete().size()) {
				break;
			}
			index++;
		}
		return index;
	}

	private SolutionStep getStepFromNode(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof SolutionStep) {
			return (SolutionStep) node.getUserObject();
		} else if (node.getUserObject() instanceof String) {
			// erstes Kind suchen, das ein SolutionStep ist
			// @SuppressWarnings("unchecked")
			Enumeration<TreeNode> nodes = node.children();
			while (nodes.hasMoreElements()) {
				DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)nodes.nextElement();
				if (nextNode.getUserObject() instanceof SolutionStep) {
					return (SolutionStep) nextNode.getUserObject();
				}
			}
		}
		return null;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode last = (DefaultMutableTreeNode) stepsTree.getLastSelectedPathComponent();
		if (last != null) {
			if (last.getUserObject() instanceof String) {
				// Erstes Step-Kind anzeigen
				// @SuppressWarnings("unchecked")
				Enumeration<TreeNode> children = last.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode act = (DefaultMutableTreeNode)children.nextElement();
					if (act.getUserObject() instanceof SolutionStep) {
						mainFrame.setSolutionStep((SolutionStep) act.getUserObject(), true);
						break;
					}
				}
			} else {
				mainFrame.setSolutionStep((SolutionStep) last.getUserObject(), true);
			}
		}
	}

	public void setSudoku(Sudoku2 sudoku) {
		this.sudoku = sudoku;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addToSolutionButton;
	private javax.swing.JToggleButton cellSortToggleButton;
	private javax.swing.JButton configureButton;
	private javax.swing.JToggleButton directSingleSortToggleButton;
	private javax.swing.JToggleButton eliminationSortToggleButton;
	private javax.swing.JButton findButton;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JToolBar.Separator jSeparator1;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JToggleButton singleSortToggleButton;
	private javax.swing.JTree stepsTree;
	private javax.swing.JToggleButton typeSortToggleButton;
	// End of variables declaration//GEN-END:variables
}
