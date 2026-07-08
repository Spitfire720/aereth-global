param(
    [string]$RepoRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Write-NoBomFile {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$Content
    )
    [System.IO.File]::WriteAllText((Resolve-Path $Path), $Content, $Utf8NoBom)
}

function Copy-ImplementationFile {
    param(
        [Parameter(Mandatory = $true)][string]$RelativePath
    )
    $Source = Join-Path $RepoRoot (Join-Path "implementation-files" $RelativePath)
    $Target = Join-Path $RepoRoot $RelativePath
    if (!(Test-Path $Source)) {
        throw "Missing implementation file: $Source"
    }
    $TargetDir = Split-Path $Target -Parent
    New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null
    Copy-Item $Source $Target -Force
    $Text = [System.IO.File]::ReadAllText((Resolve-Path $Target))
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    Write-NoBomFile -Path $Target -Content $Text
    Write-Host "[Build S3C] Copied $RelativePath"
}

function Patch-TextFile {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][scriptblock]$Patch
    )
    if (!(Test-Path $Path)) {
        throw "Missing file to patch: $Path"
    }
    $Text = [System.IO.File]::ReadAllText((Resolve-Path $Path))
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    $NewText = & $Patch $Text
    Write-NoBomFile -Path $Path -Content $NewText
}

Write-Host "[Build S3C] Applying Ability Loadout UI patch"

Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityLoadoutGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityCodexGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\FragmentEngineGuiListener.java"

$CommandPath = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"

Patch-TextFile -Path $CommandPath -Patch {
    param($Text)

    if (!$Text.Contains('import live.aereth.fragmentengine.gui.AbilityLoadoutGui;')) {
        $Text = $Text.Replace(
            'import live.aereth.fragmentengine.gui.AbilityCodexGui;',
            'import live.aereth.fragmentengine.gui.AbilityCodexGui;' + "`r`n" + 'import live.aereth.fragmentengine.gui.AbilityLoadoutGui;'
        )
    }

    if (!$Text.Contains('private final AbilityLoadoutGui abilityLoadoutGui;')) {
        $Text = $Text.Replace(
            'private final AbilityCodexGui abilityCodexGui;',
            'private final AbilityCodexGui abilityCodexGui;' + "`r`n" + '    private final AbilityLoadoutGui abilityLoadoutGui;'
        )
    }

    if (!$Text.Contains('this.abilityLoadoutGui = new AbilityLoadoutGui')) {
        $Text = $Text.Replace(
            'this.abilityCodexGui = new AbilityCodexGui(plugin, characters, disciplines, abilities);',
            'this.abilityCodexGui = new AbilityCodexGui(plugin, characters, disciplines, abilities);' + "`r`n" + '        this.abilityLoadoutGui = new AbilityLoadoutGui(plugin, characters, disciplines, abilities);'
        )
    }

    if (!$Text.Contains('case "abilityloadout" -> abilityLoadout(sender);')) {
        $Text = $Text.Replace(
            'case "abilitygui" -> abilityGui(sender);',
            'case "abilityloadout" -> abilityLoadout(sender);' + "`r`n" + '                case "abilitygui" -> abilityGui(sender);'
        )
    }

    if (!$Text.Contains('/aereth abilityloadout')) {
        $Text = $Text.Replace(
            'sender.sendMessage(Text.color("&b/aereth abilitygui"));',
            'sender.sendMessage(Text.color("&b/aereth abilityloadout"));' + "`r`n" + '        sender.sendMessage(Text.color("&b/aereth abilitygui"));'
        )
    }

    if (!$Text.Contains('private void abilityLoadout(CommandSender sender)')) {
$Method = @'
    private void abilityLoadout(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Loadout GUI."));
            return;
        }
        abilityLoadoutGui.open(player);
    }

'@
        $Text = $Text.Replace(
            '    private void abilityGui(CommandSender sender)',
            $Method + '    private void abilityGui(CommandSender sender)'
        )
    }

    if ($Text.Contains('"abilitygui",') -and !$Text.Contains('"abilityloadout", "abilitygui"')) {
        $Text = $Text.Replace('"abilitygui",', '"abilityloadout", "abilitygui",')
    }

    return $Text
}

# Clean invisible BOM goblins from Java source, because apparently this is our life now.
Get-ChildItem (Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java") -Recurse -Filter "*.java" | ForEach-Object {
    $Path = $_.FullName
    $Text = [System.IO.File]::ReadAllText($Path)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    [System.IO.File]::WriteAllText($Path, $Text, $Utf8NoBom)
}

Write-Host "[Build S3C] Build S3C patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package"
