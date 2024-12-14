package Dec25th.Deforestation;

import java.io.*;
import java.util.*;

public class Main {
    static class Fenwick {
        int n;
        int[] fenw;
        Fenwick(int n) {
            this.n = n;
            fenw = new int[n+1];
        }
        void update(int i, int v) {
            for (; i <= n; i += i & (-i)) {
                fenw[i] += v;
            }
        }
        int query(int i) {
            int s = 0;
            for (; i > 0; i -= i & (-i)) {
                s += fenw[i];
            }
            return s;
        }
        int rangeQuery(int l, int r) {
            return query(r) - query(l-1);
        }
    }

    static class Interval implements Comparable<Interval> {
        long l, r;
        int t;
        Interval(long l, long r, int t) {
            this.l = l; this.r = r; this.t = t;
        }
        public int compareTo(Interval o) {
            if (this.r == o.r) return Long.compare(this.l, o.l);
            return Long.compare(this.r, o.r);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new BufferedOutputStream(System.out));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int T = Integer.parseInt(st.nextToken());
        for (int _case = 0; _case < T; _case++) {
            st = new StringTokenizer(br.readLine());
            int N = Integer.parseInt(st.nextToken());
            int K = Integer.parseInt(st.nextToken());

            long[] trees = new long[N];
            st = new StringTokenizer(br.readLine());
            for (int i = 0; i < N; i++) {
                trees[i] = Long.parseLong(st.nextToken());
            }
            Arrays.sort(trees);

            Interval[] intervals = new Interval[K];
            for (int i = 0; i < K; i++) {
                st = new StringTokenizer(br.readLine());
                long l = Long.parseLong(st.nextToken());
                long r = Long.parseLong(st.nextToken());
                int t = Integer.parseInt(st.nextToken());
                intervals[i] = new Interval(l, r, t);
            }

            Arrays.sort(intervals);

            Fenwick fenw = new Fenwick(N);

            boolean[] chosen = new boolean[N];

            for (Interval in : intervals) {
                int L = lowerBound(trees, in.l);
                int R = upperBound(trees, in.r) - 1;

                if (L > R || L < 0 || R < 0 || L >= N || R >= N) {
                    if (in.t > 0) {
                    }
                    continue;
                }

                int chosenCount = fenw.rangeQuery(L+1, R+1);
                int need = in.t - chosenCount;
                if (need > 0) {
                    int idx = R;
                    while (need > 0) {
                        while (idx >= L && chosen[idx]) {
                            idx--;
                        }
                        if (idx < L) {
                            break;
                        }
                        chosen[idx] = true;
                        fenw.update(idx+1, 1);
                        need--;
                        idx--;
                    }
                }
            }

            int chosenCount = fenw.query(N);
            out.println(N - chosenCount);
        }

        out.flush();
        out.close();
    }

    static int lowerBound(long[] arr, long key) {
        int low = 0, high = arr.length;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (arr[mid] >= key) {
                high = mid;
            } else {
                low = mid+1;
            }
        }
        return low;
    }

    static int upperBound(long[] arr, long key) {
        int low = 0, high = arr.length;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (arr[mid] <= key) {
                low = mid+1;
            } else {
                high = mid;
            }
        }
        return low;
    }
}
