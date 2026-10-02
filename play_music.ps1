param(
    [string]$audioPath = "$PSScriptRoot\recursos\musica\Manuel - Gas Gas Gas.mp3",
    [double]$volume = 0.8
)

# Resolución dinámica de reserva en recursos/musica o recursos/audio
if (-not (Test-Path $audioPath)) {
    $candidato = Get-ChildItem -Path "$PSScriptRoot\recursos\musica\*.mp3", "$PSScriptRoot\recursos\audio\*.mp3" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidato) {
        $audioPath = $candidato.FullName
    }
}

if (-not (Test-Path $audioPath)) {
    exit
}

Add-Type -AssemblyName PresentationCore
Add-Type -AssemblyName WindowsBase

$player = New-Object System.Windows.Media.MediaPlayer
$player.Open([Uri]$audioPath)
$player.Volume = $volume

# Bucle continuo
$player.Add_MediaEnded({
    $player.Position = [TimeSpan]::Zero
    $player.Play()
})

$player.Play()

# Mantener vivo el hilo de audio con Dispatcher
[System.Windows.Threading.Dispatcher]::Run()
