// For Mr. Phu
// Need to have SQL in order to play the game:
// there are two tables in one database:
// (1) table "dictionary" holds all the possible submissions in Scrabble.
// (2) table "letters" holds a map/relation between the letter, its index, its points,
//     the amount of the letters, and the URL for its picture.
//     I believe you need to alter the URL for the pictures within the table itself.
//     No code in here needs to be changed for the letters beyond the pictures and the connection.

// In order to connect Java with SQL, need to install JBDC. Each DBMS has their own JBDC "driver" that you need to install.
        // install JBDC then add it to the external libraries.
// I am using PostgreSQL -- if you choose to use a different DBMS, then code needs to be altered
// in Pictures and searchSQL. If you have a different username or password, then that also needs to be altered.

// Link for the csvs and the json for the databes.






import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartingMenu implements ActionListener {
    private JFrame frame;
    private JPanel pane, pane2, panel, top;
    private JButton[] title;
    private JButton play, mascotholder, close;
    private ImageIcon mascot;

    Pictures p = new Pictures();

    StartingMenu(){
        top = new JPanel();
        top.setPreferredSize(new Dimension(800, 200));
        top.setLayout(new FlowLayout());

        title = new JButton[8];
        for(int i = 0; i < title.length; i++){
            title[i] = new JButton();

        }
        p.setPicture(title[0], "s");
        top.add(title[0]);
        p.setPicture(title[1], "c");
        top.add(title[1]);
        p.setPicture(title[2], "r");
        top.add(title[2]);
        p.setPicture(title[3], "a");
        top.add(title[3]);
        p.setPicture(title[4], "b");
        top.add(title[4]);
        p.setPicture(title[5], "b");
        top.add(title[5]);
        p.setPicture(title[6], "l");
        top.add(title[6]);
        p.setPicture(title[7], "e");
        top.add(title[7]);
        top.revalidate();


        pane = new JPanel(new BorderLayout());
        play = new JButton();
        play.setBackground(new Color(0xFFFFFF));
        play.addActionListener(this); // allows the button to run an event when clicked.
        play.setFont(new Font("Eurostile", Font.BOLD, 100));
        play.setText("P L A Y");
        play.setActionCommand("Play");
        play.setFocusable(false); // removes the box around the text

        mascot = new ImageIcon("C:\\Users\\Jaden\\OneDrive - Temple City Unified School District\\Desktop\\APCS\\Scrabble\\src\\ScrabbleMacsot.jpg");
        mascotholder = new JButton();
        mascotholder.setIcon(mascot);
        pane.add(mascotholder, BorderLayout.CENTER);
        pane.add(play, BorderLayout.SOUTH);

        pane2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
        close = new JButton();
        close.setText("I don't want to play!");
        close.setBackground(new Color(0xFFFFFF));
        close.addActionListener(this); // allows the button to run an event when clicked.
        close.setFont(new Font("Eurostile", Font.BOLD, 20));
        close.setFocusable(false); // removes the box around the text
        close.setActionCommand("Close");
        pane2.add(close);

       panel = new JPanel(new BorderLayout(400, 260));

        panel.add(pane, BorderLayout.CENTER);
        panel.add(pane2, BorderLayout.SOUTH);

        frame = new JFrame();
        frame.add(panel);
        frame.add(top, BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    public static void main(String[] args) {
        StartingMenu a = new StartingMenu();
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Play")){
            Gameplay a = new Gameplay();
            frame.dispose();
        }
        else if(e.getActionCommand().equals("Close")){
            frame.dispose();
        }
    }
}

