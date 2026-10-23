package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public final class Kruskal {
    private Kruskal() { }
    public static MstResult run(Graph g) {
        g.requireUndirected(); Metrics metrics = new Metrics();
        Edge[] sorted = g.edges().toArray(Edge[]::new);
        mergeSort(sorted, metrics);
        DSU dsu = new DSU(g.n(), metrics);
        List<Edge> chosen = new ArrayList<>(); long weight = 0;
        for (Edge e : sorted) {
            if (dsu.components() <= 1) break;
            metrics.edgeScans++;
            if (dsu.find(e.from()) != dsu.find(e.to())) {
                dsu.union(e.from(), e.to()); chosen.add(e); weight += e.weight();
            }
        }
        return new MstResult(weight, chosen, dsu.components(), metrics);
    }
    
    private static void mergeSort(Edge[] a, Metrics metrics) {
        Edge[] temp = new Edge[a.length];
        for (long width = 1; width < a.length; width *= 2) {
            for (long left = 0; left < a.length; left += 2 * width) {
                int l = (int) left, mid = (int) Math.min(left + width, a.length);
                int end = (int) Math.min(left + 2 * width, a.length), i = l, j = mid;
                for (int k = l; k < end; k++) {
                    if (i == mid) temp[k] = a[j++];
                    else if (j == end) temp[k] = a[i++];
                    else {
                        metrics.comparisons++;
                        temp[k] = a[i].weight() <= a[j].weight() ? a[i++] : a[j++];
                    }
                }
            }
            System.arraycopy(temp, 0, a, 0, a.length);
        }
    }
}
