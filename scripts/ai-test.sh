#!/bin/sh

set -u

ROOT_DIR=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
SELECTOR=${1:-}
LOG_DIR="$ROOT_DIR/.tmp"
LOG_FILE="$LOG_DIR/ai-test.log"

java_major_version() {
  java -version 2>&1 | awk -F '"' '/version/ {
    split($2, parts, ".")
    print (parts[1] == "1") ? parts[2] : parts[1]
    exit
  }'
}

resolve_java_home() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    return 0
  fi

  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null) && [ -n "$JAVA_HOME" ] && return 0
    JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null) && [ -n "$JAVA_HOME" ] && return 0
  fi

  JAVA_HOME=""
  return 1
}

if resolve_java_home; then
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"
elif ! command -v java >/dev/null 2>&1; then
  echo "java runtime not found. Set JAVA_HOME or install JDK 17/21." >&2
  exit 2
fi

JAVA_MAJOR=$(java_major_version)
case "$JAVA_MAJOR" in
  17|21) ;;
  *)
    echo "unsupported java version: ${JAVA_MAJOR:-unknown}. Use JDK 17 or 21." >&2
    exit 2
    ;;
esac

mkdir -p "$LOG_DIR"
cd "$ROOT_DIR" || exit 2

if [ -n "$SELECTOR" ]; then
  sh "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" -Dtest="$SELECTOR" test >"$LOG_FILE" 2>&1
else
  sh "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/pom.xml" test >"$LOG_FILE" 2>&1
fi
STATUS=$?

if [ "$STATUS" -eq 0 ]; then
  echo "OK"
  exit 0
fi

echo "FAIL"
awk -v selector="${SELECTOR:-<all-tests>}" '
  function clean_error(value) {
    sub(/^\[ERROR\][[:space:]]*/, "", value)
    return value
  }
  function is_noise(value) {
    return value == "" ||
      value ~ /^To see the full stack trace/ ||
      value ~ /^Re-run Maven using/ ||
      value ~ /^For more information/ ||
      value ~ /^\[Help [0-9]+\]/
  }
  BEGIN {
    print "selector: " selector
  }
  /^\[ERROR\] .* -- Time elapsed: .* <<< (FAILURE|ERROR)!$/ {
    if (index($0, "Tests run:") == 0) {
      line = $0
      sub(/^\[ERROR\] /, "", line)
      split(line, parts, " -- Time elapsed:")
      if (!(parts[1] in seen_tests)) {
        tests[++test_count] = parts[1]
        seen_tests[parts[1]] = 1
      }
    }
    next
  }
  /AssertionFailedError|Caused by:/ && message == "" {
    message = $0
    sub(/^\[ERROR\] /, "", message)
    next
  }
  /^\[ERROR\]   / && summary == "" {
    summary = $0
    sub(/^\[ERROR\]   /, "", summary)
    next
  }
  /^\[ERROR\] .*src\/.*\.java:\[[0-9]+,[0-9]+\]/ && location == "" {
    location = clean_error($0)
    next
  }
  /^\[ERROR\]/ && maven_message == "" {
    candidate = clean_error($0)
    if (!is_noise(candidate)) {
      maven_message = candidate
    }
    next
  }
  /^[[:space:]]+at / {
    if (location == "") {
      if ($0 ~ /com\.enjoytrip\./) {
        location = $0
        sub(/^[[:space:]]+/, "", location)
      } else if (fallback_location == "") {
        fallback_location = $0
        sub(/^[[:space:]]+/, "", fallback_location)
      }
    }
  }
  END {
    if (test_count == 0) {
      print "test: n/a"
    } else {
      for (i = 1; i <= test_count; i++) {
        print "test: " tests[i]
      }
    }

    if (message == "" && summary != "") {
      message = summary
    }
    if (message == "" && maven_message != "") {
      message = maven_message
    }
    if (message == "") {
      message = "n/a"
    }
    print "message: " message

    if (location == "" && fallback_location != "") {
      location = fallback_location
    }
    if (location == "") {
      location = "n/a"
    }
    print "location: " location
    print "full-log: " FILENAME
  }
' "$LOG_FILE"
exit "$STATUS"
