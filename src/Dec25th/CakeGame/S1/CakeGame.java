package Dec25th.CakeGame.S1;
import java.io.*;
import java.util.*;

public class CakeGame {
    static class Solution {
        int N;
        long[] cakes;
        Map<Long, Long> memo = new HashMap<>();

        // Compute state hash for memoization
        long computeStateHash(int start, int end, int turn) {
            long hash = 0;
            for (int i = start; i <= end; i++) {
                hash = hash * 31 + cakes[i];
            }
            hash = hash * 31 + turn;
            return hash;
        }

        // Solve the game using minimax with memoization
        long solve() {
            return minimax(0, N - 1, 0, 0);
        }

        // Minimax with recursive solving and pruning
        // turn: 0 = Bessie (minimizing), 1 = Elsie (maximizing)
        // start, end: current range of cakes
        // currentElsieTotal: Elsie's current total
        long minimax(int start, int end, int turn, long currentElsieTotal) {
            // Base case: single cake left
            if (start == end) {
                return turn == 1 ? currentElsieTotal + cakes[start] : 0;
            }

            // Compute state hash
            long stateHash = computeStateHash(start, end, turn);

            // Check memoized result
            if (memo.containsKey(stateHash)) {
                return memo.get(stateHash);
            }

            long result;
            if (turn == 0) {  // Bessie's turn (minimizing Elsie's total)
                result = Long.MAX_VALUE;
                // Try merging adjacent cakes
                for (int i = start; i < end; i++) {
                    // Create a copy of current cakes and merge
                    long[] tempCakes = Arrays.copyOf(cakes, cakes.length);
                    tempCakes[i] += tempCakes[i+1];

                    // Create new array without the merged cake
                    long[] newCakes = new long[tempCakes.length - 1];
                    System.arraycopy(tempCakes, 0, newCakes, 0, i+1);
                    System.arraycopy(tempCakes, i+2, newCakes, i+1, tempCakes.length - i - 2);

                    // Temporarily replace cakes
                    long[] originalCakes = cakes;
                    cakes = newCakes;
                    N = newCakes.length;

                    long elsieTotal = minimax(start, end - 1, 1, currentElsieTotal);
                    result = Math.min(result, elsieTotal);

                    // Restore original cakes
                    cakes = originalCakes;
                    N = originalCakes.length;
                }
            } else {  // Elsie's turn (maximizing her total)
                // Try taking leftmost cake
                long leftTotal = minimax(start + 1, end, 0, currentElsieTotal + cakes[start]);

                // Try taking rightmost cake
                long rightTotal = minimax(start, end - 1, 0, currentElsieTotal + cakes[end]);

                result = Math.max(leftTotal, rightTotal);
            }

            // Memoize and return
            memo.put(stateHash, result);
            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        // Read number of test cases
        int T = Integer.parseInt(br.readLine());

        // Process each test case
        for (int t = 0; t < T; t++) {
            // Read number of cakes
            int N = Integer.parseInt(br.readLine());

            // Read cake sizes
            StringTokenizer st = new StringTokenizer(br.readLine());
            long[] cakes = new long[N];
            long totalSum = 0;
            for (int i = 0; i < N; i++) {
                cakes[i] = Long.parseLong(st.nextToken());
                totalSum += cakes[i];
            }

            // Create and solve solution
            Solution solution = new Solution();
            solution.N = N;
            solution.cakes = cakes;

            // Compute Elsie's best total
            long elsieBest = solution.solve();

            // Bessie gets the remainder
            long bessieBest = totalSum - elsieBest;

            // Write output
            bw.write(bessieBest + " " + elsieBest + "\n");
        }

        bw.flush();
        bw.close();
    }
}

/*
2
4
40 30 20 10
4
10 20 30 40
 */
