# Aereth Local Tools

This folder is reserved for repo-adjacent tooling documentation.

The following root-level tool folders may still exist locally and should not be moved blindly because scheduled tasks or scripts may reference their exact paths:

- `AerethPipelineTools`
- `AerethSnapshotGit`
- `AerethSupportAgent`
- `AerethDesignTools`

Before moving them, check the Windows Scheduled Task named `AerethSnapshotPublisher` and any scripts that reference their current locations.

Root cleanliness is good. Breaking the snapshot pipeline for aesthetics is how civilizations fall, but with more PowerShell.