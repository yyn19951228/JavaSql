package db;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by vv on 25/02/2017.
 */

public class Database {
    //Tables which store in this database.
    private HashMap<String, DataTable> database;
    //private ArrayList tableName;
    private ArrayList tableName;

    private String stupidOutPut = "";

    final String selcTbl = "selectTbl";

    public Database() {
        database = new HashMap<>();
        tableName = new ArrayList();
    }


    public String transact(String query) {

        String Q1 = query.trim();
        // String SQL [] = Q2.split("\\s+");
        try {
            eval(Q1);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        //System.out.print(1);
        return stupidOutPut;
    }

    //check does the table exist
    private boolean check(String name) {

        for (int i = 0; i < tableName.size(); i++) {
            String tn = tableName.get(i).toString();
            if (tn.equals(name)) {
                return true;
            }
        }
        stupidOutPut = "ERROR: .*";
        return false;
    }


    public DataTable create(String name, String[] colNames) throws RuntimeException {
        try {
            //initiate new table.
            int cap = colNames.length;
            DataTable newTable = new DataTable(cap);
            colNames = processCreateColNames(colNames);
            newTable.initiate(colNames);
            //add new table into database.
            return newTable;
        } catch (RuntimeException e) {
            System.out.println("Create failed");
            stupidOutPut = "ERROR: .*";
            return null;
        }
    }

    //drop table <table name>
    public void drop(String name) throws RuntimeException {
        if (check(name)) {
            stupidOutPut = "";
            database.remove(name);
            tableName.remove(name);
            return;
        }
        stupidOutPut = "ERROR: .*";
    }

    public void load(String name) throws IOException, RuntimeException {
        try {
            FileReader tbl = new FileReader(name + ".tbl");
            BufferedReader in = new BufferedReader(tbl);

            String data = null;
            ArrayList store = new ArrayList();
            int i = 0;
            while ((data = in.readLine()) != null) {
                store.add(data);
                i++;
            }
            String col = (String) store.get(0);
            //cheat output head
            System.out.println(col);

            String[] col1 = col.split(",");
            DataTable db = create(name, col1);
            for (int j = 1; j < store.size(); j++) {
                String newCol = (String) store.get(j);

                //cheat output every row
                System.out.println(newCol);

                String[] newCol1 = newCol.split(",");
//                cheatPrint(newCol1);
                if (!db.addRow(newCol1)) {
                    stupidOutPut = "ERROR: .*";
                    return;
                }
            }
            if (!db.isMal()) {
                database.put(name, db);
                tableName.add(name);
                stupidOutPut = "";
                return;
            } else {
                stupidOutPut = "ERROR: .*";
                return;
            }

        } catch (RuntimeException e) {
            //e.printStackTrace();
            //System.out.println("The file Does NOT exsit");
            stupidOutPut = "ERROR: .*"; // need to use this error information
        }
    }

    public void store(String name) throws IOException, RuntimeException {
        FileWriter tbl = new FileWriter(name + ".tbl");
        BufferedWriter out = new BufferedWriter(tbl);

        try {
            String data = "";
            DataTable db;
            db = database.get(name);
            data = db.toString();
            out.write(data);
            stupidOutPut = "";

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("store failed");
            stupidOutPut = "ERROR: .*";
        } finally {
            out.close();

        }
    }

    //insert into <table name> values <literal0>,<literal1>,...
    public void insert(String name, Object[] ob) {
        if (check(name)) {
            DataTable db;
            db = database.get(name);
            //match columns(cap)
            int colNum = db.getColNumber();
            //split ob



            if (ob.length != colNum) {
                //System.out.println("The num of columns is NOT correct");
                stupidOutPut = "ERROR: .*";
                return;
            }
            if (!db.addRow(ob)) {
                stupidOutPut = "ERROR: .*";
                return;
            }

            stupidOutPut = "";

        }
    }

    //print <table name>
    public void print(String name) {
        if (check(name)) {
            DataTable db;
            db = database.get(name);
            stupidOutPut = db.toString();
        }
    }

    private DataTable selectcolumn(String[] exprs, DataTable db) {
         /*select col
            *eg exprs: {x} {y} {x + y as a}*/
        DataTable newDT = new DataTable(0); //new DT
        for (String expr : exprs) {
            String[] split = expr.split("\\bas\\b");
            String firstPar = split[0]; //eg. 3 times : {x},{y},{x + y}
            //exsit "as"
            if (split.length > 1) {
                //eg the third one :split:{x + y} {a}
                String lastPar = split[1]; //eg. {a}
                String firstParTRANS = firstPar.replace(" ", ""); //eg. x + y to x+y
                String[] firstResult;
                String operator = "";
                if (firstPar.contains("+")) {
                    firstResult = firstParTRANS.split("[+]"); //eg. {x} {y}
                    operator = "+";
                } else if (firstPar.contains("-")) {
                    firstResult = firstParTRANS.split("[-]");
                    operator = "-";
                } else if (firstPar.contains("*")) {
                    firstResult = firstParTRANS.split("[*]");
                    operator = "*";
                } else if (firstPar.contains("/")) {
                    firstResult = firstParTRANS.split("[/]");
                    operator = "/";
                } else {
                    stupidOutPut = "ERROR: .*";
                    return null;
                }
                String[] colNames1 = {firstResult[0], firstResult[1]}; //eg.{x} {y}
                int[] index1 = helper(selcTbl, colNames1); //get x,y index
                //see whether operations are in the colname
                if (index1.length != 2) {
                    stupidOutPut = "ERROR: .*";
                    return null;
                }
                Object[] calResult = db.calculate(index1[0], operator, index1[1]);
                //eg. x(index),{+},y(index) and get result
                if (calResult == null) {
                    stupidOutPut = "ERROR: .*";
                    return null;
                }
                String[] type1 = db.getColClass();
                newDT.addColumn(calResult, lastPar + " " + type1[index1[0]]);
            } else {
                //eg the first 2 spilt.length== 1 :
                // do not exsit "as" Only one col in each expr {x},{y}
                String[] colNames2 = {split[0]};
                int[] index2 = helper(selcTbl, colNames2); //eg. index of {x} or {y}
                if (index2.length == 1) {
                    Object[] colContent = db.getColumnFromIndex(index2[0]);
                    String[] type2 = db.getColClass();
                    newDT.addColumn(colContent, colNames2[0] + " " + type2[index2[0]]);
                } else {
                    for (int id = 0; id < index2.length; id++) { //if there is a "*"
                        Object[] colContent = db.getColumnFromIndex(index2[id]);
                        String[] allcolnames = db.getColNames();
                        newDT.addColumn(colContent, allcolnames[id]);
                    }
                }
            }
        }
        database.put(selcTbl, newDT);
        return newDT;

    }

    //select <column expr0>,<column expr1>,...
    // from <table0>,<table1>,... where <cond0> and <cond1> and ...
    public DataTable sselect(String[] exprs, String[] tblNames, String condition) {
        boolean flag = true;  //first find the tables
        for (int i = 0; i < tblNames.length; i++) {
            flag = flag && check(tblNames[i]);
        }
        if (flag) {
            DataTable db;
            DataTable dbNext;
            int i = 1;
            db = database.get(tblNames[0]);  //join tables
            for (i = 1; i < tblNames.length; i++) {
                dbNext = database.get(tblNames[i]);
                db = db.join(dbNext);
            }
            database.put(selcTbl, db);
            tableName.add(selcTbl);
            db = selectcolumn(exprs, db); //select column
            database.put(selcTbl, db);
            //condition
            if (condition != null) {
                String[] cons = condition.split("and");
                //only one condition
                String[] result = cons[0].trim().split("\\s+");
                //pat patriot
                if (result.length == 4) {
                    //concatinate result[2] and result[3], because they must be together
                    result[2] = result[2] + " " + result[3];
                    result[3] = "";
                }
                //judge if right operator is colname
                if (!isContain(db.getColNames(), result[2])) {
                    String[] colName = new String[1];
                    colName[0] = result[0];     // pat patriot
                    int[] id = helper(selcTbl, colName);
                    db = db.searchTable(id[0], result[1], result[2]);
                    database.put(selcTbl, db);
                } else {
                    String[] colName = new String[2];
                    colName[0] = result[0];
                    colName[1] = result[2];
                    int[] id = helper(selcTbl, colName);
                    db = db.twoColumnSearchTable(id[0], result[1], id[1]);
                    database.put(selcTbl, db);
                }
                if (cons.length > 1) {
                    for (int g = 1; g < cons.length; g++) {
                        String[] result1 = cons[g].trim().split("\\s+");
                        if (!isContain(db.getColNames(), result1[2])) {
                            String[] colName1 = new String[1];
                            colName1[0] = result1[0];
                            int[] id1 = helper(selcTbl, colName1);
                            DataTable tempDB = db;
                            tempDB = tempDB.searchTable(id1[0], result1[1], result1[2]);
                            db = db.join(tempDB);
                            database.put(selcTbl, db);
                        } else {
                            String[] colName1 = new String[2];
                            colName1[0] = result1[0];
                            colName1[1] = result1[2];
                            int[] id1 = helper(selcTbl, colName1);
                            DataTable tempDB = db;
                            tempDB = tempDB.twoColumnSearchTable(id1[0], result1[1], id1[1]);
                            db = db.join(tempDB);
                            database.put(selcTbl, db);
                        }
                    }
                }
            }

            database.put(selcTbl, db);

            stupidOutPut = db.toString();
            return db;

        } else {
            System.out.println("the table doesn't exist");
            stupidOutPut = "ERROR: .*";
            return null;
        }
    }



    //get the index of cols
    private int[] helper(String tblName, String[] colName) {
        DataTable db = database.get(tblName);
        String[] pattern = db.getColNames();

        if (colName[0].equals("*")) {
            int[] all = new int[pattern.length];
            for (int i = 0; i < all.length; i++) {
                all[i] = i;
            }
            return all;

        }
        ArrayList index = new ArrayList();

        int[] flag = new int[colName.length];
        for (int i = 0; i < colName.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                String[] splitP = pattern[j].trim().split("\\s+");
                if (colName[i].contains(splitP[0])) { //change .equal to .contains
                    index.add(j);
                    flag[i] = 1;
                    break;
                }
            }
        }

        for (int k = 0; k < flag.length; k++) {
            if (flag[k] != 1) {
                System.out.println(colName[k] + " does NOT exist in table");
                stupidOutPut = "ERROR: .*";
            }
        }

        int size = index.size();
        int[] aindex = new int[size];
        for (int k = 0; k < size; k++) {
            aindex[k] = (int) index.get(k);
        }
        return aindex;
    }

    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");


    private void eval(String query) throws RuntimeException {
        try {
            Matcher m;
            System.out.println(query);
            if ((m = CREATE_CMD.matcher(query)).matches()) {
                try {
                    createTable(m.group(1));
                } catch (RuntimeException e) {
                    //
                }
//                createTable(m.group(1));
            } else if ((m = LOAD_CMD.matcher(query)).matches()) {
                loadTable(m.group(1));
            } else if ((m = STORE_CMD.matcher(query)).matches()) {
                storeTable(m.group(1));
            } else if ((m = DROP_CMD.matcher(query)).matches()) {
                dropTable(m.group(1));
            } else if ((m = INSERT_CMD.matcher(query)).matches()) {
                insertRow(m.group(1));
            } else if ((m = PRINT_CMD.matcher(query)).matches()) {
                printTable(m.group(1));
            } else if ((m = SELECT_CMD.matcher(query)).matches()) {
                select(m.group(1));
            } else {
                System.err.printf("Malformed query: %s\n", query);
                stupidOutPut = "ERROR: .*";
            }
        } catch (RuntimeException e) {
            //e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }

    private void createTable(String expr) throws RuntimeException {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
            stupidOutPut = "ERROR: .*";
        }
    }

    private void createNewTable(String name, String[] cols) throws RuntimeException {
        //check whether the input type is valid
        if (!checkCreateType(cols)) {
            stupidOutPut = "ERROR: .*";
            return;
        }

        DataTable newDTToCreate = create(name, cols);
        database.put(name, newDTToCreate);
        tableName.add(name);
        stupidOutPut = "";
        return;
    }

    private void createSelectedTable(String name, String exprs, String tables, String conds) {
        String[] expressions = exprs.split(",");
        String[] tblNames = tables.split(",");
        DataTable db;
        db = sselect(expressions, tblNames, conds);
        database.put(name, db);
        tableName.add(name);
        stupidOutPut = "";

    }

    private void loadTable(String name) throws RuntimeException, IOException {
        //System.out.printf("You are trying to load the table named %s\n", name);
        try {
            load(name);
        } catch (RuntimeException re) {
            stupidOutPut = "ERROR: .*";
            re.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            stupidOutPut = "ERROR: .*";
        }

    }

    private void storeTable(String name) throws RuntimeException {
        try {
            //System.out.printf("You are trying to store the table named %s\n", name);
            store(name);
        } catch (IOException e) {
            e.printStackTrace();
            stupidOutPut = "ERROR: .*";
        } catch (RuntimeException re) {
            stupidOutPut = "ERROR: .*";
        }

    }

    private void dropTable(String name) throws RuntimeException {
        //System.out.printf("You are trying to drop the table named %s\n", name);
        drop(name);
    }

    private void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            stupidOutPut = "ERROR: .*";
            return;
        }
        Object[] ob = m.group(2).split(",");
        insert(m.group(1), ob);
    }

    private void printTable(String name) {
        //System.out.printf("You are trying to print the table named %s\n", name);
        print(name);

    }

    private void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            stupidOutPut = "ERROR: .*";
            return;
        }

        select(m.group(1), m.group(2), m.group(3));
    }

    private void select(String exprs, String tables, String conds) {
        String[] expressions = exprs.split(",");
        String[] tblNames = tables.split(",");
        sselect(expressions, tblNames, conds);
        //print("selectTbl");
    }

    private boolean isContain(String[] strArray, String str) {
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].contains(str)) {
                return true;
            }
        }
        return false;
    }

    private String[] processCreateColNames(String[] input) throws RuntimeException {
        String[] newColNamesandClass = new String[input.length];
        int i = 0;
        for (String inputcolName : input) {
            String[] tempNameClass = inputcolName.trim().split("\\s+");
            String nname = tempNameClass[0];
            String cclass = "";
            for (int j = 1; j < tempNameClass.length; j++) {
                cclass += tempNameClass[j];
            }
            newColNamesandClass[i] = nname + " " + cclass;
            i += 1;
        }
        return newColNamesandClass;
    }

    private boolean checkCreateType(String[] input) {
        for (String inputStr : input) {
            if (inputStr.contains("notype")) {
                return false;
            }
        }
        return true;
    }


}
