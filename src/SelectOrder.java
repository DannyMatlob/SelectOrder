import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Selection {
    final int LESS_THAN = 0;
    final int LESS_EQUAL = 4;
    final int GREATER_THAN = 1;
    final int GREATER_EQUAL = 3;
    final int EQUAL = 2;

    String value;
    int op;
    String colName;
    int colIndex;
    public Selection(String arg) throws Exception {
        String[] arr = arg.split("\\.");
        if (arr.length !=3) throw new Exception("Invalid Selection Arg: " + arg);
        colName = arr[0];

        switch (arr[1]) {
            case "gt": op = GREATER_THAN; break;
            case "ge": op = GREATER_EQUAL; break;
            case "lt": op = LESS_THAN; break;
            case "le": op = LESS_EQUAL; break;
            case "eq": op = EQUAL; break;
            default: throw new Exception("Invalid Selection Arg: must be gt, ge, lt, le, or eq");
        }
        value = arr[2];
    }
    public boolean match(String tableVal) {
        String compareValue = value;
        try {
            int newVal = Integer.parseInt(tableVal);
            //System.out.println("Integer comparison");
            int compareVal = Integer.parseInt(compareValue);
            switch (op) {
                case GREATER_THAN: return newVal>compareVal;
                case GREATER_EQUAL: return newVal>=compareVal;
                case LESS_THAN: return newVal<compareVal;
                case LESS_EQUAL: return newVal<=compareVal;
                case EQUAL: return newVal==compareVal;
            }
            return false;
        } catch (Exception e) {
            //System.out.println("Lexicographic comparison");
            switch (op) {
                case GREATER_THAN: return tableVal.compareTo(compareValue) > 0;
                case GREATER_EQUAL: return tableVal.compareTo(compareValue) >= 0;
                case LESS_THAN: return tableVal.compareTo(compareValue) < 0;
                case LESS_EQUAL: return tableVal.compareTo(compareValue) <= 0;
                case EQUAL: return tableVal.equals(compareValue);
            }
        }
        return false;
    }
    public String getColName(){
        return colName;
    }
    public void setIndex(int i) {
        colIndex = i;
    }
    public int getColIndex() {
        return colIndex;
    }

    @Override
    public String toString() {
        String result = "SELECT FROM " + colName + " WHERE value ";
        switch (op) {
            case GREATER_THAN: result+="> "; break;
            case LESS_THAN: result+="< "; break;
            case EQUAL: result += "= "; break;
        }
        result += value;
        return result;
    }
}
public class SelectOrder {
    static int ASCENDING_ORDER = 0;
    static int DESCENDING_ORDER = 1;
    public static void main(String[] args) throws Exception {
        //Step 1: Parse the command
        if (args.length < 3) {
            //System.err.println("Not Enough args");
            System.err.println("ERROR!");
            return;
        }

        //Parse Arg 0: Filename
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(args[0]));
        } catch (Exception e) {
            // Handle any exceptions that may occur during file reading
            //System.err.println("Invalid FileName: " + e.toString());
            System.err.println("ERROR!");
            return;
        }

        ////Check if file has valid col-names
        String line = reader.readLine();
        String[] colNames;
        if (line==null) {
            //System.err.println("Invalid file contents");
            System.err.println("ERROR!");
            return;
        } else {
            colNames = line.split("\t");
        }

        //Parse from arg[1] WHERE until arg[n] ORDER_BY
        ArrayList<Selection> selections = new ArrayList<>();
        int i = 1;
        if (args[1].equals("WHERE")) {
            //System.out.println("WHERE clause found");
            i++;
        } else if (args[1].equals("ORDER_BY")) {
            //System.out.println("ORDER_BY clause found");
        } else {
            //System.err.println("No WHERE or ORDER_BY found");
            System.err.println("ERROR!");
            return;
        }

        ////Find and create Selections
        while (i < args.length && !args[i].equals("ORDER_BY")) {
            try {
                selections.add(new Selection(args[i]));
            } catch (Exception e) {
                System.err.println("ERROR!");
                return;
            }
            i++;
        }

        //Parse final args past ORDER_BY
        int sortOrder = 0;
        String sortCol = null;
        if (i+2<args.length && args[i].equals("ORDER_BY")) {
            sortCol = args[i+1];
            boolean found = false;

            //Validate sort column
            for (String s : colNames) {
                //System.out.println(s + " : " + sortCol);
                if (sortCol.equals(s)) {
                    found=true;
                    break;
                }
            }
            if (!found) {
                //System.err.println("Invalid sort column");
                System.err.println("ERROR!");
                return;
            }

            //Validate Sort Order
            if (args[i+2].equals("ASC")) {
                sortOrder = ASCENDING_ORDER;
            } else if (args[i+2].equals("DESC")) {
                sortOrder = DESCENDING_ORDER;
            } else {
                //System.err.println("Incorrect sort direction");
                System.err.println("ERROR!");
                return;
            }
        } else {
            //System.err.println("Invalid ORDER_BY clause");
            System.err.println("ERROR!");
            return;
        }
        /*
        //Status Prints
        for (Selection s : selections) {
            System.out.println(s);
        }
        System.out.println("\nselections length: " + selections.size());
        System.out.println("Sort Column: " + sortCol + ", Sort Direction: " + sortOrder + " (0 ASC, 1 DESC)\n End of Program");
         */
        /*
        END OF PARSING ARGS
         */
        //Prepare selections with colIndices
        for (Selection s : selections) {
            String curColName = s.getColName();
            int colIndex = -1;
            for (i = 0; i<colNames.length; i++) {
                if (colNames[i].equals(curColName)) {
                    colIndex = i;
                    break;
                }
            }
            if (colIndex==-1) {
                //System.err.println("Selection: " + s.getColName() + " doesn't match any column names");
                System.err.println("ERROR!");
                return;
            } else {
                s.setIndex(colIndex);
            }
        }

        //Begin reading line by line, only adding rows that match the selection criteria
        ArrayList<String[]> selectedRows = new ArrayList<>();
        while((line = reader.readLine())!=null) {
            boolean addRow = true;
            String[] row = line.split("\t");
            for (Selection s : selections) {
                if (!s.match(row[s.getColIndex()])) {
                    addRow = false;
                }
            }
            if (addRow) selectedRows.add(row);
        }

        //Sort Rows
        //System.out.println("\n Sorting by: " + sortCol + " with sortOrder: " + sortOrder);
        int sortIndex = 0;
        for (i = 0; i<colNames.length; i++) {
            if (colNames[i].equals(sortCol)) {
                sortIndex = i;
                break;
            }
        }

        int finalSortIndex = sortIndex;
        Comparator<String[]> stringComparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] row1, String[] row2) {
                // Assuming the values in the specified column are integers
                String value1 = row1[finalSortIndex];
                String value2 = row2[finalSortIndex];

                // Compare the values
                return value1.compareTo(value2);
            }
        };
        Comparator<String[]> numberComparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] row1, String[] row2) {
                // Assuming the values in the specified column are integers
                int value1 = Integer.parseInt(row1[finalSortIndex]);
                int value2 = Integer.parseInt(row2[finalSortIndex]);

                // Compare the values
                return Integer.compare(value1,value2);
            }
        };
        try {
            Integer.parseInt(selectedRows.get(0)[sortIndex]);
            //System.out.println("Sorting using numberComparator");
            Collections.sort(selectedRows, numberComparator);
        } catch (Exception e) {
            //System.out.println("Sorting using stringComparator");
            Collections.sort(selectedRows, stringComparator);
        }
        if (sortOrder==DESCENDING_ORDER) Collections.reverse(selectedRows);
        //Print Rows
        for (String s : colNames) {
            System.out.print(s + "\t");
        }
        System.out.println();
        for (String[] ss : selectedRows) {
            Iterator rowIterator = Arrays.stream(ss).iterator();
            while (rowIterator.hasNext()) {
                String s = (String) rowIterator.next();
                if (rowIterator.hasNext()) {
                    System.out.print(s + "\t");
                } else {
                    System.out.print(s);
                }
            }
            System.out.println();
        }
    }
}