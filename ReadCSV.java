package ShortestMiddPath;

import java.io.*;
import java.util.*;

/*
 this class allows us to take the road data from MIDDLEBURY_ROADS.csv, which is the filtered version of ALL_ROADS.csv, the statewide file, 
 and then returns the condensed road data that only includes necessary columns
 */
public class ReadCSV {

    // column indices, fixed and irrespective of object
    private static final int CTCODE_COL = 27;
    private static final int MILES_COL  = 31;
    private static final int VISIBLE_COL = 45;
    private static final int RDNAME_COL = 51;
    private static final int START_COL = 60;
    private static final int END_COL = 61;

    // value for Middlebury
    private static final String MIDD_NUM = "0111";

    // data paths
    private static final String ORIGIN_FILE = "ShortestMiddPath/Data/MIDDLEBURY_ROADS.csv";
    private static final String RAW_DATA_FILE = "ShortestMiddPath/Data/ALL_ROADS.csv";

    // method to accomplish the tedious regex split to avoid repeating this code 
    private static String[] splitCSV(String line) {
        // takes a CSV string and then splits it by commas, returning a String array
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // from https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
    }

    // method is here to just show how I filtered for VT vs just Middlebury & visible roads, it isn't run for the this submission, but I found it useful for explanation purposes
    public static void filterData() throws IOException {
        /* this method reads Vermont statewide data and uses a 
           BufferedWriter create a csv for just visible middlebury streets if the file
           doesn't already exist
        */
        File filteredFile = new File(ORIGIN_FILE);

        if (filteredFile.exists()) return; // skip if already filtered data exists

        BufferedReader br = new BufferedReader(new FileReader(RAW_DATA_FILE));
        BufferedWriter bw = new BufferedWriter(new FileWriter(ORIGIN_FILE));
        String line;
        boolean heading = true;

        while ((line = br.readLine()) != null) {
            if (heading) { 
                heading = false;
                continue;
            }
            String[] cols = splitCSV(line);

            // check if row is in Middlebury and visible on the map 
            if (cols.length > VISIBLE_COL && cols[CTCODE_COL].equals(MIDD_NUM) && cols[VISIBLE_COL].equals("1")) {
                bw.write(line);
                bw.newLine();
            }
        }
        br.close();
        bw.close();
    }

    // read MIDDLEBURY_ROADS.csv, then return reduced csv with only road name, start node, end node, and miles (largely from prog assign. 1)
    public static ArrayList<String[]> loadData() throws IOException {
        /* method that shrinks the width of the MIDDLEBURY_ROADS csv by taking the prepopulated file name, filtering out for necessary columns,
        then returning the smaller csv with the bare minimum necessary data
         */

        filterData(); // ensure the MIDDLEBURY_ROADS.csv file exists before reading (that method is how I created the MIDDLEBURY_ROADS.csv from ALL_ROADS.csv )

        ArrayList<String[]> smallCSV = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(ORIGIN_FILE));
        String line;

        while ((line = br.readLine()) != null) {
            String[] cols = splitCSV(line);

            if (cols.length <= END_COL) continue; // avoid index out of bounds

            // create a string representing condensed row
            String[] cleanedData = {
                cols[RDNAME_COL].trim().replace("\"", ""), // remove quote marks
                cols[START_COL].trim(),
                cols[END_COL].trim(),
                cols[MILES_COL].trim(),
            };

            // append row to csv
            smallCSV.add(cleanedData);
        }
        br.close();
        return smallCSV;
    }
}