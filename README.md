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
