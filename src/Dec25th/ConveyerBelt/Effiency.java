package Dec25th.ConveyerBelt;

import java.io.*;
import java.util.*;

public class Effiency {
    static int gridSize, queries;
    static int[] queryRow, queryCol;
    static char[] queryDir;

    static int computeIndex(int row, int col) {
        return row * gridSize + col;
    }

    static int[] rowOffset = {-1, 0, 1, 0};
    static int[] colOffset = {0, 1, 0, -1};
    static char[] directionChars = {'U', 'R', 'D', 'L'};

    static char[][] board;

    static boolean[] reachable;
    static int reachableCells;

    static Deque<Integer> queue = new ArrayDeque<>();

    static int[] fixedDirection;
    static boolean[] isFixed;
    static int[] incomingReachCount;

    static int[] result;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        gridSize = Integer.parseInt(tokenizer.nextToken());
        queries = Integer.parseInt(tokenizer.nextToken());

        queryRow = new int[queries];
        queryCol = new int[queries];
        queryDir = new char[queries];

        for (int i = 0; i < queries; i++) {
            tokenizer = new StringTokenizer(reader.readLine());
            queryRow[i] = Integer.parseInt(tokenizer.nextToken()) - 1;
            queryCol[i] = Integer.parseInt(tokenizer.nextToken()) - 1;
            queryDir[i] = tokenizer.nextToken().charAt(0);
        }

        board = new char[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            Arrays.fill(board[i], '?');
        }

        for (int i = 0; i < queries; i++) {
            board[queryRow[i]][queryCol[i]] = queryDir[i];
        }

        int totalCells = gridSize * gridSize;
        reachable = new boolean[totalCells];
        isFixed = new boolean[totalCells];
        fixedDirection = new int[totalCells];
        incomingReachCount = new int[totalCells];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int cellIndex = computeIndex(row, col);
                char direction = board[row][col];
                if (direction == '?') {
                    isFixed[cellIndex] = false;
                } else {
                    isFixed[cellIndex] = true;
                    for (int dir = 0; dir < 4; dir++) {
                        if (directionChars[dir] == direction) {
                            fixedDirection[cellIndex] = dir;
                            break;
                        }
                    }
                }
            }
        }

        Arrays.fill(reachable, false);
        Arrays.fill(incomingReachCount, 0);
        reachableCells = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int cellIndex = computeIndex(row, col);
                if (isFixed[cellIndex]) {
                    int dir = fixedDirection[cellIndex];
                    int newRow = row + rowOffset[dir];
                    int newCol = col + colOffset[dir];
                    if (!isInBounds(newRow, newCol)) {
                        markReachable(cellIndex);
                    }
                } else {
                    for (int dir = 0; dir < 4; dir++) {
                        int newRow = row + rowOffset[dir];
                        int newCol = col + colOffset[dir];
                        if (!isInBounds(newRow, newCol)) {
                            markReachable(cellIndex);
                            break;
                        }
                    }
                }
            }
        }

        processReachableQueue();

        result = new int[queries];
        result[queries - 1] = totalCells - reachableCells;

        for (int i = queries - 1; i > 0; i--) {
            int row = queryRow[i], col = queryCol[i];
            int cellIndex = computeIndex(row, col);

            if (isFixed[cellIndex]) {
                isFixed[cellIndex] = false;
                incomingReachCount[cellIndex] = 0;

                boolean touchesBoundary = false;
                for (int dir = 0; dir < 4; dir++) {
                    int newRow = row + rowOffset[dir];
                    int newCol = col + colOffset[dir];
                    if (!isInBounds(newRow, newCol)) {
                        touchesBoundary = true;
                        break;
                    }
                }

                if (touchesBoundary) {
                    markReachable(cellIndex);
                } else {
                    int count = 0;
                    for (int dir = 0; dir < 4; dir++) {
                        int newRow = row + rowOffset[dir];
                        int newCol = col + colOffset[dir];
                        if (!isInBounds(newRow, newCol)) continue;
                        int neighborIndex = computeIndex(newRow, newCol);
                        if (reachable[neighborIndex]) count++;
                    }
                    if (count > 0) {
                        incomingReachCount[cellIndex] = count;
                        markReachable(cellIndex);
                    }
                }

                processReachableQueue();
            }

            result[i - 1] = totalCells - reachableCells;
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < queries; i++) {
            output.append(result[i]).append('\n');
        }
        System.out.print(output);
    }

    static boolean isInBounds(int row, int col) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }

    static void markReachable(int cellIndex) {
        if (!reachable[cellIndex]) {
            reachable[cellIndex] = true;
            reachableCells++;
            queue.addLast(cellIndex);
        }
    }

    static void processReachableQueue() {
        while (!queue.isEmpty()) {
            int currentIndex = queue.pollFirst();
            int row = currentIndex / gridSize;
            int col = currentIndex % gridSize;
            for (int dir = 0; dir < 4; dir++) {
                int neighborRow = row - rowOffset[dir];
                int neighborCol = col - colOffset[dir];
                if (!isInBounds(neighborRow, neighborCol)) continue;
                int neighborIndex = computeIndex(neighborRow, neighborCol);
                if (reachable[neighborIndex]) continue;

                if (isFixed[neighborIndex]) {
                    int fixedDir = fixedDirection[neighborIndex];
                    int targetRow = neighborRow + rowOffset[fixedDir];
                    int targetCol = neighborCol + colOffset[fixedDir];
                    if (targetRow == row && targetCol == col) {
                        markReachable(neighborIndex);
                    }
                } else {
                    if (incomingReachCount[neighborIndex] == 0) {
                        markReachable(neighborIndex);
                    }
                    incomingReachCount[neighborIndex]++;
                }
            }
        }
    }
}
