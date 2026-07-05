export type LayerKey =
  | "cities"
  | "races"
  | "dungeons"
  | "bosses"
  | "questlines"
  | "factions"
  | "erasureZones"
  | "travelRoutes";

export const LAYER_LABELS: Record<LayerKey, string> = {
  cities: "Cities",
  races: "Races",
  dungeons: "Dungeons",
  bosses: "Bosses",
  questlines: "Questlines",
  factions: "Factions",
  erasureZones: "Erasure Zones",
  travelRoutes: "Travel Routes",
};

interface Props {
  active: Record<LayerKey, boolean>;
  onToggle: (key: LayerKey) => void;
}

export default function LayerToggle({ active, onToggle }: Props) {
  return (
    <div className="pointer-events-auto flex max-w-full flex-wrap gap-1.5 rounded-lg border border-panel-edge bg-panel/80 p-2 backdrop-blur-md">
      {(Object.keys(LAYER_LABELS) as LayerKey[]).map((key) => {
        const isOn = active[key];
        return (
          <button
            key={key}
            onClick={() => onToggle(key)}
            aria-pressed={isOn}
            className={`rounded-md border px-2.5 py-1.5 font-mono text-[10px] uppercase tracking-[0.1em] transition ${
              isOn
                ? "border-signal/60 bg-signal/10 text-signal"
                : "border-panel-edge text-slate-dim hover:border-slate hover:text-slate"
            }`}
          >
            {LAYER_LABELS[key]}
          </button>
        );
      })}
    </div>
  );
}
