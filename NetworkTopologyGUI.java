import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class NetworkNode {
    int x, y, id;
    boolean isServer;

    public NetworkNode(int id, int x, int y, boolean isServer) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isServer = isServer;
    }
}

class NetworkEdge {
    NetworkNode node1, node2;
    int cost, bandwidth;

    public NetworkEdge(NetworkNode node1, NetworkNode node2, int cost, int bandwidth) {
        this.node1 = node1;
        this.node2 = node2;
        this.cost = cost;
        this.bandwidth = bandwidth;
    }
}

public class NetworkTopologyGUI extends JPanel {
    private final java.util.List<NetworkNode> nodes = new java.util.ArrayList<>();
    private final java.util.List<NetworkEdge> edges = new java.util.ArrayList<>();
    private int nodeCount = 0;
    private NetworkNode lastSelected = null; // Store last selected node
    private JButton optimizeButton, shortestPathButton;
    private JTextField startNodeField, endNodeField;
    private JLabel totalCostLabel, latencyLabel, connectivityLabel;

    public NetworkTopologyGUI() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        // Add control panel for buttons and labels
        JPanel controlPanel = new JPanel();
        optimizeButton = new JButton("Optimize Network (Min Cost)");
        shortestPathButton = new JButton("Find Shortest Path");
        startNodeField = new JTextField(5);
        endNodeField = new JTextField(5);
        totalCostLabel = new JLabel("Total Cost: $0 | Latency: 0ms | Connectivity: Disconnected");
        controlPanel.add(optimizeButton);
        controlPanel.add(new JLabel("Start Node:"));
        controlPanel.add(startNodeField);
        controlPanel.add(new JLabel("End Node:"));
        controlPanel.add(endNodeField);
        controlPanel.add(shortestPathButton);
        controlPanel.add(totalCostLabel);

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.add(controlPanel, BorderLayout.NORTH);
        }

        // Mouse listener for adding nodes and edges
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse clicked at: " + e.getX() + ", " + e.getY());

                if (SwingUtilities.isRightMouseButton(e)) {
                    boolean isServer = JOptionPane.showConfirmDialog(null, "Is this a server?", "Node Type", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    nodes.add(new NetworkNode(nodeCount++, e.getX(), e.getY(), isServer));
                    System.out.println("Added node: N" + (nodeCount - 1));
                    updateEvaluation();
                    repaint();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    NetworkNode selected = findNode(e.getX(), e.getY());
                    if (selected != null) {
                        if (lastSelected == null) {
                            lastSelected = selected;
                            System.out.println("First node selected: N" + lastSelected.id);
                        } else {
                            String costStr = JOptionPane.showInputDialog("Enter Cost:");
                            String bandwidthStr = JOptionPane.showInputDialog("Enter Bandwidth:");
                            if (costStr != null && bandwidthStr != null) {
                                try {
                                    int cost = Integer.parseInt(costStr);
                                    int bandwidth = Integer.parseInt(bandwidthStr);
                                    edges.add(new NetworkEdge(lastSelected, selected, cost, bandwidth));
                                    System.out.println("Added edge between N" + lastSelected.id + " and N" + selected.id);
                                    updateEvaluation();
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter numbers.");
                                }
                            }
                            lastSelected = null; // Reset after adding an edge
                            repaint();
                        }
                    }
                }
            }
        });

        // Button actions
        optimizeButton.addActionListener(e -> optimizeNetwork());
        shortestPathButton.addActionListener(e -> findShortestPath());
    }

    private NetworkNode findNode(int x, int y) {
        for (NetworkNode node : nodes) {
            if (Math.hypot(node.x - x, node.y - y) < 20) {
                return node;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Repainting... Nodes: " + nodes.size() + ", Edges: " + edges.size());

        for (NetworkEdge edge : edges) {
            g.setColor(Color.BLACK);
            g.drawLine(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y);
            int midX = (edge.node1.x + edge.node2.x) / 2;
            int midY = (edge.node1.y + edge.node2.y) / 2;
            g.drawString("$" + edge.cost + " | BW: " + edge.bandwidth, midX, midY);
        }

        for (NetworkNode node : nodes) {
            g.setColor(node.isServer ? Color.RED : Color.BLUE);
            g.fillOval(node.x - 10, node.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawOval(node.x - 10, node.y - 10, 20, 20);
            g.drawString("N" + node.id, node.x - 5, node.y - 5);
        }
    }

    private void updateEvaluation() {
        if (nodes.isEmpty()) {
            totalCostLabel.setText("Total Cost: $0 | Latency: 0ms | Connectivity: Disconnected");
            return;
        }

        int totalCost = 0;
        int totalLatency = 0; // Assuming latency is inversely related to bandwidth (e.g., 1000/bandwidth in ms)
        boolean isConnected = isGraphConnected();

        for (NetworkEdge edge : edges) {
            totalCost += edge.cost;
            totalLatency += 1000 / edge.bandwidth; // Simple latency calculation (higher bandwidth = lower latency)
        }

        String connectivity = isConnected ? "Connected" : "Disconnected";
        totalCostLabel.setText("Total Cost: $" + totalCost + " | Latency: " + totalLatency + "ms | Connectivity: " + connectivity);
    }

    private boolean isGraphConnected() {
        if (nodes.isEmpty()) return false;
        boolean[] visited = new boolean[nodes.size()];
        dfs(0, visited); // Start DFS from node 0
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private void dfs(int node, boolean[] visited) {
        visited[node] = true;
        for (NetworkEdge edge : edges) {
            int next = (edge.node1.id == node) ? edge.node2.id : (edge.node2.id == node) ? edge.node1.id : -1;
            if (next != -1 && !visited[next]) {
                dfs(next, visited);
            }
        }
    }

    private void optimizeNetwork() {
        if (nodes.isEmpty() || edges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No nodes or edges to optimize!");
            return;
        }

        // Use Kruskal's algorithm for minimum spanning tree (MST) to minimize cost while ensuring connectivity
        java.util.List<NetworkEdge> mst = kruskalMST();
        edges.clear();
        edges.addAll(mst);
        updateEvaluation();
        repaint();
        JOptionPane.showMessageDialog(this, "Network optimized for minimum cost!");
    }

    private java.util.List<NetworkEdge> kruskalMST() {
        java.util.List<NetworkEdge> mst = new java.util.ArrayList<>();
        java.util.List<NetworkEdge> sortedEdges = new java.util.ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(e -> e.cost)); // Sort by cost

        DisjointSet ds = new DisjointSet(nodes.size());
        for (NetworkEdge edge : sortedEdges) {
            int u = edge.node1.id;
            int v = edge.node2.id;
            if (ds.find(u) != ds.find(v)) {
                mst.add(edge);
                ds.union(u, v);
            }
        }
        return mst;
    }

    private void findShortestPath() {
        try {
            int start = Integer.parseInt(startNodeField.getText());
            int end = Integer.parseInt(endNodeField.getText());
            if (start < 0 || start >= nodes.size() || end < 0 || end >= nodes.size()) {
                JOptionPane.showMessageDialog(this, "Invalid node IDs!");
                return;
            }

            // Use Dijkstra's algorithm for shortest path based on cost
            java.util.Map<Integer, Integer> distances = dijkstra(start, true); // Using cost as weight
            if (distances.get(end) == Integer.MAX_VALUE) {
                JOptionPane.showMessageDialog(this, "No path exists between nodes!");
                return;
            }

            // Highlight the path in the GUI (simplified: print and redraw with color)
            java.util.List<Integer> path = reconstructPath(start, end, distances);
            highlightPath(path);
            JOptionPane.showMessageDialog(this, "Shortest path cost: " + distances.get(end));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid node numbers!");
        }
    }

    private java.util.Map<Integer, Integer> dijkstra(int start, boolean useCost) {
        java.util.Map<Integer, Integer> distances = new java.util.HashMap<>();
        for (NetworkNode node : nodes) {
            distances.put(node.id, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        java.util.PriorityQueue<int[]> pq = new java.util.PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int dist = current[1];

            if (dist > distances.get(u)) continue;

            for (NetworkEdge edge : edges) {
                int v = (edge.node1.id == u) ? edge.node2.id : (edge.node2.id == u) ? edge.node1.id : -1;
                if (v != -1) {
                    int weight = useCost ? edge.cost : 1000 / edge.bandwidth; // Use cost or latency (1/bandwidth)
                    int newDist = distances.get(u) + weight;
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        pq.offer(new int[]{v, newDist});
                    }
                }
            }
        }
        return distances;
    }

    private java.util.List<Integer> reconstructPath(int start, int end, java.util.Map<Integer, Integer> distances) {
        java.util.List<Integer> path = new java.util.ArrayList<>();
        int current = end;
        while (current != start) {
            path.add(0, current);
            for (NetworkEdge edge : edges) {
                int u = edge.node1.id;
                int v = edge.node2.id;
                int weight = edge.cost;
                if ((u == current && distances.get(u) + weight == distances.get(v)) ||
                    (v == current && distances.get(v) + weight == distances.get(u))) {
                    current = u == current ? v : u;
                    break;
                }
            }
        }
        path.add(0, start);
        return path;
    }

    private void highlightPath(java.util.List<Integer> path) {
        // Simplified: Print the path and redraw edges in red for the path
        System.out.println("Shortest path: " + path);
        repaint();
    }

    // Disjoint Set for Kruskal's algorithm
    static class DisjointSet {
        int[] parent, rank;

        DisjointSet(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        void union(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return;
            if (rank[px] < rank[py]) parent[px] = py;
            else if (rank[px] > rank[py]) parent[py] = px;
            else {
                parent[py] = px;
                rank[px]++;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Network Topology GUI");
            NetworkTopologyGUI panel = new NetworkTopologyGUI();
            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center window
            frame.setVisible(true);
        });
    }
}