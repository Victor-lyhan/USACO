package Dec25th.ConveyerBelt;

import java.io.*;
import java.util.*;

public class Faster {
    static int N, Q;
    static int[] rr, cc;
    static char[] dd;

    static int idx(int r, int c) {
        return r * N + c;
    }

    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};
    static char[] DIR = {'U', 'R', 'D', 'L'};

    static char[][] grid;

    static boolean[] reversedReachable;
    static int reachableCount;

    static Deque<Integer> dq = new ArrayDeque<>();

    static int[] fixedDirIndex;
    static boolean[] fixed;
    static int[] inReachCount;

    static int[] ans;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        rr = new int[Q];
        cc = new int[Q];
        dd = new char[Q];

        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            rr[i] = Integer.parseInt(st.nextToken()) - 1;
            cc[i] = Integer.parseInt(st.nextToken()) - 1;
            dd[i] = st.nextToken().charAt(0);
        }

        grid = new char[N][N];
        for (int i = 0; i < N; i++) {
            Arrays.fill(grid[i], '?');
        }

        for (int i = 0; i < Q; i++) {
            grid[rr[i]][cc[i]] = dd[i];
        }

        int total = N * N;
        reversedReachable = new boolean[total];
        fixed = new boolean[total];
        fixedDirIndex = new int[total];
        inReachCount = new int[total];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                int u = idx(r, c);
                char ch = grid[r][c];
                if (ch == '?') {
                    fixed[u] = false;
                } else {
                    fixed[u] = true;
                    int d = 0;
                    for (; d < 4; d++) {
                        if (DIR[d] == ch) break;
                    }
                    fixedDirIndex[u] = d;
                }
            }
        }

        Arrays.fill(reversedReachable, false);
        Arrays.fill(inReachCount, 0);
        reachableCount = 0;

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                int u = idx(r, c);
                if (fixed[u]) {
                    int d = fixedDirIndex[u];
                    int nr = r + dr[d], nc = c + dc[d];
                    if (!inBounds(nr, nc)) {
                        makeReachable(u);
                    }
                } else {
                    for (int d = 0; d < 4; d++) {
                        int nr = r + dr[d], nc = c + dc[d];
                        if (!inBounds(nr, nc)) {
                            makeReachable(u);
                            break;
                        }
                    }
                }
            }
        }

        processQueue();

        ans = new int[Q];
        ans[Q - 1] = total - reachableCount;

        for (int i = Q - 1; i > 0; i--) {
            int r = rr[i], c2 = cc[i];
            int u = idx(r, c2);

            if (fixed[u]) {
                fixed[u] = false;
                inReachCount[u] = 0;

                boolean leadsOutside = false;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c2 + dc[d];
                    if (!inBounds(nr, nc)) {
                        leadsOutside = true;
                        break;
                    }
                }

                if (leadsOutside) {
                    makeReachable(u);
                } else {
                    int count = 0;
                    for (int d = 0; d < 4; d++) {
                        int nr = r + dr[d], nc = c2 + dc[d];
                        if (!inBounds(nr, nc)) continue;
                        int v = idx(nr, nc);
                        if (reversedReachable[v]) count++;
                    }
                    if (count > 0) {
                        inReachCount[u] = count;
                        makeReachable(u);
                    }
                }

                processQueue();
            }

            ans[i - 1] = total - reachableCount;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Q; i++) {
            sb.append(ans[i]).append('\n');
        }

        System.out.print(sb);
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N;
    }

    static void makeReachable(int u) {
        if (!reversedReachable[u]) {
            reversedReachable[u] = true;
            reachableCount++;
            dq.addLast(u);
        }
    }

    static void processQueue() {
        while (!dq.isEmpty()) {
            int u = dq.pollFirst();
            int r = u / N, c = u % N;
            for (int d = 0; d < 4; d++) {
                int vr = r - dr[d], vc = c - dc[d];
                if (!inBounds(vr, vc)) continue;
                int v = idx(vr, vc);
                if (reversedReachable[v]) continue;

                if (fixed[v]) {
                    int dv = fixedDirIndex[v];
                    int rr = vr + dr[dv], cc = vc + dc[dv];
                    if (rr == r && cc == c) {
                        makeReachable(v);
                    }
                } else {
                    if (inReachCount[v] == 0) {
                        makeReachable(v);
                    }
                    inReachCount[v]++;
                }
            }
        }
    }
}

/*
input 1:
3 5
1 1 R
3 3 L
3 2 D
1 2 L
2 1 U

output 1:
0
0
0
2
3

input 2:
3 8
1 1 R
1 2 L
1 3 D
2 3 U
3 3 L
3 2 R
3 1 U
2 1 D

output 2:
0
2
2
4
4
6
6
9


input 3:
4 13
2 2 R
2 3 R
2 4 D
3 4 D
4 4 L
4 3 L
4 2 U
3 1 D
4 1 R
2 1 L
1 1 D
1 4 L
1 3 D

output 3:
0
0
0
0
0
0
0
0
11
11
11
11
13

 */
