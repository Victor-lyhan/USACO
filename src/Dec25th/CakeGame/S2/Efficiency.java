package Dec25th.CakeGame.S2;

import java.io.*;
import java.util.*;

public class Efficiency {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out);
        int testCases = Integer.parseInt(reader.readLine().trim());

        while (testCases-- > 0) {
            int n = Integer.parseInt(reader.readLine().trim());
            long[] cakes = Arrays.stream(reader.readLine().trim().split(" "))
                    .mapToLong(Long::parseLong)
                    .toArray();
            long[] result = findScores(cakes);
            writer.println(result[0] + " " + result[1]);
        }
        writer.close();
    }

    private static long[] findScores(long[] cakes) {
        int n = cakes.length;
        long totalSum = Arrays.stream(cakes).sum();
        if (n == 2) {
            return new long[]{totalSum, 0};
        }

        int moves = n / 2 - 1;
        long[] prefixSum = calculatePrefixSum(cakes);
        long[] leftMax = calculateLeftMax(prefixSum, moves);
        long[] rightMax = calculateRightMax(prefixSum, moves, n);

        long maxElsieScore = findMaxElsieScore(leftMax, rightMax, moves);
        return new long[]{totalSum - maxElsieScore, maxElsieScore};
    }

    private static long[] calculatePrefixSum(long[] cakes) {
        int n = cakes.length;
        long[] prefixSum = new long[n + 1];
        for (int i = 1; i <= n; i++) {
            prefixSum[i] = prefixSum[i - 1] + cakes[i - 1];
        }
        return prefixSum;
    }

    private static long[] calculateLeftMax(long[] prefixSum, int moves) {
        long[] leftMax = new long[moves + 1];
        for (int i = 1; i <= moves; i++) {
            leftMax[i] = prefixSum[i];
        }
        return leftMax;
    }

    private static long[] calculateRightMax(long[] prefixSum, int moves, int n) {
        long[] rightMax = new long[moves + 1];
        for (int i = 1; i <= moves; i++) {
            rightMax[i] = prefixSum[n] - prefixSum[n - i];
        }
        return rightMax;
    }

    private static long findMaxElsieScore(long[] leftMax, long[] rightMax, int moves) {
        long maxScore = Long.MIN_VALUE;
        for (int left = 0; left <= moves; left++) {
            int right = moves - left;
            long score = leftMax[left] + rightMax[right];
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }
}

