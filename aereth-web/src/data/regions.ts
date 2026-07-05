// Region data — sourced from the Aereth Notion canon (Regions Bible, Asset Tracker).
// Fields left as `null` are not yet classified in canon. Do not invent values for them;
// the UI is written to render an "unclassified" state gracefully instead.
//
// `mapState` (known / unstable / sealed) is an interaction-layer read of the mood
// language already in the Regions Bible (e.g. Kaer Morr = "harsh, defensive",
// Fractured Expanse = "dangerous scar, central to map identity"). It is NOT a new
// canon status — canon's own status field (idea/draft/locked/active-build/archived)
// hasn't been filled in per-region yet, so this is an inferred UX read, flagged here
// so it's easy to correct once real per-region status exists.

export type MapState = "known" | "unstable" | "sealed";

export interface Region {
  id: string;
  name: string;
  /** Short mood line, e.g. "The Anchored Heart" */
  subtitle: string;
  /** Biome identity from canon */
  biome: string;
  /** Visual mood description from canon */
  mood: string;
  /** Gameplay role from canon, where established */
  gameplayRole: string | null;
  threatLevel: "low" | "moderate" | "high" | "extreme" | null;
  starterSuitability: string | null;
  mainFactions: string[] | null;
  nativeRaces: string[] | null;
  fragmentThemes: string[] | null;
  mapState: MapState;
  /** Accent color for this region's map shape + glow, matched to reference art */
  color: string;
  /** Center point of the landmass, in percentage of the map viewBox */
  position: { x: number; y: number };
}

export const regions: Region[] = [
  {
    id: "valterra",
    name: "Valterra",
    subtitle: "The Anchored Heart",
    biome: "Highland roots, ordered stonework, mountain-fed rivers",
    mood: "Anchored, ordered, enduring — the strongest resistance to Erasure through roots, stone, and memory anchors",
    gameplayRole: "Candidate for central world identity or a major narrative region",
    threatLevel: "low",
    starterSuitability: "Strong candidate for early play",
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "known",
    color: "#d4ac52",
    position: { x: 24, y: 20 },
  },
  {
    id: "kaer-morr",
    name: "Kaer Morr",
    subtitle: "Hollowed, Frozen, Lost",
    biome: "Ice mountains, old defensive architecture",
    mood: "Harsh, old, defensive — memory freezes here before it fades",
    gameplayRole: null,
    threatLevel: "high",
    starterSuitability: null,
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "known",
    color: "#aee3f0",
    position: { x: 52, y: 15 },
  },
  {
    id: "nox-aetern",
    name: "Nox Aetern",
    subtitle: "Forbidden, Arcanum, Eternal",
    biome: "Spire-cities, arcane architecture",
    mood: "Dark, arcane — a corruption-heavy candidate for endgame content",
    gameplayRole: "Endgame / corruption-heavy candidate",
    threatLevel: "extreme",
    starterSuitability: "Not suited to new players",
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "unstable",
    color: "#9b6bff",
    position: { x: 80, y: 20 },
  },
  {
    id: "lyssara",
    name: "Lyssara",
    subtitle: "Tides, Trade, Shadows",
    biome: "Coastal, tidal, trade-route heavy",
    mood: "Should support mystery, exploration, and survivability",
    gameplayRole: "Potential early-region candidate",
    threatLevel: "low",
    starterSuitability: "Potential early-region candidate",
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "known",
    color: "#4fc9c5",
    position: { x: 22, y: 52 },
  },
  {
    id: "fractured-expanse",
    name: "Fractured Expanse",
    subtitle: "Unstable, Shifting, Corrupting",
    biome: "The scarred center of the continent",
    mood: "The dangerous scar between meanings — central to the map's identity",
    gameplayRole: "Central instability zone, not a normal leveling region",
    threatLevel: "extreme",
    starterSuitability: "Not suited to new players",
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "unstable",
    color: "#7a4fe0",
    position: { x: 52, y: 46 },
  },
  {
    id: "solmyr",
    name: "Solmyr",
    subtitle: "Mutation, Zeal, Corruption",
    biome: "Volcanic ridge, radiant/desert candidate",
    mood: "Solar, desert, or radiant region candidate",
    gameplayRole: null,
    threatLevel: "high",
    starterSuitability: null,
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "unstable",
    color: "#e2603a",
    position: { x: 80, y: 55 },
  },
  {
    id: "elderwyth",
    name: "Elderwyth",
    subtitle: "Growth, Wild, Eternal",
    biome: "Ancient forest, spiral root-formations",
    mood: "Ancient green / forest / spiral region candidate",
    gameplayRole: null,
    threatLevel: "moderate",
    starterSuitability: null,
    mainFactions: null,
    nativeRaces: null,
    fragmentThemes: null,
    mapState: "known",
    color: "#6fa85a",
    position: { x: 58, y: 76 },
  },
];

export interface OuterZone {
  id: string;
  name: string;
  mood: string;
  position: { x: number; y: number };
}

// The Void Seas and Absolute Void are the map's outer boundary — canon notes
// they are "not normal exploration content at first," so these render sealed
// and are not clickable like the seven main regions.
export const outerZones: OuterZone[] = [
  {
    id: "void-seas",
    name: "Void Seas",
    mood: "The outer sea boundary of the known world",
    position: { x: 42, y: 92 },
  },
  {
    id: "absolute-void",
    name: "Absolute Void",
    mood: "Beyond the Void Seas. Not normal exploration content at first.",
    position: { x: 50, y: 99 },
  },
];
