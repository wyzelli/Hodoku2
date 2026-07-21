# Hodoku2
An updated version of HoDoKu in a new repo

This takes the original code from https://hodoku.sourceforge.net/en/index.php, and merges it with the updates by Pseudofish from https://github.com/PseudoFish/Hodoku.

Then this whole set has been updated to work with JRE 18.

There is an exe and a built jar in the dist folder.

## Building

The project builds with Gradle (no NetBeans/Ant/launch4j needed):

```
./gradlew jar             # -> dist/HoDoKu.jar
./gradlew jpackageImage    # -> build/jpackage/Hodoku (native app image, run on the target OS - Windows for a .exe)
```

`jpackageImage` bundles a private Java runtime with the app, so end users don't need a separate JRE installed. Run it on Windows to get a native `Hodoku.exe` launcher; the produced image can then be zipped up and dropped into `dist/` to replace the old launch4j-built exe.

## Third-party components

HoDoKu itself is licensed under the GNU General Public License v3 (GPLv3),
Copyright 2008-12 Bernhard Hobiger (see the `LICENSE` file and the headers in
the source files).

The optional SukakuExplainer (SE) difficulty rating shown in the status bar is
computed by **SukakuExplainer**, a separate third-party component:

- Upstream: https://github.com/SudokuMonster/SukakuExplainer (maintained by
  SudokuMonster and contributors).
- It is a modification of **Sudoku Explainer**, originally authored by Nicolas
  Juillerat. The `serate` command-line rating tool that HoDoKu actually invokes
  was contributed by **gsf** as a modification of Sudoku Explainer.
- License: **GNU Lesser General Public License v2.1 (LGPL-2.1)**, per the
  SudokuMonster/SukakuExplainer repository.

HoDoKu does not link SukakuExplainer at compile time: it invokes SE's own
`serate` command-line entry point in a **separate subprocess**, keeping a clean
process/license boundary. For convenience the vendored `SukakuExplainer.jar` is
bundled inside `HoDoKu.jar` (extracted to a temp file at runtime). In keeping
with LGPL-2.1's requirement that the end user be able to identify and replace
the linked library, you can point HoDoKu at your own build of SukakuExplainer
via the `hodoku.serate.jar` system property (`-Dhodoku.serate.jar=/path/to/SukakuExplainer.jar`)
or the `SERATE_JAR` environment variable; either overrides the bundled copy.

See `THIRD-PARTY-NOTICES.md` and `licenses/LGPL-2.1.txt` for full details and
the complete license text.
