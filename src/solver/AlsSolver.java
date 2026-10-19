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
package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import sudoku.Candidate;
import sudoku.Chain;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

/**
 *
 * @author hobiwan
 */
public class AlsSolver extends AbstractSolver {

	/** Enable additional trace output */
	private static final boolean DEBUG = false;
	/** Enable additional timing */
	private static final boolean TIMING = false;
	/** Maximum number of RCs in an ALS-Chain when solving (not for "find all steps"). */
	private static final int MAX_RC = 50;
	/** A list holding all ALS present in the curent state of the gid. */
	private List<Als> alses = new ArrayList<Als>(500);
	/** A list with all restricted commons in the present grid. */
	private List<RestrictedCommon> restrictedCommons = new ArrayList<RestrictedCommon>(2000);
	/**
	 * The indices of the first RC in {@link #restrictedCommons} for every ALS in
	 * {@link #alses}.
	 */
	private int[] startIndices = null;
	/**
	 * The indices of the last RC in {@link #restrictedCommons} for every ALS in
	 * {@link #alses}.
	 */
	private int[] endIndices = null;
	/** all chains that have been found so far: eliminations and number of links */
	private SortedMap<String, Integer> deletesMap = new TreeMap<String, Integer>();
	/** A special comparator used to find the "best" step out of a list of steps. */
	private static AlsComparator alsComparator = null;
	/** A list with all steps found during the last run. */
	private List<SolutionStep> steps = new ArrayList<SolutionStep>();
	/** One step instance for optimization. */
	private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);
	/**
	 * The current ALS Chain: The chain consists only of its RCs. A chain cannot be
	 * longer than {@link #maxChainLinks}.
	 */
	private RestrictedCommon[] chain = new RestrictedCommon[MAX_RC];
	/**
	 * Maximum number of RCs in an ALS-Chain for the current search: {@link #MAX_RC}
	 * when solving, the "find all steps" chain length option otherwise. Set for
	 * every search, so a changed option takes effect immediately (issue #47).
	 */
	private int maxChainLinks = MAX_RC;
	/** The index into {@link #chain} for the current search. */
	private int chainIndex = -1;
	/**
	 * The first RC in the current chain (always is {@link #chain}[0]; needed for
	 * test for eliminations, cached for performance reasons).
	 */
	private RestrictedCommon firstRC = null;
	/**
	 * Chain search: for every ALS already contained in the chain the respective
	 * index is true.
	 */
	private boolean[] alsInChain;
	/** The first ALS in the chain (needed for elimination checks). */
	private Als startAls;
	/** Current recursion depth for chain search. */
	private int recDepth = 0;
	/** Maximum recursion depth for chain search. */
	private int maxRecDepth = 0;
	/** All candidates occurring in both flanking ALS of an ALS step. */
	private short possibleRestrictedCommonsSet = 0;
	/**
	 * Holds all buddies of all candidate cells for one RC (including the candidate
	 * cells themselves).
	 */
	private SudokuSet restrictedCommonBuddiesSet = new SudokuSet();
	/** All cells containing a specific candidate in two ALS. */
	private SudokuSet restrictedCommonIndexSet = new SudokuSet();
	/**
	 * One instance of {@link RCForDeathBlossom} for every cell that is not yet set.
	 */
	private RCForDeathBlossom[] rcdb = new RCForDeathBlossom[81];
	/** ALS for stem cell that is currently checked. */
	private RCForDeathBlossom aktRcdb = null;
	/** All indices of all ALS for a given stem cell (for recursive search). */
	private SudokuSet aktDBIndices = new SudokuSet();
	/** All common candidates in the current combination of ALS. */
	private short aktDBCandidates = 0;
	/** The common candidates that were reduced by the current ALS. */
	private short[] incDBCand = new short[10];
	/** The indices of all ALS in the current try of the Death Blossom search. */
	private int[] aktDBAls = new int[10];
	/** All indices of all ALS in a DB for a given candidate. */
	private SudokuSet dbIndicesPerCandidate = new SudokuSet();
	/**
	 * Maximum candidate for which a recursive search for DeathBlossom has to be
	 * made.
	 */
	private int maxDBCand = 0;
	/** The index of the current stem cell for Death Blossom. */
	private int stemCellIndex = 0;
	/** For various checks */
	private SudokuSet tmpSet = new SudokuSet();
	/** For various checks */
	private SudokuSet tmpSet1 = new SudokuSet();
	/** Statistics: Number of calls. */
	private static int anzCalls = 0;
	/** Statistics: Time for collectAllAlses(). */
	private static long allAlsesNanos = 0;
	/** Statistics: Time for collectAllRestrictedCommons(). */
	private static long allRcsNanos = 0;
	/** Statistics: Total time. */
	private static long allNanos = 0;

	/**
	 * Creates a new instance of AlsSolver
	 * 
	 * @param finder
	 */
	public AlsSolver(SudokuStepFinder finder) {
		super(finder);
		if (alsComparator == null) {
			alsComparator = new AlsComparator();
		}
	}

	@Override
	protected SolutionStep getStep(SolutionType type) {
		SolutionStep result = null;
		sudoku = finder.getSudoku();
		// normal search: only forward references
		finder.setRcOnlyForward(true);
		switch (type) {
		case ALS_XZ:
			result = getAlsXZ(true);
			break;
		case ALS_XY_WING:
			result = getAlsXYWing(true);
			break;
		case ALS_XY_CHAIN:
			// chain search needs RCs in both directions (see #23)
			finder.setRcOnlyForward(false);
			maxChainLinks = MAX_RC;
			result = getAlsXYChain();
			break;
		case DEATH_BLOSSOM:
			result = getAlsDeathBlossom(true);
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	protected boolean doStep(SolutionStep step) {
		boolean handled = true;
		sudoku = finder.getSudoku();
		switch (step.getType()) {
		case ALS_XZ:
		case ALS_XY_WING:
		case ALS_XY_CHAIN:
		case DEATH_BLOSSOM:
			for (Candidate cand : step.getCandidatesToDelete()) {
				sudoku.delCandidate(cand.getIndex(), cand.getValue());
			}
			break;
		default:
			handled = false;
		}
		return handled;
	}

	/**
	 * Finds all ALS steps except Death Blossom present in the current grid. The
	 * parameters specify, which types should be searched.
	 * 
	 * @param doXz
	 * @param doXy
	 * @param doChain
	 * @return
	 */
	protected List<SolutionStep> getAllAlses(boolean doXz, boolean doXy, boolean doChain) {
		sudoku = finder.getSudoku();
		List<SolutionStep> oldSteps = steps;
		List<SolutionStep> resultSteps = new ArrayList<SolutionStep>();
		// ALS-XZ and ALS-XY-Wing only need RCs in one direction; RCs in both
		// directions would only produce duplicate steps (issue #48). The chain
		// search switches to both directions below (see #23).
		finder.setRcOnlyForward(true);
		maxChainLinks = Math.max(1, Math.min(MAX_RC, Options.getInstance().getAllStepsAlsChainLength()));
		long millis1 = 0;
		if (TIMING) {
			millis1 = System.nanoTime();
		}
		collectAllAlses();
		collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
		if (doXz) {
			steps.clear();
			getAlsXZInt(false);
			Collections.sort(steps, alsComparator);
			resultSteps.addAll(steps);
		}
		if (doXy) {
			steps.clear();
			getAlsXYWingInt(false);
			Collections.sort(steps, alsComparator);
			resultSteps.addAll(steps);
		}
		if (doChain) {
			steps.clear();
			if (finder.isRcOnlyForward()) {
				// chain search needs RCs in both directions (see #23)
				finder.setRcOnlyForward(false);
				collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
			}
			getAlsXYChainInt();
			Collections.sort(steps, alsComparator);
			resultSteps.addAll(steps);
		}
		if (TIMING) {
			millis1 = System.nanoTime() - millis1;
//            System.out.println("getAllAlses() total: " + (millis1 / 1000000.0) + "ms");
		}
		steps = oldSteps;
		return resultSteps;
	}

	/**
	 * Finds all Death Blossoms present in the current grid.
	 * 
	 * @return
	 */
	protected List<SolutionStep> getAllDeathBlossoms() {
		sudoku = finder.getSudoku();
		List<SolutionStep> oldSteps = steps;
		List<SolutionStep> resultSteps = new ArrayList<SolutionStep>();
		long millis1 = 0;
		if (TIMING) {
			millis1 = System.nanoTime();
		}
		collectAllAlses();
		collectAllRCsForDeathBlossom();
		steps.clear();
		getAlsDeathBlossomInt(false);
		Collections.sort(steps, alsComparator);
		resultSteps.addAll(steps);
		if (TIMING) {
			millis1 = System.nanoTime() - millis1;
//            System.out.println("getAllDeathBlossoms() total: " + millis1 + "ms");
		}
		steps = oldSteps;
		return resultSteps;
	}

	/**
	 * Finds the next Death Blossom. If <code>onlyOne</code> is set, the search
	 * stops after the first occurrence has been found.
	 *
	 * @param onlyOne
	 * @return Next step or <code>null</code> if no step could be found.
	 */
	private SolutionStep getAlsDeathBlossom(boolean onlyOne) {
		steps.clear();
		collectAllAlses();
		collectAllRCsForDeathBlossom();
		SolutionStep step = getAlsDeathBlossomInt(onlyOne);
		if (!onlyOne && steps.size() > 0) {
			Collections.sort(steps, alsComparator);
			step = steps.get(0);
		}
		return step;
	}

	/**
	 * Finds the next ALS Chain.
	 *
	 * @return Next step or <code>null</code> if no step could be found.
	 */
	private SolutionStep getAlsXYChain() {
		steps.clear();
		collectAllAlses();
		collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
		getAlsXYChainInt();
		if (steps.size() > 0) {
			Collections.sort(steps, alsComparator);
			return steps.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Finds the next ALS XY-Wing. If <code>onlyOne</code> is set, the search stops
	 * after the first occurrence has been found.
	 *
	 * @param onlyOne
	 * @return Next step or <code>null</code> if no step could be found.
	 */
	private SolutionStep getAlsXYWing(boolean onlyOne) {
		steps.clear();
		collectAllAlses();
		collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
		SolutionStep step = getAlsXYWingInt(onlyOne);
		if (!onlyOne && steps.size() > 0) {
			Collections.sort(steps, alsComparator);
			step = steps.get(0);
		}
		return step;
	}

	/**
	 * Finds the next ALS XZ. If <code>onlyOne</code> is set, the search stops after
	 * the first occurrence has been found.
	 *
	 * @param onlyOne
	 * @return Next step or <code>null</code> if no step could be found.
	 */
	private SolutionStep getAlsXZ(boolean onlyOne) {
		long nanos = 0;
		if (TIMING) {
			nanos = System.nanoTime();
		}
		anzCalls++;
		steps.clear();
		collectAllAlses();
		collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
		SolutionStep step = getAlsXZInt(onlyOne);
		if (!onlyOne && steps.size() > 0) {
			Collections.sort(steps, alsComparator);
			step = steps.get(0);
		}
		if (TIMING) {
			nanos = System.nanoTime() - nanos;
			allNanos += nanos;
		}
		return step;
	}

	/**
	 * Check all restricted commons: For every RC check all candidates common to
	 * both ALS but minus the RC candidate(s). If buddies exist outside the ALS they
	 * can be eliminated.<br>
	 * 
	 * Doubly linked ALS-XZ: If two ALS are linked by 2 RCs, the rest of each ALS
	 * becomes a locked set and eliminates additional candidates; plus each of the
	 * two RCs can be used for "normal" ALS-XZ eliminations.<br>
	 *
	 * @param onlyOne If <code>true</code> the search stops after the first step was
	 *                found.
	 * @return The first step found or null
	 */
	private SolutionStep getAlsXZInt(boolean onlyOne) {
		globalStep.reset();
		for (int i = 0; i < restrictedCommons.size(); i++) {
			RestrictedCommon rc = restrictedCommons.get(i);
			// only forward check necessary
			if (rc.getAls1() > rc.getAls2()) {
				continue;
			}
			Als als1 = alses.get(rc.getAls1());
			Als als2 = alses.get(rc.getAls2());
			checkCandidatesToDelete(als1, als2, rc.getCand1());
			if (rc.getCand2() != 0) {
				// als1 and als2 are doubly linked -> check for additional eliminations
				checkCandidatesToDelete(als1, als2, rc.getCand2());
				boolean d1 = checkDoublyLinkedAls(als1, als2, rc.getCand1(), rc.getCand2());
				boolean d2 = checkDoublyLinkedAls(als2, als1, rc.getCand1(), rc.getCand2());
				if (d1 || d2) {
					// no common candidates for doublylinked als-xz
					globalStep.getFins().clear();
				}
			}
			if (globalStep.getCandidatesToDelete().size() > 0) {
				// Step zusammenbauen
				globalStep.setType(SolutionType.ALS_XZ);
				globalStep.addAls(als1.indices, als1.candidates);
				globalStep.addAls(als2.indices, als2.candidates);
				addRestrictedCommonToStep(als1, als2, rc.getCand1(), false);
				if (rc.getCand2() != 0) {
					addRestrictedCommonToStep(als1, als2, rc.getCand2(), false);
				}
				SolutionStep step = (SolutionStep) globalStep.clone();
				if (onlyOne) {
					return step;
				}
				steps.add(step);
				globalStep.reset();
			}
		}
		return null;
	}

	/**
	 * Check all combinations of two RCs and check whether it is possible to
	 * construct an ALS XY-Wing:
	 * <ul>
	 * <li>we need three different ALS</li>
	 * <li>if RC1 and RC2 both have only one candidate that candidate must
	 * differ</li>
	 * </ul>
	 * 
	 * If a valid combination could be found, identify ALS C and check ALS A and B
	 * for possible eliminations.
	 *
	 * @param onlyOne If <code>true</code> the search stops after the first step was
	 *                found.
	 * @return The first step found or null
	 */
	private SolutionStep getAlsXYWingInt(boolean onlyOne) {
		globalStep.reset();
		for (int i = 0; i < restrictedCommons.size(); i++) {
			RestrictedCommon rc1 = restrictedCommons.get(i);
			for (int j = i + 1; j < restrictedCommons.size(); j++) {
				RestrictedCommon rc2 = restrictedCommons.get(j);
				// at least two different candidates in rc1 and rc2!
				// must always be true, if the two rcs have a different
				// number of digits;
				if (rc1.getCand2() == 0 && rc2.getCand2() == 0 && rc1.getCand1() == rc2.getCand1()) {
					// both RCs have only one digit and the digits dont differ
					continue;
				}
				// the two RCs have to connect 3 different ALS; since
				// rc1.als1 != rc1.als2 && rc2.als1 != rc2.als2 not many possibilites are left
				if (!((rc1.getAls1() == rc2.getAls1() && rc1.getAls2() != rc2.getAls2())
						|| (rc1.getAls2() == rc2.getAls1() && rc1.getAls1() != rc2.getAls2())
						|| (rc1.getAls1() == rc2.getAls2() && rc1.getAls2() != rc2.getAls1())
						|| (rc1.getAls2() == rc2.getAls2() && rc1.getAls1() != rc2.getAls1()))) {
					// cant be an XY-Wing
					continue;
				}
				// Identify C so we can check for eliminations
				Als a = null;
				Als b = null;
				Als c = null;
				if (rc1.getAls1() == rc2.getAls1()) {
					c = alses.get(rc1.getAls1());
					a = alses.get(rc1.getAls2());
					b = alses.get(rc2.getAls2());
				}
				if (rc1.getAls1() == rc2.getAls2()) {
					c = alses.get(rc1.getAls1());
					a = alses.get(rc1.getAls2());
					b = alses.get(rc2.getAls1());
				}
				if (rc1.getAls2() == rc2.getAls1()) {
					c = alses.get(rc1.getAls2());
					a = alses.get(rc1.getAls1());
					b = alses.get(rc2.getAls2());
				}
				if (rc1.getAls2() == rc2.getAls2()) {
					c = alses.get(rc1.getAls2());
					a = alses.get(rc1.getAls1());
					b = alses.get(rc2.getAls1());
				}
				if (!Options.getInstance().isAllowAlsOverlap()) {
					// Check overlaps: the RCs have already been checked, a and b are missing:
					tmpSet.set(a.indices);
					if (!tmpSet.andEmpty(b.indices)) {
						// overlap -> not allowed
						continue;
					}
				}
				// even if overlaps are allowed, a(b) must not be a subset of b (a)
				tmpSet.set(a.indices);
				tmpSet.or(b.indices);
				if (tmpSet.equals(a.indices) || tmpSet.equals(b.indices)) {
					continue;
				}
				// now check candidates of A and B
				checkCandidatesToDelete(a, b, rc1.getCand1(), rc1.getCand2(), rc2.getCand1(), rc2.getCand2());
				if (globalStep.getCandidatesToDelete().size() > 0) {
					// Step zusammenbauen
					globalStep.setType(SolutionType.ALS_XY_WING);
					globalStep.addAls(a.indices, a.candidates);
					globalStep.addAls(b.indices, b.candidates);
					globalStep.addAls(c.indices, c.candidates);
					addRestrictedCommonToStep(a, c, rc1.getCand1(), false);
					if (rc1.getCand2() != 0) {
						addRestrictedCommonToStep(a, c, rc1.getCand2(), false);
					}
					addRestrictedCommonToStep(b, c, rc2.getCand1(), false);
					if (rc2.getCand2() != 0) {
						addRestrictedCommonToStep(b, c, rc2.getCand2(), false);
					}
					SolutionStep step = (SolutionStep) globalStep.clone();
					if (onlyOne) {
						return step;
					}
					steps.add(step);
					globalStep.reset();
				}
			}
		}
		return null;
	}

	/**
	 * Check all combinations starting with every ALS. The following rules are
	 * applied:
	 * <ul>
	 * <li>Two adjacent ALS may overlap as long as the overlapping area doesnt
	 * contain an RC</li>
	 * <li>Two non adjacent ALS may overlap without restrictions</li>
	 * <li>If the first and last ALS are identical or if the first ALS is contained
	 * in the last or vice versa the chain becomes a loop -> currently not
	 * implemented</li>
	 * <li>Two adjacent RCs must follow the adjency rules (see below)</li>
	 * <li>each chain must be at least 4 ALS long (2 is an XZ, 3 is an XY-Wing)</li>
	 * <li>start and end ALS must have a common candidate that exists outside the
	 * chain -> can be eliminated (the candidate can be eliminated from the chain as
	 * well but not from the start and end ALS -> cannibalistic ALS-chain)</li>
	 * </ul>
	 * 
	 * Adjacency rules for RCs joining ALS1 and ALS2:
	 * <ul>
	 * <li>get all RCs between ALS1 and ALS2 ("possible RCs" - "PRC")</li>
	 * <li>subtract the "actual RCs" ("ARC") of the previous step; the remainder
	 * becomes the new ARC(s)</li>
	 * <li>if no ARC is left, the chain ends at ALS1</li>
	 * </ul>
	 * 
	 * If a new ALS is already contained within the chain, the chain becomes a whip
	 * (not handled)<br>
	 * <br>
	 * The search runs in both directions over all RCs (they must be collected with
	 * <code>rcOnlyForward == false</code>) as a breadth-first search over chain
	 * states, see {@link #searchAlsChainStates(int, int)}. The old depth-first
	 * search over all paths was exponential when run in both directions and missed
	 * chains when run forward only (issue #23).
	 */
	private void getAlsXYChainInt() {
		recDepth = 0;
		maxRecDepth = 0;
		deletesMap.clear();
		int alsCount = alses.size();
		int stateSpace = alsCount * STATE_STRIDE;
		if (stateStamp == null || stateStamp.length < stateSpace) {
			stateStamp = new int[stateSpace];
			stampGeneration = 0;
		}
		for (int i = 0; i < alsCount; i++) {
			startAls = alses.get(i);
			// collect the possible first actual RCs: every candidate of every RC leaving
			// the start ALS can be chosen freely
			short firstDigits = 0;
			for (int k = startIndices[i]; k < endIndices[i]; k++) {
				RestrictedCommon rc = restrictedCommons.get(k);
				firstDigits |= Sudoku2.MASKS[rc.getCand1()];
				if (rc.getCand2() != 0) {
					firstDigits |= Sudoku2.MASKS[rc.getCand2()];
				}
			}
			int[] digits = Sudoku2.POSSIBLE_VALUES[firstDigits];
			for (int d = 0; d < digits.length; d++) {
				searchAlsChainStates(i, digits[d]);
			}
		}
		if (DEBUG) {
			System.out.println(steps.size() + " (max chain length: " + maxRecDepth + ")");
		}
	}

	/** Number of state slots per ALS: 3 depth buckets x 1024 digit masks. */
	private static final int STATE_STRIDE = 3 * 1024;
	/** Generation stamps for the state table (avoids clearing it for every search). */
	private int[] stateStamp = null;
	/** Current generation for {@link #stateStamp}. */
	private int stampGeneration = 0;
	/** ALS index of every state in the breadth-first chain search. */
	private int[] stAls = new int[1024];
	/** Digits excluded from the ALS by the incoming link (bit mask, bit = digit). */
	private int[] stIn = new int[1024];
	/** Number of links from the start ALS to the state. */
	private int[] stDepth = new int[1024];
	/** Parent state (-1 for the first link). */
	private int[] stParent = new int[1024];
	/** Index of the incoming RC in {@link #restrictedCommons}. */
	private int[] stRc = new int[1024];
	/** Actual RC of the incoming link (1: cand1, 2: cand2, 3: both). */
	private int[] stActual = new int[1024];

	/**
	 * Breadth-first search over ALS chain states for one start ALS and one first
	 * actual RC.<br>
	 * <br>
	 * Whether a chain allows eliminations depends only on the start ALS, the actual
	 * RC leaving it, the current (end) ALS and the actual RC(s) entering the end
	 * ALS, not on the ALS in between. A state is therefore (ALS, digits excluded
	 * from that ALS by the incoming link). Every state is expanded once per depth
	 * bucket (1, 2 and 3+ links, so that a state first reached as an ALS-XZ or
	 * ALS-XY-Wing can still be checked as a chain), and the shortest chain to it is
	 * kept.<br>
	 * <br>
	 * This replaces the old depth-first search over all paths, which was
	 * exponential without the "forward only" restriction and incomplete with it
	 * (issue #23): forward only found a chain only if the list indices of its ALS
	 * were monotonic.<br>
	 * <br>
	 * Adjacency rule: the actual RCs of a link are the RC candidates that were not
	 * excluded by the previous link. Chains that would revisit an ALS (including
	 * returning to the start ALS) are not reported.
	 *
	 * @param startIndex index of the start ALS
	 * @param firstDigit actual RC of the first link
	 */
	private void searchAlsChainStates(int startIndex, int firstDigit) {
		int maxLinks = maxChainLinks;
		stampGeneration++;
		if (stampGeneration == Integer.MAX_VALUE) {
			Arrays.fill(stateStamp, 0);
			stampGeneration = 1;
		}
		int count = 0;
		// initial states: all RCs leaving the start ALS that contain firstDigit
		for (int k = startIndices[startIndex]; k < endIndices[startIndex]; k++) {
			RestrictedCommon rc = restrictedCommons.get(k);
			int actual;
			if (rc.getCand1() == firstDigit) {
				actual = 1;
			} else if (rc.getCand2() == firstDigit) {
				actual = 2;
			} else {
				continue;
			}
			int next = rc.getAls2();
			if (next == startIndex) {
				continue;
			}
			count = addChainState(count, next, 1 << firstDigit, 1, -1, k, actual);
		}
		for (int s = 0; s < count; s++) {
			int als = stAls[s];
			int in = stIn[s];
			int depth = stDepth[s];
			if (depth > maxRecDepth) {
				maxRecDepth = depth;
			}
			if (depth >= 3) {
				checkAlsChainState(startIndex, firstDigit, s);
			}
			if (depth >= maxLinks) {
				continue;
			}
			for (int k = startIndices[als]; k < endIndices[als]; k++) {
				RestrictedCommon rc = restrictedCommons.get(k);
				int next = rc.getAls2();
				if (next == startIndex) {
					continue;
				}
				int out = 0;
				int actual = 0;
				if ((in & (1 << rc.getCand1())) == 0) {
					out |= 1 << rc.getCand1();
					actual |= 1;
				}
				if (rc.getCand2() != 0 && (in & (1 << rc.getCand2())) == 0) {
					out |= 1 << rc.getCand2();
					actual |= 2;
				}
				if (actual == 0) {
					// adjacency rule violated
					continue;
				}
				count = addChainState(count, next, out, depth + 1, s, k, actual);
			}
		}
	}

	/**
	 * Adds a new state to the breadth-first queue, if it has not been seen yet.
	 *
	 * @return the new number of states
	 */
	private int addChainState(int count, int als, int in, int depth, int parent, int rcIndex, int actual) {
		int bucket = depth >= 3 ? 2 : depth - 1;
		int key = als * STATE_STRIDE + bucket * 1024 + in;
		if (stateStamp[key] == stampGeneration) {
			return count;
		}
		stateStamp[key] = stampGeneration;
		if (count == stAls.length) {
			int newLength = stAls.length * 2;
			stAls = Arrays.copyOf(stAls, newLength);
			stIn = Arrays.copyOf(stIn, newLength);
			stDepth = Arrays.copyOf(stDepth, newLength);
			stParent = Arrays.copyOf(stParent, newLength);
			stRc = Arrays.copyOf(stRc, newLength);
			stActual = Arrays.copyOf(stActual, newLength);
		}
		stAls[count] = als;
		stIn[count] = in;
		stDepth[count] = depth;
		stParent[count] = parent;
		stRc[count] = rcIndex;
		stActual[count] = actual;
		return count + 1;
	}

	/**
	 * Checks a chain state (at least 4 ALS) for eliminations; if there are any, the
	 * chain is rebuilt from the parent pointers and written as step.
	 *
	 * @param startIndex index of the start ALS
	 * @param firstDigit actual RC of the first link
	 * @param state      index of the state to check
	 */
	private void checkAlsChainState(int startIndex, int firstDigit, int state) {
		Als endAls = alses.get(stAls[state]);
		RestrictedCommon lastRc = restrictedCommons.get(stRc[state]);
		int c3 = 0, c4 = 0;
		int lastActual = stActual[state];
		if (lastActual == 1) {
			c3 = lastRc.getCand1();
		} else if (lastActual == 2) {
			c3 = lastRc.getCand2();
		} else {
			c3 = lastRc.getCand1();
			c4 = lastRc.getCand2();
		}
		globalStep.reset();
		checkCandidatesToDelete(startAls, endAls, firstDigit, 0, c3, c4, null);
		if (globalStep.getCandidatesToDelete().isEmpty()) {
			return;
		}
		// rebuild the chain (from the end to the start)
		int links = stDepth[state];
		if (alsInChain == null || alsInChain.length < alses.size()) {
			alsInChain = new boolean[alses.size()];
		} else {
			Arrays.fill(alsInChain, false);
		}
		alsInChain[startIndex] = true;
		for (int s = state, j = links - 1; s >= 0; s = stParent[s], j--) {
			if (alsInChain[stAls[s]]) {
				// the shortest walk to this state visits an ALS twice -> not a chain
				globalStep.reset();
				return;
			}
			alsInChain[stAls[s]] = true;
			RestrictedCommon rc = restrictedCommons.get(stRc[s]);
			chain[j] = new RestrictedCommon(rc.getAls1(), rc.getAls2(), rc.getCand1(), rc.getCand2(), stActual[s]);
		}
		chainIndex = links;
		firstRC = chain[0];
		// chain found: build it and write it (eliminations are already in globalStep)
		globalStep.setType(SolutionType.ALS_XY_CHAIN);
		globalStep.addAls(startAls.indices, startAls.candidates);
		Als tmpAls = startAls;
		for (int j = 0; j < links; j++) {
			Als tmp = alses.get(chain[j].getAls2());
			globalStep.addAls(tmp.indices, tmp.candidates);
			globalStep.addRestrictedCommon((RestrictedCommon) chain[j].clone());
			if (chain[j].getActualRC() == 1 || chain[j].getActualRC() == 3) {
				addRestrictedCommonToStep(tmpAls, tmp, chain[j].getCand1(), true);
			}
			if (chain[j].getActualRC() == 2 || chain[j].getActualRC() == 3) {
				addRestrictedCommonToStep(tmpAls, tmp, chain[j].getCand2(), true);
			}
			tmpAls = tmp;
		}

		// check if we already have a chain for the given set of eliminations.
		// if we do, the new chain is only written, if it is shorter than the old one.
		boolean writeIt = true;
		int replaceIndex = -1;
		String elim = null;
		if (Options.getInstance().isOnlyOneAlsPerStep()) {
			globalStep.sortCandidatesToDelete(); // keep the deletion order (issue #36)
			elim = globalStep.getCandidateString();
			Integer alreadyThere = deletesMap.get(elim);
			if (alreadyThere != null) {
				// a step already exists!
				SolutionStep tmp = steps.get(alreadyThere);
				if (tmp.getAlsesIndexCount() > globalStep.getAlsesIndexCount()) {
					writeIt = true;
					replaceIndex = alreadyThere;
				} else {
					writeIt = false;
				}
			}
		}
		if (writeIt) {
			if (replaceIndex != -1) {
				steps.remove(replaceIndex);
				steps.add(replaceIndex, (SolutionStep) globalStep.clone());
			} else {
				steps.add((SolutionStep) globalStep.clone());
				if (elim != null) {
					deletesMap.put(elim, steps.size() - 1);
				}
			}
		}
		globalStep.reset();
	}

	/**
	 * For debugging only: show the current state of {@link #chain}.
	 * 
	 * @param recDepth
	 */
	@SuppressWarnings("unused")
	private void showActAlsChain(int recDepth) {
		if (DEBUG) {
			globalStep.reset();
			globalStep.setType(SolutionType.ALS_XY_CHAIN);
			globalStep.addAls(startAls.indices, startAls.candidates);
			Als tmpAls = startAls;
			for (int j = 0; j < chainIndex; j++) {
				Als tmp = alses.get(chain[j].getAls2());
				globalStep.addAls(tmp.indices, tmp.candidates);
				globalStep.addRestrictedCommon((RestrictedCommon) chain[j].clone());

				// write all RCs for this chain (nothing has been done yet)
				// if (DEBUG) System.out.println("chain[" + j + "]: " + chain[j] + " (" + tmpAls
				// + "/" + tmp + ")");
				if (chain[j].getActualRC() == 1 || chain[j].getActualRC() == 3) {
					addRestrictedCommonToStep(tmpAls, tmp, chain[j].getCand1(), true);
				}
				if (chain[j].getActualRC() == 2 || chain[j].getActualRC() == 3) {
					addRestrictedCommonToStep(tmpAls, tmp, chain[j].getCand2(), true);
				}
				tmpAls = tmp;
			}
			for (int i = 0; i < recDepth; i++) {
				System.out.print(" ");
			}
			System.out.println(globalStep.toString(2));
		}
	}

	/**
	 * Searches for all available Death blossoms: if a cell exists that has at least
	 * one ALS for every candidate check all combinations of available ALS for that
	 * cell. Any combination of (non overlapping) ALS has to be checked for common
	 * candidates that can eliminate candidates outside the ALS and the stem
	 * cell.<br>
	 * <br>
	 *
	 * The ALS/cell combinations must already have been written to {@link #rcdb}.
	 *
	 * @param onlyOne
	 * @return
	 */
	private SolutionStep getAlsDeathBlossomInt(boolean onlyOne) {
		deletesMap.clear();
		globalStep.reset();
		globalStep.setType(SolutionType.DEATH_BLOSSOM);
		for (int i = 0; i < Sudoku2.LENGTH; i++) {
			if (sudoku.getValue(i) != 0) {
				// cell already set -> ignore
				continue;
			}
			if (rcdb[i] == null || sudoku.getCells()[i] != rcdb[i].candMask) {
				// the cell cant see any ALS or
				// there are candidates left without ALS -> impossible
				// System.out.println("Cell " + i + ": " + rcdb[i].candMask + "/" +
				// cells[i].getCandidateMask(candType));
				continue;
			}
			// ok here it starts: try all combinations of ALS
			stemCellIndex = i;
			aktRcdb = rcdb[i];
			maxDBCand = 0;
			for (int j = 1; j <= 9; j++) {
				if (aktRcdb.indices[j] > 0) {
					maxDBCand = j;
				}
			}
			aktDBIndices.clear();
			aktDBCandidates = Sudoku2.MAX_MASK;
//            aktDBCandidates.setAll();
			for (int j = 0; j < aktDBAls.length; j++) {
				aktDBAls[j] = -1;
			}
			SolutionStep step = checkAlsDeathBlossomRecursive(1, onlyOne);
			if (onlyOne && step != null) {
				return step;
			}
		}
		return null;
	}

	/**
	 * Recursively tries all ALS for <code>candidate</code> in the cell
	 * {@link #stemCellIndex}. If <code>candidate</code> equals {@link #maxDBCand}
	 * eliminations have to be checked.
	 * 
	 * @param cand
	 * @param onlyOne
	 * @return
	 */
	private SolutionStep checkAlsDeathBlossomRecursive(int cand, boolean onlyOne) {
		if (cand > maxDBCand) {
			// nothing left to do
			return null;
		}
		if (aktRcdb.indices[cand] > 0) {
			// There are ALS to try
			for (int i = 0; i < aktRcdb.indices[cand]; i++) {
				Als als = alses.get(aktRcdb.alsPerCandidate[cand][i]);
				// if (DEBUG) System.out.println("cand = " + cand + ", i = " + i + ", ALS: " +
				// als.toString());
				// check for overlap
				if (!Options.getInstance().isAllowAlsOverlap() && !als.indices.andNotEquals(aktDBIndices)) {
					// new ALS overlaps -> we dont need to look further
					// if (DEBUG) System.out.println(" Overlap!");
					continue;
				}
				// check for common candidates
				short tmpCandSet = aktDBCandidates;
				if ((tmpCandSet & als.candidates) == 0) {
					// no common candidates -> nothing to do
					// if (DEBUG) System.out.println(" No common candidates: " + aktDBCandidates +
					// "|||" + als.candidates);
					continue;
				}
				// ALS is valid (overlap) and common candidates exist
				aktDBAls[cand] = aktRcdb.alsPerCandidate[cand][i];
				// if (DEBUG) System.out.println(" setting aktDBAls[" + cand + "] = " +
				// aktRcdb.alsPerCandidate[cand][i]);
				// get the candidates that are deleted from aktDBCandidates by als
				incDBCand[cand] = aktDBCandidates;
				incDBCand[cand] &= ~als.candidates;
				// now get the common candidates of the new combination
				aktDBCandidates &= als.candidates;
				// add the new indices
				aktDBIndices.or(als.indices);
				if (cand < maxDBCand) {
					// look further
					SolutionStep step = checkAlsDeathBlossomRecursive(cand + 1, onlyOne);
					if (onlyOne && step != null) {
						return step;
					}
				} else {
					// a valid ALS combination: check for eliminations
					// if (DEBUG) System.out.println(" Valid combination!");
					boolean found = false;
					int[] cands = Sudoku2.POSSIBLE_VALUES[aktDBCandidates];
					for (int j = 0; j < cands.length; j++) {
						int checkCand = cands[j];
						if (aktDBAls[checkCand] != -1) {
							// checkCand is used in the stemCell -> cant eliminate anything
							// if (DEBUG) System.out.println(" checkCand " + checkCand + " skipped!");
							continue;
						}
						boolean first = true;
						for (int k = 0; k < aktDBAls.length; k++) {
							if (aktDBAls[k] == -1) {
								// no ALS for that candidate
								continue;
							}
							if (first) {
								dbIndicesPerCandidate.set(alses.get(aktDBAls[k]).indicesPerCandidat[checkCand]);
								first = false;
							} else {
								dbIndicesPerCandidate.or(alses.get(aktDBAls[k]).indicesPerCandidat[checkCand]);
							}
						}
						Sudoku2.getBuddies(dbIndicesPerCandidate, tmpSet);
						// no cannibalism
						tmpSet.andNot(aktDBIndices);
						// not in the stemCell
						tmpSet.remove(stemCellIndex);

						// possible eliminations?
						// if (DEBUG) System.out.println(" checkCand = " + checkCand + ", buddies: " +
						// tmpSet);
						tmpSet.and(finder.getCandidates()[checkCand]);
						// if (DEBUG) System.out.println(" eliminations: " + tmpSet);
						if (!tmpSet.isEmpty()) {
							// we found a Death Blossom
							// record the eliminations
							found = true;
							for (int k = 0; k < tmpSet.size(); k++) {
								globalStep.addCandidateToDelete(tmpSet.get(k), checkCand);
							}
						}
					}
					// if eliminations were found, record the step
					if (found) {
						globalStep.addIndex(stemCellIndex);
						// for every ALS record the RCs as fins and add the als
						for (int k = 1; k <= 9; k++) {
							if (aktDBAls[k] == -1) {
								continue;
							}
							Als tmpAls = alses.get(aktDBAls[k]);
							for (int l = 0; l < tmpAls.indicesPerCandidat[k].size(); l++) {
								globalStep.addFin(tmpAls.indicesPerCandidat[k].get(l), k);
							}
							globalStep.addFin(stemCellIndex, k);
							globalStep.addAls(tmpAls.indices, tmpAls.candidates);
							globalStep.addRestrictedCommon(new RestrictedCommon(0, 0, k, 0, 1));
						}

						boolean writeIt = true;
						int replaceIndex = -1;
						String elim = null;
						if (Options.getInstance().isOnlyOneAlsPerStep()) {
							globalStep.sortCandidatesToDelete(); // keep the deletion order (issue #36)
							elim = globalStep.getCandidateString();
							Integer alreadyThere = deletesMap.get(elim);
							if (alreadyThere != null) {
								// a step already exists!
								SolutionStep tmp = steps.get(alreadyThere);
								if (tmp.getAlsesIndexCount() > globalStep.getAlsesIndexCount()) {
									writeIt = true;
									replaceIndex = alreadyThere;
								} else {
									writeIt = false;
								}
							}
						}
						if (writeIt) {
							if (replaceIndex != -1) {
								steps.remove(replaceIndex);
								steps.add(replaceIndex, (SolutionStep) globalStep.clone());
							} else {
								SolutionStep step = (SolutionStep) globalStep.clone();
								if (onlyOne) {
									return step;
								}
								steps.add(step);
								if (elim != null) {
									deletesMap.put(elim, steps.size() - 1);
								}
							}
						}
						globalStep.reset();
						globalStep.setType(SolutionType.DEATH_BLOSSOM);
					}
				}
				// and back again
				aktDBCandidates |= incDBCand[cand];
				aktDBIndices.andNot(als.indices);
			}
		} else {
			// nothing to do -> next candidate
			aktDBAls[cand] = -1;
			SolutionStep step = checkAlsDeathBlossomRecursive(cand + 1, onlyOne);
			if (onlyOne && step != null) {
				return step;
			}
		}
		return null;
	}

	/**
	 * Convenience method, delegates to
	 * {@link #checkCandidatesToDelete(solver.Als, solver.Als, int, int, int, int, sudoku.SudokuSet)}.
	 */
	private void checkCandidatesToDelete(Als als1, Als als2, int restr1) {
		checkCandidatesToDelete(als1, als2, restr1, -1, -1, -1, null);
	}

	/**
	 * Convenience method, delegates to
	 * {@link #checkCandidatesToDelete(solver.Als, solver.Als, int, int, int, int, sudoku.SudokuSet)}.
	 */
	private void checkCandidatesToDelete(Als als1, Als als2, int restr1, int restr2, int restr3, int restr4) {
		checkCandidatesToDelete(als1, als2, restr1, restr2, restr3, restr4, null);
	}

	/**
	 * Used for XZ, XY-Wing and Chain: Check the common candidates of als1 and als2
	 * (minus all restrx). If candidates exist, that are outside (als1 + als2) and
	 * see all occurences of one of the common candidates (see above) they can be
	 * eliminated.<br>
	 * 
	 * @param als1             The first flanking ALS
	 * @param als2             The second flanking ALS
	 * @param als3             The middle ALS in an XY-Wing, the second last ALS for
	 *                         an ALS Chain; only used for correctly adding the RCs
	 *                         to the step
	 * @param restr1           First RC (unused if -1)
	 * @param restr2           Second RC (unused if -1)
	 * @param restr3           Third RC (unused if -1)
	 * @param restr4           Fourth RC (unused if -1)
	 * @param forChain         True if method is called for a chain: No restricted
	 *                         commons are added to step
	 * @param forbiddenIndices If not null describes the cells where no eliminations
	 *                         are allowed (can be set for non cannibalistic chains)
	 */
	private void checkCandidatesToDelete(Als als1, Als als2, int restr1, int restr2, int restr3, int restr4,
			SudokuSet forbiddenIndices) {
		// boolean rcWritten = false;
		possibleRestrictedCommonsSet = als1.candidates;
		possibleRestrictedCommonsSet &= als2.candidates;
		if (restr1 != -1 && restr1 != 0) {
			possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[restr1];
		}
		if (restr2 != -1 && restr2 != 0) {
			possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[restr2];
		}
		if (restr3 != -1 && restr3 != 0) {
			possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[restr3];
		}
		if (restr4 != -1 && restr4 != 0) {
			possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[restr4];
		}
		// possibleRestrictedCommonsSet now contains all candidates from both
		// ALS except the RCs themselves
		if (possibleRestrictedCommonsSet == 0) {
			// nothing to do
			return;
		}
		// check if there are any buddies
		tmpSet.set(als1.buddies);
		if (tmpSet.andEmpty(als2.buddies)) {
			// no common buddies -> no eliminations!
			return;
		}
		// get all cells from all ALS (als3 may be null!)
		if (forbiddenIndices != null) {
			tmpSet.set(forbiddenIndices);
		} else {
			// in an ALS-XY candidates may be eliminated from ALS c!
			tmpSet.set(als1.indices);
			tmpSet.or(als2.indices);
		}
		// check all candidates common to both ALS that are not RCs
		int[] prcs = Sudoku2.POSSIBLE_VALUES[possibleRestrictedCommonsSet];
		for (int j = 0; j < prcs.length; j++) {
			int cand = prcs[j];
			// get all cells that can see all occurrences of cand in both als
			restrictedCommonBuddiesSet.set(als1.buddiesPerCandidat[cand]);
			restrictedCommonBuddiesSet.and(als2.buddiesPerCandidat[cand]);
			// eliminate forbidden cells
			if (forbiddenIndices != null) {
				restrictedCommonBuddiesSet.andNot(forbiddenIndices);
			}
			if (!restrictedCommonBuddiesSet.isEmpty()) {
				// found one -> can be eliminated
				for (int l = 0; l < restrictedCommonBuddiesSet.size(); l++) {
					globalStep.addCandidateToDelete(restrictedCommonBuddiesSet.get(l), cand);
				}
				// add the common candidates themselves as fins (for display only)
				tmpSet1.set(als1.indicesPerCandidat[cand]);
				tmpSet1.or(als2.indicesPerCandidat[cand]);
				for (int l = 0; l < tmpSet1.size(); l++) {
					globalStep.addFin(tmpSet1.get(l), cand);
				}
			}
		}
	}

	/**
	 * Adds all cells that contain <code>cand</code> in both <code>als1</code> and
	 * <code>als2</code> as endo fins to the step (only for display). If
	 * <code>withChain</code> is set, a chain is added between the pair of
	 * candidates in both ALS that have the smallest distance.
	 * 
	 * @param als1
	 * @param als2
	 * @param cand
	 * @param withChain
	 */
	private void addRestrictedCommonToStep(Als als1, Als als2, int cand, boolean withChain) {
		// get all cells in both als that contain cand
		tmpSet.set(als1.indicesPerCandidat[cand]);
		tmpSet.or(als2.indicesPerCandidat[cand]);
		for (int i = 0; i < tmpSet.size(); i++) {
			// add them as endo fins
			globalStep.addEndoFin(tmpSet.get(i), cand);
		}
		if (withChain) {
			// create a chain for the smallest distance
			int minDist = Integer.MAX_VALUE;
			int minIndex1 = -1;
			int minIndex2 = -1;
			for (int i1 = 0; i1 < als1.indicesPerCandidat[cand].size(); i1++) {
				for (int i2 = 0; i2 < als2.indicesPerCandidat[cand].size(); i2++) {
					int index1 = als1.indicesPerCandidat[cand].get(i1);
					int index2 = als2.indicesPerCandidat[cand].get(i2);
					int dx = Sudoku2.getRow(index1) - Sudoku2.getRow(index2);
					int dy = Sudoku2.getCol(index1) - Sudoku2.getCol(index2);
					int dist = dx * dx + dy * dy;
					if (dist < minDist) {
						minDist = dist;
						minIndex1 = index1;
						minIndex2 = index2;
					}
				}
			}
			int[] tmpChain = new int[2];
			tmpChain[0] = Chain.makeSEntry(minIndex1, cand, false);
			tmpChain[1] = Chain.makeSEntry(minIndex2, cand, false);
			globalStep.addChain(0, 1, tmpChain);
		}
	}

	/**
	 * als1 and als2 are doubly linked by RCs rc1 and rc2; check whether the locked
	 * set {als1 - rc1 - rc2 } can eliminate candidates that are not in als2.<br>
	 * <br>
	 * 
	 * The method has to be called twice with als1 and als2 swapped.<br>
	 * 
	 * @param als1 The als that becomes a locked set
	 * @param als2 The doubly linked second als, no candidates can be eliminated
	 *             from it
	 * @param rc1  The first Restricted Common
	 * @param rc2  The second Restricted Common
	 */
	private boolean checkDoublyLinkedAls(Als als1, Als als2, int rc1, int rc2) {
		boolean isDoubly = false;
		// collect the remaining candidates
		possibleRestrictedCommonsSet = als1.candidates;
		possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[rc1];
		possibleRestrictedCommonsSet &= ~Sudoku2.MASKS[rc2];
		if (possibleRestrictedCommonsSet == 0) {
			// nothing can be eliminated
			return false;
		}
		// for any candidate left get all buddies, subtract als1 and als2 and check for
		// eliminations
		int[] prcs = Sudoku2.POSSIBLE_VALUES[possibleRestrictedCommonsSet];
		for (int i = 0; i < prcs.length; i++) {
			int cand = prcs[i];
			restrictedCommonIndexSet.set(als1.buddiesPerCandidat[cand]);
			restrictedCommonIndexSet.andNot(als2.indices);
			if (!restrictedCommonIndexSet.isEmpty()) {
				for (int j = 0; j < restrictedCommonIndexSet.size(); j++) {
					globalStep.addCandidateToDelete(restrictedCommonIndexSet.get(j), cand);
					isDoubly = true;
				}
			}
		}
		return isDoubly;
	}

	/**
	 * For all combinations of two ALS check whether they have one or two RC(s). An
	 * RC is a candidate that is common to both ALS and where all instances of that
	 * candidate in both ALS see each other.<br>
	 * ALS with RC(s) may overlap as long as the overlapping area doesnt contain an
	 * RC.<br>
	 * Two ALS can have a maximum of two RCs.<br>
	 * The index of the first RC for {@link #alses}[i] is written to
	 * {@link #startIndices}[i], the index of the last RC is written to
	 * {@link #endIndices}[i] (needed for chain search).
	 *
	 * @param withOverlap If <code>false</code> overlapping ALS are not allowed
	 */
	private void collectAllRestrictedCommons(boolean withOverlap) {
		long ticks = 0;
		if (TIMING) {
//                System.out.println("Entering collectAllRestrictedCommons");
			ticks = System.nanoTime();
		}
		restrictedCommons = finder.getRestrictedCommons(alses, withOverlap);
		startIndices = finder.getStartIndices();
		endIndices = finder.getEndIndices();
		if (TIMING) {
			ticks = System.nanoTime() - ticks;
			allRcsNanos += ticks;
//                System.out.println("collectAllRestrictedCommons(): " + ticks + "ms; restrictedCommon size: " + restrictedCommons.size());
		}
		if (DEBUG) {
			System.out.println("collectAllRestrictedCommons(): " + (ticks / 1000000.0) + "ms; restrictedCommon size: "
					+ restrictedCommons.size());
		}
	}

	/**
	 * Collects all available ALS in the current grid by simply referring to
	 * {@link Als#getAlses(sudoku.Sudoku2) }. The only purpose of this method is to
	 * provide timing and statistics data.
	 */
	private void collectAllAlses() {
		long ticks = 0;
		if (TIMING) {
			ticks = System.nanoTime();
		}
		alses = finder.getAlses();
		if (TIMING) {
			ticks = System.nanoTime() - ticks;
			allAlsesNanos += ticks;
		}
		if (DEBUG) {
			System.out.println("collectAllAlses(): " + (ticks / 1000000.0) + "ms; alses size: " + alses.size());
		}
	}

	/**
	 * Collect all cells, that can see all instances of a candidate within an ALS.
	 * For every cell that is not yet set an instance of {@link RCForDeathBlossom}
	 * is created and stored in {@link #rcdb}. In another method
	 * <code>rcdb[i].candMask</code> is checked against the real candidate mask of
	 * the cell: if they are equal, a possible Death Blossom exists.<br>
	 * <br>
	 * 
	 * Calculate all buddies for all candidates in all ALSs -> they are all possible
	 * stem cells.
	 */
	private void collectAllRCsForDeathBlossom() {
		long ticks = 0;
		if (TIMING) {
			ticks = System.nanoTime();
		}
		// initialize rcdb
		for (int i = 0; i < rcdb.length; i++) {
			Arrays.fill(rcdb, null);
		}
		// go over all ALS
		for (int i = 0; i < alses.size(); i++) {
			Als act = alses.get(i);
			// check all candidate in the current ALS
			for (int j = 1; j <= 9; j++) {
				if ((act.candidates & Sudoku2.MASKS[j]) == 0) {
					// candidate not in als -> nothing to do
					continue;
				}
				for (int k = 0; k < act.buddiesPerCandidat[j].size(); k++) {
					int index = act.buddiesPerCandidat[j].get(k);
					if (rcdb[index] == null) {
						rcdb[index] = new RCForDeathBlossom();
					}
					rcdb[index].addAlsForCandidate(i, j);
				}
			}
		}
		if (TIMING) {
			ticks = System.nanoTime() - ticks;
//            System.out.println("collectAllRCsForDeathBlossom(): " + ticks + "ms");
		}
		// DEBUG printout
//        if (DEBUG) {
//            for (int i = 0; i < rcdb.length; i++) {
//                if (rcdb[i] == null) {
//                    continue;
//                }
//                String out = "  " + SolutionStep.getCellPrint(i) + " (" + sudoku.getCell(i).getAnzCandidates(candType) + "):";
//                for (int j = 1; j <= 9; j++) {
//                    out += " " + rcdb[i].indices[j];
//                }
//                System.out.println(out);
//            }
//        }
	}

	/**
	 * Restricted Common for Death Blossoms<br>
	 * <br>
	 *
	 * One instance belongs to one sudoku cell. It holds the indices of all ALS from
	 * {@link #alses} that can see that cell. The list is maintained for all
	 * candidates (if two candidates from one ALS can see the same cell, the ALS is
	 * recorded twice). A mask containes all candidates for which ALS have been
	 * added. If that mask equals the candidate mask of the cell, the cell is a
	 * possible stem cell for a Death Blossom.
	 */
	class RCForDeathBlossom {

		/** A mask with every candidate set that has at least one ALS. */
		short candMask;
		/** All ALSs for every candidate. */
		int[][] alsPerCandidate = new int[10][100];
		/** Number of ALS {@link #alsPerCandidate} for every candidate. */
		int[] indices = new int[10];

		/**
		 * creates a new instance.
		 */
		RCForDeathBlossom() {
		}

		/**
		 * Adds an ALS for candidate <code>candidate</code>. {@link #candMask} is
		 * updated accordingly.
		 * 
		 * @param als
		 * @param candidate
		 */
		public void addAlsForCandidate(int als, int candidate) {
			if (indices[candidate] < alsPerCandidate[candidate].length) {
				alsPerCandidate[candidate][indices[candidate]++] = als;
				candMask |= Sudoku2.MASKS[candidate];
			}
		}
	}

	public static String getStatistics() {
		return "Statistic for getAlsXZ(): number of calls: " + anzCalls + ", total time: " + (allNanos / 1000)
				+ "us, average: " + (allNanos / anzCalls / 1000) + "us\r\n" + "  getAlses(): total "
				+ (allAlsesNanos / 1000) + "us, average: " + (allAlsesNanos / anzCalls / 1000) + "us\r\n"
				+ "  getRCs(): total " + (allRcsNanos / 1000) + "us, average: " + (allRcsNanos / anzCalls / 1000)
				+ "us\r\n";
	}

	/*
	public static void main(String[] args) {
		// DEBUG = true;
		// Sudoku2 sudoku = new Sudoku2(true);
		Sudoku2 sudoku = new Sudoku2();
		// sudoku.setSudoku(":0361:4:..5.132673268..14917...2835..8..1.262.1.96758.6..283...12....83693184572..723.6..:434
		// 441 442 461 961 464 974:411:r7c39 r6c1b9 fr3c3");
		// sudoku.setSudoku(":0300:4:135792..4.9.8315278725461...8.917.5....3.4.783.72.89.1...673.1....1297..7..485..6:653
		// 472 473 277 483 683 388:481:c28 r68");
		// sudoku.setSudoku(":0000:x:7.2.34.8.........2.8..51.74.......51..63.27..29.......14.76..2.8.........2.51.8.7:::");
		// sudoku.setSudoku(":0000:x:5837..4.2.1.............1....9...63.........47.1.45928..52.38....6..427.12..6.3..:::");
//        sudoku.setSudoku(":0000:x:..65.849..15.4.7.2..9...65.9..867315681.5.279..7.9.864.63...5..1...3.94..9.7..1..:324 326 331 332 339 261 262 364 366 871 891::");        
//        sudoku.setSudoku(":0000:x:.78.6519393..1..7.516739842.9..76.1..6539.28..4..2..69657142938.2.983.5.389657421:::");        
//        sudoku.setSudoku(":0000:x:65.17....382469.5...18..6...36.4...5.27...46.845.1....2.3.845.6...5...825.8.21.34:917 931 738 739 246 147 747 947 355 356 959 266 366 767 967 978 181 981::");        
//        sudoku.setSudoku(":0000:x:8...742.5.248.57...3.621.9...94.2....1...8.2.2....63...5.263.7...214965....587..2:541 847 849 653 469 869 391 491 497::");        
//        sudoku.setSudoku(":0000:x:1.7.5.....8.17..3.3...98...7628394..8.1245.67..471682....58...6.1..67.9....92.5..:927 237 637 438 569 372 277 377 281 389 392 199::");        
//        sudoku.setSudoku(":0000:x:8..7...4...43....667.1248.9.6.2.9...4..871625...6.3.9.3.6.12.871....73.2.2..3...4:112 512 513 913 515 922 525 128 343 543 743 167 972 982 485 585 985 596::");        
		// doubly linked ALS-XZ, 16 eliminations
		// sudoku.setSudoku(":9001:23568:3.2....1.1..97........1....5..18.6....15...7..8.74.1..9....1.27.1.4.......5..79.1::285
		// 286 291 292 385 386 585 612 616 619 626 634 636 685 686 834:");
		// DeathBlossom Beispiel aus Sudopedia
		// sudoku.setSudoku(":0000:x:6....9...38..7....1....8.745...2..1946..9..3.9..8......9....1.57.5.8.6.2.16.4.7.3:517
		// 818 624 626 928 537 665 666 468 273 873::");
		// Death Blossom von
		// http://www.gamesudoku.blogspot.com/2007/02/advanced-death-blossom.html
		// r5c1<>1
		// sudoku.setSudoku(":0000:x:+36+1..+492+7.7.23.8.....7.1...8.........+356.84.........+83...4+27.....3+915.6..92....4.:432
		// 535 835 747 148 751 261 761 463 767 577 178 179 691 397 899::");
		// Five Petal Death Blossom (PIsaacson
		// sudoku.setSudoku(":0000:x:.......124...9...........54.7.2.....6.....4.....1.8...718......9...3.7..532......:723
		// 724 326 526 626 726 331 831 232 133 635 735 835 136 236 143 947 149 262 967
		// 268 276 977 278 484 684 486 586 686 488 688 689 196 997 199::");
		// Very long running time
		// sudoku.setSudoku(":9001:369:.......173.+1.8+7.+5+2+7......+3+8+4+371..+5+86+1..+84+53+7+9+985.+7.+1+2+42+7....84+5+51.7..+2.+3...5..+7.+1:215
		// 216 416 235 236 436:396 615 635 686 686 686 686 696 696 696 696 915 935 986
		// 996:");
		// ALS Chain not found: Almost Locked Set Chain: A=r3c4 {57}, B=r4c4 {57},
		// C=r4c69 {257}, D=r56c8 {567}, RCs=5,7, X=7 => r3c8<>7
//        sudoku.setSudoku(":9003:7:.+6.+4.+9+213+94.+2+3+1+68.2+1+3.6+8+9.+46+89.+1.4+3.+3..8..+1.....3..+8..+43+61..5+9+8.+5.+94+37+26+7+9+2+6+8+5+3+4+1:753 256 756 763 266 766 569:738:");
		// doubly linked ALS not found
//        sudoku.setSudoku(":0901-2:12357:9..17....4.1....7..7...31..14...6.98.9248173.38.5.......48...2.......6.7....6...3:793:172 244 385 571 572 796::");
		// ALS-Chain not found: 127- r1c156789 {1234679} -9- r3c4 {49} -4- r9c4 {49} -9-
		// r2359c3 {12679} -127 => r1c3<>1, r1c3<>2, r1c3<>7
//        sudoku.setSudoku(":0903:127:...+8......3.+57.8.98....315.9.8142......+3+5....5.+36...4.1.42+3....+3..7....6.8...531.:412 612 712 613 916 933 572 986 587 987:113 213 713::");
		// Exception when sorting
		sudoku.setSudoku(
				":0903-1:35:1+53+9+642+7+8+9847+2.3+6.72+6..8+9..638.+4+9..+7+4+91..+78.+6+5+7+2+8+1+6493+8+6+74+9..32+3.9.+7268.+2.56+8+37.9:139:334 355 534::");
		SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
//        AlsSolver as = new AlsSolver(null);
		long millis = System.nanoTime();
		int itAnz = 1;
		List<SolutionStep> steps = null;
		for (int i = 0; i < itAnz; i++) {
			steps = solver.getStepFinder().getAllAlses(sudoku, false, false, true);
//            as.getAlsXZ(true);
//            as.getAlsXYWing();
//            as.getAlsXYChain();
//            as.getAlsDeathBlossom();
//            as.steps = as.getAllAlses(sudoku);
		}
		millis = (System.nanoTime() - millis) / itAnz;
		System.out.println("Find all ALS-XX: " + (millis / 1000000.0) + "ms");
		Collections.sort(steps);
		for (int i = 0; i < steps.size(); i++) {
			System.out.println(steps.get(i));
		}
		System.out.println("Total: " + steps.size());
		System.exit(0);
	}*/
}

/**
 * Compares two ALS solution steps with each other (different from standard
 * compare). Sort order:<br>
 * <ul>
 * <li>number of eliminations</li>
 * <li>equivalency: same candidates to be deleted</li>
 * <li>sum of indices in the step</li>
 * <li>number of ALS (XY lt XY-Wing)</li>
 * <li>number of indices in all ALS</li>
 * </ul>
 * 
 * @author hobiwan
 */
class AlsComparator implements Comparator<SolutionStep> {

	/**
	 * Sort order:<br>
	 * <ul>
	 * <li>number of eliminations (descending)</li>
	 * <li>the eliminations themselves, sorted by cell index and digit and compared
	 * lexicographically (steps with the same eliminations stay together)</li>
	 * <li>number of ALS (XZ lt XY-Wing)</li>
	 * <li>number of indices in all ALS</li>
	 * <li>type</li>
	 * </ul>
	 * Every criterion is a plain key comparison, so the order is transitive as
	 * required by {@link Comparator}. The old version compared non-equivalent steps
	 * by a weighted "index sum" of the eliminations. Different eliminations can
	 * have the same sum (e.g. r7c9&lt;&gt;7 and r8c6&lt;&gt;1), and the sum depended on
	 * the order of the elimination list, so the order was not transitive and
	 * {@link java.util.Collections#sort(List, Comparator)} could throw
	 * "Comparison method violates its general contract!".
	 */
	@Override
	public int compare(SolutionStep o1, SolutionStep o2) {
		// first by number of candidates to delete (descending!)
		int result = o2.getCandidatesToDelete().size() - o1.getCandidatesToDelete().size();
		if (result != 0) {
			return result;
		}
		// then by the candidates themselves
		result = compareCandidates(o1.getCandidatesToDelete(), o2.getCandidatesToDelete());
		if (result != 0) {
			return result;
		}
		// by number of ALS
		result = o1.getAlses().size() - o2.getAlses().size();
		if (result != 0) {
			return result;
		}
		// by number of cells in all ALS
		result = o1.getAlsesIndexCount() - o2.getAlsesIndexCount();
		if (result != 0) {
			return result;
		}
		// finally by type
		return o1.getType().compare(o2.getType());
	}

	/**
	 * Compares two lists of candidates of the same size independent of the order
	 * of the lists.
	 */
	private static int compareCandidates(List<Candidate> l1, List<Candidate> l2) {
		int[] k1 = sortedKeys(l1);
		int[] k2 = sortedKeys(l2);
		int n = Math.min(k1.length, k2.length);
		for (int i = 0; i < n; i++) {
			if (k1[i] != k2[i]) {
				return Integer.compare(k1[i], k2[i]);
			}
		}
		return Integer.compare(k1.length, k2.length);
	}

	private static int[] sortedKeys(List<Candidate> list) {
		int[] keys = new int[list.size()];
		for (int i = 0; i < keys.length; i++) {
			Candidate c = list.get(i);
			keys[i] = c.getIndex() * 10 + c.getValue();
		}
		Arrays.sort(keys);
		return keys;
	}
}
