package Dec25th.CakeGame.S1;

import java.io.*;
import java.util.*;

public class Efficiency2 {
    static class Node {
        long data;
        Node prev, next;
        Node(long d) { data = d; }
    }

    static Node head, tail;
    static int numElements;
    static long totalSum;

    static final int INITIAL_SIZE = 1 << 16;
    static long[] mapKeys;
    static long[] mapValues;
    static boolean[] mapUsed;
    static int mapCapacity;
    static int mapCount;

    static PrintWriter writer;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        writer = new PrintWriter(new BufferedOutputStream(System.out));

        int testCases = Integer.parseInt(reader.readLine());
        StringBuilder results = new StringBuilder();

        while (testCases-- > 0) {
            numElements = Integer.parseInt(reader.readLine());
            head = null;
            tail = null;
            totalSum = 0;

            StringTokenizer tokens = new StringTokenizer(reader.readLine());
            for (int i = 0; i < numElements; i++) {
                long value = Long.parseLong(tokens.nextToken());
                totalSum += value;
                addNode(value);
            }

            initHashMap();
            long elsieBest = computeBest(numElements, 0, Long.MIN_VALUE, Long.MAX_VALUE);
            long bessieBest = totalSum - elsieBest;
            results.append(bessieBest).append(" ").append(elsieBest).append("\n");
        }

        writer.print(results.toString());
        writer.flush();
    }

    static void addNode(long value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    static long computeBest(int length, int turn, long alpha, long beta) {
        if (length == 1) return 0;

        long stateHash = calculateHash(length, turn);
        Long cachedValue = getHashValue(stateHash);
        if (cachedValue != null) return cachedValue;

        long bestScore;
        if (turn == 0) {
            bestScore = Long.MAX_VALUE;
            Node current = head;
            for (int i = 0; i < length - 1; i++) {
                Node first = current;
                Node second = current.next;

                long firstValue = first.data;
                long secondValue = second.data;

                first.data += second.data;
                Node nextNode = second.next;
                if (nextNode != null) nextNode.prev = first;
                else tail = first;
                first.next = nextNode;

                long result = computeBest(length - 1, 1, alpha, beta);

                first.data = firstValue;
                if (nextNode != null) nextNode.prev = second;
                first.next = second;
                second.prev = first;
                second.next = nextNode;
                second.data = secondValue;
                if (nextNode == null) tail = second;

                bestScore = Math.min(bestScore, result);
                beta = Math.min(beta, bestScore);

                if (beta <= alpha) break;

                current = current.next;
            }
        } else {
            long leftScore = evaluateLeft(length, alpha, beta);
            if (alpha >= beta) {
                storeHashValue(stateHash, leftScore);
                return leftScore;
            }
            long rightScore = evaluateRight(length, alpha, beta);
            bestScore = Math.max(leftScore, rightScore);
        }

        storeHashValue(stateHash, bestScore);
        return bestScore;
    }

    static long evaluateLeft(int length, long alpha, long beta) {
        Node originalHead = head;
        long leftValue = head.data;
        head = head.next;
        if (head != null) head.prev = null;

        long result = leftValue + computeBest(length - 1, 0, alpha, beta);

        if (head != null) head.prev = originalHead;
        originalHead.next = head;
        head = originalHead;

        return result;
    }

    static long evaluateRight(int length, long alpha, long beta) {
        Node originalTail = tail;
        long rightValue = tail.data;
        tail = tail.prev;
        if (tail != null) tail.next = null;

        long result = rightValue + computeBest(length - 1, 0, alpha, beta);

        if (tail != null) tail.next = originalTail;
        originalTail.prev = tail;
        tail = originalTail;

        return result;
    }

    static long calculateHash(int length, int turn) {
        long hash = turn;
        Node current = head;
        while (current != null) {
            hash ^= (current.data + 0x9e3779b97f4a7c16L);
            hash *= 0x85ebca6bL;
            current = current.next;
        }
        return hash;
    }

    static void initHashMap() {
        mapCapacity = INITIAL_SIZE;
        mapKeys = new long[mapCapacity];
        mapValues = new long[mapCapacity];
        mapUsed = new boolean[mapCapacity];
        mapCount = 0;
    }

    static void storeHashValue(long key, long value) {
        if (mapCount * 2 >= mapCapacity) resizeHashMap();
        int index = (int)((key * 0x9e3779b97f4a7c16L) & (mapCapacity - 1));
        while (mapUsed[index]) {
            if (mapKeys[index] == key) {
                mapValues[index] = value;
                return;
            }
            index = (index + 1) & (mapCapacity - 1);
        }
        mapUsed[index] = true;
        mapKeys[index] = key;
        mapValues[index] = value;
        mapCount++;
    }

    static Long getHashValue(long key) {
        int index = (int)((key * 0x9e3779b97f4a7c16L) & (mapCapacity - 1));
        while (mapUsed[index]) {
            if (mapKeys[index] == key) {
                return mapValues[index];
            }
            index = (index + 1) & (mapCapacity - 1);
        }
        return null;
    }

    static void resizeHashMap() {
        int oldCapacity = mapCapacity;
        long[] oldKeys = mapKeys;
        long[] oldValues = mapValues;
        boolean[] oldUsed = mapUsed;

        mapCapacity <<= 1;
        mapKeys = new long[mapCapacity];
        mapValues = new long[mapCapacity];
        mapUsed = new boolean[mapCapacity];
        mapCount = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldUsed[i]) {
                storeHashValue(oldKeys[i], oldValues[i]);
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
