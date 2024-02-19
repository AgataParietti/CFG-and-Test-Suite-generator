import networkx as nx
import matplotlib.pyplot as plt

# Creazione del grafo
G = nx.DiGraph()

# Aggiungiamo i nodi
nodes = {
    "A": "A[0]",
    "B": "B[1]",
    "C": "C[2]",
    "D": "D[3]",
    "E": "E[4]",
    "F": "F[5]",
    "G": "G[6]",
    "H": "H[7]"
}

for node, label in nodes.items():
    G.add_node(node, label=label)

# Aggiungiamo gli archi
edges = [
    ("A", "B"),
    ("B", "C"),
    ("B", "H"),
    ("C", "D"),
    ("C", "E"),
    ("D", "G"),
    ("E", "F"),
    ("F", "G"),
    ("G", "B")
]

G.add_edges_from(edges)

# Disegniamo il grafo con disposizione gerarchica
pos = nx.kamada_kawai_layout(G)
labels = {node: G.nodes[node]['label'] for node in G.nodes()}

nx.draw(G, pos, with_labels=True, labels=labels, node_size=1500, node_color="skyblue", font_size=10, font_weight="bold", arrowsize=20)
plt.title("Control Flow Graph (Tree Layout)")
plt.show()

#%%
