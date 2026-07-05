# Aereth — World Archive (Web Prototype)

Map-first homepage prototype for Aereth, built per the current Notion canon
direction (Frontend Roadmap Phase 1 + 2: static prototype → interactive map).
This intentionally does **not** include the wiki, character creation, or
server-data integration — those are Phase 3/4 per the roadmap and aren't live
yet.

## Stack

Astro 7 + Tailwind CSS v4 + React (for the interactive map only) + TypeScript.
Fonts are self-hosted via `@fontsource` (Fraunces, Inter, IBM Plex Mono) so
the site has no external runtime dependencies and works offline.

## Run locally

```bash
npm install
npm run dev
```

Then open the printed local URL (usually `http://localhost:4321`).

```bash
npm run build      # production build → dist/
npm run preview    # preview the production build locally
```

## What's here

- `src/components/WorldMap.tsx` — the interactive map: 7 clickable regions,
  hover/select states, layer toggle strip, fog/crack/beacon animations,
  mobile bottom-sheet region list.
- `src/components/RegionPanel.tsx` — the region record panel (desktop: right
  side panel, mobile: bottom sheet).
- `src/data/regions.ts` — canon region content, sourced from the Regions
  Bible + Asset Tracker.
- `src/data/entities.ts` — canon race/entity content, sourced from the Races
  and Entities Bible.
- `src/data/mapGeometry.ts` — the hand-drawn landmass shapes for the SVG map
  (visual only, separate from canon content on purpose).

## Assumptions made — please sanity-check these

1. **Map layout** is an original abstract composition loosely following the
   reference map images' arrangement (gold NW / ice N / violet NE / teal SW /
   red SE / green S / violet center), not a trace of them.
2. **`mapState` (known / unstable / sealed)** on each region is *my* read of
   the mood language already in the Regions Bible — canon's own per-region
   production status (idea/draft/locked/etc.) hasn't been filled in yet. Easy
   to find in `regions.ts` and correct once that's decided.
3. **Region ↔ race/faction associations** are left empty ("No records
   recovered") rather than guessed, since the Regions Bible doesn't assign
   them yet and Decisions Locked says not to invent this casually.
4. **Race/entity classification** (playable vs. not) is shown as "pending"
   wherever canon itself has it as TBD — this is intentional, not a bug.
5. Land shapes, fog, and crack effects are placeholder SVG/CSS per the
   Frontend Roadmap ("world map image placeholder" for Phase 1) — ready to
   swap for real illustrated map art later without touching the data files.

## What to replace later

- Swap the SVG landmasses in `mapGeometry.ts` for real illustrated map art
  or the actual reference map image once one is finalized as canon.
- Fill in `mainFactions`, `nativeRaces`, `fragmentThemes` per region once
  Regions Bible / Factions Bible get more specific.
- Wire up the "Cities / Races / Dungeons / Bosses / Questlines / Factions /
  Travel Routes" toggles to real data — right now toggling them just shows a
  "layer data not yet recovered" placeholder, on purpose.
- Phase 3 (character/race preview) and Phase 4 (live server data) per the
  Frontend Roadmap are not built yet.
