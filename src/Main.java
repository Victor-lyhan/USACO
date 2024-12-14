import java.io.*;
import java.util.*;

public class Main {
    static int N, Q;
    static int[] out; // out[u] = v or -1 if none (still '?')
    static boolean[] isFixed; // whether a cell has a fixed conveyor
    // Directions: L=(0,-1), R=(0,1), U=(-1,0), D=(1,0)

    static int dr(char d) {
        if (d == 'U') return -1;
        if (d == 'D') return 1;
        return 0;
    }

    static int dc(char d) {
        if (d == 'L') return -1;
        if (d == 'R') return 1;
        return 0;
    }

    static int idx(int r, int c) {
        return (r - 1) * N + (c - 1);
    }

    // After each day, we will:
    // 1. Build a reverse graph of the current fixed edges.
    // 2. Find all escapable nodes (nodes that can lead out or are '?').
    // 3. Reverse BFS/DFS from escapable nodes to find all escapable nodes.
    // 4. Count how many are not escapable -> these are unusable.
    static int computeUnusable() {
        int total = N * N;

        // Build reverse graph of currently fixed edges
        // We'll have at most one outgoing edge per fixed node
        // Reverse adjacency list
        ArrayList<Integer>[] rev = new ArrayList[total];
        for (int i = 0; i < total; i++) {
            rev[i] = new ArrayList<>();
        }

        // Identify nodes with fixed edges and nodes leading outside
        // out[u] = v if inside grid, or -2 if leads outside,
        // or -1 if no fixed edge ('?' cell)
        // Wait, currently we used -1 to mean no edge. Let's use -2 for outside.
        // We'll adapt logic: if inside grid v = idx(nr,nc), if outside v = -2,
        // if '?' then out[u] = -1.
        // However, the code currently never sets -2. Let's fix that logic now.
        // If an edge leads outside the grid, we set out[u] = -2.

        // We'll first fix the assignment in code after reading and placing edges.

        // Create the reverse graph
        for (int u = 0; u < total; u++) {
            if (out[u] >= 0) {
                rev[out[u]].add(u);
            }
        }

        // Escapable nodes:
        // - Those with out[u] = -1 (no fixed edge, so '?', can lead out)
        // - Those with out[u] = -2 (fixed edge leading outside the grid)
        // We start from these nodes and mark them escapable
        boolean[] escapable = new boolean[total];
        Deque<Integer> dq = new ArrayDeque<>();

        for (int u = 0; u < total; u++) {
            if (out[u] == -1 || out[u] == -2) {
                escapable[u] = true;
                dq.add(u);
            }
        }

        // Reverse BFS to mark all nodes that can reach an escapable node
        while (!dq.isEmpty()) {
            int cur = dq.poll();
            for (int p : rev[cur]) {
                if (!escapable[p]) {
                    escapable[p] = true;
                    dq.add(p);
                }
            }
        }

        // Count unusable: nodes that are not escapable
        int unusable = 0;
        for (int u = 0; u < total; u++) {
            if (!escapable[u]) unusable++;
        }

        return unusable;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        // Initialize all cells as '?'
        int total = N * N;
        out = new int[total];
        Arrays.fill(out, -1); // -1 means '?'
        isFixed = new boolean[total];

        int[] rr = new int[Q];
        int[] cc = new int[Q];
        char[] dd = new char[Q];

        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            rr[i] = Integer.parseInt(st.nextToken());
            cc[i] = Integer.parseInt(st.nextToken());
            dd[i] = st.nextToken().charAt(0);
        }

        // Process after all input read
        // We'll store results after each day
        int[] results = new int[Q];

        for (int i = 0; i < Q; i++) {
            int r = rr[i], c = cc[i];
            char d = dd[i];
            int u = idx(r, c);
            isFixed[u] = true;

            int nr = r + dr(d);
            int nc = c + dc(d);
            if (nr < 1 || nr > N || nc < 1 || nc > N) {
                // leads outside
                out[u] = -2;
            } else {
                out[u] = idx(nr, nc);
            }

            // After each day's placement, compute unusable cells
            results[i] = computeUnusable();
        }

        // Print all results at the end
        for (int i = 0; i < Q; i++) {
            System.out.println(results[i]);
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