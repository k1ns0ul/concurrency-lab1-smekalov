package lab1;

public record Snapshot(
    long[] buckets,
    long count,
    long sum,
    long min,
    long max,
    long p50,
    long p99
) {
    public static final int BUCKET_COUNT = 256;
    public static final int BUCKET_WIDTH_MS = 4;

    public static Snapshot of(long[] ownedBuckets, long count, long sum, long min, long max) {
        long p50 = percentile(ownedBuckets, count, 0.50);
        long p99 = percentile(ownedBuckets, count, 0.99);
        return new Snapshot(ownedBuckets, count, sum, min, max, p50, p99);
    }

    static long percentile(long[] buckets, long count, double fraction) {
        double threshold = count * fraction;
        long accumulated = 0;
        for (int i = 0; i < BUCKET_COUNT; i++) {
            accumulated += buckets[i];
            if (accumulated >= threshold) {
                return (long) i * BUCKET_WIDTH_MS;
            }
        }
        return (long) (BUCKET_COUNT - 1) * BUCKET_WIDTH_MS;
    }
}
