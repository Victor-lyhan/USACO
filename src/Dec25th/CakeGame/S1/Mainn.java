package Dec25th.CakeGame.S1;//package Dec25th.CakeGame;
// V2
import java.io.*;
import java.util.*;

public class Mainn {
    static long[] stacks;
    static int rounds;
    static long aggregate;

    static final int INIT_CAP = 1 << 16;
    static long[] mapKeys;
    static long[] mapValues;
    static boolean[] mapFlags;
    static int mapCap;
    static int mapSize;

    static PrintWriter output;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintWriter(new BufferedOutputStream(System.out));

        int cases = Integer.parseInt(reader.readLine());
        StringBuilder results = new StringBuilder();

        while (cases-- > 0) {
            rounds = Integer.parseInt(reader.readLine());
            stacks = new long[rounds];
            aggregate = 0;

            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
            for (int i = 0; i < rounds; i++) {
                stacks[i] = Long.parseLong(tokenizer.nextToken());
                aggregate += stacks[i];
            }

            resetHashMap();
            long maxElsie = findOptimal(rounds, 0);
            long maxBessie = aggregate - maxElsie;
            results.append(maxBessie).append(" ").append(maxElsie).append("\n");
        }

        output.print(results.toString());
        output.flush();
    }

    static long findOptimal(int len, int player) {
        if (len == 1) return 0;

        long state = computeHash(len, player);
        Long cached = retrieveFromMap(state);
        if (cached != null) return cached;

        long result;
        if (player == 0) {
            result = Long.MAX_VALUE;
            for (int i = 0; i < len - 1; i++) {
                long curr = stacks[i];
                long next = stacks[i + 1];
                stacks[i] = curr + next;

                if (len - (i + 2) > 0) {
                    System.arraycopy(stacks, i + 2, stacks, i + 1, len - (i + 2));
                }

                long score = findOptimal(len - 1, 1);

                if (len - (i + 2) > 0) {
                    System.arraycopy(stacks, i + 1, stacks, i + 2, len - (i + 2));
                }
                stacks[i] = curr;
                stacks[i + 1] = next;

                result = Math.min(result, score);
            }
        } else {
            long left = stacks[0];
            if (len > 1) {
                System.arraycopy(stacks, 1, stacks, 0, len - 1);
            }
            long leftScore = left + findOptimal(len - 1, 0);

            if (len > 1) {
                System.arraycopy(stacks, 0, stacks, 1, len - 1);
            }
            stacks[0] = left;

            long right = stacks[len - 1];
            long rightScore = right + findOptimal(len - 1, 0);

            result = Math.max(leftScore, rightScore);
        }

        storeInMap(state, result);
        return result;
    }

    static long computeHash(int len, int player) {
        long hash = player;
        for (int i = 0; i < len; i++) {
            hash ^= (stacks[i] + 0x9e3779b97f4a7c16L);
            hash *= 0x85ebca6bL;
        }
        return hash;
    }

    static void resetHashMap() {
        mapCap = INIT_CAP;
        mapKeys = new long[mapCap];
        mapValues = new long[mapCap];
        mapFlags = new boolean[mapCap];
        mapSize = 0;
    }

    static void storeInMap(long key, long value) {
        if (mapSize * 2 >= mapCap) {
            resizeHashMap();
        }
        int pos = (int)((key * 0x9e3779b97f4a7c16L) & (mapCap - 1));
        while (mapFlags[pos]) {
            if (mapKeys[pos] == key) {
                mapValues[pos] = value;
                return;
            }
            pos = (pos + 1) & (mapCap - 1);
        }
        mapFlags[pos] = true;
        mapKeys[pos] = key;
        mapValues[pos] = value;
        mapSize++;
    }

    static Long retrieveFromMap(long key) {
        int pos = (int)((key * 0x9e3779b97f4a7c16L) & (mapCap - 1));
        while (mapFlags[pos]) {
            if (mapKeys[pos] == key) {
                return mapValues[pos];
            }
            pos = (pos + 1) & (mapCap - 1);
        }
        return null;
    }

    static void resizeHashMap() {
        int oldCap = mapCap;
        long[] oldKeys = mapKeys;
        long[] oldValues = mapValues;
        boolean[] oldFlags = mapFlags;

        mapCap <<= 1;
        mapKeys = new long[mapCap];
        mapValues = new long[mapCap];
        mapFlags = new boolean[mapCap];
        mapSize = 0;

        for (int i = 0; i < oldCap; i++) {
            if (oldFlags[i]) {
                storeInMap(oldKeys[i], oldValues[i]);
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
