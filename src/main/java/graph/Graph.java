package graph;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adjacency = new ArrayList<>();
    private final List<List<Edge>> views = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Edge> edgeView = Collections.unmodifiableList(edges);
    private final int[] indegree;

    public Graph(int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Negative vertex count");
        this.n = n; this.directed = directed; indegree = new int[n];
        for (int i = 0; i < n; i++) {
            List<Edge> list = new ArrayList<>();
            adjacency.add(list); views.add(Collections.unmodifiableList(list));
        }
    }
    public int n() { return n; }
    public int m() { return edges.size(); }
    public boolean directed() { return directed; }
    public List<Edge> edges() { return edgeView; }
    public List<Edge> neighbors(int v) { checkVertex(v); return views.get(v); }
    public int indegree(int v) { checkVertex(v); return indegree[v]; }
    public int[] indegrees() { return indegree.clone(); }
    public void checkVertex(int v) {
        if (v < 0 || v >= n) throw new IllegalArgumentException("Vertex out of range: " + v);
    }
    public void requireDirected() {
        if (!directed) throw new IllegalArgumentException("A directed graph is required");
    }
    public void requireUndirected() {
        if (directed) throw new IllegalArgumentException("An undirected graph is required");
    }
    public void addEdge(int u, int v, int w) {
        checkVertex(u); checkVertex(v);
        Edge e = new Edge(u, v, w);
        edges.add(e); adjacency.get(u).add(e); indegree[v]++;
        if (!directed) { adjacency.get(v).add(new Edge(v, u, w)); indegree[u]++; }
    }
    public Graph reversed() {
        requireDirected(); Graph r = new Graph(n, true);
        for (Edge e : edges) r.addEdge(e.to(), e.from(), e.weight());
        return r;
    }
    public static Graph read(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine();
            if (line == null) throw new IOException("Missing graph header");
            String[] h = line.trim().split("\\s+");
            if (h.length != 3 || !(h[2].equals("directed") || h[2].equals("undirected")))
                throw new IOException("Expected: n m directed|undirected");
            int n = Integer.parseInt(h[0]), m = Integer.parseInt(h[1]);
            if (m < 0) throw new IOException("Negative edge count");
            Graph g = new Graph(n, h[2].equals("directed"));
            for (int i = 0; i < m; i++) {
                line = reader.readLine();
                if (line == null) throw new IOException("Missing edge " + (i + 1));
                String[] p = line.trim().split("\\s+");
                if (p.length != 3) throw new IOException("Expected: u v w");
                g.addEdge(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
            }
            while ((line = reader.readLine()) != null)
                if (!line.isBlank()) throw new IOException("More edges than declared");
            return g;
        } catch (IllegalArgumentException e) { throw new IOException("Invalid graph file: " + file, e); }
    }
}
