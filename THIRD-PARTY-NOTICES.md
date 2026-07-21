# Third-Party Notices

HoDoKu incorporates the third-party component listed below. HoDoKu itself is
licensed under the GNU General Public License v3 (GPLv3), Copyright 2008-12
Bernhard Hobiger; that license is unaffected by these notices, which apply only
to the bundled third-party component.

---

## SukakuExplainer (with the `serate` rating entry point)

- **Component:** SukakuExplainer + serate (jar manifest `Name: SudokuExplainer+serate`,
  `Specification-Title: "Sudoku Explainer with serate entry point"`).
- **Version:** 2025.10.8 (from the vendored jar manifest: `Implementation-Version`
  and `Specification-Version` = `2025.10.8`).
- **Upstream:** https://github.com/SudokuMonster/SukakuExplainer
- **Authors / attribution:**
  - **Nicolas Juillerat** — original author of *Sudoku Explainer* (SE), from
    which this component derives (jar manifest `Specification-Vendor`).
  - **gsf** — contributed the `serate` command-line rating tool as a
    modification of Sudoku Explainer (this is the entry point HoDoKu invokes;
    jar manifest `Implementation-Vendor: "gsf+1to9only"`).
  - **SudokuMonster** and contributors — maintainers of the SukakuExplainer fork.
- **License:** GNU Lesser General Public License, version 2.1 (LGPL-2.1), per
  the SudokuMonster/SukakuExplainer repository. Full text:
  [`licenses/LGPL-2.1.txt`](licenses/LGPL-2.1.txt).

### How it is used

HoDoKu does **not** link SukakuExplainer at compile time. It invokes SE's own
`serate` command-line entry point (`diuf.sudoku.test.serate`) in a **separate
subprocess** to compute the SE difficulty rating, keeping a clean process and
license boundary. The vendored `SukakuExplainer.jar` is bundled inside
`HoDoKu.jar` as a classpath resource (`/sudoku/serate/SukakuExplainer.jar`) and
extracted to a temporary file on first use.

### Identifying / replacing the bundled library (LGPL-2.1 compliance)

In keeping with LGPL-2.1's requirement that the end user be able to identify and
replace the linked library, HoDoKu lets you substitute your own build of
SukakuExplainer without modifying HoDoKu:

- system property: `-Dhodoku.serate.jar=/path/to/SukakuExplainer.jar`
- environment variable: `SERATE_JAR=/path/to/SukakuExplainer.jar`
- or place `SukakuExplainer.jar` in a `lib/` directory beside `HoDoKu.jar`.

Any of these takes priority over the copy bundled inside `HoDoKu.jar`.
