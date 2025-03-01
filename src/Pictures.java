import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;

public class Pictures {
    private ImageIcon[] images;
    private ImageIcon[] scaledImages;

    private ImageIcon star;
    private JFrame frame;
    private JButton[][] buttons;

    public Pictures() {
        Connection con = null;
        PreparedStatement query = null;
        images = new ImageIcon[27];
        for(int i = 0; i < images.length; i++){
            try { // initializing each image with their letter -- corresponding to the index.
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble", "postgres", "password");
                query = con.prepareStatement("SELECT image FROM letters WHERE index = ?");
                query.setInt(1, i); // replaces the ? in the previous query with the assigned index.
                                                 // the index pertains to the position of the letter in the alphabet. (0 = blank, 1 = a... 26 = z)

                ResultSet rs = query.executeQuery();
                rs.next();
                images[i] = new ImageIcon(rs.getString("image"));
                images[i].setDescription(i + "");
                con.close();
            } catch (SQLException e) {

            }

        }
        star = new ImageIcon("C:\\Users\\Jaden\\OneDrive - Temple City Unified School District\\Desktop\\APCS\\Scrabble\\src\\ScrabbleStar.png");

    }
        public void setPicture (JButton tile, String letter){ // takes the letter, queries its respective index, then sets the tile to the letter's image.
            Connection con = null;
            PreparedStatement query = null;
            try {
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble", "postgres", "password");
                query = con.prepareStatement("SELECT index FROM letters WHERE letter = ?");
                query.setString(1, letter); // replaces the ? in the previous query with the assigned letter

                ResultSet rs = query.executeQuery();
                rs.next();
                tile.setIcon(images[rs.getInt("index")]);
                tile.setName(letter);


                con.close();
            } catch (SQLException e) {

            }

        }
        public void updateIconSizes (JButton tile){
            scaledImages = new ImageIcon[27];
            int buttonWidth = tile.getWidth();
            int buttonHeight = tile.getHeight();

         for(int i = 0; i < scaledImages.length; i++){
             try{
                 Image scaledimage = images[i].getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                 scaledImages[i] = new ImageIcon(scaledimage);
                 ImageIcon currentIcon = (ImageIcon) tile.getIcon();
                 if ((currentIcon.getDescription().equals(i + ""))) { // if they share the same index, replace with scaled version.
                     tile.setIcon(scaledImages[i]);
                 }
             }
             catch(NullPointerException e ){ // this occurs due to the board being initially blank.

             }

         }
        }
        public ImageIcon getStar(){
            return star;
        }

        public static void main (String[]args){
            Pictures a = new Pictures();
        }

}
