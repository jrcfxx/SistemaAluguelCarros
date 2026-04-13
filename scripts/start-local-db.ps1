Param(
  [string]$SaPassword = $env:MSSQL_SA_PASSWORD,
  [string]$DbName = $env:DB_NAME
)

$ErrorActionPreference = "Stop"

function Test-DockerAvailable {
  try {
    docker version | Out-Null
    return $true
  } catch {
    return $false
  }
}

if ([string]::IsNullOrWhiteSpace($SaPassword)) {
  $SaPassword = "YourStrong!Passw0rd"
  Write-Host "MSSQL_SA_PASSWORD não informado. Usando padrão: $SaPassword"
}

if ([string]::IsNullOrWhiteSpace($DbName)) {
  $DbName = "SistemaAluguelCarros"
}

$env:MSSQL_SA_PASSWORD = $SaPassword
$env:DB_NAME = $DbName

if (-not (Test-DockerAvailable)) {
  throw @"
Docker não está disponível.

Abra o Docker Desktop e aguarde ele iniciar (modo Linux containers), depois rode este script novamente.

Se você não quiser usar Docker, rode o projeto com banco H2 local (sem Docker):
  $env:MICRONAUT_ENVIRONMENTS="local"
  .\gradlew.bat run
"@
}

Write-Host "Subindo SQL Server local via Docker (DB=$DbName)..."
docker compose up -d mssql | Out-Host

Write-Host "Aguardando SQL Server ficar pronto..."
$maxTries = 60
for ($i = 1; $i -le $maxTries; $i++) {
  try {
    docker exec sistemaaluguelcarros-mssql /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P "$SaPassword" -Q "SELECT 1" | Out-Null
    break
  } catch {
    Start-Sleep -Seconds 2
  }
  if ($i -eq $maxTries) {
    throw "SQL Server não ficou pronto a tempo."
  }
}

Write-Host "Criando database (se não existir)..."
$escapedDbName = $DbName.Replace("]", "]]")
$createDbQuery = "IF DB_ID(N'$escapedDbName') IS NULL BEGIN EXEC('CREATE DATABASE [$escapedDbName]'); END"
docker exec sistemaaluguelcarros-mssql /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P "$SaPassword" -Q "$createDbQuery" | Out-Host
if ($LASTEXITCODE -ne 0) {
  throw "Falha ao criar database dentro do container (exit=$LASTEXITCODE)."
}

Write-Host "OK. SQL Server local pronto em localhost:1433 (sa)."
