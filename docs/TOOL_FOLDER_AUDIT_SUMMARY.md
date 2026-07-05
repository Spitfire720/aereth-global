# Tool Folder Audit Summary

## Verdict

The remaining root tool folders should not be moved blindly.

AerethPipelineTools contains active snapshot tooling and generated snapshot/runtime output.

AerethSnapshotGit, AerethSupportAgent, and AerethDesignTools may still be tied to local scripts, repo mirrors, or workflow tooling.

## Important finding

The audit command exposed large snapshot/runtime paths under:

AerethPipelineTools/AerethSnapshotAgent/snapshots/

That confirms the folder contains generated output and should not be treated as clean source documentation.

## Current decision

Keep the tool folders in root for now:

- AerethPipelineTools
- AerethSnapshotGit
- AerethSupportAgent
- AerethDesignTools

Do not move them until the snapshot publisher and local scripts are refactored safely.

## Repo rule

Generated snapshot folders must stay ignored.

Operational tooling can remain local/root-level until the snapshot pipeline is rebuilt into a cleaner tools/ structure.

## Next cleanup phase

1. Leave tool folders in root.
2. Do not commit generated snapshot/runtime output.
3. Move on to Obsidian cleanup.
4. Later, refactor tooling properly if needed.
