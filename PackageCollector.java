import java.util.*;

public class PackageCollector {

    public static int minMovesToCollectPackages(int[] packages, List<List<Integer>> roads) {
        int n = packages.length;
        int totalPackages = Arrays.stream(packages).sum();
        int collected = 0;

        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        
        // Start at location 0
        queue.offer(new int[]{0, 0}); // {current location, current moves}
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int location = current[0];
            int moves = current[1];
            
            // If already visited, skip
            if (visited.contains(location)) {
                continue;
            }
            
            visited.add(location);
            collected += packages[location];
            
            // Check if all packages are collected
            if (collected == totalPackages) {
                return moves;
            }
            
            // Explore neighbors
            for (int neighbor : roads.get(location)) {
                if (!visited.contains(neighbor)) {
                    queue.offer(new int[]{neighbor, moves + 1});
                }
            }
        }
        
        return -1; // If not all packages can be collected
    }
    
    public static void main(String[] args) {
        // Example Input
        int[] packages = {1, 0, 0, 0, 1, 0}; // Adjust the package array as necessary
        
        // Roads represented as adjacency list
        List<List<Integer>> roads = new ArrayList<>();
        roads.add(Arrays.asList(1, 2)); // Location 0 connects to 1 and 2
        roads.add(Arrays.asList(0, 3)); // Location 1 connects to 0 and 3
        roads.add(Arrays.asList(0, 3)); // Location 2 connects to 0 and 3
        roads.add(Arrays.asList(1, 2, 4)); // Location 3 connects to 1, 2, and 4
        roads.add(Arrays.asList(3, 5)); // Location 4 connects to 3 and 5
        roads.add(Arrays.asList(4)); // Location 5 connects to 4
        
        // Output the minimum number of moves
        int result = minMovesToCollectPackages(packages, roads);
        System.out.println(result);
    }
}
