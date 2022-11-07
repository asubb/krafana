package krafana

/**
 * [Source](https://github.com/grafana/grafana/blob/main/packages/grafana-data/src/valueFormats/categories.ts)
 */
enum class Measure {
    none,
    percent,
    bytes,
    pps,
    rps,
    binbps,
    ns,
    µs,
    ms,
}