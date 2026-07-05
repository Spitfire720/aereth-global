import { useMemo, useState } from "react";
import { regions, outerZones, type Region } from "../data/regions";
import { regionShapes, crackLines, voidSeasPath } from "../data/mapGeometry";
import { smoothPath } from "../lib/smoothPath";
import RegionPanel from "./RegionPanel";
import LayerToggle, { type LayerKey } from "./LayerToggle";

const VIEW_W = 1600;
const VIEW_H = 900;

const initialLayers: Record<LayerKey, boolean> = {
  cities: false,
  races: false,
  dungeons: false,
  bosses: false,
  questlines: false,
  factions: false,
  erasureZones: true,
  travelRoutes: false,
};

function centroid(id: string): [number, number] {
  const pts = regionShapes[id];
  const x = pts.reduce((s, p) => s + p[0], 0) / pts.length;
  const y = pts.reduce((s, p) => s + p[1], 0) / pts.length;
  return [x, y];
}

export default function WorldMap() {
  const [hoveredId, setHoveredId] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [layers, setLayers] = useState(initialLayers);

  const selected: Region | null = useMemo(
    () => regions.find((r) => r.id === selectedId) ?? null,
    [selectedId],
  );

  const toggleLayer = (key: LayerKey) =>
    setLayers((prev) => ({ ...prev, [key]: !prev[key] }));

  const anyPlaceholderLayerOn =
    layers.cities ||
    layers.races ||
    layers.dungeons ||
    layers.bosses ||
    layers.questlines ||
    layers.factions ||
    layers.travelRoutes;

  return (
    <div className="relative h-[100vh] w-full overflow-hidden bg-void">
      {/* Drifting fog — HTML layer above the SVG for a soft, non-vector blur */}
      <div className="pointer-events-none absolute inset-0 z-10 overflow-hidden">
        <div className="fog-layer fog-a" />
        <div className="fog-layer fog-b" />
        <div className="fog-layer fog-c" />
      </div>

      <svg
        viewBox={`0 0 ${VIEW_W} ${VIEW_H}`}
        preserveAspectRatio="xMidYMid slice"
        className="absolute inset-0 h-full w-full"
        role="img"
        aria-label="Interactive map of Aereth"
      >
        <defs>
          <radialGradient id="voidBg" cx="50%" cy="38%" r="75%">
            <stop offset="0%" stopColor="#0d1220" />
            <stop offset="55%" stopColor="#070a12" />
            <stop offset="100%" stopColor="#020308" />
          </radialGradient>
          <filter id="glow" x="-60%" y="-60%" width="220%" height="220%">
            <feGaussianBlur stdDeviation="10" result="blur" />
            <feMerge>
              <feMergeNode in="blur" />
              <feMergeNode in="SourceGraphic" />
            </feMerge>
          </filter>
          <filter id="softBlur" x="-40%" y="-40%" width="180%" height="180%">
            <feGaussianBlur stdDeviation="6" />
          </filter>
          <linearGradient id="crackGrad" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="#ff3b54" />
            <stop offset="100%" stopColor="#b98cff" />
          </linearGradient>
        </defs>

        <rect x="0" y="0" width={VIEW_W} height={VIEW_H} fill="url(#voidBg)" />

        {/* Absolute Void — bottom fade, non-interactive */}
        <rect
          x="0"
          y={780}
          width={VIEW_W}
          height={VIEW_H - 780}
          fill="#010103"
          opacity="0.9"
        />

        {/* Void Seas shimmer band */}
        <path
          d={smoothPath(voidSeasPath)}
          fill="#0a2a33"
          opacity="0.55"
          className="void-shimmer"
        />
        <text
          x={VIEW_W / 2}
          y={860}
          textAnchor="middle"
          className="fill-slate-dim font-mono"
          fontSize="13"
          letterSpacing="3"
        >
          VOID SEAS — SEALED
        </text>

        {/* Erasure cracks radiating from the Fractured Expanse */}
        {crackLines.map((line, i) => (
          <polyline
            key={i}
            points={line.map((p) => p.join(",")).join(" ")}
            fill="none"
            stroke="url(#crackGrad)"
            strokeWidth={layers.erasureZones ? 2.2 : 1.1}
            strokeLinecap="round"
            opacity={layers.erasureZones ? 0.75 : 0.35}
            className="crack-pulse"
            style={{ animationDelay: `${i * 0.4}s` }}
          />
        ))}

        {/* Regions */}
        {regions.map((region) => {
          const pts = regionShapes[region.id];
          const [cx, cy] = centroid(region.id);
          const isHover = hoveredId === region.id;
          const isSelected = selectedId === region.id;
          const active = isHover || isSelected;
          const unstable = region.mapState === "unstable";

          return (
            <g
              key={region.id}
              tabIndex={0}
              role="button"
              aria-label={`${region.name}, ${region.subtitle}`}
              aria-pressed={isSelected}
              className="cursor-pointer outline-none"
              onMouseEnter={() => setHoveredId(region.id)}
              onMouseLeave={() => setHoveredId(null)}
              onFocus={() => setHoveredId(region.id)}
              onBlur={() => setHoveredId(null)}
              onClick={() => setSelectedId(region.id)}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") setSelectedId(region.id);
              }}
            >
              <path
                d={smoothPath(pts)}
                fill={region.color}
                fillOpacity={active ? 0.42 : 0.26}
                stroke={region.color}
                strokeWidth={active ? 2.5 : 1.3}
                strokeOpacity={active ? 1 : 0.7}
                filter={active ? "url(#glow)" : undefined}
                className={`transition-all duration-300 ${unstable ? "unstable-flicker" : ""} ${
                  isSelected ? "stroke-ink" : ""
                }`}
                style={isSelected ? { stroke: "#e7eaf2", strokeWidth: 2.5 } : undefined}
              />
              <circle
                cx={cx}
                cy={cy}
                r={active ? 7 : 5}
                fill={region.color}
                filter="url(#glow)"
                className="beacon-pulse transition-all duration-300"
              />
              <text
                x={cx}
                y={cy - 22}
                textAnchor="middle"
                className="pointer-events-none select-none font-display"
                fontSize={active ? 22 : 18}
                fill={active ? "#e7eaf2" : "#c3c9da"}
                style={{
                  transition: "font-size 200ms, fill 200ms",
                  textShadow: active ? `0 0 16px ${region.color}` : undefined,
                }}
              >
                {region.name}
              </text>

              {layers.cities && (
                <text
                  x={cx}
                  y={cy + 26}
                  textAnchor="middle"
                  className="fill-signal font-mono"
                  fontSize="10"
                  letterSpacing="1"
                >
                  ◆ recovered city marker
                </text>
              )}
            </g>
          );
        })}

        {/* Outer zones — sealed, non-interactive */}
        {outerZones.map((zone) => (
          <g key={zone.id} opacity="0.55">
            <text
              x={(zone.position.x / 100) * VIEW_W}
              y={(zone.position.y / 100) * VIEW_H}
              textAnchor="middle"
              className="fill-slate-dim font-mono"
              fontSize="12"
              letterSpacing="2"
            >
              {zone.id === "absolute-void" ? zone.name.toUpperCase() : ""}
            </text>
          </g>
        ))}
      </svg>

      {/* Top chrome */}
      <div className="pointer-events-none absolute inset-x-0 top-0 z-20 flex items-start justify-between gap-4 p-5 md:p-8">
        <div className="pointer-events-auto flex items-center gap-3">
          <span className="font-display text-xl tracking-wide text-ink">AERETH</span>
          <span className="hidden font-mono text-[10px] uppercase tracking-[0.2em] text-slate-dim sm:inline">
            World Archive
          </span>
        </div>
        <div className="pointer-events-auto rounded-md border border-panel-edge bg-panel/70 px-3 py-1.5 font-mono text-[11px] text-slate backdrop-blur-md">
          play.aereth.live
        </div>
      </div>

      {/* Bottom chrome: layer toggles + hint */}
      <div className="pointer-events-none absolute inset-x-0 bottom-0 z-20 flex flex-col gap-3 p-5 md:p-8">
        {anyPlaceholderLayerOn && (
          <div className="pointer-events-auto max-w-sm rounded-md border border-panel-edge bg-panel/85 px-3 py-2 font-mono text-[11px] text-slate-dim backdrop-blur-md">
            Layer data not yet recovered. Placeholder markers only.
          </div>
        )}
        <div className="flex items-end justify-between gap-3">
          <LayerToggle active={layers} onToggle={toggleLayer} />
          <div className="pointer-events-none hidden font-mono text-[10px] uppercase tracking-[0.14em] text-slate-dim md:block">
            click / tap a region to open its record
          </div>
        </div>
      </div>

      <RegionPanel region={selected} onClose={() => setSelectedId(null)} />

      {/* Mobile tappable region list fallback */}
      <div className="pointer-events-auto absolute inset-x-0 bottom-16 z-20 flex gap-2 overflow-x-auto px-4 pb-1 md:hidden">
        {regions.map((r) => (
          <button
            key={r.id}
            onClick={() => setSelectedId(r.id)}
            className="shrink-0 rounded-full border px-3 py-1.5 font-mono text-[10px] uppercase tracking-[0.1em] backdrop-blur-md"
            style={{
              borderColor: r.color,
              color: selectedId === r.id ? "#05070c" : r.color,
              background: selectedId === r.id ? r.color : "rgba(11,15,26,0.75)",
            }}
          >
            {r.name}
          </button>
        ))}
      </div>
    </div>
  );
}
