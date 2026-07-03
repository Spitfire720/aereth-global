$GodlikeRemote = "godlike:"
$R2Remote = "r2:aerethsnaphots"

# Confirmed: godlike: shows plugins/, logs/, server.properties.
$ServerRoot = "."

# Replace after wrangler deploy.
$PublicBaseUrl = "https://aereth-snapshot-gateway.aereth.workers.dev"

$SnapshotPrefix = "aereth-1e766ea2214b876ed44ec889cc404cc5/latest"

$LocalRoot = "$PSScriptRoot"
$RawDir = "$LocalRoot\raw"
$LatestDir = "$LocalRoot\latest"
$ArchiveDir = "$LocalRoot\snapshots"

$KeepLocalSnapshots = 10
