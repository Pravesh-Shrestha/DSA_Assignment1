import tkinter as tk
from tkinter import messagebox
import networkx as nx
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

class GraphApp:
    def __init__(self, master):
        self.master = master
        self.graph = nx.Graph()
        self.create_widgets()
        self.initialize_graph()
        self.draw_graph()

    def create_widgets(self):
        self.start_label = tk.Label(self.master, text="Start Node:")
        self.start_label.pack()

        self.start_node = tk.Entry(self.master)
        self.start_node.pack()

        self.end_label = tk.Label(self.master, text="End Node:")
        self.end_label.pack()

        self.end_node = tk.Entry(self.master)
        self.end_node.pack()

        self.find_button = tk.Button(self.master, text="Find Shortest Path", command=self.find_shortest_path)
        self.find_button.pack()

        self.result_area = tk.Text(self.master, height=10, width=50)
        self.result_area.pack()

        # Frame for the graph
        self.graph_frame = tk.Frame(self.master)
        self.graph_frame.pack()

    def initialize_graph(self):
        # Add nodes and edges
        self.graph.add_edges_from([(0, 1, {'cost': 10}), (0, 2, {'cost': 5}),
                                    (1, 2, {'cost': 2}), (1, 3, {'cost': 1}),
                                    (2, 3, {'cost': 9}), (3, 0, {'cost': 7})])

    def draw_graph(self):
        plt.clf()  # Clear the current figure
        pos = nx.spring_layout(self.graph)  # positions for all nodes
        nx.draw(self.graph, pos, with_labels=True, node_size=700, node_color='lightblue', font_size=10)
        
        # Draw edge labels
        edge_labels = nx.get_edge_attributes(self.graph, 'cost')
        nx.draw_networkx_edge_labels(self.graph, pos, edge_labels=edge_labels)

        # Create a canvas for the matplotlib figure
        fig = plt.gcf()
        canvas = FigureCanvasTkAgg(fig, master=self.graph_frame)
        canvas.draw()
        canvas.get_tk_widget().pack()

    def find_shortest_path(self):
        start = int(self.start_node.get())
        end = int(self.end_node.get())

        try:
            path = nx.dijkstra_path(self.graph, start, end)
            cost = sum(self.graph[u][v]['cost'] for u, v in zip(path[:-1], path[1:]))
            self.result_area.delete(1.0, tk.END)
            self.result_area.insert(tk.END, f"Shortest Path: {path}\nTotal Cost: {cost}")
        except nx.NetworkXNoPath:
            messagebox.showerror("Error", "No path found between the selected nodes.")
        except Exception as e:
            messagebox.showerror("Error", str(e))

if __name__ == "__main__":
    root = tk.Tk()
    root.title("Network Optimization")
    app = GraphApp(root)
    root.mainloop()