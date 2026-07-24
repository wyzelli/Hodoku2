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

`index.html` loads the jar from the CheerpJ virtual path `/app/HoDoKu-web.jar`.

CheerpJ mounts the **web server root** (the site origin) at `/app/`. So the jar
must be reachable such that its path under `/app/` matches what `index.html`
requests:

- Deploy the **contents of this `web/play` directory as the site root**, with
  `HoDoKu-web.jar` copied in alongside `index.html`. Then
  `index.html` -> `/` and `HoDoKu-web.jar` -> `/app/HoDoKu-web.jar`, as coded.
- If instead you serve this page under a sub-path (e.g. `https://example.com/play/`),
  the jar sits at `/app/play/HoDoKu-web.jar`; update the `cheerpjRunJar(...)`
  argument in `index.html` accordingly (or place the jar at the site root).

In all cases the jar must be deployed **alongside** `index.html`.

Example (deploying to the site root):

```sh
./gradlew webJar
cp build/web/HoDoKu-web.jar web/play/HoDoKu-web.jar   # sit next to index.html
# then publish the contents of web/play/ as the static site
```

The jar (~2.2 MB) is downloaded on first run, so initial load takes a moment.

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

- The in-app "rate puzzle" feature shells out to a separate `serate` JVM process
  (SukakuExplainer) and will simply be unavailable in the browser; the rest of
  the application degrades gracefully.
- Actual in-browser rendering must be verified manually after deployment; it
  cannot be exercised in a headless CI environment.
