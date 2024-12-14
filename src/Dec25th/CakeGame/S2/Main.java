package Dec25th.CakeGame.S2;

import java.io.*;
import java.util.*;

public class Main {
    static BufferedReader reader;
    static PrintWriter writer;
    static StringTokenizer tokenizer;

    static int readInt() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) tokenizer = new StringTokenizer(reader.readLine());
        return Integer.parseInt(tokenizer.nextToken());
    }

    static long readLong() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) tokenizer = new StringTokenizer(reader.readLine());
        return Long.parseLong(tokenizer.nextToken());
    }

    public static void main(String[] args) throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        writer = new PrintWriter(System.out);

        int testCases = readInt();
        for (int test = 0; test < testCases; test++) {
            int totalCakes = readInt();
            long[] cakes = new long[totalCakes];
            for (int i = 0; i < totalCakes; i++) {
                cakes[i] = readLong();
            }
            long[] result = calculateCakeDistribution(cakes);
            writer.println(result[0] + " " + result[1]);
        }

        writer.close();
    }

    static long[] calculateCakeDistribution(long[] cakes) {
        int cakeCount = cakes.length;
        long totalCakeSize = 0;

        for (long size : cakes) {
            totalCakeSize += size;
        }

        int elsieTurns = cakeCount / 2 - 1;

        if (elsieTurns < 0) {
            return new long[]{totalCakeSize, 0};
        }

        long[] cumulativeSum = new long[cakeCount + 1];
        for (int i = 1; i <= cakeCount; i++) {
            cumulativeSum[i] = cumulativeSum[i - 1] + cakes[i - 1];
        }

        long[] maxLeftSum = new long[elsieTurns + 1];
        long[] maxRightSum = new long[elsieTurns + 1];

        for (int i = 1; i <= elsieTurns; i++) {
            maxLeftSum[i] = cumulativeSum[i];
            maxRightSum[i] = cumulativeSum[cakeCount] - cumulativeSum[cakeCount - i];
        }

        long maxElsie = Long.MIN_VALUE;
        for (int left = 0; left <= elsieTurns; left++) {
            int right = elsieTurns - left;
            long currentSum = maxLeftSum[left] + maxRightSum[right];
            maxElsie = Math.max(maxElsie, currentSum);
        }

        long bessieShare = totalCakeSize - maxElsie;

        return new long[]{bessieShare, maxElsie};
    }
}


