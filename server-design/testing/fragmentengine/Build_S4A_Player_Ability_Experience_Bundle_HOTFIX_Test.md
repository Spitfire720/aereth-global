# Build S4A HOTFIX Test

1. Run hotfix script.
2. Run `mvn clean package`.
3. Deploy only after BUILD SUCCESS.
4. Restart server.
5. Test:
   - `/aereth abilitysummary SpitFire720`
   - `/aereth abilityresources SpitFire720`
   - `/aereth abilitysync`
   - `/aereth setdisciplinerank SpitFire720 1`
   - `/aereth setdisciplinerank SpitFire720 4`
