package ShortestMiddPath;

// the Edge class allows us to track distance of edges, it's origin (following reverse adjacency list), and street names, in the below fields
public class Edge {
    // private datafields for tracking origin String, miles double, and street name of edge (road) String
    private final String origin;
    private final double miles;
    private final String streetName;

    public Edge(String origin, double miles, String streetName) {
        /* this constructor method allows us to take in a node id as a string indicating origin, a double indicating the distance of the edge in miles, and 
        a string indicating the name of the street
        */
        this.origin = origin;
        this.miles = miles;
        this.streetName = streetName;
    }
    // public get methods
    public String getOrigin() { return origin;}
    public double getMiles() { return miles;}
    public String getStreetName() { return streetName;}
}