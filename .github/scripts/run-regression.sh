#!/usr/bin/env bash
# Runs the HoDoKu regression library and compares the failures with the
# known-failures list. Exit 0 = no new failures.
#
# Usage: .github/scripts/run-regression.sh [path/to/HoDoKu.jar]
# Run from the repo root. Works on Linux, macOS and Git Bash on Windows.
set -uo pipefail

JAR="${1:-dist/HoDoKu.jar}"
LIB="reglib-1.3.txt"   # must be relative: HoDoKu treats args starting with '/' as options
KNOWN=".github/regression-known-failures.txt"
OUT="${RUNNER_TEMP:-${TMPDIR:-/tmp}}/hodoku-regression"
mkdir -p "$OUT/tmp"

# Fresh java.io.tmpdir so a stale hodoku.hcfg can't change solver options.
java -Djava.awt.headless=true -Djava.io.tmpdir="$OUT/tmp" -jar "$JAR" /test "$LIB" > "$OUT/regression.log" 2>&1
rc=$?

if ! grep -q '^Test finished!' "$OUT/regression.log"; then
  echo "::error::Regression run did not finish (exit code $rc)"
  cat "$OUT/regression.log"
  exit 1
fi
sed -n '/^Test finished!/,/tests could not be run/p' "$OUT/regression.log"

grep '^  Should be:' "$OUT/regression.log" | sed 's/^  Should be://' | tr -d '\r' | sort -u > "$OUT/actual.txt"
grep -v '^#' "$KNOWN" | tr -d '\r' | sed '/^$/d' | sort -u > "$OUT/known.txt"

new=$(comm -23 "$OUT/actual.txt" "$OUT/known.txt")
fixed=$(comm -13 "$OUT/actual.txt" "$OUT/known.txt")

if [ -n "$fixed" ]; then
  echo
  echo "Known failures that now pass (remove them from $KNOWN):"
  while IFS= read -r line; do
    echo "::warning::Known failure now passes: ${line:0:60}..."
    echo "  $line"
  done <<< "$fixed"
fi

if [ -n "$new" ]; then
  echo
  echo "New failures:"
  sed -n '/^Failed Cases:/,$p' "$OUT/regression.log" | while IFS= read -r l; do echo "$l"; done |
    grep -F -A1 -f <(sed 's/^/  Should be:/' <<< "$new") | grep -v '^--$'
  echo "::error::$(wc -l <<< "$new" | tr -d ' ') new regression failure(s); see log above"
  exit 1
fi

echo
echo "No new regression failures."
