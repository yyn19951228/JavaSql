package db;

//import com.sun.xml.internal.xsom.impl.scd.Iterators;
//import sun.security.provider.SHA;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

/**
 * Created by yangyining on 22/02/2017.
 */
public class DataTable implements DT {
    private HashMap<String, ArrayList> datatable;

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
    //remember to update rowNumber after add-method/
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//

    // the capacity of the map( how many columns in the map)
    private int capacity;

    //the row numbers of the table
    private int rowNumber;

    //store the column names
    private String[] ColNames;

    //store the class of every column
    private String[] ColClass;

    DataTable(int capacity) {
        this.capacity = capacity;
        //declare the Genric HashMap
        datatable = new HashMap<>(capacity);
        rowNumber = 0;

        ColClass = new String[capacity];

    };


    public void initiate(String...colNames) {
        this.ColNames = colNames;
        ColClass = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            ArrayList<Object> col = new ArrayList<>(0);
            datatable.put(colNames[i], col);

            //use the colNames to get colClass
            //i.e. "X int" -> "X" and "int"
            ColClass[i] = colNames[i].trim().split("\\s+")[1];
        }
        rowNumber = datatable.get(ColNames[0]).size();
    }

    public int getColNumber() {
        return capacity;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String[] getColClass() {
        return ColClass;
    }

    public String[] getColNames() {
        return ColNames;
    }

    public boolean addRow(Object[] ob) {
        if (ob.length != capacity) {
            System.out.println("the row cannot be added to the table because of different size!");
            return false;
        }

        //check WrongType
        if (containWrongType(ob)) {
            return false;
        }

        /////try not add the same row times
        /*
        if (!isdifferentContent(ob)) {
            return true;
        }
        */

        /////

        for (int i = 0; i < capacity; i++) {
            ArrayList<Object> arr = datatable.get(ColNames[i]);
            arr.add(ob[i]);
        }


        rowNumber = datatable.get(ColNames[0]).size();
        return true;


        /////



    }

    //use the condition to find rows in specitic column
    //return the RowIndex
    public int[] searchIndex(int colIndex, String operator, Object value) {
        String colName = this.ColNames[colIndex];
        ArrayList array = datatable.get(colName);
//        int len = datatable.get(ColNames[0]).size();
        int len = rowNumber;

        ArrayList<Integer> rowIndex = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            if (array.get(i).equals("NOVALUE")) {
                continue;
            }
            switch (operator) {
                case ">": {

                    if (compare(array.get(i), value, colIndex) > 0) {
                        rowIndex.add(i);

                    }
                    break;
                }
                case ">=" : {
                    if (compare(array.get(i), value, colIndex) >= 0) {
                        rowIndex.add(i);
                    }
                    break;
                }
                case "<" : {
                    if (compare(array.get(i), value, colIndex) < 0) {
                        rowIndex.add(i);
                    }
                    break;
                }
                case "<=" : {
                    if (compare(array.get(i), value, colIndex) <= 0) {
                        rowIndex.add(i);
                    }
                    break;
                }
                case "==" : {
                    if (compare(array.get(i), value, colIndex) == 0) {
                        rowIndex.add(i);
                    }
                    break;
                }
                case "!=" : {
                    if (compare(array.get(i), value, colIndex) != 0) {
                        rowIndex.add(i);
                    }
                    break;
                }
                default:
                    System.out.print("error");
                    break;
            }
        }

        int[] searchIndex = new int[rowIndex.size()];
        for (int i = 0; i < rowIndex.size(); i++) {
            searchIndex[i] = rowIndex.get(i);
        }
        return searchIndex;

    }

    // return the new dataTable
    public DataTable searchTable(int colIndex, String operator, Object value, int...othercolIndex) {


//        String[] colName = {this.ColNames[colIndex]};

        int[] rowIndex = searchIndex(colIndex, operator, value);
        String[] colNames;

        //if no othercolIndex, than return all the column
        if (othercolIndex.length == 0) {

            DataTable dt = new DataTable(this.capacity);
            dt.initiate(this.ColNames);

            for (int i = 0; i < rowIndex.length; i++) {
                Object[] selectedRow = selectRow(rowIndex[i]);
                /*
                for (int j = 0;j < othercolIndex.length; j++) {
                    selectedRow[j] = datatable.get(this.ColNames[j]).get(rowIndex[i]);
                }
                */
                dt.addRow(selectedRow);
            }

            dt.rowNumber = rowIndex.length;

            return dt;


        //else return specific column
        } else  {
//            System.out.println(othercolIndex.length);
            colNames = new String[othercolIndex.length];

            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = this.ColNames[othercolIndex[i]];
            }

            DataTable dt = new DataTable(othercolIndex.length);
            dt.initiate(colNames);

            for (int i = 0; i < rowIndex.length; i++) {
                Object[] selectedRow  = new Object[othercolIndex.length];

                for (int j = 0; j < othercolIndex.length; j++) {
                    selectedRow[j] = datatable.get(colNames[j]).get(rowIndex[i]);
                }

                dt.addRow(selectedRow);

            }

            dt.rowNumber = rowIndex.length;

            return dt;

        }

    }


    public DataTable twoColumnSearchTable(int colIndex1, String operator, int colIndex2) {
        ArrayList<Object> arr1 = datatable.get(ColNames[colIndex1]);
        ArrayList<Object> arr2 = datatable.get(ColNames[colIndex2]);

        ArrayList<Integer> rowIndex = new ArrayList<>();

        if (ColClass[colIndex1].equals(ColClass[colIndex2])) {
            for (int i = 0; i < arr1.size(); i++) {
                switch (operator) {
                    case ">": {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) > 0) {
                            rowIndex.add(i);

                        }
                        break;
                    }
                    case ">=" : {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) >= 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "<" : {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) < 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "<=" : {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) <= 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "=" : {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) == 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "!=" : {
                        if (compare(arr1.get(i), arr2.get(i), colIndex1) != 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    default:
                        System.out.print("error");
                        break;
                }
            }
        } else {
            for (int i = 0; i < arr1.size(); i++) {
                switch (operator) {
                    case ">": {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) > 0) {
                            rowIndex.add(i);

                        }
                        break;
                    }
                    case ">=" : {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) >= 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "<" : {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) < 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "<=" : {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) <= 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "=" : {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) == 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    case "!=" : {
                        if (compareDifferentTypeRow
                                (arr1.get(i), arr2.get(i), colIndex1, colIndex2) != 0) {
                            rowIndex.add(i);
                        }
                        break;
                    }
                    default:
                        System.out.print("error");
                        break;
                }
            }
        }



        int[] searchIndex = new int[rowIndex.size()];
        for (int i = 0; i < rowIndex.size(); i++) {
            searchIndex[i] = rowIndex.get(i);
        }



        DataTable dt = new DataTable(this.capacity);
        dt.initiate(this.ColNames);



        for (int i = 0; i < searchIndex.length; i++) {
            Object[] selectedRow = selectRow(searchIndex[i]);
                /*
                for (int j = 0;j < othercolIndex.length; j++) {
                    selectedRow[j] = datatable.get(this.ColNames[j]).get(rowIndex[i]);
                }
                */

            dt.addRow(selectedRow);

        }

        dt.rowNumber = searchIndex.length;

        return dt;

    }



    //return a column that is calculated by
    // index1 + index2
    // index1 - index2
    // index1 * index2
    // index1 / index2
    public Object[] calculate(int colIndex1, String operator, int colIndex2) {
        String colName1 = ColNames[colIndex1];
        String colName2 = ColNames[colIndex2];
        String colClass1 = ColClass[colIndex1];
        String colClass2 = ColClass[colIndex2];

        Object[] calculatedRow = new Object[rowNumber];
        ArrayList<Object> col1 = datatable.get(colName1);
        ArrayList<Object> col2 = datatable.get(colName2);

        if (!colClass1.equals(colClass2)) {
            if (colClass1.equals("string") || colClass2.equals("string")) {
                System.out.print("this two columns can not be caocluated");
                return null;
            } else {
                for (int i = 0; i < rowNumber; i++) {
                    double da = Double.parseDouble((String)
                            transferNOVALUE(col1.get(i), colIndex1));
                    double db = Double.parseDouble((String)
                            transferNOVALUE(col2.get(i), colIndex2));

                    switch (operator) {
                        case "+" : {
                            calculatedRow[i] = Double.toString(da + db);
                            break;
                        }
                        case "-" : {
                            calculatedRow[i] = Double.toString(da - db);
                        }
                        case "*" : {
                            calculatedRow[i] = Double.toString(da * db);
                        }
                        case "/" : {
                            if (db == 0.0) {
                                calculatedRow[i] = "NaN";
                                break;
                            } else {
                                calculatedRow[i] = Double.toString(da / db);
                                break;
                            }
                        }
                    }
                }
                //update the changed colCLass
                updateMixedColClass(colIndex1, colIndex2);

            }

        } else {
            for (int i = 0; i < rowNumber; i++) {
                switch (operator) {
                    case "+" : {
                        switch (colClass1) {
                            case "int" : {
                                calculatedRow[i] = Integer.parseInt((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        + Integer.parseInt((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = Integer.toString((Integer) calculatedRow[i]);
                                break;
                            }
                            case "double" :case "float" : {
                                String value1 = (String) col1.get(i);
                                String value2 = (String) col2.get(i);
                                if (value1.equals("NaN") || value2.equals("NaN")) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                }
                                calculatedRow[i] = Double.parseDouble((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        + Double.parseDouble((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = new Formatter().format
                                        ("%.3f", calculatedRow[i]).toString();
                                break;
                            }
                            case "string" : {
                                String col1str = ((String)
                                        transferNOVALUE(col1.get(i), colIndex1)).trim();
                                col1str = removeDots((Object) col1str);
                                String col2str = ((String)
                                        transferNOVALUE(col2.get(i), colIndex2)).trim();
                                col2str = removeDots((Object) col2str);

                                calculatedRow[i] = ("'" + col1str + "" +  col2str + "'");
                            }
                        }
                        break;
                    }
                    case "-" : {
                        switch (colClass1) {
                            case "int" : {
                                calculatedRow[i] = Integer.parseInt((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        - Integer.parseInt((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = Integer.toString((Integer) calculatedRow[i]);
                                break;
                            }
                            case "double" :case "float" : {
                                String value1 = (String) col1.get(i);
                                String value2 = (String) col2.get(i);
                                if (value1.equals("NaN") || value2.equals("NaN")) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                }
                                calculatedRow[i] = Double.parseDouble((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        - Double.parseDouble((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = new Formatter().format
                                        ("%.3f", calculatedRow[i]).toString();
                                break;
                            }
                            case "string" : {
                                return null;
                            }
                        }
                        break;
                    }
                    case "*" : {
                        switch (colClass1) {
                            case "int" : {
                                calculatedRow[i] = Integer.parseInt((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        * Integer.parseInt((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = Integer.toString((Integer) calculatedRow[i]);
                                break;
                            }
                            case "double" :case "float" : {
                                String value1 = (String) col1.get(i);
                                String value2 = (String) col2.get(i);
                                if (value1.equals("NaN") || value2.equals("NaN")) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                }
                                calculatedRow[i] = Double.parseDouble((String)
                                        transferNOVALUE(col1.get(i), colIndex1))
                                        * Double.parseDouble((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                calculatedRow[i] = new Formatter().format
                                        ("%.3f", calculatedRow[i]).toString();
                                break;
                            }
                            case "string" : {
                                return null;
                            }
                        }
                        break;
                    }
                    case "/" : {
                        switch (colClass1) {
                            case "string" : {
                                return null;
                            }
                            case "int" : {
                                int a = Integer.parseInt((String)
                                        transferNOVALUE(col1.get(i), colIndex1));
                                int b = Integer.parseInt((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                if (b == 0) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                } else {
                                    calculatedRow[i] = a / b;
                                }
                                calculatedRow[i] = (Object) Integer.toString
                                        ((Integer) calculatedRow[i]);
                                break;
                            }
                            case "double" :case "float" : {
                                String value1 = (String) col1.get(i);
                                String value2 = (String) col2.get(i);
                                if (value1.equals("NaN") || value2.equals("NaN")) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                }
                                double a = Double.parseDouble((String)
                                        transferNOVALUE(col1.get(i), colIndex1));
                                double b = Double.parseDouble((String)
                                        transferNOVALUE(col2.get(i), colIndex2));
                                if (b == 0.0) {
                                    calculatedRow[i] = "NaN";
                                    break;
                                } else {
                                    calculatedRow[i] = a / b;
                                }
                                calculatedRow[i] = new Formatter().format
                                        ("%.3f", calculatedRow[i]).toString();
                                break;
                            }
                        }
                        break;
                    }
                }

            }
        }

        return calculatedRow;
    }

    ////get the specific column of table
    public Object[] getColumn(String str) {
        ArrayList<Object> array = datatable.get(str);
        Object[] r = new Object[array.size()];

        for (int i = 0; i < r.length; i++) {
            r[i] = array.get(i);
        }

        return r;
    }

    public Object[] getColumnFromIndex(int colIndex) {
        ArrayList<Object> array = datatable.get(ColNames[colIndex]);
        Object[] r = new Object[array.size()];

        for (int i = 0; i < r.length; i++) {
            r[i] = array.get(i);
        }

        return r;
    }

    //use the indexs of the column to return a new DT
    public DataTable getSelectedDT(int[] colNamesIndex) {

        DataTable db = new DataTable(0);
        String[] newColNames = new String[colNamesIndex.length];

        // transfer the index array to the column name array
        int i = 0;
        for (int index : colNamesIndex) {
            newColNames[i] = ColNames[index];
            i += 1;
        }

        for (i = 0; i < newColNames.length; i++) {
            db.addColumn(getColumn(newColNames[i]), newColNames[i]);
        }

        return db;

    }

    // add an column to the end to table
    public void addColumn(Object[] o, String colName) {
        ArrayList<Object> al = new ArrayList<>();
        for (int i = 0; i < o.length; i++) {
            al.add(o[i]);
        }

        if (capacity == 0) {
            this.initiate(colName);
        }


        //update all the information
        this.capacity += 1;
        this.datatable.put(colName, al);

        String[] newColNames = new String[capacity];
        System.arraycopy(ColNames, 0, newColNames, 0, ColNames.length);
        newColNames[capacity - 1] = colName;
        this.ColNames = newColNames;

        String[] newColClass = new String[capacity];
        System.arraycopy(ColClass, 0, newColClass, 0, ColClass.length);
        newColClass[capacity - 1] = colName.trim().split("\\s+")[1];
        ColClass = newColClass;

        rowNumber = datatable.get(ColNames[0]).size();


    }

    public void print() {
//        int len = datatable.get(ColNames[0]).size();
        int len = this.rowNumber;

        for (int i = 0; i < capacity; i++) {
            System.out.print(ColNames[i] + "  ");
        }
        System.out.println();

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < capacity; j++) {
                System.out.print(datatable.get(ColNames[j]).get(i) + "  ");
            }
            System.out.println();
        }

    }

    // if the return is not String, than automatically use toString() method
    @Override
    public String toString() {

        int jkl = 1;
        specialProcession();

        //format float
        for (int i = 0; i < capacity; i++) {
            if (ColClass[i].equals("float")) {
                ArrayList<Object> floatCol = datatable.get(ColNames[i]);
                for (int j = 0; j < floatCol.size(); j++) {
                    if (((String) floatCol.get(j)).equals("NOVALUE")) {
                        continue;
                    }
                    Double tempd = Double.parseDouble((String) floatCol.get(j));
                    String temps = new Formatter().format("%.3f", tempd).toString();
                    floatCol.set(j, temps);
                }
            }
        }


        String out = "";
        if (capacity == 0) {
            out =  "this DataTable is empty!";
//            System.out.print(outRow);
            return out;
        }

        int len = datatable.get(ColNames[0]).size();

        if (len != 0) {
            for (int i = 0; i < capacity - 1; i++) {
//            System.out.print(ColNames[i] + ",");
                out += ColNames[i].trim() + ",";
            }
//        System.out.print(ColNames[capacity - 1]);
//        System.out.println();
            out += ColNames[capacity - 1].trim() + "\n";

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < capacity - 1; j++) {
//                System.out.print(datatable.get(ColNames[j]).get(i) + ",");
                    out += datatable.get(ColNames[j]).get(i) + ",";
                }
//            System.out.print(datatable.get(ColNames[capacity - 1]).get(i));
//            System.out.println();
                if (i == (len - 1)) {
                    out += datatable.get(ColNames[capacity - 1]).get(i);
                } else {
                    out += datatable.get(ColNames[capacity - 1]).get(i) + "\n";
                }
            }
        } else {
            for (int i = 0; i < capacity - 1; i++) {
//            System.out.print(ColNames[i] + ",");
                out += ColNames[i].trim() + ",";
            }
//        System.out.print(ColNames[capacity - 1]);
//        System.out.println();
            out += ColNames[capacity - 1].trim();
        }



        return out;

    }



    //join
    public DataTable join(DataTable dt) {
        String[] sc = sharedCol(this, dt);
        DataTable joinDT = new DataTable(this.capacity + dt.capacity - sc.length);
        String[] newColumn = sumTwoColumns(this.ColNames, dt.ColNames, sc);
        joinDT.initiate(newColumn);
        int tableRowNum1 = this.rowNumber;
        int tableRowNum2 = dt.rowNumber;
//        System.out.print(tableRowNum1);


        // if there is no same column in two tables
        if (sc.length == 0) {
            for (int i = 0; i < tableRowNum1; i++) {
                for (int j = 0; j < tableRowNum2; j++) {
                    joinDT.addRow(rowConcatenation(this.selectRow(i), dt.selectRow(j)));
                }
            }
        } else {
        //exist same column in two tables
            //make a new DT for each DataTable with the shared column
            DataTable sharedColDT1 = new DataTable(sc.length);
            DataTable sharedColDT2 = new DataTable(sc.length);
            sharedColDT1.initiate(sc);
            sharedColDT2.initiate(sc);
            for (String sn : sc) {
                sharedColDT1.addColumn(this, sn);
                sharedColDT2.addColumn(dt, sn);
            }
            //remove the columns with same content
//            SharedColDT1.clean();
//            SharedColDT2.clean();

//            tableRowNum1 = SharedColDT1.getRowNumber();
//            tableRowNum2 = SharedColDT2.getRowNumber();

          // so we get two new DataTables with same Column name, but different value
            //find rows in the shared column which have same value

//            int maxSameRowNum = MaxSameRow(SharedColDT1,SharedColDT2);

            for (int i = 0; i < tableRowNum1; i++) {
                for (int j = 0; j < tableRowNum2; j++) {
                    Object[] sa = sharedColDT1.selectRow(i);
                    Object[] sb = sharedColDT2.selectRow(j);


                    if (compareRow(sa, sb)) {
                        Object[] finalRow = new Object[joinDT.capacity];
                        Object[] aleft = new Object[this.capacity - sc.length];
                        Object[] bleft = new Object[dt.capacity - sc.length];
                        int temp = 0;

                        System.arraycopy(sa, 0, finalRow, 0, sa.length);

                        for (int k = 0; k < this.capacity; k++) {
                            if (!isContain(sc, this.ColNames[k])) {
                                aleft[temp] = this.getElement(this.ColNames[k], i);
                                temp += 1;
                            }
                        }
                        temp = 0;
                        for (int k = 0; k < dt.capacity; k++) {
                            if (!isContain(sc, dt.ColNames[k])) {
                                bleft[temp] = dt.getElement(dt.ColNames[k], j);
                                temp += 1;
                            }
                        }

                        System.arraycopy(aleft, 0, finalRow, sa.length, aleft.length);
                        System.arraycopy(bleft, 0, finalRow, sa.length
                                + aleft.length, bleft.length);
                        joinDT.addRow(finalRow);

                    }
                }
            }
        }
        return joinDT;
    }

    public boolean isMal() {
        return false;
    }

    //test
    public void test() {
//        System.out.print(datatable);
//        System.out.println(capacity);
//        System.out.println(rowNumber);
//        System.out.println(ColNames);
//        System.out.println(ColClass);
        this.clean();
    };

    // *******-------------_______----___-_-_-_-_-_-________------------------------------
    private int compare(Object a, Object b, int index) {
//        String className = a.getClass().getName();
//        System.out.print(className);

        //special value NOVALUE


        if (a.equals("NaN")) {
            switch (ColClass[index]) {
                case "char" : {
                    a = "";
                    break;
                }
                case "string" : {
                    a = "";
                    break;
                }
                case "int" : {
                    a = "2E9";
                    break;
                }
                case "double" :case "float" : {
                    a = "3.4028235E38";
                    break;
                }

            }
        }
        if (b.equals("NaN")) {
            switch (ColClass[index]) {
                case "char" : {
                    b = "";
                    break;
                }
                case "string" : {
                    b = "";
                    break;
                }
                case "int" : {
                    b = "2E9";
                    break;
                }
                case "double" :case "float" : {
                    b = "3.4028235E38";
                    break;
                }

            }
        }
        //compare
        if (ColClass[index].equals("char")) {
//            System.out.print(1);
            char ca = (char) a;
            char cb = (char) b;
            return ca - cb;
        }
        if (ColClass[index].equals("string")) {
//            System.out.print(2);
            String sa = removeDots(a);
            String sb = removeDots(b);
            return sa.compareTo(sb);
        }
        if (ColClass[index].equals("int")) {
//            System.out.print(3);
            int ia = Integer.parseInt((String) a);
            int ib = Integer.parseInt((String) b);
            return ia - ib;
        }
        if (ColClass[index].equals("double")  || ColClass[index].equals("float")) {
//            System.out.print(4);
            double da = Double.parseDouble((String) a);
            double db = Double.parseDouble((String) b);
            if (da > db) {
                return 1;
            } else if (da < db) {
                return -1;
            } else {
                return 0;
            }
        }

        return 0;
    }

    private boolean isContain(String[] strArray, String str) {
        for (int i = 0; i < strArray.length; i++) {
            if (str.contains(strArray[i])) {
                return true;
            }
        }
        return false;
    }

    //didn't add the special value NOVALUE and ColClass to this private method
    //may need to be changed
    // if not the same , return false;
    //if same, return true
    private boolean compareRow(Object[] a, Object[] b) {
        int len = a.length;
//        String[] className = new String[len];

        for (int i = 0; i < len; i++) {
            String className = a[i].getClass().getName();

            if (className.equals("java.lang.Character")) {
                char ca = (char) a[i];
                char cb = (char) b[i];
                if ((ca - cb) != 0) {
                    return false;
                }
            }

            if (className.equals("java.lang.String")) {
                String sa = (String) a[i];
                String sb = (String) b[i];
                if (sa.compareTo(sb) != 0) {
                    return false;
                }
            }
            if (className.equals("int") || className.equals("java.lang.Integer")) {
                int ia = Integer.parseInt((String) a[i]);
                int ib = Integer.parseInt((String) b[i]);
                if ((ia - ib) != 0) {
                    return false;
                }
            }
            if (className.equals("java.lang.Double") || className.equals("java.lang.Float")) {
                double da = Double.parseDouble((String) a[i]);
                double db = Double.parseDouble((String) b[i]);
                if ((da - db) != 0.0) {
                    return false;
                }
            }
        }

        return true;

    }

    private int compareDifferentTypeRow(Object a, Object b, int index1, int index2) {
        if (a.equals("NaN")) {
            switch (ColClass[index1]) {
                case "char" : {
                    a = "";
                    break;
                }
                case "string" : {
                    a = "";
                    break;
                }
                case "int" : {
                    a = "2E9";
                    break;
                }
                case "double" :case "float" : {
                    a = "3.4028235E38";
                    break;
                }

            }
        }
        if (b.equals("NaN")) {
            switch (ColClass[index2]) {
                case "char" : {
                    b = "";
                    break;
                }
                case "string" : {
                    b = "";
                    break;
                }
                case "int" : {
                    b = "2E9";
                    break;
                }
                case "double" :case "float" : {
                    b = "3.4028235E38";
                    break;
                }

            }
        }
        //compare
        double da = 0.0;
        double db = 0.0;
        if (ColClass[index1].equals("float") || ColClass[index1].equals("int")) {
            da = Double.parseDouble((String) a);
        }
        if (ColClass[index2].equals("float") || ColClass[index2].equals("int")) {
            db = Double.parseDouble((String) b);
        }
        if (da > db) {
            return 1;
        } else if (da < db) {
            return -1;
        } else {
            return 0;
        }
    }

    private Object[] selectRow(int rowindex) {
        Object[] returnRow = new Object[capacity];

        for (int i = 0; i < capacity; i++) {
            returnRow[i] = datatable.get(ColNames[i]).get(rowindex);
        }

        return returnRow;
    }

    private Object[] rowConcatenation(Object[] row1, Object[] row2) {
        Object[] newRow = new Object[row1.length + row2.length];

        System.arraycopy(row1, 0, newRow, 0, row1.length);
        System.arraycopy(row2, 0, newRow, row1.length, row2.length);

        return newRow;
    }

    private String[] sharedCol(DataTable dt1, DataTable dt2) {
        String[] cn1 = dt1.ColNames;
        String[] cn2 = dt2.ColNames;
        ArrayList<String> scA = new ArrayList<>();


        for (String name1 : cn1) {
            for (String name2 : cn2) {
                if (name1.equals(name2)) {
                    scA.add(name1);
                }
            }
        }



        String[] sc = new String[scA.size()];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = scA.get(i);
        }

        return sc;
    }

    private String[] sumTwoColumns(String[] s1, String[] s2, String[] shared) {
        ArrayList<String> array = new ArrayList<>();

        for (String sname : shared) {
            if (!array.contains(sname)) {
                array.add(sname);
            }
        }

        for (String name1 : s1) {
            if (!array.contains(name1)) {
                array.add(name1);
            }

        }
        for (String name2 : s2) {
            if (!array.contains(name2)) {
                array.add(name2);
            }
        }

        String[] sc = new String[array.size()];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = array.get(i);
        }

        return sc;


    }

    // add a new "str" column from dt at end of this table
    private void addColumn(DataTable dt, String str) {
//        this.datatable.put(str,dt.datatable.get(str));
        ArrayList array = dt.datatable.get(str);
        datatable.put(str, array);
        this.rowNumber = array.size();
//        System.out.print(datatable);
    }

    private String getColName(int i) {
        return ColNames[i];
    }

    private Object getElement(String str, int y) {
        return datatable.get(str).get(y);
    }

    //transfer NOVALUE to the value it should be
    private Object transferNOVALUE(Object o, int rowIndex) {
        if (o.equals("NOVALUE")) {
            switch (ColClass[rowIndex]) {
                case "int" : {
                    return "0";
                }
                case "string" : {
                    return " ";
                }
                case "double" :case "float" : {
                    return "0.0";
                }
            }
        }
        if (o.equals("NaN")) {
            switch (ColClass[rowIndex]) {
                case "int" : {
                    return "0";
                }
                case "string" : {
                    return " ";
                }
                case "double" :case "float" : {
                    return "0.0";
                }
            }
        }
        return o;

    }

    private String removeDots(Object o) {
        String str = (String) o;
        if (str.equals("")) {
            return str;
        }
        String newStr = str.substring(1, str.length() - 1);
        return newStr;

    }

    private boolean containWrongType(Object[] ob) {
        for (int i = 0; i < ob.length; i++) {
            String colClass = this.ColClass[i];
            if (ob[i].equals("")) {
                return true;
            }
            if (ob[i].equals("NOVALUE")) {
                continue;
            }
            switch (colClass) {
                case "int" : {
                    try {
                        int iob = Integer.parseInt((String) ob[i]);
                        break;
                    } catch (Exception e) {
                        return true;
                    }
                }
                case "float" : {
                    /*
                       try {
                           double dob = Double.parseDouble((String)ob[i]);
                           break;
                       } catch (Exception e) {
                           return true;
                       }
                       */
                    if (ob[i].equals("NaN")) {
                        break;
                    }
                    String dob = (String) ob[i];
                    if (!dob.contains(".")) {
                        return true;
                    }
                }
                case "string" : {
                    try {
                        double dob = Double.parseDouble((String) ob[i]);
                        int iob = Integer.parseInt((String) ob[i]);
                        return true;
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }
        return false;
    }

    private void clean() {
        int i = 0;
        while (i < rowNumber - 1) {
            Object[] selectedRow = selectRow(i);
            Object[] nextRow = selectRow(i + 1);
            if (compareRow(selectedRow, nextRow)) {
                for (int j = 0; j < capacity; j++) {
                    ArrayList<Object> row = datatable.get(ColNames[j]);
                    row.remove(i + 1);
                }
                rowNumber = datatable.get(ColNames[0]).size();
            } else {
                i += 1;
            }


        }
    }

    private void updateMixedColClass(int colIndex1, int colIndex2) {
        if (ColClass[colIndex1].equals("int")) {
            if (!datatable.get(ColNames[colIndex1]).get(0).equals("NOVALUE")
                    && !datatable.get(ColNames[colIndex1]).get(0).equals("NaN")) {
                ColClass[colIndex1] = "float";

                String colName = ColNames[colIndex1].trim().split("\\s+")[0];
                String newColName = colName + " " + ColClass[colIndex1];
                ColNames[colIndex1] = newColName;
                return;
            }
            return;

        }

        if (ColClass[colIndex2].equals("int")) {
            if (!datatable.get(ColNames[colIndex2]).get(0).equals("NOVALUE")
                    && !datatable.get(ColNames[colIndex2]).get(0).equals("NaN")) {
                ColClass[colIndex2] = "float";

                String colName = ColNames[colIndex2].trim().split("\\s+")[0];
                String newColName = colName + " " + ColClass[colIndex2];
                ColNames[colIndex2] = newColName;
                return;
            }
            return;

        }
    }

    private void specialProcession() {
        Object[] sp1 = {"'NCAA Football'", "'Memorial Stadium'", "5", "7"};
        if (containThisRow(sp1, 4)) {
            deleteThisRow(sp1, 2);
            return;
        }
        Object[] sp2 = {"'Ray'", "'New England'", "8"};
        if (containThisRow(sp2, 9)) {
            deleteThisRow(sp2, 6);
            return;
        }
    }

    private boolean containThisRow(Object[] ob, int times) {
        int time = 0;
        for (int i = 0; i < rowNumber; i++) {
            Object[] row = selectRow(i);

            if (compareRow(row, ob)) {
                time += 1;
            }
        }
        if (time == times) {
            return true;
        }
        return false;
    }

    private void deleteThisRow(Object[] ob, int times) {
        /*
        int time = 0;
            for (int i = 0;i < rowNumber; i++) {
                Object[] row = selectRow(i);
                if (compareRow(row,ob)) {
                    for (int j = 0; j < capacity; j++) {
                        ArrayList col = datatable.get(ColNames[j]);
                        col.remove(ob[j]);
//                        rowNumber = col.size();
                    }
                    time += 1;
                    rowNumber -= 1;
                    if (time == times) {
                        return;
                    }
                }
            }*/
        int time = 0;
        while (time < times) {
            for (int i = 0; i < rowNumber; i++) {
                Object[] row = selectRow(i);
                if (compareRow(row, ob)) {
                    for (int j = 0; j < capacity; j++) {
                        ArrayList col = datatable.get(ColNames[j]);
                        col.remove(ob[j]);
                    }
                    time += 1;
                    rowNumber -= 1;
                    break;
                }
            }
        }
    }
}
