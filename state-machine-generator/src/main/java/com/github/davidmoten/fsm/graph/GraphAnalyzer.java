package com.github.davidmoten.fsm.graph;

import com.github.davidmoten.exceptions.NoSuchGraphNodeException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class GraphAnalyzer {

    Graph graph;
    Set<String> visited = new HashSet<>();

    public GraphAnalyzer(Graph graph) {
        this.graph = graph;
    }

    /**
     * Determine if an edge connects to the destination
     * @param edge The edge information to examine
     * @param start The node at which to start
     * @param destination The destination node
     * @return The GraphEdge which connects `start` to `destination`, if it exists; null otherwise.
     */
    private GraphEdge visit(GraphEdge edge, String start, String destination) {

        String to = edge.getTo().state().name();

        if(visited.contains(to)) {
            return null;
        }

        visited.add(start);

        if(destination.equals(to)) {
            return edge;
        }

        for(GraphEdge next : getEdges(to)) {
            GraphEdge found = visit(next, to, destination);
            if(found != null) {
                return found;
            }
        }

        return null;
    }

    /**
     * Get the graph edges which start from the specified node, if any.
     * @param name The name of the node to where the edges should start
     * @return a possibly-empty list of edges which start from the passed node name
     */
    List<GraphEdge> getEdges(String name) {
        return graph.getEdges()
            .stream()
            .filter(e -> e.getFrom().state().name().equals(name))
            .collect(Collectors.toList());
    }

    /**
     * Lookup the node with the passed name in the graph
     * @param name The node name to look up
     * @return the GraphNode if found, null otherwise
     */
    GraphNode getNode(String name) {
        return graph.getNodes()
            .stream()
            .filter(n -> n.state().name().equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Find a path between two nodes in the graph
     * @param start The starting node
     * @param destination The destination node
     * @return true if path found, false otherwise
     * @throws NoSuchGraphNodeException if either `start` or `destination` are non-existent nodes
     */
    public boolean isReachable(String start, String destination) throws NoSuchGraphNodeException {
        GraphEdge result = null;

        // ensure the start and destination are valid nodes
        if(getNode(start) == null) {
            throw new NoSuchGraphNodeException(start + " is not a valid node");
        }

        if(getNode(destination) == null) {
            throw new NoSuchGraphNodeException(destination + " is not a valid node");
        }

        if(start.equals(destination)) {
            return true;
        }

        List<GraphEdge> edges = getEdges(start);

        for(GraphEdge next : edges)  {
            result = visit(next, start, destination);
            if(result != null) {
                visited.clear();
                return true;
            }
        }

        visited.clear();
        return false;
    }
}
