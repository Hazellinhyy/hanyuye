$ErrorActionPreference = "Stop"

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$server = Join-Path $baseDir "llama.cpp\llama-server.exe"
$model = Join-Path $baseDir "models\gemma-4-E2B-it-UD-IQ2_M.gguf"

if (-not (Test-Path $server)) {
    throw "llama-server.exe not found: $server"
}

if (-not (Test-Path $model)) {
    throw "Gemma model not found: $model"
}

& $server `
    -m $model `
    --host 127.0.0.1 `
    --port 9100 `
    --ctx-size 4096 `
    --threads 6 `
    --verbose `
    --no-webui
