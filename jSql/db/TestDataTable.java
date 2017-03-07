package db;

import org.junit.Test;
/**
 * Created by yangyining on 22/02/2017.
 */
public class TestDataTable {

    @Test
    public void test() {


        String[] colName1 = {"A int", "B int", "S string", "Y string"};
        String[] colName2 = {"A int", "B int", "S string", "Z string"};

        Object[] r11 = {"7", "0", "a", "z"};
        Object[] r12 = {"7", "0", "a", "y"};
//        Object[] r12 = {"5","6","f"};
        Object[] r13 = {"1", "1", "b", "x"};


        Object[] r21 = {"7", "0", "a", "zz"};
        Object[] r22 = {"7", "0", "a", "yy"};
//        Object[] r22 = {"3","4","f"};
        Object[] r23 = {"2", "3", "c", "xx"};


        DataTable db1 = new DataTable(colName1.length);
        DataTable db2 = new DataTable(colName2.length);

        db1.initiate(colName1);
        db2.initiate(colName2);

        db1.addRow(r11);
        db1.addRow(r12);
        db1.addRow(r13);

        db2.addRow(r21);
        db2.addRow(r22);
        db2.addRow(r23);


//
        DataTable db3 = db2.join(db1);
        db3.print();


    }
    @Test
    public void testOther() {
        double a = 2.147483647E9;
        double b = 340282346638528860000000000000000000000.0;
        double c = 3.4028235E38;
        System.out.println();

        Object[] r11 = {"7", "0", "a", "z"};
        Object[] r12 = {"7", "0", "a", "z"};
//        Object[] r12 = {"5","6","f"};
        Object[] r13 = {"1", "1", "b", "x"};


        Object[] r21 = {"7", "0", "a", "zz"};
        Object[] r22 = {"7", "0", "a", "yy"};
//        Object[] r22 = {"3","4","f"};
        Object[] r23 = {"2", "3", "c", "xx"};

        System.out.println(r11.equals(r12));
    }


}
