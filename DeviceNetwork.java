import java.util.*;

public class DeviceNetwork {
    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};

        int result = minTotalCost(n, modules, connections);
        System.out.println("Minimum total cost to connect all devices: " + result); // Output: 3
    }

    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        // Create an adjacency list for the graph
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        // Add edges for the connections
        for (int[] conn : connections) {
            int device1 = conn[0] - 1;
            int device2 = conn[1] - 1;
            int cost = conn[2];
            graph.get(device1).add(new int[]{device2, cost});
            graph.get(device2).add(new int[]{device1, cost});
        }

        // Min-Heap for Prim's algorithm
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        boolean[] visited = new boolean[n];
        int totalCost = 0;

        // Add all module installation costs as initial edges
        for (int i = 0; i < n; i++) {
            minHeap.offer(new int[]{modules[i], i});
        }

        while (!minHeap.isEmpty()) {
            int[] current = minHeap.poll();
            int cost = current[0];
            int device = current[1];

            // If this device is already visited, skip it
            if (visited[device]) continue;

            // Mark the device as visited and add the cost
            visited[device] = true;
            totalCost += cost;

            // Add all edges from this device to the heap
            for (int[] neighbor : graph.get(device)) {
                if (!visited[neighbor[0]]) {
                    minHeap.offer(new int[]{neighbor[1], neighbor[0]});
                }
            }
        }

        return totalCost;
    }
}