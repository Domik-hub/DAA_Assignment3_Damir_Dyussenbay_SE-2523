package tests;

import algorithms.*;
import graph.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test
    void dsuTest() {
        int n = 100;
        DSU dsu = new DSU(n);
        int[] sets = new int[n];
        for (int i = 0; i < n; i++) sets[i] = i;
        Random random = new Random(5);

        for (int step = 0; step < 1000; step++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);
            int oldSet = sets[b];
            int newSet = sets[a];
            assertEquals(oldSet != newSet, dsu.union(a, b));
            for (int v = 0; v < n; v++) {
                if (sets[v] == oldSet) sets[v] = newSet;
            }
            assertEquals(Arrays.stream(sets).distinct().count(), dsu.components());
            for (int v = 0; v < n; v++) {
                assertEquals(sets[a] == sets[v], dsu.find(a) == dsu.find(v));
            }
        }
    }

    @Test
    void mstRandomTest() {
        Random random = new Random(10);
        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(40);
            Graph graph = new Graph(n, false);
            for (int v = 1; v < n; v++) {
                graph.addEdge(random.nextInt(v), v, random.nextInt(50) - 10);
            }
            for (int e = 0; e < 3 * n; e++) {
                graph.addEdge(random.nextInt(n), random.nextInt(n), random.nextInt(50) - 10);
            }
            MstResult kruskal = Kruskal.run(graph);
            MstResult prim = Prim.run(graph);
            assertEquals(kruskal.weight(), prim.weight());
            assertEquals(n - 1, kruskal.edges().size());
            assertEquals(n - 1, prim.edges().size());
            assertEquals(1, kruskal.components());
            assertEquals(1, prim.components());
        }
    }

    @Test
    void mstFileTest() throws Exception {
        Graph connected = Graph.read(Path.of("data/connected.txt"));
        assertEquals(10, Kruskal.run(connected).weight());
        assertEquals(10, Prim.run(connected).weight());

        // This file contains parallel edges, loops and isolated vertices.
        Graph disconnected = Graph.read(Path.of("data/disconnected.txt"));
        for (MstResult result : List.of(Kruskal.run(disconnected), Prim.run(disconnected))) {
            assertEquals(-1, result.weight());
            assertEquals(4, result.components());
            assertEquals(2, result.edges().size());
        }
    }

    @Test
    void sccBruteForceTest() {
        Random random = new Random(15);
        for (int test = 0; test < 40; test++) {
            int n = 1 + random.nextInt(200);
            Graph graph = new Graph(n, true);
            for (int e = 0; e < 3 * n; e++) {
                graph.addEdge(random.nextInt(n), random.nextInt(n), 1);
            }
            boolean[][] reachable = new boolean[n][n];
            for (int start = 0; start < n; start++) {
                ArrayDeque<Integer> queue = new ArrayDeque<>();
                reachable[start][start] = true;
                queue.add(start);
                while (!queue.isEmpty()) {
                    int u = queue.remove();
                    for (Edge edge : graph.neighbors(u)) {
                        int v = edge.to();
                        if (!reachable[start][v]) {
                            reachable[start][v] = true;
                            queue.add(v);
                        }
                    }
                }
            }
            SccResult result = Kosaraju.run(graph);
            Set<Integer> components = new HashSet<>();
            for (int u = 0; u < n; u++) {
                components.add(result.componentOf()[u]);
                for (int v = 0; v < n; v++) {
                    assertEquals(reachable[u][v] && reachable[v][u],
                            result.componentOf()[u] == result.componentOf()[v]);
                }
            }
            assertEquals(components.size(), result.count());
            assertEquals(2L * graph.m(), result.metrics().edgeScans);
        }
    }

    @Test
    void topologicalOrderTest() throws Exception {
        Graph dag = Graph.read(Path.of("data/dag-negative.txt"));
        Kahn.Result result = Kahn.run(dag);
        assertFalse(result.hasCycle());
        assertTrue(Kahn.validOrder(dag, result.order()));
        assertEquals(dag.m(), result.metrics().edgeScans);

        Graph cyclic = Graph.read(Path.of("data/cyclic.txt"));
        assertTrue(Kahn.run(cyclic).hasCycle());
        assertEquals(4, Kosaraju.run(cyclic).count());
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(cyclic));
    }

    @Test
    void shortestPathsBellmanFordTest() {
        Random random = new Random(20);
        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(30);
            Graph graph = new Graph(n, true);
            for (int e = 0; e < 100; e++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);
                if (a != b) graph.addEdge(Math.min(a, b), Math.max(a, b), random.nextInt(41) - 20);
            }

            long[] distance = new long[n];
            Arrays.fill(distance, Long.MAX_VALUE);
            for (int v = 0; v < n; v++) {
                if (graph.indegree(v) == 0) distance[v] = 0;
            }
            for (int pass = 0; pass < n - 1; pass++) {
                for (Edge edge : graph.edges()) {
                    if (distance[edge.from()] != Long.MAX_VALUE) {
                        distance[edge.to()] = Math.min(distance[edge.to()], distance[edge.from()] + edge.weight());
                    }
                }
            }
            DagShortestPath.Result result = DagShortestPath.run(graph);
            assertArrayEquals(distance, result.distance());
            assertEquals(graph.m(), result.metrics().edgeScans);
        }
    }

    @Test
    void restorePathTest() throws Exception {
        Graph graph = Graph.read(Path.of("data/dag-negative.txt"));
        DagShortestPath.Result result = DagShortestPath.run(graph);
        assertArrayEquals(new long[]{0, 0, -2, -1, 5, 1, 0}, result.distance());
        assertEquals(List.of(1, 4, 3, 5), result.pathTo(5));
        assertEquals(List.of(6), result.pathTo(6));
    }

    @Test
    void stackSafetyTest() {
        Graph graph = new Graph(100000, true);
        for (int v = 1; v < graph.n(); v++) graph.addEdge(v - 1, v, 1);
        SccResult result = Kosaraju.run(graph);
        assertEquals(100000, result.count());
        assertEquals(100000, result.metrics().maxDepth);
    }

    @Test
    void emptyAndSelfLoopTest() {
        Graph empty = new Graph(0, true);
        assertEquals(0, Kosaraju.run(empty).count());
        assertFalse(Kahn.run(empty).hasCycle());
        assertEquals(0, DagShortestPath.run(empty).sum());

        Graph graph = new Graph(2, true);
        graph.addEdge(0, 0, 1);
        assertTrue(Kahn.run(graph).hasCycle());
        assertEquals(2, Kosaraju.run(graph).count());
    }
}
