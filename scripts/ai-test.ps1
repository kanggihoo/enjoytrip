param(
    [Parameter(Mandatory = $false, Position = 0)]
    [string]$Selector
)

$ErrorActionPreference = "Stop"

$rootDir = Split-Path -Parent $PSScriptRoot
$logDir = Join-Path $rootDir ".tmp"
$logFile = Join-Path $logDir "ai-test.log"
$mavenWrapper = Join-Path $rootDir "mvnw.cmd"

function Resolve-JavaHome {
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
        return $env:JAVA_HOME
    }

    $candidates = @(
        "C:\Program Files\Microsoft\jdk-21*",
        "C:\Program Files\Eclipse Adoptium\jdk-21*",
        "C:\Program Files\Java\jdk-21*",
        "C:\Program Files\Microsoft\jdk-17*",
        "C:\Program Files\Eclipse Adoptium\jdk-17*",
        "C:\Program Files\Java\jdk-17*"
    )

    foreach ($pattern in $candidates) {
        $match = Get-ChildItem -Path $pattern -Directory -ErrorAction SilentlyContinue |
            Sort-Object Name -Descending |
            Select-Object -First 1
        if ($match -and (Test-Path (Join-Path $match.FullName "bin\java.exe"))) {
            return $match.FullName
        }
    }

    return $null
}

if (-not (Test-Path $mavenWrapper)) {
    Write-Error "mvnw.cmd not found: $mavenWrapper"
}

$resolvedJavaHome = Resolve-JavaHome
if ($resolvedJavaHome) {
    $env:JAVA_HOME = $resolvedJavaHome
    $env:PATH = "$($env:JAVA_HOME)\bin;$($env:PATH)"
}
elseif (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "java runtime not found. Set JAVA_HOME or install JDK 17/21."
}

New-Item -ItemType Directory -Force -Path $logDir | Out-Null

if ([string]::IsNullOrWhiteSpace($Selector)) {
    $mavenOutput = & $mavenWrapper "-q" "test" 2>&1
}
else {
    $mavenOutput = & $mavenWrapper "-q" "-Dtest=$Selector" "test" 2>&1
}
$status = $LASTEXITCODE
$mavenOutput | Set-Content -Path $logFile

if ($status -eq 0) {
    Write-Output "OK"
    exit 0
}

Write-Output "FAIL"
if ([string]::IsNullOrWhiteSpace($Selector)) {
    Write-Output "selector: <all-tests>"
}
else {
    Write-Output "selector: $Selector"
}

$tests = New-Object System.Collections.Generic.List[string]
$seenTests = @{}
$message = $null
$summaryLine = $null
$location = $null
$fallbackLocation = $null

foreach ($line in $mavenOutput) {
    $text = [string]$line

    if ($text -match '^\[ERROR\] .* -- Time elapsed: .* <<< (FAILURE|ERROR)!$' -and $text -notmatch 'Tests run:') {
        $clean = $text -replace '^\[ERROR\] ', ''
        $testName = ($clean -split ' -- Time elapsed:')[0]
        if (-not $seenTests.ContainsKey($testName)) {
            $tests.Add($testName)
            $seenTests[$testName] = $true
        }
        continue
    }

    if (-not $message -and $text -match 'AssertionFailedError|Caused by:') {
        $message = $text -replace '^\[ERROR\] ', ''
        continue
    }

    if (-not $summaryLine -and $text -match '^\[ERROR\]\s{2,}') {
        $summaryLine = $text -replace '^\[ERROR\]\s+', ''
        continue
    }

    if ($text -match '^\s+at ') {
        $clean = $text.TrimStart()
        if (-not $location -and $clean -match 'com\.enjoytrip\.') {
            $location = $clean
        }
        elseif (-not $fallbackLocation) {
            $fallbackLocation = $clean
        }
    }
}

if ($tests.Count -eq 0) {
    Write-Output "test: n/a"
}
else {
    foreach ($test in $tests) {
        Write-Output "test: $test"
    }
}

if (-not $message -and $summaryLine) {
    $message = $summaryLine
}
if (-not $message) {
    $message = "n/a"
}
Write-Output "message: $message"

if (-not $location -and $fallbackLocation) {
    $location = $fallbackLocation
}
if (-not $location) {
    $location = "n/a"
}
Write-Output "location: $location"
Write-Output "full-log: $logFile"
exit $status
