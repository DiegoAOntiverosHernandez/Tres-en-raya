param([string]$videoPath = "$PSScriptRoot\recursos\video\Super Smash Bros. Ultimate _3 2 1 GO!_ Green Screen (4K).mp4")

# Resolución dinámica de reserva en recursos/video
if (-not (Test-Path $videoPath)) {
    $candidato = Get-ChildItem -Path "$PSScriptRoot\recursos\video\*.mp4" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidato) {
        $videoPath = $candidato.FullName
    }
}

if (-not (Test-Path $videoPath)) {
    exit
}

Add-Type -AssemblyName PresentationFramework
Add-Type -AssemblyName PresentationCore

$win = New-Object Windows.Window
$win.Title = "Super Smash Bros 3 2 1 GO!"
$win.WindowStyle = 'None'
$win.ResizeMode = 'NoResize'
$win.Width = 640
$win.Height = 360
$win.WindowStartupLocation = 'CenterScreen'
$win.Topmost = $true
$win.Background = [Windows.Media.Brushes]::Black

$media = New-Object Windows.Controls.MediaElement
$media.LoadedBehavior = 'Manual'
$media.Source = [Uri]$videoPath
$media.Add_MediaEnded({
    $win.Close()
})
$win.Content = $media

$win.Add_Loaded({
    $media.Play()
})

$timer = New-Object Windows.Threading.DispatcherTimer
$timer.Interval = [TimeSpan]::FromSeconds(6)
$timer.Add_Tick({
    $timer.Stop()
    $win.Close()
})
$timer.Start()

$win.Add_KeyDown({
    $win.Close()
})
$win.Add_MouseDown({
    $win.Close()
})

[void]$win.ShowDialog()
