import type { Point } from "../lib/smoothPath";

// viewBox is 0 0 1600 900. Compositionally loose homage to the reference world
// map (gold NW, ice N, violet NE, teal SW, red SE, green S, violet scar center)
// — an original abstract shape, not a trace of any source image.
export const regionShapes: Record<string, Point[]> = {
  valterra: [
    [120, 70], [230, 30], [340, 50], [430, 110], [460, 190],
    [420, 270], [340, 330], [230, 340], [130, 300], [70, 210], [80, 130],
  ],
  "kaer-morr": [
    [600, 60], [700, 20], [800, 40], [860, 100], [840, 180],
    [760, 230], [660, 220], [590, 160], [570, 100],
  ],
  "nox-aetern": [
    [1180, 60], [1300, 30], [1430, 50], [1520, 120], [1540, 220],
    [1470, 300], [1360, 330], [1250, 300], [1170, 220], [1140, 130],
  ],
  lyssara: [
    [110, 420], [220, 390], [340, 410], [430, 470], [450, 560],
    [400, 640], [300, 680], [190, 660], [100, 590], [70, 500],
  ],
  "fractured-expanse": [
    [640, 380], [760, 350], [880, 380], [940, 460], [920, 550],
    [830, 610], [710, 620], [610, 560], [580, 470],
  ],
  solmyr: [
    [1200, 420], [1320, 390], [1450, 420], [1530, 500], [1540, 600],
    [1470, 680], [1350, 700], [1240, 650], [1180, 550], [1170, 470],
  ],
  elderwyth: [
    [800, 660], [910, 630], [1020, 660], [1090, 730], [1070, 800],
    [980, 840], [860, 830], [780, 770], [760, 710],
  ],
};

// Fracture cracks radiating from the Fractured Expanse — purely decorative,
// signature "erasure" visual language.
export const crackLines: Point[][] = [
  [[780, 480], [660, 400], [560, 340]],
  [[840, 460], [960, 380], [1080, 330]],
  [[820, 540], [880, 640], [900, 760]],
  [[720, 520], [600, 580], [480, 610]],
  [[860, 500], [990, 520], [1130, 500]],
];

// Void Seas band + Absolute Void fade, along the bottom of the map.
export const voidSeasPath: Point[] = [
  [0, 800], [200, 780], [400, 810], [600, 790], [800, 815],
  [1000, 795], [1200, 815], [1400, 790], [1600, 805],
  [1600, 900], [0, 900],
];
