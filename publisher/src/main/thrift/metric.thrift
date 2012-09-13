namespace java org.apache.gigo.types
enum Origin
{
  PUBLISHER=1,
  CONSUMER=2,
}

struct Metric {
  1: Origin origin,
  2: string source,
  3: string emitter,
  4: string bucket,
  5: i64 timeStamp,
  6: i64 updateTime,
  7: map<string, double> counters,
}
