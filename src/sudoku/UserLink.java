/*
 * Copyright (C) 2022 Charmingpea
 * Copyright (C) 2019-20  PseudoFish
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

/**
 * A single user-drawn link between two candidates ("user created arrows").
 *
 * <p>A link connects the {@code sourceCand} of {@code sourceIndex} to the
 * {@code targetCand} of {@code targetIndex}. Cell indices follow the usual
 * {@link Sudoku2} convention; candidate digits are 1-9 (same 1-based convention
 * used throughout the codebase, e.g. by {@code SudokuPanel.getCandKoord}).</p>
 *
 * <p>This is a plain JavaBean (public no-arg constructor plus getters/setters)
 * so that it can be serialized later. It is currently session-only and is not
 * yet wired into {@code GuiState}/{@code .hsol} persistence, but the shape is
 * intentionally kept serialization-friendly so persistence can be added without
 * reworking this class.</p>
 */
public class UserLink {

	/** The inference strength a user link represents. */
	public enum LinkType {
		STRONG,
		WEAK
	}

	private int sourceIndex;
	private int sourceCand;
	private int targetIndex;
	private int targetCand;
	private LinkType linkType = LinkType.STRONG;

	public UserLink() {
	}

	public UserLink(int sourceIndex, int sourceCand, int targetIndex, int targetCand, LinkType linkType) {
		this.sourceIndex = sourceIndex;
		this.sourceCand = sourceCand;
		this.targetIndex = targetIndex;
		this.targetCand = targetCand;
		this.linkType = linkType;
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public int getSourceCand() {
		return sourceCand;
	}

	public void setSourceCand(int sourceCand) {
		this.sourceCand = sourceCand;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}

	public int getTargetCand() {
		return targetCand;
	}

	public void setTargetCand(int targetCand) {
		this.targetCand = targetCand;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	/**
	 * @return {@code true} if the given (cell index, candidate) pair is one of
	 *         this link's two endpoints.
	 */
	public boolean touches(int index, int cand) {
		return (sourceIndex == index && sourceCand == cand)
			|| (targetIndex == index && targetCand == cand);
	}

	/**
	 * @return {@code true} if this link joins the same two endpoints as the
	 *         given pair, regardless of direction.
	 */
	public boolean connects(int i1, int c1, int i2, int c2) {
		return (sourceIndex == i1 && sourceCand == c1 && targetIndex == i2 && targetCand == c2)
			|| (sourceIndex == i2 && sourceCand == c2 && targetIndex == i1 && targetCand == c1);
	}
}
