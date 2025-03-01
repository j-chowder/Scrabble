import javax.swing.*; // package of GUI components
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*; // package of classes for creating GUI components
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.BorderLayout.*;


public class GUI implements ActionListener {

    private JFrame frame;
    private JPanel topPanel;
    private JLabel gameText, scoreText;
    private JPanel centerPanel;
    private JButton[][] board; // board
    private JPanel rightPanel, uiPanel, scorePanel;
    private JButton[] UIButtons; // composed of forfeit, exchange, pass.
    private JButton[] letters; // the 7 letters in play at a time. (for player 1)
    private JButton[] letters2; // player 2's inventory
    private JLabel invText;
    private JPanel invPanel;
    private JPanel bottomPanel1, bottomPanel2;
    private Border compound;
    private int nullcount1, nullcount2;

    private boolean selected;
    private String selectedLetter;
    private JButton selectedTile, blankTile1, blankTile2;
    private boolean finish, exchange;
    private ArrayList<JButton> tilesInPlay, tilesInExchange, selectedTiles; // keeping track of the tiles you put the letters on for that turn.
    private ArrayList<Integer> tilesInPlayRow, srow;
    private ArrayList<Integer> tilesInPlayCol, scol;

    static int scorelessCount;
    Player player1;
    Player player2;

    Pictures p = new Pictures();
    searchSQL s = new searchSQL();
    Random r = new Random();

    public GUI(Player player1, Player player2){
        compound = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createRaisedBevelBorder()); // frame for tiles in play.
        tilesInPlay = new ArrayList<JButton>();
        tilesInPlayCol = new ArrayList<Integer>(); // col and row will be used to expedite looking for possible submissions.
        tilesInPlayRow = new ArrayList<Integer>(); // using arraylist bc the size is constantly changing.
        selectedTiles = new ArrayList<JButton>();
        blankTile1 = new JButton();
        blankTile1.setName("");
        blankTile2 = new JButton();
        blankTile2.setName("");
        this.player1 = player1;
        this.player2 = player2;
        tilesInExchange = new ArrayList<JButton>();
        srow = new ArrayList<>();
        scol = new ArrayList<>();
//        r.setSeed(123456789);
        // frame
        frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(1920,1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // top panel
        topPanel = new JPanel();
        topPanel.setBackground(new Color(209, 243, 232));
        topPanel.setPreferredSize(new Dimension(800, 100));
        frame.add(topPanel, BorderLayout.NORTH);

        // text of top panel
        gameText = new JLabel();
        gameText.setText(Gameplay.playerTurn + "'s Turn.");
        gameText.setFont(new Font("Times New Roman", Font.PLAIN, 75 )); // font.plain makes it not bold or italicized
        topPanel.add(gameText);
        // inventory panel (shows up when inv button is pressed)
        invPanel = new JPanel();
        invPanel.setBackground(new Color(209, 243, 232));
        invPanel.setPreferredSize(new Dimension(400, 800));

        // text of inventory
        invText = new JLabel();
        invText.setFont(new Font("Times New Roman", Font.PLAIN, 30 )); // font.plain makes it not bold or italicized

        // bottom right panel.
        createBottomPanel();

        // board
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(15,15)); // board is 15 x 15
        centerPanel.setVisible(true);

        createBoard();
        frame.add(centerPanel, BorderLayout.CENTER);

        // right panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        uiPanel = new JPanel();
        uiPanel.setLayout(new GridLayout(2,0));
        uiPanel.setBackground(new Color(92, 247, 198));
        uiPanel.setPreferredSize(new Dimension(200, 300));

        // bottom UI
        UIButtons = new JButton[4];

        // reset/forfeit button
        UIButtons[0] = new JButton();
        setUpButtons(UIButtons[0]);
        UIButtons[0].setActionCommand("Reset");
        UIButtons[0].setText("Reset");
        uiPanel.add(UIButtons[0]);

        // exchange button
        UIButtons[1] = new JButton();
        setUpButtons(UIButtons[1]);
        UIButtons[1].setActionCommand("Exchange");
        UIButtons[1].setText("Exchange");
        uiPanel.add(UIButtons[1]);

        // see bag
        UIButtons[2] = new JButton();
        setUpButtons(UIButtons[2]);
        UIButtons[2].setActionCommand("Inv");
        UIButtons[2].setText("Inv");
        uiPanel.add(UIButtons[2]);

        // pass button
        UIButtons[3] = new JButton();
        setUpButtons(UIButtons[3]);
        UIButtons[3].setActionCommand(("Pass"));
        UIButtons[3].setText("Pass");
        uiPanel.add(UIButtons[3]);

        rightPanel.add(uiPanel, CENTER);

        scorePanel = new JPanel();
        scorePanel.setBackground(new Color(0xFFFFFF));
        scorePanel.setPreferredSize(new Dimension(200, 100));

        scoreText = new JLabel();
        setScoreText();
        scoreText.setFont(new Font("Eurostile", Font.BOLD, 50));
        scorePanel.add(scoreText);
        rightPanel.add(scorePanel, NORTH);

        frame.add(rightPanel, BorderLayout.EAST);
    }
    public void setUpButtons(JButton button){
        button.setBackground(new Color(0xFFFFFF));
        button.addActionListener(this); // allows the button to run an event when clicked.
        button.setFont(new Font("MV Boli", Font.BOLD, 10));
        button.setName("");
        button.setFocusable(false); // removes the box around the text
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Reset")){
            Reset();
        }
        else if(e.getActionCommand().equals("Pass")){
            checkEnd(true);
            exchange = false;
            changingTurns();
        }
        else if(e.getActionCommand().equals("Exchange")){
            changeTop();
            if(!exchange){
                exchange = true;
                gameText.setText(Gameplay.playerTurn + " is exchanging.");
                return;
            }
            else{
                exchange = false;
            }

        }
        else if(e.getActionCommand().equals("Return")){
            Return();
        }
        else if(e.getActionCommand().equals("Inv")){
            inventory();
        }
        else if(e.getActionCommand().equals("Submit")){
            if(exchange && !tilesInExchange.isEmpty()) { // submitting an exchange.
                if (Gameplay.playerTurn.equals("Player 1")) {
                    if (tilesInExchange.size() > player1.getInventory().size()) {
                        gameText.setText("Not enough letters.");
                        topPanel.setBackground(new Color(0xF9256B));
                        exchange = false;
                    }
                } else {
                    if (tilesInExchange.size() > player2.getInventory().size()) {
                        gameText.setText("Not enough letters.");
                        topPanel.setBackground(new Color(0xF9256B));
                        exchange = false;
                    }
                }
                if (!gameText.getText().equals("Not enough letters.")) {
                    for (int i = 0; i < tilesInExchange.size(); i++) {
                        if (Gameplay.playerTurn.equals("Player 1")) {
                            changeLetter(tilesInExchange.get(i), player1);
                        } else {
                            changeLetter(tilesInExchange.get(i), player2);
                        }
                    }
                    tilesInExchange.removeAll(tilesInExchange);
                    checkEnd(true);
                    exchange = false;
                    changingTurns();
                }
            }
            else if(!checkRequirements()){
                // checks (1) the word is at least 2 letters and (2) if first move, touches center; after, checks if word touches another preexisting letter.
            }
            else{
                // checking to see if they put it all in a row, or all in a column.
                Integer rowcompare = tilesInPlayRow.get(0);
                    int count = 1;
                for(int i = 1; i < tilesInPlayRow.size(); i++){
                    if(tilesInPlayRow.get(i).equals(rowcompare)){
                        count++;
                    }
                }
                if(count == tilesInPlayRow.size()){ // all rows are the same
                    if(searchAlg("row")){ // if true = properly submitted.
                        checkEnd(false);
                        removeLetters();
                        changingTurns();
                        resetinPlay(); // clears all the "inplay" arrayLists.
                        return;
                    }
                }
                Integer colcompare = tilesInPlayCol.get(0);
                    count = 1;
                for(int i = 1; i < tilesInPlayCol.size(); i++){
                    if(tilesInPlayCol.get(i).equals(colcompare)){
                        count++;
                    }
                }
                if(count == tilesInPlayCol.size()){
                    if(searchAlg("col")){
                        checkEnd(false);
                        removeLetters();
                        changingTurns();
                        resetinPlay();
                        return;
                    }
                }
            }
        }
        else { // clicking the hotbar
            for (int i = 0; i < letters.length; i++){
                if((e.getSource() == letters[i])){
                    if(exchange){
                        tilesInExchange.add(letters[i]);
                    }
                    else{
                        selected = true;
                        selectedLetter = letters[i].getName();
                        selectedTile = letters[i];
                    }

                }
            }
            for(int i = 0; i < letters2.length; i++){
                    if((e.getSource() == letters2[i])){
                        if(exchange){
                            tilesInExchange.add(letters2[i]);
                        }
                        else{
                            selected = true;
                            selectedLetter = letters2[i].getName();
                            selectedTile = letters2[i];
                        }
                    }
            }
        }
        if(!(finish) && (selected) && !(selectedLetter.isEmpty())){ // pressing the board.
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    if (e.getSource() == board[row][col]) {
                        if(board[row][col].getName().isEmpty()) {
                            selectedTiles.add(selectedTile);
                            p.setPicture(board[row][col], selectedLetter);
                            inPlay(board[row][col], row, col);
                                if(selectedLetter.equals("?")){ // blank tile.
                                    changeBlank(board[row][col]);
                                }
                            selected = false;
                            selectedLetter = "";
                             p.updateIconSizes(board[row][col]);
                            if(exchange){
                                tilesInExchange.removeAll(tilesInExchange);
                                exchange = false;
                            }
                        }
                    }
                }
            }
        }
    }
    public void inPlay(JButton tile, int row, int col){
        tile.setBorder(compound);
        tilesInPlay.add(tile);
        if(Gameplay.playerTurn.equals("Player 1")){
            bottomPanel1.remove(selectedTile);
        }
        else{
            bottomPanel2.remove(selectedTile);
        }
        tilesInPlayRow.add(row);
        tilesInPlayCol.add(col);
    }
    public void resetinPlay(){
        for(int i = tilesInPlay.size() - 1; i >= 0; i--){
            tilesInPlay.get(i).setBorder(UIManager.getBorder("Button.border")); // resets the border to the default JButton one.
            tilesInPlay.remove(i);
            tilesInPlayRow.remove(i);
            tilesInPlayCol.remove(i);
        }
        selectedTiles.removeAll(selectedTiles);
        srow.removeAll(srow);
        scol.removeAll(scol);
    }

    public void changeLetter(JButton tile, Player player){
        int max = player.getInventory().size();
        if(max > 0){
            int inventoryIndex = (r.nextInt(max)); // RNG from [0, total cards left]

            //System.out.println("max:" + max + " min:" + min + " range:" + range +  " index:" + inventoryIndex);
            String temp = tile.getName();
            String letter = player.getInventory().get(inventoryIndex);

//        System.out.println("Temp: " + temp + " Letter: " + letter);
            p.setPicture(tile, letter);
            if(!(temp.isEmpty())){ // this is to account for the first 7 letters, where no exchange occurs.
                player.getInventory().add(temp);
                incrementCount(temp);
            }
            player.getInventory().remove(inventoryIndex);
                decrementCount(letter);


        }
        else{
            System.out.println("This tile has been set as null.");
            tile.setName("remove");
            if(player.getName().equals("Player 1")){
                nullcount1++;
            }
            else{
                nullcount2++;
            }
            System.out.println("Null 1: " + nullcount1 + " Null 2: " + nullcount2);
        }
    }
    public void changingTurns(){
        selectedTiles.removeAll(selectedTiles);
        if(Gameplay.playerTurn.equals("Player 1")){ // since changing turns, display player2's now.
            frame.remove(bottomPanel1);
            frame.add(bottomPanel2, SOUTH);
            for(int i = 1; letters2[i] == letters2[8]; i++){
                bottomPanel2.remove(i);
                i--;
            }
            for(int i = 1; i < letters2.length; i++){
                if(!letters2[i].getName().equals("remove")){
                    bottomPanel2.add(letters2[i]);
                    bottomPanel2.revalidate();
                }
            }
            bottomPanel2.revalidate();
            bottomPanel2.repaint();
        }
        else{ // display player1's inv.
            frame.remove(bottomPanel2);
            frame.add(bottomPanel1, SOUTH);
            for(int i = 1; letters[i] == letters[8]; i++){
                bottomPanel1.remove(i);
                i--;
            }
            for(int i = 1; i < letters.length; i++){
                if(!letters[i].getName().equals("remove")){
                    bottomPanel1.add(letters[i]);
                    bottomPanel1.revalidate();
                }
            }
            bottomPanel1.revalidate();
            bottomPanel1.repaint();
        }
        Gameplay.changePlayerTurn();
        changeTop();
        srow.removeAll(srow);
        scol.removeAll(scol);

    }
    public void changeTop(){
       gameText.setText(Gameplay.playerTurn + "'s Turn.");
       topPanel.setBackground(new Color(209, 243, 232));
    }
    public boolean searchAlg(String order){
        String input = "";
        if(order.equals("row")){ // same row, so check columns.
            // words are read left-to-right.
                // minIndex = starting point. maxIndex = ending point.

            int minIndex = minRowIndex();
            int maxIndex = maxRowIndex();

            // determine if there are any spaces to the left of the minIndex.
            int minColumn = tilesInPlayCol.get(minIndex);
            // boolean == if the tile is not empty (which means there a letter exists there) OR if we reach the end.
            while((minColumn > 0) && (!(board[tilesInPlayRow.get(0)][minColumn - 1].getName().isEmpty()))){
                System.out.println("Min Column! " + minColumn);
                minColumn -= 1;
            }
            int maxColumn = tilesInPlayCol.get(maxIndex);
            while((maxColumn < 14) && (!board[tilesInPlayRow.get(0)][maxColumn + 1].getName().isEmpty())){
                System.out.println("Max Column!" + maxColumn);
                maxColumn += 1;
            }
            // now build the input with concatenations from (minColumn --> maxColumn);
            for(int i = minColumn; i <= maxColumn; i++){
                input += board[tilesInPlayRow.get(0)][i].getName();
            }
            System.out.println("Input!" + input);
            if(s.check(input)){ // queries to see if there is a match.
                    if(getStartingRow()){ // checking for surrounding words
                        String[] inputs = new String[srow.size()];
                        for(int i = 0; i < srow.size(); i++){
                            String input1 = "";
                            int minRow = srow.get(i);

                            int maxRow = srow.get(i);
                            while((maxRow < 14) && (!board[maxRow + 1][scol.get(i)].getName().isEmpty())){ // finding max row
                                maxRow += 1;
                            }
                            for(int j = minRow; j <= maxRow; j++){
                                input1 += board[j][scol.get(i)].getName();
                            }
                            System.out.println("input1: " + input1);

                            if(s.check(input1)){
                                inputs[i] = input1;
                            }
                            else {
                                gameText.setText("Invalid.");
                                topPanel.setBackground(new Color(0xF9256B));
                                srow.removeAll(srow);
                                return false;
                            }
                        }
                        System.out.println(inputs);
                        for(int k = 0; k < inputs.length; k++){
                            scoring(inputs[k]);
                        }
                    }
                scoring(input);
                return true;
            }
            else{
                gameText.setText("Invalid.");
                topPanel.setBackground(new Color(0xF9256B));
                return false;
            }
        }
        else{ // columns are the same
            int minIndex = minRowIndex();
            int maxIndex = maxRowIndex();
            // determine if there are any spaces above the minIndex.
            int minRow = tilesInPlayRow.get(minIndex);
            // boolean == if the tile is not empty (which means there a letter exists there) OR if we reach the end.
            while((minRow > 0) && (!(board[minRow - 1][tilesInPlayCol.get(0)].getName().isEmpty()))){
                System.out.println("Min row:" + minRow);
                minRow -= 1;
            }
            int maxRow = tilesInPlayRow.get(maxIndex);
            while((maxRow < 14) && (!board[maxRow + 1][tilesInPlayCol.get(0)].getName().isEmpty())){
                System.out.print("Max row:" + maxRow);
                maxRow += 1;
            }
            System.out.println("Min Row: " + minRow +" Max row: " + maxRow);
            for(int i = minRow; i <= maxRow; i++){
                input += board[i][tilesInPlayCol.get(0)].getName();
            }
            System.out.println("Input!" + input);
            if(s.check(input)){ // queries to see if there is a match.
                if(getStartingCol()){ // checking for surrounding words
                    String[] inputs = new String[scol.size()];
                    for(int i = 0; i < scol.size(); i++){
                        String input1 = "";
                        System.out.println("scol:" + scol);
                        int minColumn = scol.get(i);

                        int maxColumn = scol.get(i);
                        while((maxColumn < 14) && (!board[tilesInPlayRow.get(0)][maxColumn + 1].getName().isEmpty())){
                            maxColumn += 1;
                        }
                        for(int j = minColumn; j <= maxColumn; j++){
                            input1 += board[srow.get(i)][j].getName();
                        }
                        System.out.println("input1: " +input1);
                        if(s.check(input1)){
                            inputs[i] = input1;
                        }
                        else{
                            gameText.setText("Invalid.");
                            topPanel.setBackground(new Color(0xF9256B));
                            scol.removeAll(scol);
                            return false;
                        }
                    }
                    for(int k = 0; k < inputs.length; k++){
                        System.out.println("in this scoring.");
                        scoring(inputs[k]);
                    }
                }
                scoring(input);
                return true;
            }
            else{
                gameText.setText("Invalid.");
                topPanel.setBackground(new Color(0xF9256B));
                return false;
            }
        }
    }
    public void scoring(String input){
        int score = 0;
        ArrayList<String> blacklist = new ArrayList<String>();
        // blank tiles are worth no points. Temporarily setting it as ? for the query to know that its worth 0 (for the prem squares)
        String temp1 = blankTile1.getName();

        String temp2 = blankTile2.getName();

        blankTile1.setName("?");
        blankTile2.setName("?");
        // putting it on blacklist (for the input substring)
        blacklist.add(temp1);
        blacklist.add(temp2);

        for(int i = 0; i < tilesInPlay.size(); i++){ // checking for any premium letter squares
            if(tilesInPlay.get(i).getText().equals("3x LS")){
                score += s.checkScore(tilesInPlay.get(i).getName()) * 3;
                blacklist.add(tilesInPlay.get(i).getName());
                tilesInPlay.get(i).setText("");
            }
            else if(tilesInPlay.get(i).getText().equals("2x LS")){
                score += s.checkScore(tilesInPlay.get(i).getName()) * 2;
                blacklist.add(tilesInPlay.get(i).getName());
                tilesInPlay.get(i).setText("");
            }
            // decrementCount(tilesInPlay.get(i).getName()); // decrements the count of the letter in the inventory.
        }
        for(int i = 0; i < input.length(); i++){ // adding the rest of the letters into the score.
            boolean blacklisted = false;
            for(int j = 0; j < blacklist.size(); j++){
                if(input.substring(i, i + 1).equals(blacklist.get(j))){
                    blacklisted = true;
                    blacklist.remove(j);
                }
            }
            if(!blacklisted){
                score += s.checkScore(input.substring(i, i + 1));
            }

        }
        for(int i = 0; i < tilesInPlay.size(); i++){ // checking for premium word squares.
            if(tilesInPlay.get(i).getText().equals("3x WS")){
                score *= 3;
                tilesInPlay.get(i).setText("");
            }
            else if(tilesInPlay.get(i).getText().equals("2x WS") || Gameplay.firstMove){ // first move needs to go to center.
                Gameplay.firstMove = false; // procs only once.
                score *= 2;
                tilesInPlay.get(i).setText("");
            }
        }
        if(tilesInPlay.size() == 7){ // bingo - used all letters at once.
            score += 50;
        }
        // adding the turn's score into the player's total score.
        if(Gameplay.playerTurn.equals("Player 1")){
            player1.addScore(score);
        }
        else{
            player2.addScore(score);
        }
        blankTile1.setName(temp1);
        blankTile2.setName(temp2);

        setScoreText();
    }
    public void decrementCount(String letter){
        if(Gameplay.playerTurn.equals("Player 1")){
            player1.changeCount(letter, "dec");
        }
        else{
            player2.changeCount(letter, "dec");
        }
    }
    public void incrementCount(String letter){
        if(Gameplay.playerTurn.equals("Player 1")){
            player1.changeCount(letter, "inc");
        }
        else{
            player2.changeCount(letter, "inc");
        }
    }
    public boolean checkRequirements(){ // if there are any letters around a tile in play, return true.
        System.out.println("Checking Requirements");
        boolean dupe = false;
        int row = -1;
        int col = -1;
            // this method will only work if tilesInPlay.size() = 1.
        try {
            col = tilesInPlayCol.get(minColIndex());
            row = tilesInPlayRow.get(minRowIndex());
        }
        catch(ArrayIndexOutOfBoundsException e){
            gameText.setText("The word must be at least 2 letters long.");
            topPanel.setBackground(new Color(0xF9256B));
            return false;
        }
        if(Gameplay.firstMove){ // first move needs to be in the center tile
            if(board[7][7].getName().isEmpty()){
                gameText.setText("The first word must touch the center.");
                topPanel.setBackground(new Color(0xF9256B));
                return false;
            }
            else{
                return true;
            }
        }
        if(row == tilesInPlayRow.get(maxRowIndex())){ // same row
            if(checkRowSurrounding()){
                return true;
            }
            if(tilesInPlayCol.get(minColIndex()) != 0){
                if(!board[tilesInPlayRow.get(0)][tilesInPlayCol.get(minColIndex()) - 1].getName().isEmpty()){ // checks the left
                    return true;
                }
            }
            if(tilesInPlayCol.get(maxColIndex()) != 14){
                if(!board[tilesInPlayRow.get(0)][tilesInPlayCol.get(maxColIndex()) + 1].getName().isEmpty()){ // checks the right
                    return true;
                }
            }

        }
        else if(col == tilesInPlayCol.get(maxColIndex())){ // same col
            if(checkColSurrounding()){
                return true;
            }
            if(tilesInPlayRow.get(minRowIndex()) != 0){
                System.out.println("Row: " + (tilesInPlayRow.get(minRowIndex()) - 1) + " Col: " + tilesInPlayCol.get(0));
                if(!board[tilesInPlayRow.get(minRowIndex()) - 1][tilesInPlayCol.get(0)].getName().isEmpty()){ // directly above
                    return true;
                }
            }
            if(tilesInPlayRow.get(maxRowIndex()) != 14){
                System.out.println("Row: " + (tilesInPlayRow.get(maxRowIndex()) + 1) + " Col: " + tilesInPlayCol.get(0));
                if(!board[tilesInPlayRow.get(maxRowIndex()) + 1][tilesInPlayCol.get(0)].getName().isEmpty()){
                    return true;
                }
            }
        }
        else{ // the tiles played do not have all the same rows || not the same cols
            gameText.setText("Invalid.");
            topPanel.setBackground(new Color(0xF9256B));
            return false;
        }
        // no catches at all, which means all surrounding tiles were empty.
        gameText.setText("Words must touch a pre-existing letter.");
        topPanel.setBackground(new Color(0xF9256B));
        return false;

    }
    public void setScoreText(){
        System.out.println("Setting score:" + player1.getScore() + player2.getScore() );
        scoreText.setText("(" + player1.getScore() + ", " + player2.getScore() + ")");
    }
    public void checkEnd(boolean scoreless){ // is called in changingTurns.
        // ends if:
        // (1) At least six successive scoreless turns have occurred
        // (2) One player has played every tile on their rack and no tiles remain in the bag

        // (1)
        if(scoreless){
            scorelessCount += 1;
        }
        else{ // if a player turn ends with added score, reset scorelessCount to 0.
            scorelessCount = 0;
        }
        if(scorelessCount == 6){
            System.out.println("ends game");
            finish = true;
            checkWin(player1, 1);
        }

        // (2)
        boolean allEmpty = true;
        if(player1.getInventory().isEmpty()){ // first checks if the inventory is empty.
            for(int i = 1; i < letters.length - 1; i++){ // now checks all letters in the hotbar if empty.
                if(!letters[i].getName().equals("remove")){ // if a letter is not empty.
                    allEmpty = false;
                    break;
                }
            }
            if(allEmpty){ // all empty; ends game.
                finish = true;
                checkWin(player1,2);
            }
        }

        // checking player2's inv now.
        allEmpty = true;
        if(player2.getInventory().isEmpty()){
            for(int i = 1; i < letters2.length - 1; i++){
                if(!letters2[i].getName().equals("remove")){
                    allEmpty = false;
                    break;
                }
            }
            if(allEmpty){
                finish = true;
                checkWin(player2,2);
            }
        }

    }
    public void checkWin(Player player, int outcome) {
        int score = 0;
        if (outcome == 1) { // scoreless outcome
            // each player's score is reduced by the sum of the values of their unplayed tiles

            // player1
            for (int i = 1; i < letters.length - 1; i++) {
                player1.addScore(s.checkScore(letters[i].getName()) * -1);
            }
            for (int i = 1; i < letters2.length - 1; i++) {
                player2.addScore(s.checkScore(letters2[i].getName()) * -1);
            }
        } else if (outcome == 2) { // play-out outcome
            // a player who plays out adds twice the sum of the opponent's unplayed tiles, and the opponent's score is unchanged.

            if (player == player1) {
                for (int i = 1; i < letters2.length - 1; i++) {
                    score += s.checkScore(letters2[i].getName());
                }
            } else if (player == player2) {
                for (int i = 1; i < letters.length - 1; i++) {
                    score += s.checkScore(letters[i].getName());
                }
            }
            player.addScore(score * 2);
            setScoreText();
        }
        if (player1.getScore() > player2.getScore()) {
            gameText.setText("Player 1 has won.");

        } else {
            gameText.setText("Player 2 has won.");
        }
        if(outcome == 1){
            JOptionPane.showMessageDialog(rightPanel,"The game has ended due to 6 consecutive scoreless turns.", "Game has finished.", JOptionPane.INFORMATION_MESSAGE);
        }
        else if(outcome == 2){
            JOptionPane.showMessageDialog(rightPanel, "The game has ended due to %s playing out.".formatted(player.getName()), "Game has finished.", JOptionPane.INFORMATION_MESSAGE);
        }
        while (true){
            int choice = JOptionPane.showConfirmDialog(rightPanel, "Play again? (。O ω O。)", "Reset?", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            Reset();
            break;
        } else if (choice == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(rightPanel, "No problem! Press the Reset button if you feel like playing again! ૮₍ ˃ ⤙ ˂ ₎ა");
            break;
        } else {

        }
    }
    }
    public void removeLetters(){ // After submit, resets the selectedTiles (on the hotbar) and changes its letters, effectively taking from the bag.
        System.out.println("Removing letters. Size: " + selectedTiles.size());
        if(Gameplay.playerTurn.equals("Player 1")){
            for(int i = 0; i < selectedTiles.size(); i++){
                selectedTiles.get(i).setName("");
                selectedTiles.get(i).setIcon(null);
                changeLetter(selectedTiles.get(i), player1);
            }
        }
        else if(Gameplay.playerTurn.equals("Player 2")){
            for(int i = 0; i < selectedTiles.size(); i++){
                selectedTiles.get(i).setName("");
                selectedTiles.get(i).setIcon(null);
                changeLetter(selectedTiles.get(i), player2);
            }
        }
      selectedTiles.removeAll(selectedTiles);
    }
    public int minRowIndex(){
        int minIndex = 0;
        for(int i = 1; i < tilesInPlayCol.size(); i++){
            if(tilesInPlayRow.get(minIndex) > tilesInPlayRow.get(i)){
                minIndex = i;
            }
        }
        return minIndex;
    }
    public int maxRowIndex(){
        int maxIndex = 0;
        for(int i = 1; i < tilesInPlayRow.size(); i++){
            if(tilesInPlayRow.get(maxIndex) < tilesInPlayRow.get(i)){
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    public int minColIndex(){
        int minIndex = 0;
        for(int i = 1; i < tilesInPlayCol.size(); i++){
            if(tilesInPlayCol.get(minIndex) > tilesInPlayCol.get(i)){
                minIndex = i;
            }
        }
        return minIndex;
    }
    public int maxColIndex(){
        int maxIndex = 0;
        for(int i = 1; i < tilesInPlayCol.size(); i++){
            if(tilesInPlayCol.get(maxIndex) < tilesInPlayCol.get(i)){
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    public void inventory(){

        String inventory = "<html>";

        if(Gameplay.playerTurn.equals("Player 1")) {
            for(int i = 0; i < 27; i++){
                inventory += Player.alphabet[i] + ": " + player1.getLetterCount(i) + "<br/>";
            }
        }
        else{
            for(int i = 0; i < 27; i++){
                inventory += Player.alphabet[i] + ": " + player2.getLetterCount(i) + "<br/>";
            }
        }
        JOptionPane.showMessageDialog(rightPanel, inventory, Gameplay.playerTurn + "'s Inventory", JOptionPane.INFORMATION_MESSAGE);
    }
    public void changeBlank(JButton tile){
        while(true){
            String input = (String)JOptionPane.showInputDialog(centerPanel, "Set a letter for the blank tile.", JOptionPane.PLAIN_MESSAGE);
            p.setPicture(tile, input);
            if(input == null || input.equals("" + JOptionPane.CANCEL_OPTION)){
                Return();
                break;
            }
            else if(tile.getName().equals("?")){
                JOptionPane.showMessageDialog(null, "Invalid Input", null, JOptionPane.ERROR_MESSAGE);
            }
            else{
                if(blankTile1.getName().isEmpty()){
                    blankTile1 = tile;
                    blankTile1.setName(tile.getName());
                }
                else{
                    blankTile2 = tile;
                    blankTile2.setName(tile.getName());
                }
                break;
            }
        }
    }
    public void Return(){
        if(!tilesInPlay.isEmpty()){
            changeTop();

            // re-add the letters back to the bottom.
            if(Gameplay.playerTurn.equals("Player 1")){
                for(int i = 1; letters[i] == letters[8]; i++){
                    bottomPanel1.remove(i);
                }
                for(int i = 1; i < letters.length; i++){
                    if(!letters[i].getName().equals("remove")){
                        bottomPanel1.add(letters[i]);
                        bottomPanel1.revalidate();
                        bottomPanel1.repaint();
                    }

                }
            }
            else{
                for(int i = 1; letters[i] == letters[8]; i++){
                    bottomPanel2.remove(i);
                }
                for(int i = 1; i < letters2.length; i++){
                    if(!letters2[i].getName().equals("remove")){
                        bottomPanel2.add(letters2[i]);
                        bottomPanel2.revalidate();
                        bottomPanel2.repaint();
                    }
                }
            }

            // clear the "in-play" tiles from the board. removes all the tiles in the in-play arrayList.
            for(int i = tilesInPlay.size() - 1; i >= 0; i--){
                if(tilesInPlay.get(i) == blankTile1){
                    blankTile1 = new JButton();
                    blankTile1.setName("");
                }
                else if(tilesInPlay.get(i) == blankTile2){
                    blankTile2 = new JButton();
                    blankTile2.setName("");
                }
                tilesInPlay.get(i).setIcon(null);
                tilesInPlay.get(i).setName("");
            }
            resetinPlay();
        }
    }
    public boolean checkRowSurrounding(){

            for(int col = tilesInPlayCol.get(minColIndex()); col <= tilesInPlayCol.get(maxColIndex()); col++){
                if(tilesInPlayRow.get(0) == 14){ // checks above
                    if(!board[tilesInPlayRow.get(0) - 1][col].getName().isEmpty()){
                        return true;
                    }
                }
                else if(tilesInPlayRow.get(0) == 0){ // checks below
                    if(!board[tilesInPlayRow.get(0) + 1][col].getName().isEmpty()){
                        return true;
                    }
                }
                else{
                    for(int row = tilesInPlayRow.get(0) - 1; row <= tilesInPlayRow.get(0) + 2; row +=2){ // checks above and below
                        if(!board[row][col].getName().isEmpty()){
                            return true;
                        }
                    }
                }
            }
        return false;
    }
    public boolean checkColSurrounding(){

            for(int row = tilesInPlayRow.get(minRowIndex()); row <= tilesInPlayRow.get(maxRowIndex()); row++) {
                if(tilesInPlayCol.get(0) == 14){ // checks left
                    if(!board[row][tilesInPlayCol.get(0) - 1].getName().isEmpty()){
                        return true;
                    }
                }
                else if(tilesInPlayCol.get(0) == 0){ // checks right
                    if(!board[row][tilesInPlayCol.get(0) + 1].getName().isEmpty()){
                        return true;
                    }
                }
                else{
                    for (int col = tilesInPlayCol.get(0) - 1; col <= tilesInPlayCol.get(0) + 2; col += 2) { // checks the left and the right.
                        if (!board[row][col].getName().isEmpty()) {
                            return true;
                        }
                    }
                }

            }
        return false;
    }
//    public ArrayList<JButton> getRowSurrounding(){
//        ArrayList<JButton> surroundingTiles = new ArrayList<>();
//        srow = new ArrayList<>();
//        scol = new ArrayList<>();
//        try{
//            for(int col = tilesInPlayCol.get(minColIndex()); col <= tilesInPlayCol.get(maxColIndex()); col++){
//                for(int row = tilesInPlayRow.get(0) - 1; row <= tilesInPlayRow.get(0) + 2; row +=2){ // checks above and below
//                    if(!board[row][col].getName().isEmpty()){
//                        surroundingTiles.add(board[row][col]);
//                        srow.add(row);
//                        scol.add(col);
//                    }
//                }
//            }
//        }
//        catch(ArrayIndexOutOfBoundsException a){
//
//        }
//        return surroundingTiles;
//    }
    public boolean getStartingRow(){
        boolean surroundingExists = false;
        try{
            for(int i = 0; i < tilesInPlay.size(); i++){
                int row = tilesInPlayRow.get(i);
                    System.out.println("starting row:" + row);
                while(row > 0 && !board[row - 1][tilesInPlayCol.get(i)].getName().isEmpty()){
                    row--;
                }
                srow.add(row);
                scol.add(tilesInPlayCol.get(i));
            }
            for(int i = 0; i < srow.size(); i++){ // filtering
                if(srow.get(i) != tilesInPlayRow.get(i)){
                    surroundingExists = true;
                }
                else if(!board[srow.get(i) + 1][scol.get(i)].getName().isEmpty()){
                    surroundingExists = true;
                }
                else{
                    srow.remove(i);
                    scol.remove(i);
                    i--;

                }
            }
        }
        catch(ArrayIndexOutOfBoundsException a){

        }
        System.out.println(srow);
        System.out.println(scol);
        return surroundingExists;
    }
//    public ArrayList<JButton> getColSurrounding(){
//        ArrayList<JButton> surroundingTiles = new ArrayList<>();
//        try {
//            for (int row = tilesInPlayRow.get(minRowIndex()); row <= tilesInPlayRow.get(maxRowIndex()); row++) {
//                for (int col = tilesInPlayCol.get(0) - 1; col <= tilesInPlayCol.get(0) + 2; col += 2) { // checks the left and the right.
//                    if (!board[row][col].getName().isEmpty()) {
//                        surroundingTiles.add(board[row][col]);
//                        srow.add(row);
//                        scol.add(col);
//                    }
//                }
//            }
//        }
//        catch(ArrayIndexOutOfBoundsException a){
//
//        }
//        return surroundingTiles;
//    }
    public boolean getStartingCol(){
        boolean surroundingExists = false;
        try{
            for(int i = 0; i < tilesInPlay.size(); i++){
                int col = tilesInPlayCol.get(i);
                while(col > 0 && !board[tilesInPlayRow.get(i)][col - 1].getName().isEmpty()){
                    col--;
                }
                srow.add(tilesInPlayRow.get(i));
                scol.add(col);
            }
            for(int i = 0; i < scol.size(); i++){ // filtering
                if(!scol.get(i).equals(tilesInPlayCol.get(i))){
                    surroundingExists = true; // left exists
                }
                else if(!board[srow.get(i)][scol.get(i) + 1].getName().isEmpty()){
                    surroundingExists = true; // right exists
                }
                else{
                    scol.remove(i);
                    srow.remove(i);
                    i--;

                }
            }
        }
        catch(ArrayIndexOutOfBoundsException a){

        }
        return surroundingExists;
    }
    public void createBoard(){
        board = new JButton[15][15];
        for(int row = 0; row < board.length; row++){ // setting up each button
            for(int col = 0; col < board[row].length; col++){
                board[row][col] = new JButton();
                setUpButtons(board[row][col]);
                board[row][col].setPreferredSize(new Dimension(1,1));
                centerPanel.add(board[row][col]);
            }
        }

        // all premium squares

        // triple letter squares.
        for(int row = 1; row < board.length; row += 4){
            for(int col = 1; col < board[row].length; col +=4){
                board[row][col].setBackground(new Color(0x4A9DEF));
                board[row][col].setText("3x LS");
            }
        }
        // pink 2x word squares
        // top left to bottom right diagonal
        for (int row = 0; row < board.length; row++) {
            int col = row;
            if(col != 5 && col != 9) { // so diagonals will not erase the previous 3x LS
                board[row][col].setBackground(new Color(0xEFD5F5));
                board[row][col].setText("2x WS"); // double the word score
            }
        }
        // top right to bottom left diagonal
        int col = 0;
        for (int row = board.length - 1; row >= 0; row--) {
            if(col != 5 && col != 9) {
                board[row][col].setBackground(new Color(0xEFD5F5));
                board[row][col].setText("2x WS");

            }
            col++;
        }
        // red 3x WS
        for(int row = board.length - 1; row >= 0; row -= 7){
            for( col = board[row].length - 1; col >= 0; col -=7){
                board[row][col].setBackground(new Color(0xEF2842));
                board[row][col].setText("3x WS");
            }
        }
        // light blue 2x LS
        // ones in col index 3 and 11
        for(int row = 0; row < board.length; row += 7){
            for(col = 3; col < board[row].length; col += 8){
                board[row][col].setBackground(new Color(0x63DEEE));
                board[row][col].setText("2x LS");
            }
        }
        // ones in col index 2 and 12
        for(int row = 6; row < 9; row += 2){
            for(col = 2; col < board[row].length; col += 10){
                board[row][col].setBackground(new Color(0x63DEEE));
                board[row][col].setText("2x LS");
            }
        }
        // ones in col index 0 and 14
        for(int row = 3; row < 12; row += 8){
            for(col = 0; col < board[row].length; col += 14){
                board[row][col].setBackground(new Color(0x63DEEE));
                board[row][col].setText("2x LS");
            }
        }
        // ones in col index 6 and 8
        for(col = 6; col < 9; col += 2){
            // Top side
            for(int j = 10; j > 5; j -= 4){
                board[j - 4][col].setBackground(new Color(0x63DEEE));
                board[j - 4][col].setText("2x LS");
            }
            // bottom side
            for(int j = 4; j < 12; j += 4){
                board[j + 4][col].setBackground(new Color(0x63DEEE));
                board[j + 4][col].setText("2x LS");
            }
        }
        // ones in col 7
        for(int row = 3; row < 12; row += 8){
            board[row][7].setBackground(new Color(0x63DEEE));
            board[row][7].setText("2x LS");
        }

        // center star
        board[7][7].setBackground(new Color(0xEFD5F5));
        board[7][7].setText("");
        board[7][7].setIcon(p.getStar());
        board[7][7].setName("");
    }
    public void createInitialLetters(){
        // Player1's inventory
        for(int i = 1; i < letters.length - 1; i++){
            letters[i] = new JButton();
            letters[i].setMargin(new Insets(0, 0, 0, 0));
            letters[i].addActionListener(this);
            letters[i].setName("");
            changeLetter(letters[i], player1); // assigns a random letter + sets the icon
            bottomPanel1.add(letters[i]);
            bottomPanel1.revalidate();

        }
        // Player2's inventory
        for(int i = 1; i < letters2.length - 1; i++){
            letters2[i] = new JButton();
            letters2[i].setMargin(new Insets(0, 0, 0, 0));
            letters2[i].addActionListener(this);
            letters2[i].setName("");
            changeLetter(letters2[i], player2); // assigns a random letter + sets the icon
            bottomPanel2.add(letters2[i]);
            bottomPanel2.revalidate();
        }
    }
    public void Reset(){
        bottomPanel1.removeAll();
        bottomPanel2.removeAll();
        centerPanel.removeAll();
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
        finish = false;
        Gameplay.firstMove = true;
        checkEnd(true); // just to reset the scoreless count to 0.
        createBoard();
        createBottomPanel();
        setScoreText();
        gameText.setText("Game has been reset.");
    }
    public void deleteLetters(JButton tile, Player player){
        if(player.getName().equals("Player 1")){

        }
    }
    public void createBottomPanel(){
        bottomPanel1 = new JPanel();
        bottomPanel1.setBackground(new Color(92, 247, 198));
        bottomPanel1.setPreferredSize(new Dimension(100, 100));

        bottomPanel2 = new JPanel();
        bottomPanel2.setBackground(new Color(92, 247, 198));
        bottomPanel2.setPreferredSize(new Dimension(100, 100));
        if(Gameplay.playerTurn.equals("Player 1")){
            frame.add(bottomPanel1, SOUTH);
        }
        else{
            frame.add(bottomPanel2, SOUTH);
        }
        // 1 return button, 7 letters, 1 submit button
        letters = new JButton[9];
        letters2 = new JButton[9];

        // return button. If there are letters in play, this button will take it from the board --> back into inv.
        letters[0] = new JButton();
        setUpButtons(letters[0]);
        letters[0].setText("Return");
        letters[0].setActionCommand(("Return"));
        bottomPanel1.add(letters[0]);

        letters2[0] = new JButton();
        setUpButtons(letters2[0]);
        letters2[0].setText("Return");
        letters2[0].setActionCommand(("Return"));
        bottomPanel2.add(letters2[0]);

        // initializing both
        createInitialLetters();
        // submit button
        letters[8] = new JButton();
        setUpButtons(letters[8]);
        letters[8].setText("Submit");
        letters[8].setActionCommand("Submit");
        bottomPanel1.add(letters[8]);

        letters2[8] = new JButton();
        setUpButtons(letters2[8]);
        letters2[8].setText("Submit");
        letters2[8].setActionCommand("Submit");
        bottomPanel2.add(letters2[8]);
    }

}
