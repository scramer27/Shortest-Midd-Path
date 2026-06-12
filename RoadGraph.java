package ShortestMiddPath;

import java.util.*;

// this public class allows us to create the reverse adjacency list style graph for our bellman ford algorithm (accessible from other methods)
public class RoadGraph {
    // private datafields to store the reverse adjacency list and set of nodes (hash tables are used for effficiency)
    // asked gemini for "best java data structure that is fast for get method and allows strings as keys"
    // gemini returned HashMap, which makes sense given it's average O(1) get method from 201, corroborated with online source https://www.geeksforgeeks.org/java/difference-between-hashmap-and-hashset/
    private Map<String, List<Edge>> reverseAdjList = new HashMap<>(); // hash map, as need key system with fast get method 
    private Set<String> allNodes = new HashSet<>(); // hash set, as don't need duplicate nodes

    // constructor method, using each row from the 4-column csv to add nodes and create two-way edges
    public RoadGraph(ArrayList<String[]> roadData) {
        // loop through every row
        for (String[] row : roadData) {
            String rdName    = row[0];
            String sourceNode = row[1];
            String endNode   = row[2];
            double miles     = Double.parseDouble(row[3]);

            // add the start and end node (shouldn't duplicate as this is def of set) 
            allNodes.add(sourceNode);
            allNodes.add(endNode);
            
            // forward and backward edges are created regardless of whether or not it is a one-way road as pedestrian user is assumed
            addEdge(sourceNode, endNode, miles, rdName);
            addEdge(endNode, sourceNode, miles, rdName);
        }
    }

    // method to add edges to reverse adjacency list
    private void addEdge(String origin, String end, double weight, String name) {
        // if string key doesn't exist yet, make one
        if (!reverseAdjList.containsKey(end)) {
            reverseAdjList.put(end, new ArrayList<>());
        }
        // add the inbound edge to node's list
        reverseAdjList.get(end).add(new Edge(origin, weight, name)); // add new edge, to the reverse adjacency list to corresponding destination node
    }

    // 201 notes said to use public "get all" methods in conjuction with private datafields
    public Set<String> getAllNodes() { return allNodes; } // method to return the entire set of nodes
    public Map<String, List<Edge>> getReverseAdjList() { return reverseAdjList; } // method to return the entire adjacency list hashmap
}