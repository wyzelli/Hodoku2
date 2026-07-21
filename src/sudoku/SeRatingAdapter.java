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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter around the SukakuExplainer (SE) {@code serate} command line tool,
 * used to compute the SE difficulty rating of a puzzle.
 * <p>
 * SE is invoked as a separate JVM process rather than in-process: {@code serate}
 * is SE's own stable, documented entry point that configures all solving
 * techniques exactly as its authors intend, so this avoids brittle coupling to
 * SE internals and keeps a clean GPL process boundary (HoDoKu itself gains no
 * compile-time dependency on SE).
 * <p>
 * The SukakuExplainer jar is looked up in this order:
 * <ol>
 * <li>system property {@code hodoku.serate.jar}</li>
 * <li>environment variable {@code SERATE_JAR}</li>
 * <li>{@code lib/SukakuExplainer.jar} relative to the working directory and to
 * the location HoDoKu itself is running from</li>
 * <li>the copy of {@code SukakuExplainer.jar} bundled inside HoDoKu.jar as a
 * classpath resource, extracted once to a temp file and reused for the rest of
 * the JVM run. This is what makes the standalone {@code HoDoKu.jar} download
 * self-sufficient: SE rating works even when the jar is run alone from an
 * arbitrary directory with no {@code lib/} beside it</li>
 * </ol>
 * The external locations take priority so source builds and explicit overrides
 * still win over the bundled copy.
 * When the jar cannot be found, or the puzzle is invalid/unsolvable, or the
 * process exceeds {@link #timeoutSeconds}, {@code null} is returned, which the
 * caller maps to the "SE n/a" status.
 * <p>
 * This class does blocking I/O and must be called from a background thread (see
 * {@link SeRatingManager}). {@link #rate(String)} responds to thread
 * interruption by destroying the child process, so a superseded/cancelled
 * request is torn down promptly.
 */
public class SeRatingAdapter {

	private static final Logger LOG = Logger.getLogger(SeRatingAdapter.class.getName());

	/** System property overriding the SukakuExplainer jar location. */
	public static final String JAR_PROPERTY = "hodoku.serate.jar";
	/** Environment variable overriding the SukakuExplainer jar location. */
	public static final String JAR_ENV = "SERATE_JAR";

	private static final String SERATE_MAIN = "diuf.sudoku.test.serate";
	private static final String JAR_NAME = "SukakuExplainer.jar";
	/** Classpath location of the SukakuExplainer jar bundled inside HoDoKu.jar. */
	private static final String EMBEDDED_JAR_RESOURCE = "/sudoku/serate/" + JAR_NAME;
	/** Extracted copy of the embedded jar, cached for the lifetime of the JVM. */
	private static volatile File extractedJar;
	/** serate emits ratings as a floating point number, e.g. "7.2". */
	private static final Pattern RATING = Pattern.compile("(\\d+(?:\\.\\d+)?)");

	private final long timeoutSeconds;

	public SeRatingAdapter() {
		this(30);
	}

	public SeRatingAdapter(long timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	/**
	 * Computes the SE difficulty rating for a puzzle.
	 *
	 * @param puzzle an 81-character puzzle string (digits for givens, '.' or '0'
	 *               for empty cells), as produced by
	 *               {@link Sudoku2#getSudoku(ClipboardMode)} with
	 *               {@link ClipboardMode#CLUES_ONLY}
	 * @return the SE rating (typically 1.0 - 11.x), or {@code null} when the
	 *         puzzle cannot be rated (invalid input, missing SE jar, timeout or
	 *         any error)
	 */
	public Double rate(String puzzle) {
		String normalized = normalize(puzzle);
		if (normalized == null) {
			return null;
		}

		File jar = locateJar();
		if (jar == null) {
			LOG.log(Level.WARNING, "SukakuExplainer jar not found; SE rating unavailable. "
					+ "Set -D{0}=/path/to/{1} or place it in lib/.", new Object[] { JAR_PROPERTY, JAR_NAME });
			return null;
		}

		Process process = null;
		try {
			String java = new File(new File(System.getProperty("java.home"), "bin"),
					isWindows() ? "java.exe" : "java").getAbsolutePath();
			ProcessBuilder pb = new ProcessBuilder(java, "-cp", jar.getAbsolutePath(), SERATE_MAIN,
					"--format=%r", normalized);
			pb.redirectErrorStream(false);
			// serate writes a "SukakuExplainer.json" settings file into its
			// working directory; run it in the temp dir so HoDoKu's own
			// working directory is never polluted.
			pb.directory(new File(System.getProperty("java.io.tmpdir", ".")));
			process = pb.start();
			// serate reads no stdin in this mode; close it so it never blocks.
			process.getOutputStream().close();

			Double rating = readRating(process);

			if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
				// Invalid / non-unique puzzles can make serate run effectively
				// forever - treat as "n/a".
				process.destroyForcibly();
				return null;
			}
			if (process.exitValue() != 0) {
				return null;
			}
			return rating;
		} catch (InterruptedException ex) {
			// Request was superseded/cancelled: tear down and restore the flag.
			Thread.currentThread().interrupt();
			return null;
		} catch (Exception ex) {
			LOG.log(Level.WARNING, "SE rating computation failed", ex);
			return null;
		} finally {
			if (process != null && process.isAlive()) {
				process.destroyForcibly();
			}
		}
	}

	private Double readRating(Process process) {
		Double rating = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (Thread.currentThread().isInterrupted()) {
					break;
				}
				Matcher m = RATING.matcher(line);
				if (m.find()) {
					try {
						rating = Double.valueOf(m.group(1));
					} catch (NumberFormatException ignore) {
						// keep scanning
					}
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.FINE, "Error reading serate output", ex);
		}
		return rating;
	}

	/**
	 * Validates and normalizes the puzzle. Returns an 81-character string, or
	 * {@code null} if the input clearly cannot be rated.
	 */
	private String normalize(String puzzle) {
		if (puzzle == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(Sudoku2.LENGTH);
		int clues = 0;
		for (int i = 0; i < puzzle.length(); i++) {
			char c = puzzle.charAt(i);
			if (c >= '1' && c <= '9') {
				sb.append(c);
				clues++;
			} else if (c == '0' || c == '.') {
				sb.append('.');
			}
			// ignore any other character (whitespace, formatting)
		}
		// A classic Sudoku with a unique solution needs exactly 81 cells and at
		// least 17 givens; anything else is not worth handing to serate.
		if (sb.length() != Sudoku2.LENGTH || clues < 17) {
			return null;
		}
		return sb.toString();
	}

	private File locateJar() {
		String prop = System.getProperty(JAR_PROPERTY);
		if (prop != null && !prop.isEmpty()) {
			File f = new File(prop);
			if (f.isFile()) {
				return f;
			}
		}
		String env = System.getenv(JAR_ENV);
		if (env != null && !env.isEmpty()) {
			File f = new File(env);
			if (f.isFile()) {
				return f;
			}
		}
		for (File dir : candidateDirs()) {
			File f = new File(dir, "lib" + File.separator + JAR_NAME);
			if (f.isFile()) {
				return f;
			}
			f = new File(dir, JAR_NAME);
			if (f.isFile()) {
				return f;
			}
		}
		return extractEmbeddedJar();
	}

	/**
	 * Final fallback: use the {@code SukakuExplainer.jar} bundled inside HoDoKu.jar.
	 * It is extracted to a temp file on first use and the same file is reused for
	 * subsequent rating requests in this JVM. Returns {@code null} only when the
	 * resource is genuinely absent (e.g. a stripped build), letting the caller fall
	 * through to the "jar not found" path.
	 */
	private File extractEmbeddedJar() {
		File cached = extractedJar;
		if (cached != null && cached.isFile()) {
			return cached;
		}
		synchronized (SeRatingAdapter.class) {
			if (extractedJar != null && extractedJar.isFile()) {
				return extractedJar;
			}
			try (InputStream in = SeRatingAdapter.class.getResourceAsStream(EMBEDDED_JAR_RESOURCE)) {
				if (in == null) {
					return null;
				}
				File tmp = File.createTempFile("hodoku-serate-", ".jar");
				tmp.deleteOnExit();
				Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
				extractedJar = tmp;
				return tmp;
			} catch (Exception ex) {
				LOG.log(Level.WARNING, "Failed to extract bundled SukakuExplainer jar", ex);
				return null;
			}
		}
	}

	private java.util.List<File> candidateDirs() {
		java.util.List<File> dirs = new java.util.ArrayList<>();
		dirs.add(new File(System.getProperty("user.dir", ".")));
		try {
			CodeSource cs = SeRatingAdapter.class.getProtectionDomain().getCodeSource();
			if (cs != null && cs.getLocation() != null) {
				File self = new File(cs.getLocation().toURI());
				File base = self.isDirectory() ? self : self.getParentFile();
				if (base != null) {
					dirs.add(base);
					if (base.getParentFile() != null) {
						dirs.add(base.getParentFile());
					}
				}
			}
		} catch (Exception ignore) {
			// best effort only
		}
		return dirs;
	}

	private static boolean isWindows() {
		return System.getProperty("os.name", "").toLowerCase().contains("win");
	}
}
