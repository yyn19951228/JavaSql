package db;

import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by VV on 2/25/17.
 */
public class TestDatabase {
    @Test
    public void test1() {
        String test =  "create table seasonRatios as select Wins/Losses as Ratio from teams";
        //String test1 = test.replace(","," ");
        String test2 = test.trim();
        String[] tests = test2.split("\\s+");
        String newS = "";
        for (int i = 0; i < tests.length; i++) {
            System.out.println(tests[i]);
        }

    }

    @Test
    public void test2() throws IOException {
        FileReader tbl = new FileReader("examples/t1.tbl");
        BufferedReader in = new BufferedReader(tbl);
        String data = null;
        ArrayList store = new ArrayList();
        int i = 0;
        while ((data = in.readLine()) != null) {
            //System.out.println(data);
            store.add(data);
            i++;
        }
        for (i = 0; i < store.size(); i++) {
            System.out.println(store.get(i));
        }

        String col = (String) store.get(1);
        String[] col1 = col.split(",");
        for (i = 0; i < col1.length; i++) {
            System.out.println(col1[i]);
        }

    }
    @Test
    public void test5() throws IOException {
        FileWriter tbl = new FileWriter("examples/jyw.tbl");
        BufferedWriter out = new BufferedWriter(tbl);
        String test = "Love you";
        out.write(test);
        out.close();
    }

    @Test
    public void test3() {
        String test = "Lastname as z";
        String[] tests = test.split("as ");
        for (int i = 0; i < tests.length; i++) {
            String[] result = tests[i].trim().split(" ");
            //System.out.println(tests[i]);
            for (String temp : result) {
                System.out.println(temp);
            }
        }

    }

    @Test
    public void test4() {
        String[] pattern = new String[3];
        pattern[0] = "y int";
        pattern[1] = "z int";
        pattern[2] = "x int";
        String[] colName = new String[2];
        colName[0] = "x";
        colName[1] = "y";
        int[] flag = new int[2];
        ArrayList index = new ArrayList();


        for (int i = 0; i < pattern.length; i++) {
            String[] splitP = pattern[i].split(" ");
            for (int j = 0; j < colName.length; j++) {
                if (splitP[0].equals(colName[j])) {
                    index.add(i);
                    flag [j] = 1;
                    System.out.println("yeah " + i);
                }
            }
        }
        for (int k = 0; k < flag.length; k++) {
            if (flag[k] != 1) {
                System.out.println(colName[k] + " not exit");

            }
        }
        int size = index.size();
        int[] aindex = new int[size];
        for (int k = 0; k < size; k++) {
            aindex[k] = (int) index.get(k);
            System.out.println(aindex[k]);
        }


    }

}
