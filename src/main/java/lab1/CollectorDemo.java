package lab1;

import java.util.Arrays;
import java.util.StringJoiner;

public final class CollectorDemo {
    private static int passed;

    public static void main(String[] args) {
        handComputedPercentiles();
        thresholdReachedExactlyAtBoundary();
        largeValuesGoToLastBucket();
        emptyCollector();
        snapshotIsIsolatedFromCollector();
        laterSnapshotDoesNotTouchEarlierOne();
        System.out.println();
        System.out.println("Все проверки пройдены: " + passed);
    }

    private static void handComputedPercentiles() {
        section("1. Перцентили на 10 значениях");
        long[] values = {0, 1, 2, 3, 5, 6, 10, 20, 500, 2000};
        MetricsCollector collector = new SequentialCollector();
        for (long v : values) {
            collector.record(v);
        }
        Snapshot s = collector.snapshot();
        System.out.println("значения: " + Arrays.toString(values));
        print(s);
        check("count", s.count(), 10);
        check("sum", s.sum(), 2547);
        check("min", s.min(), 0);
        check("max", s.max(), 2000);
        check("корзина 0 (0..3)", s.buckets()[0], 4);
        check("корзина 1 (4..7)", s.buckets()[1], 2);
        check("корзина 2 (8..11)", s.buckets()[2], 1);
        check("корзина 5 (20..23)", s.buckets()[5], 1);
        check("корзина 125 (500..503)", s.buckets()[125], 1);
        check("корзина 255 (2000)", s.buckets()[255], 1);
        check("p50: порог 5.0, накоплено 4 -> 6 на корзине 1", s.p50(), 4);
        check("p99: порог 9.9, накоплено 10 только на корзине 255", s.p99(), 1020);
    }

    private static void thresholdReachedExactlyAtBoundary() {
        section("2. Порог достигается ровно (накоплено == порог)");
        MetricsCollector collector = new SequentialCollector();
        for (int i = 0; i < 99; i++) {
            collector.record(8);
        }
        collector.record(1000);
        Snapshot s = collector.snapshot();
        System.out.println("значения: 99 x 8, 1 x 1000");
        print(s);
        check("p50: порог 50.0, на корзине 2 накоплено 99", s.p50(), 8);
        check("p99: порог 99.0, на корзине 2 накоплено 99 >= 99", s.p99(), 8);
        check("max", s.max(), 1000);
    }

    private static void largeValuesGoToLastBucket() {
        section("3. Значения >= 1024 попадают в корзину 255");
        long[] values = {1019, 1020, 1023, 1024, 5000, Long.MAX_VALUE / 2};
        MetricsCollector collector = new SequentialCollector();
        for (long v : values) {
            collector.record(v);
        }
        Snapshot s = collector.snapshot();
        System.out.println("значения: " + Arrays.toString(values));
        print(s);
        check("корзина 254 (1016..1019)", s.buckets()[254], 1);
        check("корзина 255 (1020..1023 и всё >= 1024)", s.buckets()[255], 5);
        check("сумма корзин == count", Arrays.stream(s.buckets()).sum(), s.count());
    }

    private static void emptyCollector() {
        section("4. Снимок пустого коллектора");
        Snapshot s = new SequentialCollector().snapshot();
        print(s);
        check("count", s.count(), 0);
        check("sum", s.sum(), 0);
        check("min == Long.MAX_VALUE (нейтральный элемент min)", s.min(), Long.MAX_VALUE);
        check("max == 0 (нейтральный элемент max для value >= 0)", s.max(), 0);
        check("p50: порог 0.0, 0 >= 0 на корзине 0", s.p50(), 0);
        check("p99: порог 0.0, 0 >= 0 на корзине 0", s.p99(), 0);
        check("длина buckets", s.buckets().length, Snapshot.BUCKET_COUNT);
    }

    private static void snapshotIsIsolatedFromCollector() {
        section("5. Изменение массива в снимке не трогает коллектор");
        MetricsCollector collector = new SequentialCollector();
        collector.record(1);
        collector.record(2);
        Snapshot s = collector.snapshot();
        s.buckets()[0] = 999;
        s.buckets()[100] = 777;
        Snapshot fresh = collector.snapshot();
        System.out.println("в полученном снимке записали buckets[0]=999, buckets[100]=777");
        print(fresh);
        check("новый снимок: корзина 0", fresh.buckets()[0], 2);
        check("новый снимок: корзина 100", fresh.buckets()[100], 0);
        check("массивы снимков — разные объекты", s.buckets() == fresh.buckets() ? 1 : 0, 0);
    }

    private static void laterSnapshotDoesNotTouchEarlierOne() {
        section("6. Второй снимок отличается, первый не изменился");
        MetricsCollector collector = new SequentialCollector();
        collector.record(4);
        collector.record(40);
        Snapshot first = collector.snapshot();
        long[] firstBucketsBefore = first.buckets().clone();
        collector.record(400);
        collector.record(4000);
        Snapshot second = collector.snapshot();
        System.out.print("первый:  ");
        print(first);
        System.out.print("второй:  ");
        print(second);
        check("первый: count", first.count(), 2);
        check("первый: max", first.max(), 40);
        check("первый: корзины совпадают с копией до record", Arrays.equals(first.buckets(), firstBucketsBefore) ? 1 : 0, 1);
        check("первый: корзина 100", first.buckets()[100], 0);
        check("второй: count", second.count(), 4);
        check("второй: max", second.max(), 4000);
        check("второй: корзина 100 (400..403)", second.buckets()[100], 1);
        check("второй: корзина 255", second.buckets()[255], 1);
        check("корзины снимков различаются", Arrays.equals(first.buckets(), second.buckets()) ? 1 : 0, 0);
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(title);
    }

    private static void print(Snapshot s) {
        StringJoiner nonEmpty = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < s.buckets().length; i++) {
            if (s.buckets()[i] != 0) {
                nonEmpty.add(i + ":" + s.buckets()[i]);
            }
        }
        System.out.printf("count=%d sum=%d min=%d max=%d p50=%d p99=%d buckets%s%n",
            s.count(), s.sum(), s.min(), s.max(), s.p50(), s.p99(), nonEmpty);
    }

    private static void check(String what, long actual, long expected) {
        if (actual != expected) {
            System.out.printf("  FAIL %s: ожидали %d, получили %d%n", what, expected, actual);
            System.exit(1);
        }
        passed++;
        System.out.printf("  OK   %s = %d%n", what, actual);
    }
}
