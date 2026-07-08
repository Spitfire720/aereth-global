# Build S4A HOTFIX v2 Test

1. Apply pack.
2. Run `mvn clean package`.
3. Deploy jar only after `BUILD SUCCESS`.
4. Restart server.
5. Test:
   - `/aereth abilitysummary SpitFire720`
   - `/aereth abilityresources SpitFire720`
   - `/aereth abilitysync`
   - `/aereth setdisciplinerank SpitFire720 1`
   - `/aereth setdisciplinerank SpitFire720 4`

Expected: unlock recap, resource output, hotbar sync, no console errors.
