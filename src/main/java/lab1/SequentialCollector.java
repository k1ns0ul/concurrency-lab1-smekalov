package lab1;

import java.util.Arrays;

public final class SequentialCollector implements MetricsCollector {
    private final long[] buckets = new long[Snapshot.BUCKET_COUNT];
    private long count;
    private long sum;
    private long min = Long.MAX_VALUE;
    private long max;

    @Override
    public void record(long value) {
        int bucket = (int) Math.min(value / Snapshot.BUCKET_WIDTH_MS, Snapshot.BUCKET_COUNT - 1);
        buckets[bucket]++;
        count++;
        sum += value;
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    @Override
    public Snapshot snapshot() {
        return Snapshot.of(Arrays.copyOf(buckets, buckets.length), count, sum, min, max);
    }
}
