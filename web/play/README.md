# HoDoKu in the browser (CheerpJ)

This directory contains a small static web page that runs the standard HoDoKu
Swing application in the browser using [CheerpJ](https://cheerpj.com/), a
WebAssembly JVM. No server-side Java is involved: the browser downloads the
CheerpJ runtime and the HoDoKu jar and runs everything client-side.

## 1. Build the web jar

The desktop build targets Java 21. CheerpJ runs bytecode up to Java 17, so a
dedicated Gradle task compiles the same sources with `options.release = 17` and
packages a self-contained jar (same `Main-Class` and bundled resources as the
desktop `dist/HoDoKu.jar`):

```sh
./gradlew webJar
```

Output: `build/web/HoDoKu-web.jar` (Java 17 bytecode, major version 61).

This task is completely independent of the desktop `jar`/`jpackage` tasks, which
keep their Java 21 targeting.

## 2. Deploy

`index.html` is the same file that is served at https://hodoku.dev/play/. It
expects to live under a `/play/` sub-path and loads the jar from the CheerpJ
virtual path `/app/play/HoDoKu-web.jar`.

CheerpJ mounts the **web server root** (the site origin) at `/app/`, so the jar
must be deployed **alongside** `index.html`:

- **Under `/play/` (as coded):** publish `index.html` and `HoDoKu-web.jar` as
  `https://example.com/play/`. The jar is then at `/app/play/HoDoKu-web.jar`.
- **At the site root:** change the `cheerpjRunJar(...)` argument to
  `"/app/HoDoKu-web.jar"`, and remove or repoint the "← hodoku.dev" back link.

Example (deploying under `/play/`):

```sh
./gradlew webJar
mkdir -p site/play
cp web/play/index.html build/web/HoDoKu-web.jar site/play/
# then publish site/ as the static site, served by an HTTP server that supports
# Range requests (CheerpJ needs them to read the jar)
```

The jar (about 1.5 MB) is downloaded on first run, so the first load takes a moment.

### Clipboard

`cheerpjInit` sets `clipboardMode: "system"`, so puzzles copied elsewhere can be
pasted with Ctrl+V (Cmd+V on Mac) after clicking the HoDoKu window. Copying out
needs Ctrl+C pressed twice, and Edit menu Copy/Paste waits for the shortcut. The
page shows a short hint saying this. CheerpJ's default (`"java"`) keeps an
internal clipboard that can't see anything copied outside the app. See the
[CheerpJ docs](https://cheerpj.com/docs/reference/cheerpjInit).

## 3. Optional: cross-origin isolation for multithreaded WASM

CheerpJ can use multithreading (and `SharedArrayBuffer`) for better performance
when the page is [cross-origin isolated](https://web.dev/coop-coep/). This
requires two response headers. On Cloudflare Pages you can scope them to the
play area with a `_headers` file at the site root (do **not** commit one here -
this is just a reference snippet):

```
/play/*
  Cross-Origin-Opener-Policy: same-origin
  Cross-Origin-Embedder-Policy: require-corp
```

This is optional: HoDoKu runs single-threaded under CheerpJ without these
headers, just without the multithreading speed-up.

## Notes

- The SE difficulty rating runs SukakuExplainer as a separate `serate` JVM
  process, which CheerpJ cannot start. `webJar` therefore leaves the bundled
  `SukakuExplainer.jar` out, and HoDoKu hides the SE label in the status bar when
  no rating jar is available or the process cannot be started. The rest of the
  application works normally.
- Actual in-browser rendering must be verified manually after deployment; it
  cannot be exercised in a headless CI environment.
