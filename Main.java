package ShortestMiddPath;

import java.util.*;

public class Main {
    
    // starting node, corresponding to that closest to 75 Shannon Street
    private static final String ORIGIN_NODE = "53980"; // found manually

    // method to run overall program
    public static void main(String[] args) throws Exception {
         /* this main method performs user interaction, loading of data from csv files
        as well as creation of the roadgraph to be used in the bellman ford algorithm
        */

        // creates scanner to allow for user input
        Scanner scan = new Scanner(System.in);
        String finalDestination; String destinationName;

        // collects road data from csv, then places into graph format using method call
        ArrayList<String[]> graphData = ReadCSV.loadData();
        RoadGraph graph = new RoadGraph(graphData);
        
        // call bellman ford algorithm to compute shortest paths from the CS building
        BellmanFord bf = new BellmanFord(graph, ORIGIN_NODE);

        System.out.println("Shortest Path to Food Finder:\n");
        System.out.println("\nWhich food establishment do you want to go to?");
        System.out.println("#1 Two Brothers");
        System.out.println("#2 Yogurt City");
        System.out.print("Select your choice (1 or 2): ");
        
        // sets the user's integer input 
        int choice = scan.nextInt();
        scan.close();

        // use selection to choose Node ID corresponding with location desired
        if (choice == 1) {
            finalDestination = "37691"; // found manually
            destinationName = "Two Brothers";
        } else {
            finalDestination = "39027"; // found manually
            destinationName = "Yogurt City";
        }

        // print navigation instructions to user
        if (bf.getDistance(finalDestination) == Double.POSITIVE_INFINITY) {
            System.out.println("\nThe final destination desired can't be reached.");
        } else {
            printNavigation(bf, graph, finalDestination, destinationName);
        }
    }

    // method that performs the user navigation instruction printing
    private static void printNavigation(BellmanFord bf, RoadGraph graph, String targetDest, String destName) {
        /* this method takes in the bellman ford algorithm BellmanFord object, the graph we created of the road network RoadGraph object
        , our target destination in terms of ID of the node String, and the destination name corresponding with the restaurant/food 
        location String, returning appropriate navigation instructions */
        double totalDistance = bf.getDistance(targetDest); // store total distance to the target destination
        List<String> nodePath = bf.getPath(targetDest); // store the path of nodes necessary
        List<String> streetPath = bf.getPathWithStreets(targetDest); // stores the street names of this path

        System.out.println("\nNavigation instructions:");
        System.out.printf("Total Distance from 75 Shannon: %.4f miles%n", totalDistance);

        // removes repeated prints of the same road to the user
        double segmentDist = 0;
        String currentRoad = streetPath.get(0); // first element of the path

        for (int i = 0; i < streetPath.size(); i++) {
            String u = nodePath.get(i); // current intersection
            String v = nodePath.get(i + 1); // next intersection
            
            // traverse reverse adjacency list to find the edge weight between u and v
            for (Edge e : graph.getReverseAdjList().get(v)) { // look at edge coming from u
                if (e.getOrigin().equals(u)) { // if the edge's origin is equal to u
                    segmentDist += e.getMiles(); // add to the total segment's distance (sum down the same street)
                    break; // exit loop
                }
            }

            // if the street changes or we've reached the last edge, print
            if (i == streetPath.size() - 1 || !streetPath.get(i + 1).equals(currentRoad)) {
                formatNavigation(currentRoad, segmentDist, nodePath.get(i + 1), targetDest, graph);
                
                // reset value of segment distance, and jump to next segment
                if (i < streetPath.size() - 1) {
                    currentRoad = streetPath.get(i + 1);
                    segmentDist = 0;
                }
            }
        }
        System.out.println("\nNow arriving at " + destName + ".");
    }

    private static void formatNavigation(String road, double dist, String endNode, String target, RoadGraph graph) {
        /* this method takes in the road we are traveling in a string, the distance in miles of the road (a double the end node
        of the road in a string, and the target node, as well as the entire graph, and prints
        the appropriate path to the user ) */

        // here I asked gemini "how to create F strings that have two inputs, a float with 4 decimal points and a string"
        if (endNode.equals(target)) { // if the path ends in the target node
            System.out.printf("Follow %s for %.4f miles until you reach your destination.%n", road, dist);
        } else {  // otherwise check all streets at this intersection
            Set<String> intersections = new HashSet<>(); // unique roads only
            for (Edge edge : graph.getReverseAdjList().get(endNode)) { // loop thorugh edges ending at this intersection
                intersections.add(edge.getStreetName()); // add street name of edge
            }
            intersections.remove(road); // remove the road that we are currently on from output

            if (intersections.isEmpty()) { // needed to fix errors with Creek Road, which has multiple road segments despite no intersections
                System.out.printf("Follow %s for %.4f miles...%n", road, dist);
            } else {    // intersections print statement to user
                System.out.printf("Follow %s for %.4f miles until you reach an intersection with [%s]%n", 
                                  road, dist, String.join(", ", intersections));
            }
        }
    }
}