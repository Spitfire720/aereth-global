# AerethCreatorFragmentBridge

This is a small bridge plugin. It does not replace AerethCharacterCreatorCore.

Purpose:
- Reads: plugins/AerethCharacterCreatorCore/player_profiles.yml
- Writes:
  - plugins/FragmentEngine/accounts/<uuid>.yml
  - plugins/FragmentEngine/characters/<uuid>-slot<n>.yml

Why this exists:
- The uploaded AerethCharacterCreatorCore-1.0.0.jar is compiled only.
- It stores race/slot selections in its own player_profiles.yml.
- It does not directly write FragmentEngine Build 1 character files.
- This bridge mirrors that data into FragmentEngine safely.

Build:
1. Extract this folder to:
   C:\Users\Bernardo\Desktop\Aereth global\05_PLUGIN_DEV\MinecraftDev_raw_projects\AerethCreatorFragmentBridge

2. Run:
   cd "C:\Users\Bernardo\Desktop\Aereth global\05_PLUGIN_DEV\MinecraftDev_raw_projects\AerethCreatorFragmentBridge"
   mvn clean package

3. Upload this jar to the server:
   target\AerethCreatorFragmentBridge-1.0.0.jar
   -> plugins/AerethCreatorFragmentBridge-1.0.0.jar

4. Restart the server.

5. Run:
   /aerethbridge status
   /aerethbridge syncall
   /aereth profile SpitFire720
   /aereth character SpitFire720
   /aereth agent export

6. Publish snapshot:
   Start-ScheduledTask -TaskName "AerethSnapshotPublisher"

Then ask ChatGPT to check GitHub.

Notes:
- Auto-sync runs every 10 seconds by default.
- Existing FragmentEngine characters are not overwritten unless config.yml sets overwrite-existing-fragment-characters: true.
