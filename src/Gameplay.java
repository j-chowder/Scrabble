import java.util.ArrayList;
import java.util.Arrays;

public class Gameplay{
    private Player player1;
    private Player player2;
    private GUI gui;

    public static String playerTurn;
    public static boolean firstMove; // the first play has requirements.

    public Gameplay() {
        int roll = (int)(Math.random() * 100) + 1;
        if(roll >= 51){
            playerTurn = "Player 1";
        }
        else{
            playerTurn = "Player 2";
        }
        firstMove = true;
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
        gui = new GUI(player1, player2);
    }
    public static void changePlayerTurn(){
        if(playerTurn.equals("Player 1")){
            playerTurn = "Player 2";
        }
        else{
            playerTurn = "Player 1";
        }
    }

}
