package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

public class Handler {
    
    //current/highest parameters used to determine where to highlight in green/red
    private int currentRow, currentColumn, highestRow, highestColumn;
    private Game game;
    private GameObject[] gameObjects;
    
    //board being solved
    private int[] board = {8, 0, 0, 0, 5, 3, 0, 9, 0,
                           0, 5, 2, 0, 0, 0, 0, 0, 3,
                           0, 3, 0, 4, 0, 0, 0, 0, 0,
                           4, 6, 8, 0, 0, 0, 0, 2, 0,
                           0, 0, 5, 0, 3, 0, 4, 0, 0,
                           0, 2, 0, 0, 0, 0, 1, 6, 9,
                           0, 0, 0, 0, 0, 7, 0, 1, 0,
                           6, 0, 0, 0, 0, 0, 9, 5, 0,
                           0, 8, 0, 2, 6, 0, 0, 0, 0,};
    
    public Handler(Game game){
        
        this.game = game; 
        
        //array of 81 game objects for 9x9 board with their coordinates stored in object
        gameObjects = new GameObject[81];
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                boolean given = true;
                if(board[9*i+j] == 0) given = false; 
                gameObjects[9*i+j] = new GameObject(j, i+1, board[9*i+j], given, 9*i+j);
            }
        }
    }
    
    //returns if the square at coordinates x, y (parameters) is a pre-given number
    public boolean isGiven(int x, int y){
        boolean given = false;
        
        if(gameObjects[9*y+x].getGiven()){
            given = true;
        }
        
        return given;
    }
    
    //cycles temp number in non-given square by +1
    public void cycleNumber(int x, int y){
        if(gameObjects[9*y+x].getValue() < 9){
            gameObjects[9*y+x].setValue(gameObjects[9*y+x].getValue() + 1);
        }else{
            gameObjects[9*y+x].setValue(1);
        }
    }
    
    //returns lowest valid answer for vertical column
    public int vertical(int i, int testValue){
        for(int j=0; j<9; j++){
                    int index = i%9;
                    if(gameObjects[j*9+index].getValue() == testValue && i != j*9+index){
                        testValue++;
                    }
                }
        return testValue;
    }
    
    //returns lowest valid answer for horizontal row
    public int horizontal(int i, int testValue){
        for(int j=0; j<9; j++){
                    int index = i/9;
                    if(gameObjects[index*9 + j].getValue() == testValue && i != index*9 + j){
                        testValue++;
                    }
                }
        return testValue;
    }
    
    //returns lowest valid answr for 3x3 subsquare
    public int subSquare(int i, int testValue){
        int indexY = gameObjects[i].getQuadrantY();
                int indexX = gameObjects[i].getQuadrantX();
                for(int j=0; j<3; j++){
                    for(int p=0; p<3; p++){
                        if(gameObjects[27*indexY + 9*j + 3*indexX + p].getValue() == testValue && i != 27*indexY + 9*j + 3*indexX + p){
                            testValue++;
                        }
                    }
                }
        return testValue;
    }
    
    //clear values for solver alg
    public void clearValues(){
        for(int i=0; i<gameObjects.length; i++){
            if(!gameObjects[i].getGiven()){
                gameObjects[i].setValue(0);
            }
        }
    }
    
    
    
    public void solver(){
        clearValues();
        //lowest possible answer is 0
        int testValue = 1;
        int testValue2 = 1;
        
        //loop through entire gameObjects array
        for(int i=0; i<gameObjects.length; i++){
            locationFinder(i);
            //if current object is not pre-given
            if(!gameObjects[i].getGiven()){
                for(boolean clear = true; clear; ){
                        
                        //find lowest possible answe via elimination
                        testValue = vertical(i, testValue);
                        testValue = horizontal(i, testValue);
                        testValue = subSquare(i, testValue); 
                        
                        //if testValue remained the same as before previous three algs
                        if(testValue == testValue2){
                            clear = false;
                            //if no value lower than 9 was possible, backtrack a square
                            if(testValue > 9){
                                for(boolean clear2 = true; clear2; i--){
                                    if(!gameObjects[i-1].getGiven()){
                                        clear2 = false;
                                        gameObjects[i-1].setValue(gameObjects[i-1].getValue()+1);
                                        testValue = gameObjects[i-1].getValue();
                                        testValue2 = testValue;
                                        gameObjects[i-1].setValue(0);
                                        i--;
                                    }
                                }
                            }
                            //else test value is valid and less than 9, so set square's value to test value, reset values for next iteration
                            else{
                                gameObjects[i].setValue(testValue);
                                testValue = 1;
                                testValue2 = 1;
                            }
                        }
                        //else test values are different, so redo the three algs
                        else{
                            testValue2 = testValue;
                        }
                }    
            }
        }
        //alg has finished, set game state to finished
        game.gameState = game.gameState.finished;
        
    }
   
    //sets 2 value coordinates given which iteration number solver is on
    public void locationFinder(int i){
            currentRow = i/9;
            currentColumn = i%9;
                if(currentRow > highestRow){
                    highestRow = currentRow;
                    highestColumn = 0;
                }if(currentColumn > highestColumn){
                    highestColumn = currentColumn;
                }
                //timer to slow down process for visuals
                try{
                    TimeUnit.MILLISECONDS.sleep(1);
                }catch(InterruptedException e){
                    System.err.format("IOException: %s%n", e);
                }
    }
    
    public void sortingRender(Graphics2D g){
        
        //highlights all backtracked cells
        g.setColor(Color.red);
        for(int i=0; i<=highestRow; i++){
            if(i == highestRow){  
                for(int j=0; j<=highestColumn; j++){
                   g.fillRect(j*71, i*71, 71, 71);
                }
            }else{
                for(int j=0; j<9; j++){
                    g.fillRect(j*71, i*71, 71, 71);
                }
            }
        }
        
        //highlights all correct cells
        g.setColor(Color.green);
        for(int i=0; i<=currentRow; i++){
            if(i == currentRow){
                for(int j=0; j<=currentColumn; j++){
                    if(j!=currentColumn || game.gameState == game.gameState.finished ){
                        g.fillRect(j*71, i*71, 71, 71);
                    }else if(j==currentColumn){
                        if(j!= 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*71, i*71, 71, 71);
                        }else{
                            g.setColor(Color.white);
                            g.fillRect(j*71, i*71, 71, 71);
                        }
                    }
                }
            }else{
                for(int j=0; j<9; j++){
                    g.fillRect(j*71, i*71, 71, 71);
                }
            }
        }
    }
    
    public void render(Graphics2D g){
        Font font = new Font("Arial", 1, 40);
        Font font2 = new Font("Arial", 1, 25);

        for(int i=0; i<9; i++){
            for(int j=1; j<=9; j++){
                //if value of square is not 0, print its value on sqaure
                if(!(gameObjects[9*i+j-1].getValue() == 0)){
                    //prints pre-given numbers in black and large
                    if(isGiven(j-1, i)){
                        g.setColor(Color.black);
                        g.setFont(font);
                        g.drawString(gameObjects[9*i+j-1].toString(), (j-1)*71+25, i*71+48 );
                    }
                    //prints temp numbers in gray and small
                    else if(!(game.gameState == game.gameState.finished)){
                        g.setColor(Color.gray);
                        g.setFont(font2);
                        g.drawString(gameObjects[9*i+j-1].toString(), (j-1)*71+5, i*71+30);
                    }
                    //if puzzle is solved, make it look pretty
                    else if(game.gameState == game.gameState.finished){
                        g.setColor(Color.white);
                        g.setFont(font);
                        g.drawString(gameObjects[9*i+j-1].toString(), (j-1)*71+25, i*71+48 );
                    }
                }
            }
        }
    }
    
}
