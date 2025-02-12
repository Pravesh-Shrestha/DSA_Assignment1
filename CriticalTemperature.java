// Question No 1 a.



public class CriticalTemperature {
    public static int minMeasurements(int k, int n) {
        // Initialize dp array
        int[][] dp = new int[k + 1][n + 1];

        // Base cases
        for (int i = 1; i <= k; i++) {
            dp[i][0] = 0; // 0 temperature levels require 0 tests
            dp[i][1] = 1; // 1 temperature level requires 1 test
        }
        for (int j = 1; j <= n; j++) {
            dp[1][j] = j; // With 1 sample, test sequentially
        }

        // Fill dp array
        for (int i = 2; i <= k; i++) {
            for (int j = 2; j <= n; j++) {
                dp[i][j] = Integer.MAX_VALUE;
                int low = 1, high = j;
                while (low <= high) {
                    int mid = (low + high) / 2;
                    int breakCase = dp[i - 1][mid - 1];
                    int noBreakCase = dp[i][j - mid];
                    int worstCase = 1 + Math.max(breakCase, noBreakCase);

                    dp[i][j] = Math.min(dp[i][j], worstCase);

                    if (breakCase > noBreakCase) {
                        high = mid - 1;
                    } else {
                        low = mid + 1;
                    }
                }
            }
        }

        return dp[k][n];
    }

    public static void main(String[] args) {
        System.out.println(minMeasurements(1, 2)); // Output: 2
        System.out.println(minMeasurements(2, 6)); // Output: 3
        System.out.println(minMeasurements(3, 14)); // Output: 4
    }
}
