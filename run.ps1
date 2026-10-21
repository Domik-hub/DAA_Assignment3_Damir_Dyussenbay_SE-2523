$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
mvn package
if ($LASTEXITCODE -ne 0) { throw 'Build failed' }
java -Xmx2g -jar target/assignment3-graphs-1.0.jar
if ($LASTEXITCODE -ne 0) { throw 'Benchmark failed' }
python plots.py
if ($LASTEXITCODE -ne 0) { throw 'Plots failed' }
