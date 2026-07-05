import type { ReactNode } from "react";
import type { Region } from "../data/regions";

interface Props {
  region: Region | null;
  onClose: () => void;
}

const stateLabel: Record<Region["mapState"], string> = {
  known: "Known",
  unstable: "Unstable",
  sealed: "Sealed",
};

const stateColor: Record<Region["mapState"], string> = {
  known: "text-signal",
  unstable: "text-corrupt",
  sealed: "text-slate",
};

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="border-t border-panel-edge/60 py-3 first:border-t-0 first:pt-0">
      <div className="font-mono text-[10px] uppercase tracking-[0.18em] text-slate-dim">
        {label}
      </div>
      <div className="mt-1 text-sm leading-relaxed text-ink/90">{children}</div>
    </div>
  );
}

export default function RegionPanel({ region, onClose }: Props) {
  const open = region !== null;

  return (
    <aside
      className={`fixed inset-x-0 bottom-0 top-auto z-40 flex max-h-[78vh] w-full flex-col rounded-t-2xl border-t border-panel-edge bg-panel/95 backdrop-blur-md transition-transform duration-500 ease-out md:inset-y-0 md:right-0 md:left-auto md:top-0 md:max-h-none md:w-full md:max-w-sm md:rounded-none md:border-l md:border-t-0 ${
        open
          ? "translate-y-0 md:translate-x-0"
          : "translate-y-full md:translate-x-full md:translate-y-0"
      }`}
      aria-hidden={!open}
    >
      {region && (
        <div className="flex h-full flex-col overflow-y-auto">
          <div
            className="h-1.5 w-full shrink-0"
            style={{ background: region.color, boxShadow: `0 0 24px ${region.color}` }}
          />
          <div className="flex items-start justify-between gap-4 px-6 pt-6">
            <div>
              <div className="font-mono text-[10px] uppercase tracking-[0.2em] text-slate-dim">
                Archive Entry
              </div>
              <h2 className="mt-1 font-display text-3xl text-ink">{region.name}</h2>
              <p className="mt-1 font-display text-base italic text-slate">
                {region.subtitle}
              </p>
            </div>
            <button
              onClick={onClose}
              aria-label="Close region record"
              className="mt-1 shrink-0 rounded-full border border-panel-edge p-2 text-slate transition hover:border-signal hover:text-signal"
            >
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                <path
                  d="M1 1L13 13M13 1L1 13"
                  stroke="currentColor"
                  strokeWidth="1.5"
                  strokeLinecap="round"
                />
              </svg>
            </button>
          </div>

          <div className="mt-4 flex items-center gap-2 px-6">
            <span
              className={`font-mono text-[11px] uppercase tracking-[0.16em] ${stateColor[region.mapState]}`}
            >
              ● {stateLabel[region.mapState]}
            </span>
            {region.threatLevel && (
              <span className="font-mono text-[11px] uppercase tracking-[0.16em] text-slate">
                / Threat: {region.threatLevel}
              </span>
            )}
          </div>

          <div className="mt-5 px-6 pb-8">
            <Field label="Biome">{region.biome}</Field>
            <Field label="Recovered Mood">{region.mood}</Field>
            <Field label="Gameplay Role">
              {region.gameplayRole ?? (
                <span className="text-slate-dim italic">Not yet classified.</span>
              )}
            </Field>
            <Field label="Starter Suitability">
              {region.starterSuitability ?? (
                <span className="text-slate-dim italic">Not yet classified.</span>
              )}
            </Field>
            <Field label="Known Factions">
              {region.mainFactions?.length ? (
                region.mainFactions.join(", ")
              ) : (
                <span className="text-slate-dim italic">No records recovered.</span>
              )}
            </Field>
            <Field label="Native Races">
              {region.nativeRaces?.length ? (
                region.nativeRaces.join(", ")
              ) : (
                <span className="text-slate-dim italic">No records recovered.</span>
              )}
            </Field>
            <Field label="Fragment Themes">
              {region.fragmentThemes?.length ? (
                region.fragmentThemes.join(", ")
              ) : (
                <span className="text-slate-dim italic">No records recovered.</span>
              )}
            </Field>
          </div>

          <div className="mt-auto flex gap-3 border-t border-panel-edge/60 px-6 py-5">
            <button
              disabled
              title="Full region records are not published yet"
              className="flex-1 rounded-md border border-panel-edge py-2.5 text-center font-mono text-xs uppercase tracking-[0.14em] text-slate-dim"
            >
              Full Record — Coming Soon
            </button>
          </div>
        </div>
      )}
    </aside>
  );
}
