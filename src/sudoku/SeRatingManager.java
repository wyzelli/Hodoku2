/*
 * Copyright (C) 2008-24  Bernhard Hobiger
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */
package sudoku;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * Manages asynchronous computation of the SukakuExplainer (SE) difficulty
 * rating that is shown in the status bar next to HoDoKu's own difficulty text.
 * <p>
 * The rating is calculated on a background thread ({@link SwingWorker}) so the
 * event dispatch thread is never blocked. Every request is tagged with a
 * monotonically increasing generation id. When a new puzzle is loaded before an
 * older calculation has finished, the older worker is cancelled and its result
 * is discarded (stale-request protection): a superseded worker must never touch
 * the UI, so a slow calculation can never overwrite a newer rating.
 * <p>
 * All public methods must be called on the event dispatch thread.
 */
public class SeRatingManager {

	/** Label text when no puzzle is loaded / initial state. */
	public static final String STATE_IDLE = "SE --";
	/** Label text while a background calculation is running. */
	public static final String STATE_CALCULATING = "SE calculating…";
	/** Label text when the puzzle could not be rated (invalid/unsolvable). */
	public static final String STATE_NA = "SE n/a";

	private final JLabel label;
	private final AtomicLong generation = new AtomicLong(0);
	private final SeRatingAdapter adapter = new SeRatingAdapter();
	private SwingWorker<Double, Void> currentWorker;

	public SeRatingManager(JLabel label) {
		this.label = label;
		reset();
	}

	/**
	 * Resets the label to the idle state and invalidates any running worker.
	 */
	public final void reset() {
		generation.incrementAndGet();
		cancelCurrent();
		label.setText(STATE_IDLE);
	}

	/**
	 * Sets the label directly to the "SE n/a" state and invalidates any running
	 * worker. Used when the caller already knows the puzzle cannot be rated (e.g.
	 * HoDoKu has determined it is invalid/unsolvable), so no background
	 * calculation is started.
	 */
	public void setUnavailable() {
		generation.incrementAndGet();
		cancelCurrent();
		label.setText(STATE_NA);
	}

	/**
	 * Requests a new SE rating for the given puzzle. Any previously running
	 * calculation is cancelled and superseded. The label immediately switches to
	 * the "calculating" state; the final rating is applied when the background
	 * worker finishes (unless it has been superseded in the meantime).
	 *
	 * @param givens the puzzle as a clue string (typically 81 characters, digits
	 *               for givens and '.'/'0' for empty cells); may be {@code null}
	 */
	public void requestRating(final String givens) {
		final long myGeneration = generation.incrementAndGet();
		cancelCurrent();
		label.setText(STATE_CALCULATING);

		SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
			@Override
			protected Double doInBackground() throws Exception {
				return computeRating(givens);
			}

			@Override
			protected void done() {
				// Stale-request protection: ignore results from cancelled or
				// superseded workers so they never overwrite a newer rating.
				if (isCancelled() || myGeneration != generation.get()) {
					return;
				}
				Double rating;
				try {
					rating = get();
				} catch (Exception ex) {
					rating = null;
				}
				if (rating == null || rating < 0) {
					label.setText(STATE_NA);
				} else {
					label.setText(String.format(Locale.US, "SE %.1f", rating));
				}
			}
		};
		currentWorker = worker;
		worker.execute();
	}

	private void cancelCurrent() {
		if (currentWorker != null && !currentWorker.isDone()) {
			currentWorker.cancel(true);
		}
		currentWorker = null;
	}

	/**
	 * Computes the SE difficulty rating for the given puzzle by delegating to the
	 * real SukakuExplainer {@link SeRatingAdapter}. Returns {@code null} when the
	 * puzzle cannot be rated (invalid/unsolvable, SE unavailable or timed out),
	 * which the worker maps to the "SE n/a" state.
	 *
	 * @param givens the puzzle clue string
	 * @return the rating, or {@code null} when the puzzle cannot be rated
	 */
	private Double computeRating(String givens) {
		return adapter.rate(givens);
	}
}
