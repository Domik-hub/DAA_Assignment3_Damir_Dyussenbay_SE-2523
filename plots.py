import csv
import math
import os
from pathlib import Path

root = Path(__file__).resolve().parent
os.environ.setdefault("MPLCONFIGDIR", str(root / "target" / "matplotlib"))
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

with open(root / "results/results.csv") as file:
    rows = list(csv.DictReader(file))
folder = root / "results/plots"
folder.mkdir(parents=True, exist_ok=True)
algorithms = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
markers = ["o", "s", "x", "o", "+"]

def select(name, density):
    return [r for r in rows if r["algorithm"] == name and r["graph"].endswith(density)]

def slope(x, y):
    x = [math.log(v) for v in x]
    y = [math.log(v) for v in y]
    mx, my = sum(x) / len(x), sum(y) / len(y)
    return sum((a-mx)*(b-my) for a,b in zip(x,y)) / sum((a-mx)**2 for a in x)

plt.figure(figsize=(9, 4.5))
for i, name in enumerate(algorithms):
    for density, style in [("sparse", "-"), ("dense", "--")]:
        data = select(name, density)
        n = [int(r["n"]) for r in data]
        work = [int(r["edge_scans"]) + int(r["comparisons"]) + int(r["dsu_ops"]) for r in data]
        plt.loglog(n, work, linestyle=style, marker=markers[i], fillstyle="none", color=f"C{i}", label=name + " " + density)
        print(name, density, "operations slope:", round(slope(n, work), 3))
        print(name, density, "scans slope:", round(slope(n, [int(r["edge_scans"]) for r in data]), 3))
plt.xlabel("n")
plt.ylabel("Operations")
plt.title("Operations vs n")
plt.grid(alpha=0.3)
plt.legend(fontsize=7, ncol=2)
plt.tight_layout()
plt.savefig(folder / "operations-vs-n.png", dpi=160)
plt.close()

fig, axes = plt.subplots(1, 2, figsize=(9, 3.8), sharey=True)
for ax, density in zip(axes, ["sparse", "dense"]):
    for i, name in enumerate(algorithms):
        data = select(name, density)
        ax.semilogx([int(r["n"]) for r in data], [int(r["edge_scans"])/int(r["m"]) for r in data],
                    marker=markers[i], fillstyle="none", label=name)
    ax.set_title(density)
    ax.set_xlabel("n")
    ax.grid(alpha=0.3)
    ax.set_ylim(0, 2.25)
axes[0].set_ylabel("edge_scans / m")
axes[1].legend(fontsize=7, loc="upper center", bbox_to_anchor=(.5, .8), ncol=2)
plt.tight_layout()
plt.savefig(folder / "scans-per-edge.png", dpi=160)
plt.close()

plt.figure(figsize=(9, 3.6))
for density in ["sparse", "dense"]:
    data = select("Kruskal", density)
    plt.semilogx([int(r["n"]) for r in data], [int(r["edge_scans"])/int(r["m"]) for r in data], "o-", label=density)
plt.xlabel("n")
plt.ylabel("edge_scans / m")
plt.title("Share of edges examined by Kruskal")
plt.ylim(0, 1.1)
plt.grid(alpha=0.3)
plt.legend()
plt.tight_layout()
plt.savefig(folder / "kruskal-edge-share.png", dpi=160)
plt.close()
