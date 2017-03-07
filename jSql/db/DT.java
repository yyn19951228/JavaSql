package db;
/**
 * Created by yangyining on 24/02/2017.
 */
public interface DT {

    //get the column number
    int getColNumber();

    //get the row number;
    int getRowNumber();

    //get column names
    String[] getColNames();

    String[] getColClass();

    //initiate the DataTable according to the column names
    void initiate(String[] colNames);

    //add an other Row in to the DataTable
    boolean addRow(Object[] ob);

    //used to search the specific row according to the condition
    //the return value is the rowIndex array
    /*  example:
        int[] ex1 = searchIndex("names", ">", "yyn");
        int[] ex2 = searchIndex("ages", ">", 20);
        int[] ex3 = searchIndex("points", ">=", 43.5); need to use the double or float
    * */
    int[] searchIndex(int colIndex, String operator, Object value);

    // select TeamName,Season,Wins,Losses from records where Wins >= Losses  (this condition used)
    DataTable twoColumnSearchTable(int colIndex1, String operator, int colIndex2);

    //used to search the specific row according to the condition
    //the return value is an new DataTable
    DataTable searchTable(int colIndex, String operator, Object value, int... othercolIndex);

    //return a new DT formed by the specific cols
    DataTable getSelectedDT(int[] colNamesIndex);

    //calculate a new column
    Object[] calculate(int colIndex1, String operator, int colIndex2);

    //get the specific column of the DT
    Object[] getColumn(String str);
    Object[] getColumnFromIndex(int colIndex);

    //add a column to the end of the DT
    void addColumn(Object[] o, String colName);

    //print the DataTable, used to test
    void print();


    //test function
    void test();


    // join method
    DataTable join(DataTable dt);

    //check whether the table is malformed table
    boolean isMal();


}
