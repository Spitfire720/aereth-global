// Race / entity data — sourced from the Aereth Notion canon (Races and Entities
// Bible, Asset Tracker). Canon explicitly has not finished classifying most of
// these (playable vs non-playable, faction vs race). We surface that
// uncertainty in the UI rather than resolving it ourselves — per the project's
// own rule: "No race gets art/model priority until its gameplay role is classified."

export type EntityType =
  | "race-or-entity"
  | "faction-or-entity"
  | "ancient-entity"
  | "ancient-mystery"
  | "enemy-or-faction-or-entity";

export interface Entity {
  id: string;
  name: string;
  type: EntityType;
  /** "TBD" reflects canon's own current classification state */
  playable: "yes" | "no" | "TBD";
  note: string;
  priority: "high" | "medium" | "low";
}

export const entities: Entity[] = [
  {
    id: "sylvae",
    name: "Sylvae",
    type: "race-or-entity",
    playable: "TBD",
    note: "Needs identity lock.",
    priority: "high",
  },
  {
    id: "vireborn",
    name: "Vireborn",
    type: "race-or-entity",
    playable: "TBD",
    note: "Strong candidate.",
    priority: "high",
  },
  {
    id: "echoforms",
    name: "Echoforms",
    type: "race-or-entity",
    playable: "TBD",
    note: "Fits the memory theme closely.",
    priority: "high",
  },
  {
    id: "nullborn",
    name: "Nullborn",
    type: "race-or-entity",
    playable: "TBD",
    note: "Singular form, not \"Nullborns.\"",
    priority: "high",
  },
  {
    id: "fractured-sentinels",
    name: "Fractured Sentinels",
    type: "enemy-or-faction-or-entity",
    playable: "no",
    note: "Likely a non-playable guardian/construct faction.",
    priority: "medium",
  },
  {
    id: "gravewardens",
    name: "Gravewardens",
    type: "faction-or-entity",
    playable: "TBD",
    note: "Could resolve as a faction rather than a race.",
    priority: "medium",
  },
  {
    id: "cenotaphs",
    name: "Cenotaphs",
    type: "ancient-entity",
    playable: "no",
    note: "World/lore entity, not a playable race.",
    priority: "medium",
  },
  {
    id: "the-unnamed",
    name: "The Unnamed",
    type: "ancient-mystery",
    playable: "TBD",
    note: "Kept deliberately mysterious.",
    priority: "high",
  },
];
