# FragmentEngine Build 1 Source

Fresh source project for Aereth FragmentEngine v1.8.0.

This project was rebuilt from the live server/runtime schema because the clean source project was not available.

## Build

```powershell
cd "C:\path\to\FragmentEngine_Build1_Source"
mvn clean package
```

Output:

```text
target/FragmentEngine-1.8.0.jar
```

## Install on Godlike

1. Stop server.
2. Backup current jar:
   - `plugins/FragmentEngine-1.7.0.jar`
3. Upload:
   - `target/FragmentEngine-1.8.0.jar`
4. Make sure only one active FragmentEngine `.jar` exists.
5. Start server.

## Runtime folders preserved

Build 1 uses the existing folders:

```text
plugins/FragmentEngine/accounts/
plugins/FragmentEngine/characters/
plugins/FragmentEngine/agent/
```

It does not force migration to `data/players`.

## Legacy commands preserved

```text
/aereth activity <player> <activityType> <amount> <source>
/aereth echo <player> <echoId> <scope> <source>
/aereth attach <player> <fragmentId>
/aereth erasure <player> <amount> <source>
```

These are required because BetonQuest AerethAwakening already calls them.

## Test checklist

```text
/aereth status
/aereth profile SpitFire720
/aereth character SpitFire720
/aereth createcharacter SpitFire720 1 remnant
/aereth addxp SpitFire720 5000
/papi parse me %aereth_level%
/papi parse me %aereth_xp_required%
/aereth activity SpitFire720 OBSERVATION 3 manual_test
/aereth echo SpitFire720 first_remnant_contact personal manual_test
/aereth attach SpitFire720 echo_burden
/aereth erasure SpitFire720 1.5 manual_test
/aereth agent export
```
