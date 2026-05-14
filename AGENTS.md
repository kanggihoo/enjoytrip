# AGENTS

- Use the repository Maven wrapper, not a system `mvn`.
- Prefer filtered test runners over raw Maven output:
  - macOS/Linux: `./scripts/ai-test.sh`
  - Windows PowerShell: `powershell -ExecutionPolicy Bypass -File .\scripts\ai-test.ps1`
- No selector means run all tests.
- A selector means Maven Surefire syntax, for example:
  - `com.enjoytrip.common.controller.HomeControllerTest`
  - `com.enjoytrip.common.controller.HomeControllerTest#indexReturnsIndexView`
  - `'ClassA,ClassB#methodName'`
- Treat `OK` as success.
- On failure, use the reduced output only: `selector`, `test`, `message`, `location`, `full-log`.
- Output format:
  - success: `OK`
  - failure: `FAIL` followed by `selector`, one or more `test` lines, `message`, `location`, and `full-log`
- The raw log is written to `.tmp/ai-test.log` and overwritten on each run.
