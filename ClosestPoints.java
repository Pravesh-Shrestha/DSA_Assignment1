import java.util.Arrays;

public class ClosestPoints {
    public static void main(String[] args) {
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};

        int[] result = findClosestPoints(x_coords, y_coords);
        System.out.println("Indices of the closest pair of points: " + Arrays.toString(result)); // Expected Output: [0, 3]
    }

    public static int[] findClosestPoints(int[] x_coords, int[] y_coords) {
        int n = x_coords.length;
        int[] closestPair = new int[2];
        int minDistance = Integer.MAX_VALUE;

        // Iterate through all combinations of points
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) { // Ensure we are comparing different points
                    int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

                    // Check if we found a closer pair or if it's lexicographically smaller
                    if (distance < minDistance || (distance == minDistance && (i < closestPair[0] || (i == closestPair[0] && j < closestPair[1])))) {
                        minDistance = distance;
                        closestPair[0] = i;
                        closestPair[1] = j;
                    }
                }
            }
        }

        return closestPair;
    }
}