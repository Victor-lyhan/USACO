package dream;


// Bessie's dream: http://www.usaco.org/index.php?page=viewproblem2&cpid=575
/*
Run BFS on the maze, keeping in mind the constraints of the different tiles

The first "path" that hits the end will be the shortest path

*/


import java.util.*;
import java.io.*;

public class dream {
    static Cell[][] maze;
    static int n, m;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new File("dream.in"));
        n = in.nextInt();
        m = in.nextInt();

        maze = new Cell[n][m];
        for(int r = 0; r < n; r++) {
            for(int c = 0; c < m; c++) {
                maze[r][c] = new Cell(in.nextInt());
            }
        }
        in.close();
        Deque<Step> toTake = new ArrayDeque<>(8000000);
        toTake.add(new Step(0, 0, false, 0, 0));


        int result = -1;

        while(toTake.size() > 0) {
            Step s = toTake.remove();
            if(s.oob() || s.blocked() || s.visited()) continue;

            if(s.last()) {
                result = s.steps;
                break;
            }

            s.visit();
            s.queueNexts(toTake);
        }


        PrintWriter out = new PrintWriter(new File("dream.out"));
        System.out.println(result);
        out.println(result);
        out.close();
    }

    static class Cell {
        int type;
        int[] visited;

        Cell(Scanner in) {
            this(in.nextInt());
        }

        Cell(int i) {
            type = i;

            if(type == 1) visited = new int[2];
            if(type == 2 || type == 3) visited = new int[1];
            if(type == 4) visited = new int[4];

        }
    }

    static class Step {
        int r, c;
        int dir;
        int steps;
        boolean orange;

        Step(int rr, int cc, boolean oo, int dd, int ss) {
            r = rr;
            c = cc;
            orange = oo;
            dir = dd;
            steps = ss;
        }

        boolean last() {
            return r == n - 1 && c == m - 1;
        }

        boolean oob() {
            return r < 0 || c < 0 || r >= n || c >= m;
        }

        boolean blocked() {
            return maze[r][c].type == 0 || maze[r][c].type == 3 && !orange;
        }

        boolean visited() {
            switch(maze[r][c].type) {
                case 1:
                    return maze[r][c].visited[orange ? 1 : 0] > 0;
                case 2:
                case 3:
                    return maze[r][c].visited[0] > 0;
                case 4:
                    return maze[r][c].visited[dir] > 0;
                default:
                    return false;
            }
        }

        void visit() {
            switch(maze[r][c].type) {
                case 1:
                    maze[r][c].visited[orange ? 1 : 0] = steps;
                    break;
                case 2:
                case 3:
                    maze[r][c].visited[0] = steps;
                    break;
                case 4:
                    maze[r][c].visited[dir] = steps;
            }

        }

        static int[] rCh = {1, -1, 0, 0};
        static int[] cCh = {0, 0, 1, -1};

        void queueNexts(Deque<Step> q) {
            Cell x = maze[r][c];
            if(x.type == 4) {
                Step slide = new Step(r + rCh[dir], c + cCh[dir], false, dir, steps + 1);
                if(slide.oob() || slide.blocked()) {
                    for(int d = 1; d <= 3; d++) {
                        int dir2 = (dir + d) % 4;
                        q.add(new Step(r + rCh[dir2], c + cCh[dir2], false, dir2, steps + 1));
                    }
                }
                else {
                    q.add(slide);
                }
            }
            else {
                boolean o2 = x.type == 2 ? true : orange;
                for(int d = 0; d <= 3; d++) {
                    q.add(new Step(r + rCh[d], c + cCh[d], o2, d, steps + 1));
                }
            }
        }
    }

}