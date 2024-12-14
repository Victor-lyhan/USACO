package Dec25th.CakeGame.S1;

import java.io.*;
import java.util.*;

public class CakeGameOptimized {
    static int numElements;
    static long totalSum;
    static final int HASH_MOD = (int) 1e9 + 7;
    static final int INITIAL_CAPACITY = 1 << 16;
    static long[] memoKeys;
    static long[] memoValues;
    static boolean[] memoUsed;
    static int memoCapacity;
    static int memoCount;
    static PrintWriter writer;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        writer = new PrintWriter(new BufferedOutputStream(System.out));

        int testCases = Integer.parseInt(reader.readLine());
        StringBuilder results = new StringBuilder();

        initMemoization();

        while (testCases-- > 0) {
            numElements = Integer.parseInt(reader.readLine());
            totalSum = 0;
            long[] cakes = new long[numElements];

            StringTokenizer tokens = new StringTokenizer(reader.readLine());
            for (int i = 0; i < numElements; i++) {
                cakes[i] = Long.parseLong(tokens.nextToken());
                totalSum += cakes[i];
            }

            long elsieBest = computeBest(cakes, 0, 0, numElements - 1, Long.MIN_VALUE, Long.MAX_VALUE);
            long bessieBest = totalSum - elsieBest;
            results.append(bessieBest).append(" ").append(elsieBest).append("\n");
        }

        writer.print(results.toString());
        writer.flush();
    }

    static long computeBest(long[] cakes, int turn, int left, int right, long alpha, long beta) {
        if (left == right) {
            return turn == 1 ? cakes[left] : 0;
        }

        long stateHash = calculateStateHash(turn, left, right);
        Long cachedValue = getMemo(stateHash);
        if (cachedValue != null) return cachedValue;

        long result;
        if (turn == 0) { // Bessie's turn
            result = Long.MIN_VALUE;
            for (int i = left; i < right; i++) {
                long merged = cakes[i] + cakes[i + 1];

                long originalLeft = cakes[i];
                long originalRight = cakes[i + 1];

                cakes[i] = merged;
                System.arraycopy(cakes, i + 2, cakes, i + 1, right - i - 1);
                result = Math.max(result, totalSum - computeBest(cakes, 1, left, right - 1, alpha, beta));

                System.arraycopy(cakes, i + 1, cakes, i + 2, right - i - 1);
                cakes[i] = originalLeft;
                cakes[i + 1] = originalRight;

                alpha = Math.max(alpha, result);
                if (alpha >= beta) break;
            }
        } else { // Elsie's turn
            result = Long.MIN_VALUE;

            // Take the leftmost cake
            long leftChoice = cakes[left] + computeBest(cakes, 0, left + 1, right, alpha, beta);
            alpha = Math.max(alpha, leftChoice);
            if (alpha >= beta) {
                storeMemo(stateHash, alpha);
                return alpha;
            }

            // Take the rightmost cake
            long rightChoice = cakes[right] + computeBest(cakes, 0, left, right - 1, alpha, beta);
            result = Math.max(leftChoice, rightChoice);
        }

        storeMemo(stateHash, result);
        return result;
    }

    static long calculateStateHash(int turn, int left, int right) {
        return ((long) turn << 40) | ((long) left << 20) | right;
    }

    static void initMemoization() {
        memoCapacity = INITIAL_CAPACITY;
        memoKeys = new long[memoCapacity];
        memoValues = new long[memoCapacity];
        memoUsed = new boolean[memoCapacity];
        memoCount = 0;
    }

    static void storeMemo(long key, long value) {
        if (memoCount * 2 >= memoCapacity) resizeMemo();
        int index = (int) (key % memoCapacity);
        while (memoUsed[index]) {
            if (memoKeys[index] == key) {
                memoValues[index] = value;
                return;
            }
            index = (index + 1) % memoCapacity;
        }
        memoUsed[index] = true;
        memoKeys[index] = key;
        memoValues[index] = value;
        memoCount++;
    }

    static Long getMemo(long key) {
        int index = (int) (key % memoCapacity);
        while (memoUsed[index]) {
            if (memoKeys[index] == key) {
                return memoValues[index];
            }
            index = (index + 1) % memoCapacity;
        }
        return null;
    }

    static void resizeMemo() {
        int oldCapacity = memoCapacity;
        long[] oldKeys = memoKeys;
        long[] oldValues = memoValues;
        boolean[] oldUsed = memoUsed;

        memoCapacity *= 2;
        memoKeys = new long[memoCapacity];
        memoValues = new long[memoCapacity];
        memoUsed = new boolean[memoCapacity];
        memoCount = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldUsed[i]) {
                storeMemo(oldKeys[i], oldValues[i]);
            }
        }
    }
}


