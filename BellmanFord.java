package ShortestMiddPath;

import java.util.*;

// this class contains our bellman ford algorithm, that is used to find shortest distance between two points from the VT dataset
public class BellmanFord {
    // here are private datafields holding minimum distance dist, pred which is the predessessor 
    private Map<String, Double> dist; // stores current shortest distance to each node
    private Map<String, String> pred; // stores predecessor node's ID
    private Map<String, String> roadUsed; // stores the streets taken

    public BellmanFord(RoadGraph graph, String source) {
        // this method is the constructor of the bellman ford algorithm, 
        runAlgorithm(graph, source); // here we call the full algorithm
    }

    // bellman ford algorithm implementation (DP)
    private void runAlgorithm(RoadGraph graph, String source) {
        // this method runs the bellman ford algorithm
        Set<String> allNodes = graph.getAllNodes(); // set of all nodes (no repeats)
        int numNodes = allNodes.size(); // number of nodes in an integer
        Map<String, List<Edge>> reverseAdjList = graph.getReverseAdjList(); // get list of edges getting to a node

        // these will represent the shortest paths discovered using one less edge
        // asked gemini for "best java data structure that is fast for get method and allows strings as keys"
        // gemini returned HashMap, which makes sense given it's average O(1) get method from 201, corroborated with online source https://www.geeksforgeeks.org/java/difference-between-hashmap-and-hashset/
        Map<String, Double> prevDist = new HashMap<>(); 
        Map<String, String> prevPred = new HashMap<>(); 
        Map<String, String> prevRoad = new HashMap<>();
        
        // this initializes our A matrix
        for (String node : allNodes) { 
            prevDist.put(node, Double.POSITIVE_INFINITY); // initialize all distances as infinity
            prevPred.put(node, null); // initialize predecessor nodes as empty
            prevRoad.put(node, null); // initialize roads taken as empty
        }
        prevDist.put(source, 0.0); // initialize the source's distance as 0 (like lecture)

        // outer loop going from min to max # of edges, i is 1 to n-1, O(n)
        for (int i = 1; i < numNodes; i++) {

            // currDist represents A[u, i], which is the copy of the row above
            // this handles the "path uses fewer than i edges" case
            Map<String, Double> currDist = new HashMap<>(prevDist); 
            Map<String, String> currPred = new HashMap<>(prevPred);
            Map<String, String> currEdge = new HashMap<>(prevRoad);

            // middle loop traversing each node u, so O(n) is scaled every outer loop to O(n^2)
            for (String u : allNodes) {
                List<Edge> incoming = reverseAdjList.get(u);
                if (incoming == null) continue; // skip if no edges coming into the node

                // inner loop uses reverse adj list for a outer loop total of at max m edges, so other option of O(nm)
                for (Edge edge : incoming) {
                    // A[u, i] = min(A[u, i], A[v, i-1] + w(v, u)) // checks if adding the weight from a different edge is cheaper than copy
                    double candidate = prevDist.get(edge.getOrigin()) + edge.getMiles();
                    if (candidate < currDist.get(u)) {
                        currDist.put(u, candidate);
                        currPred.put(u, edge.getOrigin());
                        currEdge.put(u, edge.getStreetName());
                    }
                }
            }

            // previous row is updated with current row of matrix
            prevDist = currDist;
            prevPred = currPred;
            prevRoad = currEdge;
        }

        // keep final results
        dist = prevDist;
        pred = prevPred;
        roadUsed = prevRoad;
    }
    
    // "get methods" following 201 style given private datafields
    public double getDistance(String target) {
        // return shortest distance from origin to target, infinity if target node not reachable
        if (dist.containsKey(target)) { // if possible to reach
            return dist.get(target);
        } else { // otherwise, return infinity
            return Double.POSITIVE_INFINITY;
        }
    }

    public List<String> getPath(String target) {
        // this method returns intersection ID name paths
        ArrayList<String> reversedIntersections = new ArrayList<>();
        String currentNode = target;
        while (currentNode != null) {
            reversedIntersections.add(currentNode);
            currentNode = pred.get(currentNode);
        }
        // flip order
        Collections.reverse(reversedIntersections);
        return reversedIntersections;
    }

    public List<String> getPathWithStreets(String target) {
        // this method returns the street name paths in order to get from origin to end
        ArrayList<String> reversedStreets = new ArrayList<>();
        String currentNode = target;
        while (currentNode != null && roadUsed.get(currentNode) != null) {
            reversedStreets.add(roadUsed.get(currentNode));
            currentNode = pred.get(currentNode);
        }
        // flip order
        Collections.reverse(reversedStreets);
        return reversedStreets;
    }
}
