public class EmployeeRewards {
    public static void main(String[] args) {
        int[] ratings1 = {1, 0, 2};
        int[] ratings2 = {1, 2, 2};

        System.out.println("Minimum rewards needed for ratings " + java.util.Arrays.toString(ratings1) + ": " + minRewards(ratings1)); // Output: 5
        System.out.println("Minimum rewards needed for ratings " + java.util.Arrays.toString(ratings2) + ": " + minRewards(ratings2)); // Output: 4
    }

    public static int minRewards(int[] ratings) {
        int n = ratings.length;
        int[] rewards = new int[n];
        
        // Step 1: Initialize rewards
        for (int i = 0; i < n; i++) {
            rewards[i] = 1;
        }

        // Step 2: Left to Right
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Step 3: Right to Left
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Step 4: Calculate total rewards
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward;
        }

        return totalRewards;
    }
}