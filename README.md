# Hodoku2
An updated version of HoDoKu in a new repo

**Website: [hodoku.dev](https://hodoku.dev/)** — play HoDoKu in your browser at [hodoku.dev/play](https://hodoku.dev/play/), read the [User Manual](https://hodoku.dev/docs.html) and the [Solving Guide](https://hodoku.dev/techniques.html).

**Download:** get the latest Windows installer (`.msi`), Windows zip or `HoDoKu.jar` from [Releases](https://github.com/wyzelli/Hodoku2/releases/latest). See the [Code signing policy](#code-signing-policy).

This takes the original code from https://hodoku.sourceforge.net/en/index.php, and merges it with the updates by Pseudofish from https://github.com/PseudoFish/Hodoku.

Then this whole set has been updated to work with JRE 21.

Prebuilt downloads (Windows installer, Windows zip and `HoDoKu.jar`) are attached to each [GitHub Release](https://github.com/wyzelli/Hodoku2/releases), and copies are kept in the `dist` folder.

## Building

The project builds with Gradle (no NetBeans/Ant/launch4j needed):

```
./gradlew jar             # -> dist/HoDoKu.jar
./gradlew jpackageImage    # -> build/jpackage/Hodoku (native app image, run on the target OS - Windows for a .exe)
```

`jpackageImage` bundles a private Java runtime with the app, so end users don't need a separate JRE installed. Run it on Windows to get a native `Hodoku.exe` launcher; the produced image can then be zipped up and dropped into `dist/` to replace the old launch4j-built exe.

## Automated Windows builds

The Windows app image no longer has to be built by hand. The GitHub Actions
workflow [`.github/workflows/build-windows.yml`](.github/workflows/build-windows.yml)
runs on `windows-latest` and, on every **push of a release tag** matching the
`x.y.z` pattern (e.g. `2.4.3`), it:

1. builds `dist/HoDoKu.jar` and the native app image (`build/jpackage/Hodoku/`)
   with JDK 21,
2. copies the extra runtime data files (`hodoku.hcfg`, `reglib-1.3.txt`,
   `exemplars-1.0.txt`, `release-2.2.txt`) next to `Hodoku.exe`,
3. zips it as `Hodoku-windows.zip` and builds a machine-wide `Hodoku-windows.msi`
   installer from the same image (WiX Toolset 3 is installed on the runner if missing), and
4. **attaches `Hodoku-windows.zip`, `Hodoku-windows.msi` and `HoDoKu.jar` to the GitHub Release for
   that tag automatically** (creating the release if needed).

It can also be run manually from the Actions tab (`workflow_dispatch`), and runs
on pushes to `main` that touch build-relevant paths so regressions are caught
before tagging. In all cases the zip and jar are uploaded as downloadable
workflow artifacts, so they're available even without a release.

## Regression tests

`reglib-1.3.txt` holds about 1,100 solver test cases (one technique per line).
The [`regression.yml`](.github/workflows/regression.yml) workflow builds the jar
and runs the whole library on every PR that touches the solver or build. It fails
on any failing case that isn't listed in
[`.github/regression-known-failures.txt`](.github/regression-known-failures.txt),
and warns when a listed case starts passing. To run it locally:

```
./gradlew jar
.github/scripts/run-regression.sh
```

`java -jar dist/HoDoKu.jar /test reglib-1.3.txt` runs the library directly and
exits with code 1 if any case fails (`/testf` skips the slow cases). Pass the
file as a relative path: HoDoKu reads any argument starting with `/` as an option.

### Code signing (pending SignPath Foundation approval)

Code signing is scaffolded but **inert until signing secrets are configured** —
the sign step is skipped automatically while `SIGNPATH_API_TOKEN` is empty, so
unsigned builds still succeed. Once the project is approved by
[SignPath Foundation](https://signpath.org/) for a free open-source certificate,
the maintainer activates real signing by:

1. Adding two repository secrets (Settings → Secrets and variables → Actions):
   - `SIGNPATH_API_TOKEN` — the SignPath CI user API token.
   - `SIGNPATH_ORGANIZATION_ID` — the SignPath organization ID.
2. Filling in the placeholder values in `build-windows.yml`'s "Sign Hodoku.exe
   via SignPath" step — `project-slug` (placeholder `hodoku2`) and
   `signing-policy-slug` (placeholder `release-signing`) — to match the
   SignPath project/policy that SignPath sets up.

The signing artifact configuration lives at
[`.signpath/artifact-configurations/default.xml`](.signpath/artifact-configurations/default.xml)
and signs the `Hodoku.exe` inside the zip via SignPath's
`<zip-file><pe-file-set>` Authenticode pattern.

## Translations

- Spanish (`es`): Emanuel Marquez ([@emproducciones2257](https://github.com/emproducciones2257)).
  The language files live in `src/intl/*_es.properties` and `src/help/keyboard_es.html`;
  pick "Español" under Options → Preferences → General → Language.

## Code signing policy

Free code signing provided by [SignPath.io](https://signpath.io/), certificate by [SignPath Foundation](https://signpath.org/).

Windows releases of Hodoku2 (`Hodoku.exe` inside `Hodoku-windows.zip`) are built
from this repository by GitHub Actions and signed through SignPath. Every signing
request is approved manually by a project approver.

### Team roles

- Committers and reviewers: [wyzelli](https://github.com/wyzelli)
- Approvers: [wyzelli](https://github.com/wyzelli)

All pull requests from contributors outside the team are reviewed by a team member
before merging.

### Privacy policy

This program will not transfer any information to other networked systems unless
specifically requested by the user or the person installing or operating it.

The only network activity is opening links in your web browser when you choose an
item from the Help menu (hodoku.dev and GitHub). The optional SukakuExplainer
difficulty rating runs locally on your computer and sends nothing over the network.

### Installing and uninstalling

Windows releases come in two forms:

- **Installer (`Hodoku-windows.msi`):** installs HoDoKu for all users (administrator
  rights needed) and adds a Start menu entry, with an optional desktop shortcut.
  To uninstall, use Settings → Apps → Installed apps → Hodoku → Uninstall.
- **Zip (`Hodoku-windows.zip`):** no installer. Unzip it anywhere and run `Hodoku.exe`.
  To uninstall, delete the unzipped folder.

Both versions save your settings in `hodoku.hcfg` in your Windows temporary folder
(`%TEMP%\hodoku.hcfg`, usually `C:\Users\<you>\AppData\Local\Temp`). Uninstalling
does not remove it; delete that file too if you want to remove your settings.

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
