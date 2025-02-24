import java.util.*;

public class PackageCollection {
    static class State {
        int location;
        int roadsTraversed;

        State(int location, int roadsTraversed) {
            this.location = location;
            this.roadsTraversed = roadsTraversed;
        }
    }

    public static int minRoadsToCollect(int[] packages, int[][] roads) {
        int n = packages.length;
        // Build adjacency list (undirected graph)
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            adj.get(road[0]).add(road[1]);
            adj.get(road[1]).add(road[0]); // Undirected
        }

        // Find locations with packages
        List<Integer> packageLocations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                packageLocations.add(i);
            }
        }

        int minRoads = Integer.MAX_VALUE;
        // Try each location as starting point
        for (int start = 0; start < n; start++) {
            // BFS to collect packages and return
            Queue<State> queue = new LinkedList<>();
            boolean[] visited = new boolean[n];
            int[] distances = new int[n]; // Distance from start, initialize to -1 (unreachable)
            Arrays.fill(distances, -1); // Initialize all distances as unreachable
            boolean[] packagesCollected = new boolean[n]; // Track collected packages
            queue.offer(new State(start, 0));
            visited[start] = true;
            distances[start] = 0; // Start distance is 0

            while (!queue.isEmpty()) {
                State current = queue.poll();
                int loc = current.location;
                int currentRoads = current.roadsTraversed;

                // Collect packages within distance 2 from the current location
                boolean collectedNewPackage = false;
                for (int i = 0; i < n; i++) {
                    if (distances[i] <= 2 && distances[i] != -1 && packages[i] == 1 && !packagesCollected[i]) {
                        packagesCollected[i] = true;
                        collectedNewPackage = true;
                    }
                }

                // Check if all packages are collected
                boolean allCollected = true;
                for (int p : packageLocations) {
                    if (!packagesCollected[p]) {
                        allCollected = false;
                        break;
                    }
                }

                if (allCollected) {
                    // Try to return to start, counting roads
                    int returnRoads = bfsReturn(adj, loc, start, new boolean[n]); // New visited array
                    if (returnRoads != -1) {
                        minRoads = Math.min(minRoads, currentRoads + returnRoads);
                    }
                    continue;
                }

                // Explore neighbors to collect more packages, only if we collected a new package
                if (collectedNewPackage) {
                    for (int neighbor : adj.get(loc)) {
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            distances[neighbor] = distances[loc] + 1;
                            queue.offer(new State(neighbor, currentRoads + 1));
                        }
                    }
                }
            }
        }

        return minRoads == Integer.MAX_VALUE ? -1 : minRoads;
    }

    // Helper function to find shortest path back to start, counting roads
    private static int bfsReturn(List<List<Integer>> adj, int start, int target, boolean[] visited) {
        Queue<Integer> queue = new LinkedList<>();
        int[] distance = new int[adj.size()];
        Arrays.fill(distance, Integer.MAX_VALUE);
        queue.offer(start);
        distance[start] = 0;
        boolean[] localVisited = new boolean[adj.size()]; // Local visited array
        localVisited[start] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == target) {
                return distance[current];
            }
            for (int neighbor : adj.get(current)) {
                if (!localVisited[neighbor]) {
                    localVisited[neighbor] = true;
                    distance[neighbor] = distance[current] + 1;
                    queue.offer(neighbor);
                }
            }
        }
        return -1; // No path back
    }

    // Test the solution
    public static void main(String[] args) {
        // Test Case 1
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println("Test 1: " + minRoadsToCollect(packages1, roads1)); // Should print 2

        // Test Case 2
        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println("Test 2: " + minRoadsToCollect(packages2, roads2)); // Should print 2
    }
}