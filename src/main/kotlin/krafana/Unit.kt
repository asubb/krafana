package krafana

/**
 * [Source](https://github.com/grafana/grafana/blob/main/packages/grafana-data/src/valueFormats/categories.ts)
 */
enum class Measure {
    none,
    percent,
    bytes,
    /**
     * { name: 'packets/sec', id: 'pps', fn: SIPrefix('p/s') },
     */
    pps,
    rps,
    binbps,
    Bps,
    /**
     * { name: 'requests/min (rpm)', id: 'reqpm', fn: simpleCountUnit('req/m') },
      */
    reqpm,
    s,
    ns,
    µs,
    ms,
}