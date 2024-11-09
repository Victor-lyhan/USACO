package BFS;

import java.util.*;
import java.io.*;

// Bessie's dream: http://www.usaco.org/index.php?page=viewproblem2&cpid=575
/*
Run BFS on the maze, keeping in mind the constraints of the different tiles

The first "path" that hits the end will be the shortest path

*/


public class BessirDream {
    static Cell[][] maze;
    static int n, m;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new File("BessirDream.in"));
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


        PrintWriter out = new PrintWriter(new File("BessirDream.out"));
        System.out.println(result);
        out.println(result);
        out.close();
    }

    static class Cell {
        int type;
        int[] visited;
        /*
        0 = red
        1 = pink
        2 = orange
        3 = blue
        4 = purple

        pink: 2 dimensions (orange or no orange)

        orange and blue: 1 dimension (guaranteed to smell like oranges)

        purple: 4 dimension (4 directions, lose orange smell)

        */
        Cell(Scanner in) {
            this(in.nextInt());
        }
        Cell(int i) {
            type = i;

            if(type == 1) visited = new int[2];
            if(type == 2 || type == 3) visited = new int[1];
            if(type == 4) visited = new int[4];

            // types 0s can't be visited
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
            /*
            Ternary operator:
            int orangeInt = orange ? 1 : 0;

            int orangeInt;
            if(orange) {
                orangeInt = 1;
            }
            else {
                orangeInt = 0;
            }

            */


            switch(maze[r][c].type) {
                case 1:
                    maze[r][c].visited[orange ? 1 : 0] = steps;
                    break;
                case 2:
                case 3:
                    maze[r][c].visited[0] = steps;
                case 4:
                    maze[r][c].visited[dir] = steps;
            }

            /*
            Equivalent if-statement logic:

            if(maze[r][c].type == 1) {
                maze[r][c].visited[orange ? 1 : 0] = steps;
            }
            else if(maze[r][c].type == 2 || maze[r][c].type == 3) {
                maze[r][c].visited[0] = steps;
            }
            else if(maze[r][c].type == 4) {
                maze[r][c].visited[dir] = steps;
            }

            */

            /*
            int x = 10;
            switch(x) {
            case 1:
                System.out.println("x is equal to 1");
                break;
            case 2:
                System.out.println("x is equal to 2");
                break;
            case 10:
                System.out.println("x is equal to 10");
                break;
            default:
                System.out.println("x is some number");
                break;
            }

            if(x == 1) {
                System.out.println("x is equal to 1");
            }
            else if(x == 2) {
                System.out.println("x is equal to 2");
            }
            else if(x == 10) {
                System.out.println("x is equal to 10");
            }
            else {
                System.out.println("x is some number");
            }

            */
        }

        static int[] rCh = {1, -1, 0, 0};
        static int[] cCh = {0, 0, 1, -1};

        void queueNexts(Deque<Step> q) {
            Cell x = maze[r][c];
            if(x.type == 4) {
                Step slide = new Step(r + rCh[dir], c + cCh[dir], false, dir, steps + 1);
                if(slide.oob() || slide.blocked()) {
                    // sliding has been blocked! we can go
                    // any of the other 3 directions instead
                    for(int d = 1; d <= 3; d++) {
                        int dir2 = (dir + d) % 4;
                        q.add(new Step(r + rCh[dir2], c + cCh[dir2], false, dir2, steps + 1));
                    }
                }
                else {
                    q.add(slide);
                }
            }
            else { // NOT sliding - can go any direction
                boolean o2 = x.type == 2 ? true : orange;

                for(int d = 0; d <= 3; d++) {
                    q.add(new Step(r + rCh[d], c + cCh[d], o2, d, steps + 1));
                }
            }
        }
    }

}
