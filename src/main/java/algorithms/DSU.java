package algorithms;

import metrics.Metrics;

public final class DSU {
    private final int[] parent, size;
    private int components;
    private final Metrics metrics;
    public DSU(int n) { this(n, new Metrics()); }
    public DSU(int n, Metrics metrics) {
        if (n < 0) throw new IllegalArgumentException("Negative set count");
        parent = new int[n]; size = new int[n]; components = n; this.metrics = metrics;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }
    public static DSU makeSet(int n) { return new DSU(n); }
    public int find(int x) {
        if (x < 0 || x >= parent.length) throw new IllegalArgumentException("Invalid element: " + x);
        metrics.findCalls++;
        int root = x;
        while (parent[root] != root) root = parent[root];
        while (parent[x] != x) { int next = parent[x]; parent[x] = root; x = next; }
        return root;
    }
    public boolean union(int x, int y) {
        int a = find(x), b = find(y);
        if (a == b) return false;
        if (size[a] < size[b]) { int t = a; a = b; b = t; }
        parent[b] = a; size[a] += size[b]; components--; metrics.successfulUnions++;
        return true;
    }
    public int components() { return components; }
    public Metrics metrics() { return metrics; }
}
