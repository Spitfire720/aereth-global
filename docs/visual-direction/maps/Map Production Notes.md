# Map Production Notes

## Current Assets

- `website/public/assets/maps/aereth_macro_world_map_v1.png`
- `website/public/assets/maps/aereth_detailed_world_atlas_v1.png`
- `website/public/assets/maps/aereth_regional_progression_map_v1.png`
- `website/public/assets/maps/aereth_political_cultural_atlas_v1.png`

## Known Limitations

These are concept-quality AI-generated atlas images. They are visually strong, but some tiny labels and decorative text may be imperfect.

Canon source of truth remains:

- `lore/obsidian-vault/02_WORLD_ATLAS/Maps/`

## Recommended Manual Cleanup

Priority cleanup tasks:

1. Confirm all major labels are readable.
2. Replace any generated misspellings manually.
3. Add final consistent labels for public website use.
4. Make alternate label-free background versions if possible.
5. Create smaller web-optimized `.webp` versions.

## Suggested Public Usage

- Macro map: website/world hero
- Detailed atlas: `/world/atlas`
- Progression map: `/world/progression`
- Political map: `/world/factions`

## File Naming Rule

Use lowercase and underscores:

`aereth_[map_type]_v[number].[extension]`

Example:

`aereth_regional_progression_map_v1.png`
