<#
fix-java-env.ps1
Run this script as Administrator. It will:
 - Detect a JDK install (prefer Eclipse Adoptium if present)
 - Backup current Machine PATH to a file in ProgramData
 - Remove Oracle javapath shim from Machine PATH
 - Set MACHINE JAVA_HOME to the detected JDK
 - Prepend the JDK bin to the Machine PATH
 - Run mvn tests (rr.RoundRobinSchedulerTest) to verify

USAGE (from an elevated PowerShell):
  Set-ExecutionPolicy Bypass -Scope Process -Force; .\scripts\fix-java-env.ps1

This modifies Machine environment variables and requires Administrator privileges.
#>

function Assert-Admin {
    $isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
    if (-not $isAdmin) {
        Write-Error "This script must be run as Administrator. Open an elevated PowerShell and re-run."
        exit 1
    }
}

function Find-JdkRoot {
    # Prefer Eclipse Adoptium installs, then fallback to Program Files\Java
    $adoptium = 'C:\Program Files\Eclipse Adoptium'
    if (Test-Path $adoptium) {
        $dirs = Get-ChildItem -Path $adoptium -Directory -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending
        if ($dirs -and $dirs.Count -gt 0) { return $dirs[0].FullName }
    }
    $javaPf = 'C:\Program Files\Java'
    if (Test-Path $javaPf) {
        $dirs = Get-ChildItem -Path $javaPf -Directory -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending
        if ($dirs -and $dirs.Count -gt 0) { return $dirs[0].FullName }
    }
    return $null
}

Assert-Admin

$jdkRoot = Find-JdkRoot
if (-not $jdkRoot) {
    Write-Error "No JDK installations found in common locations. Please install a JDK first (Adoptium recommended)."
    exit 1
}

Write-Output "Detected JDK root: $jdkRoot"

# Backup current Machine PATH
$backupDir = "$env:ProgramData\java-env-backups"
if (-not (Test-Path $backupDir)) { New-Item -Path $backupDir -ItemType Directory | Out-Null }
$timestamp = Get-Date -Format 'yyyyMMdd_HHmmss'
$backupFile = Join-Path $backupDir "machine_path_backup_$timestamp.txt"
[Environment]::GetEnvironmentVariable('Path','Machine') | Out-File -FilePath $backupFile -Encoding utf8
Write-Output "Backed up Machine PATH to: $backupFile"

# Remove Oracle javapath shim from Machine PATH if present
$remove = 'C:\Program Files\Common Files\Oracle\Java\javapath'
$machinePath = [Environment]::GetEnvironmentVariable('Path','Machine')
if ($machinePath -and $machinePath.Contains($remove)) {
    $newPath = ($machinePath -split ';' | Where-Object { $_ -ne $remove }) -join ';'
    [Environment]::SetEnvironmentVariable('Path', $newPath, 'Machine')
    Write-Output "Removed Oracle javapath shim from Machine PATH"
    $machinePath = $newPath
} else {
    Write-Output "Oracle javapath shim not present in Machine PATH"
}

# Ensure JDK bin is prepended to Machine PATH
$jdkBin = Join-Path $jdkRoot 'bin'
if (-not ($machinePath -split ';' | Where-Object { $_ -ieq $jdkBin })) {
    $newMachinePath = "$jdkBin;$machinePath"
    [Environment]::SetEnvironmentVariable('Path', $newMachinePath, 'Machine')
    Write-Output "Prepended $jdkBin to Machine PATH"
} else {
    # If present but not first, move it to front
    $entries = $machinePath -split ';' | Where-Object { $_ -ne '' }
    if ($entries[0] -ne $jdkBin) {
        $entries = $entries | Where-Object { $_ -ne $jdkBin }
        $entries = ,$jdkBin + $entries
        $joined = $entries -join ';'
        [Environment]::SetEnvironmentVariable('Path', $joined, 'Machine')
        Write-Output "Moved $jdkBin to front of Machine PATH"
    } else {
        Write-Output "$jdkBin already at front of Machine PATH"
    }
}

# Set MACHINE JAVA_HOME
[Environment]::SetEnvironmentVariable('JAVA_HOME', $jdkRoot, 'Machine')
Write-Output "Set MACHINE JAVA_HOME -> $jdkRoot"

Write-Output "Environment changes applied. Note: open a new shell to pick up the Machine environment variables."

# Attempt to run mvn test to verify (if Maven is on PATH)
Write-Output "Attempting to run: mvn -Dtest=rr.RoundRobinSchedulerTest test"
try {
    # run mvn if available
    $mvn = (Get-Command mvn -ErrorAction SilentlyContinue)
    if ($mvn) {
        & mvn "-Dtest=rr.RoundRobinSchedulerTest" test
    } else {
        Write-Output "Maven (mvn) not found on PATH in this elevated session. Install Maven or run tests from your regular user shell after reopening." 
    }
} catch {
    Write-Output "Running mvn failed: $_"
}

Write-Output "Script finished. Please close and re-open your terminals (or sign out/in) to pick up the updated Machine environment variables."
