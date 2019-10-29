
import java.sql.*;
import java.util.ArrayList;

public class Cryptcon {

    public class Select_new {

        ArrayList<String> term = new ArrayList<String>();
        ArrayList<Integer> tfidf = new ArrayList<Integer>();
    }

    public Statement stmt;
    Connection con;

    public Cryptcon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://54.208.233.38:3307/test", "root", "letmein");
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void create(String name) throws SQLException {
        System.out.println("create table " + name + "(term varchar(20),tfidf int)");
        stmt.execute("create table " + name + "(term varchar(20),tfidf int)");
    }

    public void insert(String name, String term[], ArrayList<Integer> a) throws SQLException {
        for (int i = 0; i < term.length; i++) {

            stmt.executeUpdate("insert into " + name + " values('" + term[i] + "'," + a.get(i) + ")");
        }
    }


    public ArrayList<String> select(String name) throws SQLException {
        ArrayList<String> ip = new ArrayList();
        ResultSet rs = stmt.executeQuery("select *from " + name);
        while (rs.next()) {
            ip.add(rs.getString(1));
        }
        return ip;
    }

    public Select_new selectVector(String name) throws SQLException {
        Select_new termtf = new Select_new();
        ResultSet rs = stmt.executeQuery("select *from " + name);
        while (rs.next()) {
            termtf.term.add(rs.getString(1));
            termtf.tfidf.add(rs.getInt(2));
        }
        return termtf;
    }

    public void close() throws SQLException {
        con.close();
    }
}
