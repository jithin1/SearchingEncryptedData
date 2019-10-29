
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import static java.lang.Character.compare;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Searcher1 {

    public Searcher1() {
        this.comp = new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        };
    }

    public static Map<Double, String> nonsort = new HashMap<Double, String>();
    public static File folder = new File("/home/ubuntu/doc3/");
    public static File[] listOfFiles = folder.listFiles();
    public static int noOfFiles = listOfFiles.length;
    public static ArrayList<String> filename = new ArrayList<String>();
    public static ArrayList<String> all = new ArrayList<String>();
    public static ArrayList<Docvector> document = new ArrayList<Docvector>();
    public static ArrayList<String> query = new ArrayList<String>();
    public static Comparator comp;

    private static void getQueryVector() {
        Docvector d = new Docvector();
        for (String s : all) {
            if (query.contains(s)) {
                d.vector.add(1);
            } else {
                d.vector.add(0);
            }
        }
        document.add(d);
    }

    private static void getTopDocuments() {
        CosineSimilarity con = new CosineSimilarity();
        int size = document.size();
        int j = 0;
        for (int i = 0; i < size - 1; i++) {
            double d = con.cosineSimilarity(document.get(i).vector, document.get(size - 1).vector);
            if (d != 0.0) {
                nonsort.put(d, document.get(i).name);
            }
        }
        TreeMap<Double, String> sorted = new TreeMap<>(comp);
        sorted.putAll(nonsort);
        Map<Double, String> decsort = sorted.descendingMap();
        printMap(decsort);
    }

    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("cosine similarity = " + entry.getKey()
                    + "     File = " + entry.getValue());
        }
    }

    public static class Docvector {

        String name;
        ArrayList<Integer> vector = new ArrayList();
    }

    public static void main(String args[]) throws SQLException, IOException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < noOfFiles; i++) {
            String name = listOfFiles[i].getName();
            int length = name.length();
            filename.add(name.substring(0, length - 4));
        }
        addallterm();
        all.remove("null");
        Collections.sort(all);
        addvecname();
        getQuery();
        getQueryVector();
        getTopDocuments();
        long endTime   = System.currentTimeMillis();
        long totalTime = (endTime - startTime)/1000;
        System.out.println("execution time "+totalTime+" seconds");
    }

    private static void addvecname() throws SQLException {
        Cryptcon con = new Cryptcon();
        for (int i = 0; i < noOfFiles; i++) {
            Docvector v = new Docvector();
            v.name = filename.get(i);
            Cryptcon.Select_new tfidf = con.selectVector(filename.get(i));
            for (String t : all) {
                char c = t.charAt(0);
                for (int j = 0; j < tfidf.term.size(); j++) {
                    int x = compare(c, tfidf.term.get(j).charAt(0));
                    if (x < 0) {
                        v.vector.add(0);
                        break;
                    }
                    if (t.equals(tfidf.term.get(j))) {
                        v.vector.add(tfidf.tfidf.get(j));
                        break;
                    }
                }
            }
            document.add(v);
        }
    }

    private static void getQuery() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("enter the query");
        String s = br.readLine();
        StringTokenizer str = new StringTokenizer(s, " ");
        while (str.hasMoreTokens()) {
            query.add(str.nextToken());
        }
    }

    private static void addallterm() throws SQLException {
        Cryptcon con = new Cryptcon();
        for (int i = 0; i < noOfFiles; i++) {
            ArrayList<String> term = new ArrayList();
            term = con.select(filename.get(i));
            for (String t : term) {
                if (!all.contains(t)) {
                    all.add(t);
                }
            }
        }
    }
}
