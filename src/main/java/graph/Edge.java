package graph;

/** An oriented adjacency entry; the input edge list stores each edge once. */
public record Edge(int from, int to, int weight) { }
