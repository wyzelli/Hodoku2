# Hodoku2
An updated version of HoDoKu in a new repo

This takes the original code from https://hodoku.sourceforge.net/en/index.php, and merges it with the updates by Pseudofish from https://github.com/PseudoFish/Hodoku.

Then this whole set has been updated to work with JRE 21.

There is an exe and a built jar in the dist folder.

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
3. zips it as `Hodoku-windows.zip`, and
4. **attaches `Hodoku-windows.zip` and `HoDoKu.jar` to the GitHub Release for
   that tag automatically** (creating the release if needed).

It can also be run manually from the Actions tab (`workflow_dispatch`), and runs
on pushes to `main` that touch build-relevant paths so regressions are caught
before tagging. In all cases the zip and jar are uploaded as downloadable
workflow artifacts, so they're available even without a release.

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
