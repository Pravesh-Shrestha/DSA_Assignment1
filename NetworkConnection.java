import java.util.*;

class NetworkConnection {
    static class UnionFind {
        int[] parent, rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        public int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x), rootY = find(y);
            if (rootX == rootY) return false;
            if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
            else if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
            else { parent[rootY] = rootX; rank[rootX]++; }
            return true;
        }
    }

    public static int minimumCostToConnectDevices(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();

        // Add module installation as a connection to a virtual node (index 0)
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{0, i + 1, modules[i]});
        }

        // Add given connections
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0], conn[1], conn[2]});
        }

        // Sort edges by cost
        edges.sort(Comparator.comparingInt(a -> a[2]));

        UnionFind uf = new UnionFind(n + 1);
        int totalCost = 0, edgesUsed = 0;

        // Apply Kruskal's Algorithm
        for (int[] edge : edges) {
            if (uf.union(edge[0], edge[1])) {
                totalCost += edge[2];
                edgesUsed++;
                if (edgesUsed == n) break; // Stop when all nodes are connected
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minimumCostToConnectDevices(n, modules, connections)); // Output: 3
    }
}