# Assignment 3 - Graphs

Dyussenbay Damir, SE-2523

This project implements DSU, Kruskal, Prim, Kosaraju, Kahn and shortest paths in a DAG.

## Run

Requirements: Java 17+, Maven, and Python with matplotlib for the plots.

```sh
mvn test
mvn package
java -Xmx2g -jar target/assignment3-graphs-1.0.jar
python -m pip install matplotlib
python plots.py
```

After installing the requirements, `powershell -File run.ps1` builds the project, runs the tests and benchmark, and creates the plots.

The benchmark runs every algorithm five times and saves the median time in `results/results.csv`. It checks the two MST weights, topological orders and the reference results from the assignment. There are 50 rows in the CSV.

To read a small graph and print its result:

```sh
java -cp target/assignment3-graphs-1.0.jar benchmark.Demo data/disconnected.txt
java -cp target/assignment3-graphs-1.0.jar benchmark.Demo data/dag-negative.txt
```

## Files

- `src/main/java`: graph, algorithms, metrics and benchmark.
- `src/test/java`: JUnit 5 tests.
- `data`: four small graphs used by the tests.
- `results/results.csv`: benchmark results.
- `results/plots`: three charts.
- `REPORT.pdf`: report.

`edge_scans` for Kosaraju counts its two DFS passes, without graph reversal. For DAG shortest paths it counts only relaxation; Kahn's counters are available separately. The DAG time includes Kahn. Details and complexity are explained in the report.

## Git and submission

The project contains local Git history with the required feature branches, `main`, and tag `v1.0`. GitHub repository link: not added yet. Publish the repository and add the actual link before uploading the ZIP to Moodle.

## AI use

OpenAI Codex helped with the code, tests, measurements and report. The tests and benchmark were run on this project.
