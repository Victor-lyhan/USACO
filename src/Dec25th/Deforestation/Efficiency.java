package Dec25th.Deforestation;

import java.io.*;
import java.util.*;

public class Efficiency {
    static class FenwickTree {
        int size;
        int[] tree;
        FenwickTree(int n) {
            size = n;
            tree = new int[n + 1];
        }
        void increment(int idx, int value) {
            for (; idx <= size; idx += idx & -idx) {
                tree[idx] += value;
            }
        }
        int prefixSum(int idx) {
            int sum = 0;
            for (; idx > 0; idx -= idx & -idx) {
                sum += tree[idx];
            }
            return sum;
        }
        int rangeSum(int start, int end) {
            return prefixSum(end) - prefixSum(start - 1);
        }
    }

    static class Range implements Comparable<Range> {
        long start, end;
        int quota;
        Range(long s, long e, int q) {
            start = s;
            end = e;
            quota = q;
        }
        public int compareTo(Range other) {
            if (end == other.end) return Long.compare(start, other.start);
            return Long.compare(end, other.end);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(new BufferedOutputStream(System.out));

        int testCases = Integer.parseInt(reader.readLine());
        for (int t = 0; t < testCases; t++) {
            StringTokenizer input = new StringTokenizer(reader.readLine());
            int numPoints = Integer.parseInt(input.nextToken());
            int numRanges = Integer.parseInt(input.nextToken());

            long[] points = new long[numPoints];
            input = new StringTokenizer(reader.readLine());
            for (int i = 0; i < numPoints; i++) {
                points[i] = Long.parseLong(input.nextToken());
            }
            Arrays.sort(points);

            Range[] ranges = new Range[numRanges];
            for (int i = 0; i < numRanges; i++) {
                input = new StringTokenizer(reader.readLine());
                long left = Long.parseLong(input.nextToken());
                long right = Long.parseLong(input.nextToken());
                int required = Integer.parseInt(input.nextToken());
                ranges[i] = new Range(left, right, required);
            }
            Arrays.sort(ranges);

            FenwickTree tracker = new FenwickTree(numPoints);
            boolean[] used = new boolean[numPoints];

            for (Range range : ranges) {
                int leftIdx = lowerBound(points, range.start);
                int rightIdx = upperBound(points, range.end) - 1;

                if (leftIdx > rightIdx || leftIdx < 0 || rightIdx < 0 || leftIdx >= numPoints || rightIdx >= numPoints) {
                    continue;
                }

                int selectedCount = tracker.rangeSum(leftIdx + 1, rightIdx + 1);
                int needed = range.quota - selectedCount;

                if (needed > 0) {
                    int pos = rightIdx;
                    while (needed > 0) {
                        while (pos >= leftIdx && used[pos]) {
                            pos--;
                        }
                        if (pos < leftIdx) break;

                        used[pos] = true;
                        tracker.increment(pos + 1, 1);
                        needed--;
                        pos--;
                    }
                }
            }

            writer.println(numPoints - tracker.prefixSum(numPoints));
        }

        writer.flush();
        writer.close();
    }

    static int lowerBound(long[] array, long key) {
        int low = 0, high = array.length;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (array[mid] >= key) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    static int upperBound(long[] array, long key) {
        int low = 0, high = array.length;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (array[mid] <= key) low = mid + 1;
            else high = mid;
        }
        return low;
    }
}
