# Build S3J Test - Ability Resource Costs

## Compile
Run:

```powershell
mvn clean package
```

Expected: BUILD SUCCESS.

## Deploy
Deploy the built jar only. No abilities.yml upload is required for this build.

## In-game Tests
1. Open `/aereth abilityactivation`.
2. Confirm the resource panel appears.
3. Activate a stamina, mana, focus, instability, or health-cost ability.
4. Confirm the resource decreases.
5. Try activating again during cooldown.
6. Confirm cooldown still blocks repeated use.
7. Wait a few seconds and refresh the GUI.
8. Confirm resource pools regenerate.
9. Use Sneak + Swap Hands to refresh hotbar binds.
10. Confirm hotbar lore shows affordability.
11. Right-click a hotbar ability.
12. Confirm cost is paid and effects still fire.

## Expected Result
- Ability activation fails when resource is insufficient.
- Cooldown only starts after resource payment.
- GUI and hotbar show resource state.
- No console errors.
