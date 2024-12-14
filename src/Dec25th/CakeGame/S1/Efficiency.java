package Dec25th.CakeGame.S1;

import java.io.*;
import java.util.*;

public class Efficiency {
    static class Link {
        long value;
        Link prev, next;
        Link(long v) { value = v; }
    }

    static Link start, end;
    static int iterations;
    static long total;

    static final int MAP_INIT_SIZE = 1 << 16;
    static long[] hashKeys;
    static long[] hashVals;
    static boolean[] hashUsed;
    static int hashCapacity;
    static int hashCount;

    static PrintWriter output;

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintWriter(new BufferedOutputStream(System.out));

        int scenarios = Integer.parseInt(input.readLine());
        StringBuilder results = new StringBuilder();

        while (scenarios-- > 0) {
            iterations = Integer.parseInt(input.readLine());
            start = null;
            end = null;
            total = 0;

            StringTokenizer tokenizer = new StringTokenizer(input.readLine());
            for (int i = 0; i < iterations; i++) {
                long value = Long.parseLong(tokenizer.nextToken());
                total += value;
                addNode(value);
            }

            initializeHashMap();
            long optimalElsie = calculate(iterations, 0, Long.MIN_VALUE, Long.MAX_VALUE);
            long optimalBessie = total - optimalElsie;
            results.append(optimalBessie).append(" ").append(optimalElsie).append("\n");
        }

        output.print(results.toString());
        output.flush();
    }

    static void addNode(long value) {
        Link newNode = new Link(value);
        if (start == null) {
            start = end = newNode;
        } else {
            end.next = newNode;
            newNode.prev = end;
            end = newNode;
        }
    }

    static long calculate(int length, int turn, long alpha, long beta) {
        if (length == 1) return 0;

        long stateHash = generateHash(length, turn);
        Long memoized = retrieveHash(stateHash);
        if (memoized != null) return memoized;

        long result;
        if (turn == 0) {
            result = Long.MAX_VALUE;
            Link current = start;
            for (int i = 0; i < length - 1; i++) {
                Link first = current;
                Link second = current.next;

                long val1 = first.value;
                long val2 = second.value;

                first.value += second.value;
                Link nextNode = second.next;
                if (nextNode != null) nextNode.prev = first;
                else end = first;
                first.next = nextNode;

                long opponentScore = calculate(length - 1, 1, alpha, beta);

                first.value = val1;
                if (nextNode != null) nextNode.prev = second;
                first.next = second;
                second.prev = first;
                second.next = nextNode;
                second.value = val2;
                if (nextNode == null) end = second;

                result = Math.min(result, opponentScore);
                beta = Math.min(beta, result);

                if (beta <= alpha) break;

                current = current.next;
            }
        } else {
            long leftScore = takeLeft(length, alpha, beta);
            if (alpha >= beta) {
                storeHash(stateHash, leftScore);
                return leftScore;
            }
            long rightScore = takeRight(length, alpha, beta);
            result = Math.max(leftScore, rightScore);
        }

        storeHash(stateHash, result);
        return result;
    }

    static long takeLeft(int length, long alpha, long beta) {
        Link oldStart = start;
        long leftVal = start.value;
        start = start.next;
        if (start != null) start.prev = null;

        long result = leftVal + calculate(length - 1, 0, alpha, beta);

        if (start != null) start.prev = oldStart;
        oldStart.next = start;
        start = oldStart;

        return result;
    }

    static long takeRight(int length, long alpha, long beta) {
        Link oldEnd = end;
        long rightVal = end.value;
        end = end.prev;
        if (end != null) end.next = null;

        long result = rightVal + calculate(length - 1, 0, alpha, beta);

        if (end != null) end.next = oldEnd;
        oldEnd.prev = end;
        end = oldEnd;

        return result;
    }

    static long generateHash(int length, int turn) {
        long hash = turn;
        Link current = start;
        while (current != null) {
            hash ^= (current.value + 0x9e3779b97f4a7c16L);
            hash *= 0x85ebca6bL;
            current = current.next;
        }
        return hash;
    }

    static void initializeHashMap() {
        hashCapacity = MAP_INIT_SIZE;
        hashKeys = new long[hashCapacity];
        hashVals = new long[hashCapacity];
        hashUsed = new boolean[hashCapacity];
        hashCount = 0;
    }

    static void storeHash(long key, long value) {
        if (hashCount * 2 >= hashCapacity) resizeHashMap();
        int index = (int)((key * 0x9e3779b97f4a7c16L) & (hashCapacity - 1));
        while (hashUsed[index]) {
            if (hashKeys[index] == key) {
                hashVals[index] = value;
                return;
            }
            index = (index + 1) & (hashCapacity - 1);
        }
        hashUsed[index] = true;
        hashKeys[index] = key;
        hashVals[index] = value;
        hashCount++;
    }

    static Long retrieveHash(long key) {
        int index = (int)((key * 0x9e3779b97f4a7c16L) & (hashCapacity - 1));
        while (hashUsed[index]) {
            if (hashKeys[index] == key) {
                return hashVals[index];
            }
            index = (index + 1) & (hashCapacity - 1);
        }
        return null;
    }

    static void resizeHashMap() {
        int oldCapacity = hashCapacity;
        long[] oldKeys = hashKeys;
        long[] oldVals = hashVals;
        boolean[] oldUsed = hashUsed;

        hashCapacity <<= 1;
        hashKeys = new long[hashCapacity];
        hashVals = new long[hashCapacity];
        hashUsed = new boolean[hashCapacity];
        hashCount = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldUsed[i]) {
                storeHash(oldKeys[i], oldVals[i]);
            }
        }
    }
}


/*
2
4
40 30 20 10
4
10 20 30 40
 */

