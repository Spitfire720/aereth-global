# Build 01 Known Risks and Blockers

## High-risk areas

### 1. FragmentEngine command mismatch

Risk: BetonQuest tries to call commands that FragmentEngine does not expose yet.

Mitigation:

- Treat command names as contracts until verified.
- Test commands manually before wiring into dialogue.
- Use no-op placeholder dialogue if backend is not ready.

### 2. Oraxen model/pack mismatch

Risk: Items load but models/textures do not render for clients.

Mitigation:

- Test iteminfo first.
- Test pack send on one client.
- Keep placeholder models acceptable for Build 01.

### 3. MythicMobs damage tuning

Risk: Starter mobs are too strong or too weak.

Mitigation:

- Test against a fresh player, not admin gear.
- Disable complex drops until combat stability is confirmed.

### 4. WorldGuard false positives

Risk: Non-OP cannot interact with quest objects or route areas.

Mitigation:

- Test as non-OP.
- Separate safe settlement region from anomaly site region.

### 5. BetonQuest tag pollution

Risk: Test players get stuck due to old tags/objectives.

Mitigation:

- Maintain reset commands or documented manual cleanup.
- Use a fresh test account for final pass.

## Build blockers

Build 01 cannot pass if any of these remain true:

- Non-OP cannot start the quest.
- Non-OP cannot travel the route.
- Player state does not persist after relog.
- Starter mobs can one-shot a fresh player.
- Server console shows repeated critical errors.
- Oraxen pack prevents players from joining or seeing required UI/props.
- Fragment/Race/Intent state is owned by BetonQuest instead of FragmentEngine.
