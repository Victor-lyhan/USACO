package BalancedTeams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class bteams {
    private int minDifference = 1;
    private int targetSum;

    public int bteams(int[] nums) {
        int totalSum = Arrays.stream(nums).sum();
        targetSum = totalSum / 4;
        int[] teamSums = new int[4];
        Arrays.sort(nums);
        reverse(nums);
        backtrack(nums, teamSums, 0);
        return minDifference;
    }

    private void backtrack(int[] nums, int[] teamSums, int index) {
        if (index == nums.length) {
            int currentDifference = calculateDifference(teamSums);
            if (currentDifference == 1) {
                minDifference = 1;
                return;
            }
            return;
        }

        for (int i = 0; i < 4; i++) {
            teamSums[i] += nums[index];
            if (isPromising(teamSums)) {
                backtrack(nums, teamSums, index + 1);
            }
            teamSums[i] -= nums[index];
            if (teamSums[i] == 0) break;
        }
    }

    private boolean isPromising(int[] teamSums) {
        int maxSum = Arrays.stream(teamSums).max().getAsInt();
        int minSum = Arrays.stream(teamSums).min().getAsInt();
        return maxSum - minSum <= minDifference;
    }

    private int calculateDifference(int[] teamSums) {
        int maxSum = Arrays.stream(teamSums).max().getAsInt();
        int minSum = Arrays.stream(teamSums).min().getAsInt();
        return maxSum - minSum;
    }

    private void reverse(int[] array) {
        int left = 0, right = array.length - 1;
        while (left < right) {
            int temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("BalancedTeams.bteams.in"));
            int[] nums = new int[12];
            for (int i = 0; i < 12; i++) {
                if (scanner.hasNextInt()) {
                    nums[i] = scanner.nextInt();
                }
            }
            scanner.close();

            bteams solution = new bteams();
            int minDifference = solution.bteams(nums);

            PrintWriter writer = new PrintWriter("BalancedTeams.bteams.out");
            System.out.println(minDifference);
            writer.println(minDifference);
            writer.close();

        } catch (FileNotFoundException e) {
            System.err.println("Error: Input file not found.");
        }
    }
}
