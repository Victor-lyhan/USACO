package BessieInterview;

import java.io.*;
import java.util.*;

public class BessieInterview {
    static class Farmer implements Comparable<Farmer> {
        long finishTime;
        int id;
        Farmer(long finishTime, int id) {
            this.finishTime = finishTime;
            this.id = id;
        }
        public int compareTo(Farmer o) {
            if (this.finishTime != o.finishTime) return Long.compare(this.finishTime, o.finishTime);
            return Integer.compare(this.id, o.id);
        }
    }

    static int[] parent;
    static int[] size;

    static int find(int x) {
        while (x != parent[x]) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    static void union(int a, int b) {
        a = find(a);
        b = find(b);
        if (a != b) {
            if (size[a] < size[b]) {
                int tmp = a; a=b; b=tmp;
            }
            parent[b] = a;
            size[a] += size[b];
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int N = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());

        long[] t = new long[N+1];
        st = new StringTokenizer(br.readLine());
        for (int i = 1; i <= N; i++) {
            t[i] = Long.parseLong(st.nextToken());
        }

        parent = new int[K+1];
        size = new int[K+1];
        for (int i = 1; i <= K; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        PriorityQueue<Farmer> pq = new PriorityQueue<>();
        int nextCow = 1;
        int limit = Math.min(K, N);
        for (int i = 1; i <= limit; i++) {
            pq.add(new Farmer(t[i], i));
            nextCow++;
        }

        while (nextCow <= N) {
            Farmer first = pq.poll();
            long currentFinish = first.finishTime;

            int capacity = 10;
            Farmer[] tieFarmers = new Farmer[capacity];
            int tieCount = 0;
            tieFarmers[tieCount++] = first;

            while (!pq.isEmpty() && pq.peek().finishTime == currentFinish) {
                if (tieCount == capacity) {
                    capacity <<= 1;
                    tieFarmers = Arrays.copyOf(tieFarmers, capacity);
                }
                tieFarmers[tieCount++] = pq.poll();
            }

            if (tieCount > 1) {
                int baseId = tieFarmers[0].id;
                for (int i = 1; i < tieCount; i++) {
                    union(baseId, tieFarmers[i].id);
                }
            }

            Arrays.sort(tieFarmers, 0, tieCount, Comparator.comparingInt(f -> f.id));

            int cowsToAssign = Math.min(tieCount, N - nextCow + 1);
            for (int i = 0; i < cowsToAssign; i++) {
                Farmer f = tieFarmers[i];
                f.finishTime = currentFinish + t[nextCow++];
                pq.add(f);
            }
        }

        long minFinish = Long.MAX_VALUE;
        int capacity = 10;
        int minFinishCount = 0;
        int[] minFinishFarmers = new int[capacity];

        while (!pq.isEmpty()) {
            Farmer f = pq.poll();
            if (f.finishTime < minFinish) {
                minFinish = f.finishTime;
                minFinishCount = 0;
                minFinishFarmers[minFinishCount++] = f.id;
            } else if (f.finishTime == minFinish) {
                if (minFinishCount == capacity) {
                    capacity <<= 1;
                    minFinishFarmers = Arrays.copyOf(minFinishFarmers, capacity);
                }
                minFinishFarmers[minFinishCount++] = f.id;
            }
        }

        HashSet<Integer> possibleRoots = new HashSet<>();
        for (int i = 0; i < minFinishCount; i++) {
            possibleRoots.add(find(minFinishFarmers[i]));
        }

        boolean[] canInterview = new boolean[K+1];
        for (int i = 1; i <= K; i++) {
            if (possibleRoots.contains(find(i))) {
                canInterview[i] = true;
            }
        }

        System.out.println(minFinish);
        StringBuilder sb = new StringBuilder(K);
        for (int i = 1; i <= K; i++) {
            sb.append(canInterview[i] ? '1' : '0');
        }
        System.out.println(sb.toString());
    }
}

