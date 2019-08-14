
import org.telegram.telegrambots.meta.api.objects.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Users {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://localhost:3306/langdb";
    private static final String user = "root";
    private static final String password = "";
    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;



    private static int id;
    private static String firstName;
    private static String userName;
    private static String languageCode;



    private static void setUser(User us){
        id =  us.getId();
        firstName = us.getFirstName();
        userName = us.getUserName();
        languageCode = us.getLanguageCode();
    }

    public static void startUser(User us){

        setUser(us);

        if (chekUs() == 1 ){

        }else if (chekUs() == 0){
            System.out.println("Польз с id "+ id + " не найден!");
            addUs();
        }

    }

    private static void addUs(){
        String query = "INSERT INTO `users`(`id`, `firstName`, `userName`, `languageCode`) VALUES ('"+ Integer.toString(id) +
                "','"+ firstName +
                "','"+ userName +
                "','"+ languageCode +"' )" ;
        System.out.println(query);

        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            stmt.executeUpdate(query);



        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }

        }

    }

    private static int chekUs(){
        String query = "select count(*) from users WHERE `id` = " + Integer.toString(id) ;
        int count = 0;
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                count = rs.getInt(1);

            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
        return count;
    }





}
