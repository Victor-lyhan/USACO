package Dec25th.CakeGame.S1;
// V1
import java.io.*;
import java.util.*;

public class Main {
    static long[] arr;
    static int len;
    static long sum;

    static final int INIT_CAP = 1 << 16;
    static long[] mapKeys;
    static long[] mapValues;
    static boolean[] mapUsed;
    static int cap;
    static int mapSize;

    static PrintWriter output;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintWriter(new BufferedOutputStream(System.out));

        int testCases = Integer.parseInt(reader.readLine());
        StringBuilder resultBuilder = new StringBuilder(testCases * 10);

        for (int t = 0; t < testCases; t++) {
            len = Integer.parseInt(reader.readLine());
            arr = new long[len];
            sum = 0;

            StringTokenizer tokens = new StringTokenizer(reader.readLine());
            for (int i = 0; i < len; i++) {
                arr[i] = Long.parseLong(tokens.nextToken());
                sum += arr[i];
            }

            initializeMap();
            long maxElsie = calculate(len, 0);
            long maxBessie = sum - maxElsie;
            resultBuilder.append(maxBessie).append(" ").append(maxElsie).append("\n");
        }

        output.print(resultBuilder.toString());
        output.flush();
    }

    static long calculate(int currentLen, int turn) {
        if (currentLen == 1) {
            return 0;
        }

        long stateKey = generateHash(currentLen, turn);
        Long lookup = retrieve(stateKey);
        if (lookup != null) return lookup;

        long res;
        if (turn == 0) {
            res = Long.MAX_VALUE;
            for (int i = 0; i < currentLen - 1; i++) {
                long temp = arr[i];
                long merge = arr[i + 1];
                arr[i] = temp + merge;

                if (currentLen - (i + 2) > 0) {
                    System.arraycopy(arr, i + 2, arr, i + 1, currentLen - (i + 2));
                }

                long elsieTotal = calculate(currentLen - 1, 1);

                if (currentLen - (i + 2) > 0) {
                    System.arraycopy(arr, i + 1, arr, i + 2, currentLen - (i + 2));
                }
                arr[i] = temp;
                arr[i + 1] = merge;

                if (elsieTotal < res) {
                    res = elsieTotal;
                }
            }
        } else {
            long left = arr[0];
            if (currentLen > 1) {
                System.arraycopy(arr, 1, arr, 0, currentLen - 1);
            }
            long leftMax = left + calculate(currentLen - 1, 0);

            if (currentLen > 1) {
                System.arraycopy(arr, 0, arr, 1, currentLen - 1);
            }
            arr[0] = left;

            long right = arr[currentLen - 1];
            long rightMax = right + calculate(currentLen - 1, 0);

            res = Math.max(leftMax, rightMax);
        }

        store(stateKey, res);
        return res;
    }

    static long generateHash(int length, int turn) {
        long hash = turn;
        for (int i = 0; i < length; i++) {
            hash ^= (arr[i] + 0x9e3779b97f4a7c16L);
            hash *= 0x85ebca6bL;
        }
        return hash;
    }

    static void initializeMap() {
        cap = INIT_CAP;
        mapKeys = new long[cap];
        mapValues = new long[cap];
        mapUsed = new boolean[cap];
        mapSize = 0;
    }

    static void store(long key, long value) {
        if (mapSize * 2 >= cap) {
            expandMap();
        }
        int position = (int)((key * 0x9e3779b97f4a7c16L) & (cap - 1));
        while (mapUsed[position]) {
            if (mapKeys[position] == key) {
                mapValues[position] = value;
                return;
            }
            position = (position + 1) & (cap - 1);
        }
        mapUsed[position] = true;
        mapKeys[position] = key;
        mapValues[position] = value;
        mapSize++;
    }

    static Long retrieve(long key) {
        int position = (int)((key * 0x9e3779b97f4a7c16L) & (cap - 1));
        while (mapUsed[position]) {
            if (mapKeys[position] == key) {
                return mapValues[position];
            }
            position = (position + 1) & (cap - 1);
        }
        return null;
    }

    static void expandMap() {
        int oldCap = cap;
        long[] oldKeys = mapKeys;
        long[] oldValues = mapValues;
        boolean[] oldUsed = mapUsed;

        cap <<= 1;
        mapKeys = new long[cap];
        mapValues = new long[cap];
        mapUsed = new boolean[cap];
        mapSize = 0;

        for (int i = 0; i < oldCap; i++) {
            if (oldUsed[i]) {
                store(oldKeys[i], oldValues[i]);
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

