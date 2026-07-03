const SENSITIVE_PATTERNS = [
  ".db", ".sqlite", ".sqlite3", ".mv.db", ".h2.db",
  ".jar", ".zip",
  "token", "secret", "credential", "password", "keystore",
  "playerdata", "backups", "backup", "database",
  "accounts", "characters", "players",
  "resource pack", "resource-pack", "resourcepack",
  "cache", ".paper-remapped", "libs"
];

function text(status, message) {
  return new Response(message, {
    status,
    headers: {
      "content-type": "text/plain; charset=utf-8",
      "cache-control": "public, max-age=60, s-maxage=60"
    }
  });
}

function contentTypeForKey(key) {
  const lower = key.toLowerCase();

  if (lower.endsWith(".json")) return "application/json; charset=utf-8";
  if (lower.endsWith(".yml") || lower.endsWith(".yaml")) return "text/yaml; charset=utf-8";
  if (lower.endsWith(".txt") || lower.endsWith(".log") || lower.endsWith(".md")) return "text/plain; charset=utf-8";
  if (lower.endsWith(".toml") || lower.endsWith(".properties") || lower.endsWith(".conf")) return "text/plain; charset=utf-8";
  if (lower.endsWith(".bbmodel")) return "application/json; charset=utf-8";

  return "application/octet-stream";
}

function isSensitivePath(key) {
  const lower = key.toLowerCase();
  return SENSITIVE_PATTERNS.some((pattern) => lower.includes(pattern));
}

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    if (request.method !== "GET" && request.method !== "HEAD") {
      return new Response("Method not allowed", {
        status: 405,
        headers: {
          "allow": "GET, HEAD",
          "cache-control": "public, max-age=60, s-maxage=60"
        }
      });
    }

    const key = url.pathname.replace(/^\/+/, "");
    if (!key) return text(403, "Forbidden");

    const requiredPrefix = (env.SNAPSHOT_PREFIX || "").replace(/^\/+/, "").replace(/\/+$/, "") + "/";

    if (!key.startsWith(requiredPrefix)) {
      return text(403, "Path outside allowed snapshot prefix");
    }

    if (isSensitivePath(key)) {
      return text(403, "Sensitive-looking path blocked");
    }

    const cache = caches.default;
    const cacheKey = new Request(url.toString(), request);

    const cached = await cache.match(cacheKey);
    if (cached) return cached;

    const object = await env.SNAPSHOTS.get(key, {
      onlyIf: request.headers,
      range: request.headers
    });

    if (object === null) {
      return text(404, "Not found");
    }

    const headers = new Headers();
    object.writeHttpMetadata(headers);
    headers.set("etag", object.httpEtag);
    headers.set("content-type", headers.get("content-type") || contentTypeForKey(key));
    headers.set("cache-control", "public, max-age=60, s-maxage=60");
    headers.set("x-aereth-snapshot-gateway", "true");

    const response = request.method === "HEAD"
      ? new Response(null, { headers })
      : new Response(object.body, { headers });

    ctx.waitUntil(cache.put(cacheKey, response.clone()));

    return response;
  }
};
