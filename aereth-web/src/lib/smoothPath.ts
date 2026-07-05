export type Point = [number, number];

const mid = (a: Point, b: Point): Point => [(a[0] + b[0]) / 2, (a[1] + b[1]) / 2];

/**
 * Builds a smooth closed SVG path through a loop of control points.
 * Used to turn a handful of hand-placed points into an organic,
 * continent-like landmass shape instead of a hard polygon.
 */
export function smoothPath(points: Point[]): string {
  const len = points.length;
  if (len < 3) return "";
  const d: string[] = [];
  const m0 = mid(points[len - 1], points[0]);
  d.push(`M ${m0[0].toFixed(1)} ${m0[1].toFixed(1)}`);
  for (let i = 0; i < len; i++) {
    const p = points[i];
    const next = points[(i + 1) % len];
    const m = mid(p, next);
    d.push(`Q ${p[0].toFixed(1)} ${p[1].toFixed(1)} ${m[0].toFixed(1)} ${m[1].toFixed(1)}`);
  }
  d.push("Z");
  return d.join(" ");
}
