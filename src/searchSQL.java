import java.sql.*;
public class searchSQL {

    public boolean check(String input){
        Connection con = null;
        PreparedStatement query = null;
        String name = null;

        try {
            // establishing a connection
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble","postgres","password");
            // creating a query
            query = con.prepareStatement("SELECT word FROM dictionary WHERE word = ?");
            query.setString(1,input);

            // getting what I want from the result set.
            ResultSet rs = query.executeQuery();
            rs.next(); // to navigate the pointer.
            name = rs.getString(1);
            con.close(); // close the connection
        }
        catch (SQLException e) { // in the event of the query returning 0 results (which means invalid attempt)

        }

        if(input.equals(name)){
            return true;
        }
        else{
            return false;
        }
    }
    public int checkScore(String letter){
        Connection con = null;
        PreparedStatement query = null;
        int score = 0;

        try {
            // establishing a connection -- need to change the parameters, if you change DBMS.
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble","postgres","password");
            // creating a query
            query = con.prepareStatement("SELECT points FROM letters WHERE letter = ?");
            query.setString(1,letter);

            // getting what I want from the result set.
            ResultSet rs = query.executeQuery();
            rs.next(); // to navigate the pointer.
            return rs.getInt(1);

        }
        catch (SQLException e) { // in the event of the query returning 0 results (which means invalid attempt)
            return 0;
        }
    }

}
