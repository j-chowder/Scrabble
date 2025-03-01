import java.sql.*;
import java.util.ArrayList;


public class Player {
    private String name;
    private ArrayList<String> inventory;
    private int[] letterCount;
    private int score;

    static String[] alphabet;

    public Player(String name){
        this.name = name;
        inventory = new ArrayList<String>();
        makeInventory(); // initializes inventory + letterCount + totalCount
        score = 0;
        letterCount = new int[27];
        setLetterCount();
        alphabet = new String[27];
        makeAlphabet();
    }
    public void makeInventory() {
//        makeInventoryLoop(2,"o");
//        makeInventoryLoop(2,"t");
//        makeInventoryLoop(2,"e");
//        makeInventoryLoop(2,"p");
//        makeInventoryLoop(2,"i");
        // blanks
        makeInventoryLoop(2, "?");

        // 1 point letters
        makeInventoryLoop(12,"e");
        makeInventoryLoop(9,"a");
        makeInventoryLoop(9,"i");
        makeInventoryLoop(8,"o");
        makeInventoryLoop(6,"n");
        makeInventoryLoop(6,"r");
        makeInventoryLoop(6,"t");
        makeInventoryLoop(4,"l");
        makeInventoryLoop(4,"s");
        makeInventoryLoop(4,"u");

        // 2 point letters
        makeInventoryLoop(4,"d");
        makeInventoryLoop(3,"g");

        // 3 point letters
        makeInventoryLoop(2,"b");
        makeInventoryLoop(2,"c");
        makeInventoryLoop(2,"m");
        makeInventoryLoop(2,"p");

        // 4 point letters
        makeInventoryLoop(2,"f");
        makeInventoryLoop(2,"h");
        makeInventoryLoop(2,"v");
        makeInventoryLoop(2,"w");
        makeInventoryLoop(2,"y");

        // 5 point letter
        makeInventoryLoop(1,"k");

        // 8 points
        makeInventoryLoop(1,"j");
        makeInventoryLoop(1,"x");

        // 10 points
        makeInventoryLoop(1,"q");
        makeInventoryLoop(1,"z");


    }
    public void makeInventoryLoop (int count, String letter){
        for(int i = 0; i < count; i++){
            inventory.add(letter);
        }
    }
    public ArrayList<String> getInventory(){
        return inventory;
    }
    public static void makeAlphabet(){
        Connection con = null;
        PreparedStatement query = null;
        String letter = null;

        for(int i = 0; i < alphabet.length; i++){
            try { // queries the letter that corresponds to the index
                // establishing a connection
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble","postgres","password");
                // creating a query
                query = con.prepareStatement("SELECT letter FROM letters WHERE index = ?");
                query.setInt(1, i);
                ResultSet rs = query.executeQuery();
                rs.next();
                letter = rs.getString(1);
                con.close();
            }
            catch (SQLException e) {
                return;
            }
            alphabet[i] = letter;
        }
    }
    public void addScore(int score){
        this.score += score;
    }

    public int getLetterCount(int index){
        return letterCount[index];
    }
    public void setLetterCount(){ // default -- start of the game.
        letterCount[0] = 2;
        letterCount[1] = 9;
        letterCount[2] = 2;
        letterCount[3] = 2;
        letterCount[4] = 4;
        letterCount[5] = 12;
        letterCount[6] = 2;
        letterCount[7] = 3;
        letterCount[8] = 2;
        letterCount[9] = 9;
        letterCount[10] = 1;
        letterCount[11] = 1;
        letterCount[12] = 4;
        letterCount[13] = 2;
        letterCount[14] = 6;
        letterCount[15] = 8;
        letterCount[16] = 2;
        letterCount[17] = 1;
        letterCount[18] = 6;
        letterCount[19] = 4;
        letterCount[20] = 6;
        letterCount[21] = 4;
        letterCount[22] = 2;
        letterCount[23] = 2;
        letterCount[24] = 1;
        letterCount[25] = 2;
        letterCount[26] = 1;
    }
    public void changeCount(String letter, String math){
        Connection con = null;
        PreparedStatement query = null;
        int index = -1;

        try { // need to change url, user, and/or password if different DBMS -- and JDBC -- was used.
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble","postgres","password");
            // creating a query
            query = con.prepareStatement("SELECT index FROM letters WHERE letter = ?");
            query.setString(1, letter);
            ResultSet rs = query.executeQuery();
            rs.next();
            index = rs.getInt(1);
            con.close();
        }
        catch (SQLException e) {
            return;
        }
        if(math.equals("dec")){
            letterCount[index] -= 1;
        }
        else {
            letterCount[index] += 1;
        }

    }
    public int getScore(){
        return score;
    }
    public String getName(){
        return name;
    }
}
